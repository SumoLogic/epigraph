/* Created by yegor on 7/22/16. */

package io.epigraph.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class LazyInitializer<T> {

  private final AtomicReference<T> reference = new AtomicReference<>(null);

  private @Nullable Supplier<T> supplier;

  public LazyInitializer(@NotNull Supplier<T> supplier) { this.supplier = supplier; }

  public T get() {
    @Nullable T value = reference.get();
    Supplier<T> localSupplier = supplier;
    if (localSupplier != null) {
      if (value == null) {
        value = Objects.requireNonNull(localSupplier.get());
        if (reference.compareAndSet(null, value)) {
          supplier = null; // release reference to supplier (so it can be garbage-collected)
        } else {
          value = reference.get();
        }
      }
    }
    assert value != null;
    return value;
  }

}
