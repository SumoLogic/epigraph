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
import ws.epigraph.names.QualifiedTypeName;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface RecordTypeApi extends DatumTypeApi {
  @Override
  @NotNull QualifiedTypeName name();

  @NotNull Collection<@NotNull ? extends FieldApi> immediateFields(); // rename to `declaredFields` ?

  @NotNull Collection<@NotNull ? extends FieldApi> fields();

  @NotNull Map<@NotNull String, @NotNull ? extends FieldApi> fieldsMap();

//  @Override
//  default @NotNull TypeKind kind() { return TypeKind.RECORD; }

  @Override
  @NotNull List<@NotNull ? extends RecordTypeApi> supertypes();
}
