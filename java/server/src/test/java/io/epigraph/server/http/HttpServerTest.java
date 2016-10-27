package io.epigraph.server.http;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import io.epigraph.errors.ErrorValue;
import io.epigraph.idl.Idl;
import io.epigraph.idl.IdlPsiParser;
import io.epigraph.idl.ResourceIdl;
import io.epigraph.idl.operations.ReadOperationIdl;
import io.epigraph.idl.parser.IdlParserDefinition;
import io.epigraph.idl.parser.psi.IdlFile;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.service.Resource;
import io.epigraph.service.Service;
import io.epigraph.service.ServiceInitializationException;
import io.epigraph.service.operations.ReadOperation;
import io.epigraph.service.operations.ReadOperationRequest;
import io.epigraph.service.operations.ReadOperationResponse;
import io.epigraph.tests.*;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class HttpServerTest {

  private static final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      String_Person_Map.type,
      epigraph.String.type
  );

  private static final Idl idl;

  static {
    try {
      idl = parseIdlResource("/io/epigraph/server/http/testService.sdl");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Service buildUsersService() throws ServiceInitializationException {
    return new Service("users", Arrays.asList(buildUserResource(), buildUsersResource()));
  }

  private static Resource buildUserResource() throws ServiceInitializationException {
    ResourceIdl userResource = idl.resources().get("user");
    assert userResource != null;

    return new Resource(
        userResource,
        Collections.singletonList(
            buildDefaultUserReadOperation(
                ((ReadOperationIdl) userResource.operations().get(0))
            )
        )
    );
  }

  private static ReadOperation buildDefaultUserReadOperation(@NotNull ReadOperationIdl operationIdl) {
    return new ReadOperation(operationIdl) {
      @NotNull
      @Override
      public CompletableFuture<ReadOperationResponse> process(@NotNull ReadOperationRequest request) {
        Person.Imm person = User.create()
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
            .toImmutable();

        ///////////////

        CompletableFuture<ReadOperationResponse> future = new CompletableFuture<>();
        future.complete(new ReadOperationResponse(person));
        return future;
      }
    };
  }

  private static Resource buildUsersResource() throws ServiceInitializationException {
    ResourceIdl usersResource = idl.resources().get("users");
    assert usersResource != null;

    return new Resource(
        usersResource,
        Collections.singletonList(
            buildDefaultUsersReadOperation(
                ((ReadOperationIdl) usersResource.operations().get(0))
            )
        )
    );
  }

  private static ReadOperation buildDefaultUsersReadOperation(@NotNull ReadOperationIdl operationIdl) {
    return new ReadOperation(operationIdl) {
      @NotNull
      @Override
      public CompletableFuture<ReadOperationResponse> process(@NotNull ReadOperationRequest request) {
        String_Person_Map personMap = String_Person_Map
            .create()

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
    };
  }

  public static void main(String[] args) throws ServiceInitializationException {
    Undertow server = Undertow.builder()
                              .addHttpListener(8888, "localhost")
                              .setServerOption(UndertowOptions.DECODE_URL, false) // don't decode URLs
                              .setHandler(new UndertowHandler(buildUsersService(), resolver))
                              .build();

    server.start();
  }

  @NotNull
  private static Idl parseIdlText(@NotNull String text) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errAcc = new EpigraphPsiUtil.ErrorsAccumulator();
    IdlFile psiFile = (IdlFile) EpigraphPsiUtil.parseFile("dummy.idl", text, IdlParserDefinition.INSTANCE, errAcc);
    return parseIdl(psiFile, errAcc);
  }

  private static @NotNull Idl parseIdlResource(@NotNull String resourcePath) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errAcc = new EpigraphPsiUtil.ErrorsAccumulator();
    IdlFile psiFile = (IdlFile) EpigraphPsiUtil.parseResource(resourcePath, IdlParserDefinition.INSTANCE, errAcc);
    return parseIdl(psiFile, errAcc);
  }

  private static @NotNull Idl parseIdl(@NotNull IdlFile psiFile, EpigraphPsiUtil.ErrorsAccumulator errAcc)
      throws IOException {
    if (errAcc.hasErrors()) {
      for (PsiErrorElement element : errAcc.errors()) {
        System.err.println(element.getErrorDescription() + " at " + EpigraphPsiUtil.getLocation(element));
      }
      throw new RuntimeException(DebugUtil.psiTreeToString(psiFile, true));
    }
    try {
      return IdlPsiParser.parseIdl(psiFile, resolver);
    } catch (PsiProcessingException e) {
      throw new RuntimeException(e.getMessage() + " at " + EpigraphPsiUtil.getLocation(e.psi()), e);
    }
  }

}
