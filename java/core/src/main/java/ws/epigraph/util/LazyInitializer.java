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

/* Created by yegor on 7/22/16. */

package ws.epigraph.util;

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
