/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.java;

import com.sumologic.epigraph.schema.compiler.*;
import scala.collection.JavaConversions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaSchemaGenerator {

  private final CContext ctx;

  private final Path outputRoot;

  public JavaSchemaGenerator(CContext ctx, Path outputRoot) {
    this.ctx = ctx;
    this.outputRoot = outputRoot;
  }

  public JavaSchemaGenerator(CContext ctx, File outputRoot) {
    this(ctx, outputRoot.toPath());
  }

  public void generate() throws IOException {

    Path tmpRoot = GenUtils.rmrf(outputRoot.resolveSibling(outputRoot.getFileName().toString() + "~tmp"), outputRoot.getParent());

    for (CNamespace namespace : ctx.namespaces().values()) {
      new NamespaceGen(namespace, ctx).writeUnder(tmpRoot);
    }

    for (CSchemaFile schemaFile : ctx.schemaFiles().values()) {
      for (CTypeDef typeDef : JavaConversions.asJavaIterable(schemaFile.typeDefs())) {

        switch (typeDef.kind()) {

          case VARTYPE:
            //new VarTypeGen((CVarTypeDef) typeDef, ctx).writeUnder(tmpRoot);
            break;

          case RECORD:
            new RecordGen((CRecordTypeDef) typeDef, ctx).writeUnder(tmpRoot);
            break;

          case MAP:
            break;

          case LIST:
            new ListGen((CListTypeDef) typeDef, ctx).writeUnder(tmpRoot);
            break;

          case ENUM:
            break;

          case STRING:
          case INTEGER:
          case LONG:
          case DOUBLE:
          case BOOLEAN:
            new PrimitiveGen((CPrimitiveTypeDef) typeDef, ctx).writeUnder(tmpRoot);
            break;

          default:
            throw new UnsupportedOperationException(typeDef.kind().toString());
        }
      }
    }

    GenUtils.move(tmpRoot, outputRoot, outputRoot.getParent()); // move new root to final location

  }

  public static void main(String... args) throws IOException {
    new JavaSchemaGenerator(
        SchemaCompiler.testcompile(),
        Paths.get("epigraph-java-codegen-test/src/test/epigraph-java")
    ).generate();
  }


}
