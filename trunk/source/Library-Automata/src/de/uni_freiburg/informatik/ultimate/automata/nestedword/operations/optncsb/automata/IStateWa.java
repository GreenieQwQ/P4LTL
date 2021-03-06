/*
 * Copyright (C) 2017 Yong Li (liyong@ios.ac.cn)
 * Copyright (C) 2015 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2009-2015 University of Freiburg
 *
 * This file is part of the ULTIMATE Automata Library.
 *
 * The ULTIMATE Automata Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE Automata Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Automata Library. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE Automata Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE Automata Library grant you additional permission
 * to convey the resulting work.
 */

package de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.optncsb.automata;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.optncsb.util.IntSet;



/**
 * State interface of word automata
 * */
public interface IStateWa extends IState {
		
	void addSuccessor(int letter, int state);
	
	IntSet getSuccessors(int letter);
	
	Set<Integer> getEnabledLetters();

	default void toBA(PrintStream printer, List<String> alphabet) {
		Set<Integer> enabledLetters = this.getEnabledLetters();
		for(Integer letter : enabledLetters) {
        	for(Integer succ : this.getSuccessors(letter).iterable()) {
        		printer.print(alphabet.get(letter) + ",[" + this.getId() + "]->[" + succ + "]\n");
        	}
		}
	}

	@Override
	default void toDot(PrintStream printer, List<String> alphabet) {
		Set<Integer> enabledLetters = this.getEnabledLetters();
		for(Integer letter : enabledLetters) {
    		for(Integer succ : this.getSuccessors(letter).iterable()) {
    			printer.print("  " + this.getId() + " -> " + succ + " [label=\"" + alphabet.get(letter) + "\"];\n");
    		}
        }
	}
}
