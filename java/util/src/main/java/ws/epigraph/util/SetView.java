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

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

/**
 * Unmodifiable mapped view of a set.
 *
 * @param <O> Original set element type
 * @param <V> View set element type
 */
public final class SetView<O, V> extends AbstractSet<V> {

  private final @NotNull Set<? extends O> original;

  private final @NotNull Function<O, V> view;

  public SetView(@NotNull Set<? extends O> original, @NotNull Function<O, V> view) {
    this.original = original;
    this.view = view;
  }

  @Override
  public @NotNull Iterator<V> iterator() { return new MappedIterator<>(original.iterator(), view); }

  @Override
  public int size() { return original.size(); }

}

