/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.scala;

import com.sumologic.epigraph.schema.compiler.*;
import scala.collection.JavaConversions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScalaSchemaGenerator {

  private String[] args;

  private final Path outputRoot = Paths.get("target", "generated-sources", "epigraph");

  private ScalaSchemaGenerator(String... args) {
    this.args = args;
  }

  public void generate() throws IOException {
    SchemaCompilerMain.main(args);

    CContext ctx = SchemaCompilerMain.ctx();

    Path tmpRoot = GenUtils.rmrf(outputRoot.resolveSibling(outputRoot.getFileName().toString() + "~tmp"));

    for (CNamespace namespace : ctx.namespaces().values()) {
      new NamespaceGen(namespace).writeUnder(tmpRoot);
    }

    for (CSchemaFile schemaFile : ctx.schemaFiles().values()) {
      for (CTypeDef typeDef : JavaConversions.asJavaIterable(schemaFile.typeDefs())) {

        switch (typeDef.kind()) {

          case VARTYPE:
            break;

          case RECORD:
            new RecordGen((CRecordTypeDef) typeDef).writeUnder(tmpRoot);

            break;

          case MAP:
            break;

          case LIST:
            new ListGen((CListTypeDef) typeDef).writeUnder(tmpRoot);
            break;

          case ENUM:
            break;

          case STRING:
          case INTEGER:
          case LONG:
          case DOUBLE:
          case BOOLEAN:
            new PrimitiveGen((CPrimitiveTypeDef) typeDef).writeUnder(tmpRoot);
            break;

          default:
            throw new UnsupportedOperationException(typeDef.kind().toString());
        }
      }
    }

    GenUtils.move(tmpRoot, outputRoot); // move new root to final location

  }

  public static void main(String... args) throws IOException {
    new ScalaSchemaGenerator(args).generate();
  }


}
