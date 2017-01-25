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

/* Created by yegor on 7/22/16. */

package ws.epigraph.types;

import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class DatumType extends Type implements DatumTypeApi {

  public final @NotNull Tag self = new Tag(MONO_TAG_NAME, this); // TODO rename to tag?

  private final @NotNull Collection<@NotNull ? extends Tag> immediateTags = Collections.singleton(self);

  private final @Nullable DatumType declaredMetaType;

  private final @Nullable DatumType metaType;

  protected DatumType(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends DatumType> immediateSupertypes,
      @Nullable DatumType declaredMetaType
  ) {
    super(name, immediateSupertypes);
    this.declaredMetaType = declaredMetaType;
    metaType = calculateMetaType(name.toString(), declaredMetaType, immediateSupertypes);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends DatumType> immediateSupertypes() {
    return (List<? extends DatumType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends DatumType> supertypes() {
    return (Collection<? extends DatumType>) super.supertypes();
  }

  @Override
  public @NotNull TagApi self() { return self; }

  @Override
  public @NotNull Collection<@NotNull ? extends Tag> immediateTags() { return immediateTags; }

  /** @see Class#isInstance(Object) */
  public boolean isInstance(@Nullable Datum datum) { return datum != null && isAssignableFrom(datum.type()); }

  public <D extends Datum> D checkAssignable(@NotNull D datum) throws IllegalArgumentException { // TODO accept nulls?
    if (!isInstance(datum))
      throw new IllegalArgumentException(
          String.format("Type '%s' is not an instance of type '%s'", datum.type().name(), name())
      );
    return datum;
  }

  public <D extends Datum> D checkMeta(@Nullable D meta) throws IllegalArgumentException {
    if (meta == null) return null;
    else {
      final DatumType _metaType = metaType();
      if (_metaType == null) throw new IllegalArgumentException(String.format("Type '%s' has no meta-type", name()));
      return _metaType.checkAssignable(meta);
    }
  }

  @Override
  public @NotNull DataType dataType() { return new DataType(this, self); } // TODO cache

  public @Nullable DatumType declaredMetaType() { return declaredMetaType; }

  @Override
  public @Nullable DatumType metaType() { return metaType; }

  public abstract @NotNull Val.Imm createValue(@Nullable ErrorValue errorOrNull);

  // generalize to something like `findMinimumType`?
  private static @Nullable DatumType calculateMetaType(
      @NotNull String typeName,
      @Nullable DatumType declaredMetaType,
      @NotNull List<? extends DatumType> immediateSupertypes) {

    @Nullable DatumType minimalSuperMeta = null;
    for (final DatumType supertype : immediateSupertypes) {
      @Nullable DatumType superMeta = supertype.metaType;
      if (minimalSuperMeta == null)
        minimalSuperMeta = superMeta;
      else if (superMeta != null) {
        if (minimalSuperMeta.isAssignableFrom(superMeta))
          minimalSuperMeta = superMeta;
        else if (!superMeta.isAssignableFrom(minimalSuperMeta))
          throw new IllegalArgumentException("Incompatible (inherited) meta types on '" + typeName + "'"); // todo better explanation
      }
    }

    if (declaredMetaType == null) return minimalSuperMeta;
    if (minimalSuperMeta == null) return declaredMetaType;

    if (minimalSuperMeta.isAssignableFrom(declaredMetaType)) return declaredMetaType;
    if (declaredMetaType.isAssignableFrom(minimalSuperMeta)) return minimalSuperMeta;

    throw new IllegalArgumentException("Incompatible meta types on '" + typeName + "'"); // todo better explanation
  }


  public interface Raw extends Type.Raw {

    @NotNull Val.Imm.Raw createValue(@Nullable ErrorValue errorOrNull);

  }


  public interface Static<
      MyImmDatum extends Datum.Imm.Static,
      MyDatumBuilder extends Datum.Builder.Static<MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyDatumBuilder>,
      MyImmData extends Data.Imm.Static,
      MyDataBuilder extends Data.Builder.Static<MyImmData>
      > extends Type.Static<MyImmData, MyDataBuilder> {

    @Override
    @NotNull MyDataBuilder createDataBuilder();

    @NotNull MyImmVal createValue(@Nullable ErrorValue errorOrNull);

  }


}
