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

/* Created by yegor on 9/27/16. */

package ws.epigraph.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Function;

public final class MappedIterator<O, V> implements Iterator<V> {

  private final @NotNull Iterator<? extends O> original;

  private final @NotNull Function<? super O, V> view;

  public MappedIterator(@NotNull Iterator<? extends O> original, @NotNull Function<? super O, V> view) {
    this.original = original;
    this.view = view;
  }

  @Override
  public boolean hasNext() { return original.hasNext(); }

  @Override
  public V next() { return view.apply(original.next()); }

  @Override
  public void remove() { original.remove(); }

}
