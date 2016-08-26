package com.sumologic.epigraph.ideaplugin.idl;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import io.epigraph.lang.idl.parser.IdlFileType;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlFileTypeFactory extends FileTypeFactory {
  @Override
  public void createFileTypes(@NotNull FileTypeConsumer consumer) {
   consumer.consume(IdlFileType.INSTANCE, IdlFileType.DEFAULT_EXTENSION);
  }
}
