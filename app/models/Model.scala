package models

import java.util.Date
import javax.inject.Inject
import anorm.SqlParser._
import anorm._
import play.api.db.DBApi
import play.api.Play.current
import scala.language.postfixOps

case class Book(id: Option[Long],
                    name: String,
                    author: String,
                    description: String,
                    version: Option[Long])
                    
                    
class BookService @Inject() (dbapi: DBApi) {

  private val db = dbapi.database("default")

  val simple = {
    get[Option[Long]]("book.id") ~
      get[String]("book.name") ~
      get[String]("book.author") ~
      get[String]("book.description") ~
      get[Option[Long]]("book.version") map {
      case id~name~author~description~version =>
        Book(id, name, author, description, version)
    }
  }

  
    val complex = {
    get[Option[Long]]("history.id") ~
      get[String]("history.name") ~
      get[String]("history.author") ~
      get[String]("history.description") ~
      get[Option[Long]]("history.version") map {
      case id~name~author~description~version =>
        Book(id, name, author, description, version)
    }
  }

  /**
   * Retrieve a book from the id.
   */
  def findById(id: Long): Option[Book] = {
    db.withConnection { implicit connection =>
      SQL("select * from book where id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }

  /**
   *Fetch all the books
   */
  def list(): Seq[Book] = {
    db.withConnection { implicit connection =>
      SQL("select * from book").as(simple *)
    }
  }
  
  
    /**
   *Fetch all the book versions
   */
  def findHistory(id:Long): Seq[Book] = {
    db.withConnection { implicit connection =>
      SQL("select * from history where id = {id}").on('id -> id).as(complex *)
    }
  }

  /**
   * Update a book.
   */
  def update(id: Long, newVersion: Long, book:Book) = {
    db.withConnection { implicit connection =>
      SQL(
        """
          update book
          set name = {name}, author = {author}, description = {description}, version = {version}
          where id = {id}
        """
      ).on(
        'id -> id,
        'name -> book.name,
        'author -> book.author,
        'description -> book.description,
        'version -> newVersion
      ).executeUpdate()
    }
    
    db.withConnection { implicit connection =>
      SQL(
        """
          insert into history values (
            {version},{id},{name}, {author}, {description})
        """
      ).on(
        'id -> id,
        'name -> book.name,
        'author -> book.author,
        'description -> book.description,
        'version -> newVersion
      ).executeUpdate()
    }
  }

  /**
   * Insert a new book.
   */
  
  
  def insert(book:Book) = {
    db.withConnection { implicit connection =>
      SQL(
        """
          insert into book values (
            {id},{name}, {author}, {description},{version})
        """
      ).on(
        'id -> book.id,
        'name -> book.name,
        'author -> book.author,
        'description -> book.description,
        'version -> 1
      ).executeUpdate()
    }
    
     db.withConnection { implicit connection =>
      SQL(
        """
          insert into history values (
            {version}, {id}, {name}, {author}, {description})
        """
      ).on(
        'id -> book.id,
        'name -> book.name,
        'author -> book.author,
        'description -> book.description,
        'version -> 1
      ).executeUpdate()
    }
        
  }

}
