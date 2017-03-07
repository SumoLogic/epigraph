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
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.TypeApi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings({"unchecked", "MissortedModifiers"})
public abstract class VarReferenceContext<VP extends GenVarProjection<VP, ?, ?>> {

  @NotNull
  private final Qn referencesNamespace;

  @Nullable
  private final VarReferenceContext<VP> parent;
  private final Map<Qn, VP> references = new HashMap<>();
  private final Map<Qn, TextLocation> resolvedAt = new HashMap<>();

  protected VarReferenceContext(
      @NotNull final Qn referencesNamespace,
      @Nullable final VarReferenceContext<VP> parent) {

    this.referencesNamespace = referencesNamespace;
    this.parent = parent;
  }

  @NotNull
  public VP reference(@NotNull TypeApi type, @NotNull String name, @NotNull TextLocation location) {
    Qn qnName = referencesNamespace.append(name);

    VP ref = lookupReference(name);

    if (ref == null) {
      ref = newReference(type, qnName, location);
      references.put(qnName, ref);
      return ref;
    } else return ref;

  }

  @Nullable
  protected VP lookupReference(@NotNull String name) {
    Qn qnName = referencesNamespace.append(name);
    VP ref = references.get(qnName);

    if (ref != null) return ref;
    if (parent == null) return null;
    else return parent.lookupReference(name);
  }

  @NotNull
  protected abstract VP newReference(@NotNull TypeApi type, @NotNull Qn name, @NotNull TextLocation location);

  public void resolve(
      @NotNull String name, @NotNull VP value, @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) {
    Qn qnName = referencesNamespace.append(name);
    VP ref = references.get(qnName);

    if (ref == null)
      context.addError(String.format("Projection '%s' reference not found", name), location);
    else if (ref.isResolved()) {
      context.addError(
          String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(qnName)), location
      );
    } else {
      resolvedAt.put(qnName, location);
      ref.resolve(value);
    }
  }

  public void ensureAllReferencesResolved(@NotNull PsiProcessingContext context) {
    for (final Map.Entry<Qn, VP> entry : references.entrySet()) {
      Qn qnName = entry.getKey();
      String name = qnName.last();
      assert name != null;
      final VP vp = entry.getValue();

      if (!vp.isResolved())
        context.addError(
            String.format("Projection '%s' is not defined", name),
            vp.location()

        );
    }
  }

}
