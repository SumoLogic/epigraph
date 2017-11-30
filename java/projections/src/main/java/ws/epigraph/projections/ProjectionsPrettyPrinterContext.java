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
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.gen.GenProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionsPrettyPrinterContext<P extends GenProjection<?, ?, ?, ?>> {
  private final @NotNull ProjectionReferenceName namespace;
  private final @Nullable ProjectionsPrettyPrinterContext<P> parent;
  private final Map<ProjectionReferenceName, P> projections = new HashMap<>();

  public ProjectionsPrettyPrinterContext(
      final @NotNull ProjectionReferenceName namespace,
      final @Nullable ProjectionsPrettyPrinterContext<P> parent) {
    this.namespace = namespace;
    this.parent = parent;

//    if (parent !=null && !namespace.startsWith(parent.namespace))
//      throw new IllegalArgumentException(String.format(
//          "'%s' context can't be a child of '%s' context",
//          namespace,
//          parent.namespace
//      ));
  }

  public @Nullable ProjectionsPrettyPrinterContext<P> parent() { return parent; }

  public boolean inNamespace(@Nullable ProjectionReferenceName projectionName) {
    return projectionName != null && projectionName.removeLastSegment().equals(namespace);
  }

  public void addProjection(@NotNull P projection) {
    @SuppressWarnings("unchecked") final ProjectionReferenceName projectionName = projection.referenceName();

    if (inNamespace(projectionName))
      projections.put(projectionName, projection);
    else if (parent != null)
      parent.addProjection(projection);
    else
      throw new IllegalArgumentException(String.format(
          "Can't add '%s' projection to '%s' printer context",
          projectionName,
          namespace
      ));
  }

  public @NotNull Collection<P> entityProjections() { return projections.values(); }

}
