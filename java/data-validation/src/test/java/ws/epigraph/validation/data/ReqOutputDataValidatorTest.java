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
import ws.epigraph.data.Data;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.req.output.ReqOutputVarProjection;
import ws.epigraph.refs.IndexBasedTypesResolver;
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
  private final TypesResolver resolver = IndexBasedTypesResolver.INSTANCE;

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
        "Required field 'firstName' is missing at :record"
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
    final OpOutputVarProjection varProjection = DataValidatorTestUtil.parseOpOutputVarProjection(dataType, op, resolver);
    final ReqOutputVarProjection reqProjection =
        DataValidatorTestUtil.parseReqOutputVarProjection(dataType, varProjection, req, resolver).projection();
    final Data d = DataValidatorTestUtil.makeData(type, data, resolver);

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
