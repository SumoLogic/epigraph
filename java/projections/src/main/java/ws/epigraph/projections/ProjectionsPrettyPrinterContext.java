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
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.gen.GenVarProjection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionsPrettyPrinterContext<VP extends GenVarProjection<VP, ?, ?>> {
  private final @NotNull Qn projectionsNamespace;
  private final Map<Qn, VP> otherNamespaceProjections = new HashMap<>();

  public ProjectionsPrettyPrinterContext(final @NotNull Qn namespace) {projectionsNamespace = namespace;}

  public boolean inNamespace(@NotNull Qn projectionName) {
    return projectionName.removeLastSegment().equals(projectionsNamespace);
  }

  public void addOtherNamespaceProjection(@NotNull VP projection) {
    @SuppressWarnings("unchecked")
    final Qn projectionName = projection.name();

    assert projectionName != null;
    assert !inNamespace(projectionName);
    assert !otherNamespaceProjections.containsKey(projectionName) : projectionName.toString();

    otherNamespaceProjections.put(projectionName, projection);
  }

  public boolean isEmpty() { return otherNamespaceProjections.isEmpty(); }

  public Collection<VP> otherNamespaceProjections() {
    return otherNamespaceProjections.values();
  }

  public void reset() { otherNamespaceProjections.clear(); }
}
