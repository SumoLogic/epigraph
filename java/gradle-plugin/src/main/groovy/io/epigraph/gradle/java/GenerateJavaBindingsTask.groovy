package io.epigraph.gradle.java

import com.sumologic.epigraph.java.JavaSchemaGenerator
import io.epigraph.gradle.EmptyFileTree
import io.epigraph.gradle.EpigraphSchemaTaskBase
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.ParallelizableTask
import org.gradle.api.tasks.TaskAction

@ParallelizableTask
class GenerateJavaBindingsTask extends DefaultTask implements EpigraphSchemaTaskBase {
  private String sourceSetName
  private File destinationDir

  GenerateJavaBindingsTask() {}

  @TaskAction
  public void run() {
    destinationDir.mkdirs()

    def context = compileSchemaFiles();
    if (!context.errors().isEmpty())
      throw new GradleException('Epigraph schema compilation failed with errors')

    getLogger().info("Generating Java bindings to '${getDestinationDir()}'")
    new JavaSchemaGenerator(context, getDestinationDir()).generate()
  }

  @Override
  FileTree getSource() {
    return EmptyFileTree.INSTANCE // we only generate from dependencies
  }

  @Internal
  void setSourceSetName(String sourceSetName) {
    this.sourceSetName = sourceSetName
  }

  @OutputDirectory
  public File getDestinationDir() {
    if (destinationDir == null) {
      if (sourceSetName == null)
        throw new GradleException('Neither destination dir nor source name is set')

      getLogger().debug('Using default destination dir')
      def generatedSrcDir = new File(project.buildDir, 'generated-src')
      def javaGeneratedSrcDir = new File(generatedSrcDir, 'epigraph.java')
      setDestinationDir(new File(javaGeneratedSrcDir, sourceSetName))
    }

    return destinationDir
  }

  public void setDestinationDir(File destinationDir) {
    this.destinationDir = destinationDir
  }
}
