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

import ws.epigraph.compiler.*;
import scala.collection.JavaConversions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class EpigraphJavaGenerator {

  private final CContext cctx;

  private final GenContext ctx = new GenContext();

  private final Path outputRoot;

  public EpigraphJavaGenerator(CContext ctx, Path outputRoot) {
    this.cctx = ctx;
    this.outputRoot = outputRoot;
  }

  public EpigraphJavaGenerator(CContext ctx, File outputRoot) { this(ctx, outputRoot.toPath()); }

  public void generate() throws IOException {

    Path tmpRoot = JavaGenUtils.rmrf(outputRoot.resolveSibling(outputRoot.getFileName().toString() + "~tmp"), outputRoot.getParent());

//    for (CNamespace namespace : ctx.namespaces().values()) {
//      new NamespaceGen(namespace, ctx).writeUnder(tmpRoot);
//    }

    // TODO parallelize all these?

    for (CEdlFile edlFile : cctx.edlFiles().values()) {
      for (CTypeDef typeDef : JavaConversions.asJavaIterable(edlFile.typeDefs())) {

        switch (typeDef.kind()) {

          case VARTYPE:
            new VarTypeGen((CVarTypeDef) typeDef, ctx).writeUnder(tmpRoot);
            break;

          case RECORD:
            new RecordGen((CRecordTypeDef) typeDef, ctx).writeUnder(tmpRoot);
            break;

          case MAP:
            new NamedMapGen((CMapTypeDef) typeDef, ctx).writeUnder(tmpRoot);
            break;

          case LIST:
            new NamedListGen((CListTypeDef) typeDef, ctx).writeUnder(tmpRoot);
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
    
    for (CAnonListType alt : cctx.anonListTypes().values()) {
      //System.out.println(alt.name().name());
      new AnonListGen(alt, ctx).writeUnder(tmpRoot);
    }

    for (CAnonMapType amt : cctx.anonMapTypes().values()) {
      //System.out.println(amt.name().name());
      new AnonMapGen(amt, ctx).writeUnder(tmpRoot);
    }

//    final Set<CDataType> anonMapValueTypes = new HashSet<>();
//    for (CAnonMapType amt : ctx.anonMapTypes().values()) {
//      anonMapValueTypes.add(amt.valueDataType());
//    }
//    for (CDataType valueType : anonMapValueTypes) {
//      new AnonBaseMapGen(valueType, ctx).writeUnder(tmpRoot);
//    }

    new IndexGen(ctx).writeUnder(tmpRoot);

    JavaGenUtils.move(tmpRoot, outputRoot, outputRoot.getParent()); // move new root to final location

  }

//  public static void main(String... args) throws IOException {
//    new EpigraphJavaGenerator(
//        EdlCompiler.testcompile(),
//        Paths.get("java/codegen-test/src/main/java")
//    ).generate();
//  }

}
