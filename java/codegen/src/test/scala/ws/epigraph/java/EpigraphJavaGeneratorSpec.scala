package ws.epigraph.java

import java.io.File

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}
import ws.epigraph.compiler.CContext
import ws.epigraph.java.Settings.{ClientSettings, ServerSettings}

/**
 * @author yegor 2017-08-28.
 */
@RunWith(classOf[JUnitRunner])
class EpigraphJavaGeneratorSpec extends FlatSpec with Matchers {

  val ctx = new CContext()
  val settings = new Settings(new ServerSettings(false, null, null), new ClientSettings(false, null), true)

  val fooBar = new File("/foo/bar")
  val fooBarBaz = new File("/foo/bar/baz")
  val fooBarQux = new File("/foo/bar/qux")

  "EpigraphJavaGenerator constructor" should "fail if resources dir is nested under java dir" in {
    an[IllegalArgumentException] should be thrownBy
        new EpigraphJavaGenerator(ctx, fooBar, fooBarBaz, settings)
  }

  it should "fail if java dir is nested under resources dir" in {
    an[IllegalArgumentException] should be thrownBy
        new EpigraphJavaGenerator(ctx, fooBarBaz, fooBar, settings)
  }

  it should "work if resources dir is the same as java dir" in {
    noException should be thrownBy
        new EpigraphJavaGenerator(ctx, fooBar, fooBar, settings)
  }

  it should "work if resources and java dir are not nested" in {
    noException should be thrownBy
        new EpigraphJavaGenerator(ctx, fooBarBaz, fooBarQux, settings)
  }

}
