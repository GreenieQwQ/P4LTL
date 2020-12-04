/*
 * Copyright (C) 2020 Marcel Ebbinghaus
 * Copyright (C) 2020 Dominik Klumpp (klumpp@informatik.uni-freiburg.de)
 * Copyright (C) 2020 University of Freiburg
 *
 * This file is part of the ULTIMATE Automata Library.
 *
 * The ULTIMATE Automata Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE Automata Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Automata Library. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE Automata Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE Automata Library grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.automata.petrinet.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INwaOutgoingLetterAndTransitionProvider;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.VpAlphabet;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingCallTransition;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingReturnTransition;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.IPetriNet;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.ITransition;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.Marking;
import de.uni_freiburg.informatik.ultimate.automata.petrinet.PetriNetNot1SafeException;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IPetriNet2FiniteAutomatonStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IStateFactory;

public class LazyPetriNet2FiniteAutomaton<L, S> implements INwaOutgoingLetterAndTransitionProvider<L, S> {

	private final IPetriNet<L, S> mOperand;
	private final IPetriNet2FiniteAutomatonStateFactory<S> mStateFactory;
	private final Map<Marking<L, S>, S> mMarking2State = new HashMap<>();
	private final Map<S, Marking<L, S>> mState2Marking = new HashMap<>();
	private Set<S> mInitialStates;
	
	public LazyPetriNet2FiniteAutomaton(final IPetriNet<L, S> net,
			final IPetriNet2FiniteAutomatonStateFactory<S> factory,
			final IPetriNet<L, S> operand) throws PetriNetNot1SafeException{
		mOperand = operand;
		mStateFactory = factory;
	}

	@Override
	public IStateFactory<S> getStateFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VpAlphabet<L> getVpAlphabet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public S getEmptyStackState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<S> getInitialStates() {
		if (mInitialStates == null) {
			mInitialStates = constructInitialState();
		}
		return mInitialStates;
	}

	private Set<S> constructInitialState() {
		getOrConstructState(new Marking<L,S>(mOperand.getInitialPlaces()));
		return null;
	}

	private S getOrConstructState(Marking<L,S> marking) {
		S state = mMarking2State.get(marking);
		if (state == null) {
			state = mStateFactory.getContentOnPetriNet2FiniteAutomaton(marking);
			mMarking2State.put(marking, state);
			mState2Marking.put(state, marking);
		}
		return state;
		
	}

	@Override
	public boolean isInitial(final S state) {
		Set<S> initialPlaces = mOperand.getInitialPlaces();
		if (mState2Marking.get(state).containsAll(initialPlaces)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isFinal(final S state) {
		return mOperand.isAccepting(mState2Marking.get(state));
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String sizeInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<OutgoingInternalTransition<L, S>> internalSuccessors(final S state, final L letter) throws PetriNetNot1SafeException {
		final Marking<L,S> marking = mState2Marking.get(state);
		final Collection<OutgoingInternalTransition<L, S>> result = new ArrayList<>();
		final Set<ITransition<L, S>> outgoing = getOutgoingNetTransitions(marking);
		for (final ITransition<L, S> transition : outgoing) {
			if (marking.isTransitionEnabled(transition, mOperand)) {
				final Marking<L, S> succMarking = marking.fireTransition(transition, mOperand);
				final S succState = getOrConstructState(succMarking);
				result.add(new OutgoingInternalTransition<>(letter, succState));
			}
		}
		return result;
	}
	
	private Set<ITransition<L, S>> getOutgoingNetTransitions(final Marking<L, S> marking) {
		final Set<ITransition<L, S>> transitions = new HashSet<>();
		for (final S place : marking) {
			transitions.addAll(mOperand.getSuccessors(place));
		}
		return transitions;
	}

	@Override
	public Iterable<OutgoingCallTransition<L, S>> callSuccessors(final S state, final L letter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<OutgoingReturnTransition<L, S>> returnSuccessors(final S state, final S hier, final L letter) {
		// TODO Auto-generated method stub
		return null;
	}

}
