/*
 * Copyright (C) 2019 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2019 University of Freiburg
 *
 * This file is part of the ULTIMATE ModelCheckerUtils Library.
 *
 * The ULTIMATE ModelCheckerUtils Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE ModelCheckerUtils Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE ModelCheckerUtils Library. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE ModelCheckerUtils Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE ModelCheckerUtils Library grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.linearterms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import de.uni_freiburg.informatik.ultimate.boogie.BoogieUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.SmtSortUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.SmtUtils;
import de.uni_freiburg.informatik.ultimate.logic.Rational;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.util.datastructures.SparseMapBuilder;

/**
 * Provides static auxiliary methods for {@link AffineTerm}s and {@link PolynomialTerm}s
 *
 * @author Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 */
public class PolynomialTermUtils {

	private PolynomialTermUtils() {
		// do not instantiate
	}

	/**
	 * Use modulo operation to bring Rational in the range of representable values.
	 *
	 * @param bv
	 *            Rational that represents a bitvector
	 * @param sort
	 *            bitvector sort
	 * @return bv % 2^sort.getIndices[0]
	 */
	public static Rational bringValueInRange(final Rational bv, final Sort sort) {
		assert SmtSortUtils.isBitvecSort(sort);
		assert sort.getIndices().length == 1;
		assert bv.isIntegral();
		final int bitsize = Integer.valueOf(sort.getIndices()[0]);
		final BigInteger bvBigInt = bv.numerator();
		final BigInteger numberOfValues = BigInteger.valueOf(2).pow(bitsize);
		final BigInteger resultBigInt = BoogieUtils.euclideanMod(bvBigInt, numberOfValues);
		return Rational.valueOf(resultBigInt, BigInteger.ONE);
	}
	
	/**
	 * Constructs an iterated simplified SMT-Term (func arg[0] arg[1]... arg[n]) as determined by the arguments.
	 */
	public static Term constructSimplifiedTerm(final String functionSymbol, final IPolynomialTerm[] polynomialArgs,
			final Script script) {
		//TODO Think about how the coefficient in the nominator can be extracted and write tests for this case.
		//Probably I only need to let AffineTerm handle the coefficients.
		//Change AffineTerm.divide to probably PolynomialTerm.divide too
		//Ask Matthias about what to do with a zero in the denominator. Make the whole denominator zero? Skip it?
		//At the moment this code skips it.
		final IPolynomialTerm[] flattenedTerm;
		if (functionSymbol == "/") {
			flattenedTerm = flattenRealDivision(polynomialArgs, script);
		}else if (functionSymbol == "div") {
			flattenedTerm = flattenIntDivision(polynomialArgs, script);
		}else {
			throw new UnsupportedOperationException("A simplification for this function has not been straightforwardly implemented."
													+ "Try using the PolynomialTermTransformer/AffineTermTransformer or SMTUtils.");
		}
		Term[] terms = new Term[flattenedTerm.length];
		for (int i = 0; i < flattenedTerm.length; i++) {
			terms[i] = flattenedTerm[i].toTerm(script);
		}
		return script.term(functionSymbol, terms);
	}
	
	/**
	 * Assuming the given array represents a division Term
	 * (see {@PolynomialTermTransformer#divide(Sort, IPolynomialTerm[])}),
	 * this method "flattens" this term, and returns the respective array.
	 * Example for "flattening" given at {@PolynomialTest#realDivisionLeftAssoc02()}.
	 */
	private static IPolynomialTerm[] flattenRealDivision(final IPolynomialTerm[] divArray, final Script script) {
		final ArrayList<IPolynomialTerm> denominatorConstants = new ArrayList<>();
		for (int i = 1; i < divArray.length ; i++) {
			if (divArray[i].isConstant() && !divArray[i].isZero()) {
				denominatorConstants.add(divArray[i]);
			}
		}
		
		final IPolynomialTerm newNominator;
		if (denominatorConstants.size() == 0) {
			return divArray;
		}else {
			newNominator = constructQuotientOfConstants(divArray[0], denominatorConstants, script);
		}
		
		return rearrangeRealDivision(newNominator, divArray);
	}
	
	private static IPolynomialTerm constructQuotientOfConstants(final IPolynomialTerm nominator, 
														 	    final ArrayList<IPolynomialTerm> denomConstants, 
														 	    final Script script) {
		final IPolynomialTerm[] allConstants = new IPolynomialTerm[denomConstants.size() + 1];
		allConstants[0] = nominator;
		Iterator<IPolynomialTerm> iter = denomConstants.iterator();
		for (int i = 1; iter.hasNext(); i++) {
			allConstants[i] = iter.next();
		}
		if (nominator.isAffine()) {
			return AffineTerm.divide(allConstants, script);
		}else {
			return PolynomialTerm.divide(allConstants, script);
		}
	}

	/**
	 * This method constructs a new divisionArray by placing the nominator at the first place and then extracting all
	 * constants out of the divArray
	 */
	private static IPolynomialTerm[] rearrangeRealDivision(final IPolynomialTerm nominator, 
													   final IPolynomialTerm[] oldDivArray) {
		final ArrayList<IPolynomialTerm> newDiv = new ArrayList<>();
		newDiv.add(nominator);
		for (int i = 1; i < oldDivArray.length ; i++) {
			if (!oldDivArray[i].isConstant()) {
				newDiv.add(oldDivArray[i]);
			}
		}
		IPolynomialTerm[] newDivArray = new IPolynomialTerm[newDiv.size()];
		newDiv.toArray(newDivArray);
		return newDivArray;
	}

	private static IPolynomialTerm[] flattenIntDivision(final IPolynomialTerm[] divArray, final Script script) {
		if (!divArray[0].isConstant()) {
			return divArray;
		}
		
		IPolynomialTerm newNominator = null;
		final IPolynomialTerm[] binaryDivision = new IPolynomialTerm[2];
		binaryDivision[0] = divArray[0];
		int endOfSimplification = 1;
		boolean stillSimplifiable = true;
		for (; stillSimplifiable && endOfSimplification < divArray.length; endOfSimplification++) {
			if (divArray[endOfSimplification].isConstant() && !divArray[endOfSimplification].isZero()) {
				binaryDivision[1] = divArray[endOfSimplification];
				//Use the real Division here, because the int Division would return a single Variable, if the quotient
				//would've been not integral, therefore we could not (properly) check for integrality afterwards.
				newNominator = AffineTerm.divide(binaryDivision, script);
				if (newNominator.isIntegral()) {
					binaryDivision[0] = newNominator;
				}else {
					newNominator = binaryDivision[0];
					stillSimplifiable = false;
					endOfSimplification--;
				}
			}else {
				stillSimplifiable = false;
				endOfSimplification--;
			}
		}
		
		if (newNominator == null) {
			return divArray;
		}
		return rearrangeIntDivision(newNominator, divArray, endOfSimplification);
	}

	/**
	 * This method constructs a new divisionArray by placing the nominator at the first place and then
	 * copying the oldDivArray from the given startIndex (included) until the end. 
	 */
	private static IPolynomialTerm[] rearrangeIntDivision(final IPolynomialTerm nominator, 
													      final IPolynomialTerm[] oldDivArray,
													      final int startIndex) {
		final ArrayList<IPolynomialTerm> newDiv = new ArrayList<>();
		newDiv.add(nominator);
		for (int i = startIndex; i < oldDivArray.length ; i++) {
			newDiv.add(oldDivArray[i]);
			
		}
		IPolynomialTerm[] newDivArray = new IPolynomialTerm[newDiv.size()];
		newDiv.toArray(newDivArray);
		return newDivArray;
	}

	/**
	 * Generalized method for applying Modulo to the coefficients and the constant of {@link AffineTerm}s and
	 * {@link PolynomialTerm}s. The type parameter T refers either to {@link AffineTerm} or {@link PolynomialTerm}. The
	 * type parameter MNL is a {@link Term} for {@link AffineTerm}s and a {@link Monomial} for {@link PolynomialTerm}s.
	 *
	 * @param term2map
	 *            {@link Function} that returns for a given T the Map<MNL,Rational> map.
	 * @param constructor
	 *            Methods that constructs the term of type T.
	 */
	public static <T extends AbstractGeneralizedAffineTerm<MNL>, MNL extends Term> T applyModuloToAllCoefficients(
			final T agAffineTerm, final BigInteger divident, final GeneralizedConstructor<MNL, T> constructor) {
		assert SmtSortUtils.isIntSort(agAffineTerm.getSort());
		final SparseMapBuilder<MNL, Rational> mapBuilder = new SparseMapBuilder<>();
		Rational newCoeff;
		for (final Entry<MNL, Rational> entry : agAffineTerm.getAbstractVariable2Coefficient().entrySet()) {
			newCoeff = SmtUtils.toRational(BoogieUtils.euclideanMod(SmtUtils.toInt(entry.getValue()), divident));
			if (newCoeff == Rational.ZERO) {
				continue;
			} else {
				mapBuilder.put(entry.getKey(), newCoeff);
			}
		}
		final Rational constant =
				SmtUtils.toRational(BoogieUtils.euclideanMod(SmtUtils.toInt(agAffineTerm.getConstant()), divident));
		return constructor.apply(agAffineTerm.getSort(), constant, mapBuilder.getBuiltMap());
	}

	/**
	 * Returns a shrinked version of a map if possible. Returns the given map otherwise.
	 * 
	 * @param <K>
	 */
	public static <K, V> Map<K, V> shrinkMap(final Map<K, V> map) {
		if (map.size() == 0) {
			return Collections.emptyMap();
		} else if (map.size() == 1) {
			final Entry<K, V> entry = map.entrySet().iterator().next();
			return Collections.singletonMap(entry.getKey(), entry.getValue());
		}
		return map;
	}

	/**
	 * It may occur, that the PolynomialTerm-class is used to represent a term, that could be represented by the
	 * AffineTerm-class. Hence, this method checks, whether the term given by the map could be represented by the
	 * AffineTerm-class.
	 */
	public static boolean isAffineMap(final Map<Monomial, Rational> map) {
		for (final Entry<Monomial, Rational> entry : map.entrySet()) {
			if (!entry.getKey().isLinear()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert a map in <Monomial, Rational> Form to an equivalent map in <Term, Rational> Form if possible.
	 */
	public static Map<Term, Rational> convertToAffineMap(final Map<Monomial, Rational> map) {
		final SparseMapBuilder<Term, Rational> mapBuilder = new SparseMapBuilder<>();
		for (final Entry<Monomial, Rational> entry : map.entrySet()) {
			final Map<Term, Rational> monomialMap = entry.getKey().getVariable2Exponent();
			assert monomialMap.size() == 1 : "Cannot convert to AffineMap.";
			final Term term = monomialMap.keySet().iterator().next();
			mapBuilder.put(term, entry.getValue());
		}
		return mapBuilder.getBuiltMap();
	}

	/**
	 * Generalized builder for sums of {@link AffineTerm}s and {@link PolynomialTerm}s. The type parameter T refers
	 * either to {@link AffineTerm} or {@link PolynomialTerm}. The type parameter MNL is a {@link Term} for
	 * {@link AffineTerm}s and a {@link Monomial} for {@link PolynomialTerm}s.
	 *
	 * @param term2map
	 *            {@link Function} that returns for a given T the Map<MNL,Rational> map.
	 * @param wrapper
	 *            {
	 * @param constructor
	 *            Methods that constructs the term of type T.
	 */
	static <T extends AbstractGeneralizedAffineTerm<?>, MNL extends Term> T constructSum(
			final Function<IPolynomialTerm, Map<MNL, Rational>> term2map,
			final GeneralizedConstructor<MNL, T> constructor, final IPolynomialTerm... summands) {
		final Sort sort = summands[0].getSort();
		final Map<MNL, Rational> variable2Coefficient = new HashMap<>();
		Rational constant = Rational.ZERO;
		for (final IPolynomialTerm term : summands) {
			for (final Map.Entry<MNL, Rational> summand : term2map.apply(term).entrySet()) {
				// assert summand.getKey().getSort() == mSort : "Sort mismatch: " +
				// summand.getKey().getSort() + " vs. "
				// + mSort;
				final Rational coeff = variable2Coefficient.get(summand.getKey());
				if (coeff == null) {
					variable2Coefficient.put(summand.getKey(), summand.getValue());
				} else {
					final Rational newCoeff;
					if (SmtSortUtils.isBitvecSort(sort)) {
						newCoeff = bringValueInRange(coeff.add(summand.getValue()), sort);
					} else {
						newCoeff = coeff.add(summand.getValue());
					}
					if (newCoeff.equals(Rational.ZERO)) {
						variable2Coefficient.remove(summand.getKey());
					} else {
						variable2Coefficient.put(summand.getKey(), newCoeff);
					}
				}
			}
			if (SmtSortUtils.isBitvecSort(sort)) {
				constant = bringValueInRange(constant.add(term.getConstant()), sort);
			} else {
				constant = constant.add(term.getConstant());
			}
		}
		return constructor.apply(sort, constant, variable2Coefficient);
	}

	/**
	 * Generalized builder for multiplication of a constant and either an {@link AffineTerm}s or a
	 * {@link PolynomialTerm}s. The type parameter T refers either to {@link AffineTerm} or {@link PolynomialTerm}. The
	 * type parameter MNL is a {@link Term} for {@link AffineTerm}s and a {@link Monomial} for {@link PolynomialTerm}s.
	 *
	 * @param term2map
	 *            {@link Function} that returns for a given T the Map<MNL,Rational> map.
	 * @param constructor
	 *            Methods that constructs the term of type T.
	 */
	static <T extends IPolynomialTerm, MNL extends Term> T constructMul(
			final Function<IPolynomialTerm, Map<MNL, Rational>> term2map,
			final GeneralizedConstructor<MNL, T> constructor, final IPolynomialTerm term, final Rational multiplier) {
		final Sort sort;
		final Rational constant;
		final Map<MNL, Rational> variable2Coefficient;
		if (multiplier.equals(Rational.ZERO)) {
			sort = term.getSort();
			constant = Rational.ZERO;
			variable2Coefficient = Collections.emptyMap();
		} else {
			variable2Coefficient = new HashMap<>();
			sort = term.getSort();
			if (SmtSortUtils.isBitvecSort(sort)) {
				constant = PolynomialTermUtils.bringValueInRange(term.getConstant().mul(multiplier), sort);
			} else {
				assert sort.isNumericSort();
				constant = term.getConstant().mul(multiplier);
			}
			for (final Map.Entry<MNL, Rational> summand : term2map.apply(term).entrySet()) {
				variable2Coefficient.put(summand.getKey(), summand.getValue().mul(multiplier));
			}
		}
		return constructor.apply(sort, constant, variable2Coefficient);
	}

	@FunctionalInterface
	public interface GeneralizedConstructor<V, T> {
		public T apply(Sort sort, Rational constant, Map<V, Rational> map);
	}
	
	@FunctionalInterface
	public interface TriFunction<T, U, R, S> {
		public S apply(T script, U sort, R term);
	}
}
