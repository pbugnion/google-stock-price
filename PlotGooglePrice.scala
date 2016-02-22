
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import org.joda.time.Instant

object PlotGooglePrice extends App {


  val fetcher = new StockFetcher {
    val stockSymbol = "GOOG"
  }

  val writer = new PlotlyWriter {
    val username = "pbugnion"
    val filename = "google-stock-price"
    val key = sys.env.getOrElse("PLOTLY_KEY",
      throw new IllegalStateException("Expected PLOTLY_KEY environment variable")
    )
  }

  val runnable = new Runnable {
    override def run() {
      // Fetch the stock price
      val price = fetcher.stockPrice

      // When the stock price arrives, forward it to plotly
      val plotlyResponse = price.flatMap { stockPrice =>
        writer.write(Instant.now, stockPrice)
      }

      // Callbacks to print information about the API calls.
      price.onComplete {
        case Success(price) => println(s"Stock price: $price")
        case Failure(error) =>
          println(s"Error querying Markit on demand API: $error")
      }

      plotlyResponse.onComplete {
        case Success(response) =>
          println(s"Plotly status code: ${response.code}")
          println(response.body)
        case Failure(error) =>
          println(s"Error sending results to Plotly: $error")
      }

    }
  }

  val scheduler = Executors.newSingleThreadScheduledExecutor

  scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.HOURS)


}
