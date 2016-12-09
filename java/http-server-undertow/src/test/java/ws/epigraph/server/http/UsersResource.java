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

import epigraph.Error;
import epigraph.PersonId_Error_Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.idl.operations.CreateOperationIdl;
import ws.epigraph.idl.operations.DeleteOperationIdl;
import ws.epigraph.idl.operations.ReadOperationIdl;
import ws.epigraph.projections.req.delete.ReqDeleteFieldProjection;
import ws.epigraph.projections.req.delete.ReqDeleteKeyProjection;
import ws.epigraph.projections.req.delete.ReqDeleteMapModelProjection;
import ws.epigraph.projections.req.delete.ReqDeleteTagProjectionEntry;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.*;
import ws.epigraph.tests.*;
import ws.epigraph.types.DatumType;

import java.util.Collections;
import java.util.List;
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
        Collections.singletonList(
            new DeleteOp(((DeleteOperationIdl) resourceIdl.operations().get(2)), storage)
        ),
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
      return CompletableFuture.completedFuture(new ReadOperationResponse<>(
          PersonId_Person_Map.type.createDataBuilder().set(storage.users())
      ));
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

      return CompletableFuture.completedFuture(
          new ReadOperationResponse<>(PersonId_List.type.createDataBuilder().set(result))
      );
    }
  }

  // todo figure out return type
  private static final class DeleteOp extends DeleteOperation<PersonId_Error_Map.Data> {
    private final @NotNull UsersStorage storage;

    protected DeleteOp(final DeleteOperationIdl declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Error_Map.Data>>
    process(final @NotNull DeleteOperationRequest request) {
      final PersonId_Error_Map.Builder.Data builder = PersonId_Error_Map.type.createDataBuilder();

      final @NotNull ReqDeleteFieldProjection fieldProjection = request.deleteProjection();
      final ReqDeleteTagProjectionEntry tpe = fieldProjection.varProjection().tagProjection(DatumType.MONO_TAG_NAME);

      if (tpe == null) {
        builder.set_Error(new ErrorValue(400, "keys not specified")); // can never happen?
      } else {
        final PersonId_Error_Map.Builder mapBuilder = PersonId_Error_Map.create();
        builder.set(mapBuilder);

        final @NotNull ReqDeleteMapModelProjection mmp = (ReqDeleteMapModelProjection) tpe.projection();
        final List<ReqDeleteKeyProjection> keys = mmp.keys();
        assert keys != null; // guaranteed by op projection

        for (final ReqDeleteKeyProjection key : keys) {
          PersonId.Imm keyValue = (PersonId.Imm) key.value().toImmutable();
          final @Nullable Person removedPerson = storage.users().datas().remove(keyValue);
          //noinspection ConstantConditions why?
          if (removedPerson == null)
            mapBuilder.put(
                keyValue,
                Error.create()
                    .setCode(epigraph.Integer.create(404))
                    .setMessage(epigraph.String.create("Item with id " + keyValue.getVal() + " doesn't exist"))
            );

        }
      }

      return CompletableFuture.completedFuture(new ReadOperationResponse<>(builder));
    }
  }

}
