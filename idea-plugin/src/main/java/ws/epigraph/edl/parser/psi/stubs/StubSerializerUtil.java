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

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class StubSerializerUtil {
  interface Serializer<T> {
    void serialize(@NotNull T item, @NotNull StubOutputStream stream) throws IOException;
  }

  interface Deserializer<T> {
    @Nullable
    T deserialize(@NotNull StubInputStream stream) throws IOException;
  }

  public static <T> void serializeCollection(@Nullable Collection<T> collection,
                                             @NotNull Serializer<T> itemSerializer,
                                             @NotNull StubOutputStream stream) throws IOException {
    if (collection == null) stream.writeShort(0);
    else {
      stream.writeShort(collection.size());
      for (T item : collection) itemSerializer.serialize(item, stream);
    }
  }

  public static <T> void serializeCollection(@Nullable T[] collection,
                                             @NotNull Serializer<T> itemSerializer,
                                             @NotNull StubOutputStream stream) throws IOException {
    if (collection == null) stream.writeShort(0);
    else {
      stream.writeShort(collection.length);
      for (T item : collection) itemSerializer.serialize(item, stream);
    }
  }

  @NotNull
  public static <T> Set<T> deserializeSet(@NotNull Deserializer<T> itemDeserializer,
                                          @NotNull StubInputStream stream,
                                          boolean skipNulls) throws IOException {
    short numItems = stream.readShort();

    // can't do this, we may want to add more elements to it later on
    // if (numItems == 0) return Collections.emptySet();

    Set<T> result = ContainerUtil.newTroveSet();
    for (int i = 0; i < numItems; i++) {
      T item = itemDeserializer.deserialize(stream);
      if (item != null || skipNulls)
        result.add(item);
    }

    return result;
  }

  @NotNull
  public static <T> List<T> deserializeList(@NotNull Deserializer<T> itemDeserializer,
                                            @NotNull StubInputStream stream,
                                            boolean skipNulls) throws IOException {
    short numItems = stream.readShort();

    // can't do this, we may want to add more elements to it later on
    // if (numItems == 0) return Collections.emptyList();

    List<T> result = ContainerUtil.newSmartList(); // our lists often contain only one element
    for (int i = 0; i < numItems; i++) {
      T item = itemDeserializer.deserialize(stream);
      if (item != null || skipNulls)
        result.add(item);
    }

    return result;
  }
}
