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

package ws.epigraph.projections.gen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.types.*;
import ws.epigraph.util.Tuple2;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenProjectionTransformer<
    P extends GenProjection<? extends P, TP, EP, ? extends MP>,
    EP extends GenEntityProjection<EP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<EP, TP, /*MP*/?, /*RMP*/?, /*RMP*/? /*M*/>,
    RMP extends GenRecordModelProjection<P, TP, EP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<P, TP, EP, MP, MMP, ?>,
    LMP extends GenListModelProjection<P, TP, EP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<EP, TP, MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<P, TP, MP, FP>,
    FP extends GenFieldProjection<P, TP, MP, FP>
    > {

  private final Map<P, P> transformedCache = new IdentityHashMap<>();
  private final Map<P, P> visited = new IdentityHashMap<>();
  private final Set<P> usedRecursively = Collections.newSetFromMap(new IdentityHashMap<>());

  private Map<P, P> transformationMap = null;

  public @NotNull Tuple2<P, Map<P, P>> transform(@NotNull P projection, @Nullable DataTypeApi dataType) {
    this.transformationMap = new IdentityHashMap<>();
    return Tuple2.of(transformProjection(projection, dataType), transformationMap());
  }

  @SuppressWarnings("unchecked")
  public @NotNull Tuple2<MP, Map<P, P>> transform(@NotNull MP projection) {
    this.transformationMap = new IdentityHashMap<>();
    return Tuple2.of((MP) transformProjection((P) projection), transformationMap());
  }

  public @NotNull Tuple2<FP, Map<P, P>> transform(@NotNull FP fp, @NotNull DataTypeApi dataType) {
    this.transformationMap = new IdentityHashMap<>();
    return Tuple2.of(transformFieldProjection(fp, dataType), transformationMap());
  }

  @SuppressWarnings("unchecked")
  protected @NotNull Map<P, P> transformationMap() { return transformationMap; }

  // -------------------------------------------------------------------------------------------------------------------

  public void reset() {
    transformedCache.clear();
    visited.clear();
    usedRecursively.clear();
  }

  protected @NotNull P transformProjection(@NotNull P projection) {
    return transformProjection(projection, projection.type().dataType());
  }

  @SuppressWarnings("unchecked")
  protected @NotNull P transformProjection(@NotNull P projection, @Nullable DataTypeApi dataType) {
    P cached = cachedTransformedProjection(projection);
    if (cached != null)
      return cached;

    // postpone transformation if projection is not resolved yet

    if (projection.isResolved()) {
      return transformResolvedProjection(projection, dataType);
    } else {
      final P ref = newRef(projection.type(), projection.location());

//      final String me = getClass().getSimpleName();
//      final ProjectionReferenceName rn = projection.referenceName();
//      final String rns = rn == null ? "<no name>" : rn.last().toString();
//      System.out.println(String.format(
//          "%s %s %d postponed, ref: %d",
//          me,
//          rns,
//          System.identityHashCode(projection),
//          System.identityHashCode(ref)
//      ));

      transformedCache.put(projection, ref);
      projection.runOnResolved(() -> {
//        final ProjectionReferenceName rn2 = projection.referenceName();
//        final String rns2 = rn2 == null ? "<no name>" : rn2.last().toString();
//        System.out.println(String.format(
//            "%s %s %d resolved, resuming ref %d",
//            me,
//            rns2,
//            System.identityHashCode(projection),
//            System.identityHashCode(ref)
//        ));
        P transformed = transformResolvedProjection(projection, dataType);
        transformed.runOnResolved(() -> ((GenProjectionReference<P>) ref).resolve(
            projection.referenceName(),
            transformed
        ));
      });
      return ref;
    }
  }

  public /*protected*/ @Nullable P cachedTransformedProjection(@NotNull P p) {
    // have to make it public for composite transformer

    P cached = transformedCache.get(p);
    if (cached != null)
      return cached;
    if (transformedCache.values().contains(p))
      return p; // avoid transforming what is already transformed

    return null;
  }

  @SuppressWarnings("unchecked")
  protected @NotNull P transformResolvedProjection(@NotNull P projection, @Nullable DataTypeApi dataType) {
    P res = visited.get(projection);
    if (res != null) {
      usedRecursively.add(res); // do the same for models? or vars is enough?
      return res;
    }

    res = newRef(projection.type(), projection.location());

    visited.put(projection, res);

    boolean tailsChanged = false;
    List<P> transformedTails = null;

    List<? extends P> tails = projection.polymorphicTails();
    if (tails != null) {
      transformedTails = new ArrayList<>(tails.size());
      for (final P tail : tails) {
        P transformedTail = transformProjection(tail, dataType);

        if (transformedTail != tail)
          tailsChanged = true;
        transformedTails.add(transformedTail);
      }
    }

    P transformed;

    // todo we could simply always tranform tags here, for model projections this will lead
    // to model transformation via self tag. Have to figure out callbacks for this to work with existing code

    if (projection.isEntityProjection()) {
      transformed = (P) transformResolvedEntityProjection(
          projection.asEntityProjection(),
          dataType,
          (List<EP>) transformedTails,
          tailsChanged
      );
    } else {
      transformed = (P) transformResolvedModelProjection(
          projection.asModelProjection(),
          (List<MP>) transformedTails,
          tailsChanged
      );
    }

    visited.remove(projection);
    boolean usedRec = usedRecursively.contains(res);
    usedRecursively.remove(res);

    if (usedRec) {
      ((GenProjectionReference<P>) res).resolve(projection.referenceName(), transformed);
      transformationMap.put(projection, res);
      transformedCache.put(projection, res);
      return res;
    } else {
      transformationMap.put(projection, transformed);
      transformedCache.put(projection, transformed);
      return transformed;
    }
  }

  @SuppressWarnings("unchecked")
  protected @NotNull EP transformResolvedEntityProjection(
      @NotNull EP projection,
      @Nullable DataTypeApi dataType,
      @Nullable List<EP> transformedTails,
      boolean tailsChanged) {

    boolean tagsChanged = false;
    Map<String, TP> transformedTags = new LinkedHashMap<>(projection.tagProjections().size());

    for (final Map.Entry<String, TP> entry : projection.tagProjections().entrySet()) {
      TP tp = entry.getValue();
      MP mp = tp.modelProjection();
      MP transformedMp = transformProjection((P) mp).asModelProjection();
      TagApi tag = projection.type().tagsMap().get(entry.getKey());
      TP transformedTp = transformTagProjection(projection, tag, tp, transformedMp, mp != transformedMp);

      if (transformedTp != tp)
        tagsChanged = true;

      transformedTags.put(entry.getKey(), transformedTp);
    }

    return transformEntityProjection(
        projection,
        dataType,
        transformedTags,
        transformedTails,
        tagsChanged || tailsChanged
    );
  }

  @SuppressWarnings("unchecked")
  protected @NotNull MP transformResolvedModelProjection(
      @NotNull MP projection,
      @Nullable List<MP> transformedTails,
      boolean tailsChanged) {

    MP meta = (MP) projection.metaProjection();
    MP transformedMeta = null;

    if (meta != null)
      transformedMeta = (MP) transformProjection((P) meta);

    boolean mustRebuild = tailsChanged || meta != transformedMeta;
    final MP transformed;

    switch (projection.type().kind()) {

      case RECORD:
        transformed = (MP) transformRecordProjection(
            (RMP) projection,
            (List<RMP>) transformedTails,
            transformedMeta,
            mustRebuild
        );
        break;
      case MAP:
        transformed =
            (MP) transformMapProjection((MMP) projection, (List<MMP>) transformedTails, transformedMeta, mustRebuild);
        break;
      case LIST:
        transformed =
            (MP) transformListProjection((LMP) projection, (List<LMP>) transformedTails, transformedMeta, mustRebuild);
        break;
      case PRIMITIVE:
        transformed = (MP) transformPrimitiveProjection(
            (PMP) projection,
            (List<PMP>) transformedTails,
            transformedMeta,
            mustRebuild
        );
        break;
      case ENUM:
        throw new UnsupportedOperationException();
      default:
        throw new IllegalStateException();

    }

    if (projection != transformed)
      transformationMap.put((P) projection, (P) transformed);

    return transformed;
  }

  protected @NotNull RMP transformRecordProjection(
      @NotNull RMP projection,
      @Nullable List<RMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild) {

    boolean fieldsChanged = false;
    //noinspection unchecked
    Map<String, FPE> transformedFields = new LinkedHashMap<>(projection.fieldProjections().size());

    //noinspection unchecked
    for (final Map.Entry<String, FPE> entry : projection.fieldProjections().entrySet()) {
      FPE fpe = entry.getValue();

      FP fp = fpe.fieldProjection();
      FP transformedFp = transformFieldProjection(fp, fpe.field().dataType());
      FPE transformed = transformFieldProjectionEntry(projection, fpe, transformedFp, fp != transformedFp);

      if (transformed != fpe)
        fieldsChanged = true;
      transformedFields.put(entry.getKey(), transformed);
    }

    return transformRecordProjection(
        projection,
        transformedFields,
        transformedTails,
        transformedMeta,
        mustRebuild || fieldsChanged
    );
  }


  private @NotNull FP transformFieldProjection(@NotNull FP fp, @NotNull DataTypeApi dataType) {
    P p = fp.projection();
    P transformedP = transformProjection(p, dataType);
    return transformFieldProjection(fp, transformedP, p != transformedP);
  }

  protected @NotNull MMP transformMapProjection(
      @NotNull MMP projection,
      @Nullable List<MMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild) {

    //noinspection unchecked
    P transformedItemsProjection =
        transformProjection(projection.itemsProjection(), projection.type().valueType());
    //noinspection unchecked
    return transformMapProjection(
        projection,
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild || projection.itemsProjection() != transformedItemsProjection
    );
  }

  protected @NotNull LMP transformListProjection(
      @NotNull LMP projection,
      @Nullable List<LMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild) {

    //noinspection unchecked
    P transformedItemsProjection =
        transformProjection(projection.itemsProjection(), projection.type().elementType());
    //noinspection unchecked
    return transformListProjection(
        projection,
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild || projection.itemsProjection() != transformedItemsProjection
    );
  }

  // ----------------------------- abstract part

  /**
   * Transforms var projection
   *
   * @param entityProjection          original instance
   * @param dataType                  data type of the container that holds this projection (field, map or list entry), if known
   * @param transformedTagProjections transformed tag projections
   * @param transformedTails          transformed tail projections
   * @param mustRebuild               {@code true} iff any of the parts (tags or tails) has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract EP transformEntityProjection(
      @NotNull EP entityProjection,
      @Nullable DataTypeApi dataType,
      @NotNull Map<String, TP> transformedTagProjections,
      @Nullable List<EP> transformedTails,
      boolean mustRebuild);

  /**
   * Transforms tag projection
   *
   * @param entityProjection           original var projection
   * @param tag                        tag
   * @param tagProjection              original instance
   * @param transformedModelProjection transformed model projection
   * @param mustRebuild                {@code true} iff model projection has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull TP transformTagProjection(
      @NotNull EP entityProjection,
      @NotNull TagApi tag,
      @NotNull TP tagProjection,
      @NotNull MP transformedModelProjection,
      boolean mustRebuild);

  /**
   * Transforms record model projection
   *
   * @param recordModelProjection transformed instance
   * @param transformedFields     transformed field projections
   * @param transformedTails      transformed tail projections
   * @param transformedMeta       transformed meta projection
   * @param mustRebuild           {@code true} iff any of the parts (tags or tails) has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull RMP transformRecordProjection(
      @NotNull RMP recordModelProjection,
      @NotNull Map<String, FPE> transformedFields,
      @Nullable List<RMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild);

  /**
   * Transforms field projection entry
   *
   * @param modelProjection            record model projection
   * @param fieldProjectionEntry       original instance
   * @param transformedFieldProjection transformed field projection
   * @param mustRebuild                {@code true} iff field projection has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull FPE transformFieldProjectionEntry(
      @NotNull RMP modelProjection,
      @NotNull FPE fieldProjectionEntry,
      @NotNull FP transformedFieldProjection,
      boolean mustRebuild);

  /**
   * Transforms field projection
   *
   * @param fieldProjection             field projection
   * @param transformedEntityProjection transformed entity projection
   * @param mustRebuild                 {@code true} iff entity projection has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull FP transformFieldProjection(
      @NotNull FP fieldProjection,
      @NotNull P transformedEntityProjection,
      boolean mustRebuild);

  /**
   * Transforms map model projection
   *
   * @param mapModelProjection         original instance
   * @param transformedItemsProjection transformed items projection
   * @param transformedTails           transformed tail projections
   * @param transformedMeta            transformed meta projection
   * @param mustRebuild                {@code true} iff any of the parts (items or tails or meta) has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull MMP transformMapProjection(
      @NotNull MMP mapModelProjection,
      @NotNull P transformedItemsProjection,
      @Nullable List<MMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild);

  /**
   * Transforms list model projection
   *
   * @param listModelProjection        original instance
   * @param transformedItemsProjection transformed items projection
   * @param transformedTails           transformed tail projections
   * @param transformedMeta            transformed meta projection
   * @param mustRebuild                {@code true} iff any of the parts (items or tails or meta) has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull LMP transformListProjection(
      @NotNull LMP listModelProjection,
      @NotNull P transformedItemsProjection,
      @Nullable List<LMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild);

  /**
   * Transforms primitive model projection
   *
   * @param primitiveModelProjection original instance
   * @param transformedTails         transformed tail projections
   * @param transformedMeta          transformed meta projection
   * @param mustRebuild              {@code true} iff any of the parts (tails or meta) has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull PMP transformPrimitiveProjection(
      @NotNull PMP primitiveModelProjection,
      @Nullable List<PMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild);

  @SuppressWarnings("unchecked")
  private @NotNull P newRef(@NotNull TypeApi type, @NotNull TextLocation location) {
    if (type.kind() == TypeKind.ENTITY)
      return (P) newEntityRef(type, location);
    else
      return (P) newModelRef((DatumTypeApi) type, location);
  }

  protected abstract @NotNull EP newEntityRef(@NotNull TypeApi type, @NotNull TextLocation location);

  protected abstract @NotNull MP newModelRef(@NotNull DatumTypeApi model, @NotNull TextLocation location);

//  // helper stuff for transformer chaining
//
//  protected static <T> @Nullable T chainTransMap(@NotNull Function<T, T> f, @NotNull Function<T, T> s, @NotNull T key) {
//    T fv = f.apply(key);
//    T sv = s.apply(key);
//
//    if (fv != null && sv != null)
//      throw new IllegalArgumentException("Both first and second transformation maps contain '" + key + "'");
//
//    if (fv == null) {
//      return sv;
//    } else {
//      sv = s.apply(fv);
//      return sv == null ? fv : sv;
//    }
//  }
}
