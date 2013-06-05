package hu.javaportal.test

import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSuite, BeforeAndAfter}

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.StringWriter
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import com.fasterxml.jackson.databind._

import com.fasterxml.jackson.core.{JsonGenerator, Version, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule
import net.fwbrasil.activate.entity.{Entity, Var}


object JsonSupport {

  class VarDeserializer extends JsonDeserializer[Var[_]] {
    override def deserialize(jp: JsonParser, ctxt: DeserializationContext, intoValue: Var[_]) = null

    def deserialize(jp: JsonParser, ctxt: DeserializationContext) = null
  }

  class EntitySerializer extends JsonSerializer[Entity] {
    def serialize(value: Entity, jgen: JsonGenerator, provider: SerializerProvider) {
      jgen.writeString(value.id)
    }
  }

  class ActivateEntityDeserializer extends SimpleModule("ActivateEntityDeserializer", new Version(1, 0, 0, null, "hu.finesolution.activate", "entityDeserializer")) {
    addDeserializer(classOf[Var[_]], new VarDeserializer)
    addSerializer(classOf[Entity], new EntitySerializer)
  }

  val jsonMapper = new ObjectMapper {
    registerModule(DefaultScalaModule)
    registerModule(new ActivateEntityDeserializer)
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
  }

  def parse[T](value: String)(implicit m: scala.Predef.Manifest[T]): T = {
    jsonMapper.reader(m.runtimeClass.asInstanceOf[Class[T]]).readValue(value.getBytes)
  }

  def parse[T](value: String, obj: T)(implicit m: scala.Predef.Manifest[T]): T = {
    jsonMapper.reader(m.runtimeClass.asInstanceOf[Class[T]]).withValueToUpdate(obj).readValue(value.getBytes())
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
      println("Serialized: " + roles)
    }
  }
  after {
    transactional {
      all[Test].foreach(_.delete)
    }
  }
}
