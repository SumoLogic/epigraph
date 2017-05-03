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
public interface HttpStatusCode {
  int OK = 200;
  int CREATED = 201;

  int BAD_REQUEST = 400;
  int UNAUTHORIZED = 401;
  int NOT_FOUND = 404;
  int TIMEOUT = 408;
  int PRECONDITION_FAILED = 412;
  int TOO_MANY_REQUESTS = 429;

  int INTERNAL_SERVER_ERROR = 500;
  int INTERNAL_OPERATION_ERROR = 520;

}
