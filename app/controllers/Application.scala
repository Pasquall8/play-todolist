package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.data._
import play.api.data.Forms._

import models.Task
import models.User

object Application extends Controller {
  
  def index = Action {
    Redirect(routes.Application.tasks)
  }

  //Para usar el objeto en Json
  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "String").write[String] and
    (JsPath \ "taskOwner").write[String]
  )(unlift(Task.unapply))

  val taskForm = Form(
     "label" -> nonEmptyText
   )
  
  //Devuelve una coleccion JSON con la lista de tareas
  def tasks = Action {
      var json = Json.toJson(Task.all())
      Ok(json)
   }

   //Devuelve el JSON con la lista de la tarea de un usuario
   def tasksUser(taskOwner: String) = Action {
    if(User.exists(taskOwner)){
      Ok(Json.toJson(Task.all(taskOwner)))
    }
    else {BadRequest("El usuario: " + taskOwner + " no existe")}
   }
  
  //Recibe el dato de la nueva tarea (descripcion) en un formulario.
  //Debe devolver un JSON con la descripcion de la nueva tarea creada 
  //y el codigo HTTP 201 (CREATED)
   def newTask = Action { implicit request =>
     taskForm.bindFromRequest.fold(
       errors => BadRequest("Datos incorrectos"),
       label => {
          Task.create(label) match {
            case Some(id) => Created(Json.toJson(Task.getTask(id)))
            case None => InternalServerError("La tarea no ha sido encontrada")
          }
       }
     )
   }

  //Ademas introduce el usuario  
  def newUserTask(taskOwner: String) = Action { implicit request =>
     taskForm.bindFromRequest.fold(
       errors => BadRequest("Datos incorrectos"),
       label => {
          if(User.exists(taskOwner)){
            Task.create(label, taskOwner) match {
              case Some(id) => Created(Json.toJson(Task.getTask(id)))
              case None => InternalServerError("La tarea no ha sido encontrada")
            }
          }
          else NotFound("El usuario no existe")          
       }
     )
   }
  //Borra una tarea cuo identificador se pasa en la URI. 
  //Si la tarea no existe debe evolver un codigo HTTP 404 (NOT FOUND)
  def deleteTask(id: Long) = Action {
    var cons = Task.getTask(id)
    if(cons == Nil)
      NotFound("404 Tarea no encontrada")
    else{
      Task.delete(id)
      Ok("Se ha borrado la tarea correctamente")
    }
  }

  //Devuelve la representacion JSON de la tarea cuyo identificador se pasa en al URL
  def searchTask(id: Long) = Action {
    val task:Option[Task] = Task.getTask(id)
    task match {
       case Some(t) => Ok(Json.toJson(t))
       case None => NotFound("404 Tarea no encontrada")
    }
  }
  
}