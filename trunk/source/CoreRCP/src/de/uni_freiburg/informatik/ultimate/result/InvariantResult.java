package de.uni_freiburg.informatik.ultimate.result;

import java.util.List;

import de.uni_freiburg.informatik.ultimate.model.IElement;
import de.uni_freiburg.informatik.ultimate.model.ITranslator;

/**
 * Report an invariant that holds at ELEM which is a node in an Ultimate model.
 * The invariant is given as an expression of type E.
 * @author Matthias Heizmann
 */
public class InvariantResult<ELEM extends IElement, E> 
		extends AbstractResultAtElement<ELEM> implements IResultWithLocation {
	
	private final E m_Invariant;
	
	public InvariantResult(String plugin, ELEM element, 
			List<ITranslator<?,?,?,?>> translatorSequence, E invariant) {
		super(element, plugin, translatorSequence);
		this.m_Invariant = invariant;
	}
	
	public E getInvariant() {
		return m_Invariant;
	}

	@Override
	public String getShortDescription() {
		return "Loop Invariant";
	}

	@Override
	public String getLongDescription() {
		StringBuffer sb = new StringBuffer();
		sb.append("Derived loop invariant: ");
		sb.append(ResultUtil.backtranslationWorkaround(
				m_TranslatorSequence, m_Invariant));
		return sb.toString();
	}
}
