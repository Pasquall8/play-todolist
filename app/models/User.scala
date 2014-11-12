package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class User(login: String, first_name: String, last_name: String, e_mail: String)

object User {

   val user = {
     get[String]("login") ~ 
     get[String]("first_name") ~
     get[String]("last_name") ~
     get[String]("e_mail") map {
       case login~first_name~last_name~e_mail => User(login, first_name, last_name, e_mail)
     }
   }

   /*def exists(login: String): Long = 
      DB.withConnection { implicit c =>
         SQL("select count(*) from user_task where login = {login}").on(
             'login -> login
             ).as(scalar[Long].single)
   }*/

   def exists(login: String): Boolean =
   {
      readOption(login) match
      {
         case Some(_) => true
         case None => false
      }
   }

   def readOption(login: String): Option[User]={
      DB.withConnection { implicit c =>
         SQL("select * from user_task where login = {login}").on(
            'login -> login
         ).as(User.user.singleOpt)
   
      }
   }
}