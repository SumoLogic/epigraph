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

package ws.epigraph.validation.data;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.data.Data;
import ws.epigraph.data.RecordDatum;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.projections.req.output.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputDataValidator extends GenDataValidator<
    ReqOutputVarProjection,
    ReqOutputTagProjectionEntry,
    ReqOutputModelProjection<?, ?, ?>,
    ReqOutputRecordModelProjection,
    ReqOutputMapModelProjection,
    ReqOutputListModelProjection,
    ReqOutputPrimitiveModelProjection,
    ReqOutputFieldProjectionEntry,
    ReqOutputFieldProjection
    > {

  @Override
  protected void validateDataOnly(final @NotNull Data data, final @NotNull ReqOutputVarProjection projection) {
    projection.tagProjections().values().stream().filter(p -> p.projection().required()).forEach(tp -> {
      final String tagName = tp.tag().name();

      final Val val = data._raw().tagValues().get(tagName);
      if (val == null)
        context.addError(String.format("Required tag '%s' is missing", tagName));
      else {
        final ErrorValue error = val.getError();
        if (error != null)
          context.addError(String.format(
              "Required tag '%s' is a [%s] error: %s",
              tagName,
              error.statusCode(),
              error.message()
          ));
      }
    });
  }

  @Override
  protected void validateRecordDatumOnly(
      final @NotNull RecordDatum datum,
      final @NotNull ReqOutputRecordModelProjection projection) {
    projection.fieldProjections().values().stream().filter(p -> p.fieldProjection().required()).forEach(fp -> {
      final String fieldName = fp.field().name();

      if (!datum._raw().fieldsData().containsKey(fieldName))
        context.addError(String.format("Required field '%s' is missing", fieldName));
    });
  }

}
