package org.apache.streampark.flink.quickstart.connector

import org.apache.streampark.flink.connector.elasticsearch7.sink.ES7Sink
import org.apache.streampark.flink.connector.elasticsearch7.util.ElasticsearchUtils
import org.apache.streampark.flink.core.scala.FlinkStreaming
import org.apache.streampark.flink.quickstart.connector.bean.Entity
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.scala.DataStream
import org.elasticsearch.action.index.IndexRequest
import org.json4s.jackson.JsonMethods

object ES7SinkApp extends FlinkStreaming {

  implicit val entityType: TypeInformation[Entity] = TypeInformation.of(classOf[Entity])

  override def handle(): Unit = {
    val source: DataStream[Entity] = context.addSource(new MyDataSource)

    implicit def indexedSeq(x: Entity): IndexRequest = ElasticsearchUtils.indexRequest(
        "flink_order",
        s"${x.orderId}_${x.timestamp}",
        JsonMethods.mapper.writeValueAsString(x)
    )

    ES7Sink().sink[Entity](source)
  }
}
