/*
 * Copyright (C) 2022 Frank Schüssele (schuessf@informatik.uni-freiburg.de)
 * Copyright (C) 2022 University of Freiburg
 *
 * This file is part of the ULTIMATE BuchiAutomizer plug-in.
 *
 * The ULTIMATE BuchiAutomizer plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE BuchiAutomizer plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE BuchiAutomizer plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE BuchiAutomizer plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE BuchiAutomizer plug-in grant you additional permission
 * to convey the resulting work.
 */

package de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.cegar;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INwaOutgoingLetterAndTransitionProvider;
import de.uni_freiburg.informatik.ultimate.boogie.annotation.LTLPropertyCheck;
import de.uni_freiburg.informatik.ultimate.core.lib.exceptions.RunningTaskInfo;
import de.uni_freiburg.informatik.ultimate.core.lib.exceptions.ToolchainCanceledException;
import de.uni_freiburg.informatik.ultimate.core.lib.exceptions.ToolchainExceptionWrapper;
import de.uni_freiburg.informatik.ultimate.core.lib.models.annotation.BuchiProgramAcceptingStateAnnotation;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.lib.tracecheckerutils.initialabstraction.IInitialAbstractionProvider;
import de.uni_freiburg.informatik.ultimate.lib.tracecheckerutils.initialabstraction.NwaInitialAbstractionProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiCegarLoopBenchmarkGenerator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.RankVarConstructor;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.Activator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.CegarLoopStatisticsDefinitions;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.PredicateFactoryRefinement;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.WitnessAutomatonAbstractionProvider;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.witnesschecking.WitnessUtils.Property;
import de.uni_freiburg.informatik.ultimate.witnessparser.graph.WitnessEdge;
import de.uni_freiburg.informatik.ultimate.witnessparser.graph.WitnessNode;

/**
 *
 * @author Frank Schüssele (schuessf@informatik.uni-freiburg.de)
 *
 * @param <L>
 */
public class BuchiCegarLoopFactory<L extends IIcfgTransition<?>> {
	private final BuchiCegarLoopBenchmarkGenerator mCegarLoopBenchmark;
	private final Class<L> mTransitionClazz;

	public BuchiCegarLoopFactory(final Class<L> transitionClazz,
			final BuchiCegarLoopBenchmarkGenerator benchmarkGenerator) {
		mTransitionClazz = transitionClazz;
		mCegarLoopBenchmark = benchmarkGenerator;
	}

	public AbstractBuchiCegarLoop<L, ?> constructCegarLoop(final IIcfg<?> icfg,
			final RankVarConstructor rankVarConstructor, final PredicateFactory predicateFactory,
			final TAPreferences taPrefs, final IUltimateServiceProvider services,
			final INestedWordAutomaton<WitnessEdge, WitnessNode> witnessAutomaton) {
		final PredicateFactoryRefinement stateFactoryForRefinement = new PredicateFactoryRefinement(services,
				rankVarConstructor.getCsToolkitWithRankVariables().getManagedScript(), predicateFactory, false,
				Collections.emptySet());
		final var provider = createAutomataAbstractionProvider(services, predicateFactory, stateFactoryForRefinement,
				witnessAutomaton);
		final var initialAbstraction = constructInitialAbstraction(provider, icfg);
		return new BuchiAutomatonCegarLoop<>(icfg, rankVarConstructor, predicateFactory, taPrefs, services,
				mTransitionClazz, initialAbstraction, stateFactoryForRefinement, mCegarLoopBenchmark);
	}

	private static Set<IcfgLocation> getAcceptingStates(final IIcfg<?> icfg) {
		final Set<IcfgLocation> allStates =
				icfg.getProgramPoints().values().stream().flatMap(x -> x.values().stream()).collect(Collectors.toSet());
		if (LTLPropertyCheck.getAnnotation(icfg) == null) {
			return allStates;
		}
		return allStates.stream().filter(a -> BuchiProgramAcceptingStateAnnotation.getAnnotation(a) != null)
				.collect(Collectors.toSet());
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
		// OverallTime should include InitialAbstractionConstructionTime. Hence we start and stop both stopwatches.
		mCegarLoopBenchmark.start(CegarLoopStatisticsDefinitions.OverallTime);
		mCegarLoopBenchmark.start(CegarLoopStatisticsDefinitions.InitialAbstractionConstructionTime);
		try {
			return provider.getInitialAbstraction(icfg, getAcceptingStates(icfg));
		} catch (final AutomataOperationCanceledException ex) {
			final RunningTaskInfo runningTaskInfo =
					new RunningTaskInfo(this.getClass(), "constructing initial abstraction");
			ex.addRunningTaskInfo(runningTaskInfo);
			throw new ToolchainExceptionWrapper(Activator.PLUGIN_ID, ex);
		} catch (final ToolchainCanceledException ex) {
			final RunningTaskInfo runningTaskInfo =
					new RunningTaskInfo(this.getClass(), "constructing initial abstraction");
			ex.addRunningTaskInfo(runningTaskInfo);
			throw ex;
		} catch (final AutomataLibraryException e) {
			throw new ToolchainExceptionWrapper(Activator.PLUGIN_ID, e);
		} finally {
			mCegarLoopBenchmark.stop(CegarLoopStatisticsDefinitions.InitialAbstractionConstructionTime);
			mCegarLoopBenchmark.stop(CegarLoopStatisticsDefinitions.OverallTime);
		}
	}
}
