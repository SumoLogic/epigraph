/* Created by yegor on 5/19/16. */

import com.sumologic.epigraph.std.{LocalNamespaceName, QualifiedNamespaceName}

package object xp {

  val ns: QualifiedNamespaceName = new QualifiedNamespaceName(None, new LocalNamespaceName("epigraph"))

}
