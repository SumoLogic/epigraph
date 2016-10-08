/* Created by yegor on 9/27/16. */

package io.epigraph.util;

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
