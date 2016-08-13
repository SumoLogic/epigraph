/* Created by yegor on 8/12/16. */

package io.epigraph.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Util {

  static <T, R> @Nullable R apply(@NotNull Function<T, @Nullable R> function, @Nullable T arg) {
    return arg == null ? null : function.apply(arg);
  }

  static <T, R> @NotNull R apply(
      @NotNull Function<@NotNull T, @NotNull R> function,
      @Nullable T arg,
      @NotNull R ifNull
  ) {
    return arg == null ? ifNull : function.apply(arg);
  }

}
