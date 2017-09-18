/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.data.validation;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Data;
import ws.epigraph.data.RecordDatum;
import ws.epigraph.data.Val;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.TypeKind;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputDataValidator extends GenDataValidator<
    OpOutputVarProjection,
    OpOutputTagProjectionEntry,
    OpOutputModelProjection<?, ?, ?, ?>,
    OpOutputRecordModelProjection,
    OpOutputMapModelProjection,
    OpOutputListModelProjection,
    OpOutputPrimitiveModelProjection,
    OpOutputFieldProjectionEntry,
    OpOutputFieldProjection
    > {

  @Override
  protected void validateDataOnly(final @NotNull Data data, final @NotNull OpOutputVarProjection projection) {
    projection.tagProjections().values().stream().filter(p -> p.projection().flagged()).forEach(tp -> {
      final String tagName = tp.tag().name();

      final Val val = data._raw().tagValues().get(tagName);
      if (val == null || val.getDatum() == null)
        context.addError(String.format("Required tag '%s' is missing", tagName));
    });
  }

  @Override
  protected void validateRecordDatumOnly(
      final @NotNull RecordDatum datum,
      final @NotNull OpOutputRecordModelProjection projection) {

    projection.fieldProjections().values().stream().filter(p -> p.fieldProjection().flagged()).forEach(fp -> {
      final String fieldName = fp.field().name();

      final Data fieldData = datum._raw().fieldsData().get(fieldName);
      boolean failed = fieldData == null;

      if (!failed && fieldData.type().kind() != TypeKind.ENTITY) {
        final Val val = fieldData._raw().tagValues().get(DatumType.MONO_TAG_NAME);
        failed = val == null || val.getDatum() == null;
      }

      if (failed)
        context.addError(String.format("Required field '%s' is missing", fieldName));
    });
  }

}
