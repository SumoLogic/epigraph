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

package ws.epigraph.ideaplugin.edl.features;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;

import static ws.epigraph.edl.lexer.EdlElementTypes.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlQuoteHandler extends SimpleTokenSetQuoteHandler {
  public EdlQuoteHandler() {
    super(E_ID, E_STRING, E_NUMBER, E_NULL);
  }
  // TODO more elaborate implementation. Only insert `` around IDs and "" around strings
  // see TypedHandler:441

  // see TypedHandlerDelegate, we will need one to insert matching () {} <> in data
}
