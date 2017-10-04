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

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenProjectionTransformer<
    EP extends GenVarProjection<EP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, /*RMP*/?, /*RMP*/?, /*M*/?>,
    RMP extends GenRecordModelProjection<EP, TP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<EP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<EP, TP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<EP, TP, MP, FP>,
    FP extends GenFieldProjection<EP, TP, MP, FP>
    > {

  private final Map<EP, EP> transformedEntitiesCache = new IdentityHashMap<>();
  private final Map<MP, MP> transformedModelsCache = new IdentityHashMap<>();
  private final Map<EP, EP> visited = new IdentityHashMap<>();
  private final Set<EP> usedRecursively = Collections.newSetFromMap(new IdentityHashMap<>());

  private GenProjectionTransformationMap<EP, MP> transformationMap = null;

  public void reset() {
    transformedEntitiesCache.clear();
    transformedModelsCache.clear();
    visited.clear();
    usedRecursively.clear();
  }

  public @NotNull EP transform(
      @NotNull GenProjectionTransformationMap<EP, MP> transformationMap,
      @NotNull EP projection,
      @Nullable DataTypeApi dataType) {

    this.transformationMap = transformationMap;
    return transform(projection, dataType);
  }

  protected @NotNull EP transform(@NotNull EP projection, @Nullable DataTypeApi dataType) {
    EP cached = transformedEntitiesCache.get(projection);
    if (cached != null)
      return cached;
    if (transformedEntitiesCache.values().contains(projection))
      return projection; // avoid transforming what is already transformed

    // postpone transformation if projection is not resolved yet

    if (projection.isResolved()) {
      return transformResolved(projection, dataType);
    } else {
      final EP ref = newEntityRef(projection.type(), projection.location());
      transformedEntitiesCache.put(projection, ref);
      projection.runOnResolved(() -> {
        EP transformed = transformResolved(projection, dataType);
        transformed.runOnResolved(() -> ref.resolve(projection.referenceName(), transformed));
      });
      return ref;
    }
  }

  protected @NotNull EP transformResolved(@NotNull EP projection, @Nullable DataTypeApi dataType) {
    EP res = visited.get(projection);
    if (res != null) {
      usedRecursively.add(res); // do the same for models? or vars is enough?
      return res;
    }

    res = newEntityRef(projection.type(), projection.location());
    visited.put(projection, res);

    boolean tagsChanged = false;
    Map<String, TP> transformedTags = new LinkedHashMap<>(projection.tagProjections().size());

    for (final Map.Entry<String, TP> entry : projection.tagProjections().entrySet()) {
      TP tp = entry.getValue();
      MP mp = tp.projection();
      MP transformedMp = transform(mp);
      TagApi tag = projection.type().tagsMap().get(entry.getKey());
      TP transformedTp = transformTagProjection(projection, tag, tp, transformedMp, mp != transformedMp);

      if (transformedTp != tp)
        tagsChanged = true;

      transformedTags.put(entry.getKey(), transformedTp);
    }

    boolean tailsChanged = false;
    List<EP> transformedTails = null;

    List<EP> tails = projection.polymorphicTails();
    if (tails != null) {
      transformedTails = new ArrayList<>(tails.size());
      for (final EP tail : tails) {
        EP transformedTail = transform(tail, dataType);

        if (transformedTail != tail)
          tailsChanged = true;
        transformedTails.add(transformedTail);
      }
    }

    EP transformed = transformEntityProjection(
        projection,
        dataType,
        transformedTags,
        transformedTails,
        tagsChanged || tailsChanged
    );
    visited.remove(projection);
    boolean usedRec = usedRecursively.contains(res);
    usedRecursively.remove(res);

    if (usedRec) {
      res.resolve(projection.referenceName(), transformed);
      transformationMap.addEntityMapping(projection, res);
      transformedEntitiesCache.put(projection, res);
      return res;
    } else {
      transformationMap.addEntityMapping(projection, transformed);
      transformedEntitiesCache.put(projection, transformed);
      return transformed;
    }
  }

  public @NotNull MP transform(
      @NotNull GenProjectionTransformationMap<EP, MP> transformationMap,
      @NotNull MP projection) {

    this.transformationMap = transformationMap;
    return transform(projection);
  }

  @SuppressWarnings("unchecked")
  protected @NotNull MP transform(@NotNull MP projection) {
    MP cached = transformedModelsCache.get(projection);
    if (cached != null)
      return cached;
    if (transformedModelsCache.values().contains(projection))
      return projection; // avoid transforming what is already transformed

    // postpone transformation if projection is not resolved yet

    if (projection.isResolved()) {
      return transformResolved(projection);
    } else {
      final MP ref = newModelRef(projection.type(), projection.location());
      transformedModelsCache.put(projection, ref);
      projection.runOnResolved(() -> {
        MP transformed = transformResolved(projection);
        transformed.runOnResolved(() -> ((GenProjectionReference<MP>) ref).resolve(
            projection.referenceName(),
            transformed
        ));
      });
      return ref;
    }

  }

  @SuppressWarnings("unchecked")
  private @NotNull MP transformResolved(@NotNull MP projection) {
    // postpone transformation if projection is not resolved yet
    if (!projection.isResolved()) {
      final MP ref = newModelRef(projection.type(), projection.location());
      projection.runOnResolved(() -> {
        MP transformed = transform(projection);
        ((GenProjectionReference<MP>) ref).resolve(projection.referenceName(), transformed);
      });
      return ref;
    }

    boolean tailsChanged = false;
    List<MP> transformedTails = null;

    List<MP> tails = (List<MP>) projection.polymorphicTails();
    if (tails != null) {
      transformedTails = new ArrayList<>();
      for (final MP tail : tails) {
        MP transformedTail = transform(tail);

        if (transformedTail != tail)
          tailsChanged = true;
        transformedTails.add(transformedTail);
      }
    }

    MP meta = (MP) projection.metaProjection();
    MP transformedMeta = null;

    if (meta != null)
      transformedMeta = transform(meta);

    boolean mustRebuild = tailsChanged || meta != transformedMeta;
    final MP transformed;

    switch (projection.type().kind()) {

      case RECORD:
        transformed = (MP) transform((RMP) projection, (List<RMP>) transformedTails, transformedMeta, mustRebuild);
        break;
      case MAP:
        transformed = (MP) transform((MMP) projection, (List<MMP>) transformedTails, transformedMeta, mustRebuild);
        break;
      case LIST:
        transformed = (MP) transform((LMP) projection, (List<LMP>) transformedTails, transformedMeta, mustRebuild);
        break;
      case PRIMITIVE:
        transformed = (MP) transform((PMP) projection, (List<PMP>) transformedTails, transformedMeta, mustRebuild);
        break;
      case ENUM:
        throw new UnsupportedOperationException();
      default:
        throw new IllegalStateException();

    }

    if (projection != transformed)
      transformationMap.addModelMapping(projection, transformed);

    return transformed;
  }

  protected @NotNull RMP transform(
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
      FP transformedFp = transform(fp, fpe.field().dataType());
      FPE transformed = transformFieldProjectionEntry(projection, fpe, transformedFp, fp != transformedFp);

      if (transformed != fpe)
        fieldsChanged = true;
      transformedFields.put(entry.getKey(), transformed);
    }

    return transformRecordModelProjection(
        projection,
        transformedFields,
        transformedTails,
        transformedMeta,
        mustRebuild || fieldsChanged
    );
  }

  public @NotNull FP transform(
      @NotNull GenProjectionTransformationMap<EP, MP> transformationMap,
      @NotNull FP fp,
      @NotNull DataTypeApi dataType) {

    this.transformationMap = transformationMap;
    return transform(fp, dataType);
  }

  private @NotNull FP transform(@NotNull FP fp, @NotNull DataTypeApi dataType) {
    EP ep = fp.entityProjection();
    EP transformedEp = transform(ep, dataType);
    return transformFieldProjection(fp, transformedEp, ep != transformedEp);
  }

  protected @NotNull MMP transform(
      @NotNull MMP projection,
      @Nullable List<MMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild) {

    //noinspection unchecked
    EP transformedItemsProjection = transform(projection.itemsProjection(), projection.type().valueType());
    //noinspection unchecked
    return transformMapModelProjection(
        projection,
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild || projection.itemsProjection() != transformedItemsProjection
    );
  }

  protected @NotNull LMP transform(
      @NotNull LMP projection,
      @Nullable List<LMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild) {

    //noinspection unchecked
    EP transformedItemsProjection = transform(projection.itemsProjection(), projection.type().elementType());
    //noinspection unchecked
    return transformListModelProjection(
        projection,
        transformedItemsProjection,
        transformedTails,
        transformedMeta,
        mustRebuild || projection.itemsProjection() != transformedItemsProjection
    );
  }

  protected @NotNull PMP transform(
      @NotNull PMP projection,
      @Nullable List<PMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild) {
    return transformPrimitiveModelProjection(projection, transformedTails, transformedMeta, mustRebuild);
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
   * @param entityProjection              original var projection
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
  protected abstract @NotNull RMP transformRecordModelProjection(
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
      @NotNull EP transformedEntityProjection,
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
  protected abstract @NotNull MMP transformMapModelProjection(
      @NotNull MMP mapModelProjection,
      @NotNull EP transformedItemsProjection,
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
  protected abstract @NotNull LMP transformListModelProjection(
      @NotNull LMP listModelProjection,
      @NotNull EP transformedItemsProjection,
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
  protected abstract @NotNull PMP transformPrimitiveModelProjection(
      @NotNull PMP primitiveModelProjection,
      @Nullable List<PMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild);

  protected abstract @NotNull EP newEntityRef(@NotNull TypeApi type, @NotNull TextLocation location);

  protected abstract @NotNull MP newModelRef(@NotNull DatumTypeApi model, @NotNull TextLocation location);
}
