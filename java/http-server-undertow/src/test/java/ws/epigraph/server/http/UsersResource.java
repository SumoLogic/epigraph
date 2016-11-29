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

import epigraph.String;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.idl.operations.ReadOperationIdl;
import ws.epigraph.projections.req.output.*;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.tests.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UsersResource extends Resource {

  public UsersResource(@NotNull ResourceIdl resourceIdl) throws ServiceInitializationException {
    super(
        resourceIdl,
        Arrays.asList(
            new ReadOp(((ReadOperationIdl) resourceIdl.operations().get(0)))
        ),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList(),
        Collections.emptyList()
    );
  }

  private static class ReadOp extends ReadOperation<PersonId_Person_Map.Data> {

    private ReadOp(@NotNull ReadOperationIdl declaration) { super(declaration); }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>> process(@NotNull ReadOperationRequest request) {
      PersonId_Person_Map.Builder map = PersonId_Person_Map.create();

      ReqOutputMapModelProjection mapProjection =
          (ReqOutputMapModelProjection) request.outputProjection().varProjection()
              .tagProjection(String_Person_Map.type.self.name()).projection();

      List<ReqOutputKeyProjection> keyProjections = mapProjection.keys();
      if (keyProjections == null) {
        for (int id = 1; id <= 10; ++id) {
          PersonId pid = PersonId.create(id);
          map.put$(pid, fetchPerson(pid));
        }
//        map
//
//            .put$(PersonId.create(1), User.create()
//                .setId(UserId.create(1))
//                .setRecord(UserRecord.create()
//                    .setId(PersonId.create(1))
//                    .setFirstName(epigraph.String.create("Alfred"))
//                    .setLastName(epigraph.String.create("Hitchcock"))
//                    .setProfile_Error(new ErrorValue(404, "Not Found", null))
//                    .setBestFriend$(User.create()
//                        .setId(UserId.create(2))
//                        .setRecord(UserRecord.create()
//                            .setId(PersonId.create(2))
//                            .setFirstName(epigraph.String.create("Bruce"))
//                            .setLastName(epigraph.String.create("Willis"))
//                            .setProfile(Url.create("http://google.com/"))
//                        )
//                    )
//                    .setWorstEnemy(UserRecord.create()
//                        .setId(PersonId.create(3))
//                        .setFirstName(epigraph.String.create("Chuck"))
//                        .setLastName(epigraph.String.create("Norris"))
//                        .setProfile(Url.create("http://bing.com/"))
//                    )
//                    .setFriends(Person_List.create()
//                        .add(User.create()
//                            .setId(UserId.create(2))
//                            .setRecord(UserRecord.create()
//                                .setId(PersonId.create(2))
//                                .setFirstName(epigraph.String.create("Bruce"))
//                                .setLastName(epigraph.String.create("Willis"))
//                                .setProfile(Url.create("http://google.com/"))
//                            )
//                        )
//                        .add(Person.create()
//                            .setId(PersonId.create(4))
//                            .setRecord(PersonRecord.create()
//                                .setId(PersonId.create(4))
//                                .setFirstName(epigraph.String.create("Douglas"))
//                                .setLastName(epigraph.String.create("Adams"))
//                            )
//                        )
//                    )
//                )
//            )
//
//            .put$(PersonId.create(2), Person
//                .create()
//                .setId(PersonId.create(2))
//                .setRecord(
//                    PersonRecord
//                        .create()
//                        .setId(PersonId.create(2))
//                        .setFirstName(epigraph.String.create("Bruce"))
//                        .setLastName(epigraph.String.create("Willis"))
//                )
//            )
//
//            .put$(PersonId.create(3), Person
//                .create()
//                .setId(PersonId.create(3))
//                .setRecord_Error(
//                    new ErrorValue(402, new Exception("Payment required to fetch user data"))
//                )
//            );
      } else {
        keyProjections.stream().map(kp -> (PersonId) kp.value()).forEach(pid -> {
          map.put$(pid, fetchPerson(pid));
        });
      }

      CompletableFuture<ReadOperationResponse<PersonId_Person_Map.Data>> future = new CompletableFuture<>();
      future.complete(
          new ReadOperationResponse<>(
              PersonId_Person_Map.type.createDataBuilder().set(map)
          )
      );
      return future;
    }

    private Person fetchPerson(@NotNull PersonId pid) {
      Integer id = pid.getVal();

      final Person bestFriend;
      final UserRecord.@NotNull Builder bestFriendRecord = UserRecord.create()
          .setId(PersonId.create(id + 1))
          .setFirstName(String.create("First" + (id + 1)))
          .setLastName(String.create("Last" + (id + 1)))
          .setProfile(Url.create("http://google.com/" + (id + 1)));

      if (id % 5 == 0)
        bestFriend = Person.create().setId(PersonId.create(id + 1)).setRecord(bestFriendRecord);
      else
        bestFriend = User.create().setId(UserId.create(id + 1)).setRecord(bestFriendRecord);

      return User
          .create()
          .setId(UserId.create(pid.getVal()))
          .setRecord(UserRecord
              .create()
              .setId(pid)
              .setFirstName(epigraph.String.create("First" + id))
              .setLastName(epigraph.String.create("Last" + id))
              .setProfile_Error(new ErrorValue(404, "Not Found", null))
              .setBestFriend$(bestFriend
              )
              .setWorstEnemy(UserRecord.create()
                  .setId(PersonId.create(id + 2))
                  .setFirstName(epigraph.String.create("First" + (id + 2)))
                  .setLastName(epigraph.String.create("Last" + (id + 2)))
                  .setProfile(Url.create("http://bing.com/" + (id + 2)))
              )
              .setFriends(Person_List.create()
                  .add(User.create()
                      .setId(UserId.create(id + 1))
                      .setRecord(UserRecord.create()
                          .setId(PersonId.create(id + 1))
                          .setFirstName(epigraph.String.create("First" + (id + 1)))
                          .setLastName(epigraph.String.create("Last" + (id + 1)))
                          .setProfile(Url.create("http://google.com/" + (id + 1)))
                      )
                  )
                  .add(Person.create()
                      .setId(PersonId.create(id + 3))
                      .setRecord(PersonRecord.create()
                          .setId(PersonId.create(id + 3))
                          .setFirstName(epigraph.String.create("First" + (id + 3)))
                          .setLastName(epigraph.String.create("Last" + (id + 3)))
                      )
                  )
              )
          );
    }

  }

}
