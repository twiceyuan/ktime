package com.twiceyuan.ktime

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class TimeKit : CliktCommand(name = "ktime") {

    private val format by option(help = "date format, default yyyyMMdd").default("yyyyMMdd")

    override fun run() {
        currentContext.obj = SimpleDateFormat(format)
    }
}

class ConvertKit : CliktCommand(name = "convert") {

    private val date by argument(help = "date string(eg. 19930302) or timestamp(ms level)").default("")
    private val dateFormatter by requireObject<SimpleDateFormat>()

    override fun run() {
        if (date.isEmpty()) {
            return
        }

        // smart detect date is string or timestamp
        val isTimestamp = !date.any { it !in '0'..'9' }
        if (isTimestamp) {
            TermUi.echo(dateFormatter.format(Date(date.toLong())))
        } else {
            TermUi.echo(dateFormatter.parse(date).time)
        }
    }
}

class DurationKit : CliktCommand(name = "duration") {

    private val times by argument(help = "Dates to calculate duration, default format: yyyyMMdd").multiple()
    private val outputUnit by option().default("day")
    private val dateFormatter by requireObject<SimpleDateFormat>()

    override fun run() {
        if (times.size != 2) {
            TermUi.echo("Duration calculate needs two arguments", err = true)
            return
        }

        val start: Long
        val end: Long

        try {
            start = dateFormatter.parse(times[0]).time
            end = dateFormatter.parse(times[1]).time
        } catch (e: java.text.ParseException) {
            TermUi.echo("Please input correct date format: ${dateFormatter.toPattern()}")
            return
        }

        val unitMillis = when (outputUnit) {
            "day" -> 1000 * 3600 * 24
            "min" -> 1000 * 60
            "sec" -> 1000
            else -> null
        }

        if (unitMillis == null) {
            TermUi.echo("Please input correct date unit (eg. day, min, sec)")
        } else {
            TermUi.echo("${abs(end - start) / unitMillis} $outputUnit")
        }
    }
}

fun main(args: Array<String>) = TimeKit()
    .subcommands(ConvertKit(), DurationKit())
    .main(args)