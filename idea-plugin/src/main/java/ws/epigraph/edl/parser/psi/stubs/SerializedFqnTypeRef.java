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

package ws.epigraph.edl.parser.psi.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import ws.epigraph.ideaplugin.edl.brains.EdlQnReference;
import ws.epigraph.ideaplugin.edl.brains.EdlQnReferenceResolver;
import ws.epigraph.lang.Qn;
import ws.epigraph.edl.parser.psi.EdlQnTypeRef;
import ws.epigraph.edl.parser.psi.EdlTypeDef;
import ws.epigraph.edl.parser.psi.impl.EdlPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class SerializedFqnTypeRef {
  @Nullable
  private Qn shortName;
  @Nullable
  private List<Qn> namespacesToSearch;
  // OR
  @Nullable
  private EdlQnTypeRef typeRef;

  public SerializedFqnTypeRef(@Nullable Qn shortName, @Nullable List<Qn> namespacesToSearch) {
    this.namespacesToSearch = namespacesToSearch;
    this.shortName = shortName;
  }

  public SerializedFqnTypeRef(@Nullable EdlQnTypeRef typeRef) {
    this.typeRef = typeRef; // do the rest lazily
  }

  private void initFromTypeRef() {
    if ((shortName == null || namespacesToSearch == null) && typeRef != null) {
      EdlQnReference ref = (EdlQnReference) EdlPsiImplUtil.getReference(typeRef);
      if (ref != null) {
        EdlQnReferenceResolver resolver = ref.getResolver();
        shortName = resolver.getSuffix();
        namespacesToSearch = resolver.getPrefixes();
      }

      typeRef = null;
    }
  }

  @Nullable
  public Qn getShortName() {
    initFromTypeRef();
    return shortName;
  }

  @Nullable
  public List<Qn> getNamespacesToSearch() {
    initFromTypeRef();
    return namespacesToSearch;
  }

  @Nullable
  private PsiElement resolve(@NotNull Project project, @NotNull GlobalSearchScope searchScope) {
    initFromTypeRef();
    List<Qn> namespacesToSearch = getNamespacesToSearch();
    Qn shortName = getShortName();
    if (namespacesToSearch == null || shortName == null) return null;
    EdlQnReferenceResolver resolver = new EdlQnReferenceResolver(namespacesToSearch, shortName, searchScope);
    return resolver.resolve(project);
  }

  @Nullable
  public EdlTypeDef resolveTypeDef(@NotNull Project project, @NotNull GlobalSearchScope searchScope) {
    PsiElement element = resolve(project, searchScope);
    if (element instanceof EdlTypeDef) return (EdlTypeDef) element;
    return null;
  }

  public static void serializeNullable(@Nullable SerializedFqnTypeRef ref, @NotNull StubOutputStream stream) throws IOException {
    stream.writeBoolean(ref != null);
    if (ref != null) ref.serialize(stream);
  }

  @Nullable
  public static SerializedFqnTypeRef deserializeNullable(@NotNull StubInputStream stream) throws IOException {
    boolean b = stream.readBoolean();
    if (!b) return null;
    return deserialize(stream);
  }

  public void serialize(@NotNull StubOutputStream stream) throws IOException {
    initFromTypeRef();
    stream.writeName(shortName == null ? null : shortName.toString());
    if (shortName != null) {
      if (namespacesToSearch == null) stream.writeShort(0);
      else {
        // NB we're writing all namespaces to search for every type ref, but it won't take much space
        // thanks to names de-duplication done by IDEA. However this can be optimized further.
        // TODO(low) remove star imports from this list, write them at the file level
        // Estimated overhead is a few KBs per file at most.

        StubSerializerUtil.serializeCollection(namespacesToSearch, (item, s) -> s.writeName(item.toString()), stream);
      }
    }
  }

  @NotNull
  public static SerializedFqnTypeRef deserialize(@NotNull StubInputStream stream) throws IOException {
    String shortNameStr = StringRef.toString(stream.readName());

    Qn shortName = null;
    List<Qn> namespacesToSearch = null;

    if (shortNameStr != null) {
      shortName = Qn.fromDotSeparated(shortNameStr);

      namespacesToSearch = StubSerializerUtil.deserializeList(s -> {
        StringRef namespaceRef = s.readName();
        String namespace = StringRef.toString(namespaceRef);
        return namespace == null ? null : Qn.fromDotSeparated(namespace);
      }, stream, true);
    }

    return new SerializedFqnTypeRef(shortName, namespacesToSearch);
  }
}
