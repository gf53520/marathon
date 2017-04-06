package mesosphere.marathon
package api.v2.validation

import mesosphere.{ UnitTest, ValidationTestLike }
import mesosphere.marathon.raml.{ App, Container, ContainerPortMapping, EngineType, Network, NetworkMode }

class AppValidationTest extends UnitTest with ValidationTestLike {

  "canonical app validation" when {

    implicit val basicValidator = AppValidation.validateCanonicalAppAPI(Set.empty)

    "multiple container networks are specified for an app" should {

      val app = App(id = "/foo", cmd = Some("bar"), networks = 1.to(2).map(i => Network(mode = NetworkMode.Container, name = Some(i.toString))))

      // we don't allow this yet because Marathon doesn't yet support per-network port-mapping (and it's not meaningful
      // for a single host port to map to the same container port on multiple network interfaces).
      "disallow containerPort to hostPort mapping" in {
        val ct = Container(`type` = EngineType.Mesos, portMappings = Some(Seq(ContainerPortMapping(hostPort = Option(0)))))
        val badApp = app.copy(container = Some(ct))
        shouldViolate(badApp, "/container/portMappings(0)/hostPort", "must be empty")
      }

      "allow portMappings that don't declare hostPort" in {
        val ct = Container(`type` = EngineType.Mesos, portMappings = Some(Seq(ContainerPortMapping())))
        val goodApp = app.copy(container = Some(ct))
        shouldSucceed(goodApp)
      }
    }
  }
}
