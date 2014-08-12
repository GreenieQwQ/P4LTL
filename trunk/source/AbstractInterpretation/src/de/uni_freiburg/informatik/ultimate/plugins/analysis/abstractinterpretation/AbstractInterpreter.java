/**
 * 
 */
package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.uni_freiburg.informatik.ultimate.boogie.symboltable.BoogieSymbolTable;
import de.uni_freiburg.informatik.ultimate.boogie.type.PreprocessorAnnotation;
import de.uni_freiburg.informatik.ultimate.core.preferences.UltimatePreferenceStore;
import de.uni_freiburg.informatik.ultimate.core.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.irsdependencies.loopdetector.LoopDetector;
import de.uni_freiburg.informatik.ultimate.model.IElement;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.CallStatement;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Statement;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain.AbstractInterpretationBoogieVisitor;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain.AbstractState;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain.IAbstractDomainFactory;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain.booldomain.BoolDomainFactory;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain.intervaldomain.IntervalDomainFactory;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain.signdomain.SignDomainFactory;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain.topbottomdomain.TopBottomDomainFactory;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.preferences.AbstractInterpretationPreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Call;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.ParallelComposition;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.ProgramPoint;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RCFGEdge;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RCFGNode;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Return;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RootEdge;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RootNode;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.SequentialComposition;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.StatementSequence;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.Summary;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CfgBuilder.GotoEdge;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.util.RCFGEdgeVisitor;
import de.uni_freiburg.informatik.ultimate.result.AllSpecificationsHoldResult;
import de.uni_freiburg.informatik.ultimate.result.GenericResult;
import de.uni_freiburg.informatik.ultimate.result.IResultWithSeverity.Severity;

/**
 * @author Christopher Dillo
 * 
 */
public class AbstractInterpreter extends RCFGEdgeVisitor {

	private final IUltimateServiceProvider m_services;

	private Logger m_logger;

	private IAbstractDomainFactory<?> m_intDomainFactory;
	private IAbstractDomainFactory<?> m_realDomainFactory;
	private IAbstractDomainFactory<?> m_boolDomainFactory;
	private IAbstractDomainFactory<?> m_bitVectorDomainFactory;
	private IAbstractDomainFactory<?> m_stringDomainFactory;

	private final LinkedList<RCFGNode> m_nodesToVisit = new LinkedList<RCFGNode>();

	private final Map<RCFGNode, List<AbstractState>> m_states = new HashMap<RCFGNode, List<AbstractState>>();

	private AbstractState m_currentState, m_resultingState;

	private RCFGNode m_currentNode;

	private AbstractInterpretationBoogieVisitor m_boogieVisitor;

	private final Set<IAbstractStateChangeListener> m_stateChangeListeners = new HashSet<IAbstractStateChangeListener>();

	private final Set<ProgramPoint> m_errorLocs = new HashSet<ProgramPoint>();
	private final Set<ProgramPoint> m_reachedErrorLocs = new HashSet<ProgramPoint>();

	private HashMap<ProgramPoint, HashMap<RCFGEdge, RCFGEdge>> m_loopEntryNodes;
	
	private Set<String> m_fixedNumbersForWidening = new HashSet<String>();
	private Set<String> m_numbersForWidening = new HashSet<String>();
	
	private BoogieSymbolTable m_symbolTable;
	
	private boolean continueProcessing;

	// for preferences
	private String m_mainMethodName;
	private int m_iterationsUntilWidening;
	private int m_parallelStatesUntilMerge;
	private String m_widening_fixedNumbers;
	private boolean m_widening_autoNumbers;

	private boolean m_generateStateAnnotations;
	private boolean m_stateChangeLogConsole;
	private boolean m_stateChangeLogFile;
	private boolean m_stateChangeLogUseSourcePath;
	private String m_stateChangeLogPath;
	
	private boolean m_stopAfterAnyError;
	private boolean m_stopAfterAllErrors;
	
	private String m_intDomainID;
	private String m_intWideningOpName;
	private String m_intMergeOpName;
	
	private String m_realDomainID;
	private String m_realWideningOpName;
	private String m_realMergeOpName;
	
	private String m_boolDomainID;
	private String m_boolWideningOpName;
	private String m_boolMergeOpName;
	
	private String m_bitVectorDomainID;
	private String m_bitVectorWideningOpName;
	private String m_bitVectorMergeOpName;
	
	private String m_stringDomainID;
	private String m_stringWideningOpName;
	private String m_stringMergeOpName;
	
	

	public AbstractInterpreter(IUltimateServiceProvider services) {
		m_services = services;
		m_logger = m_services.getLoggingService().getLogger(Activator.s_PLUGIN_ID);

		fetchPreferences();

		String[] nums = m_widening_fixedNumbers.split(",");
		for (int i = 0; i < nums.length; i++)
			m_fixedNumbersForWidening.add(nums[i].trim());
	}

	/**
	 * Adds an AbstractInterpretationAnnotation with states at the given
	 * location IElement
	 * 
	 * @param element
	 *            The location of the given states
	 * @param states
	 *            The states at the given location
	 */
	private void annotateElement(IElement element, List<AbstractState> states) {
		AbstractInterpretationAnnotations anno = new AbstractInterpretationAnnotations(states);
		anno.annotate(element);
	}

	/**
	 * Adds a program state to a node's list of states
	 * 
	 * @param node
	 *            The node that state belongs to
	 * @param state
	 *            The program state to put on the node
	 * @return True if the state was added, false if not (when a superstate is
	 *         at the node)
	 */
	private boolean putStateToNode(AbstractState state, RCFGNode node, RCFGEdge fromEdge) {
		List<AbstractState> statesAtNode = m_states.get(node);
		if (statesAtNode == null) {
			statesAtNode = new LinkedList<AbstractState>();
			m_states.put(node, statesAtNode);
		}

		AbstractState newState = state;
		List<AbstractState> statesAtNodeBackup = new ArrayList<AbstractState>(statesAtNode);

		// check for loop entry / widening
		boolean applyWidening = false;
		ProgramPoint pp = (ProgramPoint) node;
		if ((pp != null) && m_loopEntryNodes.containsKey(pp)) {
			AbstractState.LoopStackElement le = newState.peekLoopEntry();
			if (le != null) {
				if ((le.getLoopNode() == node) && (le.getExitEdge() == fromEdge))
					newState.popLoopEntry();
			}

			if (newState.peekLoopEntry().getIterationCount(pp) >= m_iterationsUntilWidening)
				applyWidening = true;
		}

		Set<AbstractState> unprocessedStates = new HashSet<AbstractState>();
		Set<AbstractState> statesToRemove = new HashSet<AbstractState>();
		for (AbstractState s : statesAtNode) {
			if (s.isSuper(newState))
				return false; // abort if a superstate exists
			if (s.isProcessed()) {
				if (newState.isSuccessor(s)) {
					// widen if possible
					if (applyWidening && (s.peekLoopEntry().getIterationCount(pp) > 0)) {
						newState = s.widen(newState);
						m_logger.debug(String.format("Widening at %s", node.toString()));
					}

					statesToRemove.add(s); // remove old obsolete nodes
				}
			} else {
				if (newState.isSuper(s))
					statesToRemove.add(s); // remove unprocessed substates
											// of the new state
				else
					unprocessedStates.add(s); // collect unprocessed states
			}
		}
		for (AbstractState s : statesToRemove)
			statesAtNode.remove(s);
		if (unprocessedStates.size() >= m_parallelStatesUntilMerge) {
			// merge states
			m_logger.debug(String.format("Merging at %s", node.toString()));
			for (AbstractState s : unprocessedStates) {
				statesAtNode.remove(s);
				newState = newState.merge(s);
			}
		}
		statesAtNode.add(newState);
		notifyStateChangeListeners(fromEdge, statesAtNodeBackup, state, newState);

		if (m_generateStateAnnotations)
			annotateElement(node, statesAtNode);
		return true;
	}

	/**
	 * Provide the RootNode from where to start and the RCFG will be processed.
	 * 
	 * @param root
	 *            The node to start from. RootAnnotations are required for loop
	 *            detection etc.
	 */
	public void processRcfg(RootNode root) {
		m_reachedErrorLocs.clear();
		continueProcessing = true;

		// state change logger
		if (m_stateChangeLogConsole || m_stateChangeLogFile) {
			String fileDir = "";
			String fileName = "";
			if (m_stateChangeLogFile) {
				File sourceFile = new File(root.getFilename());
				DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
				fileName = sourceFile.getName() + "_AI_" + dfm.format(new Date()) + ".txt";
				if (m_stateChangeLogUseSourcePath) {
					fileDir = sourceFile.getParent();
				} else {
					fileDir = null;
				}
				if (fileDir == null) {
					fileDir = new File(m_stateChangeLogPath).getAbsolutePath();
				}
			}
			StateChangeLogger scl = new StateChangeLogger(m_logger, m_stateChangeLogConsole, m_stateChangeLogFile,
					fileDir + File.separatorChar + fileName);
			this.registerStateChangeListener(scl);
		}

		// numbers for widening
		m_numbersForWidening.clear();
		m_numbersForWidening.addAll(m_fixedNumbersForWidening);
		// collect literals if preferences say so
		if (m_widening_autoNumbers) {
			LiteralCollector literalCollector = new LiteralCollector(root, m_logger);
			m_numbersForWidening.addAll(literalCollector.getResult());
		}

		// domain factories chosen in preferences
		m_intDomainFactory = makeDomainFactory(m_intDomainID, m_intWideningOpName, m_intMergeOpName);
		m_realDomainFactory = makeDomainFactory(m_realDomainID, m_realWideningOpName, m_realMergeOpName);
		m_boolDomainFactory = makeDomainFactory(m_boolDomainID, m_boolWideningOpName, m_boolMergeOpName);
		m_bitVectorDomainFactory = makeDomainFactory(m_bitVectorDomainID, m_bitVectorWideningOpName, m_bitVectorMergeOpName);
		m_stringDomainFactory = makeDomainFactory(m_stringDomainID, m_stringWideningOpName, m_stringMergeOpName);

		// fetch loop nodes with their entry/exit edges
		LoopDetector loopDetector = new LoopDetector(m_services);
		try {
			loopDetector.process(root);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		m_loopEntryNodes = loopDetector.getResult();

		// preprocessor annotation: get symboltable
		PreprocessorAnnotation pa = PreprocessorAnnotation.getAnnotation(root);
		if (pa == null) {
			m_logger.error("No symbol table found on given RootNode.");
			return;
		}
		m_symbolTable = pa.getSymbolTable();

		m_boogieVisitor = new AbstractInterpretationBoogieVisitor(m_logger, m_symbolTable,
				m_intDomainFactory, m_realDomainFactory, m_boolDomainFactory,
				m_bitVectorDomainFactory, m_stringDomainFactory);

		// root annotation: get location list, error locations
		Map<String,ProgramPoint> entryNodes = root.getRootAnnot().getEntryNodes();
		ProgramPoint mainEntry = entryNodes.get(m_mainMethodName);
		
		Map<String,Collection<ProgramPoint>> errorLocMap = root.getRootAnnot().getErrorNodes();
		for (String s : errorLocMap.keySet())
			m_errorLocs.addAll(errorLocMap.get(s));
		
		// add entry node of Main procedure / any if no Main() exists
		for (RCFGEdge e : root.getOutgoingEdges()) {
			if (e instanceof RootEdge) {
				RCFGNode target = e.getTarget();
				if ((mainEntry == null) || (target == mainEntry)) {
					AbstractState state = new AbstractState(m_logger, m_intDomainFactory,
							m_realDomainFactory, m_boolDomainFactory,
							m_bitVectorDomainFactory, m_stringDomainFactory);
					if (mainEntry != null) {
						CallStatement mainProcMockStatement = new CallStatement(null, false, null, m_mainMethodName, null);
						state.pushStackLayer(mainProcMockStatement); // layer for main method
					}
					putStateToNode(state, target, e);
					m_nodesToVisit.add(target);
				}
			}
		}

		visitNodes();

		if (m_reachedErrorLocs.isEmpty()) {
			// report as safe
			reportSafeResult();
		}
	}

	/**
	 * Visit edges as long as there are edges in m_edgesToVisit
	 */
	protected void visitNodes() {
		RCFGNode node = m_nodesToVisit.poll();
		if (node != null) {
			List<AbstractState> statesAtNode = m_states.get(node);
			// process all unprocessed states at the node
			boolean hasUnprocessed = true;
			while (hasUnprocessed) {
				AbstractState unprocessedState = null;
				for (AbstractState s : statesAtNode) {
					if (!s.isProcessed()) {
						unprocessedState = s;
						break;
					}
				}
				if (unprocessedState != null) {
					m_currentState = unprocessedState;
					m_currentState.setProcessed(true);

					m_currentNode = node;

					for (RCFGEdge e : node.getOutgoingEdges()) {
						m_resultingState = null;
						visit(e);
					}
				} else {
					hasUnprocessed = false;
				}
			} // hasUnprocessed
			
			// remove states if they aren't needed for possible widening anymore
			if (node.getIncomingEdges().size() <= 1)
				m_states.remove(node);
			
			if (continueProcessing
					&& m_services.getProgressMonitorService().continueProcessing())
				visitNodes(); // repeat until m_nodesToVisit is empty
		}
	}

	@Override
	protected void visit(RCFGEdge e) {
		m_logger.debug("Visiting: " + e.getSource() + " -> " + e.getTarget());

		super.visit(e);

		if (m_resultingState == null)
			return; // do not process target node!
		
		Map<RCFGEdge, RCFGEdge> loopEdges = m_loopEntryNodes.get(m_currentNode);
		if (loopEdges != null) {
			RCFGEdge exitEdge = loopEdges.get(e);
			if (exitEdge != null)
				m_resultingState.pushLoopEntry((ProgramPoint) m_currentNode, exitEdge);
		}

		m_resultingState.addPassedNode(m_currentNode);

		RCFGNode targetNode = e.getTarget();
		if (targetNode != null) {
			if (putStateToNode(m_resultingState, targetNode, e)) {
				ProgramPoint pp = (ProgramPoint) targetNode;
				if (m_errorLocs.contains(pp) && !m_reachedErrorLocs.contains(pp)) {
					m_reachedErrorLocs.add(pp);
					reportErrorResult(pp, m_resultingState);
				} else {
					if (!m_nodesToVisit.contains(targetNode))
						m_nodesToVisit.add(targetNode);
				}
			}
		}
	}

	@Override
	protected void visit(RootEdge e) {
		m_logger.debug("> RootEdge");

		super.visit(e);

		m_resultingState = m_currentState.copy();
	}

	@Override
	protected void visit(CodeBlock c) {
		m_logger.debug("> CodeBlock begin");

		super.visit(c);

		m_logger.debug("> CodeBlock end");
	}

	@Override
	protected void visit(Call c) {
		m_logger.debug("> Call");

		super.visit(c);

		m_resultingState = m_boogieVisitor.evaluateStatement(c.getCallStatement(), m_currentState);
	}

	@Override
	protected void visit(GotoEdge c) {
		m_logger.debug("> GotoEdge");

		super.visit(c);

		m_resultingState = m_currentState.copy();
	}

	@Override
	protected void visit(ParallelComposition c) {
		m_logger.debug("> ParallelComposition");

		super.visit(c);

		Collection<CodeBlock> blocks = c.getBranchIndicator2CodeBlock().values();

		// process all blocks wrt. the current state and merge all resulting
		// states
		AbstractState mergedState = null;
		for (CodeBlock block : blocks) {
			visit(block);
			if (m_resultingState != null) {
				if (mergedState == null) {
					mergedState = m_resultingState;
				} else {
					mergedState.merge(m_resultingState);
				}
			}
		}
		m_resultingState = mergedState;
	}

	@Override
	protected void visit(Return c) {
		m_logger.debug("> Return");

		super.visit(c);

		m_resultingState = m_boogieVisitor.evaluateReturnStatement(c.getCallStatement(), m_currentState);
		
		// TODO: Widening for recursive functions
	}

	@Override
	protected void visit(SequentialComposition c) {
		m_logger.debug("> SequentialComposition");

		super.visit(c);

		AbstractState currentState = m_currentState;
			// backup, as the member variable is manipulated during iterating the CodeBlocks

		CodeBlock[] blocks = c.getCodeBlocks();

		for (int i = 0; i < blocks.length; i++) {
			visit(blocks[i]);
			m_currentState = m_resultingState;
				// so the next CodeBlocks current state is this states' result state
			
			if (m_resultingState == null)
				break;
		}

		m_currentState = currentState;
	}

	@Override
	protected void visit(Summary c) {
		m_logger.debug("> Summary");

		super.visit(c);

		m_resultingState = null; // do not process target node (ignore summary
									// edges)
	}

	@Override
	protected void visit(StatementSequence c) {
		m_logger.debug("> StatementSequence");

		super.visit(c);

		List<Statement> statements = c.getStatements();

		if (statements.size() > 0) {
			m_resultingState = m_currentState;
			for (int i = 0; i < statements.size(); i++) {
				Statement s = statements.get(i);
				if (s != null) {
					m_resultingState = m_boogieVisitor.evaluateStatement(s, m_resultingState);
					if (m_resultingState == null)
						break;
				}
			}
		}
	}

	/**
	 * Add a state change listener to be notified on state changes
	 * 
	 * @param listener
	 *            The listener to notify
	 * @return True if it has been added, false if not, i.e. if it was already
	 *         registered
	 */
	public boolean registerStateChangeListener(IAbstractStateChangeListener listener) {
		return m_stateChangeListeners.add(listener);
	}

	/**
	 * Remove a state change listener
	 * 
	 * @param listener
	 *            The listener to remove
	 * @return True if it was in the list and has been removed, false if it
	 *         wasn't there to begin with
	 */
	public boolean removeStateChangeListener(IAbstractStateChangeListener listener) {
		return m_stateChangeListeners.remove(listener);
	}

	/**
	 * Notifies all state change listeners of a state change
	 * 
	 * @param viaEdge
	 * @param oldStates
	 * @param newState
	 * @param mergedState
	 */
	private void notifyStateChangeListeners(RCFGEdge viaEdge, List<AbstractState> oldStates, AbstractState newState,
			AbstractState mergedState) {
		for (IAbstractStateChangeListener l : m_stateChangeListeners) {
			List<AbstractState> statesCopy = new ArrayList<AbstractState>(oldStates.size());
			statesCopy.addAll(oldStates);
			l.onStateChange(viaEdge, statesCopy, newState, mergedState);
		}
	}

	/**
	 * Reports a possible error as the plugin's result
	 * 
	 * @param location
	 *            The error location
	 * @param state
	 *            The abstract state at the error location
	 */
	private void reportErrorResult(ProgramPoint location, AbstractState state) {
		m_services.getResultService().reportResult(
				Activator.s_PLUGIN_ID,
				new GenericResult(Activator.s_PLUGIN_ID, "Possible error",
						String.format("Some program specifications may be violated. "
								+ "Error location %s has been reached in the over-approximation.",
								location.getLocationName()), Severity.ERROR));
		
		if (m_stopAfterAnyError) {
			continueProcessing = false;
			m_logger.info(String.format("Abstract interpretation finished after reaching error location %s",
					location.getLocationName()));
		} else if (m_stopAfterAllErrors) {
			if (m_reachedErrorLocs.containsAll(m_errorLocs))
				continueProcessing = false;
			m_logger.info("Abstract interpretation finished after reaching all error locations");
		}
	}

	/**
	 * Reports safety of the program as the plugin's result
	 */
	private void reportSafeResult() {
		m_services.getResultService().reportResult(Activator.s_PLUGIN_ID,
				new AllSpecificationsHoldResult(Activator.s_PLUGIN_ID, ""));
	}

	/**
	 * Get preferences from the preference store
	 */
	private void fetchPreferences() {
		UltimatePreferenceStore prefs = new UltimatePreferenceStore(Activator.s_PLUGIN_ID);
		
		m_mainMethodName = prefs.getString(AbstractInterpretationPreferenceInitializer.LABEL_MAIN_METHOD_NAME);

		m_iterationsUntilWidening = prefs
				.getInt(AbstractInterpretationPreferenceInitializer.LABEL_ITERATIONS_UNTIL_WIDENING);
		m_parallelStatesUntilMerge = prefs.getInt(AbstractInterpretationPreferenceInitializer.LABEL_STATES_UNTIL_MERGE);

		m_widening_fixedNumbers = prefs.getString(AbstractInterpretationPreferenceInitializer.LABEL_WIDENING_FIXEDNUMBERS);
		m_widening_autoNumbers = prefs.getBoolean(AbstractInterpretationPreferenceInitializer.LABEL_WIDENING_AUTONUMBERS);
		
		m_generateStateAnnotations = prefs
				.getBoolean(AbstractInterpretationPreferenceInitializer.LABEL_STATE_ANNOTATIONS);
		m_stateChangeLogConsole = prefs
		.getBoolean(AbstractInterpretationPreferenceInitializer.LABEL_LOGSTATES_CONSOLE);
		m_stateChangeLogFile = prefs
				.getBoolean(AbstractInterpretationPreferenceInitializer.LABEL_LOGSTATES_FILE);
		m_stateChangeLogUseSourcePath = prefs
				.getBoolean(AbstractInterpretationPreferenceInitializer.LABEL_LOGSTATES_USESOURCEPATH);
		m_stateChangeLogPath = prefs
				.getString(AbstractInterpretationPreferenceInitializer.LABEL_LOGSTATES_PATH);
		
		String stopAfter = prefs.getString(AbstractInterpretationPreferenceInitializer.LABEL_STOPAFTER);
		m_stopAfterAnyError = (stopAfter.equals(AbstractInterpretationPreferenceInitializer.OPTION_STOPAFTER_ANYERROR));
		m_stopAfterAllErrors = (stopAfter.equals(AbstractInterpretationPreferenceInitializer.OPTION_STOPAFTER_ALLERRORS));

		m_intDomainID = prefs.getString(AbstractInterpretationPreferenceInitializer.LABEL_INTDOMAIN);
		m_intWideningOpName = prefs.getString(String.format(
				AbstractInterpretationPreferenceInitializer.LABEL_WIDENINGOP, m_intDomainID));
		m_intMergeOpName = prefs.getString(String.format(AbstractInterpretationPreferenceInitializer.LABEL_MERGEOP,
				m_intDomainID));

		m_realDomainID = prefs.getString(AbstractInterpretationPreferenceInitializer.LABEL_REALDOMAIN);
		m_realWideningOpName = prefs.getString(String.format(
				AbstractInterpretationPreferenceInitializer.LABEL_WIDENINGOP, m_realDomainID));
		m_realMergeOpName = prefs.getString(String.format(AbstractInterpretationPreferenceInitializer.LABEL_MERGEOP,
				m_realDomainID));

		m_boolDomainID = prefs.getString(AbstractInterpretationPreferenceInitializer.LABEL_BOOLDOMAIN);
		m_boolWideningOpName = prefs.getString(String.format(
				AbstractInterpretationPreferenceInitializer.LABEL_WIDENINGOP, m_boolDomainID));
		m_boolMergeOpName = prefs.getString(String.format(AbstractInterpretationPreferenceInitializer.LABEL_MERGEOP,
				m_boolDomainID));

		m_bitVectorDomainID = prefs.getString(AbstractInterpretationPreferenceInitializer.LABEL_BITVECTORDOMAIN);
		m_bitVectorWideningOpName = prefs.getString(String.format(
				AbstractInterpretationPreferenceInitializer.LABEL_WIDENINGOP, m_bitVectorDomainID));
		m_bitVectorMergeOpName = prefs.getString(String.format(AbstractInterpretationPreferenceInitializer.LABEL_MERGEOP,
				m_bitVectorDomainID));

		m_stringDomainID = prefs.getString(AbstractInterpretationPreferenceInitializer.LABEL_STRINGDOMAIN);
		m_stringWideningOpName = prefs.getString(String.format(
				AbstractInterpretationPreferenceInitializer.LABEL_WIDENINGOP, m_stringDomainID));
		m_stringMergeOpName = prefs.getString(String.format(AbstractInterpretationPreferenceInitializer.LABEL_MERGEOP,
				m_stringDomainID));
	}


	/**
	 * @param domainID
	 * @param wideningOpName
	 * @param mergeOpName
	 * @return An abstract domain factory for the abstract domain system given by its ID
	 */
	private IAbstractDomainFactory<?> makeDomainFactory(String domainID, String wideningOpName, String mergeOpName) {
		if (domainID.equals(TopBottomDomainFactory.getDomainID()))
			return new TopBottomDomainFactory(m_logger);
		
		if (domainID.equals(BoolDomainFactory.getDomainID()))
			return new BoolDomainFactory(m_logger);
		
		if (domainID.equals(SignDomainFactory.getDomainID()))
			return new SignDomainFactory(m_logger, wideningOpName, mergeOpName);

		if (domainID.equals(IntervalDomainFactory.getDomainID()))
			return new IntervalDomainFactory(m_logger, new HashSet<String>(m_numbersForWidening), wideningOpName, mergeOpName);

		// default ADS: TOPBOTTOM
		m_logger.warn(String.format("Unknown abstract domain system \"%s\" chosen, using \"%s\" instead",
				domainID, TopBottomDomainFactory.getDomainID()));
		return new TopBottomDomainFactory(m_logger);
	}
}
