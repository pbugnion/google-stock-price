
import scala.concurrent.{ Future, Await }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{ Success, Failure }

import org.joda.time.Instant

object PlotGooglePrice extends App {

  val fetcher = new StockFetcher {
    val stockSymbol = "GOOG"
  }

  val writer = new PlotlyWriter {
    val username = "pbugnion"
    val filename = "test12"
    val key = sys.env.getOrElse("PLOTLY_KEY",
      throw new IllegalStateException("Expected PLOTLY_KEY environment variable")
    )
  }

  /** Fetch the stock price */
  val priceFuture = fetcher.stockPrice

  /** When the stock price arrives, forward it to plotly */
  val plotlyResponse = priceFuture.flatMap { stockPrice =>
    writer.write(Instant.now, stockPrice)
  }

  /** Callbacks to print information about the API calls. */
  priceFuture.onComplete {
    case Success(price) => println(s"Stock price: $price")
    case Failure(error) =>
      println(s"Error querying Markit on demand API: $error")
  }
  plotlyResponse.onComplete {
    case Success(response) =>
      println(s"Plotly status code: ${response.code}")
    case Failure(error) =>
      println(s"Error sending results to Plotly: $error")
  }

  Await.ready(plotlyResponse, 5 seconds)

}
