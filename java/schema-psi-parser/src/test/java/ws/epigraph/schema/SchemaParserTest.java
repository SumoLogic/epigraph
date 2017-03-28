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

package ws.epigraph.schema;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.ResourcesSchemaPsiParser;
import ws.epigraph.schema.parser.SchemaParserDefinition;
import ws.epigraph.schema.parser.psi.SchemaFile;
import ws.epigraph.tests.*;

import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ws.epigraph.test.TestUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaParserTest {
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      String_Person_Map.type,
      epigraph.String.type,
      epigraph.Boolean.type
  );

  @Test
  public void testEmpty() throws IOException {
    testParse(
        lines(
            "namespace test",
            "import ws.epigraph.tests.Person"
        ),
        "namespace test"
    );
  }

  @Test
  public void testEmptyResource() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] { }"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] { }"
        )
    );
  }

  @Test
  public void testResource() throws IOException {
    testParse(
        lines(
            "namespace test",
            "import ws.epigraph.tests.Person",
            "import ws.epigraph.tests.UserRecord",
            "resource users : map[String,Person] {",
            "  outputProjection defaultOutput : map[String,Person] = [forbidden](:id)",
            "  read {",
            "    doc = \"dome doc string\"",
            "    outputProjection {",
            "      ;superUser: UserRecord { default : { id : 1337 }, doc = \"super user account\" } (id)",
            "    } [required]( :`record` (id, firstName) )",
            "  }",
            "  read readWithPath {",
            "    path / .",
            "    outputProjection :`record` (id, firstName)",
            "  }",
            "  create {",
            "    inputProjection []( :`record` ( firstName, lastName) )",
            "    outputType Boolean",
            "    outputProjection", // empty projection
            "  }",
            "  update {",
            "    doc = \"dome doc string\"",
            "    inputProjection []( :`record` ( firstName, lastName) )",
            "    outputProjection $defaultOutput",
            "  }",
            "  update customUpdate {",
            "    doc = \"dome doc string\"",
            "    inputProjection []( :`record` ( firstName, lastName) )",
            "    outputProjection $defaultOutput",
            "  }",
            "  delete {",
            "    deleteProjection [forbidden]( +:`record` ( firstName ) )",
            "    outputType Boolean",
            "  }",
            "  custom customOp {",
            "    method POST",
            "    doc = \"dome doc string\"",
            "    path / . :`record` / bestFriend",
            "    inputType map[String,Person]",
            "    inputProjection []( :`record` ( firstName, lastName) )",
            "    outputType map[String,Person]",
            "    outputProjection $defaultOutput",
            "  }",
            "}"
        ),

        lines(
            "namespace test",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    doc = \"dome doc string\",",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection {",
            "      ;superUser: ws.epigraph.tests.UserRecord",
            "        { doc = \"super user account\" default: { id: 1337 } } ( id )",
            "    } [ required ]( :`record` ( id, firstName ) )",
            "  }",
            "  read readWithPath {",
            "    path / .,",
            "    outputType ws.epigraph.tests.Person,",
            "    outputProjection :`record` ( id, firstName )",
            "  }",
            "  create {",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType epigraph.Boolean",
            "  }",
            "  update {",
            "    doc = \"dome doc string\",",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $defaultOutput",
            "  }",
            "  update customUpdate {",
            "    doc = \"dome doc string\",",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $defaultOutput",
            "  }",
            "  delete {",
            "    deleteProjection [ forbidden ]( +:`record` ( firstName ) ),",
            "    outputType epigraph.Boolean",
            "  }",
            "  custom customOp {",
            "    method POST,",
            "    doc = \"dome doc string\",",
            "    path / . :`record` / bestFriend,",
            "    inputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    inputProjection [ ]( :`record` ( firstName, lastName ) ),",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $defaultOutput",
            "  }",
            "  outputProjection defaultOutput: map[epigraph.String,ws.epigraph.tests.Person]",
            "    = [ forbidden ]( :id )",
            "}"
        )
    );
  }

  @Test
  public void testParseSimpleReference() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] {",
            "  outputProjection out: map[String,Person] = [] (:id)",
            "  read {",
            "    outputProjection $out",
            "  }",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection $out",
            "  }",
            "  outputProjection out: map[epigraph.String,ws.epigraph.tests.Person] =",
            "    [ ]( :id )",
            "}"
        )
    );
  }

  @Test
  public void testParseStupidReference() throws IOException {
    try {
      testParse(
          lines(
              "namespace ws.epigraph.tests",
              "resource users: map[String,Person] {",
              "  outputProjection out: map[String,Person] = $out",
              "  read {",
              "    outputProjection $out",
              "  }",
              "}"
          ),
          lines(
              "namespace ws.epigraph.tests",
              "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
              "  read {",
              "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
              "    outputProjection $out",
              "  }",
              "  outputProjection out: map[epigraph.String,ws.epigraph.tests.Person] =",
              "    []( :id )",
              "}"
          )
      );
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("Projection 'out' is not defined"));
    }
  }

  @Test
  public void testParseCrossReference() throws IOException {
    testParse(
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[String,Person] {",
            "  outputProjection person1: Person = :`record` (id, firstName, bestFriend $person2)",
            "  outputProjection person2: Person = :`record` (id, lastName, bestFriend $person1)",
            "  read {",
            "    outputProjection [] ( $person1 )",
            "  }",
            "}"
        ),
        lines(
            "namespace ws.epigraph.tests",
            "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
            "  read {",
            "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
            "    outputProjection [ ]( $person1 )",
            "  }",
            "  outputProjection person1: ws.epigraph.tests.Person =",
            "    :`record` ( id, firstName, bestFriend $person2 )",
            "  outputProjection person2: ws.epigraph.tests.Person =",
            "    :`record` ( id, lastName, bestFriend $person1 )",
            "}"
        )
    );
  }

  @Test
  public void testParseStupidCrossReference() throws IOException {
    try {
      testParse(
          lines(
              "namespace ws.epigraph.tests",
              "resource users: map[String,Person] {",
              "  outputProjection person1: Person = $person2",
              "  outputProjection person2: Person = $person1",
              "  read {",
              "    outputProjection [] ( $person1 )",
              "  }",
              "}"
          ),
          lines(
              "namespace ws.epigraph.tests",
              "resource users: map[epigraph.String,ws.epigraph.tests.Person] {",
              "  read {",
              "    outputType map[epigraph.String,ws.epigraph.tests.Person],",
              "    outputProjection []( $person1 )",
              "  }",
              "  outputProjection person1: ws.epigraph.tests.Person =",
              "    :`record` ( id, firstName, bestFriend $person2 )",
              "  outputProjection person2: ws.epigraph.tests.Person =",
              "    :`record` ( id, lastName, bestFriend $person1 )",
              "}"
          )
      );
      fail();
    } catch (@SuppressWarnings("ErrorNotRethrown") AssertionError e) {
      assertTrue(e.getMessage().contains("Projection 'person1' is not defined"));
      assertTrue(e.getMessage().contains("Projection 'person2' is not defined"));
    }
  }

  private void testParse(String idlStr, String expected) {
    ResourcesSchema schema = parseSchema(idlStr, resolver);
    assertEquals(expected, printSchema(schema));
  }

  private static @NotNull ResourcesSchema parseSchema(@NotNull String text, @NotNull TypesResolver resolver) {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull SchemaFile psiFile =
        (SchemaFile) EpigraphPsiUtil.parseFile("test.epigraph", text, SchemaParserDefinition.INSTANCE, errorsAccumulator);

    failIfHasErrors(psiFile, errorsAccumulator);

//    String psiDump = DebugUtil.psiToString(psiFile, true, false).trim();
//    System.out.println(psiDump);

    return runPsiParser(context -> ResourcesSchemaPsiParser.parseResourcesSchema(psiFile, resolver, context));
  }

}
