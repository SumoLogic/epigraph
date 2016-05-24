/* Created by yegor on 5/19/16. */

import com.sumologic.epigraph.std.{LocalNamespaceName, QualifiedNamespaceName}

package object epigraph {

  val ns: QualifiedNamespaceName = new QualifiedNamespaceName(None, new LocalNamespaceName("epigraph"))

}
