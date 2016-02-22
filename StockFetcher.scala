
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.xml.XML

import scalaj.http.{Http, HttpRequest}

trait StockFetcher {

  def stockSymbol: String

  def stockPrice: Future[BigDecimal] = {
    val strResponse = Future { request.asString }
    val xmlResponse = strResponse.map { s => XML.loadString(s.body) }
    val priceAsString = xmlResponse.map { r =>  (r \ "LastPrice").text }
    val price = priceAsString.map { price => BigDecimal(price) }
    price
  }

  private def request: HttpRequest =
    Http("http://dev.markitondemand.com/MODApis/Api/v2/Quote")
      .param("symbol", stockSymbol)

}
