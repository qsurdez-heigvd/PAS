package Utils

/** Produces monotonically increasing unique IDs and supports resetting the
  * sequence
  */
trait IdGenerator {

  /** Returns the next unique ID in the sequence */
  def nextId(): Long

  /** Resets the ID sequence back to its initial state */
  def reset(): Unit
}

/** Thread-sage [[IdGenerator]] implemented by a simple Long counter starting at
  * 0.
  */
class DefaultIdGenerator extends IdGenerator {

  private var counter = 0L

  override def nextId(): Long =
    synchronized {
      val id = counter
      counter += 1L
      id
    }

  override def reset(): Unit =
    counter = 0L
}
