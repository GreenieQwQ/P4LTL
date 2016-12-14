/*
 * Copyright (C) 2016 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2016 University of Freiburg
 *
 * This file is part of the ULTIMATE AbstractInterpretationV2 plug-in.
 *
 * The ULTIMATE AbstractInterpretationV2 plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE AbstractInterpretationV2 plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE AbstractInterpretationV2 plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE AbstractInterpretationV2 plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE AbstractInterpretationV2 plug-in grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.abstractinterpretation.model.IAbstractPostOperator;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.AnnotatedTerm;
import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.QuantifiedFormula;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.TransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVarOrConst;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.ApplicationTermFinder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.arrays.ArrayEquality;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.arrays.ArrayEquality.ArrayEqualityExtractor;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.arrays.ArrayUpdate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.arrays.ArrayUpdate.ArrayUpdateExtractor;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.arrays.MultiDimensionalStore;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.normalForms.Nnf;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.normalForms.Nnf.QuantifierHandling;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Call;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Return;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Summary;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;

/**
 *
 * @author Yu-Wen Chen (yuwenchen1105@gmail.com)
 *
 */
public class VPPostOperator implements IAbstractPostOperator<VPState, CodeBlock, IProgramVar> {

	private final ManagedScript mScript;
	private final IUltimateServiceProvider mServices;

	private final VPDomain mDomain;
	private final VPStateFactory mStateFactory;
	private final VPDomainPreanalysis mPreAnalysis;
	private final VPTransFormulaStateBuilderPreparer mTfPreparer;
	private final VpTfStateFactory mTfStateFactory;

	public VPPostOperator(final ManagedScript script, final IUltimateServiceProvider services, final VPDomain domain) {
		mScript = script;
		mServices = services;
		mDomain = domain;
		mStateFactory = mDomain.getVpStateFactory();
		mPreAnalysis = mDomain.getPreAnalysis();
		mTfPreparer = mDomain.getTfPreparer();
		mTfStateFactory = mDomain.getTfStateFactory();
	}

	@Override
	public List<VPState> apply(final VPState oldState, final CodeBlock transition) {

		final UnmodifiableTransFormula tf = transition.getTransitionFormula();
		if (tf.getOutVars().isEmpty()) {
			return Collections.singletonList(oldState);
		}

		if (oldState.isBottom()) {
			return Collections.singletonList(oldState);
		}

		VPTfState tfPreState = mTfStateFactory.createTfState(oldState, tf);


		final Term nnfTerm = new Nnf(mScript, mServices, QuantifierHandling.CRASH).transform(tf.getFormula());
		mDomain.getLogger().debug("Original term: " + tf.getFormula());
		mDomain.getLogger().debug("Nnf term:      " + nnfTerm);


		final Map<TermVariable, IProgramVar> tvToPvMap = VPDomainHelpers.computeProgramVarMappingFromTransFormula(tf);

		final Set<VPTfState> resultTfStates = handleTransition(tfPreState, nnfTerm, tf, false);

		mDomain.getLogger().debug("states after transition " + transition + ": " + resultTfStates);

		Set<VPState> resultStates = mStateFactory.convertToStates(resultTfStates, tf);

		assert VPDomainHelpers.containsNoNullElement(resultStates);
		assert VPDomainHelpers.allStatesHaveSameVariables(resultStates);
		assert resultStates.size() < 50 : "TODO: merge internally";
		return new ArrayList<>(resultStates);
	}

	private Set<VPTfState> handleTransition(
			final VPTfState tfPreState, 
			final Term term, 
			final TransFormula tf,
			boolean negated) {

		if (term instanceof ApplicationTerm) {

			final ApplicationTerm appTerm = (ApplicationTerm) term;
			final String applicationName = appTerm.getFunction().getName();

			if (applicationName == "and") {
				assert !negated : "we transformed to nnf before, right?";

			final List<Set<VPTfState>> andList = new ArrayList<>();
			for (final Term conjunct : appTerm.getParameters()) {
				andList.add(handleTransition(tfPreState, conjunct, tf, false));
			}

			Set<VPTfState> resultStates =
					mTfStateFactory.conjoinAll((Set<VPTfState>[]) andList.toArray(new Set<?>[andList.size()]));
			assert !resultStates.isEmpty();
//			assert VPDomainHelpers.allStatesHaveSameVariables(resultStates);
			return resultStates;
			} else if (applicationName == "or") {
				assert !negated : "we transformed to nnf before, right?";

				final Set<VPTfState> orList = new HashSet<VPTfState>();
				for (final Term t : appTerm.getParameters()) {
					orList.addAll(handleTransition(tfPreState, term, tf, false));
				}
				assert !orList.isEmpty();
//				assert VPDomainHelpers.allStatesHaveSameVariables(orList);
				return orList;
			} else if (applicationName == "=") {
				/*
				 * case "ArrayEquality"
				 */
				{
					final List<ArrayEquality> aeqs =
							new ArrayEqualityExtractor(new Term[] { appTerm }).getArrayEqualities();
					if (!aeqs.isEmpty()) {
						assert aeqs.size() == 1 : "?";
						if (!mDomain.getPreAnalysis().isArrayTracked(
								aeqs.get(0).getLhs(), tf.getInVars(), tf.getOutVars())
								|| !mDomain.getPreAnalysis().isArrayTracked(
										aeqs.get(0).getRhs(), tf.getInVars(), tf.getOutVars())) {
							return Collections.singleton(tfPreState);
						}

						// we have an array equality (i.e. something like (= a b) where a,b are arrays)
						Set<VPTfState> result = handleArrayEqualityTransition(
										tfPreState,
										tf,
										aeqs.get(0),
										negated);
						assert !result.isEmpty();
//						assert VPDomainHelpers.allStatesHaveSameVariables(result);
						return result;
					}
				}

				/*
				 * case "ArrayUpdate"
				 */
				{
					final List<ArrayUpdate> aus = new ArrayUpdateExtractor(false, false, appTerm).getArrayUpdates();
					if (!aus.isEmpty()) {
						assert aus.size() == 1 : "?";
						if (!mDomain.getPreAnalysis().isArrayTracked(
								aus.get(0).getNewArray(), tf.getInVars(), tf.getOutVars())
								|| !mDomain.getPreAnalysis().isArrayTracked(
										aus.get(0).getOldArray(), tf.getInVars(), tf.getOutVars())){
							return Collections.singleton(tfPreState);
						}


						// we have an array update
						Set<VPTfState> result = handleArrayUpdateTransition(
										tfPreState, 
										tf,
										aus.get(0),
										negated);
						assert !result.isEmpty();
//						assert VPDomainHelpers.allStatesHaveSameVariables(result);
						return result;
					}
				}

				/*
				 * case "two terms we track are equated"
				 */
				Set<VPTfState> resultStates = handleBasicEquality(
						tfPreState, tf, appTerm, negated);
//				assert VPDomainHelpers.allStatesHaveSameVariables(resultStates);
				return resultStates;
			} else if (applicationName == "not") {
				assert !negated : "we transformed to nnf before, right?";
			Set<VPTfState> result = handleTransition(
					tfPreState, appTerm.getParameters()[0], tf, !negated);
			assert !result.isEmpty();
//			assert VPDomainHelpers.allStatesHaveSameVariables(result);
			return result;
			} else if (applicationName == "distinct") {

				mScript.lock(this);
				final Term equality =
						mScript.term(this, "=", appTerm.getParameters()[0], appTerm.getParameters()[1]);
				mScript.unlock(this);

				Set<VPTfState> result = handleTransition(tfPreState, equality, tf, !negated);
				assert !result.isEmpty();
//				assert VPDomainHelpers.allStatesHaveSameVariables(result);
				return result;
			} else if (applicationName == "true") {
				if (!negated) {
					return Collections.singleton(tfPreState);
				} else {
					VPTfState result = mTfStateFactory.getBottomState();
					return Collections.singleton(result);
				}
			} else if (applicationName == "false") {
				if (!negated) {
					VPTfState result = mTfStateFactory.getBottomState();
					return Collections.singleton(result);
				} else {
					return Collections.singleton(tfPreState);
				}
			} else {
				/*
				 * we don't handle this function 
				 * --> what does this mean?
				 * guesses:
				 *  - always safe: return top state
				 *  - finer: if no outVars occur, then the prestate can be returned safely, right?
				 */
//				VPState result = mStateFactory.havocVariables(assignedVars, prestate);
				assert false : "TODO: implement";
				return Collections.singleton(mTfStateFactory.getTopState());
			}

		} else if (term instanceof QuantifiedFormula) {
			// return handleTransition(preState, ((QuantifiedFormula) term).getSubformula(), tvToPvMap);
			assert false : "we currently cannot deal with quantifiers";
		return null;
		} else if (term instanceof AnnotatedTerm) {
			Set<VPTfState> result = handleTransition(
					tfPreState, 
					((AnnotatedTerm) term).getSubterm(), 
					tf, 
					negated);
			assert !result.isEmpty();
			return result;
		}

		assert false : "missed a case?";
		return Collections.singleton(tfPreState);
	}

	private Set<VPTfState> handleBasicEquality(VPTfState tfPreState, TransFormula tf, ApplicationTerm appTerm,
			boolean negated) {
		// TODO Auto-generated method stub
		return null;
	}

		private Set<VPTfState> handleArrayUpdateTransition(VPTfState tfPreState, TransFormula tf, ArrayUpdate arrayUpdate,
			boolean negated) {
		// TODO Auto-generated method stub
		return null;
	}

		private Set<VPTfState> handleArrayEqualityTransition(VPTfState tfPreState, TransFormula tf, ArrayEquality arrayEquality,
			boolean negated) {
		// TODO Auto-generated method stub
		return null;
	}


		private List<VPState> handleBasicEquality(final VPState prestate, final Map<TermVariable, IProgramVar> tvToPvMap,
				Set<IProgramVar> assignedVars, Map<IProgramVar, TermVariable> inVars,
				Map<IProgramVar, TermVariable> outVars, final boolean negated, final ApplicationTerm appTerm) {

			VPState resultState = prestate;

			Term param1 = appTerm.getParameters()[0];
			Term param2 = appTerm.getParameters()[1];

			EqNode nodeParam1 = mPreAnalysis.getEqNode(param1, tvToPvMap);
			EqNode nodeParam2 = mPreAnalysis.getEqNode(param2, tvToPvMap);

			/*
			 * If there's node that is not being tracked, return pre-state.
			 */
			if (nodeParam1 == null || nodeParam2 == null) {
				return Collections.singletonList(prestate);
			}

			boolean isParam1InVar = isXVar(inVars, param1);
			boolean isParam1OutVar = isXVar(outVars, param1);
			boolean isParam2InVar = isXVar(inVars, param2);
			boolean isParam2OutVar = isXVar(outVars, param2);


			// havocs before equation

			if (!isParam1InVar) {
				resultState = mStateFactory.havoc(nodeParam1, resultState);
			}
			if (!isParam2InVar) {
				resultState = mStateFactory.havoc(nodeParam2, resultState);
			}

			Set<VPState> resultStates = new HashSet<>();
			resultStates.add(resultState);


			// the equation

			if (isParam1InVar) {
				resultStates = addXQualitiesForInVar(prestate, negated, nodeParam1, nodeParam2, resultStates);
			}
			if (isParam1InVar) {
				resultStates = addXQualitiesForInVar(prestate, negated, nodeParam2, nodeParam1, resultStates);
			}


			// havocs after equation

			if (!isParam1OutVar) {
				resultStates = mStateFactory.havoc(nodeParam1, resultStates);
			}
			if (!isParam2OutVar) {
				resultStates = mStateFactory.havoc(nodeParam2, resultStates);
			}



			return new ArrayList<>(resultStates);


			//		for (Entry<IProgramVar, TermVariable> outPvAndTv : outVars.entrySet()) {
			//			/*
			//			 * plan : for each outVar we establish what we now about it after applying the equality
			//			 */
			//			
			//			
			//			EqNode nodeForOutPv = mPreAnalysis.getEqNode(outPvAndTv.getKey().getTerm(), Collections.emptyMap());
			//			
			//			if (outPvAndTv.getValue() != appTerm.getParameters()[0]
			//					&& outPvAndTv.getValue() != appTerm.getParameters()[1]) {
			//				// variable is inside 
			//				continue;
			//			}
			//			
			//			if (nodeForOutPv == null) {
			//				// we don't track the current pv --> do nothing
			//				continue;
			//			}
			//
			//			if (!Arrays.asList(appTerm.getFreeVars()).contains(outPvAndTv.getValue())) {
			//				// the termVariable of the current pv does not occur in the formula
			//				// --> the current pv is unconstrained in the new state
			//				resultStates = mVpStateFactory.havoc(nodeForOutPv, resultStates);
			//				continue;
			//			}
			//			
			//			// the current out pv occurs in the equation
			//			// -->
			//			
			//			Term otherSideofEquation = appTerm.getParameters()[0] == outPvAndTv.getValue() 
			//					? appTerm.getParameters()[1] 
			//							: appTerm.getParameters()[0];
			//					
			//			// the other side is either an inVar or a constant
			//			EqNode nodeForOtherSide = mPreAnalysis.getEqNode(otherSideofEquation, tvToPvMap);
			//			
			//			if (nodeForOtherSide == null) {
			//				// we don't track the other side of the equation's term --> we know nothing about it ..
			//				resultStates = mVpStateFactory.havoc(nodeForOutPv, resultStates);// implementation as fallback
			//				continue;
			//			}
			//
			//
			//			if (otherSideofEquation instanceof TermVariable
			//					&& !inVars.containsValue(otherSideofEquation)) {
			//				// equation between two outvars where at least one is not an invar -- seems odd..
			//				// but means that in the new states both are equal -- so we just add the equality..
			//				if (assignedVars.contains(nodeForOutPv)) {
			//					resultStates = mVpStateFactory.havoc(nodeForOutPv, resultStates);
			//				}
			//				resultStates = mVpStateFactory.addEquality(nodeForOutPv, nodeForOtherSide, resultStates);
			//				continue;
			//			}
			//			
			//			
			//			
			//			if (!inVars.containsValue(otherSideofEquation)) {
			//				if (assignedVars.contains(nodeForOutPv)) {
			//					resultStates = mVpStateFactory.havoc(nodeForOutPv, resultStates);
			//				}
			//				// the other side must be a constant --> we don't need to take the prestate into account
			//				if (!negated) {
			//					resultStates = mVpStateFactory.addEquality(nodeForOutPv, nodeForOtherSide, resultStates);
			//				} else {
			//					resultStates = mVpStateFactory.addDisEquality(nodeForOutPv, nodeForOtherSide, resultStates);
			//				}
			//				continue;
			//			}
			//			
			//			// the other side of the equation is a TermVariable that is an inVar 
			//			// --> we need to extract all information about it from the prestate and update the current pv in the new state accordingly
			//
			//			if (assignedVars.contains(nodeForOutPv)) {
			//				resultStates = mVpStateFactory.havoc(nodeForOutPv, resultStates);
			//			}
			//
			//			
			//			Set<EqNode> nodesEqualToOtherSideInVar = collectEqualitiesForNodeInState(nodeForOtherSide, prestate);
			//			for (EqNode equalNode : nodesEqualToOtherSideInVar) {
			//				if (!negated) {
			//					// tf says: nodeForOutPv == nodeForOtherSide
			//					// prestate says: nodeForOhterSide == equalNode
			//					// --> nodeForOutPv == equalNode
			//					resultStates = mVpStateFactory.addEquality(nodeForOutPv, equalNode, resultStates);
			//				} else {
			//					// tf says: nodeForOutPv != nodeForOtherSide
			//					// prestate says: nodeForOhterSide == equalNode
			//					// --> nodeForOutPv != equalNode
			//					resultStates = mVpStateFactory.addDisEquality(nodeForOutPv, equalNode, resultStates);
			//				}
			//			}
			//			
			//			Set<EqNode> nodesUnequalToOtherSideInVar = collectDisEqualitiesForNodeInState(nodeForOtherSide, prestate);
			//			for (EqNode unequalNode : nodesUnequalToOtherSideInVar) {
			//				if (!negated) {
			//					// tf says: nodeForOutPv == nodeForOtherSide
			//					// prestate says: nodeForOhterSide != unequalNode
			//					// --> nodeForOutPv != equalNode
			//					resultStates = mVpStateFactory.addDisEquality(nodeForOutPv, unequalNode, resultStates);
			//				} else {
			//					// tf says: nodeForOutPv != nodeForOtherSide
			//					// prestate says: nodeForOhterSide != unequalNode
			//					// --> do nothing
			//				}
			//			}
			//
			//		}
			//		return new ArrayList<>(resultStates);
		}

		private Set<VPState> addXQualitiesForInVar(final VPState prestate, final boolean negated,
				EqNode nodeParam1, EqNode nodeParam2, Set<VPState> resultStates) {
			Set<EqNode> nodesEqualToParam1InVar = collectEqualitiesForNodeInState(nodeParam1, prestate);
			for (EqNode equalNode : nodesEqualToParam1InVar) {
				if (!negated) {
					// tf says: nodeForOutPv == nodeForOtherSide
					// prestate says: nodeForOhterSide == equalNode
					// --> nodeForOutPv == equalNode
					resultStates = mStateFactory.addEquality(nodeParam2, equalNode, resultStates);
				} else {
					// tf says: nodeForOutPv != nodeForOtherSide
					// prestate says: nodeForOhterSide == equalNode
					// --> nodeForOutPv != equalNode
					resultStates = mStateFactory.addDisEquality(nodeParam2, equalNode, resultStates);
				}
			}

			Set<EqNode> nodesUnequalToOtherSideInVar = collectDisEqualitiesForNodeInState(nodeParam1, prestate);
			for (EqNode unequalNode : nodesUnequalToOtherSideInVar) {
				if (!negated) {
					// tf says: nodeForOutPv == nodeForOtherSide
					// prestate says: nodeForOhterSide != unequalNode
					// --> nodeForOutPv != equalNode
					resultStates = mStateFactory.addDisEquality(nodeParam2, unequalNode, resultStates);
				} else {
					// tf says: nodeForOutPv != nodeForOtherSide
					// prestate says: nodeForOhterSide != unequalNode
					// --> do nothing
				}
			}
			return resultStates;
		}


		private boolean isXVar(Map<IProgramVar, TermVariable> inVars, Term param1) {
			boolean result = false;
			for (TermVariable fv : param1.getFreeVars()) {
				result |= inVars.values().contains(fv);
			}
			return result;
		}

		private Set<EqNode> collectDisEqualitiesForNodeInState(EqNode nodeForOtherSide, VPState prestate) {
			return prestate.getUnequalNodes(nodeForOtherSide);
		}

		private Set<EqNode> collectEqualitiesForNodeInState(EqNode nodeForOtherSide, VPState prestate) {
			Set<EqNode> result = new HashSet<>();
			EqGraphNode graphNode = prestate.getEqNodeToEqGraphNodeMap().get(nodeForOtherSide);
			result.add(graphNode.getRepresentative().eqNode);
			for (EqGraphNode rr : graphNode.getReverseRepresentative()) {
				result.add(rr.eqNode);
			}
			return result;
		}

		private Set<VPState> handleArrayEqualityTransition(
				final VPState preState,
				final Map<TermVariable, IProgramVar> tvToPvMap, 
				Set<IProgramVar> assignedVars, 
				final Map<IProgramVar, TermVariable> inVars, 
				final Map<IProgramVar, TermVariable> outVars, 
				final boolean negated, 
				final ArrayEquality aeq) {
			final Term array1Term = aeq.getLhs();
			final Term array2Term = aeq.getRhs();
			final IProgramVarOrConst array1 =
					mDomain.getPreAnalysis().getIProgramVarOrConstOrLiteral(array1Term, tvToPvMap);
			final IProgramVarOrConst array2 =
					mDomain.getPreAnalysis().getIProgramVarOrConstOrLiteral(array2Term, tvToPvMap);

			VPState resultState = preState;

			if (assignedVars.contains(array1)) {
				resultState = mStateFactory.havocArray(array1, resultState);
			} 
			if (assignedVars.contains(array2)) {
				resultState = mStateFactory.havocArray(array1, resultState);
			}

			if (!negated) {
				Set<VPState> resultStates = mDomain.getVpStateFactory().arrayEquality(array1, array2, resultState);
				assert !resultStates.isEmpty();
				return resultStates;
			} else {
				/*
				 * something of the form a != b where a and b are arrays
				 *  --> we cannot derive anything from this because we only track a finite number
				 *     of positions in each (infinite) array
				 */
				return Collections.singleton(resultState);
			}
		}

		private Set<VPState> handleArrayUpdateTransition(
				final VPState preState,
				final Map<TermVariable, IProgramVar> tvToPvMap, 
				final Set<IProgramVar> assignedVars, 
				final Map<IProgramVar, TermVariable> inVars, 
				final Map<IProgramVar, TermVariable> outVars, 
				final boolean negated, final ArrayUpdate au) {
			final MultiDimensionalStore mdStore = au.getMultiDimensionalStore();
			final TermVariable newArrayTv = au.getNewArray();
			final Term oldArrayTerm = au.getOldArray();
			final Term value = au.getValue();

			final IProgramVarOrConst newArrayId =
					mDomain.getPreAnalysis().getIProgramVarOrConstOrLiteral(newArrayTv, tvToPvMap);
			final IProgramVarOrConst oldArrayId =
					mDomain.getPreAnalysis().getIProgramVarOrConstOrLiteral(oldArrayTerm, tvToPvMap);

			VPState havoccedPreState = preState;
			if (assignedVars.contains(newArrayId)) {
				havoccedPreState = mStateFactory.havocArray(newArrayId, havoccedPreState);
			} 
			if (assignedVars.contains(oldArrayId)) {
				havoccedPreState = mStateFactory.havocArray(oldArrayId, havoccedPreState);
			}

			if (newArrayId.equals(oldArrayId)) {
				assert newArrayId == oldArrayId : "right?..";
				/*
				 * we essentially have something of the form a[i_1, ..,i_n] := v; in terms of Boogie --> we only havoc the
				 * EqNode belonging to a[i_1, ..,i_n] and add an equality to v
				 */

				// TODO: strictly speaking we have to check here, that newArrayId is an outVar, oldArrayId an inVar

				final EqNode arrayAtIndexNode = mDomain.getPreAnalysis().getEqNode(mdStore.getStoreTerm(), tvToPvMap);
				final List<EqNode> indexNodeList = new ArrayList<>();
				for (Term index : mdStore.getIndex()) {
					indexNodeList.add(mDomain.getPreAnalysis().getEqNode(index, tvToPvMap));
				}
				final EqNode valueNode = mDomain.getPreAnalysis().getEqNode(value, tvToPvMap);

				final Set<EqNode> arrayAtIndexNodeCongruentce = new HashSet<>();

				if (!negated) {
					havoccedPreState = mDomain.getVpStateFactory().copy(havoccedPreState).build();

					/*
					 * Set of fnNodes to be handled (3 cases)
					 */
					Set<EqFunctionNode> fnNodes = mDomain.getArrayIdToEqFnNodeMap().getImage(oldArrayId);

					/*
					 * Case 1: for nodes that are not congruence with arrayAtIndexNode 
					 * 			-> copy the information into new state
					 * 			(which is being done by the copy() method called few lines before)
					 */
					for (final VPDomainSymmetricPair<EqNode> pair : havoccedPreState.getDisEqualitySet()) {
						if (pair.contains(arrayAtIndexNode)) {
							if (pair.getOther(arrayAtIndexNode) instanceof EqFunctionNode) {
								final EqFunctionNode fNode = (EqFunctionNode)pair.getOther(arrayAtIndexNode);
								if (fNode.getFunction().equals(oldArrayId)) {
									fnNodes.remove(fNode);
								}
							}
						} 
					}

					boolean isContinue;
					for (EqFunctionNode fnNode : fnNodes) {
						isContinue = true;
						/*
						 * Case 2: fnNode that are congruence with arrayAtIndexNode 
						 * 			-> havoc and set to valueNode (will be set later)
						 */
						assert arrayAtIndexNode instanceof EqFunctionNode;
						if (havoccedPreState.congruentIgnoreFunctionSymbol((EqFunctionNode)arrayAtIndexNode, fnNode)) {
							havoccedPreState = mDomain.getVpStateFactory().havoc(fnNode, havoccedPreState);
							arrayAtIndexNodeCongruentce.add(fnNode);
						} else {

							/*
							 * To catch some remaining nodes for case 1 
							 * 	-> check each argument, if at least one argument is not equal, do nothing.
							 */
							for (int i = 0; i < indexNodeList.size(); i++) {
								for (final VPDomainSymmetricPair<EqNode> pair : havoccedPreState.getDisEqualitySet()) {
									if (pair.contains(((EqFunctionNode)arrayAtIndexNode).getArgs().get(i))) {
										if (pair.getOther(((EqFunctionNode)arrayAtIndexNode).getArgs().get(i))
												.equals(fnNode.getArgs().get(i))) {
											isContinue = false;
										}
									} 
								}
							}

							/*
							 * Case 3: for those nodes that we are not sure if it's congruence with arrayAtIndexNode or not, 
							 * 			-> havoc
							 */
							if (isContinue) {
								havoccedPreState = mDomain.getVpStateFactory().havoc(fnNode, havoccedPreState);
							}
						}
					}

					final Set<VPState> resultStates = new HashSet<>();
					resultStates.addAll(mDomain.getVpStateFactory().addEquality(arrayAtIndexNode, valueNode, havoccedPreState));
					for (EqNode congruentNode : arrayAtIndexNodeCongruentce) {
						// TODO restore these node's information first?
						resultStates.addAll(mDomain.getVpStateFactory().addEquality(congruentNode, valueNode, havoccedPreState));
					}
					return resultStates;
				} else {
					final Set<VPState> resultStates = new HashSet<>();
					resultStates.addAll(mDomain.getVpStateFactory().addDisEquality(arrayAtIndexNode, valueNode, havoccedPreState));
					return resultStates;
				}
			} else {
				/*
				 * we have something of the form b := a[(i_1, ..,i_n) := v] in terms of Boogie -->
				 */

				if (!negated) {
					final EqNode arrayAtIndexNode = mDomain.getPreAnalysis().getEqNode(mdStore.getStoreTerm(), tvToPvMap);
					final EqNode valueNode = mDomain.getPreAnalysis().getEqNode(value, tvToPvMap);

					if (arrayAtIndexNode == null || valueNode == null) {
						return Collections.singleton(havoccedPreState);
					}

					/*
					 * treat essentially like an ArrayEquality (except for that one point)
					 * for all points p except (i_1, .., i_n), we add 
					 *   b[p] = a[p] to the state
					 * and we add b[i_1, ..., i_n] = v
					 */
					final VPState resultState = mDomain.getVpStateFactory().havocArray(newArrayId, havoccedPreState);
					final Set<VPState> resultStates = new HashSet<>();

					for (EqFunctionNode oldArrayNode : mDomain.getArrayIdToEqFnNodeMap().getImage(oldArrayId)) { 
						if (!oldArrayNode.equals(arrayAtIndexNode)) {
							for (EqFunctionNode newArrayNode : mDomain.getArrayIdToEqFnNodeMap().getImage(newArrayId)) {
								if (resultState.congruentIgnoreFunctionSymbol(oldArrayNode, newArrayNode)) {
									resultStates.addAll(mDomain.getVpStateFactory().addEquality(oldArrayNode, newArrayNode, resultState));
								}
							}
						} else {
							for (EqFunctionNode newArrayNode : mDomain.getArrayIdToEqFnNodeMap().getImage(newArrayId)) {
								if (resultState.congruentIgnoreFunctionSymbol(oldArrayNode, newArrayNode)) {
									resultStates.addAll(mDomain.getVpStateFactory().addEquality(newArrayNode, valueNode, resultState));
								}
							}
						}
					}

					return resultStates;
				} else {
					/*
					 * see the "negated" case in handleArrayEquality for an explanation
					 */
					return Collections.singleton(havoccedPreState);
				}
			}
		}

		@Override
		public List<VPState> apply(final VPState stateBeforeLeaving, final VPState stateAfterLeaving,
				final CodeBlock transition) {

			if (transition instanceof Call) {
				List<VPState> result = applyContextSwitch(stateBeforeLeaving, stateAfterLeaving, ((Call) transition).getLocalVarsAssignment());
				assert VPDomainHelpers.containsNoNullElement(result);
				assert VPDomainHelpers.allStatesHaveSameVariables(result);
				return result;
			} else if (transition instanceof Return) {
				List<VPState> result = applyContextSwitch(stateBeforeLeaving, stateAfterLeaving, ((Return) transition).getAssignmentOfReturn());
				assert VPDomainHelpers.containsNoNullElement(result);
				assert VPDomainHelpers.allStatesHaveSameVariables(result);
				return result;
			} else if (transition instanceof Summary) {
				List<VPState> result = applyContextSwitch(stateBeforeLeaving, stateAfterLeaving, ((Summary) transition).getTransformula());
				assert VPDomainHelpers.containsNoNullElement(result);
				assert VPDomainHelpers.allStatesHaveSameVariables(result);
				return result;
			} else {
				assert false : "unexpected..";
			}

			// List<VPState> conjoined = mDomain.getVpStateFactory().conjoin(stateBeforeLeaving, stateAfterLeaving);

			// TODO

			// return apply(conjoined, transition);
			assert false;
			return null;
		}

		private List<VPState> applyReturn(VPState stateBeforeLeaving, VPState stateAfterLeaving, Return transition) {
			transition.getAssignmentOfReturn();
			transition.getLocalVarsAssignmentOfCall();

			// TODO Auto-generated method stub
			return null;
		}

		private List<VPState> applyContextSwitch(VPState stateBeforeLeaving, VPState stateAfterLeaving, TransFormula variableAssignment) {
			/*
			 * Plan:
			 *   say x1.. xn are the parameters in the call  (sth like call x:= foo(x1, ... xn) )
			 *  - from stateBeforeLeaving extract all relations (equality/disequality) of x1 .. xn to global variables
			 *  (- background: stateAfterLeaving already has the relations that the globals have between each other patched into)
			 *  - the resulting state(s) is obtained by adding all the extracted (dis)equalities to stateAfterLeaving according
			 *    to the matching of parameters 
			 */

			if (stateAfterLeaving.isBottom()) {
				return Collections.singletonList(stateAfterLeaving);
			}

			//		Set<VPState> resultStates = new HashSet<>();
			VPState copy = mDomain.getVpStateFactory().copy(stateAfterLeaving).build();
			//        resultStates.add(copy);

			VPState resultState = copy;

			Set<ApplicationTerm> equations = 
					new ApplicationTermFinder("=", true)
					.findMatchingSubterms(
							variableAssignment.getFormula());
			for (ApplicationTerm eq : equations) {
				assert eq.getParameters().length == 2;
				/*
				 * Convention:
				 * say we have a call
				 *  call x := f(x1, .., xn)
				 * and a procedure declaration
				 *  procedure f(p1 ... pn)
				 * Then x1...xn are called "call parameters"
				 *  and p1...pn are called "function parameters".
				 */
				Term callParam = eq.getParameters()[0];
				Term funcParam = eq.getParameters()[1];

				Set<Pair<EqNode, EqNode>> equalitiesWithGlobals = 
						extractEqualitiesWithGlobals(callParam, funcParam, stateBeforeLeaving, variableAssignment, mDomain);
				for (Pair<EqNode, EqNode> pair : equalitiesWithGlobals) {
					//				resultStates.addAll(
					//						mDomain.getVpStateFactory().addEquality(
					//								pair.getFirst(), pair.getSecond(), resultStates));

					// TODO: fallback, think through
					Set<VPState> newStates = mDomain.getVpStateFactory().addEquality(
							pair.getFirst(), pair.getSecond(), resultState);
					resultState = mDomain.getVpStateFactory().disjoinAll(newStates);
				}
				Set<Pair<EqNode, EqNode>> disequalitiesWithGlobals = 
						extractDisequalitiesWithGlobals(callParam, funcParam, stateBeforeLeaving, variableAssignment, mDomain);
				for (Pair<EqNode, EqNode> pair : disequalitiesWithGlobals) {
					//				resultStates.addAll(
					//						mDomain.getVpStateFactory().addDisEquality(
					//								pair.getFirst(), pair.getSecond(), resultStates));
					// TODO: fallback, think through
					Set<VPState> newStates = mDomain.getVpStateFactory().addDisEquality(
							pair.getFirst(), pair.getSecond(), resultState);
					resultState = mDomain.getVpStateFactory().disjoinAll(newStates);

				}

			}

			//		assert VPDomainHelpers.containsNoNullElement(resultStates);
			//		return new ArrayList<>(resultStates);
			return Collections.singletonList(resultState);
		}

		private Set<Pair<EqNode, EqNode>> extractDisequalitiesWithGlobals(
				Term callParam, 
				Term funcParam, 
				VPState stateBeforeLeaving, 
				TransFormula varAssignment,
				VPDomain domain) {

			return extractDisequalitiesWithProperty(
					callParam, funcParam, stateBeforeLeaving, varAssignment, domain, 
					node -> node.isGlobal());
		}


		public static Set<Pair<EqNode, EqNode>> extractDisequalitiesWithProperty(
				Term callParam, 
				Term funcParam, 
				VPState stateBeforeLeaving, 
				TransFormula varAssignment,
				VPDomain domain,
				Predicate<EqNode> property) {
			Map<TermVariable, IProgramVar> tvToPvMap = 
					VPDomainHelpers.computeProgramVarMappingFromTransFormula(varAssignment);

			EqNode callParamNode = domain.getPreAnalysis().getEqNode(callParam, tvToPvMap);
			EqNode funcParamNode = domain.getPreAnalysis().getEqNode(funcParam, tvToPvMap);

			//TODO: do this more efficiently if necessary..
			Set<EqNode> unequalGlobals = 
					stateBeforeLeaving.getUnequalNodes(callParamNode).stream()
					.filter(property)
					.collect(Collectors.toSet());


			Set<Pair<EqNode, EqNode>> result = new HashSet<>();
			for (EqNode unequalGlobal : unequalGlobals) {
				result.add(new Pair<>(funcParamNode, unequalGlobal));
			}

			return result;
		}


		private Set<Pair<EqNode, EqNode>> extractEqualitiesWithGlobals(
				Term callParam, 
				Term funcParam, 
				VPState stateBeforeLeaving, 
				TransFormula varAssignment,
				VPDomain domain) {
			return extractEqualitiesWithProperty(
					callParam, funcParam, stateBeforeLeaving, varAssignment, domain, 
					node -> node.isGlobal());
		}

		/**
		 * 
		 * @param callParam the Term in the stateBeforeLeaving, i.e., we are looking for globals that this term is equal with
		 * @param funcParam the Term in the stateAfterLeaving, i.e., our resulting pair will equate this term with some global variables
		 * @param stateBeforeLeaving
		 * @param call 
		 * @return
		 */
		public static Set<Pair<EqNode, EqNode>> extractEqualitiesWithProperty(
				Term callParam, 
				Term funcParam, 
				VPState stateBeforeLeaving, 
				TransFormula varAssignment,
				VPDomain domain,
				Predicate<EqNode> property) {
			Map<TermVariable, IProgramVar> tvToPvMap = 
					VPDomainHelpers.computeProgramVarMappingFromTransFormula(varAssignment);

			EqNode callParamNode = domain.getPreAnalysis().getEqNode(callParam, tvToPvMap);
			EqNode funcParamNode = domain.getPreAnalysis().getEqNode(funcParam, tvToPvMap);

			//TODO: do this more efficiently if necessary..
			Set<EqNode> equivalentGlobals = 
					stateBeforeLeaving.getEquivalentEqNodes(callParamNode).stream()
					.filter(property)
					.collect(Collectors.toSet());

			Set<Pair<EqNode, EqNode>> result = new HashSet<>();
			for (EqNode equivalentGlobal : equivalentGlobals) {
				result.add(new Pair<>(funcParamNode, equivalentGlobal));
			}

			return result;
		}

	}
