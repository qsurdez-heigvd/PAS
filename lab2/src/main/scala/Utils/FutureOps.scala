package Utils

import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.{Failure, Random, Success}

object FutureOps {
  import scala.concurrent.ExecutionContext.Implicits.global

  /** Create a future that will complete after `mean` seconds (with some
    * variation based on `std`). The future has `successPercentage` chance of
    * succeeding.
    * @param mean
    *   the mean of the gaussian curve
    * @param std
    *   the std of the gaussian curve
    * @param successRate
    *   the chance that the future succeed (0.0 the future always fails, 1.0 the
    *   future always succeed)
    * @param rng
    *   the random number generator to use (injectable for testing)
    * @return
    *   the future
    */
  def randomSchedule(
      mean: Duration,
      std: Duration = 0.second,
      successRate: Double = 1.0,
  )(using rng: Random = Random): Future[Unit] =
    val succeed = rng.nextDouble() < successRate
    val sleepMillis = math.max(0, (std * rng.nextGaussian() + mean).toMillis) // avoids negative if nextGaussian << 0
    Future {
      Thread.sleep(sleepMillis)
      if !succeed then throw new Exception("Failed future")
      else ()
    }
}
