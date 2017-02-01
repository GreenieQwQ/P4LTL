/*
 * Copyright (C) 2017 Julian Loeffler (loefflju@informatik.uni-freiburg.de)
 * Copyright (C) 2017 University of Freiburg
 *
 * This file is part of the ULTIMATE SpaceExParser plug-in.
 *
 * The ULTIMATE SpaceExParser plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE SpaceExParser plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE SpaceExParser plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE SpaceExParser plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE SpaceExParser plug-in grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.spaceex.icfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.core.model.models.IPayload;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.BasicIcfg;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.TransFormulaBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula.Infeasibility;
import de.uni_freiburg.informatik.ultimate.plugins.spaceex.automata.hybridsystem.HybridAutomaton;
import de.uni_freiburg.informatik.ultimate.plugins.spaceex.automata.hybridsystem.Location;
import de.uni_freiburg.informatik.ultimate.plugins.spaceex.automata.hybridsystem.Transition;
import de.uni_freiburg.informatik.ultimate.plugins.spaceex.icfg.HybridTermBuilder.BuildScenario;
import de.uni_freiburg.informatik.ultimate.plugins.spaceex.parser.preferences.SpaceExPreferenceGroup;
import de.uni_freiburg.informatik.ultimate.plugins.spaceex.parser.preferences.SpaceExPreferenceManager;

/**
 * Class that handles conversion of Hybrid Models/Systems/Automata to an ICFG
 *
 * @author Julian Loeffler (loefflju@informatik.uni-freiburg.de)
 *
 */
public class HybridIcfgGenerator {
	
	private final ILogger mLogger;
	private final SpaceExPreferenceManager mSpaceExPreferenceManager;
	private final CfgSmtToolkit mSmtToolkit;
	private final Map<String, HybridCfgComponent> mCfgComponents;
	private final IPayload mPayload;
	private final String mProcedure = "MAIN";
	private final HybridVariableManager mVariableManager;
	private final IcfgLocation mErrorLocation;
	private final IcfgLocation mRootLocation;
	private final List<IcfgLocation> mConnectionList;
	private final Map<IcfgLocation, List<IcfgLocation>> mCreatedTransitions;
	private final Scenario mScenario;
	
	private enum Scenario {
		PREF_GROUPS, NO_GROUPS
	}
	
	public HybridIcfgGenerator(final ILogger logger, final SpaceExPreferenceManager preferenceManager,
			final CfgSmtToolkit smtToolkit, final HybridVariableManager variableManager) {
		mLogger = logger;
		mSpaceExPreferenceManager = preferenceManager;
		mSmtToolkit = smtToolkit;
		mVariableManager = variableManager;
		mPayload = null;
		mCfgComponents = new HashMap<>();
		mConnectionList = new ArrayList<>();
		mCreatedTransitions = new HashMap<>();
		mScenario = (preferenceManager.hasPreferenceGroups()) ? Scenario.PREF_GROUPS : Scenario.NO_GROUPS;
		// create a root and error location
		mErrorLocation = new IcfgLocation("error", mProcedure);
		mRootLocation = new IcfgLocation("root", mProcedure);
	}
	
	public BasicIcfg<IcfgLocation> createIfcgFromComponents(HybridAutomaton automaton) {
		// If scenario is that we have preferencegroups, get the parallel compositions of the groups.
		if (mScenario == Scenario.PREF_GROUPS) {
			final Map<Integer, HybridAutomaton> parallelCompositions =
					mSpaceExPreferenceManager.getGroupIdToParallelComposition();
			parallelCompositions.forEach((groupid, aut) -> {
				modelToIcfg(aut, groupid);
			});
		} else {
			modelToIcfg(automaton, 0);
		}
		
		final BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("icfg", mSmtToolkit, IcfgLocation.class);
		
		// root, initial state
		icfg.addLocation(mRootLocation, true, false, true, false, false);
		
		// error, error state
		icfg.addLocation(mErrorLocation, false, true, false, true, false);
		
		// push the remaining locations into the icfg
		mCfgComponents.forEach((id, comp) -> {
			// start is proc_entry + end is proc_exit
			icfg.addOrdinaryLocation(comp.getStart());
			icfg.addOrdinaryLocation(comp.getEnd());
			for (final IcfgLocation loc : comp.getLocations()) {
				icfg.addOrdinaryLocation(loc);
			}
		});
		
		// debug stuff
		if (mLogger.isDebugEnabled()) {
			mLogger.debug("################# COMPONENTS ###################");
			mCfgComponents.forEach((key, value) -> {
				mLogger.debug("ID:" + key + ", Component:" + value.toString());
			});
			mLogger.debug("################# ICFG ###################");
			mLogger.debug(icfg.getProgramPoints().toString());
			mLogger.debug(icfg.getSymboltable().getLocals("MAIN").toString());
		}
		return icfg;
	}
	
	public void modelToIcfg(final HybridAutomaton aut, int groupid) {
		/*
		 * in order to convert the hybrid model to an ICFG, we have to convert the parallelComposition of the regarded
		 * system.
		 */
		if (aut == null) {
			throw new IllegalStateException("HybridAutomaton aut has not been assigned and is null");
		} else {
			// convert automaton to cfg components
			automatonToIcfg(aut, groupid);
		}
	}
	
	private void automatonToIcfg(final HybridAutomaton automaton, int groupid) {
		final Location initialLocation = automaton.getInitialLocation();
		final Map<Integer, Location> locations = automaton.getLocations();
		final List<Transition> transitions = automaton.getTransitions();
		// we can merge all variables into one set.
		final Set<String> variables = automaton.getGlobalParameters();
		variables.addAll(automaton.getGlobalConstants());
		variables.addAll(automaton.getLocalConstants());
		variables.addAll(automaton.getLocalParameters());
		// ICFG locations + edges for variables
		variablesToIcfg(variables, groupid);
		// for locations
		locationsToIcfg(locations);
		// for transitions
		transitionsToIcfg(transitions, initialLocation);
	}
	
	/*
	 * variable methods
	 */
	private void variablesToIcfg(final Set<String> variables, int groupid) {
		
		final Script script = mSmtToolkit.getManagedScript().getScript();
		SpaceExPreferenceGroup group;
		if (mSpaceExPreferenceManager.getPreferenceGroups().containsKey(groupid)) {
			group = mSpaceExPreferenceManager.getPreferenceGroups().get(groupid);
		} else {
			group = null;
		}
		
		// get initial values of the variable
		final TransFormulaBuilder tfb = new TransFormulaBuilder(Collections.emptyMap(), Collections.emptyMap(), true,
				Collections.emptySet(), true, Collections.emptyList(), true);
		for (final String var : variables) {
			final HybridProgramVar progVar = mVariableManager.getVar2ProgramVar().get(var);
			final TermVariable inVar = mVariableManager.getVar2InVarTermVariable().get(var);
			final TermVariable outVar = mVariableManager.getVar2OutVarTermVariable().get(var);
			tfb.addInVar(progVar, inVar);
			tfb.addOutVar(progVar, outVar);
		}
		UnmodifiableTransFormula transformula;
		final String infix = (group == null) ? "" : group.getInitialVariableInfix();
		if (infix.isEmpty()) {
			transformula = TransFormulaBuilder.getTrivialTransFormula(mSmtToolkit.getManagedScript());
		} else {
			final HybridTermBuilder tb = new HybridTermBuilder(mVariableManager, script);
			final Term term = tb.infixToTerm(infix, BuildScenario.INITIALLY);
			mLogger.info(term);
			tfb.setFormula(term);
			tfb.setInfeasibility(Infeasibility.NOT_DETERMINED);
			// finish construction of the transformula.
			transformula = tfb.finishConstruction(mSmtToolkit.getManagedScript());
		}
		mLogger.debug("Transformula for varAssignment: " + transformula);
		// create variable component of the form start ----variable assignment----> end
		final List<IcfgLocation> locations = new ArrayList<>();
		final List<IcfgInternalTransition> transitions = new ArrayList<>();
		final String id = "varAssignment_" + ((group == null) ? "" : group.getId());
		final IcfgLocation start = new IcfgLocation(id + "_start", mProcedure);
		final IcfgLocation end = new IcfgLocation(id + "_end", mProcedure);
		// if the transition has not been created, do it
		if (!mCreatedTransitions.containsKey(start)) {
			final IcfgInternalTransition startEnd = createIcfgTransition(start, end, transformula);
			transitions.add(startEnd);
			// create a list for the start node which contains the target.
			final List<IcfgLocation> list = new ArrayList<>();
			list.add(end);
			mCreatedTransitions.put(start, list);
		} else if (mCreatedTransitions.containsKey(start)) {
			if (!mCreatedTransitions.get(start).contains(end)) {
				final IcfgInternalTransition startEnd = createIcfgTransition(start, end, transformula);
				transitions.add(startEnd);
				mCreatedTransitions.get(start).add(end);
			}
		}
		// new cfgComponent, has to be connected to the root node.
		mCfgComponents.put(id, new HybridCfgComponent(id, start, end, locations, transitions));
		/*
		 * Transition from Root to varAssignment
		 */
		// the target of the transition is the the start of the target CFG component
		final IcfgLocation target = mCfgComponents.get(id).getStart();
		final UnmodifiableTransFormula transFormula =
				TransFormulaBuilder.getTrivialTransFormula(mSmtToolkit.getManagedScript());
		final IcfgInternalTransition transition =
				new IcfgInternalTransition(mRootLocation, target, mPayload, transFormula);
		mRootLocation.addOutgoing(transition);
		target.addIncoming(transition);
		// add connection that has to be made from Variable assignment to initial location.
		mConnectionList.add(end);
	}
	
	private IcfgInternalTransition createIcfgTransition(IcfgLocation start, IcfgLocation end,
			UnmodifiableTransFormula transformula) {
		final IcfgInternalTransition trans = new IcfgInternalTransition(start, end, mPayload, transformula);
		start.addOutgoing(trans);
		end.addIncoming(trans);
		return trans;
	}
	
	/*
	 * Location methods
	 */
	private void locationsToIcfg(final Map<Integer, Location> autLocations) {
		/*
		 * locations consist of Flow and the invariant. -> Startnode (1) -> if/else invariant (2) -> apply flow (3) ->
		 * if/else invariant (4)
		 */
		for (final Map.Entry<Integer, Location> entry : autLocations.entrySet()) {
			final Integer autid = entry.getKey();
			final Location loc = entry.getValue();
			final List<IcfgInternalTransition> transitions = new ArrayList<>();
			final List<IcfgLocation> locations = new ArrayList<>();
			/*
			 * Locations: Start, End, Flow, InvariantCheck
			 */
			final IcfgLocation start = new IcfgLocation(autid + "_start", mProcedure);
			final IcfgLocation end = new IcfgLocation(autid + "_end", mProcedure);
			final IcfgLocation flow = new IcfgLocation(autid + "_flow", mProcedure);
			locations.add(flow);
			final IcfgLocation invCheck = new IcfgLocation(autid + "_invCheck", mProcedure);
			locations.add(invCheck);
			/*
			 * Transitions from start to Flow if invariant true
			 */
			// invariant to term:
			final String infix = preprocessLocationStatement(loc.getInvariant());
			final UnmodifiableTransFormula invariantTransformula = buildTransformula(infix, BuildScenario.INVARIANT);
			if (mLogger.isDebugEnabled()) {
				final String msg = createTransformulaLoggerMessage(invariantTransformula, infix);
				mLogger.debug(msg);
			}
			final UnmodifiableTransFormula tfStartFlow = invariantTransformula;
			final IcfgInternalTransition startFlow = new IcfgInternalTransition(start, flow, mPayload, tfStartFlow);
			start.addOutgoing(startFlow);
			flow.addIncoming(startFlow);
			transitions.add(startFlow);
			
			/*
			 * Transition from start to Error
			 */
			final IcfgInternalTransition errorTransition =
					new IcfgInternalTransition(start, mErrorLocation, mPayload, buildFalseTransformula());
			start.addOutgoing(errorTransition);
			mErrorLocation.addIncoming(errorTransition);
			
			/*
			 * Transition flow to invCheck
			 */
			final UnmodifiableTransFormula tfFlowInv =
					TransFormulaBuilder.getTrivialTransFormula(mSmtToolkit.getManagedScript());
			final IcfgInternalTransition flowInv = new IcfgInternalTransition(flow, invCheck, mPayload, tfFlowInv);
			flow.addOutgoing(flowInv);
			invCheck.addIncoming(flowInv);
			transitions.add(flowInv);
			/*
			 * Transition invCheck to end/exit if invariant true, go to end, else to error loc
			 */
			// invcheck -> end
			final UnmodifiableTransFormula tfInvEnd = invariantTransformula;
			final IcfgInternalTransition invEnd = new IcfgInternalTransition(invCheck, end, mPayload, tfInvEnd);
			invCheck.addOutgoing(invEnd);
			end.addIncoming(invEnd);
			transitions.add(invEnd);
			// create new cfgComponent
			mCfgComponents.put(autid.toString(),
					new HybridCfgComponent(autid.toString(), start, end, locations, transitions));
		}
	}
	
	/*
	 * Transition methods
	 */
	private void transitionsToIcfg(final List<Transition> transitions, final Location initialLocation) {
		// a transition in a hybrid automaton is simply an edge from one location to another.
		// guard and update can be merged with &&
		transitions.forEach(trans -> {
			// the source of the transition is the the end of the source CFG component
			final IcfgLocation source = mCfgComponents.get(Integer.toString(trans.getSourceId())).getEnd();
			// the target of the transition is the the start of the target CFG component
			final IcfgLocation target = mCfgComponents.get(Integer.toString(trans.getTargetId())).getStart();
			// invariant to term:
			final UnmodifiableTransFormula transFormula =
					buildTransitionTransformula(trans.getUpdate(), trans.getGuard());
			final String msg =
					createTransformulaLoggerMessage(transFormula, trans.getUpdate() + " && " + trans.getGuard());
			mLogger.info(msg);
			final IcfgInternalTransition transition =
					new IcfgInternalTransition(source, target, mPayload, transFormula);
			source.addOutgoing(transition);
			target.addIncoming(transition);
		});
		
		mConnectionList.forEach((loc) -> {
			mLogger.info(loc);
			// the source of the transition is the the end of the source CFG component
			final IcfgLocation source = loc;
			// the target of the transition is the the start of the target CFG component
			final IcfgLocation target = mCfgComponents.get(Integer.toString(initialLocation.getId())).getStart();
			final UnmodifiableTransFormula transformula =
					TransFormulaBuilder.getTrivialTransFormula(mSmtToolkit.getManagedScript());
			if (!mCreatedTransitions.containsKey(source)) {
				final IcfgInternalTransition transition = createIcfgTransition(source, target, transformula);
				// create a list for the start node which contains the target.
				final List<IcfgLocation> list = new ArrayList<>();
				list.add(target);
				mCreatedTransitions.put(source, list);
			} else if (mCreatedTransitions.containsKey(source)) {
				if (!mCreatedTransitions.get(source).contains(target)) {
					final IcfgInternalTransition startEnd = createIcfgTransition(source, target, transformula);
					mCreatedTransitions.get(source).add(target);
				}
			}
		});
		
	}
	
	private String createTransformulaLoggerMessage(final UnmodifiableTransFormula transFormula, final String infix) {
		String msg = "######## CREATED TRANSFORMULA ######## \n";
		msg += "created " + transFormula.toString() + "\n";
		msg += "infix: " + infix;
		return msg;
	}
	
	private UnmodifiableTransFormula buildTransitionTransformula(final String update, final String guard) {
		final HybridTermBuilder tb =
				new HybridTermBuilder(mVariableManager, mSmtToolkit.getManagedScript().getScript());
		UnmodifiableTransFormula transformula;
		Term formula = null;
		if (update.isEmpty() && guard.isEmpty()) {
			formula = mSmtToolkit.getManagedScript().getScript().term("true");
		} else if (!update.isEmpty() && !guard.isEmpty()) {
			final Term guardTerm = tb.infixToTerm(preprocessLocationStatement(guard), BuildScenario.GUARD);
			final Term updateTerm = tb.infixToTerm(preprocessLocationStatement(update), BuildScenario.UPDATE);
			formula = mSmtToolkit.getManagedScript().getScript().term("and", updateTerm, guardTerm);
		} else if (update.isEmpty() && !guard.isEmpty()) {
			formula = tb.infixToTerm(preprocessLocationStatement(guard), BuildScenario.GUARD);
		} else if (!update.isEmpty() && guard.isEmpty()) {
			formula = tb.infixToTerm(preprocessLocationStatement(update), BuildScenario.UPDATE);
		}
		// TFB
		final TransFormulaBuilder tfb = new TransFormulaBuilder(Collections.emptyMap(), Collections.emptyMap(), true,
				Collections.emptySet(), true, Collections.emptyList(), true);
		tb.getmInVars().forEach((progvar, invar) -> {
			tfb.addInVar(progvar, invar);
		});
		tb.getmOutVars().forEach((progvar, outvar) -> {
			tfb.addOutVar(progvar, outvar);
		});
		tfb.setFormula(formula);
		tfb.setInfeasibility(Infeasibility.NOT_DETERMINED);
		// finish construction of the transformula.
		transformula = tfb.finishConstruction(mSmtToolkit.getManagedScript());
		mLogger.debug("Transformula: " + transformula);
		return transformula;
	}
	
	private UnmodifiableTransFormula buildTransformula(final String infix, final BuildScenario scenario) {
		final HybridTermBuilder tb =
				new HybridTermBuilder(mVariableManager, mSmtToolkit.getManagedScript().getScript());
		final Term term = tb.infixToTerm(infix, scenario);
		final TransFormulaBuilder tfb = new TransFormulaBuilder(Collections.emptyMap(), Collections.emptyMap(), true,
				Collections.emptySet(), true, Collections.emptyList(), true);
		tb.getmInVars().forEach((progvar, invar) -> {
			tfb.addInVar(progvar, invar);
		});
		tb.getmOutVars().forEach((progvar, outvar) -> {
			tfb.addOutVar(progvar, outvar);
		});
		tfb.setFormula(term);
		tfb.setInfeasibility(Infeasibility.NOT_DETERMINED);
		// finish construction of the transformula.
		final UnmodifiableTransFormula transformula = tfb.finishConstruction(mSmtToolkit.getManagedScript());
		mLogger.debug("Transformula: " + transformula);
		return transformula;
	}
	
	private UnmodifiableTransFormula buildFalseTransformula() {
		final TransFormulaBuilder tfb = new TransFormulaBuilder(Collections.emptyMap(), Collections.emptyMap(), true,
				Collections.emptySet(), true, Collections.emptyList(), true);
		tfb.setFormula(mSmtToolkit.getManagedScript().getScript().term("false"));
		tfb.setInfeasibility(Infeasibility.NOT_DETERMINED);
		// finish construction of the transformula.
		return tfb.finishConstruction(mSmtToolkit.getManagedScript());
	}
	
	private String preprocessLocationStatement(final String invariant) {
		String inv = invariant.replaceAll(":=", "==");
		inv = inv.replaceAll("&&", "&");
		return inv;
	}
	
	public BasicIcfg<IcfgLocation> getSimpleIcfg() {
		final BasicIcfg<IcfgLocation> icfg = new BasicIcfg<>("testicfg", mSmtToolkit, IcfgLocation.class);
		
		final IcfgLocation startLoc = new IcfgLocation("start", "MAIN");
		icfg.addLocation(startLoc, true, false, true, false, false);
		
		final IcfgLocation middleLoc = new IcfgLocation("middle", "MAIN");
		icfg.addLocation(middleLoc, false, false, false, false, false);
		
		final IcfgLocation endLoc = new IcfgLocation("error", "MAIN");
		icfg.addLocation(endLoc, false, true, false, true, false);
		
		// Every procedure must have a unique entry and a unique exit. It is not allowed to have more than one exit (or
		// entry).
		
		// QUESTION: Is procExit = true correct here?
		
		TransFormulaBuilder tfb = new TransFormulaBuilder(Collections.emptyMap(), Collections.emptyMap(), true,
				Collections.emptySet(), true, Collections.emptyList(), true);
		tfb.setFormula(mSmtToolkit.getManagedScript().getScript().term("true"));
		tfb.setInfeasibility(Infeasibility.NOT_DETERMINED);
		// QUESTION: Is not determined correct here?
		
		// QUESTION: Does BoogieASTNode influence TraceAbstraction? Currently, we pass the same BoogieASTNode every time
		// to the ICFG. Should we construct new BoogieASTNodes every time?
		
		// QUESTION: Payload currently contains only a dummy location. Every payload consists of the SAME dummy
		// location. Is this correct / feasible?
		
		// Transitions
		final IcfgInternalTransition startToMiddle = new IcfgInternalTransition(startLoc, middleLoc, null,
				tfb.finishConstruction(mSmtToolkit.getManagedScript()));
		
		tfb = new TransFormulaBuilder(Collections.emptyMap(), Collections.emptyMap(), true, Collections.emptySet(),
				true, Collections.emptyList(), true);
		tfb.setFormula(mSmtToolkit.getManagedScript().getScript().term("true"));
		tfb.setInfeasibility(Infeasibility.NOT_DETERMINED);
		
		// If (true, false): Assertion error in SdHoareTripleChecker
		// If (true, true): Cast error (to CodeBlock)
		// If (false, false) or (false, true): No Error
		
		final IcfgInternalTransition middleToEnd = new IcfgInternalTransition(middleLoc, endLoc, null,
				tfb.finishConstruction(mSmtToolkit.getManagedScript()));
		
		startLoc.addOutgoing(startToMiddle);
		middleLoc.addIncoming(startToMiddle);
		middleLoc.addOutgoing(middleToEnd);
		endLoc.addIncoming(middleToEnd);
		
		return icfg;
	}
}
