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

package ws.epigraph.url.projections.req.update;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.MessagesContext;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpTagProjectionEntry;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.projections.req.ReqTagProjectionEntry;
import ws.epigraph.types.TypeKind;
import ws.epigraph.url.projections.req.AbstractReqTraversal;

import java.util.Map;
import java.util.Optional;

/**
 * Checks that if req projection is marked for 'replace' then corresponding
 * op projection is marked as 'can replace'
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqCanReplaceChecker extends AbstractReqTraversal {
  public ReqCanReplaceChecker(final @NotNull MessagesContext context) {
    super(context);
  }

  @Override
  protected boolean visitVarProjection(
      final @NotNull ReqEntityProjection projection,
      final @NotNull OpEntityProjection guide) {

    if (projection.flag() && !guide.flag() && guide.type().kind() != TypeKind.PRIMITIVE) // can always replace primitive
    {
      String description = Optional.ofNullable(currentEntityDataDescription).orElse(
          String.format("data for type '%s'", projection.type().name())
      );

      TextLocation location = Optional.ofNullable(currentEntityDataLocation).orElse(projection.location());

      context.addError("Operation doesn't support replacing data for " + description, location);
    }

    if (projection.type().kind() == TypeKind.ENTITY) {
      for (final Map.Entry<String, ReqTagProjectionEntry> entry : projection.tagProjections().entrySet()) {
        String tagName = entry.getKey();
        ReqTagProjectionEntry rtpe = entry.getValue();
        OpTagProjectionEntry gtpe = guide.tagProjection(tagName);

        if (gtpe == null)
          context.addError(String.format("Malformed projection: unsupported tag '%s'", tagName), rtpe.location());
        else if (rtpe.modelProjection().flag() && !gtpe.modelProjection().flag())
          context.addError(
              String.format("Operation doesn't support replacing data for tag '%s'", tagName),
              rtpe.location()
          );
      }
    }

    // todo replace on tails? (and model tails too)

    return super.visitEntityProjection(projection, guide);
  }

}
