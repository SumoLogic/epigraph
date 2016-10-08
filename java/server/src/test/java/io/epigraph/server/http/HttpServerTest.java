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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

  private static final String idlText = lines(
      "namespace test",
      "resource users : map[epigraph.String,io.epigraph.tests.Person] {",
      "  READ {",
      "    ; authToken : epigraph.String",
      "    output []( :(id, +record (+id, firstName)) )",
      "  }",
      "}"
  );

  private static final Idl idl;

  static {
    try {
      idl = parseFile(idlText);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Service buildUsersService() throws ServiceInitializationException {
    return new Service("users", Collections.singletonList(buildUsersResource()));
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
        String_Person_Map.Imm personMap = String_Person_Map
            .create()

            .put$(epigraph.String.create("1").toImmutable(), Person
                .create()
                .setId(PersonId.create(1))
                .setRecord(
                    PersonRecord
                        .create()
                        .setId(PersonId.create(1))
                        .setFirstName(epigraph.String.create("Alfred"))
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
                              .setHandler(new UndertowHandler(buildUsersService(), resolver))
                              .build();

    server.start();
  }

  @NotNull
  private static Idl parseFile(@NotNull String text) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull IdlFile psiFile =
        (IdlFile) EpigraphPsiUtil.parseFile("idlTest.idl", text, IdlParserDefinition.INSTANCE, errorsAccumulator);

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
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

  private static String lines(String... lines) { return Arrays.stream(lines).collect(Collectors.joining("\n")); }
}
