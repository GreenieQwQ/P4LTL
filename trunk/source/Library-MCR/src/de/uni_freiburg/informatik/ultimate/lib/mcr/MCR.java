package de.uni_freiburg.informatik.ultimate.lib.mcr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.Set;
import java.util.stream.Collectors;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Intersect;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BoogieASTNode;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.GeneratedBoogieAstVisitor;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.core.lib.exceptions.ToolchainCanceledException;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.translation.IProgramExecution;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.SmtUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.interpolant.IInterpolatingTraceCheck;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.interpolant.InterpolantComputationStatus;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.tracecheck.ITraceCheckPreferences;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.tracecheck.TraceCheckReasonUnknown;
import de.uni_freiburg.informatik.ultimate.logic.ConstantTerm;
import de.uni_freiburg.informatik.ultimate.logic.Rational;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.Util;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.util.RCFGEdgeVisitor;
import de.uni_freiburg.informatik.ultimate.util.datastructures.DataStructureUtils;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.HashRelation;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;
import de.uni_freiburg.informatik.ultimate.util.statistics.IStatisticsDataProvider;

/**
 * @author Frank Schüssele (schuessf@informatik.uni-freiburg.de)
 */
public class MCR<LETTER extends IIcfgTransition<?>> implements IInterpolatingTraceCheck<LETTER> {
	private final ILogger mLogger;
	private final IPredicateUnifier mPredicateUnifier;
	private final IUltimateServiceProvider mServices;
	private final ManagedScript mManagedScript;
	private final IEmptyStackStateFactory<IPredicate> mEmptyStackStateFactory;

	private final HashRelation<LETTER, String> mReads2Variables;
	private final HashRelation<LETTER, String> mWrites2Variables;
	private final HashRelation<String, LETTER> mVariables2Writes;
	private final HashRelation<LETTER, LETTER> mMhbInverse;
	private final Map<LETTER, Map<String, LETTER>> mPreviousWrite;
	private final Map<LETTER, Term> mActions2TermVars;

	private final Function<List<LETTER>, Pair<LBool, List<IPredicate>>> mProofProvider;
	private final Map<LETTER, Integer> mActions2Indices;
	private final AutomataLibraryServices mAutomataServices;
	private final VpAlphabet<LETTER> mAlphabet;
	private final McrTraceCheckResult<LETTER> mResult;
	private final Map<LETTER, Integer> mActionCounts;

	public MCR(final ILogger logger, final ITraceCheckPreferences prefs, final IPredicateUnifier predicateUnifier,
			final IEmptyStackStateFactory<IPredicate> emptyStackStateFactory, final List<LETTER> trace,
			final Function<List<LETTER>, Pair<LBool, List<IPredicate>>> proofProvider) throws AutomataLibraryException {
		mLogger = logger;
		mPredicateUnifier = predicateUnifier;
		mServices = prefs.getUltimateServices();
		mAutomataServices = new AutomataLibraryServices(mServices);
		// TODO: Seperate correctly
		mAlphabet = new VpAlphabet<>(new HashSet<>(trace));
		mManagedScript = prefs.getCfgSmtToolkit().getManagedScript();
		mEmptyStackStateFactory = emptyStackStateFactory;
		mProofProvider = proofProvider;
		mReads2Variables = new HashRelation<>();
		mWrites2Variables = new HashRelation<>();
		mVariables2Writes = new HashRelation<>();
		mMhbInverse = new HashRelation<>();
		mPreviousWrite = new HashMap<>();
		mActions2TermVars = new HashMap<>();
		mActions2Indices = new HashMap<>();
		mActionCounts = new HashMap<>();
		// Explore all the interleavings of trace
		mResult = exploreInterleavings(trace);
	}

	private McrTraceCheckResult<LETTER> exploreInterleavings(final List<LETTER> initialTrace)
			throws AutomataLibraryException {
		getReadsAndWrites(initialTrace);
		final LinkedList<List<LETTER>> queue = new LinkedList<>();
		final Set<List<LETTER>> visited = new HashSet<>();
		queue.add(initialTrace);
		final NestedWordAutomaton<LETTER, IPredicate> automaton =
				new NestedWordAutomaton<>(mAutomataServices, mAlphabet, mEmptyStackStateFactory);
		while (!queue.isEmpty()) {
			final List<LETTER> trace = queue.remove();
			if (visited.contains(trace)) {
				continue;
			}
			visited.add(trace);
			preprocess(trace);
			List<IPredicate> interpolants = getInterpolantsIfAccepted(automaton, trace);
			if (interpolants == null) {
				final Pair<LBool, List<IPredicate>> proof = mProofProvider.apply(trace);
				final LBool feasibility = proof.getFirst();
				if (feasibility != LBool.UNSAT) {
					// We found a feasible error trace
					return new McrTraceCheckResult<>(trace, feasibility, automaton, null);
				}
				interpolants = proof.getSecond();
				final INestedWordAutomaton<LETTER, ?> mcrAutomaton = getMcrAutomaton(trace, interpolants);
				// TOOD: Get the interpolants from mcrAutomaton (DAG-interpolation?) and add them to automaton
			}
			queue.addAll(generateSeedInterleavings(trace, interpolants));
		}
		// All traces are infeasible
		// TODO: What to use as interpolants?
		return new McrTraceCheckResult<>(initialTrace, LBool.UNSAT, automaton, null);
	}

	private void getReadsAndWrites(final List<LETTER> trace) {
		for (int i = 0; i < trace.size(); i++) {
			final LETTER action = trace.get(i);
			// TODO: There might be duplicate actions, is this a problem?
			// TODO: => Map indices to varNames
			// TODO: Can this varName collide?
			final String varName = "order_" + i;
			mActions2TermVars.put(action, SmtUtils.buildNewConstant(mManagedScript.getScript(), varName, "Int"));
			mActions2Indices.put(action, i);
			final ReadWriteFinder finder = new ReadWriteFinder(action);
			mReads2Variables.addAllPairs(action, finder.getReadVars());
			for (final String var : finder.getWrittenVars()) {
				mVariables2Writes.addPair(var, action);
				mWrites2Variables.addPair(action, var);
			}
			mActionCounts.put(action, mActionCounts.getOrDefault(action, 0) + 1);
		}
	}

	private void preprocess(final List<LETTER> trace) {
		mMhbInverse.clear();
		mPreviousWrite.clear();
		mActions2TermVars.clear();
		mActions2Indices.clear();
		// TODO: How to construct the MHB relation?
		final Map<String, LETTER> lastWrittenBy = new HashMap<>();
		for (int i = 0; i < trace.size(); i++) {
			final LETTER action = trace.get(i);
			mActions2Indices.put(action, i);
			final Set<String> reads = mReads2Variables.getImage(action);
			if (!reads.isEmpty()) {
				final Map<String, LETTER> previousWrites = new HashMap<>();
				for (final String read : reads) {
					previousWrites.put(read, lastWrittenBy.get(read));
				}
				mPreviousWrite.put(action, previousWrites);
			}
			for (final String written : mWrites2Variables.getImage(action)) {
				lastWrittenBy.put(written, action);
			}
		}
	}

	private List<IPredicate> getInterpolantsIfAccepted(final NestedWordAutomaton<LETTER, IPredicate> automaton,
			final List<LETTER> trace) {
		// TODO: Get an accepting run if possible and return its state sequence, otherwise just return null
		return null;
	}

	// TODO: Avoid duplicates by caching?
	private Collection<List<LETTER>> generateSeedInterleavings(final List<LETTER> trace,
			final List<IPredicate> interpolants) {
		final Script script = mManagedScript.getScript();
		final List<List<LETTER>> result = new ArrayList<>();
		final Term[] termVars = mActions2TermVars.values().toArray(new Term[trace.size()]);
		for (final LETTER read : trace) {
			final Set<String> readVars = mReads2Variables.getImage(read);
			if (readVars.isEmpty()) {
				continue;
			}
			for (final String var : readVars) {
				final Term preRead = getPrecondition(read, trace, interpolants, var);
				final Set<LETTER> writesWithNull =
						DataStructureUtils.union(mVariables2Writes.getImage(var), Collections.singleton(null));
				for (final LETTER write : writesWithNull) {
					if (writeImplies(write, preRead, trace, interpolants, var)) {
						continue;
					}
					script.push(1);
					// If the constraints are satisfiable, add a trace based on them
					final Term constraints = getConstraints(trace, read, write, interpolants, var);
					script.assertTerm(constraints);
					if (script.checkSat() == LBool.SAT) {
						result.add(constructTraceFromModel(trace, script.getValue(termVars)));
					}
					script.pop(1);
				}
			}
		}
		return result;
	}

	/**
	 * Encode a new trace in a formula, that is equivalent to trace, except that write happens right before read.
	 */
	private Term getConstraints(final List<LETTER> trace, final LETTER read, final LETTER write,
			final List<IPredicate> interpolants, final String variable) {
		final Script script = mManagedScript.getScript();
		final List<Term> conjuncts = new ArrayList<>();
		// Add the MHB-constraints
		// TODO: We might want to have this as an option
		for (final Entry<LETTER, LETTER> entry : mMhbInverse.entrySet()) {
			final Term var1 = mActions2TermVars.get(entry.getValue());
			final Term var2 = mActions2TermVars.get(entry.getKey());
			conjuncts.add(SmtUtils.less(script, var1, var2));
		}
		// TODO: Refine these conditions, s.t. all other reads read the same value to be sound?
		conjuncts.addAll(getRwConstraints(read, trace, interpolants));
		if (write != null) {
			conjuncts.addAll(getRwConstraints(write, trace, interpolants));
		}
		final Term post = getPostcondition(write, trace, interpolants, variable);
		conjuncts.addAll(getValueConstraints(read, post, trace, interpolants));
		return SmtUtils.and(script, conjuncts);
	}

	private Collection<Term> getRwConstraints(final LETTER action, final List<LETTER> trace,
			final List<IPredicate> interpolants) {
		final List<Term> result = new ArrayList<>();
		// TODO
		// for (final LETTER prevRead : mMhbInverse.getImage(action)) {
		// final Term preRead = getPrecondition(prevRead, trace, interpolants);
		// result.addAll(getValueConstraints(prevRead, preRead, trace, interpolants));
		// }
		return result;
	}

	private Collection<Term> getValueConstraints(final LETTER read, final Term constraint, final List<LETTER> trace,
			final List<IPredicate> interpolants) {
		final Script script = mManagedScript.getScript();
		final List<Term> result = new ArrayList<>();
		final Term readVar = mActions2TermVars.get(read);
		for (final String var : mReads2Variables.getImage(read)) {
			final List<Term> disjuncts = new ArrayList<>();
			final Set<LETTER> writesWithNull =
					DataStructureUtils.union(mVariables2Writes.getImage(var), Collections.singleton(null));
			for (final LETTER write : writesWithNull) {
				if (!writeImplies(write, constraint, trace, interpolants, var)) {
					continue;
				}
				final List<Term> conjuncts = new ArrayList<>();
				Term writeVar = null;
				if (write != null) {
					writeVar = mActions2TermVars.get(write);
					conjuncts.add(SmtUtils.less(script, writeVar, readVar));
				}
				for (final LETTER otherWrite : mVariables2Writes.getImage(var)) {
					if (otherWrite.equals(write) || writeImplies(otherWrite, constraint, trace, interpolants, var)) {
						continue;
					}
					final Term otherWriteVar = mActions2TermVars.get(otherWrite);
					final Term afterRead = SmtUtils.less(script, readVar, otherWriteVar);
					if (write != null) {
						final Term beforeWrite = SmtUtils.less(script, otherWriteVar, writeVar);
						conjuncts.add(SmtUtils.or(script, beforeWrite, afterRead));
					} else {
						conjuncts.add(afterRead);
					}
				}
				disjuncts.add(SmtUtils.and(script, conjuncts));
			}
			result.add(SmtUtils.or(mManagedScript.getScript(), disjuncts));
		}
		return result;
	}

	private boolean writeImplies(final LETTER write, final Term constraint, final List<LETTER> trace,
			final List<IPredicate> interpolants, final String variable) {
		final Script script = mManagedScript.getScript();
		final Term post = getPostcondition(write, trace, interpolants, variable);
		return Util.checkSat(script, SmtUtils.and(script, post, SmtUtils.not(script, constraint))) == LBool.UNSAT;
	}

	private List<LETTER> constructTraceFromModel(final List<LETTER> originalTrace, final Map<Term, Term> valueMap) {
		// TODO: For repeating actions sort IntStream.range(0, originalTrace.size()).boxed() instead
		return originalTrace.stream().sorted((a1, a2) -> getIntValue(valueMap, a1).compareTo(getIntValue(valueMap, a2)))
				.collect(Collectors.toList());
	}

	private BigInteger getIntValue(final Map<Term, Term> valueMap, final LETTER action) {
		final Term term = valueMap.get(mActions2TermVars.get(action));
		assert term instanceof ConstantTerm;
		final Object value = ((ConstantTerm) term).getValue();
		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}
		assert value instanceof Rational;
		final Rational rational = (Rational) value;
		assert rational.denominator().equals(BigInteger.ONE);
		return rational.numerator();
	}

	private INestedWordAutomaton<LETTER, ?> getMcrAutomaton(final List<LETTER> trace,
			final List<IPredicate> interpolants) throws AutomataLibraryException {
		INestedWordAutomaton<LETTER, String> result = null;
		final StringFactory factory = new StringFactory();
		for (final LETTER read : trace) {
			final Map<String, LETTER> previousWrites = mPreviousWrite.get(read);
			if (previousWrites == null) {
				continue;
			}
			for (final Entry<String, LETTER> entry : previousWrites.entrySet()) {
				final LETTER write = entry.getValue();
				final String var = entry.getKey();
				final NestedWordAutomaton<LETTER, String> readNwa =
						new NestedWordAutomaton<>(mAutomataServices, mAlphabet, factory);
				final Set<LETTER> writesOnVar = mVariables2Writes.getImage(var);
				final int readCount = mActionCounts.get(read);
				final String initial = "q0";
				final String postWrite = "q1";
				final String postRead = "q2";
				readNwa.addState(true, false, initial);
				if (write == null) {
					readNwa.addState(false, true, postRead);
					// TODO: The transitions are not always internal!
					readNwa.addInternalTransition(initial, read, postRead);
					for (final LETTER action : mActionCounts.keySet()) {
						if (action.equals(read) && readCount == 1) {
							continue;
						}
						if (!writesOnVar.contains(action)) {
							readNwa.addInternalTransition(initial, action, initial);
						}
						readNwa.addInternalTransition(postRead, action, postRead);
					}
				} else {
					readNwa.addState(false, false, postWrite);
					readNwa.addState(false, true, postRead);
					final Set<LETTER> correctWrites = new HashSet<>();
					final Term post = getPostcondition(write, trace, interpolants, var);
					for (final LETTER otherWrite : writesOnVar) {
						if (otherWrite.equals(read) && readCount == 1) {
							continue;
						}
						if (otherWrite.equals(write) || writeImplies(otherWrite, post, trace, interpolants, var)) {
							correctWrites.add(otherWrite);
						}
					}
					// Add all the forward edges and count the actions
					final Map<LETTER, Integer> remainingCounts = new HashMap<>(mActionCounts);
					remainingCounts.put(read, readCount - 1);
					readNwa.addInternalTransition(postWrite, read, postRead);
					for (final LETTER w : correctWrites) {
						readNwa.addInternalTransition(initial, w, postWrite);
						if (correctWrites.size() == 1) {
							remainingCounts.put(w, remainingCounts.get(w) - 1);
						}
					}
					// Add the self-loops for all the actions, that still can happen
					for (final LETTER action : mActionCounts.keySet()) {
						if (remainingCounts.get(action) == 0) {
							continue;
						}
						readNwa.addInternalTransition(initial, action, initial);
						readNwa.addInternalTransition(postRead, action, postRead);
						if (!writesOnVar.contains(action) || correctWrites.contains(action)) {
							readNwa.addInternalTransition(postWrite, action, postWrite);
						}
					}
				}
				if (result == null) {
					result = readNwa;
				} else {
					// TODO: Is there a more efficient intersection with multiple automata?
					final Intersect<LETTER, String> intersect =
							new Intersect<>(mAutomataServices, factory, result, readNwa);
					result = intersect.getResult();
				}
			}
		}
		return result;
	}

	private Term getPrecondition(final LETTER action, final List<LETTER> trace, final List<IPredicate> interpolants,
			final String variable) {
		final Map<String, LETTER> previousWrites = mPreviousWrite.get(action);
		if (previousWrites == null) {
			throw new UnsupportedOperationException(action + " is not a read.");
		}
		return getPostcondition(previousWrites.get(variable), trace, interpolants, variable);
	}

	// TODO: Cache this?
	private Term getPostcondition(final LETTER action, final List<LETTER> trace, final List<IPredicate> interpolants,
			final String variable) {
		if (action == null) {
			return mManagedScript.getScript().term("true");
		}
		final int index = mActions2Indices.get(action);
		if (index == trace.size() - 1) {
			return mManagedScript.getScript().term("false");
		}
		// TODO: Somehow abstract other variables away
		return interpolants.get(index).getClosedFormula();
	}

	public NestedWordAutomaton<LETTER, IPredicate> getAutomaton() {
		return mResult.getAutomaton();
	}

	@Override
	public List<LETTER> getTrace() {
		return mResult.getTrace();
	}

	@Override
	public IPredicate[] getInterpolants() {
		final List<IPredicate> interpolants = mResult.getInterpolants();
		return mResult.getInterpolants().toArray(new IPredicate[interpolants.size()]);
	}

	@Override
	public IPredicateUnifier getPredicateUnifier() {
		return mPredicateUnifier;
	}

	@Override
	public boolean isPerfectSequence() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InterpolantComputationStatus getInterpolantComputationStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LBool isCorrect() {
		return mResult.isCorrect();
	}

	@Override
	public IPredicate getPrecondition() {
		return mPredicateUnifier.getTruePredicate();
	}

	@Override
	public IPredicate getPostcondition() {
		return mPredicateUnifier.getFalsePredicate();
	}

	@Override
	public Map<Integer, IPredicate> getPendingContexts() {
		return Collections.emptyMap();
	}

	@Override
	public boolean providesRcfgProgramExecution() {
		return isCorrect() != LBool.SAT;
	}

	@Override
	public IProgramExecution<IIcfgTransition<IcfgLocation>, Term> getRcfgProgramExecution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatisticsDataProvider getStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ToolchainCanceledException getToolchainCanceledExpection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TraceCheckReasonUnknown getTraceCheckReasonUnknown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean wasTracecheckFinishedNormally() {
		// TODO Auto-generated method stub
		return true;
	}

	private class ReadWriteFinder extends RCFGEdgeVisitor {
		private final Set<String> mReadVars;
		private final Set<String> mWrittenVars;

		@Override
		protected void visit(final StatementSequence c) {
			for (final Statement s : c.getStatements()) {
				if (s instanceof AssumeStatement) {
					final Expression expression = ((AssumeStatement) s).getFormula();
					mReadVars.addAll(new VariableFinder(expression).getResult());
				}
				if (s instanceof AssignmentStatement) {
					final AssignmentStatement a = (AssignmentStatement) s;
					for (final LeftHandSide lhs : a.getLhs()) {
						mWrittenVars.addAll(new VariableFinder(lhs).getResult());
					}
					for (final Expression rhs : a.getRhs()) {
						mReadVars.addAll(new VariableFinder(rhs).getResult());
					}
				}
				// TODO: Other cases?
			}
			super.visit(c);
		}

		// TODO: How to handle the cast correctly?
		ReadWriteFinder(final LETTER action) {
			mReadVars = new HashSet<>();
			mWrittenVars = new HashSet<>();
			if (action instanceof IcfgEdge) {
				visit((IcfgEdge) action);
			}
		}

		Set<String> getReadVars() {
			return mReadVars;
		}

		Set<String> getWrittenVars() {
			return mWrittenVars;
		}
	}

	private class VariableFinder extends GeneratedBoogieAstVisitor {
		private final Collection<String> mResult;

		VariableFinder(final BoogieASTNode node) {
			mResult = new HashSet<>();
			node.accept(this);
		}

		Collection<String> getResult() {
			return mResult;
		}

		@Override
		public boolean visit(final IdentifierExpression node) {
			mResult.add(node.getIdentifier());
			return super.visit(node);
		}

		@Override
		public boolean visit(final VariableLHS node) {
			mResult.add(node.getIdentifier());
			return super.visit(node);
		}
	}
}
