package ws.epigraph.ideaplugin.schema.brains;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import io.epigraph.lang.Qn;
import io.epigraph.schema.parser.psi.SchemaFile;
import io.epigraph.schema.parser.psi.SchemaImportStatement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class ImportsManagerTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains";
  }

  public void testFindUnusedImports() {
    myFixture.configureByFiles("UnusedImports.esc", "foo.esc", "foobar.esc");
    Set<SchemaImportStatement> unusedImports = ImportsManager.findUnusedImports((SchemaFile) myFixture.getFile());

    Set<String> strings = unusedImports.stream().map(SchemaImportStatement::getText).collect(Collectors.toSet());
    assertEquals(new HashSet<>(Arrays.asList("import foo", "import foo.Y", "import foo.Z")), strings);
  }

  public void testOptimizeImports() {
    myFixture.configureByFiles("UnusedImports.esc", "foo.esc", "foobar.esc");
    List<Qn> optimizedImports = ImportsManager.getOptimizedImports((SchemaFile) myFixture.getFile());

    assertEquals(Arrays.asList(
        new Qn[]{
            Qn.fromDotSeparated("foo.X"),
            Qn.fromDotSeparated("foo.bar")
        }
    ), optimizedImports);

  }
}
