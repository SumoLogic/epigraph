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

package ws.epigraph.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CachingFunction<F, T> implements Function<F, T> {
  private final @NotNull Function<F, T> delegate;
  private final Map<F, T> cache = new ConcurrentHashMap<>();

  public CachingFunction(final @NotNull Function<F, T> delegate) {this.delegate = delegate;}

  @Override
  public T apply(final F f) {
    T res = cache.get(f);

    if (res == null) {
      res = delegate.apply(f);
      cache.put(f, res);
    }

    return res;
  }
}
