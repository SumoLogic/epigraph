/* Created by yegor on 7/22/16. */

package io.epigraph.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyInitializer<T> {

  private final AtomicReference<T> reference = new AtomicReference<>(null);

  @Nullable
  private Supplier<T> supplier;

  public LazyInitializer(@NotNull Supplier<T> supplier) {
    this.supplier = supplier;
  }

  public T get() {
    T value = reference.get();
    if (supplier != null) {
      if (value == null) {
        value = Objects.requireNonNull(supplier.get());
        if (reference.compareAndSet(null, value)) {
          supplier = null;
        } else {
          value = reference.get();
        }
      }
    }
    assert value != null;
    return value;
  }

}
