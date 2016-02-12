
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import scala.xml.XML

trait StockFetcher {

  def stockSymbol: String

  def stockPrice: Future[BigDecimal] = {
    val strResponse = Future { Source.fromURL(url).mkString }
    val xmlResponse = strResponse.map { s => XML.loadString(s) }
    val price = xmlResponse.map { r => BigDecimal((r \ "LastPrice").text) }
    price
  }

  private def url:String =
    "http://dev.markitondemand.com/MODApis/Api/v2/Quote?" +
    s"symbol=${stockSymbol}"

}
