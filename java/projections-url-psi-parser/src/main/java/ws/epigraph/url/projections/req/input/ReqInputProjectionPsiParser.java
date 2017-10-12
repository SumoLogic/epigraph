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

package ws.epigraph.url.projections.req.input;

import ws.epigraph.lang.MessagesContext;
import ws.epigraph.url.projections.req.DefaultReqProjectionConstructor;
import ws.epigraph.url.projections.req.PostProcessingReqProjectionPsiParser;
import ws.epigraph.url.projections.req.postprocess.ReqRequiredChecker;
import ws.epigraph.url.projections.req.postprocess.ReqRequiredSynchronizer;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqInputProjectionPsiParser extends PostProcessingReqProjectionPsiParser {

  public ReqInputProjectionPsiParser(boolean includeAllFromOp, MessagesContext context) {
    super(
        new ReqRequiredChecker(context),
        new ReqRequiredSynchronizer(context),
        DefaultReqProjectionConstructor.inputProjectionDefaultConstructor(includeAllFromOp)
    );
  }
}
