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
import ws.epigraph.types.TagApi;

import java.util.*;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenGuidedProjectionTraversal<
    P extends GenProjection<? extends P, TP, EP, ? extends MP>,
    EP extends GenEntityProjection<EP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection<EP, TP, /*MP*/?, /*RMP*/?, /*RMP*/? /*M*/>,
    RMP extends GenRecordModelProjection<P, TP, EP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<P, TP, EP, MP, MMP, ?>,
    LMP extends GenListModelProjection<P, TP, EP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<EP, TP, MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<P, TP, MP, FP>,
    FP extends GenFieldProjection<P, TP, MP, FP>,
    // "guide" projection (op)
    GP extends GenProjection<? extends GP, GTP, GEP, ? extends GMP>,
    GEP extends GenEntityProjection<GEP, GTP, GMP>,
    GTP extends GenTagProjectionEntry<GTP, GMP>,
    GMP extends GenModelProjection<GEP, GTP, /*GMP*/?, /*GRMP*/?, /*GRMP*/? /*M*/>,
    GRMP extends GenRecordModelProjection<GP, GTP, GEP, GMP, GRMP, GFPE, GFP, ?>,
    GMMP extends GenMapModelProjection<GP, GTP, GEP, GMP, GMMP, ?>,
    GLMP extends GenListModelProjection<GP, GTP, GEP, GMP, GLMP, ?>,
    GPMP extends GenPrimitiveModelProjection<GEP, GTP, GMP, GPMP, ?>,
    GFPE extends GenFieldProjectionEntry<GP, GTP, GMP, GFP>,
    GFP extends GenFieldProjection<GP, GTP, GMP, GFP>
    > {

  // NB keep code in sync with GenProjectionTraversal

  private final Set<P> visited = Collections.newSetFromMap(new IdentityHashMap<>());

  protected @Nullable String currentDataDescription; // e.g. a field name or a map value reference
  protected @Nullable TextLocation currentDataLocation;

  public boolean traverse(@NotNull P projection, @NotNull GP guide) {
    if (visited.contains(projection)) return true;
    else {
      visited.add(projection);
      if (projection.isResolved())
        return traverse0(projection, guide);
      else {
        projection.runOnResolved(() -> traverse0(projection, guide));
        return true; // ???
      }
    }
  }

  @SuppressWarnings("unchecked")
  private boolean traverse0(@NotNull P projection, @NotNull GP guide) {
    if (visitProjection(projection, guide)) {
      if (projection.isEntityProjection()) {
        EP ep = projection.asEntityProjection();
        GEP gep = guide.asEntityProjection();

        // tags
        for (final Map.Entry<String, TP> entry : projection.tagProjections().entrySet()) {
          final TP tagProjection = entry.getValue();
          final GTP guideTagProjection = guide.tagProjections().get(tagProjection.tag().name());

          if (guideTagProjection == null)
            registerMissingGuideTag(ep, gep, tagProjection.tag());
          else {
            if (!visitTagProjection(ep, gep, entry.getKey(), tagProjection, guideTagProjection)) return false;

            if (!traverse((P) tagProjection.modelProjection(), (GP) guideTagProjection.modelProjection()))
              return false;
          }
        }
      } else {
        MP mp = projection.asModelProjection();
        GMP gmp = guide.asModelProjection();

        if (!traverseModel(mp, gmp)) return false;
      }

      // tails
      final List<? extends P> tails = projection.polymorphicTails();
      if (tails != null) {
        assert !tails.isEmpty();

        List<? extends GP> guideTails = guide.polymorphicTails();
        if (guideTails == null) {
          for (final P tail : tails) {
            registerMissingGuideTail(projection, guide, tail);
          }
        } else {
          for (final P tail : tails) {
            Optional<? extends GP> guideTailOpt =
                guideTails.stream().filter(gt -> gt.type() == tail.type()).findFirst();
            if (guideTailOpt.isPresent()) {
              if (!traverse(tail, guideTailOpt.get())) return false;
            } else {
              registerMissingGuideTail(projection, guide, tail);
            }
          }
        }
      }

      return true;
    } else return false;
  }

  @SuppressWarnings("unchecked")
  private boolean traverseModel(@NotNull MP projection, @NotNull GMP guide) {
    if (visitModelProjection(projection, guide)) {

      switch (projection.type().kind()) {

        case RECORD:
          if (!traverse((RMP) projection, (GRMP) guide)) return false;
          break;
        case MAP:
          if (!traverse((MMP) projection, (GMMP) guide)) return false;
          break;
        case LIST:
          if (!traverse((LMP) projection, (GLMP) guide)) return false;
          break;
        case PRIMITIVE:
          if (!traverse((PMP) projection, (GPMP) guide)) return false;
          break;
        case ENUM:
          throw new UnsupportedOperationException();
        default:
          throw new IllegalStateException();

      }

      return true;
    } else return false;
  }

  protected boolean traverse(@NotNull RMP projection, @NotNull GRMP guide) {
    if (visitRecordModelProjection(projection, guide)) {

      //noinspection unchecked
      for (final Map.Entry<String, FPE> entry : projection.fieldProjections().entrySet()) {
        FPE fpe = entry.getValue();
        GFPE gfpe = guide.fieldProjection(entry.getKey());

        if (gfpe == null)
          registerMissingGuideField(projection, guide, entry.getKey());
        else {
          if (!traverse(projection, guide, fpe, gfpe)) return false;
        }
      }

      return true;
    } else return false;
  }

  protected boolean traverse(@NotNull RMP projection, @NotNull GRMP gp, @NotNull FPE fpe, @NotNull GFPE gfpe) {
    return withCurrentData(
        String.format("field '%s'", fpe.field().name()),
        fpe.location(),
        () -> visitFieldProjectionEntry(projection, gp, fpe, gfpe) &&
              traverse(fpe.fieldProjection(), gfpe.fieldProjection())
    );
  }

  public boolean traverse(@NotNull FP fp, @NotNull GFP gfp) {
    if (visitFieldProjection(fp, gfp)) {
      P EP = fp.projection();
      GP GEP = gfp.projection();
      return traverse(EP, GEP);
    } else return false;
  }

  protected boolean traverse(@NotNull MMP projection, @NotNull GMMP guide) {
    if (!visitMapModelProjection(projection, guide))
      return false;

    return withCurrentData(
        currentDataDescription + " value",
        projection.itemsProjection().location(),
        () -> traverse(projection.itemsProjection(), guide.itemsProjection())
    );

  }

  protected boolean traverse(@NotNull LMP projection, @NotNull GLMP guide) {
    if (!visitListModelProjection(projection, guide))
      return false;

    return withCurrentData(
        currentDataDescription + " item",
        projection.itemsProjection().location(),
        () ->
            traverse(projection.itemsProjection(), guide.itemsProjection())
    );
  }

  protected boolean traverse(@NotNull PMP projection, @NotNull GPMP guide) {
    return visitPrimitiveModelProjection(projection, guide);
  }

  protected boolean visitProjection(@NotNull P projection, @NotNull GP guide) {
    if (projection.isEntityProjection())
      return visitEntityProjection(projection.asEntityProjection(), guide.asEntityProjection());
    else
      return visitModelProjection(projection.asModelProjection(), guide.asModelProjection());
  }

  /**
   * Visits var projection
   *
   * @param entityProjection instance to visit
   * @param guide            guide projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitEntityProjection(@NotNull EP entityProjection, @NotNull GEP guide) { return true; }

  /**
   * Visits tag projection
   *
   * @param varProjection      var projection
   * @param guideVarProjection guide var projection
   * @param tagName            tag name
   * @param tagProjection      instance to visit
   * @param guideTag           guide tag projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitTagProjection(
      @NotNull EP varProjection,
      @NotNull GEP guideVarProjection,
      @NotNull String tagName,
      @NotNull TP tagProjection,
      @NotNull GTP guideTag) {

    return true;
  }

  /**
   * Visits model projection
   *
   * @param modelProjection instance to visit
   * @param guide           guide projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitModelProjection(@NotNull MP modelProjection, @NotNull GMP guide) { return true; }

  /**
   * Visits record model projection
   *
   * @param recordModelProjection instance to visit
   * @param guide                 guide projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitRecordModelProjection(@NotNull RMP recordModelProjection, @NotNull GRMP guide) { return true; }

  /**
   * Visits field projection entry
   *
   * @param modelProjection      record model projection
   * @param guideModelProjection guide record model projection
   * @param fieldProjectionEntry instance to visit
   * @param guideProjectionEntry guide projection entry to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitFieldProjectionEntry(
      @NotNull RMP modelProjection,
      @NotNull GRMP guideModelProjection,
      @NotNull FPE fieldProjectionEntry,
      @NotNull GFPE guideProjectionEntry) {

    return true;
  }

  /**
   * Visits field projection
   *
   * @param fieldProjection field projection
   * @param guide           guide projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitFieldProjection(@NotNull FP fieldProjection, @NotNull GFP guide) { return true; }

  /**
   * Visits map model projection
   *
   * @param mapModelProjection instance to visit
   * @param guide              guide projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitMapModelProjection(@NotNull MMP mapModelProjection, @NotNull GMMP guide) { return true; }

  /**
   * Visits list model projection
   *
   * @param listModelProjection instance to visit
   * @param guide               guide projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitListModelProjection(@NotNull LMP listModelProjection, @NotNull GLMP guide) { return true; }

  /**
   * Visits primitive model projection
   *
   * @param primitiveModelProjection instance to visit
   * @param guide                    guide projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitPrimitiveModelProjection(
      @NotNull PMP primitiveModelProjection,
      @NotNull GPMP guide) {

    return true;
  }

  protected void registerMissingGuideTag(@NotNull EP projection, @NotNull GEP guide, @NotNull TagApi tag) {}

  protected void registerMissingGuideTail(@NotNull P projection, @NotNull GP guide, @NotNull P tail) {}

  protected void registerMissingGuideField(@NotNull RMP mp, @NotNull GRMP gmp, @NotNull String fieldName) { }

  private <R> R withCurrentData(String currentDataDescription, TextLocation currentDataLocation, Supplier<R> sup) {
    String pd = this.currentDataDescription;
    TextLocation pl = this.currentDataLocation;

    this.currentDataDescription = currentDataDescription;
    this.currentDataLocation = currentDataLocation;

    try {
      return sup.get();
    } finally {
      this.currentDataDescription = pd;
      this.currentDataLocation = pl;
    }
  }
}
