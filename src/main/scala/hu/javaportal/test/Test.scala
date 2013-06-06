package hu.javaportal.test

import h2Context._

object EnumerationValue extends Enumeration {

  case class EnumerationValue(name: String) extends Val(name)

  val value1a = EnumerationValue("v1")
  val value2 = EnumerationValue("v2")
  val value3 = EnumerationValue("v3")
}

import EnumerationValue._

class Test extends Entity {
  var name: String = ""
  var enum: EnumerationValue = _
}

class TestMigration extends Migration {
  def timestamp = 20130523001l

  def up = {
    table[Test].createTable(
      _.column[String]("name"),
      _.column[String]("enum")
    )
  }
}

