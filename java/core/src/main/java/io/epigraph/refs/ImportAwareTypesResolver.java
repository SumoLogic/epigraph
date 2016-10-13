package io.epigraph.refs;

import io.epigraph.lang.DefaultImports;
import io.epigraph.lang.Qn;
import io.epigraph.types.DataType;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ImportAwareTypesResolver implements TypesResolver {
  @NotNull
  private final Qn currentNamespace;
  @NotNull
  private final List<Qn> imports;
  @NotNull
  private final TypesResolver childResolver;

  public ImportAwareTypesResolver(@NotNull Qn currentNamespace,
                                  @NotNull List<Qn> imports,
                                  @NotNull TypesResolver childResolver) {
    this.currentNamespace = currentNamespace;
    this.imports = imports;
    this.childResolver = childResolver;
  }

  @Nullable
  @Override
  public Type resolve(@NotNull QnTypeRef reference) {
    @NotNull final Qn qn = reference.qn();

    List<Qn> prefixes = calculateResolutionPrefixes(qn, currentNamespace, imports, false);
    if (prefixes == null) return null;

    return prefixes.stream()
                   .map(prefix -> prefix.append(qn))
                   .map(fqn -> childResolver.resolve(new QnTypeRef(fqn)))
                   .filter(Objects::nonNull)
                   .findFirst()
                   .orElse(null);
  }

  @Nullable
  public static List<Qn> calculateResolutionPrefixes(@NotNull Qn reference,
                                                     @Nullable Qn currentNamespace,
                                                     @NotNull List<Qn> imports,
                                                     boolean deduplicate) {

    // see https://github.com/SumoLogic/epigraph/wiki/References%20implementation

    // move this to a separate class? This method is also used by idea-plugin

    if (reference.isEmpty()) return null;

    String firstSegment = reference.first();
    assert firstSegment != null;

    List<Qn> prefixes = new ArrayList<>();

    prefixes.addAll(
        imports
            .stream()
            .filter(i -> firstSegment.equals(i.last()))
            .map(Qn::removeLastSegment)
            .collect(Collectors.toList())
    );

    if (reference.size() == 1) {
      prefixes.addAll(
          DefaultImports.DEFAULT_IMPORTS_LIST
              .stream()
              .filter(i -> firstSegment.equals(i.last()))
              .map(Qn::removeLastSegment)
              .collect(Collectors.toList())
      );

      if (currentNamespace != null) prefixes.add(currentNamespace);
    } else {
      prefixes.add(Qn.EMPTY);
    }

    if (deduplicate) {
      LinkedHashSet<Qn> dedup = new LinkedHashSet<>(prefixes);
      prefixes.clear();
      prefixes.addAll(dedup);
    }

    return prefixes;
  }

  @Nullable
  @Override
  public Type resolve(@NotNull AnonListRef reference) {
    @NotNull ValueTypeRef itemTypeRef = reference.itemsType();
    @Nullable DataType itemType = resolve(itemTypeRef);
    if (itemType == null) return null;

    @NotNull final AnonListRef normalizedRef = TypeReferenceFactory.createAnonListReference(itemType);
    return childResolver.resolve(normalizedRef);
  }

  @Nullable
  @Override
  public Type resolve(@NotNull AnonMapRef reference) {
    @NotNull ValueTypeRef valueTypeRef = reference.itemsType();
    @Nullable DataType valueType = resolve(valueTypeRef);
    if (valueType == null) return null;

    @Nullable Type keyType = reference.keysType().resolve(this);
    if (keyType instanceof DatumType) {
      DatumType keyDatumType = (DatumType) keyType;
      @NotNull final AnonMapRef normalizedRef = TypeReferenceFactory.createAnonMapReference(keyDatumType, valueType);
      return childResolver.resolve(normalizedRef);
    } else return null;
  }
}
