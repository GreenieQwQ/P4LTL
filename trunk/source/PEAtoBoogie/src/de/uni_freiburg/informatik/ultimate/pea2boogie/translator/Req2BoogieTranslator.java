/*
 * Copyright (C) 2013-2017 Jochen Hoenicke (hoenicke@informatik.uni-freiburg.de)
 * Copyright (C) 2017 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2013-2017 University of Freiburg
 *
 * This file is part of the ULTIMATE PEAtoBoogie plug-in.
 *
 * The ULTIMATE PEAtoBoogie plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE PEAtoBoogie plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE PEAtoBoogie plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE PEAtoBoogie plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE PEAtoBoogie plug-in grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.pea2boogie.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.boogie.BoogieExpressionTransformer;
import de.uni_freiburg.informatik.ultimate.boogie.BoogieLocation;
import de.uni_freiburg.informatik.ultimate.boogie.ExpressionFactory;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssignmentStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.AssumeStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Attribute;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BinaryExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Body;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BooleanLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Declaration;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.HavocStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IdentifierExpression;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IfStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.IntegerLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LeftHandSide;
import de.uni_freiburg.informatik.ultimate.boogie.ast.LoopInvariantSpecification;
import de.uni_freiburg.informatik.ultimate.boogie.ast.ModifiesSpecification;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Procedure;
import de.uni_freiburg.informatik.ultimate.boogie.ast.RealLiteral;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.Unit;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VarList;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableDeclaration;
import de.uni_freiburg.informatik.ultimate.boogie.ast.VariableLHS;
import de.uni_freiburg.informatik.ultimate.boogie.ast.WhileStatement;
import de.uni_freiburg.informatik.ultimate.boogie.ast.WildcardExpression;
import de.uni_freiburg.informatik.ultimate.core.model.models.ILocation;
import de.uni_freiburg.informatik.ultimate.core.model.preferences.IPreferenceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.pea.CDD;
import de.uni_freiburg.informatik.ultimate.lib.pea.CounterTrace;
import de.uni_freiburg.informatik.ultimate.lib.pea.Phase;
import de.uni_freiburg.informatik.ultimate.lib.pea.PhaseEventAutomata;
import de.uni_freiburg.informatik.ultimate.lib.pea.Transition;
import de.uni_freiburg.informatik.ultimate.lib.srparse.pattern.InitializationPattern;
import de.uni_freiburg.informatik.ultimate.lib.srparse.pattern.PatternType;
import de.uni_freiburg.informatik.ultimate.lib.srparse.pattern.PatternType.ReqPeas;
import de.uni_freiburg.informatik.ultimate.pea2boogie.Activator;
import de.uni_freiburg.informatik.ultimate.pea2boogie.IReqSymbolTable;
import de.uni_freiburg.informatik.ultimate.pea2boogie.PatternContainer;
import de.uni_freiburg.informatik.ultimate.pea2boogie.preferences.Pea2BoogiePreferences;
import de.uni_freiburg.informatik.ultimate.pea2boogie.req2pea.IReq2Pea;
import de.uni_freiburg.informatik.ultimate.pea2boogie.req2pea.IReq2PeaAnnotator;
import de.uni_freiburg.informatik.ultimate.pea2boogie.req2pea.IReq2PeaTransformer;
import de.uni_freiburg.informatik.ultimate.pea2boogie.req2pea.Req2Pea;
import de.uni_freiburg.informatik.ultimate.pea2boogie.testgen.ReqInOutGuesser;
import de.uni_freiburg.informatik.ultimate.util.simplifier.NormalFormTransformer;

/**
 * This class translates a phase event automaton to an equivalent Boogie code.
 *
 * @author Jochen Hoenicke (hoenicke@informatik.uni-freiburg.de)
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 */
public class Req2BoogieTranslator {

	private final Unit mUnit;
	private final Map<PatternType, ReqPeas> mReq2Automata;
	private final BoogieLocation mUnitLocation;

	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;

	private final NormalFormTransformer<Expression> mNormalFormTransformer;

	private final IReqSymbolTable mSymboltable;
	private IReq2PeaAnnotator mReqCheckAnnotator;

	public Req2BoogieTranslator(final IUltimateServiceProvider services, final ILogger logger,
			final List<PatternType> patterns) {
		this(services, logger, patterns, new ArrayList<IReq2PeaTransformer>());
	}

	public Req2BoogieTranslator(final IUltimateServiceProvider services, final ILogger logger,
			final List<PatternType> patterns, final List<IReq2PeaTransformer> req2peaTransformers) {
		mLogger = logger;
		mServices = services;

		mNormalFormTransformer = new NormalFormTransformer<>(new BoogieExpressionTransformer());
		final IPreferenceProvider prefs = mServices.getPreferenceProvider(Activator.PLUGIN_ID);

		List<PatternType> requirements =
				patterns.stream().filter(a -> !(a instanceof InitializationPattern)).collect(Collectors.toList());

		List<InitializationPattern> init = patterns.stream().filter(a -> a instanceof InitializationPattern)
				.map(a -> (InitializationPattern) a).collect(Collectors.toList());

		if (prefs.getBoolean(Pea2BoogiePreferences.LABEL_GUESS_IN_OUT)) {
			final ReqInOutGuesser riog = new ReqInOutGuesser(logger, mServices, init, requirements);
			init = riog.getInitializationPatterns();
			requirements = riog.getRequirements();
		}

		final IReq2Pea req2pea = createReq2Pea(req2peaTransformers, init, requirements);
		if (req2pea.hasErrors()) {
			mUnitLocation = null;
			mUnit = null;
			mReq2Automata = null;
			mSymboltable = null;
			return;
		}

		mReq2Automata = req2pea.getPattern2Peas();
		mSymboltable = req2pea.getSymboltable();
		mReqCheckAnnotator = req2pea.getAnnotator();

		// TODO: Add locations to pattern type to generate meaningful boogie locations
		mUnitLocation = new BoogieLocation("", -1, -1, -1, -1);

		final List<Declaration> decls = new ArrayList<>();
		decls.addAll(mSymboltable.getDeclarations());

		decls.add(generateProcedures(init));
		mUnit = new Unit(mUnitLocation, decls.toArray(new Declaration[decls.size()]));
		annotateContainedPatternSet(mUnit, mReq2Automata, init);

	}

	private IReq2Pea createReq2Pea(final List<IReq2PeaTransformer> req2peaTransformers,
			final List<InitializationPattern> init, final List<PatternType> requirements) {
		IReq2Pea req2pea = new Req2Pea(mServices, mLogger, init, requirements);
		for (final IReq2PeaTransformer transformer : req2peaTransformers) {
			if (req2pea.hasErrors()) {
				break;
			}
			req2pea = transformer.transform(req2pea, init, requirements);
		}

		return req2pea;
	}

	private static void annotateContainedPatternSet(final Unit unit, final Map<PatternType, ?> req2Automata2,
			final List<InitializationPattern> init) {
		final List<PatternType> patternList = new ArrayList<>(init);
		req2Automata2.entrySet().stream().map(Entry::getKey).forEachOrdered(patternList::add);
		new PatternContainer(patternList).annotate(unit);
	}

	public Unit getUnit() {
		return mUnit;
	}

	/**
	 * Generate time passing.
	 *
	 * @param clock
	 *            identifier.
	 * @param Boogie
	 *            location.
	 * @return time passing statement.
	 */
	private List<Statement> genClockPlusDelta(final Collection<String> clockVars) {
		final List<Statement> stmts = new ArrayList<>();
		final Expression deltaID = mSymboltable.getIdentifierExpression(mSymboltable.getDeltaVarName());
		for (final String clockVar : clockVars) {
			final VariableLHS lhs = mSymboltable.getVariableLhs(clockVar);
			final Expression clockID = mSymboltable.getIdentifierExpression(clockVar);
			final Expression expr = ExpressionFactory.newBinaryExpression(lhs.getLocation(),
					BinaryExpression.Operator.ARITHPLUS, clockID, deltaID);
			stmts.add(genAssignmentStmt(lhs.getLocation(), lhs, expr));
		}
		return stmts;
	}

	/**
	 * Generate the delay statements and havoc all primed variables and event variables. The code has the form
	 *
	 * <pre>
	 * 	   havoc primedVars, eventVars, delta;
	 *     assume delta > 0.0
	 *     clock1 := clock + delta;
	 *     ...
	 * </pre>
	 *
	 * @param bl
	 * @return
	 */
	private List<Statement> genDelay(final BoogieLocation bl) {
		final String deltaVarName = mSymboltable.getDeltaVarName();
		final List<Statement> stmts = new ArrayList<>();
		stmts.addAll(genHavocStmts(mSymboltable.getPrimedVars()));
		stmts.addAll(genHavocStmts(mSymboltable.getEventVars()));
		stmts.addAll(genHavocStmts(Collections.singleton(deltaVarName)));

		final IdentifierExpression delta = mSymboltable.getIdentifierExpression(deltaVarName);
		final ILocation deltaLoc = delta.getLocation();
		final RealLiteral realLiteral = ExpressionFactory.createRealLiteral(deltaLoc, "0.0");
		final Expression binExp =
				ExpressionFactory.newBinaryExpression(deltaLoc, BinaryExpression.Operator.COMPGT, delta, realLiteral);
		stmts.add(new AssumeStatement(deltaLoc, binExp));
		stmts.addAll(genClockPlusDelta(mSymboltable.getClockVars()));

		return stmts;
	}

	private List<Statement> genHavocStmts(final Collection<String> ids) {
		final List<Statement> havocs = new ArrayList<>();
		for (final String var : ids) {
			final VariableLHS lhs = mSymboltable.getVariableLhs(var);
			havocs.add(new HavocStatement(lhs.getLocation(), new VariableLHS[] { lhs }));
		}
		return havocs;
	}

	/**
	 * Generate the expression <code><i>pcName</i> == <i>phaseIndex</i></code> that checks if the automaton with a pc
	 * named pcName is currently in the phase phaseIndex.
	 *
	 * @param phaseIndex
	 *            the index of the phase we check for.
	 * @param pcName
	 *            the name of the pc variable for this automaton
	 * @param bl
	 * @return
	 */
	private Expression genComparePhaseCounter(final int phaseIndex, final String pcName, final BoogieLocation bl) {
		final IdentifierExpression identifier = mSymboltable.getIdentifierExpression(pcName);
		final IntegerLiteral intLiteral = ExpressionFactory.createIntegerLiteral(bl, Integer.toString(phaseIndex));
		return ExpressionFactory.newBinaryExpression(bl, BinaryExpression.Operator.COMPEQ, identifier, intLiteral);
	}

	/**
	 * Creates the code that checks the phase invariant of the given phase.
	 *
	 * @param phase
	 *            the phase whose invariant should be checked.
	 * @param bl
	 * @return the array of (two) statements that check the invariant.
	 */
	private Statement[] genCheckPhaseInvariant(final Phase phase, final BoogieLocation bl) {
		final CDD clockInvCdd = phase.getClockInvariant();
		final AssumeStatement assumeClInv;
		if (clockInvCdd != CDD.TRUE) {
			final Expression clInv = new CDDTranslator().toBoogie(clockInvCdd, bl);
			assumeClInv = new AssumeStatement(bl, mNormalFormTransformer.toNnf(clInv));
		} else {
			assumeClInv = null;
		}
		final CDD stateInvCdd = phase.getStateInvariant();
		final AssumeStatement assumeStateInv;
		if (stateInvCdd != CDD.TRUE) {
			final Expression stateInv = new CDDTranslator().toBoogie(stateInvCdd, bl);
			assumeStateInv = new AssumeStatement(bl, mNormalFormTransformer.toNnf(stateInv));
		} else {
			assumeStateInv = null;
		}

		if (assumeClInv == null && assumeStateInv == null) {
			return null;
		}

		return concatStmt(assumeClInv, assumeStateInv);
	}

	private static Statement[] concatStmt(final Statement... stmts) {
		if (stmts == null || stmts.length == 0) {
			throw new IllegalArgumentException();
		}
		final List<Statement> stmtList = Arrays.stream(stmts).filter(a -> a != null).collect(Collectors.toList());
		if (stmtList.isEmpty()) {
			throw new IllegalArgumentException();
		}
		return stmtList.toArray(new Statement[stmtList.size()]);
	}

	private static Statement joinIfSmts(final Statement[] statements, final BoogieLocation bl) {
		if (statements == null || statements.length == 0) {
			throw new IllegalArgumentException();
		}
		IfStatement acc = null;
		for (int i = 0; i < statements.length; i++) {
			final IfStatement currentIfSmt = (IfStatement) statements[i];
			assert currentIfSmt.getElsePart().length == 0;
			if (acc == null) {
				acc = currentIfSmt;
			} else {
				acc = new IfStatement(bl, currentIfSmt.getCondition(), currentIfSmt.getThenPart(),
						new Statement[] { acc });
			}
		}
		return acc;
	}

	private static Statement joinInnerIfSmts(final Statement[] statements, final BoogieLocation bl) {

		final List<Statement> smtList = new ArrayList<>();
		for (int i = 0; i < statements.length; i++) {
			final IfStatement oldIfSmt = (IfStatement) statements[i];
			if (smtList.isEmpty()) {
				final BooleanLiteral bLiteral = new BooleanLiteral(bl, false);
				final AssumeStatement assumeFalse = new AssumeStatement(bl, bLiteral);
				final Statement[] smt = new Statement[1];
				smt[0] = assumeFalse;
				final IfStatement newIfSmt = new IfStatement(bl, oldIfSmt.getCondition(), oldIfSmt.getThenPart(), smt);
				smtList.add(newIfSmt);
			} else {
				final Statement[] smt = new Statement[1];
				smt[0] = smtList.get(smtList.size() - 1);
				final IfStatement newIfSmt = new IfStatement(bl, oldIfSmt.getCondition(), oldIfSmt.getThenPart(), smt);
				smtList.add(newIfSmt);
			}
		}

		return smtList.get(smtList.size() - 1);
	}

	/**
	 * Check the invariants of the given automaton. This is an if statement that first checks in which phase the
	 * automaton is and then checks the corresponding invariants.
	 *
	 * @param patternType
	 *
	 * @param automaton
	 *            the automaton to check.
	 * @param bl
	 *            The location information to correspond the generated source to the property.
	 * @return The if statement checking the p
	 */
	private List<Statement> genInvariantGuards(final PatternType patternType, final PhaseEventAutomata automaton,
			final String pcName, final BoogieLocation bl) {
		final Phase[] phases = automaton.getPhases();
		assert phases.length > 0;
		// TODO: Keep the if in this case; may be helpful for vacuity reason extraction
		// if (phases.length == 1) {
		// return Arrays.asList(genCheckPhaseInvariant(phases[0], bl));
		// }

		final List<Statement> statements = new ArrayList<>();
		final Statement[] emptyElseBody = new Statement[0];
		for (int i = 0; i < phases.length; i++) {
			final Expression ifCon = genComparePhaseCounter(i, pcName, bl);
			final Statement[] ifBody = genCheckPhaseInvariant(phases[i], bl);
			if (ifBody == null) {
				mLogger.warn("phase without clock or state invariant for " + patternType);
				continue;
			}
			statements.add(new IfStatement(bl, ifCon, ifBody, emptyElseBody));
		}
		if (statements.isEmpty() || statements.size() == 1) {
			return statements;
		}
		return Collections.singletonList(joinIfSmts(statements.toArray(new Statement[statements.size()]), bl));
	}

	private static Statement genReset(final String resetVar, final BoogieLocation bl) {
		final RealLiteral realLiteral = new RealLiteral(bl, Double.toString(0.0));
		return genAssignmentStmt(bl, resetVar, realLiteral);
	}

	private static Statement genPCAssign(final String pcName, final int phaseIndex, final BoogieLocation bl) {
		final IntegerLiteral intLiteral = new IntegerLiteral(bl, Integer.toString(phaseIndex));
		return genAssignmentStmt(bl, pcName, intLiteral);
	}

	private Statement[] genInnerIfBody(final PhaseEventAutomata automaton, final String pcName,
			final Transition transition, final BoogieLocation bl) {

		final List<Statement> smtList = new ArrayList<>();
		final CDD guardCdd = transition.getGuard();
		if (guardCdd != CDD.TRUE) {
			final Expression expr = new CDDTranslator().toBoogie(guardCdd, bl);
			final AssumeStatement assumeGuard = new AssumeStatement(bl, mNormalFormTransformer.toNnf(expr));
			smtList.add(assumeGuard);
		}

		if (transition.getResets().length != 0) {
			final String[] resets = transition.getResets();
			for (int i = 0; i < resets.length; i++) {
				smtList.add(genReset(resets[i], bl));
			}
		}
		final Phase desPhase = transition.getDest();
		final Phase[] phases = automaton.getPhases();
		int phaseIndex = -1;
		for (int i = 0; i < phases.length; i++) {
			if (phases[i].getName().equals(desPhase.getName())) {
				phaseIndex = i;
			}
		}

		smtList.add(genPCAssign(pcName, phaseIndex, bl));

		return smtList.toArray(new Statement[smtList.size()]);
	}

	private Statement genOuterIfBody(final PhaseEventAutomata automaton, final String pcName, final Phase phase,
			final BoogieLocation bl) {

		final Statement[] statements = new Statement[phase.getTransitions().size()];
		final Statement[] emptyArray = new Statement[0];
		final WildcardExpression wce = new WildcardExpression(bl);
		final List<Transition> transitions = phase.getTransitions();
		for (int i = 0; i < transitions.size(); i++) {
			statements[i] =
					new IfStatement(bl, wce, genInnerIfBody(automaton, pcName, transitions.get(i), bl), emptyArray);
		}
		return joinInnerIfSmts(statements, bl);
	}

	private Statement genOuterIfTransition(final PhaseEventAutomata automaton, final String pcName,
			final BoogieLocation bl) {
		final Phase[] phases = automaton.getPhases();
		final Statement[] statements = new Statement[phases.length];
		final Statement[] emptyArray = new Statement[0];
		for (int i = 0; i < phases.length; i++) {
			final Expression ifCon = genComparePhaseCounter(i, pcName, bl);
			final Statement[] outerIfBodySmt = new Statement[] { genOuterIfBody(automaton, pcName, phases[i], bl) };
			final IfStatement ifStatement = new IfStatement(bl, ifCon, outerIfBodySmt, emptyArray);
			statements[i] = ifStatement;
		}
		return joinIfSmts(statements, bl);
	}

	private List<Statement> genStateVarsAssign() {
		final List<Statement> assignments = mSymboltable.getStateVars().stream().map(this::genStateVarAssignHistory).collect(Collectors.toList());
		assignments.addAll(mSymboltable.getStateVars().stream().map(this::genStateVarAssignPrimed).collect(Collectors.toList()));
		assignments.addAll(mSymboltable.getPcVars().stream().map(this::genStateVarAssignHistory).collect(Collectors.toList()));
		return assignments;
	}

	private AssignmentStatement genStateVarAssignPrimed(final String stateVar) {
		final VariableLHS lhsVar = mSymboltable.getVariableLhs(stateVar);
		final IdentifierExpression rhs = mSymboltable.getIdentifierExpression(mSymboltable.getPrimedVarId(stateVar));
		return genAssignmentStmt(rhs.getLocation(), lhsVar, rhs);
	}

	private AssignmentStatement genStateVarAssignHistory(final String stateVar) {
		final VariableLHS lhsVar = mSymboltable.getVariableLhs(mSymboltable.getHistoryVarId(stateVar));
		final IdentifierExpression rhs = mSymboltable.getIdentifierExpression(stateVar);
		return genAssignmentStmt(rhs.getLocation(), lhsVar, rhs);
	}

	public static String getAssertLabel(final Entry<PatternType, PhaseEventAutomata>[] subset) {
		final StringBuilder sb = new StringBuilder();
		for (final Entry<PatternType, PhaseEventAutomata> entry : subset) {
			sb.append(entry.getValue().getName() + "_");
		}
		return sb.toString();
	}

	/**
	 * Create the statements of the main loop of the pea product. The main loop looks like this
	 *
	 * <pre>
	 *    delay statements (havoc delay, eventVar, primedVars, add delay to all clocks)
	 *    check invariants of phases
	 *    assert reachability
	 *    check transitions
	 * </pre>
	 *
	 * @param bl
	 *            Location of the procedure body.
	 * @return Statements of the while-body.
	 */
	private Statement[] genWhileBody(final BoogieLocation bl) {
		final List<Statement> stmtList = new ArrayList<>();
		stmtList.addAll(genDelay(bl));

		for (final Entry<PatternType, ReqPeas> entry : mReq2Automata.entrySet()) {
			final PatternType pattern = entry.getKey();
			for (final Entry<CounterTrace, PhaseEventAutomata> pea : entry.getValue().getCounterTrace2Pea()) {
				stmtList.addAll(
						genInvariantGuards(pattern, pea.getValue(), mSymboltable.getPcName(pea.getValue()), bl));
			}
		}

		stmtList.addAll(mReqCheckAnnotator.getStateChecks());

		for (final Entry<PatternType, ReqPeas> entry : mReq2Automata.entrySet()) {
			for (final Entry<CounterTrace, PhaseEventAutomata> pea : entry.getValue().getCounterTrace2Pea()) {
				stmtList.add(genOuterIfTransition(pea.getValue(), mSymboltable.getPcName(pea.getValue()), bl));
			}
		}

		stmtList.addAll(mReqCheckAnnotator.getPostTransitionChecks());
		stmtList.addAll(genStateVarsAssign());

		return stmtList.toArray(new Statement[stmtList.size()]);
	}

	/**
	 * Create the main loop of the pea product. This is a huge while statement that contains all transitions of all
	 * components. This procedure calls {@link genWhileBody} to create the statements of the main loop.
	 *
	 * @param bl
	 *            Location of the procedure body.
	 * @return The while-statement.
	 */
	private Statement genWhileStmt(final BoogieLocation bl) {
		return new WhileStatement(bl, new WildcardExpression(bl), new LoopInvariantSpecification[0], genWhileBody(bl));
	}

	private Expression genPcExpr(final PhaseEventAutomata aut) {
		final Phase[] phases = aut.getPhases();
		final Phase[] initialPhases = aut.getInit();
		// determine initial phases
		for (int i = 0; i < phases.length; i++) {

			for (int j = 0; j < initialPhases.length; j++) {
				if (phases[i].getName().equals(initialPhases[j].getName())) {
					phases[i].setInit(true);
					break;
				}
			}
		}
		final String pcName = mSymboltable.getPcName(aut);
		// construct or over initial phases
		Expression acc = null;
		for (int i = 0; i < phases.length; i++) {
			if (!phases[i].isInit) {
				continue;
			}

			final IdentifierExpression id = mSymboltable.getIdentifierExpression(pcName);
			final Expression current =
					ExpressionFactory.newBinaryExpression(id.getLocation(), BinaryExpression.Operator.COMPEQ, id,
							ExpressionFactory.createIntegerLiteral(id.getLocation(), Integer.toString(i)));
			if (acc == null) {
				acc = current;
			} else {
				acc = ExpressionFactory.newBinaryExpression(id.getLocation(), BinaryExpression.Operator.LOGICOR, acc,
						current);
			}
		}
		return mNormalFormTransformer.toNnf(acc);
	}

	private List<Statement> genInitialPhasesStmts(final BoogieLocation bl) {
		final List<Statement> stmts = new ArrayList<>();
		for (final Entry<PatternType, ReqPeas> entry : mReq2Automata.entrySet()) {
			for (final Entry<CounterTrace, PhaseEventAutomata> ct2pea : entry.getValue().getCounterTrace2Pea()) {
				final PhaseEventAutomata aut = ct2pea.getValue();
				final VariableLHS lhs = mSymboltable.getVariableLhs(mSymboltable.getPcName(aut));
				stmts.add(new HavocStatement(lhs.getLocation(), new VariableLHS[] { lhs }));
				stmts.add(new AssumeStatement(lhs.getLocation(), genPcExpr(aut)));
			}
		}
		return stmts;
	}

	private List<Statement> genClockInitStmts() {
		if (mSymboltable.getClockVars().isEmpty()) {
			return Collections.emptyList();
		}
		final List<Statement> stmts = new ArrayList<>();
		for (final String clkId : mSymboltable.getClockVars()) {
			final IdentifierExpression id = mSymboltable.getIdentifierExpression(clkId);
			final RealLiteral real = ExpressionFactory.createRealLiteral(id.getLocation(), "0.0");

			stmts.add(new HavocStatement(id.getLocation(), new VariableLHS[] { mSymboltable.getVariableLhs(clkId) }));
			stmts.add(new AssumeStatement(id.getLocation(), ExpressionFactory.newBinaryExpression(id.getLocation(),
					BinaryExpression.Operator.COMPEQ, id, real)));
		}
		return stmts;
	}

	/**
	 * One assignment is initialized (only as an example). The genWhileSmt method is called.
	 *
	 * @param bl
	 *            Location of the procedure body.
	 * @param init
	 * @return Statements of the procedure body which includes one assignment and one while-statement.
	 */
	private Statement[] generateProcedureBodyStmts(final BoogieLocation bl, final List<InitializationPattern> init) {
		final List<Statement> statements = new ArrayList<>();
		statements.addAll(genInitialPhasesStmts(bl));
		statements.addAll(genInitialPhasesStmts(bl));
		statements.addAll(genClockInitStmts());
		//Assign the history vars with the initial state as if a small stutter step had occured initally.
		statements.addAll(mSymboltable.getStateVars().stream().map(this::genStateVarAssignHistory).collect(Collectors.toList()));
		statements.addAll(mSymboltable.getPcVars().stream().map(this::genStateVarAssignHistory).collect(Collectors.toList()));
		statements.addAll(mReqCheckAnnotator.getPreChecks());
		statements.add(genWhileStmt(bl));
		return statements.toArray(new Statement[statements.size()]);
	}

	/**
	 * Generates an {@link AssignmentStatement} of the form <code>id := val</code>.
	 */
	private static AssignmentStatement genAssignmentStmt(final ILocation bl, final String id, final Expression val) {
		return genAssignmentStmt(bl, new VariableLHS(bl, id), val);
	}

	private static AssignmentStatement genAssignmentStmt(final ILocation bl, final VariableLHS lhs,
			final Expression val) {
		assert lhs.getLocation() == bl;
		return new AssignmentStatement(bl, new LeftHandSide[] { lhs }, new Expression[] { val });
	}

	public IReqSymbolTable getReqSymbolTable() {
		return mSymboltable;
	}

	/**
	 * The procedure statement is initialized. It is deployed to the unit. The unit is sent to the print process. The
	 * result is a Boogie text file.
	 *
	 * @param init
	 */
	private Declaration generateProcedures(final List<InitializationPattern> init) {
		final BoogieLocation bl = mUnitLocation;
		final VariableDeclaration[] localVars = new VariableDeclaration[0];
		final Body body = new Body(bl, localVars, generateProcedureBodyStmts(bl, init));
		final List<String> modifiedVarsList = new ArrayList<>();

		modifiedVarsList.addAll(mSymboltable.getClockVars());
		modifiedVarsList.addAll(mSymboltable.getPcVars());
		modifiedVarsList.add(mSymboltable.getDeltaVarName());
		modifiedVarsList.addAll(mSymboltable.getStateVars());
		modifiedVarsList.addAll(mSymboltable.getPrimedVars());
		modifiedVarsList.addAll(mSymboltable.getHistoryVars());
		modifiedVarsList.addAll(mSymboltable.getEventVars());

		final VariableLHS[] modifiedVars = new VariableLHS[modifiedVarsList.size()];
		for (int i = 0; i < modifiedVars.length; i++) {
			modifiedVars[i] = new VariableLHS(bl, modifiedVarsList.get(i));
		}
		final ModifiesSpecification mod = new ModifiesSpecification(bl, false, modifiedVars);
		final ModifiesSpecification[] modArray = new ModifiesSpecification[1];
		modArray[0] = mod;
		final Attribute[] attribute = new Attribute[0];
		final String[] typeParams = new String[0];
		final VarList[] inParams = new VarList[0];
		final VarList[] outParams = new VarList[0];
		return new Procedure(bl, attribute, "myProcedure", typeParams, inParams, outParams, modArray, body);
	}
}
