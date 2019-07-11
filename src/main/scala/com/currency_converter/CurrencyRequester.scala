package com.currency_converter

import scala.io.Source.fromURL
import scala.collection.immutable.HashMap
import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.collection.mutable.ArrayBuffer

object CurrencyRequester {
  private val uri: String = "https://free.currconv.com/api/v7/convert?"
  private val apiKey: String = readKey("settings.ini")
  private val currencies: Seq[String] = Seq(
    "RUB", "EUR", "CZK", "GBP", "CAD", "KZT"
  )
  private val url: String = formGetCurrenciesUrl()

  def readKey(filePath: String): String = {
    val bufferedSource = io.Source.fromFile(filePath)
    val key = bufferedSource.getLines.find(_ => true)
    bufferedSource.close()
    key.get
  }

  def formGetCurrenciesUrl(): String = {
    val qs = currencies.map(elem => "USD_" ++ elem).mkString(",")
    s"${uri}apiKey=$apiKey&q=$qs"
  }

  def parseRawMapping(json: HashMap[String, Any]): Map[String, Double] = {
    val result = ArrayBuffer.empty[(String, Double)]
    for ((_, currency_fields) <- json) {
      val fields = currency_fields.asInstanceOf[Map[String, Any]]
      result += Tuple2[String, Double](fields("to").asInstanceOf[String], fields("val").asInstanceOf[Double])
    }
    result += Tuple2[String, Double]("USD", 1.0)
    result.toMap
  }

  def getRates(): Map[String, Double] = {
    val jsonStr: String = fromURL(url).mkString
    implicit val formats = org.json4s.DefaultFormats
    val json = parse(jsonStr).extract[Map[String, Any]]
    parseRawMapping(json("results").asInstanceOf[HashMap[String, Any]])
  }
}
