package ws.epigraph.symlink;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * "Generates" test sources under ${project.build.directory}/generated-test-sources by sym-linking (or copying)
 * configured test-source directories.
 *
 * @author yegor 2017-06-21.
 */
@Mojo(name = "add-test-sources", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES, threadSafe = true)
public class AddTestSourcesMojo extends BaseAddSourcesMojo {

  @Parameter(defaultValue = "${project.build.directory}/generated-test-sources", required = true, readonly = true)
  private File sourcesHome;

  @Parameter(name = "test-sources", required = true)
  private Source[] testSources;

  @Override
  protected @NotNull File sourcesHome() { return sourcesHome; }

  @Override
  protected @NotNull Source[] sources() { return testSources; }

  @Override
  protected void addToProject(MavenProject project, String path) { project.addCompileSourceRoot(path); }

}
