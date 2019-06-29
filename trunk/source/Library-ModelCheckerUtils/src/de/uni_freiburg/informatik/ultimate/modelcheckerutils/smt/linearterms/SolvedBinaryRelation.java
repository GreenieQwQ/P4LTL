/*
 * Copyright (C) 2019 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2019 University of Freiburg
 *
 * This file is part of the ULTIMATE ModelCheckerUtils Library.
 *
 * The ULTIMATE ModelCheckerUtils Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE ModelCheckerUtils Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE ModelCheckerUtils Library. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE ModelCheckerUtils Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE ModelCheckerUtils Library grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.linearterms;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.linearterms.BinaryRelation.RelationSymbol;

/**
 * Represents a binary relation that has been solved for a subject x. I.e. this
 * represents a formula of the form x ▷ φ, where x is a variable, φ is a term in
 * which x does not occur. Here, the binary relation symbol ▷ is an element of
 * the following list.
 * <p>
 * ▷ ∈ { =, !=, \<=, \<, \>=, \> }
 * </p>
 * Additionally, this class may provide a Boolean formula ψ and if such a
 * formula is provided the formula x ▷ φ holds only under the assumption that ψ
 * holds.
 *
 * @author Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 */
public class SolvedBinaryRelation {

	public enum AssumptionForSolvability {
		NONE, INTEGER_DIVISIBLE_BY_CONSTANT, REAL_DIVISOR_NOT_ZERO,
	}

	private final Term mLeftHandSide;
	private final Term mRightHandSide;
	private final RelationSymbol mRelationSymbol;
	private final Term mAdditionalAssumption;
	private final AssumptionForSolvability mAssumptionForSolability;

	public SolvedBinaryRelation(final Term leftHandSide, final Term rightHandSide, final RelationSymbol relationSymbol,
			final Term additionalAssumption, final AssumptionForSolvability assumptionForSolability) {
		super();
		if ((additionalAssumption == null) != (assumptionForSolability == AssumptionForSolvability.NONE)) {
			throw new IllegalArgumentException(
					"Term additionalAssumption must be null iff we not have an assumptionForSolability");
		}
		mLeftHandSide = leftHandSide;
		mRightHandSide = rightHandSide;
		mRelationSymbol = relationSymbol;
		mAdditionalAssumption = additionalAssumption;
		mAssumptionForSolability = assumptionForSolability;
	}

	/**
	 * @return The subject for which the relation is solved.
	 */
	public Term getLeftHandSide() {
		return mLeftHandSide;
	}

	/**
	 * @return {@link Term} that is in relation to subject.
	 */
	public Term getRightHandSide() {
		return mRightHandSide;
	}

	public RelationSymbol getRelationSymbol() {
		return mRelationSymbol;
	}

	/**
	 * @return Boolean {@link Term} that has to hold if this relation holds and null
	 *         if there is no such assumption.
	 */
	public Term getAdditionalAssumption() {
		return mAdditionalAssumption;
	}

	/**
	 * @return Additional assumption that was necessary in order to solve the
	 *         relation for subject x.
	 */
	public AssumptionForSolvability getAssumptionForSolability() {
		return mAssumptionForSolability;
	}

	/**
	 * @return This relation as SMT term. (Without the additional assumption.)
	 */
	public Term relationToTerm(final Script script) {
		return script.term(mRelationSymbol.toString(), mLeftHandSide, mRightHandSide);
	}

}