package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.mcr.IInterpolantProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.transitions.TransFormula;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.MultiDimensionalSelectOverStoreEliminationUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.PredicateTransformer;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.TermDomainOperationProvider;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.ManagedScript;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.QuantifierUtils;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.arrays.ArrayIndexEqualityManager;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.arrays.MultiDimensionalSelectOverNestedStore;
import de.uni_freiburg.informatik.ultimate.logic.QuantifiedFormula;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.util.datastructures.ThreeValuedEquivalenceRelation;
import de.uni_freiburg.informatik.ultimate.util.datastructures.poset.TopologicalSorter;

/**
 * IInterpolantProvider using sp or wp. For better interpolants, we abstract all variables away, that are not read
 * afterwards.
 *
 * @author Frank Schüssele (schuessf@informatik.uni-freiburg.de)
 */
public abstract class SpWpInterpolantProvider<LETTER extends IIcfgTransition<?>>
		implements IInterpolantProvider<LETTER> {

	protected final ManagedScript mManagedScript;
	protected final IUltimateServiceProvider mServices;
	protected final ILogger mLogger;
	protected final SimplificationTechnique mSimplificationTechnique;
	protected final XnfConversionTechnique mXnfConversionTechnique;
	private final IPredicateUnifier mPredicateUnifier;
	private final ArrayIndexEqualityManager mAiem;
	protected final PredicateTransformer<Term, IPredicate, TransFormula> mPredicateTransformer;

	public SpWpInterpolantProvider(final IUltimateServiceProvider services, final ILogger logger,
			final ManagedScript managedScript, final SimplificationTechnique simplificationTechnique,
			final XnfConversionTechnique xnfConversionTechnique, final IPredicateUnifier predicateUnifier) {
		mManagedScript = managedScript;
		mServices = services;
		mLogger = logger;
		mSimplificationTechnique = simplificationTechnique;
		mXnfConversionTechnique = xnfConversionTechnique;
		mPredicateUnifier = predicateUnifier;
		mPredicateTransformer =
				new PredicateTransformer<>(mManagedScript, new TermDomainOperationProvider(mServices, mManagedScript));
		// ArrayIndexEqualityManager to eliminate stores with true as context (i.e. without any known equalities)
		mAiem = new ArrayIndexEqualityManager(new ThreeValuedEquivalenceRelation<>(),
				mManagedScript.getScript().term("true"), QuantifiedFormula.EXISTS, mLogger, mManagedScript);
		mAiem.unlockSolver();
	}

	private <STATE> List<STATE> revTopSort(final INestedWordAutomaton<LETTER, STATE> automaton,
			final Map<STATE, IPredicate> stateMap) {
		final Map<STATE, Set<STATE>> successors = new HashMap<>();
		for (final STATE state : automaton.getStates()) {
			if (stateMap.containsKey(state)) {
				continue;
			}
			final Set<STATE> succs = new HashSet<>();
			for (final OutgoingInternalTransition<LETTER, STATE> edge : automaton.internalSuccessors(state)) {
				final STATE succ = edge.getSucc();
				if (!stateMap.containsKey(succ)) {
					succs.add(succ);
				}
			}
			successors.put(state, succs);
		}
		return new TopologicalSorter<>(successors::get).reversedTopologicalOrdering(successors.keySet()).get();
	}

	private <STATE> List<STATE> getOrder(final List<STATE> revTopOrder) {
		if (!useReversedOrder()) {
			Collections.reverse(revTopOrder);
		}
		return revTopOrder;
	}

	@Override
	public <STATE> Map<STATE, IPredicate> getInterpolants(final INestedWordAutomaton<LETTER, STATE> automaton,
			final Map<STATE, IPredicate> stateMap) {
		final Set<TermVariable> ipVars = stateMap.values().stream().flatMap(x -> getTermVariables(x.getVars()).stream())
				.collect(Collectors.toSet());
		final Map<STATE, Set<TermVariable>> liveIpVariables = stateMap.keySet().stream()
				.collect(Collectors.toMap(x -> x, x -> getTermVariables(stateMap.get(x).getVars())));
		final List<STATE> revTopOrder = revTopSort(automaton, stateMap);
		for (final STATE state : revTopOrder) {
			final Set<TermVariable> vars = new HashSet<>();
			for (final OutgoingInternalTransition<LETTER, STATE> edge : automaton.internalSuccessors(state)) {
				vars.addAll(getTermVariables(edge.getLetter().getTransformula().getInVars().keySet()));
				vars.addAll(liveIpVariables.get(edge.getSucc()));
			}
			vars.retainAll(ipVars);
			liveIpVariables.put(state, vars);
		}
		for (final STATE state : getOrder(revTopOrder)) {
			final Term term = calculateTerm(automaton, state, stateMap);
			if (term == null) {
				continue;
			}
			// Abstract all variables away that are not read afterwards and do not occur in the original interpolants
			final Set<TermVariable> abstractedVars = Arrays.stream(term.getFreeVars())
					.filter(x -> !liveIpVariables.get(state).contains(x)).collect(Collectors.toSet());
			Term result = getAbstraction(term, abstractedVars);
			// Ignore the interpolant, if it still contains quantifiers
			if (!QuantifierUtils.isQuantifierFree(result)) {
				continue;
			}
			// Eliminate all stores (using case distinction on index equalities)
			final List<MultiDimensionalSelectOverNestedStore> stores = MultiDimensionalSelectOverNestedStore
					.extractMultiDimensionalSelectOverStores(mManagedScript.getScript(), result);
			for (final MultiDimensionalSelectOverNestedStore m : stores) {
				result = MultiDimensionalSelectOverStoreEliminationUtils.replace(mManagedScript, mAiem, result, m);
			}
			stateMap.put(state, mPredicateUnifier.getOrConstructPredicate(result));
		}
		return stateMap;
	}

	private static Set<TermVariable> getTermVariables(final Collection<IProgramVar> vars) {
		return vars.stream().map(IProgramVar::getTermVariable).collect(Collectors.toSet());
	}

	protected abstract <STATE> Term calculateTerm(final INestedWordAutomaton<LETTER, STATE> automaton, STATE state,
			Map<STATE, IPredicate> stateMap);

	protected abstract Term getAbstraction(Term term, Set<TermVariable> variables);

	protected abstract boolean useReversedOrder();
}
