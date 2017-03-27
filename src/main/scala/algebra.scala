// Copyright: 2017 https://github.com/fommil/drone-dynamic-agents/graphs
// License: http://www.apache.org/licenses/LICENSE-2.0
package algebra

import java.util.UUID
import cats.free._
import freestyle._

object Drone {
  sealed trait Response
  case class WorkQueue(items: Int) extends Response
  case class WorkActive(items: Int) extends Response

  @free trait Services[F[_]] {
    def receiveWorkQueue(): FreeS[F, WorkQueue]
    def receiveActiveWork(): FreeS[F, WorkActive]
  }
}

object Container {
  sealed trait Ops[A]
  case class GetTime() extends Ops[String]
  case class GetNodes() extends Ops[List[UUID]]
  case class StartAgent() extends Ops[UUID]
  case class StopAgent(uuid: UUID) extends Ops[Unit]

  case class ReceiveKillEvent() extends Ops[UUID] // will be push

  // boilerplate
  class Services[F[_]](implicit I: Ops :<: F) {
    def getTime(): Free[F, String] = Free.inject[Ops, F](GetTime())
    def getNodes(): Free[F, List[UUID]] = Free.inject[Ops, F](GetNodes())
    def startAgent(): Free[F, UUID] = Free.inject[Ops, F](StartAgent())
    def stopAgent(uuid: UUID): Free[F, Unit] = Free.inject[Ops, F](StopAgent(uuid))
  }
  object Services {
    implicit def services[F[_]](implicit I: Ops :<: F): Services[F] = new Services
  }
}

object Audit {
  sealed trait Ops[A]
  case class Store(a: String) extends Ops[Unit]

  // boilerplate
  class Services[F[_]](implicit I: Ops :<: F) {
    def store(a: String): Free[F, Unit] = Free.inject[Ops, F](Store(a))
  }
  object Services {
    implicit def services[F[_]](implicit I: Ops :<: F): Services[F] = new Services
  }
}
