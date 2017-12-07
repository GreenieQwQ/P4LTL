/*
 * Copyright (C) 2016 Alexander Nutz (nutz@informatik.uni-freiburg.de)
 * Copyright (C) 2016 Mostafa M.A. (mostafa.amin93@gmail.com)
 * Copyright (C) 2016 University of Freiburg
 *
 * This file is part of the ULTIMATE TreeAutomizer Plugin.
 *
 * The ULTIMATE TreeAutomizer Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE TreeAutomizer Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE TreeAutomizer Plugin. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE TreeAutomizer Plugin, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE TreeAutomizer Plugin grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.generator.treeautomizer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.AutomatonDefinitionPrinter;
import de.uni_freiburg.informatik.ultimate.automata.AutomatonDefinitionPrinter.Format;
import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.tree.ITreeAutomatonBU;
import de.uni_freiburg.informatik.ultimate.automata.tree.InterpolantTreeAutomatonBU;
import de.uni_freiburg.informatik.ultimate.automata.tree.TreeAutomatonBU;
import de.uni_freiburg.informatik.ultimate.automata.tree.TreeAutomatonRule;
import de.uni_freiburg.informatik.ultimate.automata.tree.TreeRun;
import de.uni_freiburg.informatik.ultimate.automata.tree.operations.Accepts;
import de.uni_freiburg.informatik.ultimate.automata.tree.operations.IsEmpty;
import de.uni_freiburg.informatik.ultimate.automata.tree.operations.difference.LazyDifference;
import de.uni_freiburg.informatik.ultimate.automata.tree.operations.minimization.Minimize;
import de.uni_freiburg.informatik.ultimate.core.lib.results.TimeoutResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.TreeAutomizerSatResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.TreeAutomizerUnsatResult;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.results.IResult;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.treeautomizer.HCSymbolTable;
import de.uni_freiburg.informatik.ultimate.lib.treeautomizer.HornClause;
import de.uni_freiburg.informatik.ultimate.lib.treeautomizer.HornClausePredicateSymbol;
import de.uni_freiburg.informatik.ultimate.lib.treeautomizer.HornUtilConstants;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.hoaretriple.IHoareTripleChecker.Validity;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.PredicateUnifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.treeautomizer.Activator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.treeautomizer.parsing.HornAnnot;
import de.uni_freiburg.informatik.ultimate.plugins.generator.treeautomizer.preferences.TreeAutomizerPreferenceInitializer;

/**
 * @author Mostafa M.A. (mostafa.amin93@gmail.com)
 * @author Alexander Nutz (nutz@informatik.uni-freiburg.de)
 *
 */
public class TreeAutomizerCEGAR {

	private TreeAutomatonBU<HornClause, IPredicate> mAbstraction;
	private final HCStateFactory mStateFactory;
	private final ManagedScript mBackendSmtSolverScript;
	private int mIteration;
	private final ILogger mLogger;

	private final HornAnnot mHornAnnot;
	private final HCPredicate mInitialPredicate;
	private final HCPredicate mFinalPredicate;

	private TreeChecker mChecker;

	/**
	 * Interpolant automaton of this iteration.
	 */
	//protected InterpolantTreeAutomatonBU<HornClause, IPredicate> mInterpolAutomaton;
	protected ITreeAutomatonBU<HornClause, IPredicate> mInterpolAutomaton;
	private final HCSymbolTable mSymbolTable;
//	private final CfgSmtToolkit mCfgSmtToolkit;
	private final HCHoareTripleChecker mHoareTripleChecker;
	private final PredicateUnifier mPredicateUnifier;

	private final HCPredicateFactory mPredicateFactory;
	private final AutomataLibraryServices mAutomataLibraryServices;

	private final List<HornClause> mAlphabet;
	private TreeRun<HornClause, IPredicate> mCounterExample;
	private final IUltimateServiceProvider mServices;
	private final IPreferenceProvider mPreferences;

	/**
	 * Constructor for TreeAutomizer CEGAR
	 *
	 * @param services
	 * @param storage
	 * @param annot
	 * @param taPrefs
	 * @param logger
	 */
	public TreeAutomizerCEGAR(final IUltimateServiceProvider services, final IToolchainStorage storage,
			final HornAnnot annot, final TAPreferences taPrefs, final ILogger logger) {
		mBackendSmtSolverScript = annot.getScript();
		mSymbolTable = annot.getSymbolTable();
		mLogger = logger;
		mHornAnnot = annot;

		mAlphabet = (List<HornClause>) mHornAnnot.getAnnotationsAsMap().get(HornUtilConstants.HORN_ANNOT_NAME);

		mIteration = 0;

		mAutomataLibraryServices = new AutomataLibraryServices(services);

		mServices = services;

		mPredicateFactory = new HCPredicateFactory(services, mBackendSmtSolverScript, mSymbolTable,
				SimplificationTechnique.SIMPLIFY_DDA, XnfConversionTechnique.BDD_BASED);


		mInitialPredicate = mPredicateFactory.getTruePredicate();
		mFinalPredicate = mPredicateFactory.getFalsePredicate();

//		mCfgSmtToolkit = new CfgSmtToolkit(new ModifiableGlobalsTable(new HashRelation<>()), mBackendSmtSolverScript,
//				mSymbolTable, mInitialPredicate, Collections.singleton(HornUtilConstants.HORNCLAUSEMETHODNAME));
		mPredicateUnifier = new PredicateUnifier(services, mBackendSmtSolverScript, mPredicateFactory, mSymbolTable,
				SimplificationTechnique.SIMPLIFY_DDA, XnfConversionTechnique.BDD_BASED, mInitialPredicate);
		mHoareTripleChecker = new HCHoareTripleChecker(mPredicateUnifier, mBackendSmtSolverScript, mPredicateFactory,
				mSymbolTable);
		mStateFactory = new HCStateFactory(mBackendSmtSolverScript, mPredicateFactory, mLogger);

		mPredicateUnifier.getOrConstructPredicate(mInitialPredicate.getFormula());
		mPredicateUnifier.getOrConstructPredicate(mFinalPredicate.getFormula());

		mPreferences = services.getPreferenceProvider(Activator.PLUGIN_ID);
	}

	/**
	 * Process the iterations of the CEGAR.
	 *
	 * @return Result: The result of the verification. SAFE, UNSAFE or UNKNOWN
	 */
	public IResult iterate() throws AutomataLibraryException {
		getInitialAbstraction();

		mLogger.debug("Abstraction tree automaton before iteration #" + (mIteration + 1));
		mLogger.debug(mAbstraction);
		final int mITERATIONS = 10;

		while (mServices.getProgressMonitorService().continueProcessing()
				&& (mITERATIONS == -1 || mIteration < mITERATIONS)) {
			mLogger.debug("Iteration #" + (mIteration + 1));
			final TreeRun<HornClause, IPredicate> counterExample = isAbstractionCorrect();
			if (counterExample == null) {
				mLogger.info("The horn clause set is SAT!!");

				mLogger.info("states of the final abstraction: (i.e., the model for the uninterpreted predicates)");
				for (final IPredicate state : mAbstraction.getStates()) {
					mLogger.info(state.toString());
				}

				return new TreeAutomizerSatResult(Activator.PLUGIN_ID, "SAT", "The given horn clause set is SAT");
			}

			mBackendSmtSolverScript.lock(this);
			mBackendSmtSolverScript.push(this, 1);

			if (getCounterexampleFeasibility(counterExample, this) == LBool.SAT) {

				mLogger.info("The program is unsafe, feasible counterexample.");
				mLogger.info(counterExample.getTree());
				mBackendSmtSolverScript.pop(this, 1);
				mBackendSmtSolverScript.unlock(this);
				return new TreeAutomizerUnsatResult(Activator.PLUGIN_ID, "UNSAT", "The given horn clause set is UNSAT");
			}
			mLogger.debug("Getting Interpolants...");
			final Map<TreeRun<HornClause, IPredicate>, Term> interpolantsMap = retrieveInterpolantsMap(
					mChecker.getSSA(), counterExample);
			mBackendSmtSolverScript.pop(this, 1);
			mBackendSmtSolverScript.unlock(this);

			constructInterpolantAutomaton(counterExample, interpolantsMap);
			mLogger.debug("Interpolant automaton:");
			mLogger.debug(mInterpolAutomaton);

			mLogger.debug("Refining abstract model...");
			refineAbstraction();
			mLogger.debug("Abstraction tree automaton before iteration #" + (mIteration + 1));
			mLogger.debug(mAbstraction);
		}
		mLogger.info("The program is not decieded...");
		return new TimeoutResult(Activator.PLUGIN_ID, "TreeAutomizer says UNKNOWN/TIMEOUT");
	}

	protected void getInitialAbstraction() throws AutomataLibraryException {

		mAbstraction = new TreeAutomatonBU<>(mStateFactory);
		for (final HornClause clause : mAlphabet) {
			final List<IPredicate> tail = new ArrayList<>();
			for (final HornClausePredicateSymbol sym : clause.getBodyPredicates()) {
				tail.add(mPredicateFactory.createTruePredicateWithLocation(sym));
			}
			if (clause.isHeadFalse()) {
				mAbstraction.addRule(new TreeAutomatonRule<>(clause, tail, mFinalPredicate));
			} else {
				mAbstraction.addRule(new TreeAutomatonRule<>(clause, tail,
						mPredicateFactory.createTruePredicateWithLocation(clause.getHeadPredicate())));
			}
		}

		mAbstraction.addFinalState(mFinalPredicate);
		for (final IPredicate state : mAbstraction.getStates()) {
			mPredicateUnifier.getOrConstructPredicate(state.getFormula());
		}

		mLogger.debug("Initial abstraction tree Automaton:");
		mLogger.debug(mAbstraction);
	}

	private TreeRun<HornClause, IPredicate> isAbstractionCorrect() throws AutomataOperationCanceledException {
		final IsEmpty<HornClause, IPredicate> emptiness = new IsEmpty<>(mAutomataLibraryServices, mAbstraction);

		final TreeRun<HornClause, IPredicate> counterexample = emptiness.getTreeRun();

		if (counterexample != null) {
			mLogger.debug("Error trace found.");
			mLogger.debug(counterexample.toString());
		} else {
			mLogger.debug("No (further) counterexample found in abstraction.");
		}

		mCounterExample = counterexample;
		return counterexample;
	}

	private LBool getCounterexampleFeasibility(final TreeRun<HornClause, IPredicate> counterexample,
			final Object lockOwner) {
		mChecker = new TreeChecker(counterexample, mBackendSmtSolverScript, mInitialPredicate, mFinalPredicate, mLogger,
				mPredicateUnifier, mSymbolTable);
		return mChecker.checkTrace(lockOwner);
	}

	protected void constructInterpolantAutomaton(final TreeRun<HornClause, IPredicate> counterexample,
			final Map<TreeRun<HornClause, IPredicate>, Term> interpolantsMapSsaVersioned)
					throws AutomataOperationCanceledException {

		final TreeRun<HornClause, IPredicate> treeRunWithInterpolants = mChecker
				.annotateTreeRunWithInterpolants(interpolantsMapSsaVersioned);
		//ITreeAutomatonBU<HornClause, IPredicate> automaton = treeRunWithInterpolants.getAutomaton(mStateFactory);

		//mInterpolAutomaton = treeRunWithInterpolants.getInterpolantAutomaton(mStateFactory);
		mInterpolAutomaton = treeRunWithInterpolants.getAutomaton(mStateFactory);
		for (final IPredicate p : mInterpolAutomaton.getStates()) {
			mPredicateUnifier.getOrConstructPredicate(p.getFormula());
		}

		((TreeAutomatonBU<HornClause, IPredicate>) mInterpolAutomaton).extendAlphabet(mAbstraction.getAlphabet());

		assert allRulesAreInductive(mInterpolAutomaton);
		generalizeCounterExample(mInterpolAutomaton);

		// dump interpolant automaton
		final String automataDumpPath =
				mPreferences.getString(TreeAutomizerPreferenceInitializer.LABEL_AutomataDumpPath);
		if (!automataDumpPath.isEmpty()) {
			final String filename = mHornAnnot.getFileName() + "_interpolant_automaton_nr_" + mIteration;
			writeAutomatonToFile(mServices, mInterpolAutomaton, automataDumpPath, filename, Format.ATS_NUMERATE, "");
		}

		assert allRulesAreInductive(mInterpolAutomaton);

	}

	/**
	 * Checks if all rules in the given (interpolant-)automaton correspond to a
	 * valid Hoare triple.
	 *
	 * @param automaton
	 * @return boolean if all the rules are inductive.
	 */
	private boolean allRulesAreInductive(final ITreeAutomatonBU<HornClause, IPredicate> automaton) {
		for (final List<IPredicate> source : automaton.getSourceCombinations()) {
			for (final TreeAutomatonRule<HornClause, IPredicate> rule : automaton.getSuccessors(source)) {
				final Validity validity = mHoareTripleChecker.check(rule.getSource(), rule.getLetter(), rule.getDest());
				if (validity != Validity.VALID) {
					return false;
				}
			}
		}
		return true;
	}

	private void generalizeCounterExample(final ITreeAutomatonBU<HornClause, IPredicate> counterexample) {
		final Set<TreeAutomatonRule<HornClause, IPredicate>> rules = new HashSet<>();
		for (final TreeAutomatonRule<HornClause, IPredicate> rule : new CandidateRuleProvider(counterexample,
				mHoareTripleChecker).getCandidateRules()) {
			if (mHoareTripleChecker.check(rule) == Validity.VALID) {
				mLogger.debug(
						"Adding Rule: " + rule.getLetter() + "(" + rule.getSource() + ")" + " --> " + rule.getDest());
				rules.add(rule);
			}
		}
		mLogger.debug("Generalizing counterExample:");
		for (final TreeAutomatonRule<HornClause, IPredicate> rule : rules) {
			mInterpolAutomaton.addRule(rule);
		}
	}

	private ITreeAutomatonBU<HornClause, IPredicate> getCounterExample() {
		return mInterpolAutomaton;
	}

	protected boolean refineAbstraction() throws AutomataLibraryException {
		// dump abstraction
		final String automataDumpPath =
				mPreferences.getString(TreeAutomizerPreferenceInitializer.LABEL_AutomataDumpPath);
		if (!automataDumpPath.isEmpty()) {
			final String filename = mHornAnnot.getFileName() + "_abstraction_nr_" + mIteration;
			writeAutomatonToFile(mServices, mAbstraction, automataDumpPath, filename, Format.ATS_NUMERATE, "");
		}

		/*
		final ITreeAutomatonBU<HornClause, IPredicate> cCounterExample = (new Complement<>(mAutomataLibraryServices,
				mStateFactory, getCounterExample())).getResult();
		mLogger.debug("Complemented counter example automaton:");
		mLogger.debug(cCounterExample);

		assert !(new Accepts<>(mAutomataLibraryServices, cCounterExample, mCounterExample).getResult());
		mAbstraction = (TreeAutomatonBU<HornClause, IPredicate>) (new Intersect<>(mAutomataLibraryServices,
				mStateFactory, mAbstraction, cCounterExample)).getResult();
		mLogger.debug(String.format("Size before totalize %d states, %d rules.", mAbstraction.getStates().size(),
				((Set<TreeAutomatonRule<HornClause, IPredicate>>) mAbstraction.getRules()).size()));
		mAbstraction = (TreeAutomatonBU<HornClause, IPredicate>) (new Totalize<>(mAutomataLibraryServices,
				mStateFactory, mAbstraction)).getResult();
		mLogger.debug(String.format("Size after totalize %d states, %d rules.", mAbstraction.getStates().size(),
				((Set<TreeAutomatonRule<HornClause, IPredicate>>) mAbstraction.getRules()).size()));
		*/

		mAbstraction = (TreeAutomatonBU<HornClause, IPredicate>) (new LazyDifference<HornClause, IPredicate>(
				mAutomataLibraryServices, mStateFactory, mAbstraction, getCounterExample()).getResult());
		mLogger.debug(String.format("Size before minimize %d states, %d rules.", mAbstraction.getStates().size(),
				((Set<TreeAutomatonRule<HornClause, IPredicate>>) mAbstraction.getRules()).size()));

		final Set<IPredicate> states = new HashSet<>();
		states.addAll(mAbstraction.getStates());
		for (final IPredicate pred : states) {

			if ("false".equals(pred.getFormula().toString())) {
				mAbstraction.removeState(pred);
			}
		}
		//mAbstraction = (TreeAutomatonBU<HornClause, IPredicate>) (new Minimize<>(mAutomataLibraryServices,
		//		mStateFactory, mAbstraction)).getResult();
		mLogger.debug(String.format("Size after minimize %d states, %d rules.", mAbstraction.getStates().size(),
				((Set<TreeAutomatonRule<HornClause, IPredicate>>) mAbstraction.getRules()).size()));

		mLogger.debug("Refine ends...");

		assert !(new Accepts<>(mAutomataLibraryServices, mAbstraction, mCounterExample).getResult());
		++mIteration;
		return false;
	}

	private Map<TreeRun<HornClause, IPredicate>, Term> retrieveInterpolantsMap(final HCSsa ssa,
			final TreeRun<HornClause, IPredicate> counterExample) {

		final Term[] interpolants = mBackendSmtSolverScript.getInterpolants(this,
				ssa.getNamedTermList(mBackendSmtSolverScript, this), ssa.getStartsOfSubTrees());

		final List<TreeRun<HornClause, IPredicate>> subtreesInPostOrder = computeSubtreesInPostOrder(counterExample);

		final Map<TreeRun<HornClause, IPredicate>, Term> interpolantsMap = new HashMap<>();

		for (int i = 0; i < interpolants.length; ++i) {
			final TreeRun<HornClause, IPredicate> st = subtreesInPostOrder.get(i);

			interpolantsMap.put(st, interpolants[i]);
		}

		// The root of a tree interpolant is implicitly "false"
		interpolantsMap.put(counterExample, mBackendSmtSolverScript.term(this, "false"));

		return interpolantsMap;
	}

	private static List<TreeRun<HornClause, IPredicate>> computeSubtreesInPostOrder(
			final TreeRun<HornClause, IPredicate> root) {
		final List<TreeRun<HornClause, IPredicate>> result = new ArrayList<>();

		for (final TreeRun<HornClause, IPredicate> ch : root.getChildren()) {
			result.addAll(computeSubtreesInPostOrder(ch));
		}

		// need to make an exception for the leafs (related to the fact that our
		// tree automata have initial states)
		if (root.getRootSymbol() != null) {
			result.add(root);
		}
		return result;
	}

	protected void computeCFGHoareAnnotation() {
		return;
	}

	public IElement getArtifact() {
		return null;
	}

	protected static void writeAutomatonToFile(final IUltimateServiceProvider services,
			final IAutomaton<?, IPredicate> automaton, final String path, final String filename, final Format format,
			final String message) {
		new AutomatonDefinitionPrinter<String, String>(new AutomataLibraryServices(services), "ta",
				path + "/" + filename, format, message, automaton);
	}
}
