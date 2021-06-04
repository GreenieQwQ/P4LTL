/*
 * Copyright (C) 2015 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2021 Dominik Klumpp (klumpp@informatik.uni-freiburg.de)
 * Copyright (C) 2015-2021 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INwaOutgoingLetterAndTransitionProvider;
import de.uni_freiburg.informatik.ultimate.boogie.ast.BoogieASTNode;
import de.uni_freiburg.informatik.ultimate.core.lib.exceptions.IRunningTaskStackProvider;
import de.uni_freiburg.informatik.ultimate.core.lib.models.annotation.Check;
import de.uni_freiburg.informatik.ultimate.core.lib.models.annotation.Check.Spec;
import de.uni_freiburg.informatik.ultimate.core.lib.models.annotation.WitnessInvariant;
import de.uni_freiburg.informatik.ultimate.core.lib.results.AllSpecificationsHoldResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.CounterExampleResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.InvariantResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.PositiveResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.ProcedureContractResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.StatisticsResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.TimeoutResultAtElement;
import de.uni_freiburg.informatik.ultimate.core.lib.results.UnprovabilityReason;
import de.uni_freiburg.informatik.ultimate.core.lib.results.UnprovableResult;
import de.uni_freiburg.informatik.ultimate.core.lib.results.UserSpecifiedLimitReachedResultAtElement;
import de.uni_freiburg.informatik.ultimate.core.model.models.IElement;
import de.uni_freiburg.informatik.ultimate.core.model.models.ILocation;
import de.uni_freiburg.informatik.ultimate.core.model.results.IResult;
import de.uni_freiburg.informatik.ultimate.core.model.services.IBacktranslationService;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IProgressMonitorService;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.core.model.translation.IProgramExecution;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.IcfgPetrifier;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.IcfgPetrifier.IcfgConstructionMode;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.IcfgUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfg;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgElement;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgForkTransitionThreadCurrent;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdge;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.debugidentifiers.DebugIdentifier;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.debugidentifiers.ProcedureErrorDebugIdentifier;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.debugidentifiers.ProcedureErrorDebugIdentifier.ProcedureErrorType;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.transformations.BlockEncodingBacktranslator;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.HoareAnnotation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.IPredicateUnifier;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.smt.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgLocation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.util.IcfgAngelicProgramExecution;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.AbstractCegarLoop.Result;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.interpolantautomata.transitionappender.AbstractInterpolantAutomaton;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.petrinetlbe.PetriNetLargeBlockEncoding.IPLBECompositionFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.HoareAnnotationChecker;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TraceAbstractionPreferenceInitializer.FloydHoareAutomataReuse;
import de.uni_freiburg.informatik.ultimate.util.datastructures.DataStructureUtils;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;
import de.uni_freiburg.informatik.ultimate.witnessparser.graph.WitnessEdge;
import de.uni_freiburg.informatik.ultimate.witnessparser.graph.WitnessNode;

public class TraceAbstractionStarter<L extends IIcfgTransition<?>> {

	public static final String ULTIMATE_INIT = "ULTIMATE.init";
	public static final String ULTIMATE_START = "ULTIMATE.start";

	private static final long MILLISECONDS_PER_SECOND = 1000L;

	private final ILogger mLogger;
	private final IUltimateServiceProvider mServices;
	private final TAPreferences mPrefs;
	private final boolean mComputeHoareAnnotation;
	private final boolean mIsConcurrent;
	private final INwaOutgoingLetterAndTransitionProvider<WitnessEdge, WitnessNode> mWitnessAutomaton;

	/**
	 * Root Node of this Ultimate model. I use this to store information that should be passed to the next plugin. The
	 * Successors of this node exactly the initial nodes of procedures.
	 */
	private IElement mRootOfNewModel;
	private Result mOverallResult;
	private IElement mArtifact;

	private final List<INestedWordAutomaton<String, String>> mRawFloydHoareAutomataFromFile;
	private final List<Pair<AbstractInterpolantAutomaton<L>, IPredicateUnifier>> mFloydHoareAutomataFromErrorLocations =
			new ArrayList<>();

	private final Class<L> mTransitionClazz;
	private final IPLBECompositionFactory<L> mCompositionFactory;

	private Map<IcfgLocation, IcfgLocation> mLocationMap;
	private final Map<IcfgLocation, IResult> mResultsPerLocation = new LinkedHashMap<>();

	// list has one entry per analysis restart with increased number of threads (only 1 entry if sequential)
	private final Map<DebugIdentifier, List<TraceAbstractionBenchmarks>> mStatistics = new LinkedHashMap<>();

	public TraceAbstractionStarter(final IUltimateServiceProvider services, final IIcfg<IcfgLocation> icfg,
			final INwaOutgoingLetterAndTransitionProvider<WitnessEdge, WitnessNode> witnessAutomaton,
			final List<INestedWordAutomaton<String, String>> rawFloydHoareAutomataFromFile,
			final IPLBECompositionFactory<L> compositionFactory, final Class<L> transitionClazz) {
		mServices = services;
		mLogger = mServices.getLoggingService().getLogger(Activator.PLUGIN_ID);
		mTransitionClazz = transitionClazz;
		mCompositionFactory = compositionFactory;
		mPrefs = new TAPreferences(mServices);
		mWitnessAutomaton = witnessAutomaton;
		mRawFloydHoareAutomataFromFile = rawFloydHoareAutomataFromFile;
		mIsConcurrent = isConcurrent(icfg);

		if (mPrefs.computeHoareAnnotation() && mIsConcurrent) {
			mLogger.warn("Switching off computation of Hoare annotation because input is a concurrent program");
			mComputeHoareAnnotation = false;
		} else {
			mComputeHoareAnnotation = mPrefs.computeHoareAnnotation();
		}

		runCegarLoops(icfg);
	}

	private void runCegarLoops(final IIcfg<IcfgLocation> icfg) {
		logSettings();

		final Collection<IcfgLocation> errNodesOfAllProc = IcfgUtils.getErrorLocations(icfg);
		final int numberOfErrorLocs = errNodesOfAllProc.size();
		mLogger.info("Applying trace abstraction to program that has " + numberOfErrorLocs + " error locations.");

		mOverallResult = Result.SAFE;
		mArtifact = null;
		if (isConcurrent(icfg)) {
			analyseConcurrentProgram(icfg);
		} else {
			analyseSequentialProgram(icfg);
		}

		// Report results that were buffered because they may be overridden or amended.
		reportLocationResults();
		reportBenchmarkResults();

		logNumberOfWitnessInvariants(errNodesOfAllProc);
		if (mOverallResult == Result.SAFE) {
			final AllSpecificationsHoldResult result = AllSpecificationsHoldResult
					.createAllSpecificationsHoldResult(Activator.PLUGIN_NAME, numberOfErrorLocs);
			reportResult(result);
		}

		final IProgressMonitorService progmon = mServices.getProgressMonitorService();
		if (mComputeHoareAnnotation && mOverallResult != Result.TIMEOUT
				&& !Result.USER_LIMIT_RESULTS.contains(mOverallResult) && progmon.continueProcessing()) {
			final IBacktranslationService backTranslatorService = mServices.getBacktranslationService();
			createInvariantResults(icfg, backTranslatorService);
			createProcedureContractResults(icfg, backTranslatorService);
		}

		switch (mOverallResult) {
		case SAFE:
		case UNSAFE:
			break;
		case TIMEOUT:
			mLogger.warn("Timeout");
			break;
		case UNKNOWN:
			mLogger.warn("Unable to decide correctness. Please check the following counterexample manually.");
			break;
		default:
			throw new UnsupportedOperationException("Unknown overall result " + mOverallResult);
		}

		mRootOfNewModel = mArtifact;
	}

	private void logSettings() {
		String settings = "Automizer settings:";
		settings += " Hoare:" + mComputeHoareAnnotation;
		settings += " " + (mPrefs.differenceSenwa() ? "SeNWA" : "NWA");
		settings += " Interpolation:" + mPrefs.interpolation();
		settings += " Determinization: " + mPrefs.interpolantAutomatonEnhancement();
		mLogger.info(settings);
	}

	/**
	 * Analyses a concurrent program and detects if thread instances are insufficient. If so, the number of thread
	 * instances is increased and the analysis restarts.
	 *
	 * @param icfg
	 *            The CFG for the program (unpetrified).
	 */
	private void analyseConcurrentProgram(final IIcfg<IcfgLocation> icfg) {
		if (icfg.getInitialNodes().size() > 1) {
			throw new UnsupportedOperationException("Library mode is not supported for concurrent programs. "
					+ "There must be a unique entry procedure.");
		}

		int numberOfThreadInstances = 1;
		while (true) {
			final IIcfg<IcfgLocation> petrifiedIcfg = petrify(icfg, numberOfThreadInstances);
			bumpBenchmarkResults(petrifiedIcfg);
			mResultsPerLocation.clear();

			final var results = analyseProgram(petrifiedIcfg, TraceAbstractionStarter::hasSufficientThreadInstances);
			// Stop if either every in-use error location is unreachable or any other error locations is reachable
			if (resultsHaveSufficientInstances(results)) {
				break;
			}
			assert isConcurrent(icfg) : "Insufficient thread instances for sequential program";
			mLogger.warn(numberOfThreadInstances
					+ " thread instances were not sufficient, I will increase this number and restart the analysis");
			numberOfThreadInstances++;
		}
		mLogger.info("Analysis of concurrent program completed with " + numberOfThreadInstances + " thread instances");
	}

	private static <L extends IIcfgTransition<?>> boolean
			resultsHaveSufficientInstances(final List<CegarLoopResult<L>> results) {
		boolean res = true;
		for (final CegarLoopResult<L> r : results) {
			if (r.getOverallResult() != Result.UNSAFE) {
				continue;
			}
			if (hasSufficientThreadInstances(r)) {
				return true;
			}
			res = false;
		}
		return res;
	}

	/**
	 * Analyses a sequential program (no special handling for threads is needed).
	 *
	 * @param icfg
	 *            The CFG for the program
	 */
	private void analyseSequentialProgram(final IIcfg<IcfgLocation> icfg) {
		analyseProgram(icfg, x -> true);
	}

	/**
	 * Helper method that runs one or more CEGAR loops (depending on settings, e.g. all at once or per assertion) to
	 * analyse a program. Results from all analyses are collected and returned.
	 *
	 * @param icfg
	 *            The CFG for the program
	 * @param continueAnalysis
	 *            A predicate that returns false if the analysis should be interrupted (and the results so far should be
	 *            returned).
	 * @return the collection of all analysis results, in order
	 */
	private List<CegarLoopResult<L>> analyseProgram(final IIcfg<IcfgLocation> icfg,
			final Predicate<CegarLoopResult<L>> continueAnalysis) {
		final List<CegarLoopResult<L>> results = new ArrayList<>();

		final List<Pair<DebugIdentifier, Set<IcfgLocation>>> errorPartitions = partitionErrorLocations(icfg);
		final boolean multiplePartitions = errorPartitions.size() > 1;

		final IProgressMonitorService progmon = mServices.getProgressMonitorService();
		int finishedErrorSets = 0;
		for (final Pair<DebugIdentifier, Set<IcfgLocation>> partition : errorPartitions) {
			final DebugIdentifier name = partition.getKey();
			final Set<IcfgLocation> errorLocs = partition.getValue();

			if (mPrefs.hasLimitAnalysisTime()) {
				progmon.addChildTimer(
						progmon.getTimer(mPrefs.getLimitAnalysisTime() * MILLISECONDS_PER_SECOND * errorLocs.size()));
			}
			if (multiplePartitions) {
				mServices.getProgressMonitorService().setSubtask(name.toString());
			}
			final TraceAbstractionBenchmarks traceAbstractionBenchmark = getBenchmark(name, icfg);

			final CegarLoopResult<L> clres = executeCegarLoop(name, icfg, traceAbstractionBenchmark, errorLocs);
			results.add(clres);
			finishedErrorSets++;

			if (mPrefs.hasLimitAnalysisTime()) {
				progmon.removeChildTimer();
			}
			if (multiplePartitions) {
				mLogger.info(String.format("Result for error location %s was %s (%s/%s)", name,
						clres.getOverallResult(), finishedErrorSets, errorPartitions.size()));
			}
			if (!continueAnalysis.test(clres)) {
				break;
			}

			if (mPrefs.getFloydHoareAutomataReuse() != FloydHoareAutomataReuse.NONE) {
				mFloydHoareAutomataFromErrorLocations.addAll(clres.getFloydHoareAutomata());
			}
			mOverallResult = reportResults(errorLocs, clres);
			mArtifact = clres.getArtifact();

			if (mPrefs.allErrorLocsAtOnce() && mOverallResult == Result.UNSAFE) {
				break;
			}
		}

		return results;
	}

	/**
	 * Partitions the error locations of a CFG. Each partition shall be analysed separately, in the order in which they
	 * are returned.
	 *
	 * Currently, 2 modes are supported: all error locations at once, or each error location separately. In the future,
	 * other modes might be added.
	 *
	 * Don't forget to update {@link #getBenchmarkDescription(DebugIdentifier)} if more modes are implemented!
	 *
	 * @param icfg
	 *            The CFG whose error locations shall be partitioned.
	 * @return A partition of the error locations, each set annotated with a debug identifier
	 */
	private List<Pair<DebugIdentifier, Set<IcfgLocation>>> partitionErrorLocations(final IIcfg<IcfgLocation> icfg) {
		// TODO (Dominik 2021-04-29) Support other mode: group by thread
		// TODO (Dominik 2021-04-29) Support other mode: group by original (i.e. all copies of a location together)

		final Set<IcfgLocation> errNodesOfAllProc = IcfgUtils.getErrorLocations(icfg);
		if (!mPrefs.allErrorLocsAtOnce()) {
			Stream<IcfgLocation> errorLocs = errNodesOfAllProc.stream();
			if (mPrefs.insufficientThreadErrorsLast()) {
				// Sort the errorLocs by their type, i.e. isInsufficientThreadsLocations last
				errorLocs = errorLocs.sorted(
						(x, y) -> Boolean.compare(isInsufficientThreadsLocation(x), isInsufficientThreadsLocation(y)));
			}
			return errorLocs.map(x -> new Pair<>(x.getDebugIdentifier(), Set.of(x))).collect(Collectors.toList());
		}
		if (mPrefs.insufficientThreadErrorsLast() && isAnyForkInCycle(icfg)) {
			final Set<IcfgLocation> inUseErrors = new HashSet<>(getInUseErrorNodeMap(icfg).values());
			final Set<IcfgLocation> otherErrors = DataStructureUtils.difference(errNodesOfAllProc, inUseErrors);
			return List.of(new Pair<>(AllErrorsAtOnceDebugIdentifier.INSTANCE, otherErrors),
					new Pair<>(InUseDebugIdentifier.INSTANCE, inUseErrors));
		}
		return List.of(new Pair<>(AllErrorsAtOnceDebugIdentifier.INSTANCE, errNodesOfAllProc));
	}

	private CegarLoopResult<L> executeCegarLoop(final DebugIdentifier name, final IIcfg<IcfgLocation> icfg,
			final TraceAbstractionBenchmarks taBenchmark, final Collection<IcfgLocation> errorLocs) {
		final CegarLoopResult<L> clres = CegarLoopUtils.getCegarLoopResult(mServices, name, icfg, mPrefs,
				getPredicateFactory(icfg), errorLocs, mWitnessAutomaton, mRawFloydHoareAutomataFromFile,
				mComputeHoareAnnotation, mPrefs.getConcurrency(), mCompositionFactory, mTransitionClazz);
		taBenchmark.aggregateBenchmarkData(clres.getCegarLoopStatisticsGenerator());
		return clres;
	}

	private static boolean isAnyForkInCycle(final IIcfg<IcfgLocation> icfg) {
		final Map<String, IcfgLocation> entryLocs = icfg.getProcedureEntryNodes();
		for (final var fork : getInUseErrorNodeMap(icfg).keySet()) {
			final ArrayDeque<IcfgLocation> queue = new ArrayDeque<>();
			final Set<IcfgLocation> visited = new HashSet<>();
			queue.add(fork.getTarget());
			queue.add(entryLocs.get(fork.getNameOfForkedProcedure()));
			while (!queue.isEmpty()) {
				final IcfgLocation loc = queue.pop();
				if (!visited.add(loc)) {
					continue;
				}
				if (loc.equals(fork.getSource())) {
					return true;
				}
				for (final IcfgEdge edge : loc.getOutgoingEdges()) {
					queue.add(edge.getTarget());
					if (edge instanceof IIcfgForkTransitionThreadCurrent<?>) {
						final var fork2 = (IIcfgForkTransitionThreadCurrent<?>) edge;
						queue.add(entryLocs.get(fork2.getNameOfForkedProcedure()));
					}
				}
			}
		}
		return false;
	}

	private static Map<IIcfgForkTransitionThreadCurrent<IcfgLocation>, IcfgLocation>
			getInUseErrorNodeMap(final IIcfg<?> icfg) {
		return icfg.getCfgSmtToolkit().getConcurrencyInformation().getInUseErrorNodeMap();
	}

	private void createInvariantResults(final IIcfg<IcfgLocation> icfg,
			final IBacktranslationService backTranslatorService) {
		final CfgSmtToolkit csToolkit = icfg.getCfgSmtToolkit();
		assert new HoareAnnotationChecker(mServices, icfg, csToolkit).isInductive() : "incorrect Hoare annotation";

		final Term trueterm = csToolkit.getManagedScript().getScript().term("true");

		final Set<IcfgLocation> locsForLoopLocations = new HashSet<>();

		locsForLoopLocations.addAll(IcfgUtils.getPotentialCycleProgramPoints(icfg));
		locsForLoopLocations.addAll(icfg.getLoopLocations());
		// find all locations that have outgoing edges which are annotated with LoopEntry, i.e., all loop candidates

		for (final IcfgLocation locNode : locsForLoopLocations) {
			final HoareAnnotation hoare = HoareAnnotation.getAnnotation(locNode);
			if (hoare == null) {
				continue;
			}
			final Term formula = hoare.getFormula();
			final InvariantResult<IIcfgElement, Term> invResult =
					new InvariantResult<>(Activator.PLUGIN_NAME, locNode, backTranslatorService, formula);
			reportResult(invResult);

			if (formula.equals(trueterm)) {
				continue;
			}
			final String inv = backTranslatorService.translateExpressionToString(formula, Term.class);
			new WitnessInvariant(inv).annotate(locNode);
		}
	}

	private void createProcedureContractResults(final IIcfg<IcfgLocation> icfg,
			final IBacktranslationService backTranslatorService) {
		final Map<String, IcfgLocation> finalNodes = icfg.getProcedureExitNodes();
		for (final Entry<String, IcfgLocation> proc : finalNodes.entrySet()) {
			final String procName = proc.getKey();
			if (isAuxilliaryProcedure(procName)) {
				continue;
			}
			final IcfgLocation finalNode = proc.getValue();
			final HoareAnnotation hoare = HoareAnnotation.getAnnotation(finalNode);
			if (hoare != null) {
				final Term formula = hoare.getFormula();
				final ProcedureContractResult<IIcfgElement, Term> result = new ProcedureContractResult<>(
						Activator.PLUGIN_NAME, finalNode, backTranslatorService, procName, formula);

				reportResult(result);
				// TODO: Add setting that controls the generation of those witness invariants
			}
		}
	}

	private void logNumberOfWitnessInvariants(final Collection<IcfgLocation> errNodesOfAllProc) {
		int numberOfCheckedInvariants = 0;
		for (final IcfgLocation err : errNodesOfAllProc) {
			if (!(err instanceof BoogieIcfgLocation)) {
				mLogger.info("Did not count any witness invariants because Icfg is not BoogieIcfg");
				return;
			}
			final BoogieASTNode boogieASTNode = ((BoogieIcfgLocation) err).getBoogieASTNode();
			final Check check = Check.getAnnotation(boogieASTNode);
			if (check != null && check.getSpec().contains(Spec.WITNESS_INVARIANT)) {
				numberOfCheckedInvariants++;
			}
		}
		if (numberOfCheckedInvariants > 0) {
			mLogger.info("Automizer considered " + numberOfCheckedInvariants + " witness invariants");
			mLogger.info("WitnessConsidered=" + numberOfCheckedInvariants);
		}
	}

	private static <L extends IIcfgTransition<?>> boolean hasSufficientThreadInstances(final CegarLoopResult<L> clres) {
		if (clres.getOverallResult() != Result.UNSAFE) {
			return true;
		}
		return !isInsufficientThreadsLocation(getErrorPP(clres.getProgramExecution()));
	}

	private static boolean isInsufficientThreadsLocation(final IcfgLocation loc) {
		final Check check = Check.getAnnotation(loc);
		return check != null && check.getSpec().contains(Spec.SUFFICIENT_THREAD_INSTANCES);
	}

	private static boolean isInsufficientThreadsIdentifier(final DebugIdentifier ident) {
		if (ident instanceof ProcedureErrorDebugIdentifier) {
			return ((ProcedureErrorDebugIdentifier) ident).getType() == ProcedureErrorType.INUSE_VIOLATION;
		}
		return false;
	}

	private static boolean isConcurrent(final IIcfg<IcfgLocation> icfg) {
		return !icfg.getCfgSmtToolkit().getConcurrencyInformation().getThreadInstanceMap().isEmpty();
	}

	private IIcfg<IcfgLocation> petrify(final IIcfg<IcfgLocation> icfg, final int numberOfThreadInstances) {
		assert isConcurrent(icfg) : "Petrification unnecessary for sequential programs";

		mLogger.info("Constructing petrified ICFG for " + numberOfThreadInstances + " thread instances.");
		final IcfgPetrifier icfgPetrifier = new IcfgPetrifier(mServices, icfg,
				IcfgConstructionMode.ASSUME_THREAD_INSTANCE_SUFFICIENCY, numberOfThreadInstances);
		final IIcfg<IcfgLocation> petrifiedIcfg = icfgPetrifier.getPetrifiedIcfg();
		mLocationMap = ((BlockEncodingBacktranslator) icfgPetrifier.getBacktranslator()).getLocationMapping();
		mServices.getBacktranslationService().addTranslator(icfgPetrifier.getBacktranslator());
		return petrifiedIcfg;
	}

	private IcfgLocation getOriginalLocation(final IcfgLocation loc) {
		if (mLocationMap == null) {
			return loc;
		}
		return mLocationMap.getOrDefault(loc, loc);
	}

	private PredicateFactory getPredicateFactory(final IIcfg<IcfgLocation> icfg) {
		final CfgSmtToolkit csToolkit = icfg.getCfgSmtToolkit();
		return new PredicateFactory(mServices, csToolkit.getManagedScript(), csToolkit.getSymbolTable());
	}

	private Result reportResults(final Collection<IcfgLocation> errorLocs, final CegarLoopResult<L> clres) {
		final Result result = clres.getOverallResult();
		switch (result) {
		case SAFE:
			reportPositiveResults(errorLocs);
			return mOverallResult;
		case UNSAFE:
			reportCounterexampleResult(clres.getProgramExecution());
			return result;
		case TIMEOUT:
		case USER_LIMIT_ITERATIONS:
		case USER_LIMIT_PATH_PROGRAM:
		case USER_LIMIT_TIME:
		case USER_LIMIT_TRACEHISTOGRAM:
			reportLimitResult(result, errorLocs, clres.getRunningTaskStackProvider());
			return mOverallResult != Result.UNSAFE ? result : mOverallResult;
		case UNKNOWN:
			final IProgramExecution<L, Term> pe = clres.getProgramExecution();
			reportUnproveableResult(pe, clres.getUnprovabilityReasons());
			return mOverallResult != Result.UNSAFE ? result : mOverallResult;
		default:
			throw new IllegalArgumentException();
		}
	}

	private void reportPositiveResults(final Collection<IcfgLocation> errorLocs) {
		for (final IcfgLocation errorLoc : errorLocs) {
			final PositiveResult<IIcfgElement> pResult =
					new PositiveResult<>(Activator.PLUGIN_NAME, errorLoc, mServices.getBacktranslationService());
			recordLocationResult(errorLoc, pResult);
		}
	}

	private void reportCounterexampleResult(final IProgramExecution<L, Term> pe) {
		final List<UnprovabilityReason> upreasons = UnprovabilityReason.getUnprovabilityReasons(pe);
		if (!upreasons.isEmpty()) {
			reportUnproveableResult(pe, upreasons);
			return;
		}
		if (isAngelicallySafe(pe)) {
			mLogger.info("Ignoring angelically safe counterexample");
			return;
		}
		final IcfgLocation errorLoc = getErrorPP(pe);
		recordLocationResult(errorLoc,
				new CounterExampleResult<>(errorLoc, Activator.PLUGIN_NAME, mServices.getBacktranslationService(), pe));
	}

	private static <L extends IIcfgTransition<?>> boolean isAngelicallySafe(final IProgramExecution<L, Term> pe) {
		if (pe instanceof IcfgAngelicProgramExecution) {
			return !((IcfgAngelicProgramExecution<L>) pe).getAngelicStatus();
		}
		return false;
	}

	private void reportLimitResult(final Result result, final Collection<IcfgLocation> errorLocs,
			final IRunningTaskStackProvider rtsp) {
		for (final IcfgLocation errorIpp : errorLocs) {
			final IResult res = constructLimitResult(mServices, result, rtsp, errorIpp);
			recordLocationResult(errorIpp, res);
		}
	}

	public static IResult constructLimitResult(final IUltimateServiceProvider services, final Result result,
			final IRunningTaskStackProvider rtsp, final IcfgLocation errorIpp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Unable to prove that ");
		sb.append(Check.getAnnotation(errorIpp).getPositiveMessage());
		if (errorIpp instanceof BoogieIcfgLocation) {
			final ILocation origin = ((BoogieIcfgLocation) errorIpp).getBoogieASTNode().getLocation();
			sb.append(" (line ").append(origin.getStartLine()).append(").");
		}
		if (rtsp != null) {
			sb.append(" Cancelled ").append(rtsp.printRunningTaskMessage());
		}

		final IResult res;
		if (result == Result.TIMEOUT) {
			res = new TimeoutResultAtElement<>(errorIpp, Activator.PLUGIN_NAME, services.getBacktranslationService(),
					sb.toString());
		} else {
			res = new UserSpecifiedLimitReachedResultAtElement<IElement>(result.toString(), errorIpp,
					Activator.PLUGIN_NAME, services.getBacktranslationService(), sb.toString());
		}
		return res;
	}

	private void reportUnproveableResult(final IProgramExecution<L, Term> pe,
			final List<UnprovabilityReason> unproabilityReasons) {
		final IcfgLocation errorPP = getErrorPP(pe);
		reportResult(new UnprovableResult<>(Activator.PLUGIN_NAME, errorPP, mServices.getBacktranslationService(), pe,
				unproabilityReasons));
	}

	private TraceAbstractionBenchmarks getBenchmark(final DebugIdentifier ident, final IIcfg<IcfgLocation> icfg) {
		final List<TraceAbstractionBenchmarks> benchmarks = mStatistics.computeIfAbsent(ident, x -> new ArrayList<>());
		if (benchmarks.isEmpty()) {
			final TraceAbstractionBenchmarks bench = new TraceAbstractionBenchmarks(icfg);
			benchmarks.add(bench);
			return bench;
		}
		return benchmarks.get(benchmarks.size() - 1);
	}

	private void bumpBenchmarkResults(final IIcfg<IcfgLocation> icfg) {
		for (final List<TraceAbstractionBenchmarks> allStats : mStatistics.values()) {
			allStats.add(new TraceAbstractionBenchmarks(icfg));
		}
	}

	private void reportBenchmarkResults() {
		for (final Map.Entry<DebugIdentifier, List<TraceAbstractionBenchmarks>> entry : mStatistics.entrySet()) {
			final DebugIdentifier ident = entry.getKey();

			int i = 1;
			for (final TraceAbstractionBenchmarks benchmark : entry.getValue()) {
				final String shortDescription = getBenchmarkDescription(ident, i);
				reportResult(new StatisticsResult<>(Activator.PLUGIN_NAME, shortDescription, benchmark));
				i++;
			}
		}
	}

	private String getBenchmarkDescription(final DebugIdentifier ident, final int numThreads) {
		final String description;
		if (ident instanceof AllErrorsAtOnceDebugIdentifier) {
			description = "Ultimate Automizer benchmark data";
		} else if (ident instanceof InUseDebugIdentifier) {
			description = "Ultimate Automizer benchmark data for thread instance sufficiency";
		} else if (isInsufficientThreadsIdentifier(ident)) {
			description = "Ultimate Automizer benchmark data for thread instance sufficiency: " + ident;
		} else {
			description = "Ultimate Automizer benchmark data for error location: " + ident;
		}

		if (mIsConcurrent) {
			return description + " with " + numThreads + " thread instances";
		}
		return description;
	}

	private static boolean isAuxilliaryProcedure(final String proc) {
		return ULTIMATE_INIT.equals(proc) || ULTIMATE_START.equals(proc);
	}

	private void recordLocationResult(final IcfgLocation loc, final IResult res) {
		final IcfgLocation original = getOriginalLocation(loc);
		final IResult old = mResultsPerLocation.get(original);
		if (old == null) {
			mResultsPerLocation.put(original, res);
		} else {
			mResultsPerLocation.put(original, combineLocationResults(old, res));
		}
	}

	/**
	 * Combines results for multiple petrification-created copies of a location, for a fixed number of thread instances.
	 *
	 * @param oldResult
	 *            The first computed result
	 * @param newResult
	 *            A new result computed for another copy of the location
	 * @return the combined result
	 */
	private static IResult combineLocationResults(final IResult oldResult, final IResult newResult) {
		if (newResult instanceof CounterExampleResult<?, ?, ?>) {
			return newResult;
		}
		if (oldResult instanceof TimeoutResultAtElement<?>
				|| oldResult instanceof UserSpecifiedLimitReachedResultAtElement<?>
				|| oldResult instanceof CounterExampleResult<?, ?, ?>) {
			return oldResult;
		}
		assert oldResult instanceof PositiveResult<?> : "Unsupported location-specific result: " + oldResult;
		return newResult;
	}

	private void reportLocationResults() {
		// Determine if we were unable to prove thread instance sufficiency. This could e.g. be due to a counterexample,
		// a timeout, or a unprovable trace.
		final boolean couldBeInsufficient =
				mResultsPerLocation.entrySet().stream().anyMatch(entry -> isInsufficientThreadsLocation(entry.getKey())
						&& !(entry.getValue() instanceof PositiveResult<?>));

		for (final Map.Entry<IcfgLocation, IResult> entry : mResultsPerLocation.entrySet()) {
			final boolean output;
			if (couldBeInsufficient) {
				// Output all non-positive results (for real error locations, and for insufficient threads). Results for
				// insufficient threads are reported to explain why a determination on some real error locations could
				// potentially not be made.
				output = !(entry.getValue() instanceof PositiveResult<?>);
			} else {
				// Output only results for real error locations. (If not mentioned, the user can simply assume that
				// sufficient thread instances were used.)
				output = !isInsufficientThreadsLocation(entry.getKey());
			}

			if (output) {
				reportResult(entry.getValue());
			}
		}
	}

	private void reportResult(final IResult res) {
		mServices.getResultService().reportResult(Activator.PLUGIN_ID, res);
	}

	/**
	 * @return the root of the CFG.
	 */
	public IElement getRootOfNewModel() {
		return mRootOfNewModel;
	}

	private static <L extends IIcfgTransition<?>> IcfgLocation getErrorPP(final IProgramExecution<L, Term> pe) {
		final int lastPosition = pe.getLength() - 1;
		final IIcfgTransition<?> last = pe.getTraceElement(lastPosition).getTraceElement();
		return last.getTarget();
	}

	public static final class AllErrorsAtOnceDebugIdentifier extends DebugIdentifier {

		public static final AllErrorsAtOnceDebugIdentifier INSTANCE = new AllErrorsAtOnceDebugIdentifier();

		private AllErrorsAtOnceDebugIdentifier() {
			// singleton constructor
		}

		@Override
		public String toString() {
			return "AllErrorsAtOnce";
		}
	}

	public static final class InUseDebugIdentifier extends DebugIdentifier {

		public static final InUseDebugIdentifier INSTANCE = new InUseDebugIdentifier();

		private InUseDebugIdentifier() {
			// singleton constructor
		}

		@Override
		public String toString() {
			return "InUseError";
		}
	}
}
