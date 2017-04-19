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

import org.jetbrains.annotations.NotNull;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.tests.*;
import ws.epigraph.tests.resources.user.AbstractUserResourceFactory;

import java.util.concurrent.CompletableFuture;

public class UserResourceFactory extends AbstractUserResourceFactory {

  @Override
  protected @NotNull ReadOperation<Person> constructReadOperation(final @NotNull ReadOperationDeclaration operationDeclaration)
      throws ServiceInitializationException {
    return new ReadOp(operationDeclaration);
  }

  private static final class ReadOp extends ReadOperation<Person> {

    private ReadOp(@NotNull ReadOperationDeclaration declaration) { super(declaration); }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<Person>> process(@NotNull ReadOperationRequest request) {

      Person person = User.create()
          .setId(UserId.create(1))
          .setRecord(UserRecord.create()
              .setId(PersonId.create(1))
              .setFirstName("Alfred")
              .setLastName("Hitchcock")
              .setProfile_Error(new ErrorValue(404, "Not Found", null))
              .setBestFriend(User.create()
                  .setId(UserId.create(2))
                  .setRecord(UserRecord.create()
                      .setId(PersonId.create(2))
                      .setFirstName("Bruce")
                      .setLastName("Willis")
                      .setProfile(Url.create("http://google.com/"))
                  )
              )
              .setWorstEnemy(UserRecord.create()
                  .setId(PersonId.create(3))
                  .setFirstName("Chuck")
                  .setLastName("Norris")
                  .setProfile(Url.create("http://bing.com/"))
              )
              .setFriends(Person_List.create()
                  .add(User.create()
                      .setId(UserId.create(2))
                      .setRecord(UserRecord.create()
                          .setId(PersonId.create(2))
                          .setFirstName("Bruce")
                          .setLastName("Willis")
                          .setProfile(Url.create("http://google.com/"))
                      )
                  )
                  .add(Person.create()
                      .setId(PersonId.create(4))
                      .setRecord(PersonRecord.create()
                          .setId(PersonId.create(4))
                          .setFirstName("Douglas")
                          .setLastName("Adams")
                      )
                  )
              )
          );

      CompletableFuture<ReadOperationResponse<Person>> future = new CompletableFuture<>();
      future.complete(new ReadOperationResponse<>(person));
      return future;
    }

  }

}
