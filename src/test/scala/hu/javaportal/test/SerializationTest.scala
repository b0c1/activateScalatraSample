package hu.javaportal.test

import org.scalatra.ScalatraServlet
import org.json4s.{DefaultFormats, Formats}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest.ScalatraFunSuite
import org.scalatest.mock.MockitoSugar
import org.scalatest.BeforeAndAfter
import org.scalatra.json.JacksonJsonSupport


import h2Context._

case class Wrapper[T](success: Boolean, data: T)

class SerializationServlet extends ScalatraServlet with JacksonJsonSupport {
  protected implicit def jsonFormats: Formats = DefaultFormats + new EntityJson4sSerializer[Test]

  before() {
    contentType = formats("json")
  }

  post("/bad") {
    transactional {
      val t = createEntityFromJson[Test](request.body)
      Wrapper(true, t)
    }
  }

  post("/ok") {
    transactional {
      val t = createEntityFromJson[Test](request.body)
      parse(t.toJson)
    }
  }

  post("/bad2") {
    val t = transactional {
      createEntityFromJson[Test](request.body)
    }
    t
  }
}

@RunWith(classOf[JUnitRunner])
class SerializationTest extends ScalatraFunSuite with MockitoSugar with BeforeAndAfter {
  transactional {}

  addServlet(new SerializationServlet, "/*")

  test("Wrapper serialization problem") {
    println("Start test")
    post("/bad", """{"name":"test"}""", headers = Map("Content-Type" -> "application/json")) {
      status should equal(200)
    }
  }

  test("Serialization with string result") {
    post("/ok", """{"name":"test"}""", headers = Map("Content-Type" -> "application/json")) {
      status should equal(200)
      println(body)
      body should include( """"name":"test"}""")
    }
  }

  test("Serialization problem") {
    println("Start test")
    post("/bad2", """{"name":"test"}""", headers = Map("Content-Type" -> "application/json")) {
      status should equal(200)
      println(body)
      body should include( """"name":"test"}""")
    }
  }
}
