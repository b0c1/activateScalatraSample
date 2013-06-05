package hu.javaportal.test

import com.fasterxml.jackson.databind.{DeserializationFeature, SerializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.StringWriter

object JsonSupport {

  val jsonMapper = new ObjectMapper {
    registerModule(DefaultScalaModule)
    registerModule(ActivateJacksonModule)
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
