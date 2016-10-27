/* Created by yegor on 10/27/16. */

package io.epigraph.server.http;

import io.epigraph.errors.ErrorValue;
import io.epigraph.idl.ResourceIdl;
import io.epigraph.idl.operations.ReadOperationIdl;
import io.epigraph.service.Resource;
import io.epigraph.service.ServiceInitializationException;
import io.epigraph.service.operations.ReadOperation;
import io.epigraph.service.operations.ReadOperationRequest;
import io.epigraph.service.operations.ReadOperationResponse;
import io.epigraph.tests.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class UsersResource extends Resource {

  public UsersResource(@NotNull ResourceIdl resourceIdl) throws ServiceInitializationException {
    super(
        resourceIdl,
        Arrays.asList(
            new ReadOp(((ReadOperationIdl) resourceIdl.operations().get(0)))
        )
    );
  }

  private static class ReadOp extends ReadOperation {

    private ReadOp(@NotNull ReadOperationIdl declaration) { super(declaration); }

    @Override
    public @NotNull CompletableFuture<ReadOperationResponse> process(@NotNull ReadOperationRequest request) {

      String_Person_Map personMap = String_Person_Map.create()

          .put$(epigraph.String.create("1").toImmutable(), User.create()
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
              )
          )

          .put$(epigraph.String.create("2").toImmutable(), Person
              .create()
              .setId(PersonId.create(2))
              .setRecord(
                  PersonRecord
                      .create()
                      .setId(PersonId.create(2))
                      .setFirstName(epigraph.String.create("Bruce"))
                      .setLastName(epigraph.String.create("Willis"))
              )
          )

          .put$(epigraph.String.create("3").toImmutable(), Person
              .create()
              .setId(PersonId.create(3))
              .setRecord_Error(
                  new ErrorValue(402, new Exception("Payment required to fetch user data"))
              )
          )

          .toImmutable();

      ///////////////

      CompletableFuture<ReadOperationResponse> future = new CompletableFuture<>();
      future.complete(
          new ReadOperationResponse(
              String_Person_Map.type.createDataBuilder().set(personMap)
          )
      );
      return future;
    }

  }

}
