/*
 * Copyright (C) 2014-2015 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2014-2015 Jan Leike (leike@informatik.uni-freiburg.de)
 * Copyright (C) 2014-2015 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2012-2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE LassoRanker Library.
 * 
 * The ULTIMATE LassoRanker Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE LassoRanker Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE LassoRanker Library. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE LassoRanker Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE LassoRanker Library grant you additional permission 
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.lassoranker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.uni_freiburg.informatik.ultimate.core.services.IToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lassoranker.exceptions.TermException;
import de.uni_freiburg.informatik.ultimate.lassoranker.nontermination.NonTerminationAnalysisSettings;
import de.uni_freiburg.informatik.ultimate.lassoranker.nontermination.NonTerminationArgument;
import de.uni_freiburg.informatik.ultimate.lassoranker.nontermination.NonTerminationArgumentSynthesizer;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.AddAxioms;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.CommuHashPreprocessor;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.DNF;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.LassoPartitioneerPreprocessor;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.LassoPreprocessor;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.MatchInOutVars;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RemoveNegation;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RewriteArrays2;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RewriteBooleans;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RewriteDivision;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RewriteEquality;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RewriteIte;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RewriteStrictInequalities;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RewriteTrueFalse;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.RewriteUserDefinedTypes;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.SimplifyPreprocessor;
import de.uni_freiburg.informatik.ultimate.lassoranker.preprocessors.StemAndLoopPreprocessor;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.TerminationAnalysisBenchmark;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.TerminationAnalysisSettings;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.TerminationArgument;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.TerminationArgumentSynthesizer;
import de.uni_freiburg.informatik.ultimate.lassoranker.termination.templates.RankingTemplate;
import de.uni_freiburg.informatik.ultimate.lassoranker.variables.LassoBuilder;
import de.uni_freiburg.informatik.ultimate.logic.Rational;
import de.uni_freiburg.informatik.ultimate.logic.SMTLIBException;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.model.boogie.BoogieVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Boogie2SMT;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.TransFormula;
import de.uni_freiburg.informatik.ultimate.util.DebugMessage;

/**
 * This is the class that controls LassoRanker's (non-)termination analysis
 * 
 * Tools that use LassoRanker as a library probably want to use this class as an
 * interface for invoking LassoRanker. This class can also be derived for a more
 * fine-grained control over the synthesis process.
 * 
 * @author Jan Leike 
 * @author Matthias Heizmann
 */
public class LassoAnalysis {
	private final Logger m_Logger;
	
	/**
	 * Analysis techniques supported by this library.
	 * TODO: Is "analysis techniques" the right term?
	 */
	public enum AnalysisTechnique { 
		/**
		 * Termination analysis based on the synthesis of ranking functions and 
		 * supporting invariants.
		 */
		RANKING_FUNCTIONS_SUPPORTING_INVARIANTS,
		/**
		 * Nontermination analysis based on the synthesis of geometric 
		 * nontermination arguments.
		 */
		GEOMETRIC_NONTERMINATION_ARGUMENTS,
	}

	/**
	 * Stem formula of the linear lasso program
	 */
	private TransFormula m_stem_transition;

	/**
	 * Loop formula of the linear lasso program
	 */
	private final TransFormula m_loop_transition;

	/**
	 * Representation of the lasso that we are analyzing which is split
	 * into a conjunction of lassos.
	 */
	private Collection<Lasso> m_lassos;
	
	/**
	 * Global BoogieVars that are modifiable in the procedure where the honda 
	 * of the lasso lies.
	 */
	private Set<BoogieVar> m_ModifiableGlobalsAtHonda;

	/**
	 * SMT script that created the transition formulae
	 */
	protected final Script m_old_script;

	/**
	 * The axioms regarding the transitions' constants
	 */
	protected final Term[] m_axioms;

	/**
	 * The current preferences
	 */
	protected final LassoRankerPreferences m_preferences;

	/**
	 * Set of terms in which RewriteArrays puts additional supporting invariants
	 */
	protected final Set<Term> m_ArrayIndexSupportingInvariants;

	private final Boogie2SMT m_Boogie2SMT;

	private final IUltimateServiceProvider mServices;

	private final IToolchainStorage mStorage;

	/**
	 * Benchmark data from last termination analysis. 
	 * Includes e.g., the number  of Motzkin's Theorem applications.
	 */
	private List<TerminationAnalysisBenchmark> m_LassoTerminationAnalysisBenchmarks;
	
	/**
	 * Benchmark data from the preprocessing of the lasso.
	 */
	private PreprocessingBenchmark m_PreprocessingBenchmark;



	/**
	 * Constructor for the LassoRanker interface. Calling this invokes the
	 * preprocessor on the stem and loop formula.
	 * 
	 * If the stem is null, the stem has to be added separately by calling
	 * addStem().
	 * 
	 * @param script
	 *            the SMT script used to construct the transition formulae
	 * @param boogie2smt
	 *            the boogie2smt object that created the TransFormula's
	 * @param stem
	 *            a transition formula corresponding to the lasso's stem
	 * @param loop
	 *            a transition formula corresponding to the lasso's loop
	 * @param modifiableGlobalsAtHonda
	 * 			  global BoogieVars that are modifiable in the procedure
	 *            where the honda of the lasso lies.
	 * @param axioms
	 *            a collection of axioms regarding the transitions' constants
	 * @param preferences
	 *            configuration options for this plugin; these are constant for
	 *            the life time of this object
	 * @param services
	 * @param storage
	 * @throws TermException
	 *             if preprocessing fails
	 * @throws FileNotFoundException
	 *             if the file for dumping the script cannot be opened
	 */
	public LassoAnalysis(Script script, Boogie2SMT boogie2smt, TransFormula stem_transition,
			TransFormula loop_transition, Set<BoogieVar> modifiableGlobalsAtHonda, Term[] axioms, LassoRankerPreferences preferences,
			IUltimateServiceProvider services, IToolchainStorage storage) throws TermException {
		
		mServices = services;
		mStorage = storage;
		m_Logger = mServices.getLoggingService().getLogger(Activator.s_PLUGIN_ID);
		m_preferences = new LassoRankerPreferences(preferences); // defensive
																	// copy
		m_preferences.checkSanity();
		m_Logger.info("Preferences:\n" + m_preferences.toString());
		
		m_old_script = script;
		m_axioms = axioms;
		m_ArrayIndexSupportingInvariants = new HashSet<Term>();
		m_Boogie2SMT = boogie2smt;
		
		m_LassoTerminationAnalysisBenchmarks =
				new ArrayList<TerminationAnalysisBenchmark>();
		
		m_stem_transition = stem_transition;
		m_loop_transition = loop_transition;
		m_ModifiableGlobalsAtHonda = modifiableGlobalsAtHonda;
		assert (m_loop_transition != null);
		
		// Preprocessing creates the Lasso object
		this.preprocess();
		
		// This is now a good time to do garbage collection to free the memory
		// allocated during preprocessing. Hopefully it is then available when
		// we call the SMT solver.
	}

	/**
	 * Constructor for the LassoRanker interface. Calling this invokes the
	 * preprocessor on the stem and loop formula.
	 * 
	 * @param script
	 *            the SMT script used to construct the transition formulae
	 * @param boogie2smt
	 *            the boogie2smt object that created the TransFormulas
	 * @param loop
	 *            a transition formula corresponding to the lasso's loop
	 * @param axioms
	 *            a collection of axioms regarding the transitions' constants
	 * @param preferences
	 *            configuration options for this plugin; these are constant for
	 *            the life time of this object
	 * @param services
	 * @param storage
	 * @throws TermException
	 *             if preprocessing fails
	 * @throws FileNotFoundException
	 *             if the file for dumping the script cannot be opened
	 */
	public LassoAnalysis(Script script, Boogie2SMT boogie2smt, TransFormula loop, Set<BoogieVar> modifiableGlobalsAtHonda, Term[] axioms,
			LassoRankerPreferences preferences, IUltimateServiceProvider services, IToolchainStorage storage)
			throws TermException, FileNotFoundException {
		this(script, boogie2smt, null, loop, modifiableGlobalsAtHonda, axioms, preferences, services, storage);
	}
	
	/**
	 * Preprocess the stem or loop transition. This applies the preprocessor
	 * classes and transforms the formula into a list of inequalities in DNF.
	 * 
	 * The list of preprocessors is given by this.getPreProcessors().
	 * 
	 * @see PreProcessor
	 * @throws TermException if preprocessing fails
	 */
	protected void preprocess() throws TermException {
		m_Logger.info("Starting lasso preprocessing...");
		LassoBuilder lassoBuilder = new LassoBuilder(m_Logger, m_old_script, m_Boogie2SMT,
				m_stem_transition, m_loop_transition, m_preferences.nlaHandling);
		assert lassoBuilder.isSane();
		lassoBuilder.preprocess(this.getPreProcessors(lassoBuilder, m_preferences.overapproximateArrayIndexConnection), 
				this.getPreProcessors(lassoBuilder, false));
		
		m_PreprocessingBenchmark = lassoBuilder.getPreprocessingBenchmark();
		
		lassoBuilder.constructPolyhedra();
				
		m_lassos = lassoBuilder.getLassos();
		
		// Some debug messages
		m_Logger.debug(new DebugMessage("Original stem:\n{0}",
				m_stem_transition));
		m_Logger.debug(new DebugMessage("Original loop:\n{0}",
				m_loop_transition));
		m_Logger.debug(new DebugMessage("After preprocessing:\n{0}",
				lassoBuilder));
		m_Logger.debug("Guesses for Motzkin coefficients: "
				+ eigenvalueGuesses(m_lassos));
		m_Logger.info("Preprocessing complete.");
	}
	
	/**
	 * @param lassoBuilder 
	 * @return an array of all preprocessors that should be called before
	 *         termination analysis
	 */
	protected LassoPreprocessor[] getPreProcessors(
			LassoBuilder lassoBuilder, boolean overapproximateArrayIndexConnection) {
		return new LassoPreprocessor[] {
				new StemAndLoopPreprocessor(m_old_script, new MatchInOutVars(m_Boogie2SMT.getVariableManager())),
				new StemAndLoopPreprocessor(m_old_script, new AddAxioms(lassoBuilder.getReplacementVarFactory(), m_axioms)),
				new StemAndLoopPreprocessor(m_old_script, new CommuHashPreprocessor(mServices)),
				m_preferences.enable_partitioneer ? new LassoPartitioneerPreprocessor(m_old_script, mServices, m_Boogie2SMT) : null,
				new RewriteArrays2(true, m_stem_transition, m_loop_transition, m_ModifiableGlobalsAtHonda, 
						mServices, m_ArrayIndexSupportingInvariants, m_Boogie2SMT, lassoBuilder.getReplacementVarFactory()),
				new StemAndLoopPreprocessor(m_old_script, new MatchInOutVars(m_Boogie2SMT.getVariableManager())),
				m_preferences.enable_partitioneer ? new LassoPartitioneerPreprocessor(m_old_script, mServices, m_Boogie2SMT) : null,
				new StemAndLoopPreprocessor(m_old_script, new RewriteDivision(lassoBuilder.getReplacementVarFactory())),
				new StemAndLoopPreprocessor(m_old_script, new RewriteBooleans(lassoBuilder.getReplacementVarFactory(), lassoBuilder.getScript())),
				new StemAndLoopPreprocessor(m_old_script, new RewriteIte()),
				new StemAndLoopPreprocessor(m_old_script, new RewriteUserDefinedTypes(lassoBuilder.getReplacementVarFactory(), lassoBuilder.getScript())),
				new StemAndLoopPreprocessor(m_old_script, new RewriteEquality()),
				new StemAndLoopPreprocessor(m_old_script, new CommuHashPreprocessor(mServices)),
				new StemAndLoopPreprocessor(m_old_script, new SimplifyPreprocessor(mServices, mStorage)),
				new StemAndLoopPreprocessor(m_old_script, new DNF(mServices, m_Boogie2SMT.getVariableManager())),
				new StemAndLoopPreprocessor(m_old_script, new SimplifyPreprocessor(mServices, mStorage)),
				new StemAndLoopPreprocessor(m_old_script, new RewriteTrueFalse()),
				new StemAndLoopPreprocessor(m_old_script, new RemoveNegation()),
				new StemAndLoopPreprocessor(m_old_script, new RewriteStrictInequalities()),
		};
	}
	
	/**
	 * @return the preprocessed lassos
	 */
	public Collection<Lasso> getLassos() {
		return m_lassos;
	}
	
	public List<TerminationAnalysisBenchmark> getTerminationAnalysisBenchmarks() {
		return m_LassoTerminationAnalysisBenchmarks;
	}
	
	public PreprocessingBenchmark getPreprocessingBenchmark() {
		return m_PreprocessingBenchmark;
	}
	
	protected String benchmarkScriptMessage(LBool constraintSat, RankingTemplate template) {
		StringBuilder sb = new StringBuilder();
		sb.append("BenchmarkResult: ");
		sb.append(constraintSat);
		sb.append(" for template ");
		sb.append(template.getName());
		sb.append(" with degree ");
		sb.append(template.getDegree());
		sb.append(". ");
		sb.append(m_LassoTerminationAnalysisBenchmarks.toString());
		return sb.toString();
	}

	/**
	 * @return a pretty version of the guesses for loop eigenvalues
	 */
	protected static String eigenvalueGuesses(Collection<Lasso> lassos) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Lasso lasso : lassos) {
			Rational[] eigenvalues = lasso.guessEigenvalues(true);
			for (int i = 0; i < eigenvalues.length; ++i) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(eigenvalues[i].toString());
			}
		}
		sb.append("]");
		return sb.toString();
	}


	/**
	 * Try to find a non-termination argument for the lasso program.
	 * 
	 * @param settings
	 *            (local) settings for nontermination analysis
	 * @param services
	 * @return the list of non-termination arguments (one for each component)
	 *         or null if at least one component does not have one
	 * @throws IOException 
	 */
	public NonTerminationArgument checkNonTermination(
			NonTerminationAnalysisSettings settings) throws SMTLIBException,
			TermException, IOException {
		m_Logger.info("Checking for nontermination...");
		
		List<NonTerminationArgument> ntas
			= new ArrayList<NonTerminationArgument>(m_lassos.size());
		if (m_lassos.size() == 0) {
			m_lassos.add(new Lasso(LinearTransition.getTranstionTrue(),
					LinearTransition.getTranstionTrue()));
		}
		for (Lasso lasso : m_lassos) {
			NonTerminationArgumentSynthesizer nas =
					new NonTerminationArgumentSynthesizer(lasso, m_preferences,
							settings, mServices, mStorage);
			final LBool constraintSat = nas.synthesize();
			if (constraintSat == LBool.SAT) {
				m_Logger.info("Proved nontermination for one component.");
				NonTerminationArgument nta = nas.getArgument();
				ntas.add(nta);
				m_Logger.info(nta);
			} else if (constraintSat == LBool.UNKNOWN) {
				m_Logger.info("Proving nontermination failed: SMT Solver returned 'unknown'.");
			} else if (constraintSat == LBool.UNSAT) {
				m_Logger.info("Proving nontermination failed: No geometric nontermination argument exists.");
			} else {
				assert false;
			}
			nas.close();
			if (constraintSat != LBool.SAT) {
				// One component did not have a nontermination argument.
				// Therefore we have to give up.
				return null;
			}
		}
		
		// Join nontermination arguments
		assert ntas.size() > 0;
		NonTerminationArgument nta = ntas.get(0);
		for (int i = 1; i < ntas.size(); ++i) {
			nta = nta.join(ntas.get(i));
		}
		return nta;
	}

	/**
	 * Try to find a termination argument for the lasso program specified by the
	 * given ranking function template.
	 * 
	 * @param template
	 *            the ranking function template
	 * @param settings
	 *            (local) settings for termination analysis
	 * @return the termination argument or null of none is found
	 * @throws IOException 
	 */
	public TerminationArgument tryTemplate(RankingTemplate template, TerminationAnalysisSettings settings)
			throws SMTLIBException, TermException, IOException {
		// ignore stem
		m_Logger.info("Using template '" + template.getName() + "'.");
		m_Logger.debug(template);
		
		for (Lasso lasso : m_lassos) {
			// It suffices to prove termination for one component
			long startTime = System.nanoTime();
			
			TerminationArgumentSynthesizer tas =
					new TerminationArgumentSynthesizer(lasso, template,
							m_preferences, settings,
							m_ArrayIndexSupportingInvariants, mServices, mStorage);
			final LBool constraintSat = tas.synthesize();
			
			long endTime = System.nanoTime();
			
			TerminationAnalysisBenchmark tab = new TerminationAnalysisBenchmark(
					constraintSat, lasso.getStemVarNum(),
					lasso.getLoopVarNum(), lasso.getStemDisjuncts(),
					lasso.getLoopDisjuncts(), template.getName(),
					template.getDegree(), tas.getNumSIs(), tas.getNumMotzkin(),
					endTime - startTime);
			m_LassoTerminationAnalysisBenchmarks.add(tab);
			m_Logger.debug(benchmarkScriptMessage(constraintSat, template));
			
			if (constraintSat == LBool.SAT) {
				m_Logger.info("Proved termination.");
				TerminationArgument ta = tas.getArgument();
				m_Logger.info(ta);
				Term[] lexTerm = ta.getRankingFunction().asLexTerm(m_old_script);
				for (Term t : lexTerm) {
					m_Logger.debug(new DebugMessage("{0}", new SMTPrettyPrinter(t)));
				}
				tas.close();
				return ta;
			} else if (constraintSat == LBool.UNKNOWN) {
				m_Logger.info("Proving termination failed: SMT Solver returned 'unknown'.");
			} else if (constraintSat == LBool.UNSAT) {
				m_Logger.info("Proving termination failed for this template and these settings.");
			} else {
				assert false;
			}
			tas.close();
		}
		// No lasso hat a termination argument, so we have to give up
		return null;
	}
	
	/**
	 * Objects for collecting data from the preprocessing steps.
	 * 
	 * @author Matthias Heizmann
	 *
	 */
	public static class PreprocessingBenchmark {
		private final int m_IntialMaxDagSizeLassos;
		private final List<String> m_Preprocessors = new ArrayList<>();
		private final List<Integer> m_MaxDagSizeLassosAbsolut = new ArrayList<Integer>();;
		private final List<Float> m_MaxDagSizeLassosRelative = new ArrayList<Float>();
		public PreprocessingBenchmark(int intialMaxDagSizeLassos) {
			super();
			m_IntialMaxDagSizeLassos = intialMaxDagSizeLassos;
		}
		public void addPreprocessingData(String description,
				int maxDagSizeNontermination, int maxDagSizeLassos) {
			m_Preprocessors.add(description);
			m_MaxDagSizeLassosAbsolut.add(maxDagSizeLassos);
			m_MaxDagSizeLassosRelative.add(computeQuotiontOfLastTwoEntries(
					m_MaxDagSizeLassosAbsolut, m_IntialMaxDagSizeLassos));
		}
		
		public void addPreprocessingData(String description,
				int maxDagSizeLassos) {
			m_Preprocessors.add(description);
			m_MaxDagSizeLassosAbsolut.add(maxDagSizeLassos);
			m_MaxDagSizeLassosRelative.add(computeQuotiontOfLastTwoEntries(
					m_MaxDagSizeLassosAbsolut, m_IntialMaxDagSizeLassos));
		}
		
		public float computeQuotiontOfLastTwoEntries(List<Integer> list, int initialValue) {
			int lastEntry;
			int secondLastEntry;
			if (list.size() == 0) {
				throw new IllegalArgumentException();
			} else {
				lastEntry = list.get(list.size() - 1);
				if (list.size() == 1) {
					secondLastEntry = initialValue; 
				} else {
					secondLastEntry = list.get(list.size() - 2);
				}
			}
			return ((float) lastEntry) / ((float) secondLastEntry);
		}
		public int getIntialMaxDagSizeLassos() {
			return m_IntialMaxDagSizeLassos;
		}
		public List<String> getPreprocessors() {
			return m_Preprocessors;
		}
		public List<String> getPreprocessorsNon() {
			return m_Preprocessors;
		}
		public List<Float> getMaxDagSizeLassosRelative() {
			return m_MaxDagSizeLassosRelative;
		}
		
		
		public static String prettyprint(List<PreprocessingBenchmark> benchmarks) {
			if (benchmarks.isEmpty()) {
				return "";
			}
			List<String> preprocessors = benchmarks.get(0).getPreprocessors();
			List<String> preprocessorAbbreviations = computeAbbrev(preprocessors);
			float[] LassosData = new float[preprocessors.size()];
			int LassosAverageInitial = 0;
			for (PreprocessingBenchmark pb : benchmarks) {
				addListElements(LassosData, pb.getMaxDagSizeLassosRelative());
				LassosAverageInitial += pb.getIntialMaxDagSizeLassos();
			}
			divideAllEntries(LassosData, benchmarks.size());
			LassosAverageInitial /= benchmarks.size();
			StringBuilder sb = new StringBuilder();
			sb.append("  ");
			sb.append("Lassos: ");
			sb.append("inital");
			sb.append(LassosAverageInitial);
			sb.append(" ");
			sb.append(ppOne(LassosData, preprocessorAbbreviations));
			return sb.toString();
		}
		
		private static List<String> computeAbbrev(List<String> preprocessors) {
			List<String> result = new ArrayList<>();
			for (String description : preprocessors) {
				result.add(computeAbbrev(description));
			}
			return result;
		}
		
		private static String computeAbbrev(String ppId) {
			switch (ppId) {
			case DNF.s_Description:
				return "dnf";
			case SimplifyPreprocessor.s_Description:
				return "smp";
			case RewriteArrays2.s_Description:
				return "arr";
			case RewriteEquality.s_Description:
				return "eq";
			case RewriteStrictInequalities.s_Description:
				return "sie";
			case LassoPartitioneerPreprocessor.s_Description:
				return "lsp";
			case RemoveNegation.s_Description:
				return "neg";
			case RewriteDivision.s_Description:
				return "div";
			case RewriteBooleans.s_Description:
				return "bol";
			case MatchInOutVars.s_Description:
				return "mio";
			case RewriteTrueFalse.s_Description:
				return "tf";
			case RewriteIte.s_Description:
				return "ite";
			case AddAxioms.s_Description:
				return "ax";
			case CommuHashPreprocessor.s_Description:
				return "hnf";
			default:
				return "ukn";
			}
		}
		private static String ppOne(float[] relativeEqualizedData, List<String> preprocessorAbbreviations) {
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<relativeEqualizedData.length; i++) {
				sb.append(preprocessorAbbreviations.get(i));
				sb.append(String.valueOf(makePercent(relativeEqualizedData[i])));
				sb.append(" ");
			}
			return sb.toString();
		}
		
		private static int makePercent(float f) {
			return (int) Math.floor(f * 100);
		}
		private static void addListElements(float[] modifiedArray, List<Float> incrementList) {
			assert modifiedArray.length == incrementList.size();
			for (int i=0; i<modifiedArray.length; i++) {
				modifiedArray[i] += incrementList.get(i);
			}
		}
		
		private static void divideAllEntries(float[] modifiedArray, int divisor) {
			for (int i=0; i<modifiedArray.length; i++) {
				modifiedArray[i] /= (float) divisor;
			}
		}
		

	}
}
