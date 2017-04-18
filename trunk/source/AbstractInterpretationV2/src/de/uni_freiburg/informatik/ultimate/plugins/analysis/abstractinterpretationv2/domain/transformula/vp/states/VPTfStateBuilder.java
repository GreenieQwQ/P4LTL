/*
 * Copyright (C) 2016 Yu-Wen Chen
 * Copyright (C) 2016 Alexander Nutz (nutz@informatik.uni-freiburg.de)
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
package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.states;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.TransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVarOrConst;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.VPDomainHelpers;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.VPDomainPreanalysis;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.VPDomainSymmetricPair;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.VPTransFormulaStateBuilderPreparer;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.elements.EqGraphNode;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.elements.IArrayWrapper;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.elements.IElementWrapper;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.elements.VPTfArrayIdentifier;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.transformula.vp.elements.VPTfNodeIdentifier;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.HashRelation;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.NestedMap3;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;

/**
 * 
 * @author Alexander Nutz (nutz@informatik.uni-freiburg.de)
 *
 */
public class VPTfStateBuilder extends IVPStateOrTfStateBuilder<VPTfState, VPTfNodeIdentifier, VPTfArrayIdentifier> {
	
	
	private final Set<IProgramVarOrConst> mInVars;
	private final Set<IProgramVarOrConst> mOutVars;

	private final Set<VPTfNodeIdentifier> mAllNodeIds;

	private final Map<VPTfNodeIdentifier, EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier>> mNodeIdToEqGraphNode;

	private final HashRelation<VPTfArrayIdentifier, VPTfNodeIdentifier> mArrayIdToFunctionNodes = new HashRelation<>();

	private final TransFormula mTransFormula;

	private final VPTransFormulaStateBuilderPreparer mTfStatePreparer;

	private final NestedMap3<IProgramVarOrConst, 
		Pair<IProgramVar, TermVariable>, 
		Pair<IProgramVar, TermVariable>, VPTfArrayIdentifier> mPvocToInVarToOutVarToArrayIdentifier =
			new NestedMap3<>();

	private final VPDomainPreanalysis mPreAnalysis;

	private final Map<Term, IArrayWrapper> mTermToArrayWrapper;
	private final Map<Term, IElementWrapper> mTermToElementWrapper;

	
	public VPTfStateBuilder(VPDomainPreanalysis preAnalysis, VPTransFormulaStateBuilderPreparer tfStatePreparer, 
			TransFormula transFormula, 
			Set<IProgramVarOrConst> inVars, Set<IProgramVarOrConst> outVars, 
			Set<VPTfNodeIdentifier> allNodeIds, 
			Map<VPTfNodeIdentifier, EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier>> nodeIdToEqGraphNode, 
			Map<Term, IArrayWrapper> termToArrayWrapper, 
			Map<Term, IElementWrapper> termToElementWrapper, 
			Set<VPDomainSymmetricPair<VPTfNodeIdentifier>>  initialDisequalities) {
		super(initialDisequalities);
		mPreAnalysis = preAnalysis;
		mTfStatePreparer = tfStatePreparer;
		mTransFormula = transFormula;

		mInVars = Collections.unmodifiableSet(inVars);
		mOutVars = Collections.unmodifiableSet(outVars);

		mAllNodeIds = Collections.unmodifiableSet(allNodeIds);
		mNodeIdToEqGraphNode = Collections.unmodifiableMap(nodeIdToEqGraphNode);
		
		mTermToArrayWrapper = Collections.unmodifiableMap(termToArrayWrapper);
		mTermToElementWrapper = Collections.unmodifiableMap(termToElementWrapper);
	}

	/**
	 * Copy constructor.
	 *
	 * @param builder
	 */
	public VPTfStateBuilder(final VPTfStateBuilder builder) {
		super(builder);
		assert builder.isTopConsistent();
		mPreAnalysis = builder.mPreAnalysis;
		mTfStatePreparer = builder.mTfStatePreparer;
		mTransFormula = builder.mTransFormula;
		mInVars = builder.mInVars;
		mOutVars = builder.mOutVars;
		
		mTermToElementWrapper = builder.mTermToElementWrapper;
		mTermToArrayWrapper = builder.mTermToArrayWrapper;

//		// the nodeIdentifiers are shared between all "sibling" builders (i.e. builders for the same TransFormula)
		mAllNodeIds = builder.mAllNodeIds;

		// nodes need to be deepcopied..
		mNodeIdToEqGraphNode = new HashMap<>();
		for (final Entry<VPTfNodeIdentifier, EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier>> en : 
				builder.mNodeIdToEqGraphNode.entrySet()) {
			final EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier> egnInOldState = en.getValue();
			final VPTfNodeIdentifier nodeId = en.getKey();
			final EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier> newGraphNode = new EqGraphNode<>(nodeId);
			assert newGraphNode != null;
			mNodeIdToEqGraphNode.put(nodeId, newGraphNode);
			assert !builder.mIsTop || newGraphNode.getRepresentative() == newGraphNode;
		}

		for (final Entry<VPTfNodeIdentifier, EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier>> en : 
				builder.mNodeIdToEqGraphNode.entrySet()) {
			final EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier> egnInOldState = en.getValue();
			final VPTfNodeIdentifier nodeId = en.getKey();
			final EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier> newGraphNode = getEqGraphNode(nodeId);
			EqGraphNode.copyFields(egnInOldState, newGraphNode, this);

			newGraphNode.setupNode();
		}

		assert isTopConsistent();
	}


	@Override
	public VPTfState build() {
		assert mTransFormula != null;
		assert mNodeIdToEqGraphNode != null;
		assert mArrayIdToFunctionNodes != null;
		assert mDisEqualitySet != null;
		assert mInVars != null;
		assert mOutVars != null;
		assert isTopConsistent();
		assert VPDomainHelpers.disEqualitySetContainsOnlyRepresentatives(mDisEqualitySet, this);

		return new VPTfState(mTransFormula, this, mNodeIdToEqGraphNode, mAllNodeIds, mArrayIdToFunctionNodes, 
				mDisEqualitySet, mIsTop, mInVars, mOutVars);
	}

	@Override
	public EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier> getEqGraphNode(final VPTfNodeIdentifier i) {
		return mNodeIdToEqGraphNode.get(i);
	}

	@Override
	Collection<EqGraphNode<VPTfNodeIdentifier, VPTfArrayIdentifier>> getAllEqGraphNodes() {
		return mNodeIdToEqGraphNode.values();
	}

	public VPTfArrayIdentifier getOrConstructArrayIdentifier(final Term term) {
		return getOrConstructArrayIdentifier(
				mPreAnalysis.getIProgramVarOrConstOrLiteral(term,
						VPDomainHelpers.computeProgramVarMappingFromTransFormula(mTransFormula)),
				VPDomainHelpers.projectToTerm(mTransFormula.getInVars(), term),
				VPDomainHelpers.projectToTerm(mTransFormula.getOutVars(), term));
	}

	/**
	 *
	 * @param function
	 * @param inVars
	 * @param outVars
	 * @return
	 *
	 */
	public VPTfArrayIdentifier getOrConstructArrayIdentifier(final IProgramVarOrConst function,
			final Map<IProgramVar, TermVariable> inVars, final Map<IProgramVar, TermVariable> outVars) {
		Pair<IProgramVar, TermVariable> inVar = null;
		Pair<IProgramVar, TermVariable> outVar = null;

		final TermVariable iTv = inVars.get(function);
		if (iTv != null) {
			inVar = new Pair<>((IProgramVar) function, iTv);
		}
		final TermVariable oTv = outVars.get(function);
		if (oTv != null) {
			outVar = new Pair<>((IProgramVar) function, oTv);
		}
		VPTfArrayIdentifier result = mPvocToInVarToOutVarToArrayIdentifier.get(function, inVar, outVar);

		if (result == null) {
			result = new VPTfArrayIdentifier(function, inVar, outVar);
			mPvocToInVarToOutVarToArrayIdentifier.put(function, inVar, outVar, result);
		}
		return result;
	}

	public TransFormula getTransFormula() {
		return mTransFormula;
	}

	/**
	 *
	 * @param term
	 * @return null means the array is not tracked
	 */
	IArrayWrapper getArrayWrapper(final Term term) {
		return mTermToArrayWrapper.get(term);
	}

	/**
	 *
	 * @param term
	 * @return null means the array is not tracked
	 */
	IElementWrapper getElementWrapper(final Term term) {
		return  mTermToElementWrapper.get(term);
	}
	
	Set<IProgramVarOrConst> getInVariables() {
		return mInVars;
	}

	Set<IProgramVarOrConst> getOutVariables() {
		return mOutVars;
	}

}