/*
 * Copyright (C) 2019 Elisabeth Schanno
 * Copyright (C) 2019 Dominik Klumpp (klumpp@informatik.uni-freiburg.de)
 * Copyright (C) 2019 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2019 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.petrinetlbe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.automata.partialorder.LiptonReduction.ICompositionFactory;
import de.uni_freiburg.informatik.ultimate.core.model.models.ModelUtils;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.IcfgUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IIcfgTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgEdgeFactory;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgInternalTransition;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.transitions.TransFormulaUtils;
import de.uni_freiburg.informatik.ultimate.lib.modelcheckerutils.cfg.transitions.UnmodifiableTransFormula;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.ManagedScript;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SmtSortUtils;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.lib.smtlibutils.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.logic.Sort;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Summary;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.Activator;
import de.uni_freiburg.informatik.ultimate.util.HashUtils;

/**
 * Implements an {@link ICompositionFactory} that is suitable for Trace
 * Abstraction. This class is used by {@link PetriNetLargeBlockEncoding}.
 *
 * @author Elisabeth Schanno
 * @author Dominik Klumpp (klumpp@informatik.uni-freiburg.de)
 * @author Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 *
 */
public class IcfgCompositionFactory implements ICompositionFactory<IIcfgTransition<?>> {

	private final SimplificationTechnique mSimplificationTechnique = SimplificationTechnique.SIMPLIFY_DDA;
	private final XnfConversionTechnique mXnfConversionTechnique = XnfConversionTechnique.BOTTOM_UP_WITH_LOCAL_SIMPLIFICATION;

	private final IUltimateServiceProvider mServices;
	private final ILogger mLogger;
	private final ManagedScript mManagedScript;
	private final IcfgEdgeFactory mEdgeFactory;

	private final Map<IIcfgTransition<?>, TermVariable> mBranchEncoders = new HashMap<>();

	public IcfgCompositionFactory(final IUltimateServiceProvider services, final CfgSmtToolkit cfgSmtToolkit) {
		mServices = services;
		mLogger = services.getLoggingService().getLogger(Activator.PLUGIN_ID);
		mManagedScript = cfgSmtToolkit.getManagedScript();
		mEdgeFactory = cfgSmtToolkit.getIcfgEdgeFactory();
	}

	@Override
	public boolean isComposable(final IIcfgTransition<?> transition) {
		return transition instanceof IIcfgInternalTransition<?> && !(transition instanceof Summary);
	}

	private boolean isComposable(final List<IIcfgTransition<?>> transitions) {
		return transitions.stream().allMatch(this::isComposable);
	}

	@Override
	public IIcfgTransition<IcfgLocation> composeSequential(IIcfgTransition<?> first, IIcfgTransition<?> second) {
		// Simplify resulting TransFormula because various other algorithms in Ultimate
		// have to work with this term.
		final boolean simplify = true;
		// Try to eliminate auxiliary variables to avoid quantifier alterations in
		// subsequent SlettersMT solver calls during verification.
		final boolean tryAuxVarElimination = true;

		final IcfgLocation source = first.getSource();
		final IcfgLocation target = second.getTarget();

		final List<IIcfgTransition<?>> transitions = Arrays.asList(first, second);
		assert isComposable(transitions) : "You cannot have calls or returns in sequential compositions";

		// TODO This partially duplicates code in IcfgEdgeBuilder
		final List<UnmodifiableTransFormula> transFormulas = transitions.stream().map(IcfgUtils::getTransformula)
				.collect(Collectors.toList());
		final UnmodifiableTransFormula tf = TransFormulaUtils.sequentialComposition(mLogger, mServices, mManagedScript,
				simplify, tryAuxVarElimination, false, mXnfConversionTechnique, mSimplificationTechnique,
				transFormulas);
		final IcfgInternalTransition rtr = mEdgeFactory.createInternalTransition(source, target, null, tf);
		ModelUtils.mergeAnnotations(transitions, rtr);

		return rtr;
	}

	@Override
	public IIcfgTransition<IcfgLocation> composeParallel(List<IIcfgTransition<?>> transitions) {
		assert !transitions.isEmpty() : "Cannot compose 0 transitions";
		assert isComposable(transitions) : "You cannot have calls or returns in parallel compositions";

		final IcfgLocation source = transitions.get(0).getSource();
		final IcfgLocation target = transitions.get(0).getTarget();
		assert transitions.stream().allMatch(t -> t.getSource() == source
				&& t.getTarget() == target) : "Can only compose transitions with equal sources and targets.";

		// TODO This partially duplicates code in IcfgEdgeBuilder
		final List<UnmodifiableTransFormula> transFormulas = transitions.stream().map(IcfgUtils::getTransformula)
				.collect(Collectors.toList());
		final UnmodifiableTransFormula[] tfArray = transFormulas
				.toArray(new UnmodifiableTransFormula[transFormulas.size()]);
		// TODO Matthias 2019-11-13: Serial number should be unique!!!?!
		// Maybe we should move these constructions to the edge factory
		// which can construct unique serial numbers
		final int serialNumber = HashUtils.hashHsieh(293, (Object[]) tfArray);
		final UnmodifiableTransFormula parallelTf = TransFormulaUtils.parallelComposition(mLogger, mServices,
				serialNumber, mManagedScript, null, false, mXnfConversionTechnique, tfArray);
		final LinkedHashMap<TermVariable, IIcfgTransition<?>> branchIndicator2edge = constructBranchIndicatorToEdgeMapping(
				serialNumber, mManagedScript, transitions);
		final TermVariable[] branchIndicatorArray = branchIndicator2edge.keySet()
				.toArray(new TermVariable[branchIndicator2edge.size()]);
		final UnmodifiableTransFormula parallelWithBranchIndicators = TransFormulaUtils.parallelComposition(mLogger,
				mServices, serialNumber, mManagedScript, branchIndicatorArray, false, mXnfConversionTechnique, tfArray);
		final IcfgInternalTransition rtr = mEdgeFactory.createInternalTransitionWithBranchEncoders(source, target, null,
				parallelTf, parallelWithBranchIndicators, branchIndicator2edge);
		ModelUtils.mergeAnnotations(transitions, rtr);

		// Update info for back translation
		updateChoiceCompositions(rtr, branchIndicator2edge);

		return rtr;
	}

	private static LinkedHashMap<TermVariable, IIcfgTransition<?>> constructBranchIndicatorToEdgeMapping(
			final int serialNumber, final ManagedScript managedScript, final List<IIcfgTransition<?>> transitions) {
		final LinkedHashMap<TermVariable, IIcfgTransition<?>> result = new LinkedHashMap<>();
		managedScript.lock(result);
		for (int i = 0; i < transitions.size(); i++) {
			final String varname = "BraInd" + i + "of" + serialNumber;
			final Sort boolSort = SmtSortUtils.getBoolSort(managedScript.getScript());
			final TermVariable tv = managedScript.constructFreshTermVariable(varname, boolSort);
			result.put(tv, transitions.get(i));
		}
		managedScript.unlock(result);
		return result;
	}

	private void updateChoiceCompositions(final IIcfgTransition<?> choiceIcfgEdge,
			final Map<TermVariable, IIcfgTransition<?>> indicators) {
		for (final Map.Entry<TermVariable, IIcfgTransition<?>> entry : indicators.entrySet()) {
			assert !mBranchEncoders.containsKey(entry.getValue()) : "Ambiguous branch encoder for transition";
			mBranchEncoders.put(entry.getValue(), entry.getKey());
		}
	}

	public Map<IIcfgTransition<?>, TermVariable> getBranchEncoders() {
		return mBranchEncoders;
	}
}
