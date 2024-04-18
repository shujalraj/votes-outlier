package com.exercise
import scala.io.Source
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}


object ExerciseApp extends App {
     @transient val spark = SparkSession
        .builder()
        .appName("calculate_outlier")
        .master("local[*]") // Change to your desired master URL for cluster deployment
        .getOrCreate()

     @transient val  jdbcUrl = "jdbc:sqlite:./warehouse-test.db"
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
            //Reading data from files incrementally


            try {
                  val schema = StructType(List(StructField("Id", StringType, nullable = true)
                        , StructField("PostId", StringType, nullable = true)
                        , StructField("VoteTypeId", StringType, nullable = true)
                        , StructField("CreationDate", TimestampType, nullable = true)))
                  val votesDFTarget = spark.read.jdbc(jdbcUrl, "votes", new java.util.Properties())
                  val votesDFSource = spark.read.schema(schema).json(args(1))

                  // Identifying new data using a left join between source and target table.

                  val joinedDF = votesDFSource.join(votesDFTarget,votesDFSource.col("Id") === votesDFTarget.col("Id"),"left")
                                              .select(votesDFSource.col("Id")
                                                     ,votesDFSource.col("PostId")
                                                     ,votesDFSource.col("VoteTypeId")
                                                     ,votesDFSource.col("CreationDate")
                                                     ,votesDFTarget.col("Id").as("tgtId"))
                  val incrementalDF = joinedDF.filter("tgtId is null").drop("tgtId")

                  // Write DataFrame to SQLite table
                  val tableName = "votes"
                  val mode = SaveMode.Append // Choose the appropriate save mode
                  incrementalDF.write
                    .mode(mode)
                    .jdbc(jdbcUrl, tableName, new java.util.Properties())

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
                  // Read data from the SQLite table into a DataFrame
                  print("Reading data from SQL-Lite database")
                  val votesDF = spark.read.jdbc(jdbcUrl, "votes", new java.util.Properties()).dropDuplicates()
                  val votesByYearAndWeekDF = votesDF.groupBy(year(col("CreationDate")).as("year"),
                        weekofyear(col("CreationDate")).as("weekNumber")).agg(count("*").as("totalVotesByYearWeek"))
                  val totalAvgVotes = votesByYearAndWeekDF.agg(avg("totalVotesByYearWeek").as("totalAvgVotes")).first().getAs[Double]("totalAvgVotes")
                  val deviationDF = votesByYearAndWeekDF.withColumn("deviation", abs(lit(1) - (col("totalVotesByYearWeek") / totalAvgVotes)))
                  val outlier = deviationDF.filter("deviation > 0.2").select(col("year"), col("weekNumber"), col("totalVotesByYearWeek").as("VoteCount")).orderBy("Year", "weekNumber")

                  //Writing the final table to SQLLite database
                  outlier.write
                    .mode("overwrite")
                    .jdbc(jdbcUrl, "outlier_weeks", new java.util.Properties())
                  val outlierWeeksDF = spark.read.jdbc(jdbcUrl, "outlier_weeks", new java.util.Properties())
                  outlierWeeksDF.show()
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