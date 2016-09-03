/* Created by yegor on 8/12/16. */

package io.epigraph.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public interface Util {

  @Deprecated
  static <T, R> @Nullable R apply(@NotNull Function<T, @Nullable R> function, @Nullable T arg) {
    return arg == null ? null : function.apply(arg);
  }

  static <T, R> @Nullable R apply(@Nullable T arg, @NotNull Function<T, @Nullable R> function) {
    return arg == null ? null : function.apply(arg);
  }

  @Deprecated
  static <T, R> @NotNull R apply(
      @NotNull Function<@NotNull T, @NotNull R> function,
      @Nullable T arg,
      @NotNull R ifNull
  ) { return arg == null ? ifNull : function.apply(arg); }

  static <T, R> @NotNull R apply(
      @Nullable T arg,
      @NotNull Function<@NotNull T, @NotNull R> function,
      @NotNull R ifNull
  ) { return arg == null ? ifNull : function.apply(arg); }

  static <T> java.util.List<T> cast(java.util.List<? super T> list) { return (List<T>) list; }

}
