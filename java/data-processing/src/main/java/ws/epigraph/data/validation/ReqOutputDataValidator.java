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
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.req.*;
import ws.epigraph.types.TypeKind;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputDataValidator extends GenDataValidator<
    ReqEntityProjection,
    ReqTagProjectionEntry,
    ReqModelProjection<?, ?, ?>,
    ReqRecordModelProjection,
    ReqMapModelProjection,
    ReqListModelProjection,
    ReqPrimitiveModelProjection,
    ReqFieldProjectionEntry,
    ReqFieldProjection
    > {

  @Override
  protected void validateDataOnly(final @NotNull Data data, final @NotNull ReqEntityProjection projection) {

    projection.tagProjections().values().stream().filter(p -> p.projection().flag()).forEach(tp -> {
      final String tagName = tp.tag().name();

      final String obj = data.type().kind() == TypeKind.ENTITY ? "tag '" + tagName + "'" : "value";
      final Val val = data._raw().tagValues().get(tagName);
      if (val == null)
        context.addOperationImplementationError(String.format("Required %s is missing", obj));
      else if (val.getDatum() == null) {
        final ErrorValue error = val.getError();
        if (error == null) context.addError(String.format("Required %s is missing", obj));
        else context.addError(String.format(
            "Required %s is a [%s] error: %s",
            obj,
            error.statusCode(),
            error.message()
        ));
      }
    });
  }

  @Override
  protected void validateRecordDatumOnly(
      final @NotNull RecordDatum datum,
      final @NotNull ReqRecordModelProjection projection) {

    projection.fieldProjections().values().stream().filter(p -> p.fieldProjection().flag()).forEach(fp -> {
      final String fieldName = fp.field().name();

      final Data fieldData = datum._raw().fieldsData().get(fieldName);

      if (fieldData == null)
        context.addOperationImplementationError(String.format("Required field '%s' is missing", fieldName));

      // will be reported by (self) tag validation
//      else if (fieldData.type().kind() != TypeKind.UNION) {
//        final Val val = fieldData._raw().tagValues().get(DatumType.MONO_TAG_NAME);
//
//        if (val == null)
//          context.addOperationImplementationError(String.format("Required field '%s' is missing", fieldName));
//        else if (val.getDatum() == null) {
//          final ErrorValue error = val.getError();
//          if (error == null) context.addError(String.format("Required field '%s' is missing", fieldName));
//          else context.addError(String.format(
//              "Required field '%s' is a [%s] error: %s",
//              fieldName,
//              error.statusCode(),
//              error.message()
//          ));
//        }
//      }
    });
  }

}
