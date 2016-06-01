namespace some

import <error descr="Unresolved reference">some.other</error>
import <error descr="Unresolved reference">foo</error>
import <error descr="Unresolved reference">bar</error>.*

record R extends <error descr="Unresolved reference">Q</error>

long L extends <error descr="Wrong parent type kind">R</error>

record <error descr="Circular inheritance">R1</error> extends R2
record <error descr="Circular inheritance">R2</error> extends R1

record Q1 {
  <error descr="field overrides nothing">override</error> f: R
}

vartype V1 {
  <error descr="tag overrides nothing">override</error> t: R
}
