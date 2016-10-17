package io.epigraph.idl;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.impl.DebugUtil;
import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import io.epigraph.idl.parser.IdlParserDefinition;
import io.epigraph.idl.parser.psi.IdlFile;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.SimpleTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.tests.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlTest {
  private final TypesResolver resolver = new SimpleTypesResolver(
      PersonId.type,
      Person.type,
      User.type,
      UserId.type,
      UserRecord.type,
      String_Person_Map.type,
      epigraph.String.type
  );

  @Test
  public void testEmpty() throws Exception {
    testParse(
        lines(
            "namespace test",
            "import io.epigraph.tests.Person"
        ),
        "namespace test"
    );
  }

  @Test
  public void testEmptyResource() throws Exception {
    testParse(
        lines(
            "namespace io.epigraph.tests",
            "resource users: map[String,Person] { }"
        ),
        lines(
            "namespace io.epigraph.tests",
            "resource users: map[epigraph.String,io.epigraph.tests.Person] { }"
        )
    );
  }

  @Test
  public void testResource() throws Exception {
    testParse(
        lines(
            "namespace test",
            "import io.epigraph.tests.Person",
            "resource users : map[String,Person] {",
            "  READ {",
            "    doc = \"dome doc string\"",
            "    ; authToken : String",
            "    output [required]( :record (id, firstName) )",
            "  }",
            "  UPDATE {",
            "    doc = \"dome doc string\"",
            "    ; authToken : String",
            "    input []( :record ( firstName, lastName) )",
            "    output [forbidden]( :id )",
            "  }",
            "  customUpdate UPDATE {",
            "    doc = \"dome doc string\"",
            "    ; authToken : String",
            "    input []( :record ( firstName, lastName) )",
            "    output [forbidden]( :id )",
            "  }",
            "}"
        ),
        lines(
            "namespace test",
            "resource users: map[epigraph.String,io.epigraph.tests.Person]",
            "{",
            "  READ",
            "  { ;authToken: epigraph.String, doc = \"dome doc string\",",
            "    output [ required ]( :record ( id, firstName ) )",
            "  }",
            "  UPDATE",
            "  { ;authToken: epigraph.String, doc = \"dome doc string\",",
            "    input []( :record ( firstName, lastName ) ),",
            "    output [ forbidden ]( :id )",
            "  }",
            "  customUpdate UPDATE",
            "  { ;authToken: epigraph.String, doc = \"dome doc string\",",
            "    input []( :record ( firstName, lastName ) ),",
            "    output [ forbidden ]( :id )",
            "  } }"
        )
    );
  }

  private void testParse(String str) throws IOException {
    testParse(str, str);
  }

  private void testParse(String idlStr, String expected) throws IOException {
    Idl idl = parseFile(idlStr);

    StringBackend sb = new StringBackend(80);
    Layouter<NoExceptions> l = new Layouter<>(sb, 2);
    IdlPrettyPrinter<NoExceptions> pp = new IdlPrettyPrinter<>(l);
    pp.print(idl);
    l.close();

    String s = sb.getString();
    assertEquals(expected, s);
  }

  private Idl parseFile(String text) throws IOException {
    EpigraphPsiUtil.ErrorsAccumulator errorsAccumulator = new EpigraphPsiUtil.ErrorsAccumulator();

    @NotNull IdlFile psiFile =
        (IdlFile) EpigraphPsiUtil.parseFile("idlTest.idl", text, IdlParserDefinition.INSTANCE, errorsAccumulator);

    if (errorsAccumulator.hasErrors()) {
      for (PsiErrorElement element : errorsAccumulator.errors()) {
        System.err.println(element.getErrorDescription() + " at " + EpigraphPsiUtil.getLocation(element));
      }
      fail(DebugUtil.psiTreeToString(psiFile, true));
    }

    try {
      return IdlPsiParser.parseIdl(psiFile, resolver);
    } catch (PsiProcessingException e) {
      e.printStackTrace();
      System.err.println(e.getMessage() + " at " + EpigraphPsiUtil.getLocation(e.psi()));
      fail();
    }

    throw new RuntimeException("Unreachable");
  }

  private static String lines(String... lines) { return Arrays.stream(lines).collect(Collectors.joining("\n")); }
}
