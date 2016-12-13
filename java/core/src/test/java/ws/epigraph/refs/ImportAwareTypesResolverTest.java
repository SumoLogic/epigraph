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

package ws.epigraph.refs;

import ws.epigraph.data.Data;
import ws.epigraph.data.PrimitiveDatum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.lang.Qn;
import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class ImportAwareTypesResolverTest {
  @Test
  public void testSameNamespace() {
    Type t1 = createType("T1", "foo", "bar");

    SimpleTypesResolver simpleResolver = new SimpleTypesResolver(
        t1
    );

    // sanity check
    assertEquals(t1, simpleResolver.resolve(new QnTypeRef(Qn.fromDotSeparated("foo.bar.T1"))));

    ImportAwareTypesResolver importAwareResolver = new ImportAwareTypesResolver(
        Qn.fromDotSeparated("foo.bar"),
        Collections.emptyList(),
        simpleResolver
    );

    assertEquals(t1, importAwareResolver.resolve(new QnTypeRef(new Qn("T1"))));
  }

  @Test
  public void testShortImported() {
    Type t1 = createType("T1", "foo", "bar");

    SimpleTypesResolver simpleResolver = new SimpleTypesResolver(
        t1
    );

    ImportAwareTypesResolver importAwareResolver = new ImportAwareTypesResolver(
        Qn.fromDotSeparated("qux"),
        Collections.singletonList(Qn.fromDotSeparated("foo.bar.T1")),
        simpleResolver
    );

    assertEquals(t1, importAwareResolver.resolve(new QnTypeRef(new Qn("T1"))));
  }

  @Test
  public void testLongImported() {
    Type t1 = createType("T1", "foo", "bar");

    SimpleTypesResolver simpleResolver = new SimpleTypesResolver(
        t1
    );

    ImportAwareTypesResolver importAwareResolver = new ImportAwareTypesResolver(
        Qn.fromDotSeparated("qux"),
        Collections.singletonList(Qn.fromDotSeparated("foo.bar")),
        simpleResolver
    );

    assertEquals(t1, importAwareResolver.resolve(new QnTypeRef(new Qn("bar", "T1"))));
  }

  @Test
  public void testFullyQualified() {
    Type t1 = createType("T1", "foo", "bar");

    SimpleTypesResolver simpleResolver = new SimpleTypesResolver(
        t1
    );

    ImportAwareTypesResolver importAwareResolver = new ImportAwareTypesResolver(
        Qn.fromDotSeparated("qux"),
        Collections.emptyList(),
        simpleResolver
    );

    assertEquals(t1, importAwareResolver.resolve(new QnTypeRef(new Qn("foo", "bar", "T1"))));
  }

  @Test
  public void testExplicit() {
    Type t1 = createType("String", "epigraph");

    SimpleTypesResolver simpleResolver = new SimpleTypesResolver(
        t1
    );

    ImportAwareTypesResolver importAwareResolver = new ImportAwareTypesResolver(
        Qn.fromDotSeparated("qux"),
        Collections.emptyList(),
        simpleResolver
    );

    assertEquals(t1, importAwareResolver.resolve(new QnTypeRef(new Qn("String"))));
  }

  @Test
  public void testAnonList() {
    DatumType t1 = createType("T1", "foo", "bar");
    AnonListType lt = createAnonListType(t1);

    SimpleTypesResolver simpleResolver = new SimpleTypesResolver(
        t1, lt
    );

    ImportAwareTypesResolver importAwareResolver = new ImportAwareTypesResolver(
        Qn.fromDotSeparated("foo.bar"),
        Collections.emptyList(),
        simpleResolver
    );

    assertEquals(
        lt,
        importAwareResolver.resolve(
            new AnonListRef(
                new ValueTypeRef(
                    new QnTypeRef(new Qn("T1")),
                    null
                )
            )
        )
    );
  }

  @Test
  public void testAnonMap() {
    DatumType t1 = createType("T1", "foo", "bar");
    DatumType t2 = createType("T2", "foo", "bar");

    AnonMapType mt = createAnonMapType(t1, t2);

    SimpleTypesResolver simpleResolver = new SimpleTypesResolver(
        t1, t2, mt
    );

    ImportAwareTypesResolver importAwareResolver = new ImportAwareTypesResolver(
        Qn.fromDotSeparated("foo.bar"),
        Collections.emptyList(),
        simpleResolver
    );

    assertEquals(
        mt,
        importAwareResolver.resolve(
            new AnonMapRef(
                new QnTypeRef(new Qn("T1")),
                new ValueTypeRef(
                    new QnTypeRef(new Qn("T2")),
                    null
                )
            )
        )
    );
  }

  private static @NotNull DatumType createType(@NotNull String localName, String... namespaceNames) {
    return new PrimitiveType<String>(
        new QualifiedTypeName(localName, namespaceNames),
        Collections.emptyList()
    ) {
      @Override
      public @NotNull Data.Builder createDataBuilder() { return null; }

      @Override
      public @NotNull Val.Imm createValue(@Nullable ErrorValue errorOrNull) { return null; }

      @Override
      public @NotNull PrimitiveDatum.Builder<String> createBuilder(@NotNull String val) { return null; }
    };
  }

  private static AnonListType createAnonListType(DatumType elementType) {
    return new AnonListType.Raw(new DataType(elementType, elementType.self));
  }

  private static AnonMapType createAnonMapType(DatumType keyType, DatumType itemType) {
    return new AnonMapType.Raw(keyType, new DataType(itemType, itemType.self));
  }
}
