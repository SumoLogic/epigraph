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

import ws.epigraph.errors.ErrorValue;
import ws.epigraph.idl.ResourceDeclaration;
import ws.epigraph.idl.operations.ReadOperationDeclaration;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.tests.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class UserResource extends Resource {

  public UserResource(@NotNull ResourceDeclaration resourceDeclaration) throws ServiceInitializationException {
    super(
        resourceDeclaration,
        Collections.singletonList(
            new ReadOp(((ReadOperationDeclaration) resourceDeclaration.operations().get(0)))
        ),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList()
    );
  }

  private static final class ReadOp extends ReadOperation<Person> {

    private ReadOp(@NotNull ReadOperationDeclaration declaration) { super(declaration); }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<Person>> process(@NotNull ReadOperationRequest request) {

      Person person = User.create()
          .setId(UserId.create(1))
          .setRecord(UserRecord.create()
              .setId(PersonId.create(1))
              .setFirstName(epigraph.String.create("Alfred"))
              .setLastName(epigraph.String.create("Hitchcock"))
              .setProfile_Error(new ErrorValue(404, "Not Found", null))
              .setBestFriend$(User.create()
                  .setId(UserId.create(2))
                  .setRecord(UserRecord.create()
                      .setId(PersonId.create(2))
                      .setFirstName(epigraph.String.create("Bruce"))
                      .setLastName(epigraph.String.create("Willis"))
                      .setProfile(Url.create("http://google.com/"))
                  )
              )
              .setWorstEnemy(UserRecord.create()
                  .setId(PersonId.create(3))
                  .setFirstName(epigraph.String.create("Chuck"))
                  .setLastName(epigraph.String.create("Norris"))
                  .setProfile(Url.create("http://bing.com/"))
              )
              .setFriends(Person_List.create()
                  .add(User.create()
                      .setId(UserId.create(2))
                      .setRecord(UserRecord.create()
                          .setId(PersonId.create(2))
                          .setFirstName(epigraph.String.create("Bruce"))
                          .setLastName(epigraph.String.create("Willis"))
                          .setProfile(Url.create("http://google.com/"))
                      )
                  )
                  .add(Person.create()
                      .setId(PersonId.create(4))
                      .setRecord(PersonRecord.create()
                          .setId(PersonId.create(4))
                          .setFirstName(epigraph.String.create("Douglas"))
                          .setLastName(epigraph.String.create("Adams"))
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
