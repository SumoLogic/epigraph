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

/* Created by yegor on 7/22/16. */

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.data.Datum;
import ws.epigraph.names.TypeName;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class DatumTypeImpl extends TypeImpl implements DatumType {

  private final @NotNull Tag self = new Tag(MONO_TAG_NAME, this, Annotations.EMPTY); // TODO rename to tag?

  private final @NotNull Collection<@NotNull ? extends Tag> immediateTags = Collections.singleton(self);

  private final @Nullable DatumType declaredMetaType;

  private final @Nullable DatumType metaType;

  protected DatumTypeImpl(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends DatumType> immediateSupertypes,
      @Nullable DatumType declaredMetaType,
      @NotNull Annotations annotations
  ) {
    super(name, immediateSupertypes, annotations);
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
  public @NotNull List<@NotNull ? extends DatumType> supertypes() {
    return (List<? extends DatumType>) super.supertypes();
  }

  @Override
  public @NotNull Tag self() { return self; }

  @Override
  public @NotNull Collection<@NotNull ? extends Tag> immediateTags() { return immediateTags; }

  /** @see Class#isInstance(Object) */
  @Override
  public boolean isInstance(@Nullable Datum datum) { return datum != null && isAssignableFrom(datum.type()); }

  @Override
  public <D extends Datum> D checkAssignable(@NotNull D datum) throws IllegalArgumentException { // TODO accept nulls?
    if (!isInstance(datum))
      throw new IllegalArgumentException(
          String.format("Type '%s' is not an instance of type '%s'", datum.type().name(), name())
      );
    return datum;
  }

  @Override
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

  @Override
  public @Nullable DatumType declaredMetaType() { return declaredMetaType; }

  @Override
  public @Nullable DatumType metaType() { return metaType; }

  // generalize to something like `findMinimumType`?
  private static @Nullable DatumType calculateMetaType(
      @NotNull String typeName,
      @Nullable DatumType declaredMetaType,
      @NotNull List<? extends DatumType> immediateSupertypes
  ) {

    @Nullable DatumType minimalSuperMeta = null;
    for (final DatumType supertype : immediateSupertypes) {
      @Nullable DatumType superMeta = supertype.metaType();
      if (minimalSuperMeta == null)
        minimalSuperMeta = superMeta;
      else if (superMeta != null) {
        if (minimalSuperMeta.isAssignableFrom(superMeta))
          minimalSuperMeta = superMeta;
        else if (!superMeta.isAssignableFrom(minimalSuperMeta))
          throw new IllegalArgumentException(
              "Incompatible (inherited) meta types on '" + typeName + "'"); // todo better explanation
      }
    }

    if (declaredMetaType == null) return minimalSuperMeta;
    if (minimalSuperMeta == null) return declaredMetaType;

    if (minimalSuperMeta.isAssignableFrom(declaredMetaType)) return declaredMetaType;
    if (declaredMetaType.isAssignableFrom(minimalSuperMeta)) return minimalSuperMeta;

    throw new IllegalArgumentException("Incompatible meta types on '" + typeName + "'"); // todo better explanation
  }

}
