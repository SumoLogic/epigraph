/* Created by yegor on 8/12/16. */

package io.epigraph.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;
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
