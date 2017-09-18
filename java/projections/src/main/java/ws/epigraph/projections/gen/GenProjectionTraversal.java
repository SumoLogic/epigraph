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

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class GenProjectionTraversal<
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

  // NB keep code in sync with GenGuidedProjectionTraversal

  private final Set<VP> visitedEntities = Collections.newSetFromMap(new IdentityHashMap<>());
  private final Set<MP> visitedModels = Collections.newSetFromMap(new IdentityHashMap<>());

  public boolean traverse(@NotNull VP projection) {
    if (visitedEntities.contains(projection)) return true;
    else {
      visitedEntities.add(projection);
      if (projection.isResolved())
        return traverse0(projection);
      else {
        projection.runOnResolved(() -> traverse0(projection));
        return true; // ???
      }
    }
  }

  private boolean traverse0(final @NotNull VP projection) {
    if (visitVarProjection(projection)) {
      for (final Map.Entry<String, TP> entry : projection.tagProjections().entrySet()) {
        final TP tagProjection = entry.getValue();
        if (!visitTagProjection(projection, entry.getKey(), tagProjection)) return false;

        if (!traverse(tagProjection.projection())) return false;
      }

      final List<VP> tails = projection.polymorphicTails();
      if (tails != null) {
        for (final VP tail : tails) {
          if (!traverse(tail)) return false;
        }
      }

      return true;
    } else return false;
  }

  public boolean traverse(@NotNull MP projection) {
    if (visitedModels.contains(projection)) return true;
    else {
      visitedModels.add(projection);
      if (projection.isResolved())
        return this.traverse0(projection);
      else {
        projection.runOnResolved(() -> this.traverse0(projection));
        return true; // ???
      }
    }
  }

  @SuppressWarnings("unchecked")
  private boolean traverse0(@NotNull MP projection) {
    if (visitModelProjection(projection)) {

      switch (projection.type().kind()) {

        case RECORD:
          if (!traverse((RMP) projection)) return false;
          break;
        case MAP:
          if (!traverse((MMP) projection)) return false;
          break;
        case LIST:
          if (!traverse((LMP) projection)) return false;
          break;
        case PRIMITIVE:
          if (!traverse((PMP) projection)) return false;
          break;
        case ENUM:
          throw new UnsupportedOperationException();
        default:
          throw new IllegalStateException();

      }

      return true;
    } else return false;
  }

  protected boolean traverse(@NotNull RMP projection) {
    if (visitRecordModelProjection(projection)) {

      //noinspection unchecked
      for (final Map.Entry<String, FPE> entry : projection.fieldProjections().entrySet()) {
        final FPE fpe = entry.getValue();

        if (!traverse(projection, fpe)) return false;
      }

      return true;
    } else return false;
  }

  protected boolean traverse(@NotNull RMP projection, @NotNull FPE fpe) {
    return visitFieldProjectionEntry(projection, fpe) && traverse(fpe.fieldProjection());
  }

  public boolean traverse(@NotNull FP fp) {
    if (visitFieldProjection(fp)) {
      VP vp = fp.varProjection();
      return traverse(vp);
    } else return false;
  }

  protected boolean traverse(@NotNull MMP projection) {
    //noinspection unchecked
    return visitMapModelProjection(projection) && traverse(projection.itemsProjection());
  }

  protected boolean traverse(@NotNull LMP projection) {
    //noinspection unchecked
    return visitListModelProjection(projection) && traverse(projection.itemsProjection());
  }

  protected boolean traverse(@NotNull PMP projection) { return visitPrimitiveModelProjection(projection); }

  /**
   * Visits var projection
   *
   * @param varProjection instance to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitVarProjection(@NotNull VP varProjection) { return true; }

  /**
   * Visits tag projection
   *
   * @param varProjection var projection
   * @param tagName       tag name
   * @param tagProjection instance to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitTagProjection(@NotNull VP varProjection, @NotNull String tagName, @NotNull TP tagProjection) {
    return true;
  }

  /**
   * Visits model projection
   *
   * @param modelProjection instance to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitModelProjection(@NotNull MP modelProjection) { return true; }

  /**
   * Visits record model projection
   *
   * @param recordModelProjection instance to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitRecordModelProjection(@NotNull RMP recordModelProjection) { return true; }

  /**
   * Visits field projection entry
   *
   * @param modelProjection      record model projection
   * @param fieldProjectionEntry instance to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitFieldProjectionEntry(@NotNull RMP modelProjection, @NotNull FPE fieldProjectionEntry) {
    return true;
  }

  /**
   * Visits field projection
   *
   * @param fieldProjection field projection
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitFieldProjection(@NotNull FP fieldProjection) { return true; }

  /**
   * Visits map model projection
   *
   * @param mapModelProjection instance to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitMapModelProjection(@NotNull MMP mapModelProjection) { return true; }

  /**
   * Visits list model projection
   *
   * @param listModelProjection instance to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitListModelProjection(@NotNull LMP listModelProjection) { return true; }

  /**
   * Visits primitive model projection
   *
   * @param primitiveModelProjection instance to visit
   *
   * @return {@code true} iff traversal should continue
   */
  protected boolean visitPrimitiveModelProjection(@NotNull PMP primitiveModelProjection) { return true; }
}
