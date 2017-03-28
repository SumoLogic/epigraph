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
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.types.*;

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

  @NotNull
  public MP modelReference(
      @NotNull DatumTypeApi type,
      @NotNull String name,
      boolean useParent,
      @NotNull TextLocation location) throws PsiProcessingException {

    MP ref = lookupModelReference(name, useParent, location);

    if (ref == null) {
      ref = newModelReference(type, location);
      references.put(name, ref);
      return ref;
    } else return ref;

  }

  public boolean isResolved(@NotNull String name) {
    final GenProjectionReference<?> reference = lookupReference(name, true);
    return reference != null && reference.isResolved();
  }

  @Nullable
  private VP lookupVarReference(@NotNull String name, boolean useParent) {
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
  private MP lookupModelReference(@NotNull String name, boolean useParent, @NotNull TextLocation location)
      throws PsiProcessingException {
    final GenProjectionReference<?> reference = lookupReference(name, useParent);

    if (reference == null)
      return null;

    else if (reference instanceof GenVarProjection) {
      VP varRef = (VP) reference;
      final TypeApi varType = varRef.type();

      if (varType.kind() == TypeKind.UNION)
        throw new PsiProcessingException(
            String.format(
                "Expected reference '%s' to be a model reference, got var reference instead",
                name
            ),
            location, psiProcessingContext
        );
      else {
        final TagApi selfTag = ((DatumTypeApi) varType).self();
        if (!varRef.isResolved()) return null;
        final GenTagProjectionEntry<?, MP> tpe = varRef.tagProjections().get(selfTag.name());
        if (tpe == null) return null;
        else return tpe.projection();
      }

    } else if (reference instanceof GenModelProjection)
      return (MP) reference;

    throw new RuntimeException(String.format("Unreachable: '%s'", name));
  }

  @Nullable
  private GenProjectionReference<?> lookupReference(@NotNull String name, boolean useParent) {
    GenProjectionReference<?> ref = references.get(name);

    if (ref != null) return ref;
    if (!useParent || parent == null) return null;
    else return parent.lookupReference(name, true);
  }

  public <R extends GenProjectionReference<R>> void resolve(
      @NotNull final String name,
      @NotNull final R value,
      @NotNull final TextLocation location,
      @NotNull final PsiProcessingContext context) {

    // R should also be super of VP & MP but Java can't express this

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
      } else {

        if (ref.type().isAssignableFrom(value.type())) {
          final R normalized;

          // resolve model ref from var projection
          if (value instanceof GenVarProjection<?, ?, ?>) {
            VP varValue = (VP) value;
            VP _normalized = varValue.normalizedForType(ref.type());

            if (ref instanceof GenModelProjection<?, ?, ?, ?>) {
              if (_normalized.type().kind() == TypeKind.UNION)
                throw new RuntimeException(
                    String.format("Broken isAssignableFrom between %s and %s", ref.type().name(), value.type().name())
                );
              else {
                final GenTagProjectionEntry<?, MP> tpe = _normalized.singleTagProjection();
                assert tpe != null;
                normalized = (R) tpe.projection();
              }
            } else
              normalized = (R) _normalized;

          } else if (value instanceof GenModelProjection<?, ?, ?, ?>) {
            MP modelValue = (MP) value;
            normalized = (R) modelValue.normalizedForType((DatumTypeApi) ref.type());
          } else
            throw new RuntimeException(String.format("Unreachable: '%s'", name));

          resolvedAt.put(name, location);
          ref.resolve(referencesNamespace.append(name), normalized);

        } else {
          context.addError(
              String.format(
                  "Projection '%s' type '%s' is not compatible with reference type '%s'",
                  name,
                  value.type().name(),
                  ref.type().name()
              ),
              location
          );
        }

      }

    });

  }

  public void ensureAllReferencesResolved() {
    for (final Map.Entry<String, GenProjectionReference<?>> entry : references.entrySet()) {
      String name = entry.getKey();
      assert name != null;
      final GenProjectionReference<?> ref = entry.getValue();

      if (!ref.isResolved()) {
        // delegate it to parent if possible:
        //
        //    b = a
        // a = 1

        if (parent == null) {
          psiProcessingContext.addError(String.format("Projection '%s' is not defined", name), ref.location());
        } else {
          final GenProjectionReference<?> parentRef = parent.references.get(name);
          if (parentRef == null) {
            parent.references.put(name, ref);
          } else if (parentRef != ref) {
            //?? can't happen
            psiProcessingContext.addError(
                String.format(
                    "Internal error: different references to projection '%s' in this and parent context",
                    name
                ),
                ref.location()
            );
          }
        }
      }
    }
  }

  @NotNull
  protected MP newModelReference(@NotNull DatumTypeApi type, @NotNull TextLocation location)
      throws PsiProcessingException {
    switch (type.kind()) {

      case RECORD:
        if (type instanceof RecordTypeApi)
          return newRecordModelReference((RecordTypeApi) type, location);
        else throw new PsiProcessingException(String.format(
            "Can't create record model projection for type '%s'",
            type.name()
        ), location, psiProcessingContext);

      case MAP:
        if (type instanceof MapTypeApi)
          return newMapModelReference((MapTypeApi) type, location);
        else throw new PsiProcessingException(String.format(
            "Can't create map model projection for type '%s'",
            type.name()
        ), location, psiProcessingContext);

      case LIST:
        if (type instanceof ListTypeApi)
          return newListModelReference((ListTypeApi) type, location);
        else throw new PsiProcessingException(String.format(
            "Can't create list model projection for type '%s'",
            type.name()
        ), location, psiProcessingContext);

      case PRIMITIVE:
        if (type instanceof PrimitiveTypeApi)
          return newPrimitiveModelReference((PrimitiveTypeApi) type, location);
        else throw new PsiProcessingException(String.format(
            "Can't create primitive model projection for type '%s'",
            type.name()
        ), location, psiProcessingContext);

      default:
        throw new PsiProcessingException(String.format(
            "Can't create model projection for type '%s'",
            type.name()
        ), location, psiProcessingContext);
    }
  }

  @NotNull
  protected abstract VP newVarReference(@NotNull TypeApi type, @NotNull TextLocation location);

  protected abstract MP newRecordModelReference(@NotNull RecordTypeApi type, @NotNull TextLocation location);

  protected abstract MP newMapModelReference(@NotNull MapTypeApi type, @NotNull TextLocation location);

  protected abstract MP newListModelReference(@NotNull ListTypeApi type, @NotNull TextLocation location);

  protected abstract MP newPrimitiveModelReference(@NotNull PrimitiveTypeApi type, @NotNull TextLocation location);

  protected abstract @NotNull VP toSelfVar(@NotNull MP mRef);

}
