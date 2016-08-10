/* Created by yegor on 7/28/16. */

package io.epigraph.util;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Unmodifiable mapped collection view.
 *
 * @param <O> Original collection element type
 * @param <V> View collection element type
 */
public class CollectionView<O, V> extends AbstractCollection<V> {

  private final Collection<? extends O> original;

  private final Function<O, V> view;

  public CollectionView(@NotNull Collection<? extends O> original, Function<O, V> view) {
    this.original = original;
    this.view = view;
  }

  @Override
  public @NotNull Iterator<V> iterator() { return new MappedIterator<>(original.iterator(), view); }

  @Override
  public int size() { return original.size(); }


  private static class MappedIterator<O, V> implements Iterator<V> {

    private final Iterator<? extends O> iterator;

    private final Function<? super O, V> view;

    public MappedIterator(Iterator<? extends O> iterator, Function<? super O, V> view) {
      this.iterator = iterator;
      this.view = view;
    }

    @Override
    public boolean hasNext() { return iterator.hasNext(); }

    @Override
    public V next() { return view.apply(iterator.next()); }

  }


}
