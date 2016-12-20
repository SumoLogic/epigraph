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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gen.Constants;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("unchecked")
public final class IndexBasedTypesResolver implements TypesResolver {
  public static final TypesResolver INSTANCE = new IndexBasedTypesResolver();

  public static final String INDEX_CLASS_NAME = Constants.TypesIndex.namespace + "." + Constants.TypesIndex.className;

  private static final Map<@NotNull String, @NotNull ? extends Type> index;

  static {
    try {
      Class<?> indexClass = Class.forName(INDEX_CLASS_NAME);
      index = (Map<String, ? extends Type>) indexClass.getField("types").get(null); // todo constant
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private IndexBasedTypesResolver() {}

  @Override
  public @Nullable Type resolve(final @NotNull QnTypeRef reference) {
    return index.get(name(reference));
  }

  @Override
  public @Nullable Type resolve(final @NotNull AnonListRef reference) {
    return index.get(name(reference));
  }

  @Override
  public @Nullable Type resolve(final @NotNull AnonMapRef reference) {
    return index.get(name(reference));
  }

  private @NotNull String name(@NotNull TypeRef ref) {
    // this code reverses the logic of CType.toString (including subclasses)
    if (ref instanceof QnTypeRef) return ((QnTypeRef) ref).qn().toString();
    else if (ref instanceof AnonMapRef) {
      AnonMapRef mr = (AnonMapRef) ref;
      String ks = name(mr.keysType());
      String vs = name(mr.itemsType());
      return String.format("map[%s,%s]", ks, vs);
    } else if (ref instanceof AnonListRef)
      return String.format("list[%s]", name(((AnonListRef) ref).itemsType()));
    else throw new IllegalArgumentException("Unknown ref: " + ref.getClass().getName());
  }

  private @NotNull String name(@NotNull ValueTypeRef ref) {
    final String s = name(ref.typeRef());
    final String defaultOverride = ref.defaultOverride();
    return defaultOverride == null || Objects.equals(defaultOverride, DatumType.MONO_TAG_NAME)
           ? s : s + " default " + defaultOverride;
  }
}
