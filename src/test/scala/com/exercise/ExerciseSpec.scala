package com.exercise
import org.scalatest.flatspec.AnyFlatSpec
import java.sql.{Connection, DriverManager, Statement}

class ExerciseSpec extends AnyFlatSpec {
  val expectedTotalCountVotes: Int = 40299 // Change this to your expected total count
  val expectedTotalCountVO: Int = 163 // Change this to your expected total count
 "Database" should "accept statements" in {
     var row1InsertionCheck = false
     val conn: Connection = DriverManager.getConnection("jdbc:sqlite:./warehouse-test.db")
     val stm: Statement = conn.createStatement
     val rs = stm.executeQuery("select 1")
     rs.next
     assert((1 == rs.getInt(1)), "Statement didn't execute correctly")
     conn.close()
 }
  "Table" should "should exists" in {
    val conn: Connection = DriverManager.getConnection("jdbc:sqlite:./warehouse-test.db")
    val metaData = conn.getMetaData
    val tables = metaData.getTables(null, null, "votes", null)
    assert(tables.next(), "Votes table does not exist")
    conn.close()
  }

  "Total count on votes data" should "match expected count" in {
    val conn: Connection = DriverManager.getConnection("jdbc:sqlite:./warehouse-test.db")
    val stm: Statement = conn.createStatement
    val rs = stm.executeQuery("SELECT COUNT(*) FROM votes")
    rs.next
    val count: Int = rs.getInt(1)
    assert(count == expectedTotalCountVotes, s"Expected total count $expectedTotalCountVotes, but found $count")
    conn.close()
  }

  "outlier_weeks table" should "exist" in {
    val conn: Connection = DriverManager.getConnection("jdbc:sqlite:./warehouse-test.db")
    val metaData = conn.getMetaData
    val tables = metaData.getTables(null, null, "outlier_weeks", null)
    assert(tables.next(), "outlier_weeks table does not exist")
    conn.close()
  }

  "Total count on outlier_weeks data" should "match expected count" in {
    val conn: Connection = DriverManager.getConnection("jdbc:sqlite:./warehouse-test.db")
    val stm: Statement = conn.createStatement
    val rs = stm.executeQuery("SELECT COUNT(*) FROM outlier_weeks")
    rs.next
    val count: Int = rs.getInt(1)
    assert(count == expectedTotalCountVO, s"Expected total count $expectedTotalCountVO in outlier_weeks table, but found $count")
    conn.close()
  }



}