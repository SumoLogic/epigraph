/* Created by yegor on 5/19/16. */
package epigraph

import com.sumologic.epigraph.names.{LocalNamespaceName, QualifiedNamespaceName}

object `package` {

  val ns: QualifiedNamespaceName = new QualifiedNamespaceName(None, new LocalNamespaceName("epigraph"))

}
