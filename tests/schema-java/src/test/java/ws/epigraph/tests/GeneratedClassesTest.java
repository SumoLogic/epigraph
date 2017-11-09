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
import epigraph.annotations.Doc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import ws.epigraph.data.Data;
import ws.epigraph.data.ListDatum;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.ProjectionsPrettyPrinterContext;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.*;
import ws.epigraph.refs.QnTypeRef;
import ws.epigraph.refs.StaticTypesResolver;
import ws.epigraph.schema.Namespaces;
import ws.epigraph.schema.operations.OperationKind;
import ws.epigraph.tests._resources.users.UsersResourceDeclaration;
import ws.epigraph.tests._resources.users.operations.update._default.update.UpdatePersonMapProjection;
import ws.epigraph.tests._resources.users.operations.update._default.update.UpdateUsersFieldProjection;
import ws.epigraph.tests._resources.users.operations.update._default.update.elements.UpdatePersonProjection;
import ws.epigraph.tests._resources.users.operations.update._default.update.elements.record.UpdatePersonRecordProjection;
import ws.epigraph.tests.codegenstress._projections.output.sub.OutputSubUserRecordProjection;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.ListType;

import java.util.List;

import static org.junit.Assert.*;

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
        elementType.self(),
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
    final @NotNull OpEntityProjection varProjection =
        UsersResourceDeclaration.recursiveTestDeleteOperationDeclaration.deleteProjection().entityProjection();

    assertEquals(
        "[ ]( $recTest = :`record` ( bestFriend $recTest ) )",
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

  @Test
  public void testTypeAnnotation() {
    AvroType.Imm avroType = PersonRecord.type.annotations().get(AvroType.type);
    assertNotNull(avroType);
    assertEquals("Person", avroType.getVal());
  }

  @Test
  public void testTagAnnotation() {
    Doc.Imm doc = Person.id.annotations().get(Doc.type);
    assertNotNull(doc);
    assertEquals("Person ID model", doc.getVal());
  }

  @Test
  public void testFieldAnnotation() {
    AvroField.Imm avroField = PersonRecord.id.annotations().get(AvroField.type);
    assertNotNull(avroField);
    assertEquals("personId", avroField.getVal());
  }

  @Test
  public void testIndex() {
    assertNotNull(StaticTypesResolver.instance()
        .resolve(new QnTypeRef(Qn.fromDotSeparated("ws.epigraph.tests.PersonMap"))));
  }

  @Test
  public void testStringFieldSetter() {
    CharSequence fn = "foo";
    PersonRecord.create().setFirstName(fn); // should accept CharSequence
  }

  @Test
  public void testPrimitiveProjectionParamGetter() {
    try {
      ws.epigraph.tests._resources.users.operations.read.bestfriend.output.record.OutputPersonRecordProjection p = null;
      UserId u = p.getParamParameter(); // should not be Long
    } catch (NullPointerException ignored) {
    }
  }

  @Test
  public void testOpOutputDefaults() {
    OpFieldProjection fieldProjection =
        ws.epigraph.tests._resources.users.UsersResourceDeclaration.readOperationDeclaration.outputProjection();
    OpEntityProjection entityProjection = fieldProjection.entityProjection();
    assertFalse(entityProjection.flag());

    OpTagProjectionEntry tpe = entityProjection.singleTagProjection(); // self
    assertNotNull(tpe);

    OpModelProjection<?, ?, ?, ?> modelProjection = tpe.projection();
    assertFalse(modelProjection.flag());
    assertTrue(modelProjection instanceof OpMapModelProjection);
    OpMapModelProjection mapModelProjection = (OpMapModelProjection) modelProjection;
    entityProjection = mapModelProjection.itemsProjection();
    assertFalse(entityProjection.flag());

    tpe = entityProjection.tagProjection("id");
    assertNotNull(tpe);
    assertTrue(tpe.projection().flag());

    tpe = entityProjection.tagProjection("record");
    assertNotNull(tpe);
    modelProjection = tpe.projection();
    assertFalse(modelProjection.flag());
    assertTrue(modelProjection instanceof OpRecordModelProjection);
    OpRecordModelProjection recordModelProjection = (OpRecordModelProjection) modelProjection;

    OpFieldProjectionEntry fpe = recordModelProjection.fieldProjection("id");
    assertNotNull(fpe);
    assertTrue(fpe.fieldProjection().flag());

    fpe = recordModelProjection.fieldProjection("firstName");
    assertNotNull(fpe);
    assertFalse(fpe.fieldProjection().flag());

    fpe = recordModelProjection.fieldProjection("lastName");
    assertNotNull(fpe);
    assertFalse(fpe.fieldProjection().flag());

    fpe = recordModelProjection.fieldProjection("bestFriend");
    assertNotNull(fpe);
    assertTrue(fpe.fieldProjection().flag());
  }

  @Test
  public void testNamedDoubleTail() {
    OutputSubUserRecordProjection p = null;
    // should compile, all fields must be present
    try {
      p.lastName();
      p.firstName();
      p.id();
    } catch (NullPointerException ignored) {}
  }

  private static @NotNull String printOpDeleteVarProjection(
      Qn namespace,
      String resourceName,
      String operationName,
      OpEntityProjection projection) {

    StringBackend sb = new StringBackend(120);
    Layouter<NoExceptions> layouter = new Layouter<>(sb, 2);
    OpProjectionsPrettyPrinter<NoExceptions> printer = new OpProjectionsPrettyPrinter<>(
        layouter,
        new ProjectionsPrettyPrinterContext<>(
            ProjectionReferenceName.fromQn(
                new Namespaces(namespace).operationDeleteProjectionsNamespace(
                    resourceName,
                    OperationKind.DELETE,
                    operationName
                )
            ),
            null
        )
    );
    printer.printEntity(projection, 0);
    layouter.close();
    return sb.getString();
  }
}
