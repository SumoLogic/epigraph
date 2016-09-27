/* Created by yegor on 9/27/16. */

package io.epigraph.util;

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

