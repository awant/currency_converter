package com.currency_converter

import scala.io.Source.fromURL

object CurrencyRequester {
  private val uri: String = "https://free.currconv.com/api/v7/convert?"
  private val apiKey: String = readKey("settings.ini")
  private val currencies: Seq[String] = Seq(
    "RUB", "EUR", "CZK", "GBP", "CAD",
    "KZT"
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

  def getRates(): Map[String, Double] = {
    val url = formGetCurrenciesUrl()
    val json = fromURL(url)
    println("json", json)
    println(json("results"))

    Map("USD" -> 1, "RUB" -> 2.0, "CAD" -> 3.0, "CZK" -> 4.0)
  }
}
