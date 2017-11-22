/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqKeyProjection {
  private final @NotNull Datum value;
  private final @NotNull ReqParams params;
  private final @NotNull Directives directives;
  private final @NotNull TextLocation location;

  public ReqKeyProjection(
      @NotNull Datum value,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull TextLocation location) {
    this.value = value;
    this.params = params;
    this.directives = directives;
    this.location = location;
  }

  public @NotNull Datum value() { return value; }

  public @NotNull ReqParams params() { return params; }

  public @NotNull Directives directives() { return directives; }

  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqKeyProjection that = (ReqKeyProjection) o;
    return Objects.equals(value, that.value) &&
           Objects.equals(params, that.params) &&
           Objects.equals(directives, that.directives);
  }

  @Override
  public int hashCode() { return Objects.hash(value, params, directives); }

  public static <RKP extends ReqKeyProjection> List<RKP> merge(
      @NotNull Stream<RKP> keysToMerge,
      @NotNull MergedReqKeyFactory<RKP> factory) {

//    if (keysToMerge.size() < 2) return keysToMerge;

    Map<Datum, List<RKP>> groupedKeysToMerge = new LinkedHashMap<>();
    keysToMerge.forEach(key -> {
      final @NotNull Datum keyValue = key.value();
      Collection<RKP> group = groupedKeysToMerge.computeIfAbsent(keyValue, k -> new ArrayList<>());
      group.add(key);
    });

    List<RKP> mergedKeys = new ArrayList<>(groupedKeysToMerge.size());
    for (final Map.Entry<Datum, List<RKP>> entry : groupedKeysToMerge.entrySet()) {
      Datum keyValue = entry.getKey();
      List<RKP> group = entry.getValue();
      mergedKeys.add(
          factory.create(
              group,
              keyValue,
              ReqParams.merge(group.stream().map(ReqKeyProjection::params)),
              Directives.merge(group.stream().map(ReqKeyProjection::directives))
          )
      );
    }

    return mergedKeys;
  }

  public interface MergedReqKeyFactory<RKP extends ReqKeyProjection> {
    RKP create(
        @NotNull List<RKP> keysToMerge,
        @NotNull Datum value,
        @NotNull ReqParams mergedParams,
        @NotNull Directives mergedDirectives);
  }
}
