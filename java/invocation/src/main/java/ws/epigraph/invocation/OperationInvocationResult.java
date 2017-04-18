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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Operation invocation results: either a success of type {@code Rsp} or
 * an {@code OperationInvocationError}
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationInvocationResult<R> {
  private final @Nullable R result;
  private final @Nullable OperationInvocationError error;

  private OperationInvocationResult(
      final @Nullable R result,
      final @Nullable OperationInvocationError error) {
    this.result = result;
    this.error = error;
  }

  public static <R>
  OperationInvocationResult<R> success(@NotNull R result) {
    return new OperationInvocationResult<>(result, null);
  }

  public static <R>
  OperationInvocationResult<R> failure(@NotNull OperationInvocationError error) {
    return new OperationInvocationResult<>(null, error);
  }

  public boolean isSuccess() { return result != null; }

  public boolean isFailure() { return error != null; }

  public @Nullable R result() { return result; }

  public @Nullable OperationInvocationError error() { return error; }

  public void onSuccess(@NotNull Consumer<R> c) { if (isSuccess()) c.accept(result); }

  public void onFailure(@NotNull Consumer<OperationInvocationError> c) { if (isFailure()) c.accept(error); }

  public <T> @NotNull T apply(
      @NotNull Function<R, T> successFunction,
      @NotNull Function<OperationInvocationError, T> errorFunction) {
    return isSuccess() ? successFunction.apply(result) : errorFunction.apply(error);
  }

  public void consume(
      @NotNull Consumer<R> successConsumer,
      @NotNull Consumer<OperationInvocationError> errorConsumer) {
    if (isSuccess()) successConsumer.accept(result);
    else errorConsumer.accept(error);
  }

  public <T> @NotNull OperationInvocationResult<T> mapSuccess(@NotNull Function<R, T> f) {
    return error == null ? success(f.apply(result)) : failure(error);
  }
}
