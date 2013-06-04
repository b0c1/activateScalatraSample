package hu.javaportal.test

import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSuite, BeforeAndAfter}

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.StringWriter
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import com.fasterxml.jackson.databind.{DeserializationFeature, SerializationFeature, PropertyNamingStrategy, ObjectMapper}
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility

object JsonSupport {
  val jsonMapper = new ObjectMapper {
    registerModule(DefaultScalaModule)
    setVisibility(PropertyAccessor.ALL, Visibility.NONE)
    setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
    configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
    configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
  }

  def parse[T](value: String)(implicit m: scala.Predef.Manifest[T]): Option[T] = {
    if (m.runtimeClass.isInstanceOf[Class[T]]) {
      println(new String(value.getBytes))
      Some(jsonMapper.readValue(value.getBytes, m.runtimeClass.asInstanceOf[Class[T]]))
    } else {
      None
    }
  }

  def json(value: Any): String = {
    val writer = new StringWriter
    jsonMapper.writeValue(writer, value)
    writer.toString
  }

  def apply(value: Any): String = {
    json(value)
  }
}

import h2Context._

@RunWith(classOf[JUnitRunner])
class JacksonTest extends FunSuite with MockitoSugar with BeforeAndAfter {
  transactional {}

  test("Deserialization test") {
    val json = """{"name":"hello"}"""
    transactional {
      val result = JsonSupport.parse[Test](json)

    }
    transactional {
      val roles = all[Test]
      JsonSupport.json(roles) === """[{"name":"hello"}]"""
    }
  }
  test("Serialization test") {
    transactional {
      new Test("hello")
    }

    transactional {
      val roles = all[Test]
      JsonSupport.json(roles) === """[{"name":"hello"}]"""
    }
  }
  after {
    transactional {
      all[Test].foreach(_.delete)
    }
  }
}
