package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.data._
import play.api.data.Forms._

import models.Task

object Application extends Controller {
  
  def index = Action {
    Redirect(routes.Application.tasks)
  }

  //Para usar el objeto en Json
  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "String").write[String] 
  )(unlift(Task.unapply))

  val taskForm = Form(
     "label" -> nonEmptyText
   )
  
  //Devuelve una coleccion JSON con la lista de tareas
  def tasks = Action {
      var json = Json.toJson(Task.all())
      Ok(json)
   }
  
  //Recibe el dato de la nueva tarea (descripcion) en un formulario.
  //Debe devolver un JSON con la descripcion de la nueva tarea creada 
  //y el codigo HTTP 201 (CREATED)  
  def newTask = Action { implicit request =>
     taskForm.bindFromRequest.fold(
       errors => BadRequest(views.html.index(Task.all(), errors)),
       label => {
          Task.create(label)
          var json = Json.toJson(Task.getLastTask())
          Created(json)
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

  //Devuelve la representacion JSON de la tarea cuyo identificador se pasa en al URI
  def searchTask(id: Long) = Action {
    var json = Json.toJson(Task.getTask(id))
    Ok(json)
  }
  
}