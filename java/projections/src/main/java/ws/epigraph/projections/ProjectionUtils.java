package ws.epigraph.projections;

import ws.epigraph.projections.gen.*;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.RecordType;
import ws.epigraph.types.Type;
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
  public static DataType tipType(@NotNull GenVarProjection<?, ?, ?> path) {
    DataType lastDataType;

    final Type type = path.type();
    if (type instanceof DatumType) {
      DatumType datumType = (DatumType) type;
      lastDataType = datumType.dataType();
    } else {
      lastDataType = new DataType(type, null);
    }

    while (true) {
      final GenTagProjectionEntry<?> tagProjection = path.pathTagProjection();
      if (tagProjection == null) break;

      lastDataType = tagProjection.tag().type.dataType();

      final GenModelProjection<?, ?> modelPath = tagProjection.projection();
      final DatumType model = modelPath.model();
      switch (model.kind()) {
        case RECORD:
          GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?> recordPath =
              (GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>) modelPath;

          GenFieldProjectionEntry<?, ?, ?, ?> fieldProjection = recordPath.pathFieldProjection();
          if (fieldProjection == null) break;
          lastDataType = fieldProjection.field().dataType();
          path = fieldProjection.projection().projection();
          break;
        case MAP:
          GenMapModelProjection<?, ?, ?, ?, ?> mapPath = (GenMapModelProjection<?, ?, ?, ?, ?>) modelPath;

          lastDataType = mapPath.model().valueType();
          path = mapPath.itemsProjection();
          break;
        default:
          break;
      }
    }

    return lastDataType;
  }

  public static int pathLength(@NotNull GenVarProjection<?, ?, ?> path) {
    int len = 0;

    while (true) {
      final GenTagProjectionEntry<?> tagProjection = path.pathTagProjection();
      if (tagProjection == null) break;

      len++;

      final GenModelProjection<?, ?> modelPath = tagProjection.projection();
      final DatumType model = modelPath.model();
      switch (model.kind()) {
        case RECORD:
          GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?> recordPath =
              (GenRecordModelProjection<?, ?, ?, ?, ?, ?, ?>) modelPath;

          GenFieldProjectionEntry<?, ?, ?, ?> fieldProjection = recordPath.pathFieldProjection();
          if (fieldProjection == null) break;
          len++;
          path = fieldProjection.projection().projection();
          break;
        case MAP:
          GenMapModelProjection<?, ?, ?, ?, ?> mapPath = (GenMapModelProjection<?, ?, ?, ?, ?>) modelPath;
          len++;
          path = mapPath.itemsProjection();
          break;
        default:
          break;
      }
    }

    return len;
  }
}
