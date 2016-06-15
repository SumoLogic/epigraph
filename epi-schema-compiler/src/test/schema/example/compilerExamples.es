namespace example

import epigraph.String
import epigraph.Integer
import epigraph.Long
import epigraph.Double
import epigraph.Boolean

map[String, Integer] StringToIntegerMap {

}

record Foo123 extends epigraph.schema.BooleanTypeData {

  `map`: map[String, Long]
  `list`: list[map[list[Integer], list[String]]]

}

vartype Bar234 extends epigraph.schema.ByNameRef {
  `id`: Integer
  `record`: Foo123
}
