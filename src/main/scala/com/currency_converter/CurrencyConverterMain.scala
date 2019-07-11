package com.currency_converter


object CurrencyConverterMain extends App {
  def close(lhs: Double, rhs: Double, precision: Double = 0.1): Boolean = {
    (lhs - rhs).abs < precision
  }

  val converter = new CurrencyConverter("currency_rates_usd.csv")
  println(converter.convert(Currency(1.0), "RUB").value)
  println(converter.convert(Currency(1.0), "USD").value)
  println(converter.convert(Currency(1.0, "CAD"), "CZK").value)
}
