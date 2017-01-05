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

package ws.epigraph.projections.gen;

import ws.epigraph.types.RecordTypeApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenRecordModelProjection<
    VP extends GenVarProjection<VP, TP, MP>,
    TP extends GenTagProjectionEntry<TP, MP>,
    MP extends GenModelProjection</*MP*/?, ?>,
    RMP extends GenRecordModelProjection<VP, TP, MP, RMP, FPE, FP, M>,
    FPE extends GenFieldProjectionEntry<VP, TP, MP, FP>,
    FP extends GenFieldProjection<VP, TP, MP, FP>,
    M extends RecordTypeApi
    > extends GenModelProjection<RMP, M> {

  @NotNull Map<String, FPE> fieldProjections();

  default @Nullable FPE fieldProjection(@NotNull String fieldName) { return fieldProjections().get(fieldName); }

  /**
   * @return single field projection if there's just one, {@code null} otherwise
   */
  default @Nullable FPE pathFieldProjection() {
    final @NotNull Map<String, FPE> fieldProjections = fieldProjections();
    if (fieldProjections.size() == 1) return fieldProjections.values().iterator().next();
    else return null;
  }
}
