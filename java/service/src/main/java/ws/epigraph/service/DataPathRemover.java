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

  @NotNull
  public static PathRemovalResult removePath(@NotNull Data data, int steps) throws AmbiguousPathException {
    if (steps == 0) return new PathRemovalResult(data);

    final Map<@NotNull String, @NotNull ? extends Val> tagValues = data._raw().tagValues();
    if (tagValues.size() == 0) return PathRemovalResult.NULL;
    if (tagValues.size() > 1) throw new AmbiguousPathException();

    final Val val = tagValues.values().iterator().next();
    return removePath(val, steps - 1);
  }

  @NotNull
  public static PathRemovalResult removePath(@NotNull Val val, int steps) throws AmbiguousPathException {
    if (val.getError() != null) return new PathRemovalResult(val.getError()); // error on any segment = fail?
    @Nullable final Datum datum = val.getDatum();
    if (datum == null) return PathRemovalResult.NULL;

    return removePath(datum, steps);
  }

  @NotNull
  public static PathRemovalResult removePath(@NotNull Datum datum, int steps) throws AmbiguousPathException {
    if (steps == 0) return new PathRemovalResult(datum);

    if (datum instanceof RecordDatum) {
      RecordDatum record = (RecordDatum) datum;

      final Map<String, ? extends Data> fields = record._raw().fieldsData();
      if (fields.size() == 0) return PathRemovalResult.NULL;
      if (fields.size() > 1) throw new AmbiguousPathException();

      final Data data = fields.values().iterator().next();
      return removePath(data, steps - 1);
    }

    if (datum instanceof MapDatum) {
      MapDatum map = (MapDatum) datum;

      final Map<Datum.Imm, @NotNull ? extends Data> elements = map._raw().elements();
      if (elements.size() == 0) return PathRemovalResult.NULL;
      if (elements.size() > 1) throw new AmbiguousPathException();

      final Data data = elements.values().iterator().next();
      return removePath(data, steps - 1);
    }

    throw new AmbiguousPathException(); // don't know how to drill into anything else
  }

  public static class PathRemovalResult {
    public static final PathRemovalResult NULL = new PathRemovalResult(null, null, null);

    @Nullable
    public final Data data;
    @Nullable
    public final Datum datum;
    @Nullable
    public final ErrorValue error;

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
