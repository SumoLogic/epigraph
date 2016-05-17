package com.sumologic.epigraph.ideaplugin.schema.brains.search;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.sumologic.epigraph.ideaplugin.schema.index.SchemaIndexUtil;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class InheritorsSearchTest extends LightCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "src/test/resources/testData/brains/search";
  }

  public void testDirectInheritors() {
    myFixture.configureByFile("InheritorsSearch.es");
    SchemaTypeDef typeDef = SchemaIndexUtil.findTypeDef(myFixture.getProject(), Collections.singleton("x"), "R1");
    assertNotNull(typeDef);
    Collection<SchemaTypeDef> directInheritors = SchemaDirectTypeInheritorsSearch.search(typeDef).findAll();
    assertEquals(1, directInheritors.size());
    SchemaTypeDef r2 = directInheritors.iterator().next();
    assertEquals("R2", r2.getName());
  }

  public void testInheritors() {
    myFixture.configureByFile("InheritorsSearch.es");
    SchemaTypeDef typeDef = SchemaIndexUtil.findTypeDef(myFixture.getProject(), Collections.singleton("x"), "R1");
    assertNotNull(typeDef);
    Collection<SchemaTypeDef> inheritors = SchemaTypeInheritorsSearch.search(typeDef).findAll();
    Set<String> names = inheritors.stream().map(SchemaTypeDef::getName).collect(Collectors.toSet());
    Set<String> expectedNames = new HashSet<>(Arrays.asList("R2", "R2"));
    assertEquals(expectedNames, names);
  }
}
