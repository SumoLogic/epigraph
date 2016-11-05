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

import ws.epigraph.lang.Qn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class QualifiedName {

  private final @Nullable NamespaceName namespaceName;

  private final @NotNull String localName;

  protected QualifiedName(@Nullable NamespaceName namespaceName, @NotNull String localName) {
    this.namespaceName = namespaceName;
    this.localName = localName;
  }

  @NotNull
  public Qn toFqn() {
    if (namespaceName == null) return new Qn(localName);
    else return namespaceName.toFqn().append(localName);
  }

  /** Returns canonical string representation for this qualified name. */
  @Override
  public @NotNull String toString() {
    return namespaceName == null ? localName : namespaceName.toString() + '.' + localName;
  }

}
