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

package ws.epigraph.projections.op;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParams {
  public static final OpParams EMPTY = new OpParams(Collections.emptyMap());

  private final @NotNull Map<String, OpParam> params;

  public static @NotNull OpParams fromMap(@Nullable Map<String, OpParam> params) {
    return params == null ? EMPTY : new OpParams(params);
  }

  public static @NotNull OpParams fromCollection(@Nullable Collection<OpParam> params) {
    return params == null ? EMPTY : new OpParams(params);
  }

  public OpParams(@NotNull Map<String, OpParam> params) {this.params = params;}

  public OpParams(OpParam... params) {
    this.params = new HashMap<>();
    for (OpParam param : params)
      this.params.put(param.name(), param);
  }

  public OpParams(@NotNull Collection<OpParam> params) {
    this.params = new HashMap<>();
    for (OpParam param : params)
      this.params.put(param.name(), param);
  }

  public boolean hasParam(@NotNull String name) { return params.containsKey(name); }

  public boolean isEmpty() { return params.isEmpty(); }

  public @Nullable OpParam get(@NotNull String key) { return params.get(key); }

  public @NotNull Map<String, OpParam> asMap() { return params; }

  public static @NotNull OpParams merge(@NotNull Stream<OpParams> paramsToMerge) {
    // using `reduce` would produce too much garbage

    Map<String, OpParam> entries = new HashMap<>();

    paramsToMerge.forEach(params -> {
      for (final Map.Entry<String, OpParam> entry : params.asMap().entrySet()) {
        String key = entry.getKey();
        if (!entries.containsKey(key))
          entries.put(key, entry.getValue());
      }
    });

    return new OpParams(entries);
  }

  public static @NotNull OpParams merge(@NotNull Collection<OpParams> paramsToMerge) {
    if (paramsToMerge.isEmpty()) return EMPTY;
    if (paramsToMerge.size() == 1) return paramsToMerge.iterator().next();

    return merge(paramsToMerge.stream());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OpParams opParams = (OpParams) o;
    return Objects.equals(params, opParams.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params);
  }
}
