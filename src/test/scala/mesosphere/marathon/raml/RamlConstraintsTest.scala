package mesosphere.marathon
package raml

import mesosphere.UnitTest
import play.api.data.validation.ValidationError
import play.api.libs.json.{ JsPath, JsResultException, Json }

class RamlConstraintsTest extends UnitTest {

  "RamlConstraints" when {
    "keyPattern" should {
      "validate pod environment variables" in {
        // pod environment variables use strict validation for variable names, based on keyPattern
        val raw =
          """
            |{
            |  "id": "/foo",
            |  "containers": [
            |    {
            |      "name": "c1",
            |      "type": "MESOS",
            |      "image": { "kind": "DOCKER", "id": "busybox" },
            |      "resources": { "cpus": 0.1, "mem": 64 },
            |      "environment": { "bad variable name" : "some value" }
            |    }
            |  ]
            |}
          """.stripMargin

        val errorPath = JsPath \ "containers" \ 0 \ "environment" \ "bad variable name"
        val expectedError = errorPath -> Seq(ValidationError("error.pattern"))

        val ex = intercept[JsResultException](Json.parse(raw).as[Pod])
        ex.errors should contain(expectedError)
      }
    }
  }
}
