package io.epigraph.gradle.schema

import io.epigraph.gradle.EpigraphConstants
import io.epigraph.gradle.EpigraphSchemaTaskBase
import org.gradle.api.GradleException
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.StandardCopyOption

@ParallelizableTask
class CompileSchemaTask extends SourceTask implements EpigraphSchemaTaskBase {
  private File destinationDir;

  CompileSchemaTask() {}

  /**
   * Returns the directory to put verified files into.
   *
   * @return The destination directory.
   */
  @OutputDirectory
  public File getDestinationDir() {
    return destinationDir;
  }

  void setDestinationDir(File destinationDir) {
    this.destinationDir = destinationDir
  }

  @TaskAction
  public void run() {
    def context = compileSchemaFiles();

    if (!context.errors().isEmpty())
      throw new GradleException('Epigraph schema compilation failed with errors')

    def artifactsDir = new File(destinationDir, EpigraphConstants.ARTIFACTS_PATH_PREFIX).getCanonicalFile()
    artifactsDir.delete()

    // see BaseCompileMojo
    getFileSources().each { fileSource ->
      def sourceFileName = fileSource.name() // this is full path actually
      def sourceFile = new File(sourceFileName)
      def schemaFile = context.schemaFiles().get(sourceFileName)
      assert schemaFile != null

      def relativePath = schemaFile.namespace().fqn().segments.join(File.separator)
      def targetDir = new File(artifactsDir, relativePath)

      targetDir.mkdirs()
      Files.copy(
          sourceFile.toPath(),
          targetDir.toPath().resolve(sourceFile.name),
          StandardCopyOption.REPLACE_EXISTING
      )

//      getLogger().info("Copying ${sourceFile.toPath()} -> ${targetDir.toPath().resolve(sourceFile.name)}")
    }
  }
}
