package de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.linearTerms.BinaryEqualityRelation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.linearTerms.BinaryRelation.NoRelationOfThisKindException;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.linearTerms.BinaryRelation.RelationSymbol;

/**
 * Wrapper for an equality (resp. not equals relation) of the form 
 * arr' = (store, arr, index, value), 
 * where 
 * the array arr' is a TermVariable, and
 * (store, arr, index, value) is a multidimensional store.
 * A boolean flag allows to put the requirenment that also arr is a 
 * TermVariable.
 * @author Matthias Heizmann
 */
public class ArrayUpdate {
	private final TermVariable m_NewArray;
	private final MultiDimensionalStore m_MultiDimensionalStore;
	private final Term m_ArrayUpdateTerm;
	
	/**
	 * Construct ArrayUpdate wrapper from term. Throw an ArrayUpdateException if
	 * term is no array update.
	 */
	public ArrayUpdate(Term term, boolean isNegated, 
			boolean oldArrayIsTermVariable) throws ArrayUpdateException {
		BinaryEqualityRelation ber = null;
		try {
			ber = new BinaryEqualityRelation(term);
		} catch (NoRelationOfThisKindException e) {
			throw new ArrayUpdateException(e.getMessage());
		}
		if (isNegated && ber.getRelationSymbol() != RelationSymbol.DISTINCT) {
			throw new ArrayUpdateException("no negated array update");
		}
		if (!isNegated && ber.getRelationSymbol() != RelationSymbol.EQ) {
			throw new ArrayUpdateException("no not negated array update");
		}
		m_ArrayUpdateTerm = term;
		Term lhs = ber.getLhs();
		Term rhs = ber.getRhs();
		ApplicationTerm allegedStoreTerm;
		if (isArrayTermVariable(lhs)) {
			if (isStoreTerm(rhs)) {
				m_NewArray = (TermVariable) lhs;
				allegedStoreTerm = (ApplicationTerm) rhs;
			} else {
				throw new ArrayUpdateException("no array update");
			}
		} else if (isArrayTermVariable(rhs)) {
			if (isStoreTerm(lhs)) {
				m_NewArray = (TermVariable) rhs;
				allegedStoreTerm = (ApplicationTerm) lhs;
			} else {
				throw new ArrayUpdateException("no array update");
			}
		} else {
			throw new ArrayUpdateException("no array update");
		}
		assert allegedStoreTerm.getFunction().getName().equals("store");
		assert allegedStoreTerm.getParameters().length == 3;
		assert m_NewArray.getSort() == allegedStoreTerm.getSort();
		
		m_MultiDimensionalStore = new MultiDimensionalStore(allegedStoreTerm);
		if (m_MultiDimensionalStore.getIndex().size() == 0) {
			throw new ArrayUpdateException("no multidimensional array");
		}
		if (!m_MultiDimensionalStore.getArray().getSort().equals(m_NewArray.getSort())) {
			throw new AssertionError("sort mismatch");
		}
		if (oldArrayIsTermVariable && 
				!(m_MultiDimensionalStore.getArray() instanceof TermVariable)) {
			throw new ArrayUpdateException("old array is no term variable");
			
		}
	}
	
	/**
	 * Returns true iff term is TermVariable and has array sort
	 */
	private boolean isArrayTermVariable(Term term) {
		if (term instanceof TermVariable) {
			if (term.getSort().isArraySort()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true iff term is ApplicationTerm whose function symbol is
	 * "store".
	 */
	private boolean isStoreTerm(Term term) {
		if (term instanceof ApplicationTerm) {
			ApplicationTerm appTerm = (ApplicationTerm) term;
			if (appTerm.getFunction().getName().equals("store")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If term is a term variable of Sort sort, return term as TermVariable,
	 * return null otherwise.
	 */
	TermVariable isArrayWithSort(Term term, Sort sort) {
		if (term instanceof TermVariable) {
			if (term.getSort().equals(sort)) {
				return (TermVariable) term;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	public Term getOldArray() {
		return m_MultiDimensionalStore.getArray();
	}
	public TermVariable getNewArray() {
		return m_NewArray;
	}
	public ArrayIndex getIndex() {
		return m_MultiDimensionalStore.getIndex();
	}
	public Term getValue() {
		return m_MultiDimensionalStore.getValue();
	}
	public Term getArrayUpdateTerm() {
		return m_ArrayUpdateTerm;
	}
	public MultiDimensionalStore getMultiDimensionalStore() {
		return m_MultiDimensionalStore;
	}
	
	@Override
	public String toString() {
		return m_ArrayUpdateTerm.toString();
	}
	
	
	public static class ArrayUpdateException extends Exception {

		private static final long serialVersionUID = -5344050289008681972L;

		public ArrayUpdateException(String message) {
			super(message);
		}
	}
	
	/**
	 * Given an array of terms, partition them into terms that are array updates
	 * and terms that are not array updates.
	 */
	public static class ArrayUpdateExtractor {
		private final Map<Term, Term> m_Store2TermVariable = 
				new HashMap<Term, Term>();
		private final List<ArrayUpdate> m_ArrayUpdates = 
				new ArrayList<ArrayUpdate>();
		private final List<Term> remainingTerms = 
				new ArrayList<Term>();
		
		/**
		 * If negatedUpdate is true we search for terms of the form
		 * (not (= a (store a' b c)))
		 */
		public ArrayUpdateExtractor(boolean negatedUpdate, 
				boolean oldArrayIsTermVariable, Term... terms) {
			for (Term term : terms) {
				ArrayUpdate au;
				try {
					au = new ArrayUpdate(term, negatedUpdate, oldArrayIsTermVariable);
				} catch (ArrayUpdateException e) {
					au = null;
				}
				if (au == null) {
					remainingTerms.add(term);
				} else {
					m_ArrayUpdates.add(au);
					m_Store2TermVariable.put(
							au.getMultiDimensionalStore().getStoreTerm(), 
							au.getNewArray());
				}
			}
		}

		public Map<Term, Term> getStore2TermVariable() {
			return m_Store2TermVariable;
		}

		public List<ArrayUpdate> getArrayUpdates() {
			return m_ArrayUpdates;
		}

		public List<Term> getRemainingTerms() {
			return remainingTerms;
		}
	}
}