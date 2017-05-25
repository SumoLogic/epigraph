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

/* Created by yegor on 10/27/16. */

package ws.epigraph.tests;

import epigraph.Error;
import epigraph.PersonId_Error_Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.schema.operations.*;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.*;
import ws.epigraph.tests.resources.users.AbstractUsersResourceFactory;
import ws.epigraph.tests.resources.users.operations.create._default.AbstractCreateOperation;
import ws.epigraph.tests.resources.users.operations.create._default.input.InputUsersFieldProjection;
import ws.epigraph.tests.resources.users.operations.delete._default.AbstractDeleteOperation;
import ws.epigraph.tests.resources.users.operations.delete._default.delete.DeletePersonMapKeyProjection;
import ws.epigraph.tests.resources.users.operations.delete._default.delete.DeleteUsersFieldProjection;
import ws.epigraph.tests.resources.users.operations.read._default.AbstractReadOperation;
import ws.epigraph.tests.resources.users.operations.read._default.output.OutputPersonMapKeyProjection;
import ws.epigraph.tests.resources.users.operations.read._default.output.OutputPersonMapProjection;
import ws.epigraph.tests.resources.users.operations.read._default.output.OutputUsersFieldProjection;
import ws.epigraph.tests.resources.users.operations.read._default.output.meta.OutputPaginationInfoProjection;
import ws.epigraph.tests.resources.users.operations.update._default.AbstractUpdateOperation;
import ws.epigraph.tests.resources.users.operations.update._default.update.UpdateUsersFieldProjection;
import ws.epigraph.tests.resources.users.operations.update._default.update.elements.UpdatePersonProjection;
import ws.epigraph.tests.resources.users.operations.update._default.update.elements.record.UpdatePersonRecordProjection;
import ws.epigraph.tests.resources.users.operations.custom.capitalize.AbstractCustomCapitalizeOperation;
import ws.epigraph.tests.resources.users.operations.custom.capitalize.input.InputPersonRecordProjection;
import ws.epigraph.tests.resources.users.operations.custom.capitalize.path.UsersFieldPath;
import ws.epigraph.tests.resources.users.operations.delete.recursivetest.AbstractDeleteRecursiveTestOperation;
import ws.epigraph.util.HttpStatusCode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UsersResourceFactory extends AbstractUsersResourceFactory {
  public static final Logger LOG = LoggerFactory.getLogger(UsersResourceFactory.class);

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
  protected @NotNull ReadOperation<Person> constructBestFriendReadOperation(final @NotNull ReadOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new ReadBestFriendOperation(operationDeclaration, storage);
  }

  @Override
  protected @NotNull CreateOperation<PersonId_List.Data> constructCreateOperation(final @NotNull CreateOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new CreateOp(operationDeclaration, storage);
  }

  @Override
  protected @NotNull CreateOperation<Person_List.Data> constructFriendsCreateOperation(final @NotNull CreateOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new CreateFriendsOperation(operationDeclaration, storage);
  }

  @Override
  protected @NotNull UpdateOperation<PersonId_Error_Map.Data> constructUpdateOperation(final @NotNull UpdateOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new UpdateOp(operationDeclaration, storage);
  }

  @Override
  protected @NotNull UpdateOperation<Error.Data> constructBestFriendUpdateOperation(final @NotNull UpdateOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new UpdateBestFriendOperation(operationDeclaration, storage);
  }

  @Override
  protected @NotNull DeleteOperation<PersonId_Error_Map.Data> constructDeleteOperation(final @NotNull DeleteOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new DeleteOp(operationDeclaration, storage);
  }

  @Override
  protected @NotNull DeleteOperation<Error.Data> constructBestFriendDeleteOperation(final @NotNull DeleteOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new DeleteBestFriendOperation(operationDeclaration, storage);
  }

  @Override
  protected @NotNull DeleteOperation<PersonId_Error_Map.Data> constructRecursiveTestDeleteOperation(final @NotNull DeleteOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new AbstractDeleteRecursiveTestOperation(operationDeclaration) {
      @Override
      protected @NotNull CompletableFuture<PersonId_Error_Map.Data> process(
          final @NotNull PersonId_Error_Map.Data.Builder builder,
          final @NotNull ws.epigraph.tests.resources.users.operations.delete.recursivetest.delete.DeleteUsersFieldProjection deleteProjection,
          final @NotNull ws.epigraph.tests.resources.users.operations.delete.recursivetest.output.OutputUsersFieldProjection outputProjection) {
        throw new RuntimeException("Unimplemented");
      }
    };
  }

  @Override
  protected @NotNull CustomOperation<PersonMap.Data> constructEchoCustomOperation(final @NotNull CustomOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new EchoOperation(operationDeclaration);
  }

  @Override
  protected @NotNull CustomOperation<PersonRecord.Data> constructCapitalizeCustomOperation(final @NotNull CustomOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new CapitalizeOp(operationDeclaration, storage);
  }

// todo create/update/delete should only take :id model for bestFriend/worstEnemy/friends*
  // read should use correctly construct them my making separate calls to the 'backend'
  // it's probably better to implement this once we have generated projection classes

  private static final class ReadOp extends AbstractReadOperation {
    private final @NotNull UsersStorage storage;

    ReadOp(@NotNull ReadOperationDeclaration declaration, @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    protected @NotNull CompletableFuture<PersonMap.Data> process(
        @NotNull PersonMap.Data.Builder builder,
        @NotNull OutputUsersFieldProjection projection) {

      final OutputPersonMapProjection mapProjection = projection.dataProjection();

      Long start = mapProjection.getStartParameter();
      Long count = mapProjection.getCountParameter();

      final OutputPaginationInfoProjection metaProjection = mapProjection.meta();

      // keys check
      final List<OutputPersonMapKeyProjection> keys = mapProjection.keys();
      if (keys != null) {
        LOG.debug("Requested keys: " +
                  keys.stream().map(k -> k.value().getVal().toString()).collect(Collectors.joining(", "))
        );
      }

      // poly tails check
      final @Nullable ws.epigraph.tests.resources.users.operations.read._default.output.elements._normalized.user.record.OutputUserRecordProjection
          userRecordProjection = mapProjection.itemsProjection().normalizedFor_User().record();
      if (userRecordProjection != null) userRecordProjection.worstEnemy();

      final PersonMap.Builder users = storage.users();
      if (metaProjection != null) {
        final PaginationInfo.Builder paginationInfoBuilder = PaginationInfo.type.createBuilder();
        if (start != null && metaProjection.start() != null)
          paginationInfoBuilder.setStart(start);
        if (count != null && metaProjection.count() != null)
          paginationInfoBuilder.setCount(count);
        users.setMeta(paginationInfoBuilder);
      }

      return CompletableFuture.completedFuture(builder.set(users));
    }

  }

  private static final class CreateOp extends AbstractCreateOperation {
    private final @NotNull UsersStorage storage;

    protected CreateOp(final CreateOperationDeclaration declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    protected @NotNull CompletableFuture<PersonId_List.Data> process(
        @NotNull PersonId_List.Data.Builder builder,
        @NotNull PersonRecord_List inputList,
        @Nullable InputUsersFieldProjection inputProjection,
        @NotNull ws.epigraph.tests.resources.users.operations.create._default.output.OutputUsersFieldProjection outputProjection) {

      final PersonId_List.Builder resultListBuilder = PersonId_List.create();

      for (final PersonRecord record : inputList.datums()) {
        // we know it's a builder by implementation. Todo: add `toBuilder`!
        final PersonRecord.Builder recordBuilder = (PersonRecord.Builder) record;
        final PersonId id = storage.insertPerson(recordBuilder);
        resultListBuilder.add(id);
      }

      return CompletableFuture.completedFuture(builder.set(resultListBuilder));
    }

  }

  private static class UpdateOp extends AbstractUpdateOperation {
    private final @NotNull UsersStorage storage;

    protected UpdateOp(final UpdateOperationDeclaration declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    protected @NotNull CompletableFuture<PersonId_Error_Map.Data> process(
        @NotNull PersonId_Error_Map.Data.Builder builder,
        @NotNull PersonMap inputMap,
        @Nullable UpdateUsersFieldProjection updateProjection,
        @NotNull ws.epigraph.tests.resources.users.operations.update._default.output.OutputUsersFieldProjection outputProjection) {

      final PersonId_Error_Map.Builder resultMapBuilder = PersonId_Error_Map.create();

      final @Nullable UpdatePersonProjection updatePersonProjection =
          updateProjection == null ? null :
          updateProjection.dataProjection().itemsProjection();

      for (final Map.Entry<? extends PersonId.Imm, ? extends Person> entry : inputMap.datas().entrySet()) {
        // do we want to treat puts for non-existent keys as creates or as errors?
        // treating as creates for now

        Person currentPerson = storage.users().datas().get(entry.getKey());

        //noinspection ConstantConditions
        if (currentPerson == null) {

          resultMapBuilder.put(entry.getKey(), Error.create()
              .setCode(HttpStatusCode.NOT_FOUND)
              .setMessage("User with id " + entry.getKey().getVal() + " not found"));

        } else {

          if (currentPerson instanceof Person.Builder) {
            final Person.Builder currentPersonBuilder = (Person.Builder) currentPerson; // todo fragile, toBuilder

            final Person personUpdate = entry.getValue();

            final @Nullable Person update = update(currentPersonBuilder, personUpdate, updatePersonProjection);
            if (update == null)
              storage.users().datas().remove(entry.getKey());
            else
              storage.users().put$(entry.getKey(), update);

          } else {
            final User.Builder currentUserBuilder = (User.Builder) currentPerson; // todo fragile, toBuilder

            final Person personUpdate = entry.getValue();

            final @Nullable Person update = updateUser(currentUserBuilder, personUpdate, updatePersonProjection);
            if (update == null)
              storage.users().datas().remove(entry.getKey());
            else
              storage.users().put$(entry.getKey(), update);
          }
        }
      }

      return CompletableFuture.completedFuture(builder.set(resultMapBuilder));
    }

    private @Nullable Person update(
        @Nullable Person.Builder current,
        @NotNull Person update,
        @Nullable UpdatePersonProjection personProjection) {
      if (current == null) return update;

      final PersonRecord.Builder currentRecord = (PersonRecord.Builder) current.getRecord(); // toBuilder
      if (currentRecord == null) return update; // or fail?

      final PersonRecord updateRecord = update.getRecord();
      if (updateRecord == null) return current;

      if (personProjection == null) { // projection not specified = data driven
        if (updateRecord.getFirstName_() != null)
          currentRecord.setFirstName(updateRecord.getFirstName());

        if (updateRecord.getLastName_() != null)
          currentRecord.setLastName(updateRecord.getLastName());

      } else { // projection driven
        final UpdatePersonRecordProjection recordProjection = personProjection.record();
        if (recordProjection == null) return null; // projection for record not specified.. remove element?

        if (recordProjection.firstName() != null)
          currentRecord.setFirstName(updateRecord.getFirstName());

        if (recordProjection.lastName() != null)
          currentRecord.setLastName(updateRecord.getLastName());
      }

      return current;
    }

    private @Nullable Person updateUser(
        @Nullable User.Builder current,
        Person update,
        @Nullable UpdatePersonProjection userProjection) {
      if (current == null) return update;

      final UserRecord.Builder currentRecord = (UserRecord.Builder) current.getRecord(); // toBuilder
      if (currentRecord == null) return update; // or fail?

      final PersonRecord updateRecord = update.getRecord();
      if (updateRecord == null) return current;

      if (userProjection == null) { // projection not specified = data driven
        if (updateRecord.getFirstName_() != null)
          currentRecord.setFirstName(updateRecord.getFirstName());

        if (updateRecord.getLastName_() != null)
          currentRecord.setLastName(updateRecord.getLastName());

      } else { // projection driven
        final @Nullable UpdatePersonRecordProjection recordProjection = userProjection.record();
        if (recordProjection == null) return null; // projection for record not specified.. remove element?

        if (recordProjection.firstName() != null)
          currentRecord.setFirstName(updateRecord.getFirstName());

        if (recordProjection.lastName() != null)
          currentRecord.setLastName(updateRecord.getLastName());
      }

      return current;
    }

  }

  private static final class DeleteOp extends AbstractDeleteOperation {
    private final @NotNull UsersStorage storage;

    DeleteOp(final DeleteOperationDeclaration declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @Override
    protected @NotNull CompletableFuture<PersonId_Error_Map.Data> process(
        @NotNull PersonId_Error_Map.Data.Builder builder,
        @NotNull DeleteUsersFieldProjection deleteProjection,
        @NotNull ws.epigraph.tests.resources.users.operations.delete._default.output.OutputUsersFieldProjection outputProjection) {

      final List<DeletePersonMapKeyProjection> keys = deleteProjection.dataProjection().keys();

      final PersonId_Error_Map.Builder resultMapBuilder = PersonId_Error_Map.create();
      builder.set(resultMapBuilder);

      for (final DeletePersonMapKeyProjection key : keys) {
        final @Nullable Person removedPerson = storage.users().datas().remove(key.value());
        //noinspection ConstantConditions why?
        if (removedPerson == null)
          resultMapBuilder.put(
              key.value(),
              Error.create()
                  .setCode(HttpStatusCode.NOT_FOUND)
                  .setMessage("Item with id " + key.value().getVal() + " doesn't exist")
          );

      }

      return CompletableFuture.completedFuture(builder);
    }

  }

  private static final class CapitalizeOp extends AbstractCustomCapitalizeOperation {
    private final @NotNull UsersStorage storage;

    protected CapitalizeOp(final CustomOperationDeclaration declaration, final @NotNull UsersStorage storage) {
      super(declaration);
      this.storage = storage;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected @NotNull CompletableFuture<PersonRecord.Data> process(
        @NotNull PersonRecord.Data.Builder builder,
        @Nullable PersonRecord inputData,
        @NotNull UsersFieldPath path,
        @Nullable ws.epigraph.tests.resources.users.operations.custom.capitalize.input.InputUsersFieldProjection inputFieldProjection,
        @NotNull ws.epigraph.tests.resources.users.operations.custom.capitalize.output.OutputUsersFieldProjection outputProjection) {

      PersonId.Imm key = path.dataProjection().key().value();
      final Person.Builder person = (Person.Builder) storage.users().datas().get(key);
      if (person == null) {
        builder.set_Error(new ErrorValue(HttpStatusCode.NOT_FOUND, "Person with id " + key.getVal() + " not found"));
      } else {
        PersonRecord.Builder personRecord = (PersonRecord.Builder) person.getRecord();
        if (personRecord != null) {
          if (inputFieldProjection != null) {
            @NotNull InputPersonRecordProjection inputProjection = inputFieldProjection.dataProjection();

            if (inputProjection.firstName() != null) {
              final String firstName = personRecord.getFirstName();
              if (firstName != null) {
                personRecord.setFirstName(firstName.toUpperCase());
              }
            }

            if (inputProjection.lastName() != null) {
              final String lastName = personRecord.getFirstName();
              if (lastName != null) {
                personRecord.setFirstName(lastName.toUpperCase());
              }
            }

            builder.set(personRecord);
          }
        }
      }

      return CompletableFuture.completedFuture(builder);
    }

  }

}
