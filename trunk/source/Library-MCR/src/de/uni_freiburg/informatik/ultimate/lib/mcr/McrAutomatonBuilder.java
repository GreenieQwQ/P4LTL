package de.uni_freiburg.informatik.ultimate.lib.mcr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.Word;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INwaOutgoingLetterAndTransitionProvider;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Accepts;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.IntersectNwa;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.reachablestates.NestedWordAutomatonReachableStates;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.IncomingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IEmptyStackStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.StringFactory;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.transitions.TransFormula;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.PartialQuantifierElimination;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.interpolant.QualifiedTracePredicates;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.PredicateTransformer;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.TermDomainOperationProvider;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.ManagedScript;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SmtUtils;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.logic.QuantifiedFormula;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.util.datastructures.DataStructureUtils;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.HashRelation;

/**
 * Class to construct automata based on the MCR relation.
 *
 * @author Frank Schüssele (schuessf@informatik.uni-freiburg.de)
 */
public class McrAutomatonBuilder<LETTER extends IIcfgTransition<?>> {
	private final List<LETTER> mOriginalTrace;
	private final IPredicateUnifier mPredicateUnifier;
	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	private final AutomataLibraryServices mAutomataServices;
	private final IEmptyStackStateFactory<IPredicate> mEmptyStackFactory;
	private final VpAlphabet<LETTER> mAlphabet;
	private final VpAlphabet<Integer> mIntAlphabet;
	private final ManagedScript mManagedScript;
	private final SimplificationTechnique mSimplificationTechnique;
	private final XnfConversionTechnique mXnfConversionTechnique;
	private final PredicateTransformer<Term, IPredicate, TransFormula> mPredicateTransformer;

	private List<INestedWordAutomaton<Integer, String>> mThreadAutomata;

	private final HashRelation<IProgramVar, Integer> mVariables2Writes;
	private final Map<LETTER, List<Integer>> mActions2Indices;
	private final Map<String, List<Integer>> mThreads2SortedActions;

	public McrAutomatonBuilder(final List<LETTER> trace, final IPredicateUnifier predicateUnifier,
			final IEmptyStackStateFactory<IPredicate> emptyStackFactory, final ILogger logger,
			final VpAlphabet<LETTER> alphabet, final IUltimateServiceProvider services,
			final ManagedScript managedScript, final SimplificationTechnique simplificationTechnique,
			final XnfConversionTechnique xnfConversionTechnique) {
		mOriginalTrace = trace;
		mLogger = logger;
		mPredicateUnifier = predicateUnifier;
		mServices = services;
		mAutomataServices = new AutomataLibraryServices(services);
		mEmptyStackFactory = emptyStackFactory;
		mAlphabet = alphabet;
		mManagedScript = managedScript;
		mSimplificationTechnique = simplificationTechnique;
		mXnfConversionTechnique = xnfConversionTechnique;
		mPredicateTransformer =
				new PredicateTransformer<>(managedScript, new TermDomainOperationProvider(services, managedScript));
		mIntAlphabet = new VpAlphabet<>(IntStream.range(0, trace.size()).boxed().collect(Collectors.toSet()));
		mVariables2Writes = new HashRelation<>();
		mThreads2SortedActions = new HashMap<>();
		mActions2Indices = new HashMap<>();
		preprocess();
	}

	private void preprocess() {
		for (int i = 0; i < mOriginalTrace.size(); i++) {
			final LETTER action = mOriginalTrace.get(i);
			List<Integer> currentIndices = mActions2Indices.get(action);
			if (currentIndices == null) {
				currentIndices = new ArrayList<>();
				mActions2Indices.put(action, currentIndices);
			}
			currentIndices.add(i);
			for (final IProgramVar var : action.getTransformula().getAssignedVars()) {
				mVariables2Writes.addPair(var, i);
			}
			final String currentThread = action.getPrecedingProcedure();
			List<Integer> threadActions = mThreads2SortedActions.get(currentThread);
			if (threadActions == null) {
				threadActions = new ArrayList<>();
				mThreads2SortedActions.put(currentThread, threadActions);
			}
			threadActions.add(i);
			final String nextThread = action.getSucceedingProcedure();
			if (currentThread != nextThread) {
				threadActions = mThreads2SortedActions.get(nextThread);
				if (threadActions == null) {
					threadActions = new ArrayList<>();
					mThreads2SortedActions.put(nextThread, threadActions);
				}
				threadActions.add(i);
			}
		}
	}

	private static String getState(final int i) {
		return "q" + i;
	}

	private List<INestedWordAutomaton<Integer, String>> getThreadAutomata() {
		if (mThreadAutomata == null) {
			mThreadAutomata = new ArrayList<>();
			final StringFactory factory = new StringFactory();
			// Construct automata for the MHB relation
			for (final List<Integer> threadActions : mThreads2SortedActions.values()) {
				final NestedWordAutomaton<Integer, String> nwa =
						new NestedWordAutomaton<>(mAutomataServices, mIntAlphabet, factory);
				final Set<Integer> threadActionSet = new HashSet<>(threadActions);
				for (int i = 0; i <= threadActions.size(); i++) {
					nwa.addState(i == 0, i == threadActions.size(), getState(i));
					if (i > 0) {
						nwa.addInternalTransition(getState(i - 1), threadActions.get(i - 1), getState(i));
					}
					for (int other = 0; other < mOriginalTrace.size(); other++) {
						if (!threadActionSet.contains(other)) {
							nwa.addInternalTransition(getState(i), other, getState(i));
						}
					}
				}
				mThreadAutomata.add(nwa);
			}
		}
		return mThreadAutomata;
	}

	public NestedWordAutomaton<LETTER, IPredicate> buildMhbAutomaton(final PredicateFactory predicateFactory)
			throws AutomataLibraryException {
		final NestedWordAutomaton<LETTER, IPredicate> result =
				new NestedWordAutomaton<>(mAutomataServices, mAlphabet, mEmptyStackFactory);
		final INestedWordAutomaton<Integer, String> intAutomaton = intersectNwa(getThreadAutomata());
		final Set<String> initialStates = intAutomaton.getInitialStates();
		final Set<String> finalStates = new HashSet<>(intAutomaton.getFinalStates());
		final LinkedList<String> queue = new LinkedList<>(finalStates);
		final Map<String, IPredicate> states2Predicates = new HashMap<>();
		final Term trueTerm = mPredicateUnifier.getTruePredicate().getFormula();
		for (final String state : intAutomaton.getStates()) {
			final IPredicate predicate = predicateFactory.newSPredicate(null, trueTerm);
			states2Predicates.put(state, predicate);
			result.addState(initialStates.contains(state), finalStates.contains(state), predicate);
		}
		final Set<String> visited = new HashSet<>();
		while (!queue.isEmpty()) {
			final String state = queue.remove();
			if (!visited.add(state)) {
				continue;
			}
			for (final IncomingInternalTransition<Integer, String> edge : intAutomaton.internalPredecessors(state)) {
				final LETTER letter = mOriginalTrace.get(edge.getLetter());
				result.addInternalTransition(states2Predicates.get(edge.getPred()), letter,
						states2Predicates.get(state));
				queue.add(edge.getPred());
			}
		}
		return result;
	}

	private boolean isInterleaving(final List<Integer> intTrace) throws AutomataLibraryException {
		final INwaOutgoingLetterAndTransitionProvider<Integer, String> mhbAutomaton = intersect(getThreadAutomata());
		final Word<Integer> word = new Word<>(intTrace.toArray(new Integer[intTrace.size()]));
		return new Accepts<>(mAutomataServices, mhbAutomaton, NestedWord.nestedWord(word)).getResult();
	}

	private List<Integer> getIntTrace(final List<LETTER> trace) {
		final List<Integer> intTrace = new ArrayList<>(trace.size());
		final Map<LETTER, Integer> counts = new HashMap<>();
		for (final LETTER action : trace) {
			final int count = counts.getOrDefault(action, 0);
			intTrace.add(mActions2Indices.get(action).get(count));
			counts.put(action, count + 1);
		}
		return intTrace;
	}

	private INestedWordAutomaton<Integer, String> buildMcrAutomaton(final List<Integer> intTrace)
			throws AutomataLibraryException {
		mLogger.info("Constructing automaton for MCR equivalence class.");
		// Determine all previous writes
		final List<Map<IProgramVar, Integer>> previousWrite = new ArrayList<>(intTrace.size());
		final Map<IProgramVar, Integer> lastWrittenBy = new HashMap<>();
		for (final int index : intTrace) {
			final Map<IProgramVar, Integer> previousWrites = new HashMap<>();
			final TransFormula transformula = mOriginalTrace.get(index).getTransformula();
			for (final IProgramVar read : transformula.getInVars().keySet()) {
				previousWrites.put(read, lastWrittenBy.get(read));
			}
			previousWrite.add(previousWrites);
			for (final IProgramVar written : transformula.getAssignedVars()) {
				lastWrittenBy.put(written, index);
			}
		}
		// Add all thread automata
		final List<INestedWordAutomaton<Integer, String>> automata = new ArrayList<>(getThreadAutomata());
		// Construct automata for each read to be preceded by the same write
		final StringFactory factory = new StringFactory();
		for (int j = 0; j < mOriginalTrace.size(); j++) {
			final int read = intTrace.get(j);
			final Map<IProgramVar, Integer> previousWrites = previousWrite.get(j);
			for (final Entry<IProgramVar, Integer> entry : previousWrites.entrySet()) {
				final Integer write = entry.getValue();
				final IProgramVar var = entry.getKey();
				final NestedWordAutomaton<Integer, String> nwa =
						new NestedWordAutomaton<>(mAutomataServices, mIntAlphabet, factory);
				final Set<Integer> writesOnVar = mVariables2Writes.getImage(var);
				nwa.addState(write == null, false, getState(1));
				nwa.addState(false, true, getState(2));
				nwa.addInternalTransition(getState(1), read, getState(2));
				if (write != null) {
					nwa.addState(true, false, getState(0));
					nwa.addInternalTransition(getState(0), write, getState(1));
				}
				for (int i = 0; i < mOriginalTrace.size(); i++) {
					if (i == read || write != null && i == write) {
						continue;
					}
					if (write != null) {
						nwa.addInternalTransition(getState(0), i, getState(0));
					}
					if (!writesOnVar.contains(i)) {
						nwa.addInternalTransition(getState(1), i, getState(1));
					}
					nwa.addInternalTransition(getState(2), i, getState(2));
				}
				automata.add(nwa);
			}
		}
		return intersectNwa(automata);
	}

	private INestedWordAutomaton<Integer, String> intersectNwa(
			final Collection<INestedWordAutomaton<Integer, String>> automata) throws AutomataLibraryException {
		mLogger.info("Started intersection.");
		final INestedWordAutomaton<Integer, String> result =
				new NestedWordAutomatonReachableStates<>(mAutomataServices, intersect(automata));
		mLogger.info("Finished intersection with " + result.sizeInformation());
		return result;
	}

	private static INwaOutgoingLetterAndTransitionProvider<Integer, String> intersect(
			final Collection<INestedWordAutomaton<Integer, String>> automata) throws AutomataLibraryException {
		final StringFactory factory = new StringFactory();
		INwaOutgoingLetterAndTransitionProvider<Integer, String> result = null;
		for (final INestedWordAutomaton<Integer, String> a : automata) {
			if (result == null) {
				result = a;
			} else {
				result = new IntersectNwa<>(result, a, factory, false);
			}
		}
		return result;
	}

	// TODO: Use interpolantProvider in the future (for now use always "hardcoded" wp)
	public NestedWordAutomaton<LETTER, IPredicate> buildInterpolantAutomaton(final List<LETTER> trace,
			final Collection<QualifiedTracePredicates> tracePredicates,
			final IInterpolantProvider<LETTER> interpolantProvider) throws AutomataLibraryException {
		final List<Integer> intTrace = getIntTrace(trace);
		assert isInterleaving(intTrace) : "Can only create an automaton for interleavings";
		final INestedWordAutomaton<Integer, String> automaton = buildMcrAutomaton(intTrace);
		mLogger.info("Constructing interpolant automaton by labelling MCR automaton.");
		final NestedWordAutomaton<LETTER, IPredicate> result =
				new NestedWordAutomaton<>(mAutomataServices, mAlphabet, mEmptyStackFactory);
		final IPredicate truePred = mPredicateUnifier.getTruePredicate();
		final IPredicate falsePred = mPredicateUnifier.getFalsePredicate();
		result.addState(true, false, truePred);
		result.addState(false, true, falsePred);
		final Set<IPredicate> initIps = new HashSet<>();
		initIps.add(truePred);
		initIps.add(falsePred);
		for (final QualifiedTracePredicates tp : tracePredicates) {
			final List<IPredicate> interpolants = tp.getPredicates();
			initIps.addAll(interpolants);
			final Map<String, IPredicate> stateMap = new HashMap<>();
			// Fill stateMap and automaton with the given interpolants
			String currentState = automaton.getInitialStates().iterator().next();
			IPredicate currentPredicate = truePred;
			stateMap.put(currentState, currentPredicate);
			for (int i = 0; i < trace.size(); i++) {
				final int index = intTrace.get(i);
				final Iterator<OutgoingInternalTransition<Integer, String>> succStates =
						automaton.internalSuccessors(currentState, index).iterator();
				if (!succStates.hasNext()) {
					throw new IllegalStateException("Trace is not present in the MCR automaton");
				}
				currentState = succStates.next().getSucc();
				final IPredicate nextPredicate = i == interpolants.size() ? falsePred
						: mPredicateUnifier.getOrConstructPredicate(interpolants.get(i));
				if (!result.contains(nextPredicate)) {
					result.addState(false, false, nextPredicate);
				}
				currentPredicate = nextPredicate;
				stateMap.put(currentState, currentPredicate);
			}
			// Find interpolants for all other states
			// Therefore start with all states with only labeled successors
			final ArrayDeque<String> dequeue = new ArrayDeque<>();
			final Map<String, Set<String>> unlabeledSuccessors = new HashMap<>();
			for (final String state : automaton.getStates()) {
				final Set<String> succs = new HashSet<>();
				for (final OutgoingInternalTransition<Integer, String> edge : automaton.internalSuccessors(state)) {
					final String succ = edge.getSucc();
					if (!stateMap.containsKey(succ)) {
						succs.add(succ);
					}
				}
				if (succs.isEmpty()) {
					dequeue.add(state);
				} else {
					unlabeledSuccessors.put(state, succs);
				}
			}
			final Script script = mManagedScript.getScript();
			while (!dequeue.isEmpty()) {
				final String state = dequeue.pop();
				unlabeledSuccessors.remove(state);
				IPredicate predicate = stateMap.get(state);
				if (predicate == null) {
					// Calculate the conjunction of wp for all successors
					final List<Term> wpConjuncts = new ArrayList<>();
					for (final OutgoingInternalTransition<Integer, String> outgoing : automaton
							.internalSuccessors(state)) {
						final IPredicate succ = stateMap.get(outgoing.getSucc());
						assert succ != null;
						wpConjuncts.add(mPredicateTransformer.weakestPrecondition(succ,
								mOriginalTrace.get(outgoing.getLetter()).getTransformula()));
					}
					// Quantify variables not contained in the original interpolants away
					final Term wpAnd = SmtUtils.and(script, wpConjuncts);
					final Set<TermVariable> ipVars =
							interpolants.stream().flatMap(x -> x.getVars().stream().map(IProgramVar::getTermVariable))
									.collect(Collectors.toSet());
					final Set<TermVariable> unnecessaryVars = Arrays.stream(wpAnd.getFreeVars())
							.filter(x -> !ipVars.contains(x)).collect(Collectors.toSet());
					final Term wpQuantified =
							SmtUtils.quantifier(script, QuantifiedFormula.FORALL, unnecessaryVars, wpAnd);
					final Term wpEliminated = PartialQuantifierElimination.tryToEliminate(mServices, mLogger,
							mManagedScript, wpQuantified, mSimplificationTechnique, mXnfConversionTechnique);
					// Add the wp conjunction as a predicate
					predicate = mPredicateUnifier.getOrConstructPredicate(wpEliminated);
					stateMap.put(state, predicate);
					if (!result.contains(predicate)) {
						result.addState(false, false, predicate);
					}
					// Add new states (with state as only unlabeled successor) to the queue
					for (final Entry<String, Set<String>> entry : unlabeledSuccessors.entrySet()) {
						final Set<String> succs = entry.getValue();
						if (succs.remove(state) && succs.isEmpty()) {
							dequeue.add(entry.getKey());
						}
					}
				}
				// Add the transitions to all successors
				for (final OutgoingInternalTransition<Integer, String> outgoing : automaton.internalSuccessors(state)) {
					result.addInternalTransition(predicate, mOriginalTrace.get(outgoing.getLetter()),
							stateMap.get(outgoing.getSucc()));
				}
			}
		}
		final Set<IPredicate> mcrIps = DataStructureUtils.difference(result.getStates(), initIps);
		mLogger.info("Construction finished. MCR generated " + mcrIps.size() + " new interpolants: " + mcrIps);
		return result;
	}
}
