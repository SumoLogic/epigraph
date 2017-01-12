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

package ws.epigraph.types;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface ListTypeApi extends DatumTypeApi {
  @NotNull DataTypeApi elementType();

  @Override
  @NotNull Collection<@NotNull ? extends ListTypeApi> supertypes();
//
//  @Override
//  default @NotNull TypeKind kind() { return TypeKind.LIST; }
}