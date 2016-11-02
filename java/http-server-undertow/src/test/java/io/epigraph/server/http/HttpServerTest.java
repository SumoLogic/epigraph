package io.epigraph.server.http;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import io.epigraph.idl.Idl;
import io.epigraph.idl.IdlPsiParser;
import io.epigraph.idl.parser.IdlParserDefinition;
import io.epigraph.idl.parser.psi.IdlFile;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.service.Service;
import io.epigraph.service.ServiceInitializationException;
import io.epigraph.tests.*;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
      PersonId_Person_Map.type,
      epigraph.String.type
  );

  private static final Idl idl;

  static {
    try {
      idl = parseIdlResource("/io/epigraph/tests/service/testService.sdl");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Service buildUsersService() throws ServiceInitializationException {
    return new Service(
        "users",
        Arrays.asList(
            new UserResource(idl.resources().get("user")),
            new UsersResource(idl.resources().get("users"))
        )
    );
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

    Idl idl = null;
    List<PsiProcessingError> errors = new ArrayList<>();
    try {
      idl = IdlPsiParser.parseIdl(psiFile, resolver, errors);
    } catch (PsiProcessingException e) {
      errors = e.errors();
    }

    if (!errors.isEmpty()) {
      for (final PsiProcessingError error : errors) {
        System.err.print(error.message() + " at " + error.location());
      }

      throw new RuntimeException("IDL errors detected");
    }

    assert idl != null;
    return idl;
  }

}
