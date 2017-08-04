package ws.epigraph.tests.multimodule.foobarbaz;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.refs.StaticTypesResolver;

/**
 * @author yegor 2017-08-03.
 */
public class FooBarBazTest {

  private static final Logger logger = LoggerFactory.getLogger(FooBarBazTest.class);

  @Test
  public void testTypesResolver() {
    logger.info("Resolved static types: {}", StaticTypesResolver.instance().types());
  }

}
