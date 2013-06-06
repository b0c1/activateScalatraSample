package hu.javaportal.test

import h2Context._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import org.scalatra.test.scalatest.ScalatraFunSuite
import org.scalatra.ScalatraServlet

class TestServlet extends ScalatraServlet {
  post("/") {
    import JacksonJsonContext._
    transactional {
      contentType = "application/json"
      val entity = createOrUpdateEntityFromJson[Test](request.body)
      entity.entityToJson
    }
  }
}

@RunWith(classOf[JUnitRunner])
class ScalatraTest extends ScalatraFunSuite with MockitoSugar with BeforeAndAfter {
  transactional {}
  addServlet(new TestServlet, "/*")

  test("Create") {
    post("/", """{"name":"Hi there"}""") {
      status should equal(200)
      println("Body is:"+body)
    }
  }

  after {
    transactional {
      all[Test].foreach(_.delete)
    }
  }
}
