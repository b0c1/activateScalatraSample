package hu.javaportal.test

import net.fwbrasil.activate.ActivateContext
import com.fasterxml.jackson.databind.{JsonNode, DeserializationFeature, SerializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.StringWriter

trait JacksonActivateContext {
  this: ActivateContext =>

  def mapper = new ObjectMapper {
    registerModule(DefaultScalaModule)
    registerModule(ActivateJacksonModule)
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
  }

  def parse[T](value: String)(implicit m: scala.Predef.Manifest[T]): T = {
    mapper.reader(m.runtimeClass.asInstanceOf[Class[T]]).readValue(value.getBytes)
  }

  def parse[T](value: String, obj: T)(implicit m: scala.Predef.Manifest[T]): T = {
    mapper.reader(m.runtimeClass.asInstanceOf[Class[T]]).withValueToUpdate(obj).readValue(value.getBytes())
  }


  def json(value: Any): String = {
    val writer = new StringWriter
    mapper.writeValue(writer, value)
    writer.toString
  }

  def createOrUpdateEntityFromJson[E <: Entity : Manifest](json: String): E = {
    mapper.readTree(json).get("id") match {
      case id: JsonNode =>
        val entity = byId[E](id.asText()).get
        parse[E](json, entity)
      case _ => parse[E](json)
    }

  }

  implicit class EntityJsonMethods[E <: Entity : Manifest](val entity: E) {
    def toJson = {
      json(entity)
    }

    def updateFromJson(json: String): E = {
      parse[E](json, entity)
    }
  }

}
