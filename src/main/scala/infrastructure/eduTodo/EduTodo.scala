package infrastructure.eduTodo

import javax.inject.Singleton

import lepus.database.DatabaseConfig
import lepus.doobie.DatabaseModule

@Singleton
class Master extends DatabaseModule:
  val databaseConfig: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo/master")

@Singleton
class Slave extends DatabaseModule:
  val databaseConfig: DatabaseConfig = DatabaseConfig("lepus.database://edu_todo/slave")
