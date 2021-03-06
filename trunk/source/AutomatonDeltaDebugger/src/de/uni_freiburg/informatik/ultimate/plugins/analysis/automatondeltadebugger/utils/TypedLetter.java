/*
 * Copyright (C) 2015-2016 Christian Schilling (schillic@informatik.uni-freiburg.de)
 * Copyright (C) 2015-2016 University of Freiburg
 * 
 * This file is part of the ULTIMATE Automaton Delta Debugger.
 * 
 * The ULTIMATE Automaton Delta Debugger is free software: you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * The ULTIMATE Automaton Delta Debugger is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Automaton Delta Debugger. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7: If you modify the
 * ULTIMATE Automaton Delta Debugger, or any covered work, by linking or
 * combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE Automaton Delta Debugger grant you additional
 * permission to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.analysis.automatondeltadebugger.utils;

/**
 * Wraps a letter together with its type (internal, call, return).
 * 
 * @author Christian Schilling (schillic@informatik.uni-freiburg.de)
 * @param <LETTER>
 *            letter type
 */
public final class TypedLetter<LETTER> {
	private final LETTER mLetter;
	private final LetterType mType;

	/**
	 * @param letter
	 *            Letter.
	 * @param type
	 *            letter type
	 */
	public TypedLetter(final LETTER letter, final LetterType type) {
		mLetter = letter;
		mType = type;
	}

	public LETTER getLetter() {
		return mLetter;
	}

	public LetterType getType() {
		return mType;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		// @formatter:off
		builder.append(mLetter)
				.append('(')
				.append(mType)
				.append(')');
		// @formatter:on
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return mLetter.hashCode() + mType.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final TypedLetter<?> other = (TypedLetter<?>) obj;
		return (other.mLetter.equals(mLetter)) && (other.mType == mType);
	}
}
