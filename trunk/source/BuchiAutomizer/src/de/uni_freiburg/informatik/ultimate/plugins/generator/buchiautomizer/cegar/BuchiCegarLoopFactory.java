package de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.cegar;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INwaOutgoingLetterAndTransitionProvider;
import de.uni_freiburg.informatik.ultimate.boogie.annotation.LTLPropertyCheck;
import de.uni_freiburg.informatik.ultimate.core.lib.models.annotation.BuchiProgramAcceptingStateAnnotation;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.lib.tracecheckerutils.initialabstraction.IInitialAbstractionProvider;
import de.uni_freiburg.informatik.ultimate.lib.tracecheckerutils.initialabstraction.NwaInitialAbstractionProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.RankVarConstructor;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PredicateFactoryRefinement;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.WitnessAutomatonAbstractionProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.witnesschecking.WitnessUtils.Property;
import de.uni_freiburg.informatik.ultimate.witnessparser.graph.WitnessEdge;
import de.uni_freiburg.informatik.ultimate.witnessparser.graph.WitnessNode;

public class BuchiCegarLoopFactory<L extends IIcfgTransition<?>> {
	// private void getInitialAbstraction() {
	// final Collection<IcfgLocation> acceptingNodes;
	// final Collection<IcfgLocation> allNodes = new HashSet<>();
	// for (final Map<DebugIdentifier, ? extends IcfgLocation> prog2pp : mIcfg.getProgramPoints().values()) {
	// allNodes.addAll(prog2pp.values());
	// }
	//
	// // check if we run in LTL mode and set accepting states accordingly
	// if (LTLPropertyCheck.getAnnotation(mIcfg) != null) {
	// mLTLMode = true;
	// acceptingNodes =
	// allNodes.stream().filter(a -> BuchiProgramAcceptingStateAnnotation.getAnnotation(a) != null)
	// .collect(Collectors.toSet());
	// } else {
	// mLTLMode = false;
	// acceptingNodes = allNodes;
	// }
	// mAbstraction = Cfg2Automaton.constructAutomatonWithSPredicates(mServices, mIcfg, mDefaultStateFactory,
	// acceptingNodes, mPref.interprocedural(), mPredicateFactory);
	// if (!ALLOW_CALLS && !mAbstraction.getVpAlphabet().getCallAlphabet().isEmpty()) {
	// throw new AssertionError("Calls are not allowed in this debugging mode");
	// }
	// if (mWitnessAutomaton != null) {
	// try {
	// final AutomataLibraryServices services = new AutomataLibraryServices(mServices);
	// final NestedWordAutomatonReachableStates<WitnessEdge, WitnessNode> reach =
	// new NestedWordAutomatonReachableStates<>(services, mWitnessAutomaton);
	// final INestedWordAutomaton<WitnessEdge, WitnessNode> allAccepting =
	// new NestedWordAutomatonFilteredStates<>(services, reach, mWitnessAutomaton.getStates(),
	// mWitnessAutomaton.getInitialStates(), mWitnessAutomaton.getStates());
	// mAbstraction = WitnessUtils.constructIcfgAndWitnessProduct(mServices, mAbstraction, allAccepting,
	// mCsToolkitWithoutRankVars, mPredicateFactory, mStateFactoryForRefinement, mLogger,
	// Property.TERMINATION);
	// } catch (final AutomataOperationCanceledException e) {
	// final RunningTaskInfo rti = new RunningTaskInfo(getClass(),
	// "constructing product with witness of size " + mWitnessAutomaton.size());
	// throw new ToolchainCanceledException(e, rti);
	// }
	// }
	// }

	public AbstractBuchiCegarLoop<L, ?> constructCegarLoop(final IIcfg<?> icfg,
			final RankVarConstructor rankVarConstructor, final PredicateFactory predicateFactory,
			final TAPreferences taPrefs, final IUltimateServiceProvider services,
			final INestedWordAutomaton<WitnessEdge, WitnessNode> witnessAutomaton, final Class<L> transitionClazz) {
		final PredicateFactoryRefinement stateFactoryForRefinement = new PredicateFactoryRefinement(services,
				rankVarConstructor.getCsToolkitWithRankVariables().getManagedScript(), predicateFactory, false,
				Collections.emptySet());
		final var provider = createAutomataAbstractionProvider(services, predicateFactory, stateFactoryForRefinement,
				witnessAutomaton);
		final var initialAbstraction = constructInitialAbstraction(provider, icfg);
		return new BuchiAutomatonCegarLoop<>(icfg, rankVarConstructor, predicateFactory, taPrefs, services,
				transitionClazz, initialAbstraction, stateFactoryForRefinement);
	}

	private static Set<IcfgLocation> getAcceptingStates(final IIcfg<?> icfg) {
		final Set<IcfgLocation> allStates =
				icfg.getProgramPoints().values().stream().flatMap(x -> x.values().stream()).collect(Collectors.toSet());
		if (LTLPropertyCheck.getAnnotation(icfg) != null) {
			return allStates.stream().filter(a -> BuchiProgramAcceptingStateAnnotation.getAnnotation(a) != null)
					.collect(Collectors.toSet());
		}
		return allStates;
	}

	private IInitialAbstractionProvider<L, ? extends INestedWordAutomaton<L, IPredicate>>
			createAutomataAbstractionProvider(final IUltimateServiceProvider services,
					final PredicateFactory predicateFactory, final PredicateFactoryRefinement stateFactory,
					final INwaOutgoingLetterAndTransitionProvider<WitnessEdge, WitnessNode> witnessAutomaton) {
		final IInitialAbstractionProvider<L, INestedWordAutomaton<L, IPredicate>> provider =
				new NwaInitialAbstractionProvider<>(services, stateFactory, true, predicateFactory);
		if (witnessAutomaton == null) {
			return provider;
		}
		return new WitnessAutomatonAbstractionProvider<>(services, predicateFactory, stateFactory, provider,
				witnessAutomaton, Property.TERMINATION);
	}

	private <A extends IAutomaton<L, IPredicate>> A
			constructInitialAbstraction(final IInitialAbstractionProvider<L, A> provider, final IIcfg<?> icfg) {
		// TODO: Do some statistics
		// TODO: Proper exception handling
		try {
			return provider.getInitialAbstraction(icfg, getAcceptingStates(icfg));
		} catch (final AutomataLibraryException e) {
			throw new AssertionError(e);
		}
	}
}
