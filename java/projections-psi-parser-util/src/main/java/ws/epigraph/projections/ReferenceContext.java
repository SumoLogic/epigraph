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
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings({"unchecked", "MissortedModifiers"})
public abstract class ReferenceContext<
    P extends GenProjection<?, ?, EP, ? extends MP>,
    EP extends GenEntityProjection<?, ?, ?>,
    MP extends GenModelProjection<?, ?, ?, ?, ?>
    > {

  @NotNull
  private final ProjectionReferenceName referencesNamespace;
  @NotNull
  private final PsiProcessingContext context;

  @Nullable
  private final ReferenceContext<P, EP, MP> parent;
  private final Map<String, RefItem<P>> references = new HashMap<>();
  private final Map<String, TextLocation> resolvedAt = new HashMap<>();

  protected ReferenceContext(
      @NotNull final ProjectionReferenceName referencesNamespace,
      @Nullable final ReferenceContext<P, EP, MP> parent,
      @NotNull PsiProcessingContext context) {

//    System.out.println("++Created new context: "+referencesNamespace);
    this.referencesNamespace = referencesNamespace;
    this.parent = parent;
    this.context = context;
  }

  @Nullable
  public ReferenceContext<P, EP, MP> parentContext() { return parent; }

  @NotNull
  public ReferenceContext<P, EP, MP> parentOrThis() { return parent == null ? this : parent; }

  @NotNull
  public P reference(
      @NotNull TypeApi type,
      @NotNull String name,
      boolean useParent,
      @NotNull TextLocation location) throws PsiProcessingException {

    if (type.kind() == TypeKind.ENTITY)
      return (P) entityReference(type, name, useParent, location);
    else
      return (P) modelReference((DatumTypeApi) type, name, useParent, location);
  }

  @NotNull
  public EP entityReference(
      @NotNull TypeApi type,
      @NotNull String name,
      boolean useParent,
      @NotNull TextLocation location) throws PsiProcessingException {

    @Nullable ReferenceContext.RefItem<EP> ref = lookupEntityReference(name, useParent, type, location);

    if (ref == null) {
      ref = new IdRefItem<>(newEntityReference(type, location));
//      System.out.println("Allocated entity reference "+referencesNamespace.append(new ProjectionReferenceName.StringRefNameSegment(name)));
      addReference(name, (RefItem<P>) ref);
      return ref.apply();
    } else return ref.apply();
  }

  @NotNull
  public <R extends GenProjectionReference<?>> MP modelReference(
      @NotNull DatumTypeApi type,
      @NotNull String name,
      boolean useParent,
      @NotNull TextLocation location) throws PsiProcessingException {

    @Nullable ReferenceContext.RefItem<MP> ref = lookupModelReference(name, useParent, type, location);

    if (ref == null) {
      ref = (RefItem<MP>) new IdRefItem<>((R) newModelReference(type, location));
//      System.out.println("Allocated model reference "+referencesNamespace.append(new ProjectionReferenceName.StringRefNameSegment(name)));
      addReference(name, (RefItem<P>) ref);
      return ref.apply();
    } else return ref.apply();

  }

  public void addReference(@NotNull String name, @NotNull RefItem<P> refItem) { references.put(name, refItem); }

  public boolean isResolved(@NotNull String name) {
    RefItem<P> refItem = lookupReference(name, true);
    return refItem != null && refItem.isResolved();
  }

  @Nullable
  public ReferenceContext.RefItem<P> lookupReference(@NotNull String name, boolean useParent) {
    RefItem<P> item = references.get(name);
    if (item == null && useParent && parent != null)
      item = parent.lookupReference(name, true);

    return item;
  }

  @Nullable
  private ReferenceContext.RefItem<EP> lookupEntityReference(
      @NotNull String name,
      boolean useParent,
      @NotNull TypeApi targetType,
      TextLocation location) throws PsiProcessingException {

    RefItem<P> refItem = lookupReference(name, useParent);
    if (refItem == null) {
      return null;
    } else {
      if (refItem.argument.isEntityProjection())
        return (RefItem<EP>) refItem;
      else
        throw new PsiProcessingException(
            String.format(
                "Model reference '%s' of type '%s' can't be used for an entity type '%s'",
                name,
                refItem.argument.type().name(),
                targetType.name()
            ),
            location,
            context
        );
    }
  }

  @Nullable
  private ReferenceContext.RefItem<MP> lookupModelReference(
      @NotNull String name,
      boolean useParent,
      @NotNull TypeApi targetType,
      TextLocation location) throws PsiProcessingException {

    RefItem<P> refItem = lookupReference(name, useParent);
    if (refItem == null) {
      return null;
    } else {
      if (refItem.argument.isModelProjection())
        return (RefItem<MP>) refItem;
      else
        throw new PsiProcessingException(
            String.format(
                "Entity reference '%s' of type '%s' can't be used for a model type '%s'",
                name,
                refItem.argument.type().name(),
                targetType.name()
            ),
            location,
            context
        );
    }
  }

  protected boolean hasReference(@NotNull String name) {
    return references.containsKey(name) || (parent != null && parent.hasReference(name));
  }

  public void resolveRef(
      @NotNull String name,
      @NotNull P value,
      @NotNull TextLocation location) {

    value.runOnResolved(() -> {
      ProjectionReferenceName referenceName = projectionReferenceName(name);
      ProjectionReferenceName valueReferenceName = value.referenceName();

      if (valueReferenceName == null)
        value.setReferenceName(referencesNamespace);

      RefItem<P> item = references.get(name);
      P ref = item == null ? null : item.apply();

      if (ref == null) {
        if (parent != null && parent.hasReference(name)) {

          // a = c
          //    b = a
          //    a = 2         <-- prohibited
          // c = 1

          context.addError(String.format("Can't override projection '%s' from parent context", name), location);

        } else {
          // race?
          context.addError(String.format(
              "Projection '%s' reference not found (context: '%s')",
              name,
              referencesNamespace.toString()
          ), location);
        }
      } else {
        if (ref.isResolved()) {
          context.addError(
              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
          );
        } else {
          // not resolved yet, check type compatibility and resolve
          if (ref.type().isAssignableFrom(value.type())) {
            P _normalized = (P) value.normalizedForType(ref.type());
            ((GenProjectionReference<P>) item.argument()).resolve(referenceName, _normalized);
            resolvedAt.put(name, location);
          } else {
            addIncompatibleProjectionTypeError(name, value.type(), ref.type(), location);
          }
        }
      }
    });
  }

//  public void resolveEntityRef(@NotNull String name, @NotNull EP value, @NotNull TextLocation location) {
//    resolveEntityRef(name, value, true, location);
//  }
//
//  private <R extends GenProjectionReference<R>> void resolveEntityRef(
//      @NotNull String name,
//      @NotNull EP value,
//      boolean updateMRef,
//      @NotNull TextLocation location) {
//
//    value.runOnResolved(() -> {
//      ProjectionReferenceName referenceName = projectionReferenceName(name);
//
//      ProjectionReferenceName valueReferenceName = value.referenceName();
//      if (valueReferenceName == null)
//        value.setReferenceName(referenceName);
//      // this is valid, e.g.
//      // outputProjection $foo = $bar
//      // 'name' is 'foo', 'referenceName' is 'some.foo', 'valueReferenceName' is 'some.bar'
////      else if (!referenceName.equals(valueReferenceName))
////        context.addError(
////            String.format(
////                "Internal error for entity projection '%s': reference inconsistency: '%s' != '%s'",
////                name, referenceName, valueReferenceName
////            ),
////            location
////        );
//
//      RefItem<EP> eitem = entityReferences.get(name);
//      RefItem<MP> mitem = modelReferences.get(name);
//
//      EP eref = eitem == null ? null : eitem.apply();
//      MP mref = mitem == null ? null : mitem.apply();
//
//      if (eref != null) {
//        if (eref.isResolved()) {
//          context.addError(
//              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
//          );
//        } else {
//          if (eref.type().isAssignableFrom(value.type())) {
//            EP _normalized = value.normalizedForType(eref.type());
//            eitem.argument().resolve(referenceName, _normalized);
//            resolvedAt.put(name, location);
//
//          } else {
//            addIncompatibleProjectionTypeError(name, value.type(), eref.type(), location);
//          }
//        }
//      }
//
//      if (updateMRef && mref != null) {
//        if (mref.isResolved()) {
//          context.addError(
//              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
//          );
//        } else {
//
//          if (mref.type().isAssignableFrom(value.type())) { // type correct?
//            EP _normalized = value.normalizedForType(mref.type());
//            if (_normalized.type().kind() == TypeKind.ENTITY) // type still correct after normalization?
//              throw new RuntimeException(
//                  String.format("Broken isAssignableFrom between %s and %s", mref.type().name(), value.type().name())
//              );
//            else {
//              ((GenProjectionReference<R>) mitem.argument()).resolve(referenceName, (R) (fromSelfVar(_normalized)));
//              resolvedAt.put(name, location);
//            }
//          } else
//            addIncompatibleProjectionTypeError(name, value.type(), mref.type(), location);
//
//        }
//      }
//
//      if (eref == null && mref == null) {
//        if (parent != null && parent.hasReference(name)) {
//
//          // a = c
//          //    b = a
//          //    a = 2         <-- prohibited
//          // c = 1
//
//          context.addError(String.format("Can't override projection '%s' from parent context", name), location);
//
//        } else {
//          // race?
//          context.addError(String.format(
//              "Projection '%s' entity reference not found (context: '%s')",
//              name,
//              referencesNamespace.toString()
//          ), location);
////          entityReferences.put(name, new IdRefItem<>(value));
//        }
//      }
//    });
//  }
//
//  public void resolveModelRef(@NotNull String name, @NotNull MP value, @NotNull TextLocation location) {
//    resolveModelRef(name, value, true, location);
//  }
//
//  private <R extends GenProjectionReference<R>> void resolveModelRef(
//      @NotNull String name,
//      @NotNull MP value,
//      boolean updateERef,
//      @NotNull TextLocation location) {
//
//    value.runOnResolved(() -> {
//      ProjectionReferenceName referenceName = projectionReferenceName(name);
//
//      ProjectionReferenceName valueReferenceName = value.referenceName();
//      if (valueReferenceName == null)
//        value.setReferenceName(referenceName);
//      // this is valid, e.g.
//      // outputProjection $foo = $bar
//      // 'name' is 'foo', 'referenceName' is 'some.foo', 'valueReferenceName' is 'some.bar'
////      else if (!referenceName.equals(valueReferenceName))
////        context.addError(
////            String.format(
////                "Internal error for model projection '%s': reference inconsistency: '%s' != '%s'",
////                name, referenceName, valueReferenceName
////            ),
////            location
////        );
//
//      RefItem<EP> eitem = entityReferences.get(name);
//      RefItem<MP> mitem = modelReferences.get(name);
//
//      EP eref = eitem == null ? null : eitem.apply();
//      MP mref = mitem == null ? null : mitem.apply();
//
//      if (mref != null) {
//        if (mref.isResolved()) {
//          context.addError(
//              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
//          );
//        } else {
//          if (mref.type().isAssignableFrom(value.type())) {
//            MP _normalized = (MP) value.normalizedForType(mref.type());
//            ((GenProjectionReference<R>) mitem.argument()).resolve(referenceName, (R) _normalized);
//            resolvedAt.put(name, location);
//          } else {
//            addIncompatibleProjectionTypeError(name, value.type(), mref.type(), location);
//          }
//        }
//      }
//
//      if (updateERef && eref != null) {
//        if (eref.isResolved()) {
//          context.addError(
//              String.format("Projection '%s' was already resolved at %s", name, resolvedAt.get(name)), location
//          );
//        } else {
//
//          if (eref.type().isAssignableFrom(value.type())) {
//            EP evalue = toSelfVar(value);
//            eitem.argument().resolve(referenceName, evalue);
//            resolvedAt.put(name, location);
//          } else
//            addIncompatibleProjectionTypeError(name, value.type(), eref.type(), location);
//
//        }
//      }
//
//      if (eref == null && mref == null) {
//        if (parent != null && parent.hasReference(name)) {
//
//          // a = c
//          //    b = a
//          //    a = 2         <-- prohibited
//          // c = 1
//
//          context.addError(String.format("Can't override projection '%s' from parent context", name), location);
//
//        } else {
//          // race?
//          context.addError(String.format(
//              "Projection '%s' model reference not found (context: '%s')",
//              name,
//              referencesNamespace.toString()
//          ), location);
////          modelReferences.put(name, new IdRefItem<>(value));
//        }
//      }
//    });
//
//  }

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

  public boolean ensureAllReferencesResolved() {
    boolean allResolved = true;

    for (Map.Entry<String, RefItem<P>> entry : references.entrySet())
      allResolved &= ensureReferenceResolved(
          entry.getKey(),
          entry.getValue(),
          parent == null ? null : parent.references
      );

//    if (parent != null)
//      parent.ensureAllReferencesResolved();

    return allResolved;
  }

  private boolean ensureReferenceResolved(
      @NotNull String name,
      @NotNull ReferenceContext.RefItem<P> ref,
      @Nullable Map<String, RefItem<P>> parentReferences) {

    boolean allResolved = true;

    if (!ref.isResolved()) {
      // check if backup resolver can handle it (resolve entity ref from model ref or v.v.)

      // delegate it to parent if possible:
      //
      //   b = a
      // a = 1

      if (parentReferences == null) {
        allResolved = false;
        context.addError(
            String.format("Projection '%s' is not defined (context: '%s')", name, referencesNamespace),
            ref.location()
        );
      } else {
        assert parent != null;
        RefItem<P> parentRef = parentReferences.get(name);
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

    return allResolved;
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
  protected abstract EP newEntityReference(@NotNull TypeApi type, @NotNull TextLocation location);

  protected abstract MP newRecordModelReference(@NotNull RecordTypeApi type, @NotNull TextLocation location);

  protected abstract MP newMapModelReference(@NotNull MapTypeApi type, @NotNull TextLocation location);

  protected abstract MP newListModelReference(@NotNull ListTypeApi type, @NotNull TextLocation location);

  protected abstract MP newPrimitiveModelReference(@NotNull PrimitiveTypeApi type, @NotNull TextLocation location);

  /**
   * Updates references using a transformation map obtained
   * during projection {@link GenProjectionTransformer transformation}.
   *
   * @param transformationMap references transformation map
   */
  public final void transform(@NotNull Map<P, P> transformationMap) {
//    if (!ensureAllReferencesResolved())
//      throw new IllegalArgumentException(
//          "Can't apply transformation map to a reference context when not all references are resolved");

    for (final Map.Entry<String, RefItem<P>> entry : references.entrySet()) {
      P old = entry.getValue().argument();
      P _new = transformationMap.get(old);

      if (_new != null)
        entry.getValue().setArgument(_new);
    }

    if (parent != null)
      parent.transform(transformationMap);
  }

  @Override
  public String toString() { return "'" + referencesNamespace + "' reference context"; }

  /**
   * Reference item is a pair of a reference of type {@code R}
   * called "argument" and a function from {@code R} to {@code R}.
   * <p>
   * "argument" can be unresolved and can also be refined (changed) over time,
   * for instance if projection is transformed and references
   * are updated using {@link ReferenceContext#transform(Map)}
   * transformation map.
   * Function must be stateless.
   * <p>
   * In simplest case "argument" is target reference and function is identity,
   * this case is described by {@link IdRefItem}.
   * <p>
   * A more complex case is a named normalized tail reference, for example
   * <code>
   * <pre>
   *     outputProjection $foo = ( a, b ) ~Bar $bar = ( c )
   *   </pre>
   * </code>
   * This results in "bar" reference created in the same context as "foo", with
   * {@code RefItem} having "foo" as the argument and a call
   * to {@link GenModelProjection#normalizedForType(TypeApi) normalizedForType} as a function
   *
   * @param <R>
   */
  public static class RefItem<R extends GenProjectionReference</*R*/?>> {
    private @NotNull R argument;
    private final @NotNull Function<R, R> func;
    private final @NotNull TextLocation location;

    public RefItem(
        @NotNull final R argument,
        @NotNull final Function<R, R> func,
        @NotNull final TextLocation location) {
      this.argument = argument;
      this.func = func;
      this.location = location;
    }

    public @NotNull R argument() { return argument; }

    public void setArgument(final @NotNull R argument) { this.argument = argument; }

    public @NotNull R apply() { return func.apply(argument()); }

    boolean isResolved() { return argument().isResolved(); }

    public @NotNull TextLocation location() { return location; }
  }

  public static final class IdRefItem<R extends GenProjectionReference<?>> extends RefItem<R> {
    public IdRefItem(@NotNull final R argument) {
      super(argument, Function.identity(), argument.location());
    }
  }
}
