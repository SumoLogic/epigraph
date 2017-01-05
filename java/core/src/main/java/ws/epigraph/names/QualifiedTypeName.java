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

/* Created by yegor on 7/22/16. */

package ws.epigraph.names;

import ws.epigraph.data.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.Qn;

public final class QualifiedTypeName extends QualifiedName implements TypeName, Immutable {

  private @Nullable String toString = null;

  public QualifiedTypeName(@Nullable NamespaceName namespaceName, @NotNull String localName) {
    super(namespaceName, localName);
  }

  public QualifiedTypeName(@NotNull String localName, @NotNull String... namespaceNames) {
    this(NamespaceName.from(namespaceNames), localName);
  }

  public static QualifiedTypeName fromFqn(@NotNull Qn fqn) {
    if (fqn.isEmpty()) throw new IllegalArgumentException("Empty FQN");
    if (fqn.size() == 1) return new QualifiedTypeName(null, fqn.first());
    final NamespaceName namespaceName = NamespaceName.from(fqn.removeLastSegment().segments);
    return new QualifiedTypeName(namespaceName, fqn.last());
  }

  @Override
  public @NotNull String toString() {
    if (toString == null) toString = super.toString();
    return toString;
  }

}
