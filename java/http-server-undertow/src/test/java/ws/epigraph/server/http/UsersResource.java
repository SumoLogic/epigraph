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

/* Created by yegor on 10/27/16. */

package ws.epigraph.server.http;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.idl.operations.CreateOperationIdl;
import ws.epigraph.idl.operations.ReadOperationIdl;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.*;
import ws.epigraph.tests.*;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class UsersResource extends Resource {
  public UsersResource(@NotNull ResourceIdl resourceIdl, @NotNull UsersStorage storage)
      throws ServiceInitializationException {

    super(
        resourceIdl,
        Collections.singletonList(
            new ReadOp(((ReadOperationIdl) resourceIdl.operations().get(0)), storage)
        ),
        Collections.singletonList(
            new CreateOp(((CreateOperationIdl) resourceIdl.operations().get(1)), storage)
        ),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList()
    );

  }


  private static final class ReadOp extends ReadOperation<PersonId_Person_Map.Data> {
    private final @NotNull UsersStorage storage;

    ReadOp(@NotNull ReadOperationIdl declaration, @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>>
    process(@NotNull ReadOperationRequest request) {
      return toFuture(
          new ReadOperationResponse<>(
              PersonId_Person_Map.type.createDataBuilder().set(storage.users())
          )
      );
    }
  }

  private static final class CreateOp extends CreateOperation<PersonId_List.Data> {
    private final @NotNull UsersStorage storage;

    protected CreateOp(final CreateOperationIdl declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_List.Data>>
    process(final @NotNull CreateOperationRequest request) {
      // todo operation stubs must be generated
      final PersonRecord_List recordList =
          (PersonRecord_List) request.data()._raw().getDatum(PersonRecord_List.type.self);

      final PersonId_List.Builder result = PersonId_List.create();

      if (recordList != null) {
        for (final PersonRecord record : recordList.datums()) {
          // we know it's a builder by implementation. Todo: add `toBuilder`!
          final PersonRecord.Builder builder = (PersonRecord.Builder) record;
          final PersonId id = storage.insertPerson(builder);
          result.add(id);
        }
      }

      return toFuture(new ReadOperationResponse<>(PersonId_List.type.createDataBuilder().set(result)));
    }
  }

  private static @NotNull <T> CompletableFuture<T> toFuture(@NotNull T value) {
    CompletableFuture<T> f = new CompletableFuture<>();
    f.complete(value);
    return f;
  }

}
