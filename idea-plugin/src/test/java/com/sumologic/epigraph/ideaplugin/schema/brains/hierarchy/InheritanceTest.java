package com.sumologic.epigraph.ideaplugin.schema.brains.hierarchy;

import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class InheritanceTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/hierarchy";
  }

  public void testDirectInheritors() {
    myFixture.configureByFile("InheritanceSearch.esc");
    EpigraphTypeDef typeDef = findTypeDef("R1");
    Collection<EpigraphTypeDef> directInheritors = SchemaDirectTypeInheritorsSearch.search(typeDef).findAll();
    assertEquals(1, directInheritors.size());
    EpigraphTypeDef r2 = directInheritors.iterator().next();
    assertEquals("R2", r2.getName());
  }

  public void testInheritors() {
    myFixture.configureByFile("InheritanceSearch.esc");
    EpigraphTypeDef typeDef = findTypeDef("R1");
    Collection<EpigraphTypeDef> inheritors = SchemaTypeInheritorsSearch.search(typeDef).findAll();
    checkResults(inheritors, "R2", "R3", "R4");
  }

  public void testSupplements() {
    myFixture.configureByFile("InheritanceSearch2.esc");
    EpigraphTypeDef r4 = findTypeDef("R4");
    Collection<EpigraphTypeDef> inheritors = SchemaTypeInheritorsSearch.search(r4).findAll();
    checkUnorderedResults(inheritors, "R3", "R2", "R1"); // Do we need ordering for transitive inheritors?

    EpigraphTypeDef r1 = findTypeDef("R1");
    Collection<EpigraphTypeDef> parents = SchemaDirectTypeParentsSearch.search(r1).findAll();
    checkResults(parents, "R3");

    EpigraphTypeDef r3 = findTypeDef("R3");
    parents = SchemaDirectTypeParentsSearch.search(r3).findAll();
    checkResults(parents, "R4");

    inheritors = SchemaDirectTypeInheritorsSearch.search(r4).findAll();
    checkResults(inheritors, "R3");
  }

  public void testDirectParents() {
    myFixture.configureByFile("InheritanceSearch.esc");
    EpigraphTypeDef typeDef = findTypeDef("R2");
    Collection<EpigraphTypeDef> directInheritors = SchemaDirectTypeParentsSearch.search(typeDef).findAll();
    assertEquals(1, directInheritors.size());
    EpigraphTypeDef r2 = directInheritors.iterator().next();
    assertEquals("R1", r2.getName());
  }

  public void testParents() {
    myFixture.configureByFile("InheritanceSearch.esc");
    EpigraphTypeDef r3 = findTypeDef("R4");
    Collection<EpigraphTypeDef> parents = SchemaTypeParentsSearch.search(r3).findAll();
    checkResults(parents, "R3", "R2", "R1");
  }

  public void testCircularInheritors() {
    myFixture.configureByFile("Circular.esc");
    EpigraphTypeDef typeDef = findTypeDef("R1");
    Collection<EpigraphTypeDef> inheritors = SchemaTypeInheritorsSearch.search(typeDef).findAll();
    checkResults(inheritors, "R2", "R3", "R1");
  }

  public void testCircularParents() {
    myFixture.configureByFile("Circular.esc");
    EpigraphTypeDef typeDef = findTypeDef("R1");
    Collection<EpigraphTypeDef> inheritors = SchemaTypeParentsSearch.search(typeDef).findAll();
    checkResults(inheritors, "R3", "R2", "R1");
  }

  private EpigraphTypeDef findTypeDef(String name) {
    EpigraphTypeDef typeDef = SchemaIndexUtil.findTypeDef(
        myFixture.getProject(),
        Collections.singleton(new Fqn("x")),
        new Fqn(name),
        GlobalSearchScope.allScope(myFixture.getProject()));
    assertNotNull(typeDef);
    return typeDef;
  }

  private void checkResults(Collection<EpigraphTypeDef> result, String... expected) {
    List<String> names = result.stream().map(EpigraphTypeDef::getName).collect(Collectors.toList());
    List<String> expectedNames = Arrays.asList(expected);
    assertEquals(expectedNames, names);
  }

  private void checkUnorderedResults(Collection<EpigraphTypeDef> result, String... expected) {
    Set<String> names = result.stream().map(EpigraphTypeDef::getName).collect(Collectors.toSet());
    Set<String> expectedNames = new HashSet<>(Arrays.asList(expected));
    assertEquals(expectedNames, names);
  }
}
