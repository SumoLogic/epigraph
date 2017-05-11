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
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.tests.resources.users.operations.bestfriend.AbstractReadBestFriendOperation;
import ws.epigraph.tests.resources.users.operations.bestfriend.output.OutputUsersFieldProjection;
import ws.epigraph.tests.resources.users.operations.bestfriend.path.UsersFieldPath;
import ws.epigraph.util.HttpStatusCode;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadBestFriendOperation extends AbstractReadBestFriendOperation{
  private final @NotNull UsersStorage usersStorage;

  protected ReadBestFriendOperation(
      final @NotNull ReadOperationDeclaration declaration,
      final @NotNull UsersStorage storage) {

    super(declaration);
    usersStorage = storage;
  }

  @Override
  protected @NotNull CompletableFuture<Person> process(
      final @NotNull Person.Builder builder,
      final @NotNull UsersFieldPath path,
      final @NotNull OutputUsersFieldProjection projection) {

    PersonId.Imm personId = path.dataProjection().key().value();
    Person person = usersStorage.users().datas().get(personId);

    if (person == null) {
      builder.setRecord_Error(
          new ErrorValue(
              HttpStatusCode.NOT_FOUND,
              "User with id " + personId.getVal() + " not found"
          )
      );
      return CompletableFuture.completedFuture(builder);
    }

    return CompletableFuture.completedFuture(person.getRecord().getBestFriend());
  }
}
