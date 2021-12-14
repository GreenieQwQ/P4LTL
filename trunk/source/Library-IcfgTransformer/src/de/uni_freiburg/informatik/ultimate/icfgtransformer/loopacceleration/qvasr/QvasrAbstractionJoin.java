/*
 * Copyright (C) 2021 Jonas Werner (wernerj@informatik.uni-freiburg.de)
 * Copyright (C) 2021 University of Freiburg
 *
 * This file is part of the ULTIMATE IcfgTransformer library.
 *
 * The ULTIMATE IcfgTransformer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE IcfgTransformer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE IcfgTransformer library. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE IcfgTransformer library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE IcfgTransformer grant you additional permission
 * to convey the resulting work.
 */

package de.uni_freiburg.informatik.ultimate.icfgtransformer.loopacceleration.qvasr;

import java.util.HashSet;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.logic.Rational;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;

/**
 * Class for computing the join of two Qvasr abstractions to form a least upper bound.
 *
 * @author Jonas Werner (wernerj@informatik.uni-freiburg.de)
 *
 */
public class QvasrAbstractionJoin {
	private QvasrAbstractionJoin() {
		// Prevent instantiation of this utility class
	}

	public static void qvasrAbstractionJoin(final QvasrAbstraction abstractionOne,
			final QvasrAbstraction abstractionTwo) {

	}

	/**
	 * Get coherence classes of a given Qvasr abstraction. A coherence class is a set of rows i,j, where r_i = r_j in
	 * the reset vector of the abstraction's qvasr.
	 *
	 * @param qvasrAbstraction
	 * @return
	 */
	private static Set<Set<Integer>> getCoherenceClasses(final QvasrAbstraction qvasrAbstraction) {
		final Qvasr qvasr = qvasrAbstraction.getQvasr();
		final Set<Set<Integer>> coherenceClasses = new HashSet<>();
		for (final Pair<Rational[], Rational[]> transformer : qvasr.getQvasrTransformer()) {
			final Rational[] resetVector = transformer.getFirst();
			for (int i = 0; i < resetVector.length; i++) {
				final Set<Integer> coherenceClass = new HashSet<>();
				coherenceClass.add(i);
				for (int j = 0; j < resetVector.length; j++) {
					if (i != j && resetVector[i] == resetVector[j]) {
						coherenceClass.add(j);
					}
				}
				coherenceClasses.add(coherenceClass);
			}
		}
		return coherenceClasses;
	}

}
