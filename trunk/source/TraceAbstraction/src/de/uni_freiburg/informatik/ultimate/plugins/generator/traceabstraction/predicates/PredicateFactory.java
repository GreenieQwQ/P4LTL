/*
 * Copyright (C) 2016 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2016 University of Freiburg
 * 
 * This file is part of the ULTIMATE TraceAbstraction plug-in.
 * 
 * The ULTIMATE TraceAbstraction plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE TraceAbstraction plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE TraceAbstraction plug-in. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE TraceAbstraction plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE TraceAbstraction plug-in grant you additional permission 
 * to convey the resulting work.
 */

package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Boogie2SmtSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.ModifiableGlobalVariableManager;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.BasicPredicateFactory;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.TermVarsProc;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.ProgramPoint;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.HoareAnnotation;
import de.uni_freiburg.informatik.ultimate.witnessparser.graph.WitnessNode;

public class PredicateFactory extends BasicPredicateFactory {

	public PredicateFactory(final IUltimateServiceProvider services, final ManagedScript mgdScript, final Boogie2SmtSymbolTable symbolTable, final SimplificationTechnique simplificationTechnique, final XnfConversionTechnique xnfConversionTechnique) {
		super(services, mgdScript, symbolTable, simplificationTechnique, xnfConversionTechnique);
	}
	
	public static HoareAnnotation getHoareAnnotation(final ProgramPoint programPoint) {
		return HoareAnnotation.getAnnotation(programPoint);
	}

	public PredicateWithHistory newPredicateWithHistory(final ProgramPoint pp, final Term term, final Map<Integer, Term> history) {
		final TermVarsProc tvp = constructTermVarsProc(term);
		final PredicateWithHistory pred = new PredicateWithHistory(pp, constructFreshSerialNumber(), tvp.getProcedures(), tvp.getFormula(), tvp.getVars(),
				tvp.getClosedFormula(), history);
		return pred;
	}

	public SPredicate newSPredicate(final ProgramPoint pp, final Term term) {
		final TermVarsProc termVarsProc = constructTermVarsProc(term);
		return newSPredicate(pp, termVarsProc);
	}
	
	SPredicate newSPredicate(final ProgramPoint pp, final TermVarsProc termVarsProc) {
		final SPredicate pred = new SPredicate(pp, constructFreshSerialNumber(), termVarsProc.getProcedures(), termVarsProc.getFormula(),
				termVarsProc.getVars(), termVarsProc.getClosedFormula());
		return pred;
	}
	
	public ISLPredicate newEmptyStackPredicate() {
		final ProgramPoint pp = new ProgramPoint("noCaller", "noCaller", false, null);
		return newSPredicate(pp, new TermVarsProc(mEmptyStackTerm, EMPTY_VARS, NO_PROCEDURE, mEmptyStackTerm));
	
	}

	public MLPredicate newMLPredicate(final ProgramPoint[] programPoints, final Term term) {
		final TermVarsProc termVarsProc = constructTermVarsProc(term);
		final MLPredicate predicate = new MLPredicate(programPoints, constructFreshSerialNumber(), termVarsProc.getProcedures(),
				termVarsProc.getFormula(), termVarsProc.getVars(), termVarsProc.getClosedFormula());
		return predicate;
	}
	
	public MLPredicate newMLDontCarePredicate(final ProgramPoint[] programPoints) {
		final TermVarsProc termVarsProc = constructTermVarsProc(mDontCareTerm);
		final MLPredicate predicate = new MLPredicate(programPoints, constructFreshSerialNumber(), termVarsProc.getProcedures(),
				termVarsProc.getFormula(), termVarsProc.getVars(), termVarsProc.getClosedFormula());
		return predicate;
	}
	
	public ProdState getNewProdState(final List<IPredicate> programPoints) {
		return new ProdState(constructFreshSerialNumber(), programPoints, mScript.term("true"),new HashSet<IProgramVar>(0));
	}
	
	public UnknownState newDontCarePredicate(final ProgramPoint pp) {
		final UnknownState pred = new UnknownState(pp, constructFreshSerialNumber(), mDontCareTerm);
		return pred;
	}

	public SPredicate newTrueSLPredicateWithWitnessNode(final ProgramPoint pp, final WitnessNode witnessNode, final Integer stutteringSteps) {
		final SPredicate pred = new SPredicateWithWitnessNode(pp, constructFreshSerialNumber(), NO_PROCEDURE, mScript.term("true"), EMPTY_VARS,
				mScript.term("true"), witnessNode, stutteringSteps);
		return pred;
	}

	public HoareAnnotation getNewHoareAnnotation(final ProgramPoint pp, final ModifiableGlobalVariableManager modifiableGlobals) {
		return new HoareAnnotation(pp, constructFreshSerialNumber(), mSymbolTable, this, modifiableGlobals, mMgdScript, mScript, mServices, mSimplificationTechnique, mXnfConversionTechnique);
	}




}
