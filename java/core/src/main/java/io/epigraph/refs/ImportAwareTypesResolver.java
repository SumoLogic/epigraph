package io.epigraph.refs;

import io.epigraph.lang.DefaultImports;
import io.epigraph.lang.Qn;
import io.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    // see https://github.com/SumoLogic/epigraph/wiki/References%20implementation

    @NotNull final Qn qn = reference.qn();
    if (qn.isEmpty()) return null;

    String firstSegment = qn.first();
    assert firstSegment != null;

    List<Qn> prefixes = new ArrayList<>();

    prefixes.addAll(
        imports
            .stream()
            .filter(i -> firstSegment.equals(i.last()))
            .map(Qn::removeLastSegment)
            .collect(Collectors.toList())
    );

    if (qn.size() == 1) {
      prefixes.addAll(
          DefaultImports.DEFAULT_IMPORTS_LIST
              .stream()
              .filter(i -> firstSegment.equals(i.last()))
              .map(Qn::removeLastSegment)
              .collect(Collectors.toList())
      );

      prefixes.add(currentNamespace);
    } else {
      prefixes.add(Qn.EMPTY);
    }

    return prefixes.stream()
                   .map(prefix -> prefix.append(qn))
                   .map(fqn -> typesResolver.resolve(new QnTypeRef(fqn)))
                   .filter(Objects::nonNull)
                   .findFirst()
                   .orElse(null);
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
