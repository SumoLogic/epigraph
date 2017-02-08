/* Created by yegor on 8/22/16. */

package ws.epigraph.mavenizer;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;

import java.io.File;

@Mojo(name = "compile", defaultPhase = LifecyclePhase.COMPILE, requiresProject = true, threadSafe = true)
public class CompileMojo extends AbstractMojo {

  @Parameter(required = true)
  private File sourceJar;

  /**
   * Directory where source jar will be unpacked for further repackaging.
   */
  @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
  private File classesDirectory;

  @Component(role = UnArchiver.class, hint = "jar") // TODO get it from archive manager instead?
  private ZipUnArchiver jarUnArchiver;

  /**
   * The {@link MavenSession}.
   */
  @Parameter(defaultValue = "${session}", readonly = true, required = true)
  private MavenSession session;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    //archiverManager.getUnArchiver(sourceJar);
    jarUnArchiver.setSourceFile(sourceJar);
    jarUnArchiver.setDestDirectory(classesDirectory);
    classesDirectory.mkdirs(); // TODO check it returns `true`?
    getLog().info("Unpacking '" + sourceJar.getAbsolutePath() + "' to '" + classesDirectory.getAbsolutePath() + "'");
    jarUnArchiver.extract();
  }

}
