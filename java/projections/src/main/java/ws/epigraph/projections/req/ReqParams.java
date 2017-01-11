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
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqParams {
  public static final ReqParams EMPTY = new ReqParams(Collections.emptyMap());
  private final @NotNull Map<String, ReqParam> params;

  public static @NotNull ReqParams fromMap(@Nullable Map<String, ReqParam> params) {
    return params == null ? EMPTY : new ReqParams(params);
  }

  public static @NotNull ReqParams fromCollection(@Nullable Collection<ReqParam> params) {
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

  public @Nullable ReqParam get(@NotNull String key) { return params.get(key); }

  public @NotNull Map<String, ReqParam> asMap() { return params; }

  public static @NotNull ReqParams merge(@NotNull Stream<ReqParams> paramsToMerge) {
    Map<String,ReqParam> entries = new HashMap<>();
    
    paramsToMerge.forEach(params -> {
      for (final Map.Entry<String, ReqParam> entry : params.asMap().entrySet()) {
        String key = entry.getKey();
        if (!entries.containsKey(key))
          entries.put(key, entry.getValue());
      }
    });

    return new ReqParams(entries);
  }
  
  public static @NotNull ReqParams merge(@NotNull Collection<ReqParams> paramsToMerge) {
    if (paramsToMerge.isEmpty()) return EMPTY;
    if (paramsToMerge.size() == 1) return paramsToMerge.iterator().next();

    return merge(paramsToMerge.stream());
  }

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
