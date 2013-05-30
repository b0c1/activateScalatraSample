package hu.javaportal.test

import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.storage.relational.PooledJdbcRelationalStorage
import net.fwbrasil.activate.storage.relational.idiom.h2Dialect
import net.fwbrasil.activate.json4s.Json4sContext

object h2Context extends ActivateContext with Json4sContext {
  val storage = new PooledJdbcRelationalStorage {
    val jdbcDriver = "org.h2.Driver"
    val user = ""
    val password = ""
    val url = "jdbc:h2:mem:my_database;DB_CLOSE_DELAY=-1"
    val dialect = h2Dialect
  }
  protected val jsonMethods = org.json4s.jackson.JsonMethods
}