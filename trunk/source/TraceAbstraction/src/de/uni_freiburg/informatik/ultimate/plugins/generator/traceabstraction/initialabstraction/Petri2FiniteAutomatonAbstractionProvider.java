/*
 * Copyright (C) 2022 Dominik Klumpp (klumpp@informatik.uni-freiburg.de)
 * Copyright (C) 2022 University of Freiburg
 *
 * This file is part of the ULTIMATE TraceAbstraction plug-in.
 *
 * The ULTIMATE TraceAbstraction plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE TraceAbstraction plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE TraceAbstraction plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE TraceAbstraction plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE TraceAbstraction plug-in grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.initialabstraction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INwaOutgoingLetterAndTransitionProvider;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.IPetriNet;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.Marking;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.PetriNetNot1SafeException;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.operations.LazyPetriNet2FiniteAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.operations.PetriNet2FiniteAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IPetriNet2FiniteAutomatonStateFactory;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.IcfgUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.ISLPredicate;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;

public abstract class Petri2FiniteAutomatonAbstractionProvider<L extends IIcfgTransition<?>, A extends INwaOutgoingLetterAndTransitionProvider<L, IPredicate>>
		implements IInitialAbstractionProvider<L, A> {
	private final Map<IcfgLocation, Boolean> mHopelessCache = new HashMap<>();

	protected final IInitialAbstractionProvider<L, ? extends IPetriNet<L, IPredicate>> mUnderlying;
	protected final AutomataLibraryServices mServices;
	protected final IPetriNet2FiniteAutomatonStateFactory<IPredicate> mStateFactory;

	public Petri2FiniteAutomatonAbstractionProvider(
			final IInitialAbstractionProvider<L, ? extends IPetriNet<L, IPredicate>> underlying,
			final IPetriNet2FiniteAutomatonStateFactory<IPredicate> stateFactory,
			final AutomataLibraryServices services) {
		mUnderlying = underlying;
		mServices = services;
		mStateFactory = stateFactory;
	}

	/**
	 * Determines if the locations belonging to the given marking are all hopeless. In this case, the state
	 * corresponding to this marking can be omitted from the program automaton.
	 */
	protected boolean areAllLocationsHopeless(final Set<? extends IcfgLocation> errorLocs,
			final Marking<?, IPredicate> marking) {
		for (final IPredicate place : marking) {
			if (place instanceof ISLPredicate) {
				final IcfgLocation location = ((ISLPredicate) place).getProgramPoint();
				if (!isLocationHopeless(errorLocs, location)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * A location is hopeless if in the CFG there is no path from this location to an error location.
	 */
	private boolean isLocationHopeless(final Set<? extends IcfgLocation> errorLocs, final IcfgLocation loc) {
		if (errorLocs.contains(loc)) {
			return false;
		}
		return !IcfgUtils.canReachCached(loc, e -> errorLocs.contains(e.getTarget()), e -> false, l -> {
			if (!mHopelessCache.containsKey(l)) {
				return LBool.UNKNOWN;
			}
			return mHopelessCache.get(l) ? LBool.SAT : LBool.UNSAT;
		}, (l, res) -> {
			assert mHopelessCache.getOrDefault(l, res) == res : "contradictory reachability";
			assert res != null;
			mHopelessCache.put(l, res);
		});
	}

	public static class Eager<L extends IIcfgTransition<?>>
			extends Petri2FiniteAutomatonAbstractionProvider<L, INestedWordAutomaton<L, IPredicate>> {

		public Eager(final IInitialAbstractionProvider<L, ? extends IPetriNet<L, IPredicate>> underlying,
				final IPetriNet2FiniteAutomatonStateFactory<IPredicate> stateFactory,
				final AutomataLibraryServices services) {
			super(underlying, stateFactory, services);
		}

		@Override
		public INestedWordAutomaton<L, IPredicate> getInitialAbstraction(final IIcfg<? extends IcfgLocation> icfg,
				final Set<? extends IcfgLocation> errorLocs) throws AutomataLibraryException {
			final IPetriNet<L, IPredicate> net = mUnderlying.getInitialAbstraction(icfg, errorLocs);
			try {
				return new PetriNet2FiniteAutomaton<>(mServices, mStateFactory, net,
						s -> areAllLocationsHopeless(errorLocs, s)).getResult();
			} catch (final PetriNetNot1SafeException e) {
				final Collection<?> unsafePlaces = e.getUnsafePlaces();
				if (unsafePlaces == null) {
					throw new AssertionError("Unable to find Petri net place that violates 1-safety");
				}
				final ISLPredicate unsafePlace = (ISLPredicate) unsafePlaces.iterator().next();
				final String proc = unsafePlace.getProgramPoint().getProcedure();
				throw new IllegalStateException("Petrification does not provide enough thread instances for " + proc);
			}
		}
	}

	public static class Lazy<L extends IIcfgTransition<?>>
			extends Petri2FiniteAutomatonAbstractionProvider<L, LazyPetriNet2FiniteAutomaton<L, IPredicate>> {

		public Lazy(final IInitialAbstractionProvider<L, ? extends IPetriNet<L, IPredicate>> underlying,
				final IPetriNet2FiniteAutomatonStateFactory<IPredicate> stateFactory,
				final AutomataLibraryServices services) {
			super(underlying, stateFactory, services);
		}

		@Override
		public LazyPetriNet2FiniteAutomaton<L, IPredicate> getInitialAbstraction(
				final IIcfg<? extends IcfgLocation> icfg, final Set<? extends IcfgLocation> errorLocs)
				throws AutomataLibraryException {
			final IPetriNet<L, IPredicate> net = mUnderlying.getInitialAbstraction(icfg, errorLocs);
			return new LazyPetriNet2FiniteAutomaton<>(mServices, mStateFactory, net,
					s -> areAllLocationsHopeless(errorLocs, s));
		}
	}
}
