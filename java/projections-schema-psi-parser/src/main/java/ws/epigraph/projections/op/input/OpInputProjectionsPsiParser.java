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

import ws.epigraph.lang.MessagesContext;
import ws.epigraph.projections.op.PostProcessingOpProjectionPsiParser;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpInputProjectionsPsiParser extends PostProcessingOpProjectionPsiParser {
  public static final String FLAG_REQUIRED = "required";
  public static final String FLAG_CAN_REPLACE = "'can replace'";
  public static final String FLAGGED = "flagged";

  public OpInputProjectionsPsiParser(String flagSemantics, MessagesContext context) {
    super(null, null);
  }

  public OpInputProjectionsPsiParser(MessagesContext context) {
    this(FLAGGED, context);
  }

}
