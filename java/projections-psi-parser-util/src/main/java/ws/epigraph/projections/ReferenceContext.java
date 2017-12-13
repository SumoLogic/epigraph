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
import ws.epigraph.util.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings({"unchecked", "MissortedModifiers"})
public abstract class ReferenceContext<
    P extends GenProjection<?, ?, EP, ? extends MP>,
    EP extends GenEntityProjection<EP, ?, ?>,
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

    final @Nullable Tuple2<RefItem<EP>, ProjectionReferenceName> tuple =
        lookupEntityReference(name, useParent, type, location);

    final RefItem<EP> ref = tuple == null ? null : tuple._1;

    if (ref == null) {
      ProjectionReferenceName referenceName = projectionReferenceName(name);
      ReferenceContext.RefItem<EP> newRef = new IdRefItem<>(newEntityReference(type, referenceName, location));
      addReference(name, (RefItem<P>) newRef);
      return newRef.apply();
    } else if (ref.isResolved()) {
      return ref.apply(); // todo may still cause an unresolved reference exception!
    } else {
      // postpone function call till resolved. TODO this approach doesn't work with projection
      // transformations, `ep` won't be updated if `ref.argument` is transformed/replaced
      String lazyName = name + "#";
      final EP ep;

      @Nullable Tuple2<RefItem<P>, ProjectionReferenceName> lazyTuple = lookupReference(lazyName, true);

      if (lazyTuple == null) {
        ProjectionReferenceName lazyReferenceName = projectionReferenceName(lazyName);
        EP lazyEp = newEntityReference(type, lazyReferenceName, location);
        ref.argument().runOnResolved(
            () -> {
              ProjectionReferenceName resultReferenceName =
                  Optional.ofNullable(ref.argument().referenceName()).orElse(tuple._2);

              lazyEp.resolve(resultReferenceName, ref.apply());
            }
        );

        ep = lazyEp;
      } else {
        // todo even with this hack this should be recursive
        ep = (EP) lazyTuple._1.apply();
      }

      return ep;
    }
  }

  @NotNull
  public <R extends GenProjectionReference<?>> MP modelReference(
      @NotNull DatumTypeApi type,
      @NotNull String name,
      boolean useParent,
      @NotNull TextLocation location) throws PsiProcessingException {

    final @Nullable Tuple2<RefItem<MP>, ProjectionReferenceName> tuple =
        lookupModelReference(name, useParent, type, location);

    final RefItem<MP> ref = tuple == null ? null : tuple._1;

    if (tuple == null) {
      ProjectionReferenceName referenceName = projectionReferenceName(name);
      ReferenceContext.RefItem<MP> newRef =
          (RefItem<MP>) new IdRefItem<>((R) newModelReference(type, referenceName, location));

      addReference(name, (RefItem<P>) newRef);
      return newRef.apply();
    } else if (ref.isResolved()) {
      return ref.apply(); // todo may still cause an unresolved reference exception!
    } else {
      // postpone function call till resolved. TODO this approach doesn't work with projection
      // transformations, `mp` won't be updated if `ref.argument` is transformed/replaced
      String lazyName = name + "#";
      final MP mp;

      @Nullable Tuple2<RefItem<P>, ProjectionReferenceName> lazyTuple = lookupReference(lazyName, true);

      if (lazyTuple == null) {
        ProjectionReferenceName lazyReferenceName = projectionReferenceName(lazyName);
        MP lazyMp = newModelReference(type, lazyReferenceName, location);
        addReference(lazyName, new IdRefItem<>((P) lazyMp));

        ref.argument().runOnResolved(
            () -> {
              ProjectionReferenceName resultReferenceName =
                  Optional.ofNullable(ref.argument().referenceName()).orElse(tuple._2);

              ((GenProjectionReference<MP>) lazyMp).resolve(resultReferenceName, ref.apply());
            }
        );

        mp = lazyMp;
      } else {
        // todo even with this hack this should be recursive
        mp = (MP) lazyTuple._1.apply();
      }

      return mp;
    }

  }

  public void addReference(@NotNull String name, @NotNull RefItem<P> refItem) {
//    if (references.containsKey(name))
//      throw new IllegalArgumentException("Can't replace reference '" + name + "'");

    RefItem<P> prev = references.put(name, refItem);
    if (prev != null) {
      if (prev instanceof IdRefItem) {
        IdRefItem<P> idRefItem = (IdRefItem<P>) prev;
        P oldRef = idRefItem.argument();

        if (oldRef.isResolved())
          throw new IllegalArgumentException("Can't replace reference '" + name + "'");

        // an (unresolved) reference to this name already exists, replace it but resolve it in the future
        // so that all the usages get updated

        refItem.argument().runOnResolvedWithRetry(() -> {
          P resolved = refItem.apply();
          ((GenProjectionReference<P>) oldRef).resolve(resolved.referenceName(), resolved);
        });
      } else {
        throw new IllegalArgumentException("Can't replace reference '" + name + "'");
      }
    }
  }

  public boolean isResolved(@NotNull String name) {
    @Nullable Tuple2<RefItem<P>, ProjectionReferenceName> refItem = lookupReference(name, true);
    return refItem != null && refItem._1.isResolved();
  }

  @Nullable
  public Tuple2<ReferenceContext.RefItem<P>, ProjectionReferenceName>
  lookupReference(@NotNull String name, boolean useParent) {
    RefItem<P> item = references.get(name);

    if (item == null && useParent && parent != null)
      return parent.lookupReference(name, true);

    return item == null ? null : Tuple2.of(item, projectionReferenceName(name));
  }

  @Nullable
  private Tuple2<ReferenceContext.RefItem<EP>, ProjectionReferenceName> lookupEntityReference(
      @NotNull String name,
      boolean useParent,
      @NotNull TypeApi targetType,
      TextLocation location) throws PsiProcessingException {

    @Nullable Tuple2<RefItem<P>, ProjectionReferenceName> tuple = lookupReference(name, useParent);
    if (tuple == null) {
      return null;
    } else {
      if (tuple._1.argument.isEntityProjection())
        return Tuple2.of((RefItem<EP>) tuple._1, tuple._2);
      else
        throw new PsiProcessingException(
            String.format(
                "Model reference '%s' of type '%s' can't be used for an entity type '%s'",
                name,
                tuple._1.argument.type().name(),
                targetType.name()
            ),
            location,
            context
        );
    }
  }

  @Nullable
  private Tuple2<ReferenceContext.RefItem<MP>, ProjectionReferenceName> lookupModelReference(
      @NotNull String name,
      boolean useParent,
      @NotNull TypeApi targetType,
      TextLocation location) throws PsiProcessingException {

    @Nullable Tuple2<RefItem<P>, ProjectionReferenceName> tuple = lookupReference(name, useParent);
    if (tuple == null) {
      return null;
    } else {
      if (tuple._1.argument.isModelProjection())
        return Tuple2.of((RefItem<MP>) tuple._1, tuple._2);
      else
        throw new PsiProcessingException(
            String.format(
                "Entity reference '%s' of type '%s' can't be used for a model type '%s'",
                name,
                tuple._1.argument.type().name(),
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

    value.runOnResolved/*WithRetry*/(() -> {
      ProjectionReferenceName referenceName = projectionReferenceName(name);
      ProjectionReferenceName valueReferenceName = value.referenceName();

      if (valueReferenceName == null)
        value.setReferenceName(referenceName);

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
          entry.getValue()
      );

//    if (parent != null)
//      parent.ensureAllReferencesResolved();

    return allResolved;
  }

  private boolean ensureReferenceResolved(
      @NotNull String name,
      @NotNull ReferenceContext.RefItem<P> ref) {

    boolean allResolved = true;

    if (!ref.isResolved()) {
      // check if backup resolver can handle it (resolve entity ref from model ref or v.v.)

      // delegate it to parent if possible:
      //
      //   b = a
      // a = 1

      if (parent == null) {
        allResolved = false;
        context.addError(
            String.format("Projection '%s' is not defined (context: '%s')", name, referencesNamespace),
            ref.location()
        );
      } else {
        RefItem<P> parentRef = parent.references.get(name);
        if (parentRef == null) {
          parent.references.put(name, ref);
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
  private MP newModelReference(
      @NotNull DatumTypeApi type,
      @NotNull ProjectionReferenceName name,
      @NotNull TextLocation location)
      throws PsiProcessingException {
    switch (type.kind()) {

      case RECORD:
        if (type instanceof RecordTypeApi)
          return newRecordModelReference((RecordTypeApi) type, name, location);
        else throw new PsiProcessingException(String.format(
            "Can't create record model projection for type '%s'",
            type.name()
        ), location, context);

      case MAP:
        if (type instanceof MapTypeApi)
          return newMapModelReference((MapTypeApi) type, name, location);
        else throw new PsiProcessingException(String.format(
            "Can't create map model projection for type '%s'",
            type.name()
        ), location, context);

      case LIST:
        if (type instanceof ListTypeApi)
          return newListModelReference((ListTypeApi) type, name, location);
        else throw new PsiProcessingException(String.format(
            "Can't create list model projection for type '%s'",
            type.name()
        ), location, context);

      case PRIMITIVE:
        if (type instanceof PrimitiveTypeApi)
          return newPrimitiveModelReference((PrimitiveTypeApi) type, name, location);
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
  protected abstract EP newEntityReference(
      @NotNull TypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location);

  protected abstract MP newRecordModelReference(
      @NotNull RecordTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location);

  protected abstract MP newMapModelReference(
      @NotNull MapTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location);

  protected abstract MP newListModelReference(
      @NotNull ListTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location);

  protected abstract MP newPrimitiveModelReference(
      @NotNull PrimitiveTypeApi type,
      @Nullable ProjectionReferenceName name,
      @NotNull TextLocation location);

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
