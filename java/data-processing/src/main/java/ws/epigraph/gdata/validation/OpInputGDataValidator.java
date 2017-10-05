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

package ws.epigraph.gdata.validation;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.gdata.GData;
import ws.epigraph.gdata.GRecordDatum;
import ws.epigraph.projections.op.*;
import ws.epigraph.refs.TypesResolver;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputGDataValidator extends GenGDataValidator<
    OpEntityProjection,
    OpTagProjectionEntry,
    OpModelProjection<?, ?, ?, ?>,
    OpRecordModelProjection,
    OpMapModelProjection,
    OpListModelProjection,
    OpPrimitiveModelProjection,
    OpFieldProjectionEntry,
    OpFieldProjection
    > {

  public OpInputGDataValidator(final @NotNull TypesResolver resolver) {
    super(resolver);
  }

  @Override
  protected void validateDataOnly(final @NotNull GData data, final @NotNull OpEntityProjection projection) {
    projection.tagProjections().values().stream().filter(p -> p.projection().flag()).forEach(tp -> {
      final String tagName = tp.tag().name();

      if (!data.tags().containsKey(tagName))
        context.addError(String.format("Required tag '%s' is missing", tagName), data.location());
    });
  }

  @Override
  protected void validateRecordDatumOnly(
      final @NotNull GRecordDatum datum,
      final @NotNull OpRecordModelProjection projection) {
    projection.fieldProjections().values().stream().filter(p -> p.fieldProjection().flag()).forEach(fp -> {
      final String fieldName = fp.field().name();

      if (!datum.fields().containsKey(fieldName))
        context.addError(String.format("Required field '%s' is missing", fieldName), datum.location());
    });
  }

}
