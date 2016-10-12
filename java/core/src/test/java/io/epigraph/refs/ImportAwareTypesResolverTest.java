package io.epigraph.refs;

import io.epigraph.data.Data;
import io.epigraph.data.PrimitiveDatum;
import io.epigraph.data.Val;
import io.epigraph.errors.ErrorValue;
import io.epigraph.lang.Qn;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.types.PrimitiveType;
import io.epigraph.types.Type;
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

  private static Type createType(String localName, String... namespaceNames) {
    return new PrimitiveType<String>(
        new QualifiedTypeName(localName, namespaceNames),
        Collections.emptyList()
    ) {
      @Override
      public Data.Builder createDataBuilder() { return null; }

      @Override
      public Val.Imm createValue(@Nullable ErrorValue errorOrNull) { return null; }

      @NotNull
      @Override
      public PrimitiveDatum.Builder<String> createBuilder(String val) { return null; }
    };
  }
}
