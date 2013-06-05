package hu.javaportal.test

import h2Context._
import scala.beans.BeanProperty

class Test extends Entity {
  var name: String = ""
}

class TestMigration extends Migration {
  def timestamp = 20130523001l

  def up = {
    table[Test].createTable(
      _.column[String]("name")
    )
  }
}

