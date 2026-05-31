package labTestSuites.testSuite

import munit.ScalaCheckSuite
import org.scalacheck.Prop._
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt

class ConfiguredSuite extends ScalaCheckSuite {
  override def munitTimeout: Duration = 1.second
}
