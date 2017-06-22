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
import ws.epigraph.names.TypeName;

import java.util.List;

abstract class ListTypeImpl extends DatumTypeImpl implements ListType {

  public final @NotNull DataType elementType; // TODO rename to elementDataType

  protected ListTypeImpl(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends ListType> immediateSupertypes,
      @NotNull DataType elementDataType,
      @Nullable DatumType declaredMetaType,
      @NotNull Annotations annotations
  ) {
    super(name, immediateSupertypes, declaredMetaType, annotations);
    this.elementType = elementDataType;
  }

//  protected ListType(@NotNull AnonListTypeName name, @NotNull DataType elementDataType) {
//    this(
//        name,
//        elementDataType.type.immediateSupertypes().stream().map(Type::listOf).collect(Collectors.toList()),
//        elementDataType
//    );
//  }
//
//  protected ListType(
//      @NotNull QualifiedTypeName name,
//      @NotNull List<@NotNull NamedListType> immediateNamedSupertypes,
//      @NotNull DataType elementDataType
//  ) {
//    this(name, addAnonSupertypes(immediateNamedSupertypes, elementDataType.type), elementDataType);
//  }
//
//  private static @NotNull List<@NotNull ListType> addAnonSupertypes(
//      @NotNull List<@NotNull ? extends NamedListType> namedSupertypes,
//      @NotNull Type elementType
//  ) {
//    Stream<? extends Type> missingElementSupertypes = elementType.immediateSupertypes().stream().filter(est ->
//        namedSupertypes.stream().anyMatch(nst -> !nst.elementType.type.doesExtend(est))
//    );
//    return Stream.concat(
//        namedSupertypes.stream(),
//        missingElementSupertypes.map(Type::listOf)
//    ).collect(Collectors.toList());
//  }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.LIST; }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends ListType> immediateSupertypes() {
    return (List<? extends ListType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends ListType> supertypes() {
    return (List<? extends ListType>) super.supertypes();
  }

  @Override
  public @NotNull DataType elementType() { return elementType; }

}
