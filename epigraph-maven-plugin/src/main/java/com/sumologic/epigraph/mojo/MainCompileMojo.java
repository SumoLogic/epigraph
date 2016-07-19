/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.mojo;

import com.sumologic.epigraph.schema.compiler.*;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import scala.Option;

import java.io.File;
import java.io.IOException;

import java.util.Collection;

/**
 * Compile Epigraph source files (.esc)
 */
@Mojo(
    name = "compile",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.COMPILE,
    threadSafe = true
)
public class MainCompileMojo extends BaseCompileMojo {

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.esc</code> is used to select epigraph schema files.
   */
  @Parameter
  private String[] includes = new String[]{"**/*.esc"};

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.esc</code> is used to select epigraph schema files.
   */
  @Parameter
  private String[] testIncludes = new String[]{"**/*.esc"};

  @Override
  protected void doCompile(File outputDirectory, Collection<Source> sources, Collection<Source> dependencySources)
      throws IOException, MojoFailureException {

    final SchemaCompiler compiler = new SchemaCompiler(sources, dependencySources);
    try {
      CContext ctx = compiler.compile();
    } catch (SchemaCompilerException failure) {
      StringBuilder sb = new StringBuilder();
      for (CError err : compiler.ctx().errors()) {
        final CErrorPosition pos = err.position(); // TODO skip :line:colon, line text, and ^ if NA
        sb.append(err.filename()).append(':').append(pos.line()).append(':').append(pos.column()).append('\n');
        sb.append("Error: ").append(err.message()).append('\n');
        final Option<String> errText = pos.lineText();
        if (errText.nonEmpty()) {
          sb.append(errText.get()).append('\n');
          sb.append(String.format("%" + (pos.column()) + "s", "^").replace(" ", ".")).append('\n');
        }
      }
      throw new MojoFailureException(this, "Schema compilation failed", sb.toString());
    }

    // TODO catch and sort compiler exceptions into MojoExecutionException (abnormal) and MojoFailureException (normal failure)

//    System.out.println(ctx.errors().size());

//    SpecificCompiler compiler = new SpecificCompiler(schema);
//    compiler.setTemplateDir(templateDirectory);
//    compiler.setStringType(StringType.valueOf(stringType));
//    compiler.setFieldVisibility(getFieldVisibility());
//    compiler.setCreateSetters(createSetters);
//    compiler.setEnableDecimalLogicalType(enableDecimalLogicalType);
//    compiler.setOutputCharacterEncoding(project.getProperties().getProperty("project.build.sourceEncoding"));
//    compiler.compileToDestination(src, outputDirectory);
  }

  @Override
  protected String[] getIncludes() {
    return includes;
  }

  @Override
  protected String[] getTestIncludes() {
    return testIncludes;
  }

}
