/*
 * Copyright (C) 2018 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2019 Nico Hauff (hauffn@informatik.uni-freiburg.de)
 * Copyright (C) 2019 Elisabeth Henkel (henkele@informatik.uni-freiburg.de)
 * Copyright (C) 2018 University of Freiburg
 *
 * This file is part of the ULTIMATE MSO Library package.
 *
 * The ULTIMATE MSO Library package library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE MSO Library package library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE MSO Library package. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE MSO Library package, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE MSO Library package library grant you additional permission
 * to convey the resulting work.
 */

package de.uni_freiburg.informatik.ultimate.mso;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomatonDefinitionPrinter;
import de.uni_freiburg.informatik.ultimate.automata.AutomatonDefinitionPrinter.Format;
import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Complement;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.Intersect;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.SmtSortUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.SmtUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.linearterms.AbstractGeneralizedaAffineRelation.TransformInequality;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.linearterms.AffineRelation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.linearterms.AffineTerm;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.linearterms.AffineTermTransformer;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.linearterms.NotAffineException;
import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.Logics;
import de.uni_freiburg.informatik.ultimate.logic.Model;
import de.uni_freiburg.informatik.ultimate.logic.NoopScript;
import de.uni_freiburg.informatik.ultimate.logic.QuantifiedFormula;
import de.uni_freiburg.informatik.ultimate.logic.Rational;
import de.uni_freiburg.informatik.ultimate.logic.SMTLIBException;
import de.uni_freiburg.informatik.ultimate.logic.Term;

/**
 * TODO: Comment Class TODO: Check inputs.
 *
 * @author Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * @author Elisabeth Henkel (henkele@informatik.uni-freiburg.de)
 * @author Nico Hauff (hauffn@informatik.uni-freiburg.de)
 */
public class MSODScript extends NoopScript {

	private final IUltimateServiceProvider mUltimateServiceProvider;
	private final AutomataLibraryServices mAutomataLibrarayServices;
	private final MSODOperations mMSODOperations;
	public final ILogger mLogger;
	private Term mAssertionTerm;
	private Map<Term, Term> mModel;

	public MSODScript(final IUltimateServiceProvider services, final ILogger logger, final MSODOperations operations) {
		mUltimateServiceProvider = services;
		mAutomataLibrarayServices = new AutomataLibraryServices(services);
		mLogger = logger;

		mMSODOperations = operations;
	}

	@Override
	public void setLogic(final String logic) throws UnsupportedOperationException, SMTLIBException {
		super.setLogic(logic);
	}

	@Override
	public void setLogic(final Logics logic) throws UnsupportedOperationException, SMTLIBException {
		super.setLogic(logic);
	}

	@Override
	public LBool assertTerm(final Term term) throws SMTLIBException {
		mAssertionTerm = mAssertionTerm == null ? term : term("and", new Term[] { mAssertionTerm, term });
		return null;
	}

	@Override
	public LBool checkSat() throws SMTLIBException {
		mLogger.info("INPUT: " + mAssertionTerm);
		LBool result = LBool.UNKNOWN;

		try {

			final INestedWordAutomaton<MSODAlphabetSymbol, String> automaton = traversePostOrder(mAssertionTerm);

			// mLogger.info(automatonToString(automaton, Format.ATS));
			mModel = mMSODOperations.getResult(this, mAutomataLibrarayServices, automaton);

			if (mModel == null) {
				result = LBool.UNSAT;
				// mLogger.info("RESULT: UNSAT");
			} else {
				result = LBool.SAT;

				// mLogger.info("RESULT: SAT");
				// mLogger.info("MODEL: " + mModel);
			}

		} catch (final Exception e) {
			mLogger.info(e);
		}

		return result;
	}

	@Override
	public Map<Term, Term> getValue(final Term[] terms) throws SMTLIBException {
		final Map<Term, Term> values = new HashMap<>();

		if (mModel == null) {
			return values;
		}

		for (final Term term : terms) {
			Term value = mModel.get(term);

			if (value == null) {
				if (SmtSortUtils.isIntSort(term.getSort())) {
					value = SmtUtils.constructIntValue(this, BigInteger.ZERO);
				}

				if (MSODUtils.isSetOfIntSort(term.getSort())) {
					value = MSODUtils.constructSetOfIntValue(this, new HashSet<BigInteger>());
				}
			}
			values.put(term, value);
		}

		return values;
	}

	@Override
	public Model getModel() throws SMTLIBException, UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Traverses term in post order.
	 *
	 * @throws AutomataLibraryException
	 *             iff π = 4.
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> traversePostOrder(final Term term) throws Exception {
		mLogger.info("Traverse term: " + term);

		if (term instanceof QuantifiedFormula) {
			final QuantifiedFormula quantifiedFormula = (QuantifiedFormula) term;

			if (quantifiedFormula.getQuantifier() == QuantifiedFormula.FORALL) {
				return processForall(quantifiedFormula);
			}

			if (quantifiedFormula.getQuantifier() == QuantifiedFormula.EXISTS) {
				return processExists(quantifiedFormula);
			}
		}

		if (term instanceof ApplicationTerm) {
			final ApplicationTerm applicationTerm = (ApplicationTerm) term;
			final String functionSymbol = applicationTerm.getFunction().getName();

			if (functionSymbol.equals("true")) {
				return processTrue();
			}

			if (functionSymbol.equals("false")) {
				return processFalse();
			}

			if (functionSymbol.equals("not")) {
				return processNegation(applicationTerm);
			}

			if (functionSymbol.equals("and")) {
				return processConjunction(applicationTerm);
			}

			if (functionSymbol.equals("or")) {
				return processDisjunction(applicationTerm);
			}

			if (functionSymbol.equals("=>")) {
				return processImplication(applicationTerm);
			}

			if (functionSymbol.equals("strictSubsetInt")) {
				return processStrictSubset(applicationTerm);
			}

			if (functionSymbol.equals("subsetInt")) {
				return processSubset(applicationTerm);
			}

			if (functionSymbol.equals("element")) {
				return processElement(applicationTerm);
			}

			if (functionSymbol.equals("=")) {
				return processEqual(applicationTerm);
			}

			if (functionSymbol.equals(">")) {
				return processGreater(applicationTerm);
			}

			if (functionSymbol.equals(">=")) {
				return processGreaterEqual(applicationTerm);
			}

			if (functionSymbol.equals("<") || functionSymbol.equals("<=")) {
				return processLessOrLessEqual(applicationTerm);
			}
		}

		throw new IllegalArgumentException("Input must be a QuantifiedFormula or an ApplicationTerm.");
	}

	/**
	 * Returns automaton that represents "forall φ". Performs equivalent transformation to existential quantifier and
	 * calls {@link #traversePostOrder(Term)} with the result".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processForall(final QuantifiedFormula term)
			throws Exception {

		final Term subformula = SmtUtils.not(this, term.getSubformula());
		final Term exists = SmtUtils.not(this, quantifier(QuantifiedFormula.EXISTS, term.getVariables(), subformula));

		return traversePostOrder(exists);
	}

	/**
	 * Returns automaton that represents "exists φ".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processExists(final QuantifiedFormula term)
			throws Exception {

		INestedWordAutomaton<MSODAlphabetSymbol, String> result = traversePostOrder(term.getSubformula());
		mLogger.info("Construct ∃ φ: " + term);

		final Term[] quantifiedVariables = term.getVariables();

		if (MSODUtils.containsSort(result.getAlphabet(), MSODUtils.getSetOfIntSort(this).getName()).isEmpty()) {
			final Set<MSODAlphabetSymbol> zeros =
					MSODUtils.allMatchesAlphabet(result.getAlphabet(), false, quantifiedVariables);

			final Queue<String> states = new LinkedList<>(result.getFinalStates());
			final Set<String> additionalFinals = new HashSet<>();

			while (!states.isEmpty()) {
				final Set<String> preds = MSODUtils.hierarchicalPredecessorsIncoming(result, states.poll(), zeros);

				for (final String pred : preds) {
					if (!result.isFinal(pred) && additionalFinals.add(pred)) {
						states.add(pred);
					}
				}
			}

			result = mMSODOperations.makeStatesFinal(mAutomataLibrarayServices, result, additionalFinals);
		}

		final Set<Term> freeVars = new HashSet<>(result.getAlphabet().iterator().next().getMap().keySet());
		freeVars.removeAll(Arrays.asList(quantifiedVariables));

		final Set<MSODAlphabetSymbol> reducedAlphabet = MSODUtils.createAlphabet(freeVars.toArray(new Term[0]));
		result = mMSODOperations.reconstruct(mAutomataLibrarayServices, result, reducedAlphabet, false);

		return result;
	}

	/**
	 * Returns automaton that represents "true".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processTrue() {
		mLogger.info("Construct true");

		return mMSODOperations.trueAutomaton(mAutomataLibrarayServices);
	}

	/**
	 * Returns automaton that represents "false".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processFalse() {
		mLogger.info("Construct false");

		return mMSODOperations.falseAutomaton(mAutomataLibrarayServices);
	}

	/**
	 * Returns automaton that represents "not φ".
	 *
	 * @throws AutomataLibraryException
	 *             if construction of {@link Complement} or {@link Intersect} fails.
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processNegation(final ApplicationTerm term)
			throws AutomataLibraryException, Exception {

		INestedWordAutomaton<MSODAlphabetSymbol, String> result = traversePostOrder(term.getParameters()[0]);
		mLogger.info("Construct not φ: " + term);

		result = mMSODOperations.complement(mAutomataLibrarayServices, result);

		return result;
	}

	/**
	 * Returns automaton that represents "φ and ... and ψ".
	 *
	 * @throws AutomataLibraryException
	 *             if construction of {@link Intersect} fails.
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processConjunction(final ApplicationTerm term)
			throws AutomataLibraryException, Exception {

		INestedWordAutomaton<MSODAlphabetSymbol, String> result = traversePostOrder(term.getParameters()[0]);
		mLogger.info("Construct φ and ψ (0): " + term);

		for (int i = 1; i < term.getParameters().length; i++) {
			INestedWordAutomaton<MSODAlphabetSymbol, String> tmp = traversePostOrder(term.getParameters()[i]);
			mLogger.info("Construct φ and ψ (" + i + "): " + term);

			Set<MSODAlphabetSymbol> symbols;
			symbols = MSODUtils.mergeAlphabets(result.getAlphabet(), tmp.getAlphabet());

			result = mMSODOperations.reconstruct(mAutomataLibrarayServices, result, symbols, true);
			tmp = mMSODOperations.reconstruct(mAutomataLibrarayServices, tmp, symbols, true);

			result = mMSODOperations.intersect(mAutomataLibrarayServices, result, tmp);
		}

		// result = new MinimizeSevpa<>(mAutomataLibrarayServices, new MSODStringFactory(), result).getResult();

		return result;
	}

	/**
	 * Returns automaton that represents "φ or ... or ψ". Performs equivalent transformation to conjunction and calls
	 * {@link #traversePostOrder(Term)} with the result".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processDisjunction(final ApplicationTerm term)
			throws Exception {

		INestedWordAutomaton<MSODAlphabetSymbol, String> result = traversePostOrder(term.getParameters()[0]);
		mLogger.info("Construct φ and ψ (0): " + term);

		for (int i = 1; i < term.getParameters().length; i++) {
			INestedWordAutomaton<MSODAlphabetSymbol, String> tmp = traversePostOrder(term.getParameters()[i]);
			mLogger.info("Construct φ and ψ (" + i + "): " + term);

			Set<MSODAlphabetSymbol> symbols;
			symbols = MSODUtils.mergeAlphabets(result.getAlphabet(), tmp.getAlphabet());

			result = mMSODOperations.reconstruct(mAutomataLibrarayServices, result, symbols, true);
			tmp = mMSODOperations.reconstruct(mAutomataLibrarayServices, tmp, symbols, true);

			result = mMSODOperations.union(mAutomataLibrarayServices, result, tmp);
		}

		return result;
	}

	/**
	 * Returns automaton that represents "φ implies ψ". Performs equivalent transformation to "not φ and ψ" and calls
	 * {@link #traversePostOrder(Term)} with the result".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processImplication(final ApplicationTerm term)
			throws Exception {

		final Term[] terms = term.getParameters();
		for (int i = 0; i < terms.length - 1; i++) {
			terms[i] = SmtUtils.not(this, terms[i]);
		}

		return traversePostOrder(SmtUtils.or(this, terms));
	}

	/**
	 * Returns automaton that represents "t = c". Performs equivalent transformation to "t <= c and not t < c" and calls
	 * {@link #traversePostOrder(Term)} with the result".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processEqual(final ApplicationTerm term) throws Exception {

		final Term[] terms = term.getParameters();
		final Term lessEqual = SmtUtils.leq(this, terms[0], terms[1]);
		final Term greaterEqual = SmtUtils.not(this, SmtUtils.less(this, terms[0], terms[1]));
		final Term equal = SmtUtils.and(this, lessEqual, greaterEqual);

		return traversePostOrder(equal);
	}

	/**
	 * Returns automaton that represents "t > c". Performs equivalent transformation to "not t <= c" and calls
	 * {@link #traversePostOrder(Term)} with the result".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processGreater(final ApplicationTerm term)
			throws Exception {

		final Term[] terms = term.getParameters();
		final Term greater = SmtUtils.not(this, SmtUtils.leq(this, terms[0], terms[1]));

		return traversePostOrder(greater);
	}

	/**
	 * Returns automaton that represents "t >= c". Performs equivalent transformation to "not t < c" and calls
	 * {@link #traversePostOrder(Term)} with the result".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processGreaterEqual(final ApplicationTerm term)
			throws Exception {

		final Term[] terms = term.getParameters();
		final Term greaterEqual = SmtUtils.not(this, SmtUtils.less(this, terms[0], terms[1]));

		return traversePostOrder(greaterEqual);
	}

	/**
	 * Returns automaton that represents "t < c" or "t <= c".
	 *
	 * @throws NotAffineException
	 *             if construction of {@link AffineRelation} fails.
	 *
	 * @throws AutomataLibraryException
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processLessOrLessEqual(final ApplicationTerm term)
			throws NotAffineException, AutomataLibraryException {

		final AffineRelation affineRelation = AffineRelation.convert(this, term, TransformInequality.NONSTRICT2STRICT);
		final AffineTerm affineTerm = affineRelation.getAffineTerm();
		final Map<Term, Rational> variables = affineTerm.getVariable2Coefficient();
		final Rational constant = affineTerm.getConstant().negate();

		if (variables.size() == 1) {
			final Entry<Term, Rational> var = variables.entrySet().iterator().next();

			if (var.getValue().equals(Rational.ONE)) {
				mLogger.info("Construct x < c: " + term);
				return mMSODOperations.strictIneqAutomaton(mAutomataLibrarayServices, var.getKey(), constant);
			}

			if (var.getValue().equals(Rational.MONE)) {
				mLogger.info("Construct -x < c: " + term);
				return mMSODOperations.strictNegIneqAutomaton(mAutomataLibrarayServices, var.getKey(), constant);
			}
		}

		if (variables.size() == 2) {
			mLogger.info("Construct x-y < c: " + term);

			final Iterator<Entry<Term, Rational>> it = variables.entrySet().iterator();
			final Entry<Term, Rational> var1 = it.next();
			final Entry<Term, Rational> var2 = it.next();

			if (!var1.getValue().add(var2.getValue()).equals(Rational.ZERO)) {
				throw new IllegalArgumentException("Input is not difference logic.");
			}

			if (var1.getValue().equals(Rational.ONE)) {
				return mMSODOperations.strictIneqAutomaton(mAutomataLibrarayServices, var1.getKey(), var2.getKey(),
						constant);
			}

			if (var2.getValue().equals(Rational.ONE)) {
				return mMSODOperations.strictIneqAutomaton(mAutomataLibrarayServices, var2.getKey(), var1.getKey(),
						constant);
			}
		}

		throw new IllegalArgumentException("Invalid inequality");
	}

	/**
	 * Returns automaton that represents "X strictSubset Y".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processStrictSubset(final ApplicationTerm term) {
		mLogger.info("Construct X strictSubset Y: " + term);

		if (term.getParameters().length != 2) {
			throw new IllegalArgumentException("StrictSubset must have exactly two parameters.");
		}

		return mMSODOperations.strictSubsetAutomaton(mAutomataLibrarayServices, term.getParameters()[0],
				term.getParameters()[1]);
	}

	/**
	 * Returns automaton that represents "X subset Y".
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processSubset(final ApplicationTerm term) {
		mLogger.info("Construct X subset Y: " + term);

		if (term.getParameters().length != 2) {
			throw new IllegalArgumentException("Subset must have exactly two parameters.");
		}

		return mMSODOperations.subsetAutomaton(mAutomataLibrarayServices, term.getParameters()[0],
				term.getParameters()[1]);
	}

	/**
	 * Returns automaton that represents "t element X".
	 *
	 * @throws AutomataLibraryException
	 */
	private INestedWordAutomaton<MSODAlphabetSymbol, String> processElement(final ApplicationTerm term)
			throws AutomataLibraryException {
		if (term.getParameters().length != 2) {
			throw new IllegalArgumentException("Element must have exactly two parameters.");
		}

		final AffineTerm affineTerm = (AffineTerm) new AffineTermTransformer(this).transform(term.getParameters()[0]);

		if (affineTerm.isErrorTerm()) {
			throw new IllegalArgumentException("Could not create AffineTerm.");
		}

		final Map<Term, Rational> variables = affineTerm.getVariable2Coefficient();
		final Rational constant = affineTerm.getConstant();

		if (variables.size() == 0) {
			mLogger.info("Construct c element X: " + term);
			return mMSODOperations.constElementAutomaton(mAutomataLibrarayServices, constant, term.getParameters()[1]);
		}

		if (variables.size() == 1) {
			mLogger.info("Construct x+c element Y: " + term);
			final Entry<Term, Rational> variable = variables.entrySet().iterator().next();

			if (!variable.getValue().equals(Rational.ONE)) {
				throw new IllegalArgumentException("Invalid input.");
			}

			return mMSODOperations.elementAutomaton(mAutomataLibrarayServices, variable.getKey(), constant,
					term.getParameters()[1]);
		}

		throw new IllegalArgumentException("Invalid input.");
	}

	/**
	 * Returns a string representation of the given automaton. (only for debugging)
	 */
	private String automatonToString(final IAutomaton<?, ?> automaton, final Format format) {
		AutomatonDefinitionPrinter<?, ?> printer;
		printer = new AutomatonDefinitionPrinter<>(mAutomataLibrarayServices, "", Format.ATS, automaton);

		return printer.getDefinitionAsString();
	}
}
