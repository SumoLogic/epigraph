package io.epigraph.projections;

import io.epigraph.types.RecordType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionUtils {
  @NotNull
  public static <K, V> LinkedHashMap<K, V> singletonLinkedHashMap(@NotNull K key, @NotNull V value) {
    final LinkedHashMap<K, V> res = new LinkedHashMap<>();
    res.put(key, value);
    return res;
  }

  public static void checkFieldsBelongsToModel(
      @NotNull Collection<RecordType.Field> fields,
      @NotNull RecordType model) {

    for (RecordType.Field field : fields) {
      if (!fields.contains(field))
        throw new IllegalArgumentException(
            String.format("Field '%s' does not belong to record model '%s'. Known fields: %s",
                          field.name(), model.name(), listFields(fields)
            )
        );
    }

  }

  @NotNull
  public static String listFields(@Nullable Collection<? extends RecordType.Field> fields) {
    if (fields == null) return "<none>";
    return fields.stream().map(RecordType.Field::name).collect(Collectors.joining(","));
  }
}
