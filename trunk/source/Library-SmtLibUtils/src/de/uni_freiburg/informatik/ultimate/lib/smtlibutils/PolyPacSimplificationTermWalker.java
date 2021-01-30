/*
 * Copyright (C) 2020 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2020 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.lib.smtlibutils;

import java.util.Arrays;
import java.util.List;

import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.TermContextTransformationEngine.DescendResult;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.TermContextTransformationEngine.TermWalker;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.polynomials.PolyPoNeUtils;
import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.FunctionSymbol;
import de.uni_freiburg.informatik.ultimate.logic.QuantifiedFormula;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;

public class PolyPacSimplificationTermWalker extends TermWalker<Term> {
	private final Script mScript;

	private static final boolean DEBUG_CHECK_RESULT = false;

	public PolyPacSimplificationTermWalker(final Script script) {
		super();
		mScript = script;
	}

	@Override
	Term constructContextForApplicationTerm(final Term context, final FunctionSymbol symb,
			final List<Term> allParams, final int selectedParam) {
		return Context.buildCriticalConstraintForConDis(mScript, context, symb, allParams, selectedParam);
	}

	@Override
	Term constructContextForQuantifiedFormula(final Term context, final int quant, final TermVariable[] vars) {
		return Context.buildCriticalContraintForQuantifiedFormula(mScript, context, Arrays.asList(vars));
	}

	@Override
	DescendResult convert(final Term context, final Term term) {
		if (term instanceof ApplicationTerm) {
			final ApplicationTerm appTerm = (ApplicationTerm) term;
			if (appTerm.getFunction().getName().equals("and") || appTerm.getFunction().getName().equals("or")) {
				return new TermContextTransformationEngine.IntermediateResultForDescend(term);
			}
		} else if (term instanceof QuantifiedFormula) {
			return new TermContextTransformationEngine.IntermediateResultForDescend(term);
		}
		return new TermContextTransformationEngine.FinalResultForAscend<Term>(term);
	}

	@Override
	Term constructResultForApplicationTerm(final Term context, final ApplicationTerm originalApplicationTerm,
			final Term[] resultParams) {
		if (originalApplicationTerm.getFunction().getName().equals("and")) {
			return PolyPoNeUtils.and(mScript, context, Arrays.asList(resultParams));
		}
		if (originalApplicationTerm.getFunction().getName().equals("or")) {
			return PolyPoNeUtils.or(mScript, context, Arrays.asList(resultParams));
		}
		throw new AssertionError();
	}

	public static Term simplify(final Script script, final Term term) {
		final Term result = TermContextTransformationEngine.transform(new PolyPacSimplificationTermWalker(script),
				script.term("true"), term);
		if (DEBUG_CHECK_RESULT) {
			final boolean tolerateUnknown = true;
			SmtUtils.checkLogicalEquivalenceForDebugging(script, result, term, PolyPoNeUtils.class,
					tolerateUnknown);
		}
		return result;
	}

	@Override
	Term constructResultForQuantifiedFormula(final Term context, final QuantifiedFormula originalQuantifiedFormula,
			final Term resultSubformula) {
		return SmtUtils.quantifier(mScript, originalQuantifiedFormula.getQuantifier(),
				Arrays.asList(originalQuantifiedFormula.getVariables()), resultSubformula);
	}

	@Override
	boolean applyRepeatedlyUntilNoChange() {
		return false;
	}

}
