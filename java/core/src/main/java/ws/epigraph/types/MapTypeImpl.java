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

/* Created by yegor on 9/20/16. */

package ws.epigraph.types;

import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

abstract class MapTypeImpl extends DatumTypeImpl implements MapType {

  public final @NotNull DatumType keyType;
  public final @NotNull DataType valueType;

  protected MapTypeImpl(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends MapType> immediateSupertypes,
      @NotNull DatumType keyType,
      @NotNull DataType valueType,
      @Nullable DatumType declaredMetaType,
      @NotNull Annotations annotations
  ) {
    super(name, immediateSupertypes, declaredMetaType, annotations);
    this.keyType = keyType;
    this.valueType = valueType;
    if (keyType.metaType() != null) throw new IllegalArgumentException(
        String.format(
            "Map type '%s' key type '%s' should not have a meta-type",
            name, keyType.name()
        )
    );
  }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.MAP; }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends MapType> immediateSupertypes() {
    return (List<? extends MapType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends MapType> supertypes() {
    return (List<? extends MapType>) super.supertypes();
  }

  @Override
  public @NotNull DatumType keyType() { return keyType; }

  @Override
  public @NotNull DataType valueType() { return valueType; }

}
