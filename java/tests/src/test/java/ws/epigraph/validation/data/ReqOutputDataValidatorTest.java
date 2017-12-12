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
import org.junit.Test;
import ws.epigraph.EpigraphTestUtil;
import ws.epigraph.data.Data;
import ws.epigraph.data.validation.DataValidationError;
import ws.epigraph.data.validation.ReqOutputDataValidator;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.projections.req.ReqEntityProjection;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.tests.Person;
import ws.epigraph.types.DataType;
import ws.epigraph.types.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputDataValidatorTest {
  private final TypesResolver resolver = StaticTypesResolver.instance();

  @Test
  public void testNoError() {
    test(
        Person.type,
        ":id",
        ":id",
        "<id:1>"
    );
  }

  @Test
  public void testMissingField() {
    test(
        Person.type,
        ":`record`(firstName)",
        ":record(+firstName)",
        "<`record`:{}>",
        ":record : Required field 'firstName' is missing"
    );
  }

  @Test
  public void testMissingModel() {
    test(
        Person.type,
        ":(id,`record`)",
        ":(id,+record)",
        "<id:1>",
        "Required tag 'record' is missing"
    );
  }

  private void test(
      @NotNull Type type,
      @NotNull String op,
      @NotNull String req,
      @NotNull String data,
      String... expectedErrorMessages) {

    final DataType dataType = (DataType) type.dataType();
    final OpProjection<?, ?> opProjection = EpigraphTestUtil.parseOpProjection(dataType, op, resolver);
    final ReqEntityProjection reqProjection =
        EpigraphTestUtil.parseReqOutputEntityProjection(dataType, opProjection, req, resolver).projection();
    final Data d = EpigraphTestUtil.makeData(type, data, resolver);

    final ReqOutputDataValidator validator = new ReqOutputDataValidator();
    validator.validateData(d, reqProjection);

    Set<String> errorMessages =
        validator.errors().stream().map(DataValidationError::toString).collect(Collectors.toSet());

    Set<String> expectedMessages = new HashSet<>(Arrays.asList(expectedErrorMessages));

    for (final String message : errorMessages) {
      if (!expectedMessages.contains(message))
        fail(message);
      expectedMessages.remove(message);
    }

    assertTrue(
        expectedMessages.stream().collect(Collectors.joining("\n")),
        expectedMessages.isEmpty()
    );

  }
}
