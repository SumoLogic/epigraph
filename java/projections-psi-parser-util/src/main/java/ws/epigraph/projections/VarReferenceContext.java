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
  private final Map<String, VP> references = new HashMap<>();
  private final Map<String, TextLocation> resolvedAt = new HashMap<>();

  protected VarReferenceContext(
      @NotNull final Qn referencesNamespace,
      @Nullable final VarReferenceContext<VP> parent) {

    this.referencesNamespace = referencesNamespace;
    this.parent = parent;
  }

  @NotNull
  public VP reference(@NotNull TypeApi type, @NotNull String name, boolean useParent, @NotNull TextLocation location) {
    VP ref = useParent ? lookupReference(name) : references.get(name);

    if (ref == null) {
      ref = newReference(type, location);
      references.put(name, ref);
      return ref;
    } else return ref;
  }

  public boolean exists(@NotNull String name) { return references.containsKey(name); }

  @Nullable
  protected VP lookupReference(@NotNull String name) {
    VP ref = references.get(name);

    if (ref != null) return ref;
    if (parent == null) return null;
    else return parent.lookupReference(name);
  }

  @NotNull
  protected abstract VP newReference(@NotNull TypeApi type, @NotNull TextLocation location);

  public void resolve(
      @NotNull String name, @NotNull VP value, @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) {

    VP ref = references.get(name);

    if (ref == null) {

      if (parent != null && parent.lookupReference(name) != null) {

        // a = c
        //    b = a
        //    a = 2         <-- prohibited
        // c = 1

        context.addError(String.format("Can't override projection '%s' from parent context", name), location);

      } else
        context.addError(String.format("Projection '%s' reference not found", name), location);

    } else if (ref.isResolved()) {
      context.addError(
          String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
      );
    } else {
      resolvedAt.put(name, location);
      ref.resolve(referencesNamespace.append(name), value);
    }

  }

  public void ensureAllReferencesResolved(@NotNull PsiProcessingContext context) {
    for (final Map.Entry<String, VP> entry : references.entrySet()) {
      String name = entry.getKey();
      assert name != null;
      final VP vp = entry.getValue();

      if (!vp.isResolved()) {
        // delegate it to parent if possible:
        //
        //    b = a
        // a = 1

        if (parent == null) {
          context.addError(String.format("Projection '%s' is not defined", name), vp.location());
        } else {
          final VP parentRef = parent.references.get(name);
          if (parentRef == null) {
            parent.references.put(name, vp);
          } else if (parentRef != vp) {
            //?? can't happen
            context.addError(
                String.format(
                    "Internal error: different references to projection '%s' in this and parent context",
                    name
                ),
                vp.location()
            );
          }
        }
      }
    }
  }

}
