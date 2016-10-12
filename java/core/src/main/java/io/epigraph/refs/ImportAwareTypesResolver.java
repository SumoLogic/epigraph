package io.epigraph.refs;

import io.epigraph.lang.DefaultImports;
import io.epigraph.lang.Qn;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
  private final TypesResolver typesResolver;

  public ImportAwareTypesResolver(@NotNull Qn currentNamespace,
                                  @NotNull List<Qn> imports,
                                  @NotNull TypesResolver typesResolver) {
    this.currentNamespace = currentNamespace;
    this.imports = imports;
    this.typesResolver = typesResolver;
  }

  @Nullable
  @Override
  public Type resolve(@NotNull QnTypeRef reference) {
    @NotNull final Qn qn = reference.qn();

    List<Qn> prefixes = calculateResolutionPrefixes(qn, currentNamespace, imports, false);
    if (prefixes == null) return null;

    return prefixes.stream()
                   .map(prefix -> prefix.append(qn))
                   .map(fqn -> typesResolver.resolve(new QnTypeRef(fqn)))
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
    return typesResolver.resolve(reference);
  }

  @Nullable
  @Override
  public Type resolve(@NotNull AnonMapRef reference) {
    return typesResolver.resolve(reference);
  }
}
