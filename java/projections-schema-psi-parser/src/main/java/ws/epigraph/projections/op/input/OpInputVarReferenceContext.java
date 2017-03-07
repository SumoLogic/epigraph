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

package ws.epigraph.projections.op.input;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.Qn;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.VarReferenceContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.types.TypeApi;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpInputVarReferenceContext extends VarReferenceContext<OpInputVarProjection> {

  public OpInputVarReferenceContext(
      final Qn referencesNamespace,
      final VarReferenceContext<OpInputVarProjection> parent) {
    super(referencesNamespace, parent);
  }

  @NotNull
  @Override
  protected OpInputVarProjection newReference(
      @NotNull final TypeApi type, @NotNull final TextLocation location) {
    return new OpInputVarProjection(type, location);
  }
}
