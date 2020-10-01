/*
 * Copyright (C) 2018-2019 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2018-2019 University of Freiburg
 *
 * This file is part of the ULTIMATE Library-srParse plug-in.
 *
 * The ULTIMATE Library-srParse plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE Library-srParse plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Library-srParse plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE Library-srParse plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE Library-srParse plug-in grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.lib.srparse.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.lib.pea.CDD;
import de.uni_freiburg.informatik.ultimate.lib.pea.CounterTrace;
import de.uni_freiburg.informatik.ultimate.lib.pea.CounterTrace.BoundTypes;
import de.uni_freiburg.informatik.ultimate.lib.pea.CounterTrace.DCPhase;
import de.uni_freiburg.informatik.ultimate.lib.pea.PhaseEventAutomata;
import de.uni_freiburg.informatik.ultimate.lib.pea.Trace2PeaCompilerStateless;
import de.uni_freiburg.informatik.ultimate.lib.srparse.SrParseScope;
import de.uni_freiburg.informatik.ultimate.util.datastructures.relation.Pair;

/**
 * All Hanfor patterns have this type as base type.
 *
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 *
 */
public abstract class PatternType<T extends PatternType> {

	private final List<CDD> mCdds;
	private final List<String> mDurations;
	private final SrParseScope mScope;
	private final String mId;

	private ReqPeas mPEAs;

	public PatternType(final SrParseScope scope, final String id, final List<CDD> cdds, final List<String> durations) {
		mScope = scope;
		mId = id;
		mCdds = cdds;
		mDurations = durations;
	}

	public abstract T create(final SrParseScope scope, final String id, final List<CDD> cdds,
			final List<String> durations);

	public List<String> getDuration() {
		return Collections.unmodifiableList(mDurations);
	}

	public List<CDD> getCdds() {
		return Collections.unmodifiableList(mCdds);
	}

	public CDD[] getCddsAsArray() {
		return getCdds().toArray(new CDD[getCdds().size()]);
	}

	private String[] getDurationsAsArray() {
		return getDuration().toArray(new String[getDuration().size()]);
	}

	private int[] getDurationsAsIntArray(final Map<String, Integer> id2bounds) {
		final String[] durations = getDurationsAsArray();
		final int[] rtr = new int[durations.length];
		for (int i = 0; i < durations.length; ++i) {
			rtr[i] = parseDuration(durations[i], id2bounds);
		}
		return rtr;
	}

	public String getId() {
		return mId;
	}

	public SrParseScope getScope() {
		return mScope;
	}

	public abstract PatternType rename(String newName);

	public ReqPeas transformToPea(final ILogger logger, final Map<String, Integer> id2bounds) {
		if (mPEAs == null) {
			final List<CounterTrace> cts = constructCounterTrace(id2bounds);
			final String name = getId() + "_" + createPeaSuffix();

			final List<Entry<CounterTrace, PhaseEventAutomata>> peas = new ArrayList<>(cts.size());
			for (final CounterTrace ct : cts) {
				final Trace2PeaCompilerStateless compiler =
						new Trace2PeaCompilerStateless(logger, name, ct, id2bounds.keySet());
				peas.add(new Pair<>(ct, compiler.getResult()));
			}
			mPEAs = new ReqPeas(this, peas);
		}
		return mPEAs;
	}

	public List<CounterTrace> constructCounterTrace(final Map<String, Integer> id2bounds) {
		final CDD[] cdds = getCddsAsArray();
		final int[] durations = getDurationsAsIntArray(id2bounds);
		assert cdds.length == getExpectedCddSize() : "Wrong number of observables for pattern " + getName();
		assert durations.length == getExpectedDurationSize() : "Wrong number of durations for pattern " + getName();
		return transform(cdds, durations);
	}

	protected abstract List<CounterTrace> transform(CDD[] cdds, int[] durations);

	public abstract int getExpectedCddSize();

	public abstract int getExpectedDurationSize();

	public String getName() {
		return getClass().getSimpleName();
	}

	protected int parseDuration(final String duration, final Map<String, Integer> id2bounds) {
		if (duration == null) {
			throw new IllegalArgumentException("Duration cannot be null");
		}
		try {
			return Integer.parseInt(duration);
		} catch (final NumberFormatException nfe) {
			if (id2bounds == null) {
				throw new IllegalArgumentException("Cannot parse duration and no alternative bounds are given");
			}
			final Integer actualDuration = id2bounds.get(duration);
			if (actualDuration == null) {
				throw new IllegalArgumentException(
						mId + ": Cannot parse duration and alternative bounds do not contain " + duration);
			}
			return actualDuration;
		}
	}

	protected static CounterTrace counterTrace(final CounterTrace.DCPhase... phases) {
		if (phases == null || phases.length == 0) {
			throw new IllegalArgumentException("Need at least one phase");
		}
		return new CounterTrace(phases);
	}

	protected static CounterTrace counterTrace(final CDD... cdds) {
		if (cdds == null || cdds.length == 0) {
			throw new IllegalArgumentException("Need at least one phase");
		}
		final DCPhase[] phases = new DCPhase[cdds.length];
		for (int i = 0; i < cdds.length; ++i) {
			phases[i] = phase(cdds[i]);
		}
		return new CounterTrace(phases);
	}

	protected static DCPhase phase(final CDD x) {
		return new CounterTrace.DCPhase(x);
	}

	protected static DCPhase phase(final CDD x, final BoundTypes boundType, final int duration) {
		return new CounterTrace.DCPhase(x, boundType.asValue(), duration);
	}

	/**
	 * @return A phase with bound that can be empty
	 */
	protected static DCPhase phaseE(final CDD x, final BoundTypes boundType, final int duration) {
		return new CounterTrace.DCPhase(cddT(), x, boundType.asValue(), duration, Collections.emptySet(), true);
	}

	/**
	 * @return A true-{@link DCPhase} that can be empty.
	 */
	protected static DCPhase phaseT() {
		return new CounterTrace.DCPhase();
	}

	protected static CDD cddT() {
		return CDD.TRUE;
	}

	private String createPeaSuffix() {
		final String suffix;
		if (mScope == null) {
			suffix = "NoScope";
		} else {
			// remove SrParseScope from scope class name
			suffix = mScope.getClass().getSimpleName().substring(12);
		}
		final String className = getClass().getSimpleName();
		return suffix + "_" + className.replaceAll("Pattern", "");
	}

	@Override
	public String toString() {
		assert getScope() != null || this instanceof InitializationPattern;
		if (getScope() == null) {
			return getClass().toString();
		}
		return getScope().toString() + getClass().toString();
	}

	@Override
	public int hashCode() {
		// note that mId and mPea are deliberately not part of the hash code or the
		// equality comparison
		final int prime = 31;
		int result = 1;
		result = prime * result + (mCdds == null ? 0 : mCdds.hashCode());
		result = prime * result + (mDurations == null ? 0 : mDurations.hashCode());
		result = prime * result + (mScope == null ? 0 : mScope.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		// note that mId and mPea are deliberately not part of the hash code or the
		// equality comparison
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PatternType other = (PatternType) obj;
		if (mDurations == null) {
			if (other.mDurations != null) {
				return false;
			}
		} else if (!mDurations.equals(other.mDurations)) {
			return false;
		}
		if (mScope == null) {
			if (other.mScope != null) {
				return false;
			}
		} else if (!mScope.equals(other.mScope)) {
			return false;
		}
		if (mCdds == null) {
			if (other.mCdds != null) {
				return false;
			}
		} else if (!mCdds.equals(other.mCdds)) {
			return false;
		}
		return true;
	}

	/**
	 *
	 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
	 *
	 */
	public static final class ReqPeas {
		private final PatternType mReq;
		private final List<Entry<CounterTrace, PhaseEventAutomata>> mPeas;

		public ReqPeas(final PatternType req, final List<Entry<CounterTrace, PhaseEventAutomata>> peas) {
			mReq = Objects.requireNonNull(req);
			mPeas = Collections.unmodifiableList(Objects.requireNonNull(peas));
			if (mPeas.isEmpty()) {
				throw new IllegalArgumentException("peas is empty");
			}
		}

		public PatternType getPattern() {
			return mReq;
		}

		public List<Entry<CounterTrace, PhaseEventAutomata>> getCounterTrace2Pea() {
			return mPeas;
		}
	}
}
