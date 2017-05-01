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

package ws.epigraph.util;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public enum HttpStatusCode {
  OK(200),
  CREATED(201),

  BAD_REQUEST(400),
  UNAUTHORIZED(401),
  NOT_FOUND(404),
  TIMEOUT(408),
  PRECONDITION_FAILED(412),
  TOO_MANY_REQUESTS(429),

  INTERNAL_SERVER_ERROR(500),
  INTERNAL_OPERATION_ERROR(520);

  final int code;

  HttpStatusCode(final int code) {this.code = code;}

  public int code() { return code; }
}
