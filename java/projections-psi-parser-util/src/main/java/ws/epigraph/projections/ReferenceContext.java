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
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.types.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings({"unchecked", "MissortedModifiers"})
public abstract class ReferenceContext<
    EP extends GenVarProjection<EP, ?, MP>,
    MP extends GenModelProjection<?, ?, ?, ?>
    > {

  @NotNull
  private final ProjectionReferenceName referencesNamespace;
  @NotNull
  private final PsiProcessingContext context;

  @Nullable
  private final ReferenceContext<EP, MP> parent;
  private final Map<String, EP> entityReferences = new HashMap<>();
  private final Map<String, MP> modelReferences = new HashMap<>();
  private final Map<String, TextLocation> resolvedAt = new HashMap<>();

  protected ReferenceContext(
      @NotNull final ProjectionReferenceName referencesNamespace,
      @Nullable final ReferenceContext<EP, MP> parent,
      @NotNull PsiProcessingContext context) {

//    System.out.println("++Created new context: "+referencesNamespace);
    this.referencesNamespace = referencesNamespace;
    this.parent = parent;
    this.context = context;
  }

  @Nullable
  public ReferenceContext<EP, MP> parentContext() { return parent; }

  @NotNull
  public ReferenceContext<EP, MP> parentOrThis() { return parent == null ? this : parent; }

  @NotNull
  public EP varReference(
      @NotNull TypeApi type,
      @NotNull String name,
      boolean useParent,
      @NotNull TextLocation location) {

    EP ref = lookupEntityReference(name, useParent);

    if (ref == null) {
      ref = newVarReference(type, location);
//      System.out.println("Allocated entity reference "+referencesNamespace.append(new ProjectionReferenceName.StringRefNameSegment(name)));
      entityReferences.put(name, ref);
      return ref;
    } else return ref;

  }

  @NotNull
  public MP modelReference(
      @NotNull DatumTypeApi type,
      @NotNull String name,
      boolean useParent,
      @NotNull TextLocation location) throws PsiProcessingException {

    MP ref = lookupModelReference(name, useParent);

    if (ref == null) {
      ref = newModelReference(type, location);
//      System.out.println("Allocated model reference "+referencesNamespace.append(new ProjectionReferenceName.StringRefNameSegment(name)));
      modelReferences.put(name, ref);
      return ref;
    } else return ref;

  }

  public boolean isResolved(@NotNull String name) {
    final GenProjectionReference<?> entityReference = lookupEntityReference(name, true);
    final GenProjectionReference<?> modelReference = lookupModelReference(name, true);
    return (entityReference != null && entityReference.isResolved()) ||
           (modelReference != null && modelReference.isResolved());
  }

  @Nullable
  public EP lookupEntityReference(@NotNull String name, boolean useParent) {
    EP eref = entityReferences.get(name);

    if (eref == null) {
      MP mref = modelReferences.get(name);
      if (mref == null) {
        if (useParent && parent != null) { eref = parent.lookupEntityReference(name, true); }
      } else if (mref.isResolved()) {
        eref = toSelfVar(mref);
      }

    }

    return eref;
  }

  @Nullable
  public MP lookupModelReference(@NotNull String name, boolean useParent) {

    MP mref = modelReferences.get(name);

    if (mref == null) {
      EP eref = entityReferences.get(name);
      if (eref == null) {
        if (useParent && parent != null) { mref = parent.lookupModelReference(name, true); }
      } else if (eref.isResolved()) {
        mref = fromSelfVar(eref);
      }
    }

    return mref;
  }

  protected boolean hasReference(@NotNull String name) {
    return modelReferences.containsKey(name) || entityReferences.containsKey(name) ||
           (parent != null && parent.hasReference(name));
  }

  public <R extends GenProjectionReference<R>> void resolveEntityRef(
      @NotNull String name,
      @NotNull EP value,
      @NotNull TextLocation location) {

    value.runOnResolved(() -> {
      ProjectionReferenceName referenceName = projectionReferenceName(name);

      EP eref = entityReferences.get(name);
      MP mref = modelReferences.get(name);

      if (eref != null) {
        if (eref.isResolved()) {
          context.addError(
              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
          );
        } else {
          if (eref.type().isAssignableFrom(value.type())) {
            EP _normalized = value.normalizedForType(eref.type());
            eref.resolve(referenceName, _normalized);
            resolvedAt.put(name, location);

          } else {
            addIncompatibleProjectionTypeError(name, value.type(), eref.type(), location);
          }
        }
      }

      if (mref != null) {
        if (mref.isResolved()) {
          context.addError(
              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
          );
        } else {

          if (mref.type().isAssignableFrom(value.type())) { // type correct?
            EP _normalized = value.normalizedForType(mref.type());
            if (_normalized.type().kind() == TypeKind.ENTITY) // type still correct after normalization?
              throw new RuntimeException(
                  String.format("Broken isAssignableFrom between %s and %s", mref.type().name(), value.type().name())
              );
            else {
              ((GenProjectionReference<R>) mref).resolve(referenceName, (R) (fromSelfVar(_normalized)));
              resolvedAt.put(name, location);
            }
          } else
            addIncompatibleProjectionTypeError(name, value.type(), mref.type(), location);

        }
      }

      if (eref == null && mref == null) {
        if (parent != null && parent.hasReference(name)) {

          // a = c
          //    b = a
          //    a = 2         <-- prohibited
          // c = 1

          context.addError(String.format("Can't override projection '%s' from parent context", name), location);

        } else {
          context.addError(String.format("Projection '%s' reference not found", name), location);
        }
      }
    });
  }

  public <R extends GenProjectionReference<R>> void resolveModelRef(
      @NotNull String name,
      @NotNull MP value,
      @NotNull TextLocation location) {

    value.runOnResolved(() -> {
      ProjectionReferenceName referenceName =
          projectionReferenceName(name);

      EP eref = entityReferences.get(name);
      MP mref = modelReferences.get(name);

      if (mref != null) {
        if (mref.isResolved()) {
          context.addError(
              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
          );
        } else {
          if (mref.type().isAssignableFrom(value.type())) {
            MP _normalized = (MP) value.normalizedForType(mref.type());
            ((GenProjectionReference<R>) mref).resolve(referenceName, (R) _normalized);
            resolvedAt.put(name, location);
          } else {
            addIncompatibleProjectionTypeError(name, value.type(), mref.type(), location);
          }
        }
      }

      if (eref != null) {
        if (eref.isResolved()) {
          context.addError(
              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
          );
        } else {

          if (eref.type().isAssignableFrom(value.type())) {
            EP evalue = toSelfVar(value);
            eref.resolve(referenceName, evalue);
            resolvedAt.put(name, location);
          } else
            addIncompatibleProjectionTypeError(name, value.type(), eref.type(), location);

        }
      }

      if (eref == null && mref == null) {
        if (parent != null && parent.hasReference(name)) {

          // a = c
          //    b = a
          //    a = 2         <-- prohibited
          // c = 1

          context.addError(String.format("Can't override projection '%s' from parent context", name), location);

        } else {
          context.addError(String.format("Projection '%s' reference not found", name), location);
        }
      }
    });

  }

  public @NotNull ProjectionReferenceName projectionReferenceName(final @NotNull String name) {
    return referencesNamespace.append(new ProjectionReferenceName.StringRefNameSegment(name));
  }

  private void addIncompatibleProjectionTypeError(
      @NotNull String projectionName,
      @NotNull TypeApi projectionType,
      @NotNull TypeApi refType,
      @NotNull TextLocation location) {

    context.addError(
        String.format(
            "Projection '%s' type '%s' is not compatible with reference type '%s'",
            projectionName,
            projectionType.name(),
            refType.name()
        ),
        location
    );
  }

  public <R extends GenProjectionReference<R>> void ensureAllReferencesResolved() {
    for (Map.Entry<String, EP> entry : entityReferences.entrySet())
      ensureReferenceResolved(
          entry.getKey(),
          entry.getValue(),
          () -> {
            MP mref = modelReferences.get(entry.getKey());
            if (mref != null && mref.isResolved()) {
              resolveEntityRef(entry.getKey(), toSelfVar(mref), mref.location());
              return true;
            } else
              return false;
          },
          parent == null ? null : parent.entityReferences
      );

    for (final Map.Entry<String, MP> entry : modelReferences.entrySet()) {
      ensureReferenceResolved(
          entry.getKey(),
          (R) entry.getValue(),
          () -> {
            EP eref = entityReferences.get(entry.getKey());
            if (eref != null && eref.isResolved()) {
              resolveModelRef(entry.getKey(), fromSelfVar(eref), eref.location());
              return true;
            } else
              return false;
          },
          parent == null ? null : (Map<String, R>) parent.modelReferences
      );
    }

//    if (parent != null)
//      parent.ensureAllReferencesResolved();
  }

  private <R extends GenProjectionReference<R>> void ensureReferenceResolved(
      @NotNull String name,
      @NotNull R ref,
      @NotNull Supplier<Boolean> backupResolver,
      @Nullable Map<String, R> parentReferences) {

    if (!ref.isResolved()) {
      // check if backup resolver can handle it (resolve entity ref from model ref or v.v.)

      if (!backupResolver.get()) {

        // delegate it to parent if possible:
        //
        //    b = a
        // a = 1

        if (parentReferences == null) {
          context.addError(
              String.format("Projection '%s' is not defined (context: %s)", name, referencesNamespace),
              ref.location()
          );
        } else {
          assert parent != null;
          R parentRef = parentReferences.get(name);
          if (parentRef == null) {
            parentReferences.put(name, ref);
          } else if (parentRef != ref) {
            context.addError(
                String.format(
                    "Internal error: different references to projection '%s' in this ('%s') and parent ('%s') context",
                    name,
                    referencesNamespace,
                    parent.referencesNamespace
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
        ), location, context);

      case MAP:
        if (type instanceof MapTypeApi)
          return newMapModelReference((MapTypeApi) type, location);
        else throw new PsiProcessingException(String.format(
            "Can't create map model projection for type '%s'",
            type.name()
        ), location, context);

      case LIST:
        if (type instanceof ListTypeApi)
          return newListModelReference((ListTypeApi) type, location);
        else throw new PsiProcessingException(String.format(
            "Can't create list model projection for type '%s'",
            type.name()
        ), location, context);

      case PRIMITIVE:
        if (type instanceof PrimitiveTypeApi)
          return newPrimitiveModelReference((PrimitiveTypeApi) type, location);
        else throw new PsiProcessingException(String.format(
            "Can't create primitive model projection for type '%s'",
            type.name()
        ), location, context);

      default:
        throw new PsiProcessingException(String.format(
            "Can't create model projection for type '%s'",
            type.name()
        ), location, context);
    }
  }

  @NotNull
  protected abstract EP newVarReference(@NotNull TypeApi type, @NotNull TextLocation location);

  protected abstract MP newRecordModelReference(@NotNull RecordTypeApi type, @NotNull TextLocation location);

  protected abstract MP newMapModelReference(@NotNull MapTypeApi type, @NotNull TextLocation location);

  protected abstract MP newListModelReference(@NotNull ListTypeApi type, @NotNull TextLocation location);

  protected abstract MP newPrimitiveModelReference(@NotNull PrimitiveTypeApi type, @NotNull TextLocation location);

  protected abstract @NotNull EP toSelfVar(@NotNull MP mRef);

  protected @NotNull MP fromSelfVar(@NotNull EP eRef) {
    assert eRef.isResolved();
    assert eRef.type().kind() != TypeKind.ENTITY;
    GenTagProjectionEntry<?, MP> tpe = eRef.singleTagProjection();
    assert tpe != null;
    return tpe.projection();
  }

  @Override
  public String toString() { return "'" + referencesNamespace + "' reference context"; }
}
