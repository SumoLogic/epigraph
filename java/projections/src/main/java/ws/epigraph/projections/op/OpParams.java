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

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParams {
  public static final OpParams EMPTY = new OpParams(Collections.emptyMap());

  @NotNull
  private final Map<String, OpParam> params;

  @NotNull
  public static OpParams fromMap(@Nullable Map<String, OpParam> params) {
    return params == null ? EMPTY : new OpParams(params);
  }

  @NotNull
  public static OpParams fromCollection(@Nullable Collection<OpParam> params) {
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

  @Nullable
  public OpParam get(@NotNull String key) { return params.get(key); }

  @NotNull
  public Map<String, OpParam> params() { return params; }

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
