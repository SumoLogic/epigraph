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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.schema.operations.CreateOperationDeclaration;
import ws.epigraph.tests._resources.users.operations.create.friends.AbstractCreateFriendsOperation;
import ws.epigraph.tests._resources.users.operations.create.friends.input.InputUsersFieldProjection;
import ws.epigraph.tests._resources.users.operations.create.friends.output.OutputUsersFieldProjection;
import ws.epigraph.tests._resources.users.operations.create.friends.path.UsersFieldPath;
import ws.epigraph.util.HttpStatusCode;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CreateFriendsOperation extends AbstractCreateFriendsOperation {
  private final @NotNull UsersStorage usersStorage;

  protected CreateFriendsOperation(
      final @NotNull CreateOperationDeclaration declaration,
      final @NotNull UsersStorage storage) {

    super(declaration);
    usersStorage = storage;
  }

  @Override
  protected @NotNull CompletableFuture<Person_List.Data> process(
      final @NotNull Person_List.Data.Builder resultBuilder,
      final @NotNull Person_List inputData,
      final @NotNull UsersFieldPath path,
      final @Nullable InputUsersFieldProjection inputProjection,
      final @NotNull OutputUsersFieldProjection outputProjection) {

    PersonId.Imm personId = path.dataProjection().key().value();
    Map<PersonId.Imm, Person> personMap = usersStorage.users().datas();
    User.Builder person = (User.Builder) personMap.get(personId); // crude! and may fail

    if (person == null)
      resultBuilder.set_Error(new ErrorValue(
          HttpStatusCode.NOT_FOUND,
          "User with id " + personId.getVal() + " not found"
      ));
    else {
      Person_List.Builder resultListBuilder = Person_List.create();
      resultBuilder.set(resultListBuilder);

      UserRecord.Builder personRecord = (UserRecord.Builder) person.getRecord();
      assert personRecord != null; // we have records for everyone

      Person_List.Builder friends = (Person_List.Builder) personRecord.getFriends();
      if (friends == null) {
        friends = Person_List.create();
        personRecord.setFriends(friends);

        for (final Person friend : inputData.datas()) {
          PersonId friendId = friend.getId();
          assert friendId != null;

          Person friend_ = personMap.get(friendId.toImmutable());
          if (friend_ == null)
            resultListBuilder.add(Person.Type.instance().createDataBuilder().setId_Error(
                new ErrorValue(HttpStatusCode.NOT_FOUND, "User with id " + friendId.getVal() + " not found")
            ));
          else {
            friends.add(friend_);
            resultListBuilder.add(friend_);
          }

        }
      } else {

        for (final Person friend : inputData.datas()) {
          PersonId friendId = friend.getId();
          assert friendId != null;
          Integer friendIdVal = friendId.getVal();

          boolean friendExists =
              friends.datas().stream().anyMatch(f -> f.getId() != null && friendIdVal.equals(f.getId().getVal()));

          if (friendExists) {
            resultListBuilder.add(Person.Type.instance().createDataBuilder().setId_Error(
                new ErrorValue(HttpStatusCode.BAD_REQUEST, "Friend with id " + friendIdVal + " already exists")
            ));
          } else {
            Person friend_ = personMap.get(friendId.toImmutable());
            if (friend_ == null)
              resultListBuilder.add(Person.Type.instance().createDataBuilder().setId_Error(
                  new ErrorValue(HttpStatusCode.NOT_FOUND, "User with id " + friendIdVal + " not found")
              ));
            else {
              friends.add(friend_);
              resultListBuilder.add(friend_);
            }
          }
        }

      }
    }


    return CompletableFuture.completedFuture(resultBuilder);
  }
}
