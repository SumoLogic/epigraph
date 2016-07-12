/* Created by yegor on 7/6/16. */

package com.sumologic.epigraph.scala;

import com.sumologic.epigraph.schema.compiler.*;
import scala.collection.JavaConversions;

public class ScalaSchemaGenerator {

  private String[] args;

  private ScalaSchemaGenerator(String... args) {
    this.args = args;
  }

  public void generate() {
    SchemaCompilerMain.main(args);

    CContext ctx = SchemaCompilerMain.ctx();

    for (CNamespace namespace : ctx.namespaces().values()) {

      System.out.println(NamespaceGen.generate(namespace));

    }

    for (CSchemaFile schemaFile : ctx.schemaFiles().values()) {
      final CNamespace namespace = schemaFile.namespace();
      for (CTypeDef typeDef : JavaConversions.asJavaIterable(schemaFile.typeDefs())) {

        switch (typeDef.kind()) {

          case VARTYPE:
            break;

          case RECORD:
            System.out.println(RecordGen.generate((CRecordTypeDef) typeDef));

            break;

          case MAP:
            break;

          case LIST:
            System.out.println(ListGen.generate((CListTypeDef) typeDef));
            break;

          case ENUM:
            break;

          case STRING:
          case INTEGER:
          case LONG:
          case DOUBLE:
          case BOOLEAN:
            System.out.println(PrimitiveGen.generate((CPrimitiveTypeDef) typeDef));
            break;

          default:
            throw new UnsupportedOperationException(typeDef.kind().toString());
        }
      }
    }

  }

  public static void main(String... args) {
    new ScalaSchemaGenerator(args).generate();
  }


}
