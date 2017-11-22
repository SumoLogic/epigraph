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

package ws.epigraph.client.http;

import org.junit.Test;
import ws.epigraph.projections.StepsAndProjection;
import ws.epigraph.projections.req.ReqFieldProjection;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.service.operations.CreateOperationRequest;
import ws.epigraph.service.operations.CustomOperationRequest;
import ws.epigraph.test.TestUtil;
import ws.epigraph.tests.PersonId;
import ws.epigraph.tests.PersonRecord;
import ws.epigraph.tests.PersonRecord_List;
import ws.epigraph.tests._resources.users.UsersResourceDeclaration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class RequestFactoryTest {
  @Test
  public void testCreateRequestWithoutInputProjection() {
    CreateOperationRequest request = RequestFactory.constructCreateRequest(
        UsersResourceDeclaration.INSTANCE.fieldType(),
        UsersResourceDeclaration.createOperationDeclaration,
        null,
        null,
        PersonRecord_List.type.createDataBuilder().set(
            PersonRecord_List.create().add(
                PersonRecord.create().setId(PersonId.create(111))
            )
        ),
        "",
        StaticTypesResolver.instance()
    );

    assertNull(request.inputProjection());
  }

  @Test
  public void testCustomRequestWithPartialInputProjection() {
    CustomOperationRequest request = RequestFactory.constructCustomRequest(
        UsersResourceDeclaration.INSTANCE.fieldType(),
        UsersResourceDeclaration.capitalizeCustomOperationDeclaration,
        "/123",
        ";useLowerCase=true",
        null,
        "",
        StaticTypesResolver.instance()
    );

    StepsAndProjection<ReqFieldProjection> inputStepsAndProjection = request.inputStepsAndProjection();
    assertNotNull(inputStepsAndProjection);
    assertEquals(2, inputStepsAndProjection.pathSteps()); // one step for field, one for self-var
    String s = TestUtil.printReqEntityProjection(inputStepsAndProjection.projection().projection(), 0);
    assertEquals(";useLowerCase = true", s);
  }
}
