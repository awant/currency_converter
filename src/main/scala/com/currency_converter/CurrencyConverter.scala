package com.currency_converter


case class Currency(value: Double, label: String = "USD")

class CurrencyConverter(filePath: String) {
  val ratesMap: Map[String, Double] = loadRates(filePath)

  def loadFromFile(filePath: String): Map[String, Double] = {
    var map = Map.empty[String, Double]
    val separator = ","
    val bufferedSource = io.Source.fromFile(filePath)
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(separator).map(_.trim)
      map += (cols(0) -> cols(1).toDouble)
    }
    bufferedSource.close
    map
  }

  def loadRates(filePath: String): Map[String, Double] = {
    CurrencyRequester.getRates()
    //    try {
    //      CurrencyAPI.getRates()
    //    } catch {
    //      case _: Exception => loadFromFile(filePath)
    //    }
  }

  def convert(currency: Currency, label: String): Currency = {
    val labelFrom = currency.label
    if (!ratesMap.contains(label)) {
      throw new Exception("currencyTo is unknown")
    }
    if (!ratesMap.contains(labelFrom)) {
      throw new Exception("currencyFrom is unknown")
    }

    val rateFrom = ratesMap(currency.label)
    val rateTo = ratesMap(label)
    val value = currency.value / rateFrom * rateTo
    Currency(value, label)
  }
}

//object CurrencyConverterMain {
//  def close(lhs: Double, rhs: Double, precision: Double = 0.1): Boolean = {
//    (lhs - rhs).abs < precision
//  }
//
//  def main(args: Array[String]): Unit = {
//    val converter = new CurrencyConverter("currency_rates_usd.csv")
//
//    assert(close(converter.convert(Currency(1.0), "RUB").value, 63.33))
//    assert(close(converter.convert(Currency(1.0), "USD").value, 1.0))
//    assert(close(converter.convert(Currency(1.0, "CAD"), "CZK").value, 17.22))
//  }
//}
