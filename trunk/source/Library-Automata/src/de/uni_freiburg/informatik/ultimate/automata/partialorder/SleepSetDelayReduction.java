package de.uni_freiburg.informatik.ultimate.automata.partialorder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INwaOutgoingLetterAndTransitionProvider;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.UnaryNwaOperation;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IStateFactory;

public class SleepSetDelayReduction<L, S> extends UnaryNwaOperation<L, S, IStateFactory<S>>{
	
	private final INwaOutgoingLetterAndTransitionProvider<L, S> mOperand;
	private final S mStartState;
	private final HashMap<S, Set<L>> mHashMap;
	private final HashMap<S, Set<L>> mSleepSetMap;
	private final HashMap<S, Set<Set<L>>> mDelaySetMap;
	private final Stack<S> mStack;
	private final ISleepSetOrder<S, L> mOrder;
	private final NestedRun<L, S> mAcceptingRun;
	
	public SleepSetDelayReduction(final INwaOutgoingLetterAndTransitionProvider<L, S> operand,
			final S startState,
			final IIndependenceRelation<S, L> independenceRelation,
			final ISleepSetOrder<S, L> sleepSetOrder,
			final AutomataLibraryServices services) {
		super(services);
		mOperand = operand;
		assert operand.getVpAlphabet().getCallAlphabet().isEmpty();
		assert operand.getVpAlphabet().getReturnAlphabet().isEmpty();
		mHashMap = new HashMap<S, Set<L>>();
		mSleepSetMap = new HashMap<S, Set<L>>();
		mSleepSetMap.put(startState, Collections.<L>emptySet());
		mDelaySetMap = new HashMap<S, Set<Set<L>>>();
		mDelaySetMap.put(startState, Collections.<Set<L>>emptySet());
		mStack = new Stack<S>();
		mStartState = startState;
		mStack.push(startState);
		mOrder = sleepSetOrder;
		
		mAcceptingRun = getAcceptingRun();
		
	}

	private NestedRun<L,S> getAcceptingRun(){
		
		// TODO Insert pseudo code here
		S currentState = mStack.firstElement();
		ArrayList<OutgoingInternalTransition<L, S>> successorTransitionList = new ArrayList<OutgoingInternalTransition<L, S>>();
		Set<L> currentSleepSet = mSleepSetMap.get(currentState);
		Set<Set<L>> currentDelaySet = mDelaySetMap.get(currentState);
		
		if (mHashMap.get(currentState) == null){
			mHashMap.put(currentState, mSleepSetMap.get(currentState));
			for (OutgoingInternalTransition<L, S> transition : mOperand.internalSuccessors(currentState)) {
				if (currentSleepSet.contains(transition.getLetter()) == false) {
					successorTransitionList.add(transition);
				}	
			}
		}
		
		else {
			Set<L> currentHash = mHashMap.get(currentState);
			for (L letter : currentHash) {
				if (currentSleepSet.contains(letter) == false) {
					//following for loop is always executed exactly once, but needed to avoid a type mismatch
					for (OutgoingInternalTransition<L, S> transition : mOperand.internalSuccessors(currentState, letter)) {
						successorTransitionList.add(transition);
					}
				}
			}
			for (L letter : currentSleepSet) {
				if (currentHash.contains(letter) == false) {
					currentSleepSet.remove(letter);
				}
			}
			mSleepSetMap.put(currentState, currentSleepSet);
			mHashMap.put(currentState, currentSleepSet);
		}
		//sort successorTransitionList according to the given order
		Comparator<L> order = mOrder.getOrder(currentState);
		for (OutgoingInternalTransition<L, S> transition : successorTransitionList) {
			//do stuff
		}
		mStack.pop();
		if (currentDelaySet.isEmpty() == false) {
			for (Set<L> sleepSet : currentDelaySet) {
				if (sleepSet.equals(currentSleepSet)) {
					currentDelaySet.remove(sleepSet);
				}
			}
			mDelaySetMap.put(currentState, currentDelaySet);
			mStack.push(currentState);
			getAcceptingRun();
		}
		return null;
	}
	
	@Override
	public Boolean getResult() {
		// TODO Auto-generated method stub
		return mAcceptingRun == null;
	}

	@Override
	protected INwaOutgoingLetterAndTransitionProvider<L, S> getOperand() {
		// TODO Auto-generated method stub
		return null;
	}

}
