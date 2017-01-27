/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 7/6/16. */

package ws.epigraph.java;

import scala.collection.JavaConversions;
import ws.epigraph.compiler.*;
import ws.epigraph.java.projections.req.output.ReqOutputListModelProjectionGen;
import ws.epigraph.java.projections.req.output.ReqOutputMapModelProjectionGen;
import ws.epigraph.java.projections.req.output.ReqOutputRecordModelProjectionGen;
import ws.epigraph.java.projections.req.output.ReqOutputVarProjectionGen;
import ws.epigraph.java.service.AbstractResourceFactoryGen;
import ws.epigraph.java.service.ResourceDeclarationGen;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.ResourceDeclaration;
import ws.epigraph.schema.ResourcesSchema;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class EpigraphJavaGenerator {

  private final CContext cctx;

  private final GenContext ctx;

  private final Path outputRoot;

  public EpigraphJavaGenerator(CContext ctx, Path outputRoot, GenSettings settings) {
    this.cctx = ctx;
    this.outputRoot = outputRoot;
    this.ctx = new GenContext(settings);
  }

  public EpigraphJavaGenerator(CContext ctx, File outputRoot, GenSettings settings) {
    this(ctx, outputRoot.toPath(), settings);
  }

  public void generate() throws IOException {

    Path tmpRoot = JavaGenUtils.rmrf(
        outputRoot.resolveSibling(outputRoot.getFileName().toString() + "~tmp"),
        outputRoot.getParent()
    );

//    for (CNamespace namespace : ctx.namespaces().values()) {
//      new NamespaceGen(namespace, ctx).writeUnder(tmpRoot);
//    }

    // TODO only generate request projection classes if there are any resources defined ?

    // TODO parallelize all these?

    for (CSchemaFile schemaFile : cctx.schemaFiles().values()) {
      for (CTypeDef typeDef : JavaConversions.asJavaIterable(schemaFile.typeDefs())) {

        try {
          switch (typeDef.kind()) {

            case VARTYPE:
              final CVarTypeDef varTypeDef = (CVarTypeDef) typeDef;
              new VarTypeGen(varTypeDef, ctx).writeUnder(tmpRoot);
              new ReqOutputVarProjectionGen(varTypeDef, ctx).writeUnder(tmpRoot);
              break;

            case RECORD:
              final CRecordTypeDef recordTypeDef = (CRecordTypeDef) typeDef;
              new RecordGen(recordTypeDef, ctx).writeUnder(tmpRoot);
              new ReqOutputRecordModelProjectionGen(recordTypeDef, ctx).writeUnder(tmpRoot);
              break;

            case MAP:
              final CMapTypeDef mapTypeDef = (CMapTypeDef) typeDef;
              new NamedMapGen(mapTypeDef, ctx).writeUnder(tmpRoot);
              new ReqOutputMapModelProjectionGen(mapTypeDef, ctx).writeUnder(tmpRoot);
              break;

            case LIST:
              final CListTypeDef listTypeDef = (CListTypeDef) typeDef;
              new NamedListGen(listTypeDef, ctx).writeUnder(tmpRoot);
              new ReqOutputListModelProjectionGen(listTypeDef, ctx).writeUnder(tmpRoot);
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
        } catch (CompilerException ignored) {
          // keep going
        }
      }
    }

    for (CAnonListType alt : cctx.anonListTypes().values()) {
      try {
        //System.out.println(alt.name().name());
        new AnonListGen(alt, ctx).writeUnder(tmpRoot);
        new ReqOutputListModelProjectionGen(alt, ctx).writeUnder(tmpRoot);
      } catch (CompilerException ignored) {
      }
    }

    for (CAnonMapType amt : cctx.anonMapTypes().values()) {
      try {
        //System.out.println(amt.name().name());
        new AnonMapGen(amt, ctx).writeUnder(tmpRoot);
        new ReqOutputMapModelProjectionGen(amt, ctx).writeUnder(tmpRoot);
      } catch (CompilerException ignored) {
      }
    }

    handleErrors();

//    final Set<CDataType> anonMapValueTypes = new HashSet<>();
//    for (CAnonMapType amt : ctx.anonMapTypes().values()) {
//      anonMapValueTypes.add(amt.valueDataType());
//    }
//    for (CDataType valueType : anonMapValueTypes) {
//      new AnonBaseMapGen(valueType, ctx).writeUnder(tmpRoot);
//    }

    new IndexGen(ctx).writeUnder(tmpRoot);
    final GenSettings settings = ctx.settings();

    for (final Map.Entry<CSchemaFile, ResourcesSchema> entry : cctx.resourcesSchemas().entrySet()) {
      ResourcesSchema rs = entry.getValue();

      Qn namespace = rs.namespace();

      for (final ResourceDeclaration resourceDeclaration : rs.resources().values()) {
        new ResourceDeclarationGen(resourceDeclaration).writeUnder(tmpRoot, namespace, ctx);

        String resourceName = namespace.append(JavaGenUtils.up(resourceDeclaration.fieldName())).toString();

        // change them to be patters/regex?
        if (settings.generateImplementationStubs() == null ||
            settings.generateImplementationStubs().contains(resourceName)) {

          new AbstractResourceFactoryGen(resourceDeclaration).writeUnder(tmpRoot, namespace, ctx);
        }

      }

    }

    JavaGenUtils.move(tmpRoot, outputRoot, outputRoot.getParent()); // move new root to final location

  }

//  public static void main(String... args) throws IOException {
//    new EpigraphJavaGenerator(
//        SchemaCompiler.testcompile(),
//        Paths.get("java/codegen-test/src/main/java")
//    ).generate();
//  }

  private void handleErrors() {
    if (!cctx.errors().isEmpty()) {
      EpigraphCompiler.renderErrors(cctx);
      System.exit(10);
    }
  }

}
