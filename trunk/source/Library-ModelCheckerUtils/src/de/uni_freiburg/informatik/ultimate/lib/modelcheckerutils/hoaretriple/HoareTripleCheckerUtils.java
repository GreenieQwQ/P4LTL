/*
 * Copyright (C) 2021 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2021 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.hoaretriple;

import java.util.function.Predicate;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.ModelCheckerUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IAction;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SubtermPropertyChecker;
import de.uni_freiburg.informatik.ultimate.logic.QuantifiedFormula;

/**
 * Provides helper methods for {@link IHoareTripleChecker}s.
 *
 * @author Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 *
 */
public final class HoareTripleCheckerUtils {

	private static final boolean REVIEW_SMT_RESULTS_IF_ASSERTIONS_ENABLED = true;
	private static final boolean REVIEW_SD_RESULTS_IF_ASSERTIONS_ENABLED = true;

	private HoareTripleCheckerUtils() {
		// do not instantiate utility class
	}

	public static ChainingHoareTripleChecker constructEfficientHoareTripleChecker(
			final IUltimateServiceProvider services, final HoareTripleChecks hoareTripleChecks,
			final CfgSmtToolkit csToolkit, final IPredicateUnifier predicateUnifier) throws AssertionError {
		final ILogger logger = services.getLoggingService().getLogger(HoareTripleCheckerUtils.class);
		final IHoareTripleChecker reviewHtc =
				constructSmtHoareTripleChecker(services, HoareTripleChecks.MONOLITHIC, csToolkit);
		final IHoareTripleChecker solverHtc = constructSmtHoareTripleChecker(services, hoareTripleChecks, csToolkit);

		ChainingHoareTripleChecker rtr = ChainingHoareTripleChecker.with(logger,
				new SdHoareTripleChecker(csToolkit, predicateUnifier, solverHtc.getEdgeCheckerBenchmark()));
		if (REVIEW_SD_RESULTS_IF_ASSERTIONS_ENABLED) {
			rtr = rtr.reviewWith(reviewHtc);
		}

		rtr = rtr.andThen(solverHtc);
		// protect against quantified transition formulas and intricate predicates
		final SubtermPropertyChecker quantifierFinder = new SubtermPropertyChecker(QuantifiedFormula.class::isInstance);
		final Predicate<IPredicate> noIntricateNoQuantifier = p -> predicateUnifier.isIntricatePredicate(p)
				|| quantifierFinder.isSatisfiedBySomeSubterm(p.getFormula());
		final Predicate<IAction> noQuantifier =
				a -> quantifierFinder.isSatisfiedBySomeSubterm(a.getTransformula().getFormula());
		rtr = rtr.predicatesProtectedBy(noIntricateNoQuantifier).actionsProtectedBy(noQuantifier);

		if (REVIEW_SMT_RESULTS_IF_ASSERTIONS_ENABLED) {
			rtr = rtr.reviewWith(reviewHtc);
		}

		return rtr;
	}

	public static IHoareTripleChecker constructSmtHoareTripleChecker(final IUltimateServiceProvider services,
			final HoareTripleChecks hoareTripleChecks, final CfgSmtToolkit csToolkit) throws AssertionError {
		final IHoareTripleChecker solverHtc;
		switch (hoareTripleChecks) {
		case MONOLITHIC:
			solverHtc = new MonolithicHoareTripleChecker(csToolkit);
			break;
		case INCREMENTAL:
			solverHtc = new IncrementalHoareTripleChecker(csToolkit, false,
					services.getLoggingService().getLogger(ModelCheckerUtils.class));
			break;
		default:
			throw new AssertionError("unknown value");
		}
		return solverHtc;
	}

	public static IHoareTripleChecker constructEfficientHoareTripleCheckerWithCaching(
			final IUltimateServiceProvider services, final HoareTripleChecks hoareTripleChecks,
			final CfgSmtToolkit csToolkit, final IPredicateUnifier predicateUnifier) throws AssertionError {
		final IHoareTripleChecker ehtc =
				constructEfficientHoareTripleChecker(services, hoareTripleChecks, csToolkit, predicateUnifier);
		return new CachingHoareTripleCheckerMap(services, ehtc, predicateUnifier);
	}

	/**
	 * Hoare triple check mode.
	 */
	public enum HoareTripleChecks {
		MONOLITHIC, INCREMENTAL
	}

}
