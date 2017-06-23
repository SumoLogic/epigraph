package ws.epigraph.symlink;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author yegor 2017-06-21.
 */
public abstract class BaseAddSourcesMojo extends AbstractMojo {

  /** The {@link {MavenProject}. */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  protected MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    Path sourcesHome = sourcesHome().toPath();
    try {
      Files.createDirectories(sourcesHome);
      for (Source source : sources()) {
        addToProject(project, link(sourcesHome, source).toString());
        getLog().info(String.format("Source directory: %s added.", source.location));
      }
    } catch (IOException e) {
      throw new MojoExecutionException(String.format("Error adding sources: %s", e.toString()), e);
    }
  }

  private static @NotNull Path link(Path sourcesHome, Source source) throws IOException {
    Path linkPath = sourcesHome.resolve(source.name);
    Path locationPath = source.location.toPath(); // TODO check it's an existing directory?
    try {
      return Files.createSymbolicLink(linkPath, locationPath);
    } catch (UnsupportedOperationException e) {
      throw e; // TODO copy location recursively instead of linking?
    } catch (FileAlreadyExistsException e) {
      Path existingLinkPath = Files.readSymbolicLink(linkPath);
      if (existingLinkPath.equals(locationPath)) return linkPath;
      throw e;
    }
  }

  protected abstract @NotNull File sourcesHome();

  protected abstract @NotNull Source[] sources();

  protected abstract void addToProject(MavenProject project, String path);


  /** Structure that holds name for and location of a source directory to add to the project. */
  public static class Source {

    /** Name for the added sources directory */
    String name; // TODO alias? label?

    /** Path (from project base directory) to the imported sources directory */
    File location;

  }


}
