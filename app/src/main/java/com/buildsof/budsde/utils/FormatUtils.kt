package com.buildsof.budsde.utils

import com.buildsof.budsde.data.Currency
import com.buildsof.budsde.data.MeasurementUnit

object FormatUtils {
    
    fun formatDistance(meters: Double, unit: MeasurementUnit): String {
        return when (unit) {
            MeasurementUnit.METERS -> "${String.format("%.2f", meters)} m"
            MeasurementUnit.CENTIMETERS -> "${String.format("%.0f", meters * 100)} cm"
        }
    }
    
    fun formatArea(squareMeters: Double, unit: MeasurementUnit): String {
        return when (unit) {
            MeasurementUnit.METERS -> "${String.format("%.2f", squareMeters)} m²"
            MeasurementUnit.CENTIMETERS -> "${String.format("%.0f", squareMeters * 10000)} cm²"
        }
    }
    
    fun formatPrice(amount: Double, currency: Currency): String {
        return "${currency.symbol}${String.format("%.2f", amount)}"
    }
    
    fun getDistanceLabel(unit: MeasurementUnit): String {
        return when (unit) {
            MeasurementUnit.METERS -> "meters"
            MeasurementUnit.CENTIMETERS -> "centimeters"
        }
    }
}
