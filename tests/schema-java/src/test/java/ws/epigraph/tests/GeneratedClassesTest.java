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

package ws.epigraph.tests;

import de.uka.ilkd.pp.Layouter;
import de.uka.ilkd.pp.NoExceptions;
import de.uka.ilkd.pp.StringBackend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.data.Data;
import ws.epigraph.data.ListDatum;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPrettyPrinter;
import ws.epigraph.projections.op.delete.OpDeleteVarProjection;
import ws.epigraph.schema.Namespaces;
import ws.epigraph.tests.resources.users.UsersResourceDeclaration;
import ws.epigraph.tests.resources.users.operations._update.update.UpdatePersonMapProjection;
import ws.epigraph.tests.resources.users.operations._update.update.UpdateUsersFieldProjection;
import ws.epigraph.tests.resources.users.operations._update.update.elements.UpdatePersonProjection;
import ws.epigraph.tests.resources.users.operations._update.update.elements.record.UpdatePersonRecordProjection;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.ListType;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@SuppressWarnings("ConstantConditions")
public class GeneratedClassesTest {
  @Test
  public void testListBuilder() {
    final PersonRecord_List.Builder listBuilder = PersonRecord_List.create();
    listBuilder.add(PersonRecord.create().setId(PersonId.create(1)));

    final List<@Nullable PersonRecord> datums = listBuilder.datums();
    final PersonRecord record = datums.get(0);
    assertEquals(1, record.getId().getVal().intValue());
  }

  @Test
  public void testListBuilder2() {
    // simulating unmarshaller logic
    ListType listType = PersonRecord_List.type;
    ListDatum.Builder listDatumBuilder = listType.createBuilder();

    @NotNull DatumType elementType = (DatumType) listType.elementType().type;

    final PersonRecord.Builder personBuilder = PersonRecord.create().setId(PersonId.create(1));
    PersonRecord.Builder.@NotNull Value personValue = personBuilder.asValue();

    final Data.Builder personDataBuilder = elementType.createDataBuilder();
    personDataBuilder._raw().setValue(
        elementType.self,
        personValue
    );

    listDatumBuilder._raw().elements().add(personDataBuilder);

    PersonRecord_List list = (PersonRecord_List) listDatumBuilder;

    final List<@Nullable ? extends PersonRecord> datums = list.datums();
    final PersonRecord record = datums.get(0);
    assertEquals(1, record.getId().getVal().intValue());
  }

  @Test
  public void testRecursiveOpProjection() {
    final OpDeleteVarProjection varProjection =
        UsersResourceDeclaration.recursiveTestDeleteOperationDeclaration.deleteProjection().varProjection();

    assertEquals(
        "[ ]( $recTest = :`record` ( bestFriend $recTest, +id ) )",
        printOpDeleteVarProjection(
            Qn.fromDotSeparated("ws.epigraph.tests"),
            UsersResourceDeclaration.INSTANCE.fieldName(),
            UsersResourceDeclaration.recursiveTestDeleteOperationDeclaration.name(),
            varProjection
        )
    );
  }

  @Test
  public void testGeneratedReqUpdateProjection() {
    UpdateUsersFieldProjection updateUsersFieldProjection = null;
    try {
      // this should simply compile
      UpdatePersonMapProjection updatePersonMapProjection = updateUsersFieldProjection.dataProjection();
      UpdatePersonProjection updatePersonProjection = updatePersonMapProjection.itemsProjection();
      updatePersonProjection.replace();
      UpdatePersonRecordProjection updatePersonRecordProjection = updatePersonProjection.record();
      updatePersonRecordProjection.firstName();
      updatePersonRecordProjection.lastName();
      updatePersonRecordProjection.replace();
    } catch (NullPointerException ignored) { }
  }

  public static @NotNull String printOpDeleteVarProjection(
      Qn namespace,
      String resourceName,
      String operationName,
      OpDeleteVarProjection projection) {

    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpDeleteProjectionsPrettyPrinter<NoExceptions> printer = new OpDeleteProjectionsPrettyPrinter<>(
        layouter,
        new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(
                new Namespaces(namespace).operationDeleteProjectionsNamespace(
                    resourceName,
                    operationName
                )
            )
        )
    );
    printer.printVar(projection, 0);
    layouter.close();
    return sb.getString();
  }
}
