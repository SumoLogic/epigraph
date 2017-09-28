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
import ws.epigraph.types.TagApi;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenGuidedProjectionTraversal<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, /*RMP*/?, /*RMP*/?, /*M*/?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP, FP>,
    // "guide" projection (op)
    GVP extends GenVarProjection<GVP, GTP, GMP>,
    GTP extends GenTagProjectionEntry<GTP, GMP>,
    GMP extends GenModelProjection</*GMP*/?, /*GRMP*/?, /*GRMP*/?, /*M*/?>,
    GRMP extends GenRecordModelProjection<GVP, GTP, GMP, GRMP, GFPE, GFP, ?>,
    GMMP extends GenMapModelProjection<GVP, GTP, GMP, GMMP, ?>,
    GLMP extends GenListModelProjection<GVP, GTP, GMP, GLMP, ?>,
    GPMP extends GenPrimitiveModelProjection<GMP, GPMP, ?>,
    GFPE extends GenFieldProjectionEntry<GVP, GTP, GMP, GFP>,
    GFP extends GenFieldProjection<GVP, GTP, GMP, GFP>
    > {

  // NB keep code in sync with GenProjectionTraversal

  private final Set<VP> visitedEntities = Collections.newSetFromMap(new IdentityHashMap<>());
  private final Set<MP> visitedModels = Collections.newSetFromMap(new IdentityHashMap<>());

  public boolean traverse(@NotNull VP projection, @NotNull GVP guide) {
    if (visitedEntities.contains(projection)) return true;
    else {
      visitedEntities.add(projection);
      if (projection.isResolved())
        return traverse0(projection, guide);
      else {
        projection.runOnResolved(() -> traverse0(projection, guide));
        return true; // ???
      }
    }
  }

  private boolean traverse0(@NotNull VP projection, @NotNull GVP guide) {
    if (visitVarProjection(projection, guide)) {
      // tags
      for (final Map.Entry<String, TP> entry : projection.tagProjections().entrySet()) {
        final TP tagProjection = entry.getValue();
        final GTP guideTagProjection = guide.tagProjections().get(tagProjection.tag().name());

        if (guideTagProjection == null)
          registerMissingGuideTag(projection, guide, tagProjection.tag());
        else {
          if (!visitTagProjection(projection, guide, entry.getKey(), tagProjection, guideTagProjection)) return false;

          if (!traverse(tagProjection.projection(), guideTagProjection.projection())) return false;
        }
      }

      // tails
      final List<VP> tails = projection.polymorphicTails();
      if (tails != null) {
        assert !tails.isEmpty();

        List<GVP> guideTails = guide.polymorphicTails();
        if (guideTails == null) {
          for (final VP tail : tails) {
            registerMissingGuideTail(projection, guide, tail);
          }
        } else {
          for (final VP tail : tails) {
            Optional<GVP> guideTailOpt = guideTails.stream().filter(gt -> gt.type() == tail.type()).findFirst();
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

  private boolean traverse(@NotNull MP projection, @NotNull GMP guide) {
    if (visitedModels.contains(projection)) return true;
    else {
      visitedModels.add(projection);
      if (projection.isResolved())
        return this.traverse0(projection, guide);
      else {
        projection.runOnResolved(() -> this.traverse0(projection, guide));
        return true; // ???
      }
    }
  }

  @SuppressWarnings("unchecked")
  private boolean traverse0(@NotNull MP projection, @NotNull GMP guide) {
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
    return visitFieldProjectionEntry(projection, gp, fpe, gfpe) &&
           traverse(fpe.fieldProjection(), gfpe.fieldProjection());
  }

  public boolean traverse(@NotNull FP fp, @NotNull GFP gfp) {
    if (visitFieldProjection(fp, gfp)) {
      VP vp = fp.entityProjection();
      GVP gvp = gfp.entityProjection();
      return traverse(vp, gvp);
    } else return false;
  }

  protected boolean traverse(@NotNull MMP projection, @NotNull GMMP guide) {
    //noinspection unchecked
    return visitMapModelProjection(projection, guide) &&
           traverse(projection.itemsProjection(), guide.itemsProjection());
  }

  protected boolean traverse(@NotNull LMP projection, @NotNull GLMP guide) {
    //noinspection unchecked
    return visitListModelProjection(projection, guide) &&
           traverse(projection.itemsProjection(), guide.itemsProjection());
  }

  protected boolean traverse(@NotNull PMP projection, @NotNull GPMP guide) {
    return visitPrimitiveModelProjection(projection, guide);
  }

  /**
   * Visits var projection
   *
   * @param varProjection instance to visit
   * @param guide         guide projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitVarProjection(@NotNull VP varProjection, @NotNull GVP guide) { return true; }

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
      @NotNull VP varProjection,
      @NotNull GVP guideVarProjection,
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

  protected void registerMissingGuideTag(@NotNull VP vp, @NotNull GVP gvp, @NotNull TagApi tag) {}

  protected void registerMissingGuideTail(@NotNull VP vp, @NotNull GVP gvp, @NotNull VP tail) {}

  protected void registerMissingGuideField(@NotNull RMP mp, @NotNull GRMP gmp, @NotNull String fieldName) { }
}
