package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class Task(id: Long, label: String, taskOwner: String)

object Task {

   val task = {
     get[Long]("id") ~ 
     get[String]("label") ~//map{
     get[String]("taskOwner") map {
       case id~label~taskOwner => Task(id, label, taskOwner)
       //case id~label => Task(id, label)
     }
   }
  
  def all(): List[Task] = DB.withConnection { implicit c =>
     SQL("select * from task").as(task *)
   }

   def all(taskOwner: String) = DB.withConnection { implicit c =>
     SQL("select * from task where taskOwner = {taskOwner}").on (
         'taskOwner -> taskOwner
      ).as(task *)
   }
  
  def create(label: String): Option[Long]={
      create(label,"anonymous")
  }

  def create(label: String, taskOwner: String): Option[Long]={
      DB.withConnection { implicit c =>
       SQL("insert into task (label, taskOwner) values ({label},{taskOwner})").on(
         'label -> label, 
         'taskOwner -> taskOwner
       ).executeInsert()
     }
  }
  
  def delete(id: Long) {
      DB.withConnection { implicit c =>
       SQL("delete from task where id = {id}").on(
         'id -> id
       ).executeUpdate()
     }
  }

  //Devolver tarea con el id pasado por parametros
  def getTask(id: Long): Option[Task]={
      DB.withConnection { implicit c =>
         SQL("select * from task where id = {id}").on(
            'id -> id
         ).as(Task.task.singleOpt)
      }
   }

  //Devolver la ultima tarea introducida
  def getLastTask(): List[Task]={
      DB.withConnection { implicit c =>
         SQL("select * from task where id = (select max(id) from task)").as(task *)
      }
   }
}