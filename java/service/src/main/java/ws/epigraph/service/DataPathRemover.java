/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.service;

import ws.epigraph.data.*;
import ws.epigraph.errors.ErrorValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class DataPathRemover { // todo move somewhere?

  public static @NotNull PathRemovalResult removePath(@NotNull Data data, int steps) throws AmbiguousPathException {
    if (steps == 0) return new PathRemovalResult(data);
    final Map<@NotNull String, @NotNull ? extends Val> tagValues = data._raw().tagValues();
    switch (tagValues.size()) {
      case 0:return PathRemovalResult.NULL;
      case 1:return removePath(tagValues.values().iterator().next(), steps - 1);
      default: throw new AmbiguousPathException();
    }
  }

  public static @NotNull PathRemovalResult removePath(@NotNull Val val, int steps) throws AmbiguousPathException {
    if (val.getError() != null) return new PathRemovalResult(val.getError()); // error on any segment = fail?
    @Nullable final Datum datum = val.getDatum();
    return (datum == null) ? PathRemovalResult.NULL : removePath(datum, steps);
  }

  public static @NotNull PathRemovalResult removePath(@NotNull Datum datum, int steps) throws AmbiguousPathException {
    if (steps == 0) return new PathRemovalResult(datum);
    final Map<?, @NotNull ? extends Data> map;
    if (datum instanceof RecordDatum) map = ((RecordDatum) datum)._raw().fieldsData();
    else if (datum instanceof MapDatum) map = ((MapDatum) datum)._raw().elements();
    else throw new AmbiguousPathException(); // don't know how to drill into anything else // TODO better exception
    switch (map.size()) {
      case 0: return PathRemovalResult.NULL;
      case 1: return removePath(map.values().iterator().next(), steps - 1);
      default: throw new AmbiguousPathException();
    }
  }

  public static class PathRemovalResult { // TODO use inheritance instead of null members
    public static final PathRemovalResult NULL = new PathRemovalResult(null, null, null);

    public final @Nullable Data data;
    public final @Nullable Datum datum;
    public final @Nullable ErrorValue error;

    public PathRemovalResult(@Nullable Data data, @Nullable Datum datum, @Nullable ErrorValue error) {
      this.data = data;
      this.datum = datum;
      this.error = error;
    }

    public PathRemovalResult(@Nullable Data data) { this(data, null, null); }

    public PathRemovalResult(@Nullable Datum datum) { this(null, datum, null); }

    public PathRemovalResult(@Nullable ErrorValue error) { this(null, null, error); }
  }
}
