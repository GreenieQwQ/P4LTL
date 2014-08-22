package de.uni_freiburg.informatik.ultimatetest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.uni_freiburg.informatik.junit_helper.testfactory.FactoryTestMethod;
import de.uni_freiburg.informatik.ultimate.result.IResult;
import de.uni_freiburg.informatik.ultimate.util.ExceptionUtils;
import de.uni_freiburg.informatik.ultimatetest.decider.ITestResultDecider;
import de.uni_freiburg.informatik.ultimatetest.decider.ITestResultDecider.TestResult;
import de.uni_freiburg.informatik.ultimatetest.summary.ITestSummary;
import static org.junit.Assert.fail;

/**
 * @author dietsch
 * 
 */
public class UltimateTestCase {

	private String mName;
	private UltimateRunDefinition m_UltimateRunDefinition;
	private UltimateStarter mStarter;
	private ITestResultDecider mDecider;
	private ITestSummary mSummary;
	private Logger mLogger;

	public UltimateTestCase(UltimateStarter starter, ITestResultDecider decider, ITestSummary summary, String name,
			UltimateRunDefinition ultimateRunDefinition) {
		mLogger = Logger.getLogger(UltimateStarter.class);
		mStarter = starter;
		mName = name;
		mDecider = decider;
		mSummary = summary;
		m_UltimateRunDefinition = ultimateRunDefinition;
	}

	@FactoryTestMethod
	public void test() {

		Throwable th = null;

		TestResult result = TestResult.FAIL;

		try {
			mStarter.runUltimate();
			result = mDecider.getTestResult(mStarter.getServices());
		} catch (Throwable e) {
			th = e;
			result = mDecider.getTestResult(mStarter.getServices(), e);
			mLogger.fatal(String.format("There was an exception during the execution of Ultimate: %s%n%s", e,
					ExceptionUtils.getStackTrace(e)));
		} finally {
			// we need to obtain results here, because afterwards the run is
			// completed we cannot obtain the results any more
			HashMap<String, List<IResult>> ultimateIResults = 
					new HashMap<String, List<IResult>>(mStarter.getServices().getResultService().getResults());
			mStarter.complete();

			boolean success = mDecider.getJUnitTestResult(result);

			if (mSummary != null) {
				mSummary.addResult(result, success, mDecider.getResultCategory(), m_UltimateRunDefinition,
						mDecider.getResultMessage(), ultimateIResults);
			}

			if (!success) {
				String message = mDecider.getResultMessage();
				if (message == null) {
					message = "ITestResultDecider provided no message";
				}
				if (th != null) {
					message += " (Ultimate threw an Exception: " + th.getMessage() + ")";
				}
				fail(message);
			}
		}
	}

	@Override
	public String toString() {
		return mName;
	}
}