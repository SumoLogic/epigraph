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

package ws.epigraph.invocation.filters;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.invocation.InvocationError;
import ws.epigraph.invocation.InvocationErrorImpl;
import ws.epigraph.data.validation.DataValidationError;
import ws.epigraph.util.HttpStatusCode;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class FilterUtil {
  private FilterUtil() {}

  public static @NotNull InvocationError validationError(
      @NotNull Collection<? extends DataValidationError> validationErrors) {
    return new InvocationErrorImpl(

        validationErrors.stream().anyMatch(DataValidationError::isImplementationError)
        ? HttpStatusCode.INTERNAL_OPERATION_ERROR
        : HttpStatusCode.PRECONDITION_FAILED, validationErrors.stream()
            .map(DataValidationError::toString)
            .collect(Collectors.joining("\n"))

    );
  }
}
