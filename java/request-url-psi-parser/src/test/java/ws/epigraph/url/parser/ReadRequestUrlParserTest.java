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

package ws.epigraph.url.parser;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.idl.Idl;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.idl.operations.OperationIdl;
import ws.epigraph.idl.operations.ReadOperationIdl;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.output.ReqOutputFieldProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import ws.epigraph.url.ReadRequestUrl;
import ws.epigraph.url.parser.psi.UrlReadUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static ws.epigraph.test.TestUtil.*;
import static ws.epigraph.url.parser.RequestUrlParserUtil.parseIdl;
import static ws.epigraph.url.parser.RequestUrlParserUtil.printParameters;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadRequestUrlParserTest {
  private TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      String_Person_Map.type,
      epigraph.String.type,
      epigraph.Boolean.type
  );

  private String idlText = lines(
      "namespace test",
      "import ws.epigraph.tests.Person",
      "import ws.epigraph.tests.UserRecord",
      "resource users : map[String,Person] {",
      "  READ {",
      "    outputProjection [required]( :record (id, firstName) )",
      "  }",
      "  READ {",
      "    outputProjection [required]( :record (id, firstName, lastName) )",
      "  }",
      "  READ {",
      "    path /.",
      "    outputProjection :record (id, firstName, bestFriend :record (id, firstName) )",
      "  }",
      "  READ {",
      "    path /.:record/bestFriend",
      "    outputProjection :record (id, firstName)",
      "  }",
      "}"
  );

  private ReadOperationIdl readIdl1;
  private DataType resourceType = String_Person_Map.type.dataType();

  {
    try {
      Idl idl = parseIdl(idlText, resolver);
      ResourceIdl resourceIdl = idl.resources().get("users");

      final @NotNull List<OperationIdl> operationIdls = resourceIdl.operations();
      readIdl1 = (ReadOperationIdl) operationIdls.get(0);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testParsing1() throws IOException, PsiProcessingException {
    test(
        readIdl1,
        "/users/123:record(id)?format='json'&verbose=true",
        "users",
        3,
        "+users / \"123\" :record ( id )",
        "{format = \"json\", verbose = true}"
    );
  }

  private void test(
      ReadOperationIdl op,
      String url,
      String expectedResource,
      int expectedSteps,
      String expectedProjection,
      String expectedParams)
      throws IOException, PsiProcessingException {

    List<PsiProcessingError> errors = new ArrayList<>();
    final @NotNull ReadRequestUrl requestUrl = ReadRequestUrlPsiParser.parseReadRequestUrl(
        resourceType,
        op,
        parseUrlPsi(url),
        resolver,
        errors
    );

    failIfHasErrors(errors);

    assertEquals(expectedResource, requestUrl.fieldName());

    final @NotNull StepsAndProjection<ReqOutputFieldProjection> stepsAndProjection = requestUrl.outputProjection();
    assertEquals(expectedSteps, stepsAndProjection.pathSteps());
    assertEquals(
        expectedProjection,
        printReqOutputFieldProjection(expectedResource, stepsAndProjection.projection(), expectedSteps)
    );

    assertEquals(expectedParams, printParameters(requestUrl.parameters()));
  }


  private static UrlReadUrl parseUrlPsi(@NotNull String text) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull UrlReadUrl urlPsi =
        EpigraphPsiUtil.parseText(text, UrlSubParserDefinitions.READ_URL, errorsAccumulator);

    failIfHasErrors(urlPsi, errorsAccumulator);

    return urlPsi;
  }

}
