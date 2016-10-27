/* Created by yegor on 10/24/16. */

package io.epigraph.wire.json;

import io.epigraph.projections.abs.AbstractVarProjection;
import io.epigraph.projections.req.output.ReqOutputFieldProjectionEntry;
import io.epigraph.projections.req.output.ReqOutputModelProjection;
import io.epigraph.projections.req.output.ReqOutputRecordModelProjection;
import io.epigraph.projections.req.output.ReqOutputTagProjectionEntry;
import io.epigraph.projections.req.output.ReqOutputVarProjection;
import io.epigraph.types.RecordType.Field;
import io.epigraph.types.Type;
import io.epigraph.types.Type.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;

public abstract class JsonFormatCommon {

  private JsonFormatCommon() {}

  public static <Acc extends Collection<ReqOutputVarProjection>> @NotNull Acc flatten(
      @NotNull Acc acc,
      @NotNull Collection<? extends ReqOutputVarProjection> projections,
      @NotNull Type varType
  ) {
    // TODO more careful ordering of projections might be needed to ensure last one is the most precise in complex cases
    for (ReqOutputVarProjection projection : projections) append(acc, projection, varType);
    return acc;
  }

  public static <Acc extends Collection<ReqOutputVarProjection>> Acc append(
      @NotNull Acc acc,
      @NotNull ReqOutputVarProjection varProjection,
      @NotNull Type actualType
  ) {
    acc.add(varProjection);
    Iterable<ReqOutputVarProjection> tails = varProjection.polymorphicTails();
    if (tails != null) for (ReqOutputVarProjection tail : tails) {
      if (tail.type().isAssignableFrom(actualType)) return append(acc, tail, actualType); // dfs
    }
    return acc;
  }

  public static @NotNull Type mostSpecificType(
      @NotNull List<? extends AbstractVarProjection> projections // non-empty, polymorphic tails ignored
  ) {
    Type type = null;
    for (
        ListIterator<? extends AbstractVarProjection> it = projections.listIterator(projections.size());
        it.hasPrevious();
        ) {
      Type vpType = it.previous().type();
      if (type == null || type.isAssignableFrom(vpType)) type = vpType;
    }
    assert type != null : "empty projections";
    return type;
  }

  @Deprecated // use monoTag(projections) == null
  public static boolean needMultiRendering(@NotNull Iterable<? extends ReqOutputVarProjection> projections) {
    return monoTag(projections) == null;
  }

  public static @Nullable String monoTag(@NotNull Iterable<? extends ReqOutputVarProjection> projections) {
    String tagName = null;
    for (ReqOutputVarProjection vp : projections) {
      // TODO confirm multi-tag projection yields parenthesized=true (even if built from defaults)
      if (vp.parenthesized()) return null;
      for (String vpTagName : vp.tagProjections().keySet()) {
        if (tagName == null) tagName = vpTagName;
        else if (!tagName.equals(vpTagName)) return null;
      }
    }
    return tagName; // non-null if there was exactly one tag and no parenthesized projections
  }

  /** @return non-empty collection or `null` */
  public static <Coll extends Collection<ReqOutputModelProjection>> @Nullable Coll tagModelProjections(
      @NotNull Tag tag,
      @NotNull Iterable<? extends ReqOutputVarProjection> projections, // non-empty, polymorphic tails ignored
      @NotNull Supplier<@NotNull Coll> collSupplier
  ) {
    Coll tagModelProjections = null;
    for (ReqOutputVarProjection vp : projections) {
      ReqOutputTagProjectionEntry tagProjection = vp.tagProjection(tag.name());
      if (tagProjection != null) {
        if (tagModelProjections == null) tagModelProjections = collSupplier.get();
        tagModelProjections.add(tagProjection.projection());
      }
    }
    return tagModelProjections;
  }

  /** @return non-empty collection or `null` */
  public static <Coll extends Collection<ReqOutputVarProjection>> @Nullable Coll fieldVarProjections(
      @NotNull Iterable<? extends ReqOutputRecordModelProjection> projections, // non-empty
      @NotNull Field field,
      @NotNull Supplier<@NotNull Coll> collSupplier
  ) {
    Coll varProjections = null;
    for (ReqOutputRecordModelProjection mp : projections) {
      ReqOutputFieldProjectionEntry fieldProjectionEntry = mp.fieldProjection(field.name());
      if (fieldProjectionEntry != null) {
        if (varProjections == null) varProjections = collSupplier.get();
        varProjections.add(fieldProjectionEntry.projection().projection());
      }
    }
    return varProjections;
  }

}
