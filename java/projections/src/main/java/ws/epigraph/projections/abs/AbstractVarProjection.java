/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.projections.abs;

import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class AbstractVarProjection<
    VP extends AbstractVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<MP>,
    MP extends GenModelProjection</*MP*/?, ?>
    > implements GenVarProjection<VP, TP, MP> {

  @NotNull
  private final Type type;
  @NotNull
  private final Map<String, TP> tagProjections;
  @Nullable
  private final List<VP> polymorphicTails;

  private int polymorphicDepth = -1;
  @NotNull
  private final TextLocation location;

  public AbstractVarProjection(
      @NotNull Type type,
      @NotNull Map<String, TP> tagProjections,
      @Nullable List<VP> polymorphicTails,
      @NotNull TextLocation location) {
    this.type = type;
    this.tagProjections = tagProjections;
    this.polymorphicTails = polymorphicTails;
    this.location = location;

    // todo validate tag types == tag projection models
    // todo validate tails (should be subtypes of `type`)
  }

  @NotNull
  public Type type() { return type; }

  @NotNull
  public Map<String, TP> tagProjections() { return tagProjections; }

  @Nullable
  public TP tagProjection(@NotNull String tagName) { return tagProjections.get(tagName); }

  @Nullable
  public List<VP> polymorphicTails() { return polymorphicTails; }

  /**
   * Max polymorphic tail depth.
   */
  public int polymorphicDepth() {
    if (polymorphicDepth == -1)
      polymorphicDepth = polymorphicTails == null
                         ? 0
                         : polymorphicTails.stream()
                                           .mapToInt(AbstractVarProjection::polymorphicDepth)
                                           .max()
                                           .orElse(0);
    return polymorphicDepth;
  }

  @NotNull
  public TextLocation location() {
    return location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractVarProjection that = (AbstractVarProjection) o;
    return Objects.equals(type, that.type) &&
           Objects.equals(tagProjections, that.tagProjections) &&
           Objects.equals(polymorphicTails, that.polymorphicTails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, tagProjections, polymorphicTails);
  }
}
