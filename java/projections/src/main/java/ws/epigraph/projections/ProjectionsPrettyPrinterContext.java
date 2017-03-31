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
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionsPrettyPrinterContext<
    VP extends GenVarProjection<VP, ?, MP>,
    MP extends GenModelProjection<?, ?, ?, ?>> {

  private final @NotNull ProjectionReferenceName projectionsNamespace;
  private final Map<ProjectionReferenceName, VP> otherNamespaceVarProjections = new HashMap<>();
  private final Map<ProjectionReferenceName, MP> otherNamespaceModelProjections = new HashMap<>();

  public ProjectionsPrettyPrinterContext(final @NotNull ProjectionReferenceName namespace) {projectionsNamespace = namespace;}

  public boolean inNamespace(@NotNull ProjectionReferenceName projectionName) {
    return projectionName.removeLastSegment().equals(projectionsNamespace);
  }

  public void addOtherNamespaceVarProjection(@NotNull VP projection) {
    @SuppressWarnings("unchecked") final ProjectionReferenceName projectionName = projection.referenceName();

    assert projectionName != null;
    assert !inNamespace(projectionName);
    assert !otherNamespaceVarProjections.containsKey(projectionName) : projectionName.toString();

    otherNamespaceVarProjections.put(projectionName, projection);
  }

  public void addOtherNamespaceModelProjection(@NotNull MP projection) {
    @SuppressWarnings("unchecked") final ProjectionReferenceName projectionName = projection.referenceName();

    assert projectionName != null;
    assert !inNamespace(projectionName);
    assert !otherNamespaceModelProjections.containsKey(projectionName) : projectionName.toString();

    otherNamespaceModelProjections.put(projectionName, projection);
  }

  public Collection<VP> otherNamespaceVarProjections() { return otherNamespaceVarProjections.values(); }

  public Collection<MP> otherNamespaceModelProjections() { return otherNamespaceModelProjections.values(); }

  public void reset() { otherNamespaceVarProjections.clear(); }
}
