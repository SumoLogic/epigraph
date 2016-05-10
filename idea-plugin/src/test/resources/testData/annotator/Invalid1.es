namespace some

import <error descr="Unresolved reference">some.other</error>
import <error descr="Unresolved reference">foo</error>
import <error descr="Unresolved reference">bar</error>.*

record R extends <error descr="Unresolved reference">Q</error>

long L extends <error descr="Wrong parent type kind">R</error>