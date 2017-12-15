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

/* Created by yegor on 10/24/16. */

package ws.epigraph.wire.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.gen.*;
import ws.epigraph.types.*;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.function.Supplier;

public final class JsonFormatCommon {

  private JsonFormatCommon() {}

  /**
   * Recursively traverse all {@code projections}, including tails, and collect those applicable to {@code type},
   * with most specific one being last
   */
  public static <P extends GenProjection<? extends P, ?, ?, ?>, Acc extends Collection<P>> @NotNull Acc flatten(
      @NotNull Acc acc,
      @NotNull Collection<? extends P> projections,
      @NotNull TypeApi type
  ) {
    // TODO more careful ordering of projections might be needed to ensure last one is the most precise in complex cases
    for (P projection : projections) append(acc, projection, type);
    return acc;
  }

  public static <P extends GenProjection<? extends P, ?, ?, ?>, Acc extends Collection<P>> Acc append(
      @NotNull Acc acc,
      @NotNull P projection,
      @NotNull TypeApi type
  ) {
    // effectively this is
    // Collections.reverse(ProjectionUtils.linearizeTails(type, Collections.singleton(varProjection).stream()));

    if (projection.type().isAssignableFrom(type)) acc.add(projection);
    Iterable<? extends P> tails = projection.polymorphicTails();
    if (tails != null) for (P tail : tails) {
      if (tail.type().isAssignableFrom(type)) return append(acc, tail, type); // dfs
    }
    return acc;
  }

//
//  public static @NotNull Type mostSpecificType(
//      @NotNull List<? extends AbstractVarProjection> projections // non-empty, polymorphic tails ignored
//  ) {
//    Type type = null;
//    for (
//        ListIterator<? extends AbstractVarProjection> it = projections.listIterator(projections.size());
//        it.hasPrevious();
//        ) {
//      Type vpType = it.previous().type();
//      if (type == null || type.isAssignableFrom(vpType)) type = vpType;
//    }
//    assert type != null : "empty projections";
//    return type;
//  }

  // return 'tag' if all projections are of the form ':tag(...)'
  public static @Nullable <P extends GenProjection<? extends P, ?, ?, ?>>
  String monoTag(@NotNull Iterable<? extends P> projections) {
    String tagName = null;
    for (P p : projections) {
      // TODO confirm multi-tag projection yields parenthesized=true (even if built from defaults)
      if (p.isEntityProjection() && p.asEntityProjection().parenthesized()) return null;
      for (String vpTagName : p.tagProjections().keySet()) {
        if (tagName == null) tagName = vpTagName;
        else if (!tagName.equals(vpTagName)) return null;
      }
    }
    return tagName; // non-null if there was exactly one tag and no parenthesized projections
  }

  /**
   * @return non-empty collection or `null`
   */
  public static <
      P extends GenProjection<? extends P, TP, ?, ? extends MP>,
      TP extends GenTagProjectionEntry<TP, MP>,
      MP extends GenModelProjection<?, TP, /*MP*/?, ?, ?>,
      Coll extends Collection<MP>>

  @Nullable Coll tagModelProjections(
      @NotNull TagApi tag,
      @NotNull Iterable<? extends P> projections, // non-empty, polymorphic tails ignored
      @NotNull Supplier<@NotNull Coll> collSupplier
  ) {
    Coll tagModelProjections = null;
    for (P p : projections) {
      TP tagProjection = p.tagProjections().get(tag.name());
      if (tagProjection != null) {
        if (tagModelProjections == null) tagModelProjections = collSupplier.get();
        tagModelProjections.add(tagProjection.modelProjection());
      }
    }
    return tagModelProjections;
  }

  /**
   * @return non-empty collection or `null`
   */
  public static <
      P extends GenProjection<? extends P, ?, ?, ?>,
      RMP extends GenRecordModelProjection<P, ?, ?, ?, RMP, FPE, ?, ?>,
      FPE extends GenFieldProjectionEntry<P, ?, ?, ?>,
      Coll extends Collection<P>>
  @Nullable Coll fieldProjections(
      @NotNull Iterable<? extends RMP> projections, // non-empty
      @NotNull FieldApi field,
      @NotNull Supplier<@NotNull Coll> collSupplier
  ) {
    Coll res = null;
    for (RMP mp : projections) {
      FPE fieldProjectionEntry = mp.fieldProjection(field.name());
      if (fieldProjectionEntry != null) {
        if (res == null) res = collSupplier.get();
        res.add(fieldProjectionEntry.fieldProjection().projection());
      }
    }
    return res;
  }

}
