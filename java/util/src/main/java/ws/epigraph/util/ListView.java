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

/* Created by yegor on 8/12/16. */

package ws.epigraph.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

/**
 * Modifiable mapped view of modifiable list. Optimized for random access original lists.
 *
 * @param <O> Original list element type
 * @param <V> View list element type
 */
public final class ListView<O, V> extends AbstractList<V> {

  private final @NotNull List<@NotNull O> original;

  private final @NotNull Function<@NotNull O, @Nullable V> view;

  private final @NotNull Function<@Nullable V, @NotNull O> originalElementConstructor;

  public ListView(
      @NotNull List<O> original,
      @NotNull Function<@NotNull O, @Nullable V> view,
      @NotNull Function<@Nullable V, @NotNull O> originalElementConstructor
  ) {
    this.original = original;
    this.view = view;
    this.originalElementConstructor = originalElementConstructor;
  }

  @Override
  public V get(int index) { return view.apply(original.get(index)); }

  @Override
  public int size() { return original.size(); }

  @Override
  public V set(int index, V element) { // TODO take merger in constructor?
    return view.apply(original.set(index, originalElementConstructor.apply(element)));
  }

  @Override
  public void add(int index, V element) { original.add(index, originalElementConstructor.apply(element)); }

  @Override
  public V remove(int index) { return view.apply(original.remove(index)); }

}
