/* Created by yegor on 8/12/16. */

package io.epigraph.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Modifiable mapped view of modifiable list. Optimized for random access original lists.
 *
 * @param <O> Original list element type
 * @param <V> View list element type
 */
public final class ListView<O, V> extends AbstractList<V> {

  private final @NotNull List<@NotNull O> original;

  private final @NotNull Function<@NotNull O, @Nullable V> view;

  private final @NotNull BiConsumer<@NotNull ? super O, @Nullable ? super V> merger;

  private final @NotNull Supplier<@NotNull O> originalElementConstructor;

  public ListView(
      @NotNull List<O> original,
      @NotNull Function<@NotNull O, @Nullable V> view,
      @NotNull BiConsumer<@NotNull ? super O, @Nullable ? super V> merger,
      @NotNull Supplier<@NotNull O> originalElementConstructor
  ) {
    this.original = original;
    this.view = view;
    this.merger = merger;
    this.originalElementConstructor = originalElementConstructor;
  }

  @Override
  public V get(int index) { return view.apply(original.get(index)); }

  @Override
  public int size() { return original.size(); }

  @Override
  public V set(int index, V element) {
    O originalElement = original.get(index);
    V old = view.apply(originalElement);
    merger.accept(originalElement, element);
    return old;
  }

  @Override
  public void add(int index, V element) {
    O originalElement = originalElementConstructor.get();
    merger.accept(originalElement, element);
    original.add(index, originalElement);
  }

  @Override
  public V remove(int index) { return view.apply(original.remove(index)); }

}
