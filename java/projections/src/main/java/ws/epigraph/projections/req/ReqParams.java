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

package ws.epigraph.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqParams {
  public static final ReqParams EMPTY = new ReqParams(Collections.emptyMap());
  @NotNull
  private final Map<String, ReqParam> params;

  @NotNull
  public static ReqParams fromMap(@Nullable Map<String, ReqParam> params) {
    return params == null ? EMPTY : new ReqParams(params);
  }

  @NotNull
  public static ReqParams fromCollection(@Nullable Collection<ReqParam> params) {
    return params == null ? EMPTY : new ReqParams(params);
  }

  public ReqParams(@NotNull Map<String, ReqParam> params) {this.params = params;}

  public ReqParams(ReqParam... params) {
    this.params = new HashMap<>();
    for (ReqParam param : params)
      this.params.put(param.name(), param);
  }

  public ReqParams(@NotNull Collection<ReqParam> params) {
    this.params = new HashMap<>();
    for (ReqParam param : params)
      this.params.put(param.name(), param);
  }

  public boolean hasParam(@NotNull String name) { return params.containsKey(name); }

  public boolean isEmpty() { return params.isEmpty(); }

  @Nullable
  public ReqParam get(@NotNull String key) { return params.get(key); }

  @NotNull
  public Map<String, ReqParam> params() { return params; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqParams reqParams = (ReqParams) o;
    return Objects.equals(params, reqParams.params);
  }

  @Override
  public int hashCode() {
    return Objects.hash(params);
  }
}
