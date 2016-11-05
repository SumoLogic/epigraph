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

package ws.epigraph.projections.op.path;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import ws.epigraph.idl.parser.projections.IdlSubParserDefinitions;
import ws.epigraph.idl.parser.psi.IdlOpVarPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.SimpleTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.*;
import ws.epigraph.types.DataType;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputPathTest {
  @Test
  public void testParseEmpty() throws PsiProcessingException {
    testVarPathParsing("");
  }

  @Test
  public void testParseTag() throws PsiProcessingException {
    testVarPathParsingErr(":id");
  }

  @Test
  public void testParseParam() throws PsiProcessingException {
    testVarPathParsing(
        ":record { ;foo: epigraph.String } / id { ;+param: map[epigraph.String,ws.epigraph.tests.Person] []( :id ) = ( \"foo\": < id: 123 > ) { deprecated = true } }"
        ,
        lines(
            ":record { ;foo: epigraph.String }",
            "  /",
            "    id {",
            "      ;+param: map[epigraph.String,ws.epigraph.tests.Person] []( :id ) = ( \"foo\": < id: 123 > ) { deprecated = true }",
            "    }"
        )
    );
  }


  @Test
  public void testParseRecordFieldsWithStructure() throws PsiProcessingException {
    testVarPathParsing(":record / bestFriend :record / id");
  }

  @Test
  public void testParseRecordFieldsWithCustomParams() throws PsiProcessingException {
    testVarPathParsing(":record / bestFriend { deprecated = true } :record / id");
  }

  @Test
  public void testParseMap() throws PsiProcessingException {
    testVarPathParsing(":record / friendsMap { ;param: epigraph.String } / . :record / id");
  }

  private void testVarPathParsingErr(String str) {
    try {
      testVarPathParsing(
          new DataType(Person.type, Person.id),
          str
          ,
          str
      );
      fail();
    } catch (PsiProcessingException ignored) {
    }
  }

  private void testVarPathParsing(String str) throws PsiProcessingException {
    testVarPathParsing(
        str
        ,
        str
    );
  }

  private void testVarPathParsing(String str, String expected) throws PsiProcessingException {
    testVarPathParsing(
        new DataType(Person.type, Person.id),
        str
        ,
        expected
    );
  }

  private void testVarPathParsing(DataType varDataType, String projectionString, String expected)
      throws PsiProcessingException {

    OpVarPath varPath = parseOpVarPath(varDataType, projectionString);

    String actual = print(varPath);

    assertEquals("\n" + actual, expected, actual);
//    assertEquals(expected.trim(), actual.trim());
  }

  private OpVarPath parseOpVarPath(DataType varDataType, String projectionString) throws PsiProcessingException {

    TypesResolver
        resolver = new SimpleTypesResolver(
        PersonId.type,
        Person.type,
        User.type,
        UserId.type,
        UserRecord.type,
        String_Person_Map.type,
        epigraph.String.type
    );

    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    IdlOpVarPath psiVarProjection = EpigraphPsiUtil.parseText(
        projectionString,
        IdlSubParserDefinitions.OP_VAR_PATH.rootElementType(),
        IdlOpVarPath.class,
        IdlSubParserDefinitions.OP_VAR_PATH,
        errorsAccumulator
    );

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " +
                           EpigraphPsiUtil.getLocation(element));
      }
      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
      fail(psiDump);
    }

    OpVarPath varPath;
//    try {
    varPath = OpPathPsiParser.parseVarPath(
        varDataType,
        psiVarProjection,
        resolver
    );

//    } catch (PsiProcessingException e) {
//      e.printStackTrace();
//      System.err.println(e.getMessage() + " at " +
//                         EpigraphPsiUtil.getLocation(e.psi()));
//      String psiDump = DebugUtil.psiToString(psiVarProjection, true, false).trim();
//      fail(psiDump);
//    }
    return varPath;
  }

  private String print(OpVarPath path) {
    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpPathPrettyPrinter<NoExceptions> printer = new OpPathPrettyPrinter<>(layouter);
    printer.print(path, 0);
    layouter.close();
    return sb.getString();
  }

  private static String lines(String... lines) {
    return Arrays.stream(lines).collect(Collectors.joining("\n"));
  }

}
