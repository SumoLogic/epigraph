/* Created by yegor on 10/27/16. */

package ws.epigraph.server.http;

import ws.epigraph.errors.ErrorValue;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.idl.operations.ReadOperationIdl;
import ws.epigraph.service.Resource;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.service.operations.ReadOperation;
import ws.epigraph.service.operations.ReadOperationRequest;
import ws.epigraph.service.operations.ReadOperationResponse;
import ws.epigraph.tests.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class UserResource extends Resource {

  public UserResource(@NotNull ResourceIdl resourceIdl) throws ServiceInitializationException {
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

  private static class ReadOp extends ReadOperation<Person> {

    private ReadOp(@NotNull ReadOperationIdl declaration) { super(declaration); }

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
