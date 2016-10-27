package io.epigraph.projections;

import io.epigraph.projections.op.path.*;
import io.epigraph.types.DataType;
import io.epigraph.types.DatumType;
import io.epigraph.types.RecordType;
import io.epigraph.types.Type;
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

  /**
   * @return {@code path} tip type
   */
  @NotNull
  public static DataType tipType(@NotNull OpVarPath path) {
    DataType lastDataType;

    final Type type = path.type();
    if (type instanceof DatumType) {
      DatumType datumType = (DatumType) type;
      lastDataType = datumType.dataType();
    } else {
      lastDataType = new DataType(type, null);
    }

    while (true) {
      final OpTagPath tagProjection = path.pathTagProjection();
      if (tagProjection == null) break;

      lastDataType = tagProjection.tag().type.dataType();

      final OpModelPath<?, ?> modelPath = tagProjection.projection();
      final DatumType model = modelPath.model();
      switch (model.kind()) {
        case RECORD:
          OpRecordModelPath recordPath = (OpRecordModelPath) modelPath;
          OpFieldPathEntry fieldProjection = recordPath.pathFieldProjection();
          if (fieldProjection == null) break;
          lastDataType = fieldProjection.field().dataType();
          path = fieldProjection.projection().projection();
          break;
        case MAP:
          OpMapModelPath mapPath = (OpMapModelPath) modelPath;
          lastDataType = mapPath.model().valueType();
          path = mapPath.itemsProjection();
          break;
        default:
          break;
      }
    }

    return lastDataType;
  }
}
