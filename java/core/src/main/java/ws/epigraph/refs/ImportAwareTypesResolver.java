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

import ws.epigraph.lang.DefaultImports;
import ws.epigraph.lang.Qn;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ImportAwareTypesResolver implements TypesResolver {
  private final @NotNull Qn currentNamespace;
  private final @NotNull List<Qn> imports;
  private final @NotNull TypesResolver childResolver;

  public ImportAwareTypesResolver(@NotNull Qn currentNamespace,
                                  @NotNull List<Qn> imports,
                                  @NotNull TypesResolver childResolver) {
    this.currentNamespace = currentNamespace;
    this.imports = imports;
    this.childResolver = childResolver;
  }

  public @NotNull TypesResolver childResolver() { return childResolver; }

  @Override
  public @Nullable TypeApi resolve(@NotNull QnTypeRef reference) {
    final @NotNull Qn qn = reference.qn();

    List<Qn> prefixes = calculateResolutionPrefixes(qn, currentNamespace, imports, false);
    if (prefixes == null) return null;

    return prefixes.stream()
                   .map(prefix -> prefix.append(qn))
                   .map(fqn -> childResolver.resolve(new QnTypeRef(fqn)))
                   .filter(Objects::nonNull)
                   .findFirst()
                   .orElse(null);
  }

  public static @Nullable List<Qn> calculateResolutionPrefixes(@NotNull Qn reference,
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

  @Override
  public @Nullable TypeApi resolve(@NotNull AnonListRef reference) {
    @NotNull ValueTypeRef itemTypeRef = reference.itemsType();
    @Nullable DataTypeApi itemType = resolve(itemTypeRef);
    if (itemType == null) return null;

    final @NotNull AnonListRef normalizedRef = TypeReferenceFactory.createAnonListReference(itemType);
    return childResolver.resolve(normalizedRef);
  }

  @Override
  public @Nullable TypeApi resolve(@NotNull AnonMapRef reference) {
    @NotNull ValueTypeRef valueTypeRef = reference.itemsType();
    @Nullable DataTypeApi valueType = resolve(valueTypeRef);
    if (valueType == null) return null;

    @Nullable TypeApi keyType = reference.keysType().resolve(this);
    if (keyType instanceof DatumType) {
      DatumType keyDatumType = (DatumType) keyType;
      final @NotNull AnonMapRef normalizedRef = TypeReferenceFactory.createAnonMapReference(keyDatumType, valueType);
      return childResolver.resolve(normalizedRef);
    } else return null;
  }
}
