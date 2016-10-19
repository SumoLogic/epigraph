package io.epigraph.projections;

import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

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

  public static void checkFieldsBelongsToModel(@NotNull Collection<String> fieldNames, @NotNull RecordType model) {
    final Set<String> modelFieldNames = model.fieldsMap().keySet();
    for (String fieldName : fieldNames) {
      if (!modelFieldNames.contains(fieldName))
        throw new IllegalArgumentException(
            String.format("Field '%s' does not belong to record model '%s'. Known fields: %s",
                          fieldName, model.name(), listFields(modelFieldNames)
            )
        );
    }

  }

  @NotNull
  public static String listFields(@Nullable Collection<String> fieldNames) {
    if (fieldNames == null) return "<none>";
    return String.join(",", fieldNames);
  }
}
