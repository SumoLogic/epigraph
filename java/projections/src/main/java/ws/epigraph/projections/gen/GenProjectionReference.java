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

package ws.epigraph.projections.gen;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.TypeApi;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenProjectionReference<R extends GenProjectionReference</*R*/?>> {
  /** @return qualified projection reference name or {@code null} if there is no name */
  ProjectionReferenceName referenceName();

  void setReferenceName(@NotNull ProjectionReferenceName referenceName);

  /**
   * Resolves this projection reference from another instance.
   *
   * @param name  projection reference name
   * @param value projection instance to copy state from
   * @see #referenceName()
   */
  void resolve(ProjectionReferenceName name, @NotNull R value);

  /**
   * Checks if this projection is resolved, i.e. it's not an empty placeholder instance.
   *
   * @return {@code false} iff this is a reference and is not resolved
   * @see #referenceName()
   */
  boolean isResolved();

  /**
   * Registers a callback to be called after this reference is resolved. Multiple callbacks may be registered.
   *
   * @param callback callback to call when this reference is resolved
   */
  void runOnResolved(@NotNull Runnable callback);

  @NotNull TypeApi type();

  @NotNull TextLocation location();
}
