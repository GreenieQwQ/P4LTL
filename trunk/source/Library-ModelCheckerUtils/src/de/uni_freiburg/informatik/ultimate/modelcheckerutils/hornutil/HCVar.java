/*
 * Copyright (C) 2016 Alexander Nutz (nutz@informatik.uni-freiburg.de)
 * Copyright (C) 2016 Mostafa M.A. (mostafa.amin93@gmail.com)
 * Copyright (C) 2016 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.modelcheckerutils.hornutil;

import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;


/**
 * A HCVar for a set of HornClausese is what an IProgramVar is for a program.
 * A HCVar consists of an uninterpreted predicate symbol that occurs in the HornClause set together
 * with a position n identifying the n-th variable of that predicate.
 * 
 * @author Alexander Nutz (nutz@informatik.uni-freiburg.de)
 *
 */
public class HCVar implements IProgramVar {

	private static final long serialVersionUID = 4653727851496150630L;

	private final HornClausePredicateSymbol mPredicateSymbol;
	private final TermVariable mTermVariable;
	private final int mIdx;
	
	private final ApplicationTerm mDefaultConstant;
	private final ApplicationTerm mPrimedConstant;

	private final String mGloballyUniqueId;
	
	public HCVar(HornClausePredicateSymbol pr, int pos, TermVariable v, 
			ApplicationTerm defaultConstant, ApplicationTerm primedConstant) {
		mPredicateSymbol = pr;
		mIdx = pos;
		mTermVariable = v;
		mDefaultConstant = defaultConstant;
		mPrimedConstant = primedConstant;
		mGloballyUniqueId = String.format("%s_%d", mPredicateSymbol.getName(), mIdx);
	}


	@Override
	public TermVariable getTermVariable() {
		return mTermVariable;
	}

	@Override
	public String toString() {
		return mPredicateSymbol.getName() + "{" + mIdx + "}" + ":" + mTermVariable.toString();
	}

	@Override
	public String getGloballyUniqueId() {
		return mGloballyUniqueId;
	}

	@Override
	public String getProcedure() {
		return null;
	}

	@Override
	public boolean isGlobal() {
		return true;
	}

	@Override
	public boolean isOldvar() {
		return false;
	}

	@Override
	public ApplicationTerm getDefaultConstant() {
		return mDefaultConstant;
	}

	@Override
	public ApplicationTerm getPrimedConstant() {
		return mPrimedConstant;
	}

	@Override
	public Term getTerm() {
		return mTermVariable;
	}

	@Override
	public int hashCode() {
		return mGloballyUniqueId.hashCode();
	}


	@Override
	public boolean equals(Object arg0) {
		if (this == arg0) {
			return true; // just for speedup
		}
		if (!(arg0 instanceof HCVar)) {
			return false;
		}
		HCVar otherHcVar = (HCVar) arg0;
		if (!mPredicateSymbol.equals(otherHcVar.mPredicateSymbol)) {
			return false;
		}
		if (mIdx != otherHcVar.mIdx) {
			return false;
		}
		return true;
	}
}
