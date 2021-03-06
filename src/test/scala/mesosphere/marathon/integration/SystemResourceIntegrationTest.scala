package mesosphere.marathon
package integration

import mesosphere.AkkaIntegrationTest
import mesosphere.marathon.integration.setup.EmbeddedMarathonTest
import org.slf4j.LoggerFactory

/**
  * Integration tests for non-app / non-pod end points such as /ping and /metrics
  */
@IntegrationTest
class SystemResourceIntegrationTest extends AkkaIntegrationTest with EmbeddedMarathonTest {

  private[this] val log = LoggerFactory.getLogger(getClass)

  //clean up state before running the test case
  before(cleanUp())

  "Marathon" should {
    "responses to a ping" in {

      When("The system is pinged")
      val result = marathon.ping()

      Then("The system responses with a http 200 pong")
      result.code should be(200)
      result.entityString should be("pong")
    }
  }
}
