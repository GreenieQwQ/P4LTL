package de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.smt.linearTerms;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Rational;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.logic.Util;


/**
 * This represents an affine term in the form of
 * <pre>Σ c_i * x_i + c,</pre>
 * where c_i, c are rational constants
 * and x_i are variables. The variables x_i can be either TermVariables or
 * array read expressions.
 * 
 * Note that this call extends Term but is not supported by the solver.
 * We extend Term only that this can be returned by a TermTransformer.
 * 
 * Note that there is a class
 * de.uni_freiburg.informatik.ultimate.smtinterpol.convert.AffineTerm
 * which is similar but unusable in this case because it is closely
 * interweaved with the SMTLIB interiors.
 * 
 * @author Matthias Heizmann, Jan Leike
 */
public class AffineTerm extends Term {
	/**
	 * All summands that contain a variable.
	 */
	private final Map<Term, Rational> m_Variable2Coefficient;
	
	/**
	 * Affine constant.
	 */
	private final Rational m_Constant;
	
	/**
	 * Sort of this term. E.g, "Int" or "Real".
	 */
	private final Sort m_Sort;
	
	/**
	 * AffineTerm that represents the Rational r of sort sort. 
	 */
	public AffineTerm(Sort s, Rational r) {
		super(0);
		m_Sort = s;
		m_Constant = r;
		m_Variable2Coefficient = Collections.emptyMap();
	}
	
	/**
	 * AffineTerm that consists of the single variable tv.
	 */
	public AffineTerm(TermVariable tv) {
		super(0);
		m_Sort = tv.getSort();
		m_Constant = Rational.ZERO;
		m_Variable2Coefficient = Collections.singletonMap((Term) tv, Rational.ONE);
	}
	
	/**
	 * AffineTerm that consists of the single variable which is an array read
	 * expression.
	 */
	public AffineTerm(ApplicationTerm appTerm) {
		super(0);
		m_Sort = appTerm.getSort();
		String funName = appTerm.getFunction().getName();
		if (funName.equals("select") || appTerm.getParameters().length == 0) {
			m_Variable2Coefficient = Collections.singletonMap((Term) appTerm, Rational.ONE);
			m_Constant = Rational.ZERO;
		} else {
			throw new UnsupportedOperationException(
					"This application term may not be variable in AffineTerm: " + funName);
		}
	}
	
	/**
	 * AffineTerm that represents the sum of affineTerms.
	 */
	public AffineTerm(AffineTerm... affineTerms) {
		super(0);
		m_Sort = affineTerms[0].getSort();
		m_Variable2Coefficient = new HashMap<Term, Rational>();
		Rational constant = Rational.ZERO;
		for (AffineTerm affineTerm : affineTerms) {
			for (Entry<Term, Rational> summand  : affineTerm.m_Variable2Coefficient.entrySet()) {
				assert summand.getKey().getSort() == m_Sort;
				Rational coeff = m_Variable2Coefficient.get(summand.getKey());
				if (coeff == null) {
					m_Variable2Coefficient.put(summand.getKey(), summand.getValue());
				} else {
					Rational newCoeff = coeff.add(summand.getValue());
					m_Variable2Coefficient.put(summand.getKey(), newCoeff);
				}
			}
			constant = constant.add(affineTerm.m_Constant);
		}
		m_Constant = constant;
	}

	/**
	 * AffineTerm that represents the product of affineTerm and multiplier.
	 */
	public AffineTerm(AffineTerm affineTerm, Rational multiplier) {
		super(0);
		m_Variable2Coefficient = new HashMap<Term, Rational>();
		m_Constant = affineTerm.m_Constant.mul(multiplier);
		m_Sort = affineTerm.getSort();
		for (Entry<Term, Rational> summand  : affineTerm.m_Variable2Coefficient.entrySet()) {
			m_Variable2Coefficient.put(summand.getKey(), summand.getValue().mul(multiplier));
		}
	}

	/**
	 * Auxiliary affine term that represents an error during the translation
	 * process, e.g., if original term was not linear.
	 */
	public AffineTerm() {
		super(0);
		m_Variable2Coefficient = null;
		m_Constant = null;
		m_Sort = null;
	}
	
	/**
	 * True if this represents not an affine term but an error during the 
	 * translation process, e.g., if original term was not linear.
	 */
	public boolean isErrorTerm() {
		if (m_Variable2Coefficient == null) {
			assert m_Constant == null;
			assert m_Sort == null;
			return true;
		} else {
			assert m_Constant != null;
			assert m_Sort != null;
			return false;
		}
	}
	

	/**
	 * @return true iff the affine term is just a constant
	 */
	public boolean isConstant() {
		return m_Variable2Coefficient.isEmpty();
	}
	
	/**
	 * @return the constant summand of this affine term
	 */
	public Rational getConstant() {
		return m_Constant;
	}
	
	/**
	 * @return unmodifiable map where each variable is mapped to its coefficient. 
	 */
	public Map<Term, Rational> getVariable2Coefficient() {
		return Collections.unmodifiableMap(m_Variable2Coefficient);
	}
	
	/**
	 * Transforms this AffineTerm into a Term that is supported by the solver.
	 * @param script Script for that this term is constructed.
	 */
	public Term toTerm(Script script) {
		Term[] summands = new Term[m_Variable2Coefficient.size()+1];
		int i=0;
		for (Entry<Term, Rational> entry : m_Variable2Coefficient.entrySet()) {
			assert entry.getValue().isIntegral();
			Term coeff = script.numeral(entry.getValue().numerator());
			summands[i] = script.term("*", coeff, entry.getKey());
		}
		Term result = Util.sum(script, summands);
		return result;
	}
	
	@Override
	public String toString() {
		if (isErrorTerm()) {
			return "auxilliaryErrorTerm";
		}
		String result = "";
		for (Map.Entry<Term, Rational> entry : m_Variable2Coefficient.entrySet()) {
			result += entry.getValue().isNegative() ? " - " : " + ";
			result += entry.getValue().abs() + "*" + entry.getKey();
		}
		if (!m_Constant.equals(Rational.ZERO) || result.isEmpty()) {
			if (m_Constant.isNegative() || !result.isEmpty()) {
				result += m_Constant.isNegative() ? " - " : " + ";
			}
			result += m_Constant.abs();
		}
		if (result.charAt(0) == ' ') {
			result = result.substring(1); // Drop first space
		}
		
		return result;
	}

	@Override
	public Sort getSort() {
		return m_Sort;
	}

	@Override
	public void toStringHelper(ArrayDeque<Object> m_Todo) {
		throw new UnsupportedOperationException(
				"This is an auxilliary Term and not supported by the solver");
	}
	
}
