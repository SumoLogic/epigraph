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

package ws.epigraph.projections;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.projections.gen.GenProjectionReference;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UnresolvedReferenceException extends RuntimeException {
  private final @NotNull GenProjectionReference<?> reference;

  public UnresolvedReferenceException(final @NotNull GenProjectionReference<?> reference) {this.reference = reference;}

  public GenProjectionReference<?> reference() { return reference; }

  @Override
  public String getMessage() {
    Throwable allocationTrace = reference.allocationTrace();
    if (allocationTrace == null)
      return String.format("Unresolved reference '%s' (location: %s)", reference, reference.location());
    else {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      allocationTrace.printStackTrace(pw);
      return String.format(
          "Unresolved reference '%s' (location: %s) allocated at:\n%s",
          reference,
          reference.location(),
          allocationTrace
      );
    }

  }
}
