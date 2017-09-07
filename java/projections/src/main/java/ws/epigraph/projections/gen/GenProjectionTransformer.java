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
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TagApi;
import ws.epigraph.types.TypeApi;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@Deprecated // transformations break references
public abstract class GenProjectionTransformer<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, /*RMP*/?, /*RMP*/?, /*M*/?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP, FP>
    > {

  private final Map<VP, VP> transformedCache = new IdentityHashMap<>();
  private final Map<VP, VP> visited = new IdentityHashMap<>();
//  private final Map<VP, VP> postponed = new IdentityHashMap<>();
  private final Set<VP> usedRecursively = Collections.newSetFromMap(new IdentityHashMap<>());

  private GenProjectionTransformationMap<VP, MP> transformationMap = null;

  void reset() {
    transformedCache.clear();
    visited.clear();
//    postponed.clear();
    usedRecursively.clear();
  }

  public @NotNull VP transform(
      @NotNull GenProjectionTransformationMap<VP, MP> transformationMap,
      @NotNull VP projection) {

    this.transformationMap = transformationMap;
    return transform(projection);
  }

  protected @NotNull VP transform(@NotNull VP projection) {
    VP cached = transformedCache.get(projection);
    if (cached != null)
      return cached;
    if (transformedCache.values().contains(projection))
      return projection; // avoid transforming what is already transformed

    // postpone transformation if projection is not resolved yet
//    VP res = postponed.get(projection);
//    if (res != null)
//      return res;

    if (projection.isResolved()) {
      return transformResolved(projection);
    } else {
      final VP ref = newEntityRef(projection.type(), projection.location());
      transformedCache.put(projection, ref);
//      postponed.put(projection, ref);
      projection.runOnResolved(() -> {
        VP transformed = transformResolved(projection);
        transformed.runOnResolved(() -> {
//          postponed.remove(projection);
          ref.resolve(projection.referenceName(), transformed);
        });
      });
      return ref;
    }
  }

  private @NotNull VP transformResolved(@NotNull VP projection) {
    VP res = visited.get(projection);
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
    List<VP> transformedTails = null;

    List<VP> tails = projection.polymorphicTails();
    if (tails != null) {
      transformedTails = new ArrayList<>(tails.size());
      for (final VP tail : tails) {
        VP transformedTail = transform(tail);

        if (transformedTail != tail)
          tailsChanged = true;
        transformedTails.add(transformedTail);
      }
    }

    VP transformed = transformVarProjection(projection, transformedTags, transformedTails, tagsChanged || tailsChanged);
    visited.remove(projection);
    boolean usedRec = usedRecursively.contains(res);
    usedRecursively.remove(res);

    if (usedRec) {
      res.resolve(projection.referenceName(), transformed);
      transformationMap.addEntityMapping(projection, res);
      transformedCache.put(projection, res);
      return res;
    } else {
      transformationMap.addEntityMapping(projection, transformed);
      transformedCache.put(projection, transformed);
      return transformed;
    }
  }


  @SuppressWarnings("unchecked")
  protected @NotNull MP transform(@NotNull MP projection) {
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
      FP transformedFp = transform(fp);
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

  public @NotNull FP transform(@NotNull GenProjectionTransformationMap<VP, MP> transformationMap, @NotNull FP fp) {
    this.transformationMap = transformationMap;
    return transform(fp);
  }

  private @NotNull FP transform(@NotNull FP fp) {
    VP ep = fp.varProjection();
    VP transformedEp = transform(ep);
    return transformFieldProjection(fp, transformedEp, ep != transformedEp);
  }

  protected @NotNull MMP transform(
      @NotNull MMP projection,
      @Nullable List<MMP> transformedTails,
      @Nullable MP transformedMeta,
      boolean mustRebuild) {

    //noinspection unchecked
    VP transformedItemsProjection = transform(projection.itemsProjection());
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
    VP transformedItemsProjection = transform(projection.itemsProjection());
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
   * @param varProjection             original instance
   * @param transformedTagProjections transformed tag projections
   * @param transformedTails          transformed tail projections
   * @param mustRebuild               {@code true} iff any of the parts (tags or tails) has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull VP transformVarProjection(
      @NotNull VP varProjection,
      @NotNull Map<String, TP> transformedTagProjections,
      @Nullable List<VP> transformedTails,
      boolean mustRebuild);

  /**
   * Transforms tag projection
   *
   * @param varProjection              original var projection
   * @param tag                        tag
   * @param tagProjection              original instance
   * @param transformedModelProjection transformed model projection
   * @param mustRebuild                {@code true} iff model projection has changed and projection must be rebuilt
   *
   * @return original or transformed projection
   */
  protected abstract @NotNull TP transformTagProjection(
      @NotNull VP varProjection,
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
      @NotNull VP transformedEntityProjection,
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
      @NotNull VP transformedItemsProjection,
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
      @NotNull VP transformedItemsProjection,
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

  protected abstract @NotNull VP newEntityRef(@NotNull TypeApi type, @NotNull TextLocation location);

  protected abstract @NotNull MP newModelRef(@NotNull DatumTypeApi model, @NotNull TextLocation location);
}
