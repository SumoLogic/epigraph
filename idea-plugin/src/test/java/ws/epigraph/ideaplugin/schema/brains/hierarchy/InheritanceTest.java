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

package ws.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import ws.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import ws.epigraph.lang.Qn;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class InheritanceTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/hierarchy";
  }

  public void testDirectInheritors() {
    myFixture.configureByFile("InheritanceSearch.esc");
    SchemaTypeDef typeDef = findTypeDef("R1");
    Collection<SchemaTypeDef> directInheritors = SchemaDirectTypeInheritorsSearch.search(typeDef).findAll();
    assertEquals(1, directInheritors.size());
    SchemaTypeDef r2 = directInheritors.iterator().next();
    assertEquals("R2", r2.getName());
  }

  public void testInheritors() {
    myFixture.configureByFile("InheritanceSearch.esc");
    SchemaTypeDef typeDef = findTypeDef("R1");
    Collection<SchemaTypeDef> inheritors = SchemaTypeInheritorsSearch.search(typeDef).findAll();
    checkResults(inheritors, "R2", "R3", "R4");
  }

  public void testSupplements() {
    myFixture.configureByFile("InheritanceSearch2.esc");
    SchemaTypeDef r4 = findTypeDef("R4");
    Collection<SchemaTypeDef> inheritors = SchemaTypeInheritorsSearch.search(r4).findAll();
    checkUnorderedResults(inheritors, "R3", "R2", "R1"); // Do we need ordering for transitive inheritors?

    SchemaTypeDef r1 = findTypeDef("R1");
    Collection<SchemaTypeDef> parents = SchemaDirectTypeParentsSearch.search(r1).findAll();
    checkResults(parents, "R3");

    SchemaTypeDef r3 = findTypeDef("R3");
    parents = SchemaDirectTypeParentsSearch.search(r3).findAll();
    checkResults(parents, "R4");

    inheritors = SchemaDirectTypeInheritorsSearch.search(r4).findAll();
    checkResults(inheritors, "R3");
  }

  public void testDirectParents() {
    myFixture.configureByFile("InheritanceSearch.esc");
    SchemaTypeDef typeDef = findTypeDef("R2");
    Collection<SchemaTypeDef> directInheritors = SchemaDirectTypeParentsSearch.search(typeDef).findAll();
    assertEquals(1, directInheritors.size());
    SchemaTypeDef r2 = directInheritors.iterator().next();
    assertEquals("R1", r2.getName());
  }

  public void testParents() {
    myFixture.configureByFile("InheritanceSearch.esc");
    SchemaTypeDef r3 = findTypeDef("R4");
    Collection<SchemaTypeDef> parents = SchemaTypeParentsSearch.search(r3).findAll();
    checkResults(parents, "R3", "R2", "R1");
  }

  public void testCircularInheritors() {
    myFixture.configureByFile("Circular.esc");
    SchemaTypeDef typeDef = findTypeDef("R1");
    Collection<SchemaTypeDef> inheritors = SchemaTypeInheritorsSearch.search(typeDef).findAll();
    checkResults(inheritors, "R2", "R3", "R1");
  }

  public void testCircularParents() {
    myFixture.configureByFile("Circular.esc");
    SchemaTypeDef typeDef = findTypeDef("R1");
    Collection<SchemaTypeDef> inheritors = SchemaTypeParentsSearch.search(typeDef).findAll();
    checkResults(inheritors, "R3", "R2", "R1");
  }

  private SchemaTypeDef findTypeDef(String name) {
    SchemaTypeDef typeDef = SchemaIndexUtil.findTypeDef(
        myFixture.getProject(),
        Collections.singleton(new Qn("x")),
        new Qn(name),
        GlobalSearchScope.allScope(myFixture.getProject()));
    assertNotNull(typeDef);
    return typeDef;
  }

  private void checkResults(Collection<SchemaTypeDef> result, String... expected) {
    List<String> names = result.stream().map(SchemaTypeDef::getName).collect(Collectors.toList());
    List<String> expectedNames = Arrays.asList(expected);
    assertEquals(expectedNames, names);
  }

  private void checkUnorderedResults(Collection<SchemaTypeDef> result, String... expected) {
    Set<String> names = result.stream().map(SchemaTypeDef::getName).collect(Collectors.toSet());
    Set<String> expectedNames = new HashSet<>(Arrays.asList(expected));
    assertEquals(expectedNames, names);
  }
}
