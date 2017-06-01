/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.invocation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Invocation results: either a success of type {@code R} or
 * an {@code InvocationError}
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("ConstantConditions")
public final class InvocationResult<R> {
  private final @Nullable R result;
  private final @Nullable InvocationError error;

  private InvocationResult(
      final @Nullable R result,
      final @Nullable InvocationError error) {
    this.result = result;
    this.error = error;
  }

  public static @NotNull <R>
  InvocationResult<R> success(@Nullable R result) {
    return new InvocationResult<>(result, null);
  }

  public static @NotNull <R>
  InvocationResult<R> failure(@NotNull InvocationError error) {
    return new InvocationResult<>(null, error);
  }

  @Contract(pure = true)
  public boolean isSuccess() { return error == null; }

  @Contract(pure = true)
  public boolean isFailure() { return error != null; }

  @Contract(pure = true)
  public @Nullable R result() { return result; }

  @Contract(pure = true)
  public @Nullable InvocationError error() { return error; }

  /**
   * Gets invocation result or throw a runtime exception if there was an invocation error
   *
   * @return operation invocation result
   * @throws OperationInvocationException if there was an invocation error
   */
  public @Nullable R get() throws OperationInvocationException {
    if (isSuccess()) return result;
    else throw new OperationInvocationException(error);
  }

  public void onSuccess(@NotNull Consumer<@Nullable R> c) { if (isSuccess()) c.accept(result); }

  public void onFailure(@NotNull Consumer<@NotNull InvocationError> c) { if (isFailure()) c.accept(error); }

  public <T> @NotNull T apply(
      @NotNull Function<@Nullable R, T> successFunction,
      @NotNull Function<@NotNull InvocationError, T> errorFunction) {

    return isSuccess() ? successFunction.apply(result) : errorFunction.apply(error);
  }

  public void consume(
      @NotNull Consumer<@Nullable R> successConsumer,
      @NotNull Consumer<@NotNull InvocationError> errorConsumer) {

    if (isSuccess()) successConsumer.accept(result);
    else errorConsumer.accept(error);
  }

  public <T> @NotNull InvocationResult<T> mapSuccess(@NotNull Function<@Nullable R, T> f) {
    return isSuccess() ? success(f.apply(result)) : failure(error);
  }
}
