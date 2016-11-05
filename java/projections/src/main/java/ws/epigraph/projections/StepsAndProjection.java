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

package ws.epigraph.projections;

import org.jetbrains.annotations.NotNull;

/**
 * Projection together with number of steps along the path. Projection should not have
 * any branching points in the first `steps` segments.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class StepsAndProjection<P> {
  private final int pathSteps;
  @NotNull
  private final P projection;

  public StepsAndProjection(int pathSteps, @NotNull P projection) {
    this.pathSteps = pathSteps;
    this.projection = projection;
  }

  public int pathSteps() {
    return pathSteps;
  }

  @NotNull
  public P projection() {
    return projection;
  }
}
