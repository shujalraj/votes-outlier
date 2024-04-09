package com.exercise
import org.scalatest.flatspec.AnyFlatSpec
import java.sql.{Connection, DriverManager, Statement}

class ExerciseSpec extends AnyFlatSpec {
 "Database" should "accept statements" in {
     var row1InsertionCheck = false
     val conn: Connection = DriverManager.getConnection("jdbc:sqlite:./warehouse-test.db")
     val stm: Statement = conn.createStatement
     val rs = stm.executeQuery("select 1")
     rs.next
     assert((1 == rs.getInt(1)), "Statement didn't execute correctly")
     conn.close()
 }   
}