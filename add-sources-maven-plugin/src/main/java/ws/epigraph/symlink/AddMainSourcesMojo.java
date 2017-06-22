package ws.epigraph.symlink;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * "Generates" sources under ${project.build.directory}/generated-sources by sym-linking (or copying) configured source
 * directories.
 *
 * @author yegor 2017-06-21.
 */
@Mojo(name = "add-sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class AddMainSourcesMojo extends BaseAddSourcesMojo {

  @Parameter(defaultValue = "${project.build.directory}/generated-sources", required = true, readonly = true)
  private File sourcesHome;

  /** A list of <source><name>name</name><location>path</location></source> source directories to add. */
  @Parameter(required = true)
  private Source[] sources;

  @Override
  protected @NotNull File sourcesHome() { return sourcesHome; }

  @Override
  protected @NotNull Source[] sources() { return sources; }

  @Override
  protected void addToProject(MavenProject project, String path) { project.addCompileSourceRoot(path); }

}
