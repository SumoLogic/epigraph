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

package ws.epigraph.server.http;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import ws.epigraph.idl.Idl;
import ws.epigraph.idl.parser.IdlPsiParser;
import ws.epigraph.idl.parser.IdlParserDefinition;
import ws.epigraph.idl.parser.psi.IdlFile;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.server.http.undertow.UndertowHandler;
import ws.epigraph.service.Service;
import ws.epigraph.service.ServiceInitializationException;
import ws.epigraph.tests.*;
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
      idl = parseIdlResource("/ws/epigraph/tests/service/testService.sdl");
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
