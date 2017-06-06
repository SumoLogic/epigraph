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

package ws.epigraph.tests;

import epigraph.Error;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.tests._resources.users.operations.update.bestfriend.AbstractUpdateBestFriendOperation;
import ws.epigraph.tests._resources.users.operations.update.bestfriend.output.OutputUsersFieldProjection;
import ws.epigraph.tests._resources.users.operations.update.bestfriend.path.UsersFieldPath;
import ws.epigraph.tests._resources.users.operations.update.bestfriend.update.UpdateUsersFieldProjection;
import ws.epigraph.util.HttpStatusCode;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UpdateBestFriendOperation extends AbstractUpdateBestFriendOperation {
  private final @NotNull UsersStorage usersStorage;

  protected UpdateBestFriendOperation(
      final @NotNull UpdateOperationDeclaration declaration,
      final @NotNull UsersStorage storage) {
    super(declaration);
    usersStorage = storage;
  }

  @Override
  protected @NotNull CompletableFuture<Error.Data> process(
      final @NotNull Error.Data.Builder resultBuilder,
      final @NotNull Person updateData,
      final @NotNull UsersFieldPath path,
      final @Nullable UpdateUsersFieldProjection updateProjection,
      final @NotNull OutputUsersFieldProjection outputProjection) {

    PersonId.Imm personId = path.dataProjection().key().value();
    Person person = usersStorage.users().datas().get(personId);

    if (person == null) {
      resultBuilder.set(Error.create()
          .setCode(HttpStatusCode.NOT_FOUND).setMessage("User with id " + personId.getVal() + " not found"));
    } else {
      PersonId newBestFriendId = updateData.getId();
      assert newBestFriendId != null; // required by projection
      Person newBestFriend = usersStorage.users().datas().get(newBestFriendId.toImmutable());

      if (newBestFriend == null) {
        resultBuilder.set(Error.create()
            .setCode(HttpStatusCode.NOT_FOUND).setMessage("User with id " + newBestFriendId.getVal() + " not found"));
      } else {

        // need toBuilder!
        PersonRecord personRecord = person.getRecord();
        assert personRecord != null;

        if (personRecord instanceof PersonRecord.SetBestFriend) {
          PersonRecord.SetBestFriend settable = (PersonRecord.SetBestFriend) personRecord;
          settable.setBestFriend(newBestFriend);
        }

      }

    }

    return CompletableFuture.completedFuture(resultBuilder);
  }
}
