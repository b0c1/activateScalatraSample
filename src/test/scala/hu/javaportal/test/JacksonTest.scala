package hu.javaportal.test

import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSuite, BeforeAndAfter}

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith


import h2Context._


@RunWith(classOf[JUnitRunner])
class JacksonTest extends FunSuite with MockitoSugar with BeforeAndAfter {
  transactional {}

  test("Deserialization test") {
    val json = """{"name":"hello"}"""
    transactional {
      val result = JsonSupport.parse[Test](json)
      println("Deserialized first: " + all[Test])
      JsonSupport.parse[Test]( """{"name":"bizz"}""", result)
      println("Deserialized second: " + all[Test])
    }
    transactional {
      val roles = all[Test]
      println("Deserialization read back: " + roles)
      JsonSupport.json(roles) === """[{"name":"hello"}]"""
    }
  }
  test("Serialization test") {
    transactional {
      val t = new Test
      t.name = "hello"
    }

    transactional {
      val roles = all[Test]
      JsonSupport.json(roles) === """[{"name":"hello"}]"""
      println("Serialized: " + JsonSupport.json(roles))
    }
  }

  after {
    transactional {
      all[Test].foreach(_.delete)
    }
  }
}
