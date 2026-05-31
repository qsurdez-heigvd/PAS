package labTestSuites

/** A trait that extend this one can be use as the definition of an exercice.
  */
trait Exercice {
}

/** The objects that extend this trait contain the implementations of a given
  * exercices
  */
trait Implementations[T <: Exercice]:
  /** At least one correct implementation of the exercice
    */
  val correct: Seq[T]

//
// BELOW ARE THE METHODS AND VALUE THAT NEED TO BE IMPLEMENTED FOR EACH EXERCICE
// - Nomenclature: <name of the serie><exercice number>
//
// ⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟

/** Exercice d'example (x puissance y)
  *   - Extrait de la slide 15 du chapitre 3
  */
trait X extends Exercice:
  def power(x: Double, n: Int): Double

trait B1 extends Exercice:
  def func(ls: List[Int]): Int

trait B2 extends Exercice:
  val vector: Vector[Int]

  def func(v: Int): Int

  val apply: Vector[BigInt]

trait B3 extends Exercice:
  val vector: Vector[Int]

  def fact(x: Int): BigInt

  def fib(x: Int): BigInt

  def fibRec(x: Int): BigInt

  val apply: Vector[BigInt]

trait B4 extends Exercice:
  def fastExp(base: Int, exp: Int): Int

trait C1 extends Exercice:
  def zip(v: Vector[String], l: List[Int]): List[(Int, String)]
  def toMap(t: List[(Int, String)]): Map[Int, String]

trait C2 extends Exercice:
  def init[T](xs: List[T]): List[T]

trait C3 extends Exercice:
  def penultimate[T](xs: List[T]): T

trait C4 extends Exercice:
  def isPalindrome[T](xs: List[T]): Boolean

trait C5 extends Exercice:
  def removeAt[T](n: Int, xs: List[T]): List[T]

trait C6 extends Exercice:
  def pack[T](xs: List[T]): List[List[T]]

trait C7 extends Exercice:
  def encode[T](xs: List[T]): List[(T, Int)]

trait C8 extends Exercice:
  def splitRecursive[A](n: Int, ls: List[A]): (List[A], List[A])

trait C9 extends Exercice:
  def decode[T](xs: List[(T, Int)]): List[T]

trait C10 extends Exercice:
  def takeWhileStrictlyIncreasing(list: List[Int]): List[Int]

trait D1 extends Exercice:
  def func(v: Vector[Int], l: List[Int]): Vector[List[Int]]

trait D2 extends Exercice:
  def max(l: List[Int]): Int

trait D3 extends Exercice:
  def func(n: Int): BigInt

trait D4 extends Exercice:
  def baseTwoPower(n: Int): BigInt

trait D5 extends Exercice:
  def catSpace(xs: Seq[String]): String

trait D6 extends Exercice:
  def reverse[T](l: List[T]): List[T]

trait D7 extends Exercice:
  def firstColumn(xs: List[List[Int]]): List[Int]

  def column(xs: List[List[Int]], col: Int): List[Int]

trait D8 extends Exercice:
  def diagonal(xs: List[List[Int]]): List[Int]

trait D9 extends Exercice:
  def hasZeroRow(matrix: List[List[Int]]): Boolean

trait D10 extends Exercice:
  def isPrime(x: Int): Boolean

trait D11 extends Exercice:
  def linesLonger(lines: List[String], len: Int): List[String]

trait D12 extends Exercice:
  def longestLineLength(lines: List[String]): Int

trait D13 extends Exercice:
  def elimEmptyLines(lines: List[String]): List[String]

trait D14 extends Exercice:
  def longestLine(lines: List[String]): String

trait D15 extends Exercice:
  def compress[A](ls: List[A]): List[A]

trait D16 extends Exercice:
  def averageOfDoubles(l: List[Double]): Double
