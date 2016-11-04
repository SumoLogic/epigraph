package ws.epigraph.ideaplugin.schema;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import io.epigraph.schema.parser.SchemaFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaFileTypeFactory extends FileTypeFactory {
  @Override
  public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
    fileTypeConsumer.consume(SchemaFileType.INSTANCE, SchemaFileType.DEFAULT_EXTENSION);
  }
}
