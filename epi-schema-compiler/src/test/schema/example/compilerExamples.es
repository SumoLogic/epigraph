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

string S1

string S2 extends S1

string S3a extends S2 supplements S1

string S3b extends S2 supplements S1
