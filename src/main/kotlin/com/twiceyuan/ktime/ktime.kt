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

class TimeKit : CliktCommand(name = "ktime") {

    private val format by option(help = "时间的格式，默认 yyyy-MM-dd").default("yyyy-MM-dd")

    override fun run() {
        context.obj = SimpleDateFormat(format)
    }
}

class ConvertKit : CliktCommand(name = "convert") {

    private val time by argument(help = "需要转换的时间戳或时间字符串").default("")
    private val dateFormatter by requireObject<SimpleDateFormat>()

    override fun run() {
        if (time.isEmpty()) {
            return
        }

        val isTimestamp = !time.any { it !in '0'..'9' }
        if (isTimestamp) {
            TermUi.echo(dateFormatter.format(Date(time.toLong())))
        } else {
            TermUi.echo(dateFormatter.parse(time).time)
        }
    }
}

class DurationKit : CliktCommand(name = "duration") {

    private val times by argument(help = "默认格式 yyyy-MM-dd").multiple()
    private val outputUnit by option().default("day")
    private val dateFormatter by requireObject<SimpleDateFormat>()

    override fun run() {
        if (times.size != 2) {
            TermUi.echo("计算时间间隔必须输入两个时间点", err = true)
            return
        }

        val start = dateFormatter.parse(times[0]).time
        val end = dateFormatter.parse(times[1]).time

        val unitMillis = when (outputUnit) {
            "day" -> 1000 * 3600 * 24
            "min" -> 1000 * 60
            "sec" -> 1000
            else -> null
        }

        if (unitMillis == null) {
            TermUi.echo("请输入正确的单位(day, min, sec)")
        } else {
            TermUi.echo("${Math.abs(end - start) / unitMillis} $outputUnit")
        }
    }
}

/**
 * > ktime --duration 2018-01-20 2018-01-21 --unit day
 * < 2018-01-20 到 2018-01-21 相隔 1 天
 */
fun main(args: Array<String>) = TimeKit()
        .subcommands(ConvertKit(), DurationKit())
        .main(args)