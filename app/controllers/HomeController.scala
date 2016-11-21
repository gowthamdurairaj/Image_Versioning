package controllers

import javax.inject.Inject

import models._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n._
import play.api.mvc._
import views._

class HomeController @Inject() (bookService: BookService)
  extends Controller {
  
  val bookForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
      "author" -> nonEmptyText,
      "description" -> nonEmptyText,
      "version" -> optional(longNumber)
    )(Book.apply)(Book.unapply)
  )
  
  // -- Actions

  // Home Page
  def index = Action { implicit request =>
    Ok(html.list(bookService.list()))
  }
  
  /**
   * Edit a book to new version.
   */
  def edit(id: Long, version: Long) = Action {
    bookService.findById(id).map { book =>
      Ok(html.editForm(id, version, bookForm.fill(book)))
    }.getOrElse(NotFound)
  }
  
   /**
   * Get all the versions of the Book.
   */
  def getHistory(isbn: Long) = Action {
        Ok(html.history(bookService.findHistory(isbn)))
  }
  
  /**
   * Update Request
   */
  def update(id: Long, version: Long) = Action { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.editForm(id,version, formWithErrors)),
      book => {
        bookService.update(id,version + 1, book)
            Ok(html.list(bookService.list()))
      }
    ) 
  }
  
  /**
   * Add a new Book
   */
  def create = Action {
    Ok(html.createForm(bookForm))
  }
  
  /**
   * Save New Request
   */
  def save = Action { implicit request =>
    bookForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.createForm(formWithErrors)),
      book => {
        bookService.insert(book)
        Ok(html.list(bookService.list())) 
      }
    )
  }

}
            
