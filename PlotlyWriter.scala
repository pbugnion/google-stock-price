
import scalaj.http._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.Instant
import org.joda.time.format.DateTimeFormat

trait PlotlyWriter {

  def username: String
  def key: String
  def filename: String

  private def request(time: Instant, value: BigDecimal): HttpRequest = {
    val plotlyUrl = "https://plot.ly/clientresp"
    val fmt = DateTimeFormat.forPattern("yyyy-MM-dd kk:mm")
    val timeStr = time.toString(fmt)
    val request = Http(plotlyUrl).postForm(Seq(
      "un" -> username,
      "key" -> key,
      "origin" -> "plot",
      "platform" -> "scala",
      "args" -> s"""[ ["$timeStr"], [$value] ]""",
      "kwargs" -> s""" { "filename": "$filename", "fileopt": "extend", "traces": [0] }"""
    ))
    request
  }

  def write(time: Instant, value: BigDecimal): Future[HttpResponse[String]] = Future {
    request(time, value).asString
  }

}
