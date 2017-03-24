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
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenProjectionReference;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.types.TypeApi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings({"unchecked", "MissortedModifiers"})
public abstract class ReferenceContext<
    VP extends GenVarProjection<VP, ?, MP>,
    MP extends GenModelProjection<?, ?, ?, ?>
    > {

  @NotNull
  private final Qn referencesNamespace;
  @NotNull
  private final PsiProcessingContext psiProcessingContext;

  @Nullable
  private final ReferenceContext<VP, MP> parent;
  private final Map<String, GenProjectionReference<?>> references = new HashMap<>();
  private final Map<String, TextLocation> resolvedAt = new HashMap<>();

  protected ReferenceContext(
      @NotNull final Qn referencesNamespace,
      @Nullable final ReferenceContext<VP, MP> parent,
      @NotNull PsiProcessingContext context) {

    this.referencesNamespace = referencesNamespace;
    this.parent = parent;
    psiProcessingContext = context;
  }

  @NotNull
  public VP varReference(
      @NotNull TypeApi type,
      @NotNull String name,
      boolean useParent,
      @NotNull TextLocation location) {
    VP ref = lookupVarReference(name, useParent);

    if (ref == null) {
      ref = newVarReference(type, location);
      references.put(name, ref);
      return ref;
    } else return ref;
  }

  public boolean isResolved(@NotNull String name) {
    final GenProjectionReference<?> reference = lookupReference(name, true);
    return reference != null && reference.isResolved();
  }

  @Nullable
  protected VP lookupVarReference(@NotNull String name, boolean useParent) {
    final GenProjectionReference<?> reference = lookupReference(name, useParent);

    if (reference == null)
      return null;

    else if (reference instanceof GenVarProjection)
      return (VP) reference;

    else if (reference instanceof GenModelProjection)
      return toSelfVar((MP) reference);

    throw new RuntimeException(String.format("Unreachable: '%s'", name));
  }

  @Nullable
  protected GenProjectionReference<?> lookupReference(@NotNull String name, boolean useParent) {
    GenProjectionReference<?> ref = references.get(name);

    if (ref != null) return ref;
    if (!useParent || parent == null) return null;
    else return parent.lookupReference(name, true);
  }

  @NotNull
  protected abstract VP newVarReference(@NotNull TypeApi type, @NotNull TextLocation location);

  public <R extends GenProjectionReference<R>> void resolveVar(
      @NotNull final String name,
      @NotNull final VP value,
      @NotNull final TextLocation location,
      @NotNull final PsiProcessingContext context) {

    value.runOnResolved(() -> {
      R ref = (R) references.get(name);

      if (ref == null) {

        if (parent != null && parent.lookupReference(name, true) != null) {

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
      } else if (!ref.type().isAssignableFrom(value.type())) {
        context.addError(
            String.format(
                "Projection '%s' type '%s' is not compatible with reference type '%s'",
                name,
                value.type().name(),
                ref.type().name()
            ),
            location
        );
      } else {
        resolvedAt.put(name, location);
        ref.resolve(referencesNamespace.append(name), (R) value);
      }

    });

  }

  public void ensureAllReferencesResolved() {
    for (final Map.Entry<String, GenProjectionReference<?>> entry : references.entrySet()) {
      String name = entry.getKey();
      assert name != null;
      final GenProjectionReference<?> vp = entry.getValue();

      if (!vp.isResolved()) {
        // delegate it to parent if possible:
        //
        //    b = a
        // a = 1

        if (parent == null) {
          psiProcessingContext.addError(String.format("Projection '%s' is not defined", name), vp.location());
        } else {
          final GenProjectionReference<?> parentRef = parent.references.get(name);
          if (parentRef == null) {
            parent.references.put(name, vp);
          } else if (parentRef != vp) {
            //?? can't happen
            psiProcessingContext.addError(
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

  protected abstract @NotNull VP toSelfVar(@NotNull MP mRef);

}
