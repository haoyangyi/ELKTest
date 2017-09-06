                                                   package com.hyy.elk
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaManager

/*,
  * Created by root on 2017/6/11.
  */
object AppDirectStream {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local").setAppName("AppDirectStream")
    val ssc = new StreamingContext(conf, Seconds(5))
    val topics = Set("logstash")
    val brokers = "spark1:9092,spark2:9092,spark3:9092"
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers,
      "serializer.class" -> "kafka.serializer.StringEncoder",
      "group.id"->"appTest"
    )
    val kafkaManager = new KafkaManager(kafkaParams)
    val messages = kafkaManager.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
    messages.foreachRDD(rdd => {
      kafkaManager.updateZKOffsets(rdd)
    })

//    messages.foreachRDD(rdd=>{
//      rdd.foreach(println(_))
//    })

    val words = messages.flatMap(_._2.split(" "))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _).transform(rdd=>{
      rdd.sortBy(_._2,false)
    })

    wordCounts.print()
    //启动计算作业
    ssc.start()
    //等待结束，什么时候结束作业，即触发什么条件会让作业执行结束
    ssc.awaitTermination()

  }
}
