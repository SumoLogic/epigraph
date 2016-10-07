package io.epigraph.service;

import io.epigraph.idl.ResourceIdl;
import io.epigraph.lang.TextLocation;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.projections.op.output.OpOutputFieldProjection;
import io.epigraph.projections.op.output.OpOutputRecordModelProjection;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.DataType;
import io.epigraph.types.RecordType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RootComposer {
  public static OpOutputRecordModelProjection composeRoot(@NotNull Service service, @NotNull TypesResolver resolver) {
    // todo set namespace?
    final String rootTypeName = service.name() + "Root";

    final List<@NotNull ? extends RecordType.Field> fields = rootFields(service);

    RecordType rootType = new RecordType.Raw(
        new QualifiedTypeName(rootTypeName),
        Collections.emptyList()
    ) {
      @Override
      public @NotNull List<@NotNull ? extends Field> immediateFields() {
        return fields;
      }
    };

    LinkedHashMap<RecordType.Field, OpOutputFieldProjection> fieldProjections = null;

    return new OpOutputRecordModelProjection(
        rootType,
        false,
        null,
        null,
        null,
        fieldProjections,
        TextLocation.UNKNOWN
    );
  }

  @NotNull
  private static List<@NotNull ? extends RecordType.Field> rootFields(@NotNull Service service) {
    final List<RecordType.Field> res = new ArrayList<>(service.resources().size());

    for (Resource resource : service.resources().values()) {
      @NotNull ResourceIdl resourceDeclaration = resource.declaration();

      @NotNull String fieldName = resourceDeclaration.fieldName();
      @NotNull DataType fieldType = resourceDeclaration.fieldType();

      res.add(new RecordType.Field(fieldName, fieldType));
    }

    return res;
  }

  @NotNull
  private static OpOutputFieldProjection rootFieldProjection(@NotNull RecordType.Field field,
                                                             @NotNull Service service) {

    Resource resource = service.resources().get(field.name());
    assert resource != null;

//    resource.
    return null;
  }
}
