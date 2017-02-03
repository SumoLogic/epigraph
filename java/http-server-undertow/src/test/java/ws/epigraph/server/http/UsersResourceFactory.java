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
import ws.epigraph.projections.req.delete.ReqDeleteFieldProjection;
import ws.epigraph.projections.req.delete.ReqDeleteKeyProjection;
import ws.epigraph.projections.req.delete.ReqDeleteMapModelProjection;
import ws.epigraph.projections.req.delete.ReqDeleteTagProjectionEntry;
import ws.epigraph.projections.req.input.ReqInputFieldProjection;
import ws.epigraph.projections.req.input.ReqInputRecordModelProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.projections.req.path.ReqMapModelPath;
import ws.epigraph.schema.operations.*;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.*;
import ws.epigraph.tests.*;
import ws.epigraph.tests.resources.users.operations.read.output.ReqOutputPersonMapKeyProjection;
import ws.epigraph.tests.resources.users.operations.read.output.ReqOutputPersonMapProjection;
import ws.epigraph.tests.resources.users.operations.read.output.meta.ReqOutputPaginationInfoProjection;
import ws.epigraph.types.DatumType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UsersResourceFactory extends AbstractUsersResourceFactory {
  private final UsersStorage storage;

  public UsersResourceFactory(@NotNull UsersStorage storage) {
    this.storage = storage;
  }

  @Override
  protected @NotNull ReadOperation<PersonMap.Data> constructReadOperation(final @NotNull ReadOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new ReadOp(operationDeclaration, storage);
  }

  @Override
  protected @NotNull CreateOperation<PersonId_List.Data> constructCreateOperation(final @NotNull CreateOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new CreateOp(operationDeclaration, storage);
  }

  @Override
  protected @NotNull UpdateOperation<PersonId_Error_Map.Data> constructUpdateOperation(final @NotNull UpdateOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new UpdateOp(operationDeclaration, storage);
  }

  @Override
  protected @NotNull DeleteOperation<PersonId_Error_Map.Data> constructDeleteOperation(final @NotNull DeleteOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new DeleteOp(operationDeclaration, storage);
  }

  @Override
  protected @NotNull CustomOperation<PersonRecord.Data> constructCapitalizeCustomOperation(final @NotNull CustomOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new CapitalizeOp(operationDeclaration, storage);
  }

// todo create/update/delete should only take :id model for bestFriend/worstEnemy/friends*
  // read should use correctly construct them my making separate calls to the 'backend'
  // it's probably better to implement this once we have generated projection classes

  private static final class ReadOp extends ReadOperation<PersonMap.Data> {
    private final @NotNull UsersStorage storage;

    ReadOp(@NotNull ReadOperationDeclaration declaration, @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonMap.Data>>
    process(@NotNull ReadOperationRequest request) {

      // todo replace when generated projections are ready
      final ReqOutputFieldProjection fieldProjection = request.outputProjection();
      ws.epigraph.tests.resources.users.operations.read.output.ReqOutputUsersFieldProjection typeSafeFieldProjection =
          new ws.epigraph.tests.resources.users.operations.read.output.ReqOutputUsersFieldProjection(fieldProjection);

      // todo must be model, not field params
      Long start = typeSafeFieldProjection.getStartParameter();
      Long count = typeSafeFieldProjection.getCountParameter();

      final ReqOutputPersonMapProjection mapProjection = typeSafeFieldProjection.dataProjection();
      final ReqOutputPaginationInfoProjection metaProjection = mapProjection.meta();

      final List<ReqOutputPersonMapKeyProjection> keys = mapProjection.keys();
      if (keys != null) {
        System.out.println("Requested keys: " +
                           keys.stream().map(k -> k.value().toString()).collect(Collectors.joining(", "))
        );
      }

      final PersonMap.Builder users = storage.users();
      if (metaProjection != null) {
        final PaginationInfo.Builder paginationInfoBuilder = PaginationInfo.type.createBuilder();
        if (start != null && metaProjection.start() != null)
          paginationInfoBuilder.setStart(epigraph.Long.create(start));
        if (count != null && metaProjection.count() != null)
          paginationInfoBuilder.setCount(epigraph.Long.create(count));
        users.setMeta(paginationInfoBuilder);
      }

      return CompletableFuture.completedFuture(new ReadOperationResponse<>(
          PersonMap.type.createDataBuilder().set(users)
      ));
    }

  }

  private static final class CreateOp extends CreateOperation<PersonId_List.Data> {
    private final @NotNull UsersStorage storage;

    protected CreateOp(final CreateOperationDeclaration declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_List.Data>>
    process(final @NotNull CreateOperationRequest request) {
      // todo operation stubs must be generated
      final PersonRecord_List inputList =
          (PersonRecord_List) request.data()._raw().getDatum(PersonRecord_List.type.self);

      final PersonId_List.Builder.Data resultListDataBuilder = PersonId_List.type.createDataBuilder();

      if (inputList == null) {
        resultListDataBuilder.set_Error(new ErrorValue(400, "Input data not specified"));
      } else {
        final PersonId_List.Builder resultListBuilder = PersonId_List.create();
        resultListDataBuilder.set(resultListBuilder);

        for (final PersonRecord record : inputList.datums()) {
          // we know it's a builder by implementation. Todo: add `toBuilder`!
          final PersonRecord.Builder builder = (PersonRecord.Builder) record;
          final PersonId id = storage.insertPerson(builder);
          resultListBuilder.add(id);
        }
      }

      return CompletableFuture.completedFuture(new ReadOperationResponse<>(resultListDataBuilder));
    }
  }

  private static class UpdateOp extends UpdateOperation<PersonId_Error_Map.Data> {
    private final @NotNull UsersStorage storage;

    protected UpdateOp(final UpdateOperationDeclaration declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Error_Map.Data>>
    process(final @NotNull UpdateOperationRequest request) {
      final PersonId_Person_Map inputMap =
          (PersonId_Person_Map) request.data()._raw().getDatum(PersonId_Person_Map.type.self);

      final PersonId_Error_Map.Builder.Data resultMapDataBuilder = PersonId_Error_Map.type.createDataBuilder();

      if (inputMap == null) {
        resultMapDataBuilder.set_Error(new ErrorValue(400, "Input data not specified"));
      } else {
        final PersonId_Error_Map.Builder resultMapBuilder = PersonId_Error_Map.create();
        resultMapDataBuilder.set(resultMapBuilder);

        for (final Map.Entry<? extends PersonId.Imm, ? extends Person> entry : inputMap.datas().entrySet()) {
          // do we want to treat puts for non-existent keys as creates or as errors?
          // treating as creates for now

          final Person.Builder currentPerson =
              (Person.Builder) storage.users().datas().get(entry.getKey()); // todo toBuilder
          final Person personUpdate = entry.getValue();

          storage.users().put$(entry.getKey(), update(currentPerson, personUpdate));
        }
      }

      return CompletableFuture.completedFuture(new ReadOperationResponse<>(resultMapDataBuilder));
    }

    private Person update(@Nullable Person.Builder current, @NotNull Person update) {
      if (current == null) return update;

      final PersonRecord.Builder currentRecord = (PersonRecord.Builder) current.getRecord(); // toBuilder
      if (currentRecord == null) return update; // or fail?

      final PersonRecord updateRecord = update.getRecord();
      if (updateRecord == null) return current;

      if (updateRecord.getFirstName_() != null)
        currentRecord.setFirstName(updateRecord.getFirstName());

      if (updateRecord.getLastName_() != null)
        currentRecord.setLastName(updateRecord.getLastName());

      return current;
    }
  }

  private static final class DeleteOp extends DeleteOperation<PersonId_Error_Map.Data> {
    private final @NotNull UsersStorage storage;

    DeleteOp(final DeleteOperationDeclaration declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Error_Map.Data>>
    process(final @NotNull DeleteOperationRequest request) {
      final PersonId_Error_Map.Builder.Data resultBuilder = PersonId_Error_Map.type.createDataBuilder();

      final @NotNull ReqDeleteFieldProjection fieldProjection = request.deleteProjection();
      final ReqDeleteTagProjectionEntry tpe = fieldProjection.varProjection().tagProjection(DatumType.MONO_TAG_NAME);

      if (tpe == null) {
        resultBuilder.set_Error(new ErrorValue(400, "keys not specified")); // can never happen?
      } else {
        final PersonId_Error_Map.Builder resultMapBuilder = PersonId_Error_Map.create();
        resultBuilder.set(resultMapBuilder);

        final @NotNull ReqDeleteMapModelProjection mmp = (ReqDeleteMapModelProjection) tpe.projection();
        final List<ReqDeleteKeyProjection> keys = mmp.keys();
        assert keys != null; // guaranteed by op projection

        for (final ReqDeleteKeyProjection key : keys) {
          PersonId.Imm keyValue = (PersonId.Imm) key.value().toImmutable();
          final @Nullable Person removedPerson = storage.users().datas().remove(keyValue);
          //noinspection ConstantConditions why?
          if (removedPerson == null)
            resultMapBuilder.put(
                keyValue,
                Error.create()
                    .setCode(epigraph.Integer.create(404))
                    .setMessage(epigraph.String.create("Item with id " + keyValue.getVal() + " doesn't exist"))
            );

        }
      }

      return CompletableFuture.completedFuture(new ReadOperationResponse<>(resultBuilder));
    }
  }

  private static final class CapitalizeOp extends CustomOperation<PersonRecord.Data> {
    private final @NotNull UsersStorage storage;

    protected CapitalizeOp(final CustomOperationDeclaration declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonRecord.Data>>
    process(final @NotNull CustomOperationRequest request) {
      PersonRecord.Builder.Data resultBuilder = PersonRecord.type.createDataBuilder();

      // todo: use generated projections
      // todo: generated server stubs should make 'key' directly accessible
      @SuppressWarnings("ConstantConditions")
      ReqMapModelPath mapPath =
          (ReqMapModelPath) request.path().varProjection().tagProjection(DatumType.MONO_TAG_NAME).projection();
      PersonId key = (PersonId) mapPath.key().value();

      final Person.Builder person = (Person.Builder) storage.users().datas().get(key.toImmutable());
      if (person == null) {
        resultBuilder.set_Error(new ErrorValue(404, "Person with id " + key.getVal() + " not found"));
      } else {
        PersonRecord.Builder personRecord = (PersonRecord.Builder) person.getRecord();
        if (personRecord != null) {
          final ReqInputFieldProjection inputFieldProjection = request.inputProjection();
          if (inputFieldProjection != null) {
            ReqInputRecordModelProjection inputProjection = (ReqInputRecordModelProjection) inputFieldProjection
                .varProjection()
                .tagProjection(DatumType.MONO_TAG_NAME)
                .projection();

            if (inputProjection.fieldProjection(PersonRecord.firstName.name()) != null) {
              final epigraph.String firstName = personRecord.getFirstName();
              if (firstName != null) {
                personRecord.setFirstName(epigraph.String.create(firstName.getVal().toUpperCase()));
              }
            }

            if (inputProjection.fieldProjection(PersonRecord.lastName.name()) != null) {
              final epigraph.String lastName = personRecord.getFirstName();
              if (lastName != null) {
                personRecord.setFirstName(epigraph.String.create(lastName.getVal().toUpperCase()));
              }
            }

            resultBuilder.set(personRecord);
          }
        }
      }


      return CompletableFuture.completedFuture(new ReadOperationResponse<>(resultBuilder));
    }
  }

}
