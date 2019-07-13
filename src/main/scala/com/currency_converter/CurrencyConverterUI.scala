package com.currency_converter

import javafx.concurrent.Task
import javafx.beans.value.{ChangeListener, ObservableValue}
import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.{Button, ComboBox, Label, ProgressIndicator, TableColumn, TableView, TextField}
import javafx.event.ActionEvent
import TableColumn._


object CurrencyConverterUI extends JFXApp {
  private val converter = new CurrencyConverter

  def setResultedCurrency(newValue: Any, tgtTF: TextField, srcLbl: String, tgtLbl: String): Unit = {
    try {
      val value = newValue.toString.toDouble
      tgtTF.text = converter.convert(Currency(value, srcLbl), tgtLbl).value.toString
    } catch {
      case _: NumberFormatException => tgtTF.text = ""
    }
  }

  stage = new JFXApp.PrimaryStage {
    title = "Currency converter"
    scene = new Scene(400, 400) {
      // Grid for elements
      private val yShifts = (20, 50, 90, 125)
      private val xShifts = (20, 80, 160)

      private val curRateLabels = converter.getCurrencyLabels
      private var fromRateLabel = converter.defaultCurrencyLabel
      private var toRateLabel = converter.defaultCurrencyLabel

      // First row ('From' currency)
      val fromLabel = new Label("From:")
      fromLabel.layoutX = xShifts._1
      fromLabel.layoutY = yShifts._1

      val fromCurRateBox = new ComboBox(curRateLabels)
      fromCurRateBox.setValue(fromRateLabel)
      fromCurRateBox.layoutX = xShifts._2
      fromCurRateBox.layoutY = yShifts._1

      val fromCurrencyTF = new TextField
      fromCurrencyTF.layoutX = xShifts._3
      fromCurrencyTF.layoutY = yShifts._1

      // Second row ('To' currency)
      val toLabel = new Label("To:")
      toLabel.layoutX = xShifts._1
      toLabel.layoutY = yShifts._2

      val toCurRateBox = new ComboBox(curRateLabels)
      toCurRateBox.setValue(toRateLabel)
      toCurRateBox.layoutX = xShifts._2
      toCurRateBox.layoutY = yShifts._2

      val toCurrencyTF = new TextField
      toCurrencyTF.layoutX = xShifts._3
      toCurrencyTF.layoutY = yShifts._2

      // Rating updates
      val updRatesBtn = new Button("Update rates")
      updRatesBtn.layoutX = xShifts._1
      updRatesBtn.layoutY = yShifts._3

      val progressIndicator = new ProgressIndicator
      progressIndicator.visible = false
      progressIndicator.setMaxHeight(25)
      progressIndicator.layoutX = xShifts._2 + 40
      progressIndicator.layoutY = yShifts._3

      // converter.getCurrencyLabels
      val ratesTable: TableView[Rate] = new TableView[Rate](ObservableBuffer[Rate](converter.getRates)) {
        columns ++= List(
          new TableColumn[Rate, String] {
            text = "Currency label"
            cellValueFactory = {lbl => StringProperty(lbl.value.label)}
            prefWidth = 180
          },
          new TableColumn[Rate, Double] {
            text = "Rate"
            cellValueFactory = {rate => ObjectProperty(rate.value.rate)}
            prefWidth = 180
          }
        )
      }
      ratesTable.setPrefHeight(curRateLabels.size * 35)
      ratesTable.layoutX = xShifts._1
      ratesTable.layoutY = yShifts._4

      content = List(fromLabel, fromCurRateBox, fromCurrencyTF,
        toLabel, toCurRateBox, toCurrencyTF,
        updRatesBtn, progressIndicator,
        ratesTable)

      // Actions
      fromCurRateBox.onAction = (_: ActionEvent) => {
        fromRateLabel = fromCurRateBox.selectionModel.apply.getSelectedItem
        setResultedCurrency(fromCurrencyTF.getText, toCurrencyTF, fromRateLabel, toRateLabel)
      }

      toCurRateBox.onAction = (_: ActionEvent) => {
        toRateLabel = toCurRateBox.selectionModel.apply.getSelectedItem
        setResultedCurrency(fromCurrencyTF.getText, toCurrencyTF, fromRateLabel, toRateLabel)
      }

      fromCurrencyTF.textProperty.addListener(new ChangeListener[Any] {
        def changed(observable: ObservableValue[_], oldValue: Any, newValue: Any) {
          setResultedCurrency(newValue, toCurrencyTF, fromRateLabel, toRateLabel)
        }
      })

      // Updating rates in a background thread
      updRatesBtn.onAction = (_: ActionEvent) => {
        progressIndicator.visible = true

        val task = new Task[Boolean] {
          override def call(): Boolean = {
            Thread.sleep(2000)
            true
          }
          override def succeeded(): Unit = {
            progressIndicator.visible = false
          }
          override def failed(): Unit = {
            progressIndicator.visible = false
          }
        }

        val t = new Thread(task, "Currency rates updating task")
        t.setDaemon(true)
        t.start()
      }

    }
  }
}
