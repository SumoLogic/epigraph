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
import ws.epigraph.types.TypeKind;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class GenProjectionsComparator<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, /*SMP*/?, /*TMP*/?, /*M*/?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
    MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
    LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>,
    PMP extends GenPrimitiveModelProjection<MP, PMP, ?>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP, FP>
    > {

  private final Map<RecEntry, Set<RecEntry>> visited = new HashMap<>();

  /**
   * Checks if two var projections are structurally equal. Projection types are not checked.
   *
   * @param vp1 first projection
   * @param vp2 second projection
   * @return {@code true} iff projections are structurally equal
   */
  public boolean equals(@NotNull VP vp1, @NotNull VP vp2) {
    return varEquals(Collections.singleton(vp1), Collections.singleton(vp2));
  }

  public static <
      VP extends GenVarProjection<VP, TP, MP>,
      TP extends GenTagProjectionEntry<TP, MP>,
      MP extends GenModelProjection</*MP*/?, /*SMP*/?, /*TMP*/?, /*M*/?>,
      RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, ?>,
      MMP extends GenMapModelProjection<VP, TP, MP, MMP, ?>,
      LMP extends GenListModelProjection<VP, TP, MP, LMP, ?>,
      PMP extends GenPrimitiveModelProjection<MP, PMP, ?>,
      FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
      FP extends GenFieldProjection<VP, TP, MP, FP>
      > boolean varEquals(@NotNull VP vp1, @NotNull VP vp2) {
    return new GenProjectionsComparator<VP, TP, MP, RMP, MMP, LMP, PMP, FPE, FP>().equals(vp1, vp2);
  }

  public void reset() {
    visited.clear();
  }

  public boolean varEquals(@NotNull Collection<@NotNull VP> vps1, @NotNull Collection<@NotNull VP> vps2) {
    if (vps1.isEmpty())
      return vps2.isEmpty();

    // check for recursion
    RecEntry entry1 = new RecEntry(vps1);
    RecEntry entry2 = new RecEntry(vps2);

    Set<RecEntry> entries = visited.computeIfAbsent(entry1, k -> new HashSet<>());
    if (entries.contains(entry2))
      return true;
    else
      entries.add(entry2);


    Map<String, Collection<TP>> tags1 = collectTags(vps1);
    Map<String, Collection<TP>> tags2 = collectTags(vps2);

    if (!tags1.keySet().equals(tags2.keySet()))
      return false;

    for (final Map.Entry<String, Collection<TP>> entry : tags1.entrySet()) {
      String tagName = entry.getKey();

      final List<@NotNull MP> models1 = entry.getValue()
          .stream().map(GenTagProjectionEntry::projection).collect(Collectors.toList());

      final List<@NotNull MP> models2 = tags2.get(tagName)
          .stream().map(GenTagProjectionEntry::projection).collect(Collectors.toList());

      if (!modelEquals(models1, models2))
        return false;
    }

    final List<VP> tails1 = collectTails(vps1);
    final List<VP> tails2 = collectTails(vps2);

    return varEquals(tails1, tails2);
  }

  private @NotNull Map<String, Collection<TP>> collectTags(@NotNull Collection<@NotNull VP> vps) {
    final Map<String, Collection<TP>> res = new HashMap<>();

    for (VP vp : vps) {
      for (final Map.Entry<String, TP> entry : vp.tagProjections().entrySet()) {
        Collection<TP> tps = res.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
        tps.add(entry.getValue());
      }
    }

    return res;
  }

  private List<VP> collectTails(final Collection<@NotNull VP> vps) {
    return vps.stream()
        .map(VP::polymorphicTails)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  protected boolean modelEquals(@NotNull Collection<@NotNull MP> mps1, @NotNull Collection<@NotNull MP> mps2) {
    if (mps1.isEmpty())
      return mps2.isEmpty();

    final TypeKind kind = mps1.iterator().next().type().kind();

    assert mps1.stream().allMatch(m -> m.type().kind() == kind);
    assert mps2.stream().allMatch(m -> m.type().kind() == kind);

    final List<@Nullable ?> metas1 =
        mps1.stream().map(m -> m.metaProjection()).filter(Objects::nonNull).collect(Collectors.toList());
    final List<@Nullable ?> metas2 =
        mps2.stream().map(m -> m.metaProjection()).filter(Objects::nonNull).collect(Collectors.toList());

    if (!modelEquals((Collection<MP>) metas1, (Collection<MP>) metas2))
      return false;

    switch (kind) {
      case ENTITY:
        throw new IllegalArgumentException("Unsupported model kind: " + kind);
      case RECORD:
        return recordModelEquals((Collection<RMP>) mps1, (Collection<RMP>) mps2);
      case MAP:
        return mapModelEquals((Collection<MMP>) mps1, (Collection<MMP>) mps2);
      case LIST:
        return listModelEquals((Collection<LMP>) mps1, (Collection<LMP>) mps2);
      case ENUM:
        throw new IllegalArgumentException("Unsupported model kind: " + kind);
      case PRIMITIVE:
        return primitiveModelEquals((Collection<PMP>) mps1, (Collection<PMP>) mps2);
      default:
        throw new IllegalArgumentException("Unsupported model kind: " + kind);
    }
  }

  protected boolean recordModelEquals(@NotNull Collection<@NotNull RMP> mps1, @NotNull Collection<@NotNull RMP> mps2) {

    final Map<String, Collection<FPE>> fields1 = collectFields(mps1);
    final Map<String, Collection<FPE>> fields2 = collectFields(mps2);

    if (!fields1.keySet().equals(fields2.keySet()))
      return false;

    for (final Map.Entry<String, Collection<FPE>> entry : fields1.entrySet()) {
      String fieldName = entry.getKey();

      final List<@NotNull VP> vps1 = entry.getValue()
          .stream().map(e -> e.fieldProjection().entityProjection()).collect(Collectors.toList());
      final List<@NotNull VP> vps2 = fields2.get(fieldName)
          .stream().map(e -> e.fieldProjection().entityProjection()).collect(Collectors.toList());

      if (!varEquals(vps1, vps2))
        return false;
    }

    return true;
  }

  //  @SuppressWarnings("unchecked")
  private Map<String, Collection<FPE>> collectFields(@NotNull Collection<@NotNull RMP> rmps) {
    Map<String, Collection<FPE>> res = new HashMap<>();

    for (final RMP rmp : rmps) {
      for (final Map.Entry<String, FPE> entry : rmp.fieldProjections().entrySet()) {
        final Collection<FPE> fpes = res.computeIfAbsent(entry.getKey(), k -> new ArrayList<>());
        fpes.add(entry.getValue());
      }
    }

    return res;
  }

  protected boolean mapModelEquals(@NotNull Collection<@NotNull MMP> mps1, @NotNull Collection<@NotNull MMP> mps2) {
    final List<@NotNull VP> vps1 = mps1.stream().map(MMP::itemsProjection).collect(Collectors.toList());
    final List<@NotNull VP> vps2 = mps2.stream().map(MMP::itemsProjection).collect(Collectors.toList());

    return varEquals(vps1, vps2);
  }

  protected boolean listModelEquals(@NotNull Collection<@NotNull LMP> mps1, @NotNull Collection<@NotNull LMP> mps2) {
    final List<@NotNull VP> vps1 = mps1.stream().map(LMP::itemsProjection).collect(Collectors.toList());
    final List<@NotNull VP> vps2 = mps2.stream().map(LMP::itemsProjection).collect(Collectors.toList());

    return varEquals(vps1, vps2);
  }

  protected boolean primitiveModelEquals(
      @NotNull Collection<@NotNull PMP> mps1,
      @NotNull Collection<@NotNull PMP> mps2) {

    return true;
  }

  private final class RecEntry {
    private final Collection<VP> vps;
    private final int hashCode;

    private RecEntry(final Collection<VP> vps) {
      this.vps = vps;

      int hashCode = 31;
      for (final VP vp : vps) {
        hashCode = hashCode * 31 + System.identityHashCode(vp);
      }

      this.hashCode = hashCode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final RecEntry entry = (RecEntry) o;
      if (hashCode != entry.hashCode) return false;

      for (final VP vp1 : vps) {
        boolean found = false;
        for (final VP vp2 : entry.vps) {
          if (vp1 == vp2) {
            found = true;
            break;
          }
        }
        if (!found)
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() { return hashCode; }
  }
}
