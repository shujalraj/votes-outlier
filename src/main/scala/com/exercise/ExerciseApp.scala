package com.exercise
import scala.io.Source
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession

object ExerciseApp extends App {
      val spark = SparkSession
        .builder()
        .appName("calculate_outlier")
        .master("local[*]") // Change to your desired master URL for cluster deployment
        .getOrCreate()

      args.length match {
            case 0 => {
                  println("Usage: operation [ingest|outliers] args")
            }
            case _ => {
                  args(0) match {
                        case "ingest" => performIngestion(args)
                        case "outliers" => executeOutliersQuery()
                        case _ => {
                              println("Usage: operation [ingest|outliers] args")
                        }
                  }
            }
      }

      def performIngestion(args: Array[String]): Unit = {
            try {
                  val schema = StructType(List(StructField("Id", StringType, nullable = true)
                        , StructField("PostId", StringType, nullable = true)
                        , StructField("VoteTypeId", StringType, nullable = true)
                        , StructField("CreationDate", TimestampType, nullable = true)))
                  val votesDF = spark.read.schema(schema).json(args(1))

                  //Cleanup table and directory
                  spark.sql("DROP TABLE IF EXISTS votes")
                  val directoryPath = "spark-warehouse/votes"
                  val hadoopConf = spark.sparkContext.hadoopConfiguration
                  val fs = org.apache.hadoop.fs.FileSystem.get(hadoopConf)
                  val pathToDelete = new org.apache.hadoop.fs.Path(directoryPath)
                  if (fs.exists(pathToDelete)) {
                        fs.delete(pathToDelete, true)
                        println(s"Directory $directoryPath deleted successfully.")
                  } else {
                        println(s"Directory $directoryPath does not exist.")
                  }

                  //Writing Data to internal table on disk
                  votesDF.write.mode("overwrite").format("parquet").saveAsTable("votes")
            }
            catch {
                  case e: org.apache.spark.sql.AnalysisException =>
                        println("AnalysisException occurred: " + e.getMessage)
                  case e: java.io.FileNotFoundException =>
                        println("FileNotFoundException occurred: " + e.getMessage)
                  case e: java.io.IOException =>
                        println("IOException occurred: " + e.getMessage)
                  case e: Exception => println(s"An error occurred: ${e.getMessage}")
            }
      }

      def executeOutliersQuery(): Unit = {
            try {
                  val votesDF = spark.read.parquet("spark-warehouse/votes/")
                  val votesByYearAndWeekDF = votesDF.groupBy(year(col("CreationDate")).as("year"),
                        weekofyear(col("CreationDate")).as("weekNumber")).agg(count("*").as("totalVotesByYearWeek"))
                  val totalAvgVotes = votesByYearAndWeekDF.agg(avg("totalVotesByYearWeek").as("totalAvgVotes")).first().getAs[Double]("totalAvgVotes")
                  val deviationDF = votesByYearAndWeekDF.withColumn("deviation", abs(lit(1) - (col("totalVotesByYearWeek") / totalAvgVotes)))
                  val outlier = deviationDF.filter("deviation > 0.2").select(col("year"), col("weekNumber"), col("totalVotesByYearWeek").as("VoteCount")).orderBy("Year", "weekNumber")
                  outlier.createOrReplaceTempView("outlier_weeks")
                  spark.sql("select * from outlier_weeks").show()
            }
            catch
            {
                  case e: org.apache.spark.sql.AnalysisException =>
                        println("AnalysisException occurred: " + e.getMessage)
                  case e: java.io.FileNotFoundException =>
                        println("FileNotFoundException occurred: " + e.getMessage)
                  case e: java.io.IOException =>
                        println("IOException occurred: " + e.getMessage)
                  case e: Exception => println(s"An error occurred: ${e.getMessage}")
            }
      }
}