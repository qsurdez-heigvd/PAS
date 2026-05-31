package labTestSuites.implementations.surdez

import scala.annotation.tailrec
import labTestSuites._

/** Référence all the implementations so that they can be used in the tests
  * SHOULD NOT BE MODIFIED !!!
  */
val all = Seq(
  X,
  B1,
  B2,
  B3,
  B4,
  C1,
  C2,
  C3,
  C4,
  C5,
  C6,
  C7,
  C8,
  C9,
  C10,
  D1,
  D2,
  D3,
  D4,
  D5,
  D6,
  D7,
  D8,
  D9,
  D10,
  D11,
  D12,
  D13,
  D14,
  D15,
  D16,
)

//
// BELOW ARE THE OBJECTS IN WHICH YOU WILL
// ADD THE IMPLEMENTATIONS OF EACH EXERCICE
//
// ⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟⮟

/** Example of the implementation of an exercice
  */
object X extends Implementations[X]:
  /** Assign each implementation to a variable. It will simplify changing their
    * order.
    */
  val recursive = new X:
    def square(x: Double): Double = x * x
    def power(x: Double, n: Int): Double =
      require(n >= 0)
      if n == 0 then 1.0
      else if n % 2 == 0 then square(power(x, n / 2))
      else x * power(x, n - 1)

  /** List here all your implementations of the current exercice.
    *
    * Only the first implementation in the sequence will be tested by your test
    * suite. If you want to test another implementation, move it to the top of
    * the sequence. The first implementation is the one that counts for the
    * correction.
    */
  val correct: Seq[X] = Seq(
    recursive,
  )

object B1 extends Implementations[B1]:
  val implB1 = new B1:
    override def func(ls: List[Int]): Int =
      ls match
        case x :: xs => x + func(xs)
        case _       => 0
  val correct: Seq[B1] = Seq(implB1)

object B2 extends Implementations[B2]:
  val implB2 = new B2:
    override val vector: Vector[Int] = (1 to 1000).toVector

    override def func(v: Int): Int =
      v match
        case _ if v % 3 == 0                => v * v * v
        case _ if v % 2 == 0                => v * v
        case _ if v % 2 == 1 || v % 2 == -1 => -1 // In Scala -3 % 2 = -1 !

    override val apply: Vector[BigInt] = vector.map(x => func(x))
  val correct: Seq[B2] = Seq(implB2)

object B3 extends Implementations[B3]:
  val implB3 = new B3:
    override val vector: Vector[Int] = (1 to 10).toVector

    override def fact(x: Int): BigInt = {
      require(x >= 0)
      x match
        case 0 => 1
        case n => n * fact(n - 1)
    }

    override def fib(x: Int): BigInt = {
      require(x >= 0)
      x match
        case 0 => 0
        case 1 => 1
        case n => fib(n - 1) + fib(n - 2)
    }

    override def fibRec(x: Int): BigInt = {
      require(x >= 0)

      @tailrec
      def loop(n: Int, prev: BigInt, curr: BigInt): BigInt = {
        n match
          case 0 => prev
          case _ => loop(n - 1, curr, prev + curr)
      }

      loop(x, 0, 1)
    }

    override val apply: Vector[BigInt] = vector.map {
      case n if n % 2 == 0 => fact(n)
      case n               => fibRec(n)
    }

  val correct: Seq[B3] = Seq(implB3)

object B4 extends Implementations[B4]:
  val implB4 = new B4:
    override def fastExp(base: Int, exp: Int): Int = {
      @tailrec
      def fastExp2(y: Int, base: Int, exp: Int): Int =
        exp match
          case exp if exp < 0 =>
            throw IllegalArgumentException(
              "exp should be >= 0",
            ) // as the func returns an Int we can't have 0.25 if (2, -2) so we throw
          case exp if exp == 0     => y
          case exp if exp % 2 == 0 => fastExp2(y, base * base, exp / 2)
          case exp if exp % 2 != 0 =>
            fastExp2(base * y, base * base, (exp - 1) / 2)

      fastExp2(1, base, exp)

    }

  val correct: Seq[B4] = Seq(implB4)

object C1 extends Implementations[C1]:
  val implC1 = new C1:

    override def zip(v: Vector[String], l: List[Int]): List[(Int, String)] =
      @tailrec
      def zip2(
          idx: Int,
          l: List[Int],
          acc: List[(Int, String)],
      ): List[(Int, String)] =
        l match {
          case lx :: ls if idx < v.length =>
            zip2(idx + 1, ls, (lx, v(idx)) :: acc)
          case _ => acc.reverse
        }

      zip2(0, l, List[(Int, String)]())

    override def toMap(t: List[(Int, String)]): Map[Int, String] =
      @tailrec
      def toMap2(
          rest: List[(Int, String)],
          acc: Map[Int, String],
      ): Map[Int, String] =
        rest match {
          case (key, value) :: rest => toMap2(rest, acc + (key -> value))
          case _                    => acc
        }

      toMap2(t, Map[Int, String]())

  val correct: Seq[C1] = Seq(implC1)

object C2 extends Implementations[C2]:
  val implC2 = new C2:
    override def init[T](xs: List[T]): List[T] = xs match
      case List()  => throw new Exception("init of empty list")
      case List(x) => List()
      case y :: ys => y :: init(ys)

  val correct: Seq[C2] = Seq(implC2)

object C3 extends Implementations[C3]:
  val implC3 = new C3:
    @tailrec
    override def penultimate[T](xs: List[T]): T = {
      xs match {
        case List(x, y) => x
        case y :: ys    => penultimate(ys)
        case _ =>
          throw new IllegalArgumentException("xs must have at least 2 elems")
      }
    }

  val correct: Seq[C3] = Seq(implC3)

object C4 extends Implementations[C4]:
  val implC4 = new C4 {
    override def isPalindrome[T](xs: List[T]): Boolean =
      return xs == xs.reverse
  }
  val correct: Seq[C4] = Seq(implC4)

object C5 extends Implementations[C5]:
  val implC5 = new C5 {
    override def removeAt[T](n: Int, xs: List[T]): List[T] = (n, xs) match
      case (_, List())  => xs
      case (0, _ :: ys) => ys
      case (n, y :: ys) => y :: removeAt(n - 1, ys)
  }
  val correct: Seq[C5] = Seq(implC5)

object C6 extends Implementations[C6]:
  val implC6 = new C6 {
    override def pack[T](xs: List[T]): List[List[T]] = xs match {
      case Nil => Nil
      case y :: ys =>
        val (same, rest) = ys.span(_ == y)
        (y :: same) :: pack(rest)
    }
  }
  val correct: Seq[C6] = Seq(implC6)

object C7 extends Implementations[C7]:
  val implC7 = new C7 {
    val implC6: C6 = C6.implC6
    override def encode[T](xs: List[T]): List[(T, Int)] =
      implC6.pack(xs).collect {
        case x :: rest => (x, rest.length + 1)
      }
  }
  // Old implementation with the case Nil that is not nice
  val oldImplC7 = new C7 {
    val implC6: C6 = C6.implC6
    override def encode[T](xs: List[T]): List[(T, Int)] =
      implC6
        .pack(xs)
        .map {
          case y :: ys => (y, ys.length + 1)
          case Nil =>
            throw new Exception(
              "This hsould never happen (check in the tests)",
            )
        }
  }
  val correct: Seq[C7] = Seq(implC7)

object C8 extends Implementations[C8]:
  val implC8 = new C8 {
    override def splitRecursive[A](n: Int, ls: List[A]): (List[A], List[A]) =
      ls match {
        case Nil            => (Nil, Nil)
        case list if n <= 0 => (Nil, list)
        case h :: tail =>
          val (left, right) = splitRecursive(n - 1, tail)
          (h :: left, right)
      }
  }
  val correct: Seq[C8] = Seq(implC8)

object C9 extends Implementations[C9]:
  val implC9 = new C9 {
    override def decode[T](xs: List[(T, Int)]): List[T] =
      xs.flatMap((x, n) => List.fill(n)(x))
  }
  val oldImplC9 = new C9 {
    // old implementation that was a bit weird with the postfix operator
    override def decode[T](xs: List[(T, Int)]): List[T] = xs match {
      case (x, n) :: xs =>
        List.fill(n)(x) ::: decode(
          xs,
        ) // postfix operator to not reverse it at the end
      case Nil => List()
    }
  }
  val correct: Seq[C9] = Seq(implC9)

object C10 extends Implementations[C10]:
  val implC10 = new C10 {
    override def takeWhileStrictlyIncreasing(list: List[Int]): List[Int] =
      list match {
        case x :: y :: xs if x < y => x :: takeWhileStrictlyIncreasing(y :: xs)
        case x :: _                => List(x)
        case List()                => List()
      }
  }
  val correct: Seq[C10] = Seq(implC10)

object D1 extends Implementations[D1]:
  val implD1 = new D1 {
    override def func(v: Vector[Int], l: List[Int]): Vector[List[Int]] =
      v.map(multiplier => l.map(element => multiplier * element))
  }
  val correct: Seq[D1] = Seq(implD1)

object D2 extends Implementations[D2]:
  val implD2 = new D2 {
    override def max(l: List[Int]): Int =
      l.reduceLeft((x, y) => if x > y then x else y)
  }
  val correct: Seq[D2] = Seq(implD2)

object D3 extends Implementations[D3]:
  val implD3 = new D3 {
    override def func(n: Int): BigInt = {
      require(n >= 0)
      n match
        case 0 => 1
        // Replace reduce with product (IDE warning)
        case _ => BigInt(1).to(BigInt(n)).reduceLeft(_ * _)
    }
  }
  val correct: Seq[D3] = Seq(implD3)

object D4 extends Implementations[D4]:
  val implD4 = new D4 {
    override def baseTwoPower(n: Int): BigInt = {
      require(n >= 0)
      n match
        case 0 => 1
        // Replace reduce with product (IDE warning)
        // reduceLeft is the function that calculates 2^n
        case _ => List.fill(n)(BigInt(2)).reduceLeft(_ * _)
    }
  }
  val correct: Seq[D4] = Seq(implD4)

object D5 extends Implementations[D5]:
  val implD5 = new D5 {
    override def catSpace(xs: Seq[String]): String =
      xs match {
        case Nil    => ""
        case Seq(x) => x
        case _      => xs.reduceLeft(_ ++ " " ++ _)
      }
  }
  val correct: Seq[D5] = Seq(implD5)

object D6 extends Implementations[D6]:
  val implD6 = new D6 {
    override def reverse[T](l: List[T]): List[T] =
      l.foldLeft(List[T]())((x, y) => y :: x)
  }
  val correct: Seq[D6] = Seq(implD6)

object D7 extends Implementations[D7]:
  val implD7 = new D7 {
    override def firstColumn(xs: List[List[Int]]): List[Int] =
      xs.map { row =>
        require(row.nonEmpty)
        row.head
      }

    override def column(xs: List[List[Int]], col: Int): List[Int] =
      xs.map { row =>
        require(row.isDefinedAt(col))
        row(col)
      }
  }
  val correct: Seq[D7] = Seq(implD7)

object D8 extends Implementations[D8]:
  val implD8 = new D8 {
    override def diagonal(xs: List[List[Int]]): List[Int] =
      xs.foldLeft((List[Int](), 0))({ case ((diag, i), row) =>
        if i < row.length then (diag :+ row(i), i + 1) else (diag, i + 1)
      })._1
  }
  val correct: Seq[D8] = Seq(implD8)

object D9 extends Implementations[D9]:
  val implD9 = new D9 {
    override def hasZeroRow(matrix: List[List[Int]]): Boolean =
      matrix.exists(row => row.nonEmpty && row.forall(value => value == 0))
  }
  val correct: Seq[D9] = Seq(implD9)

object D10 extends Implementations[D10]:
  val implD10 = new D10 {
    override def isPrime(x: Int): Boolean =
      if (x > 1 && (2 until x - 1).forall(n => x % n != 0)) then true else false
  }
  val correct: Seq[D10] = Seq(implD10)

object D11 extends Implementations[D11]:
  val implD11 = new D11 {
    override def linesLonger(lines: List[String], len: Int): List[String] =
      lines.filter(string => string.length > len)
  }
  val correct: Seq[D11] = Seq(implD11)

object D12 extends Implementations[D12]:
  val implD12 = new D12 {
    override def longestLineLength(lines: List[String]): Int =
      lines.foldLeft(0)({ (acc, string) =>
        if string.length > acc then string.length else acc
      })
  }
  val correct: Seq[D12] = Seq(implD12)

object D13 extends Implementations[D13]:
  val implD13 = new D13 {
    override def elimEmptyLines(lines: List[String]): List[String] =
      lines.filter(string => string.nonEmpty)
  }
  val correct: Seq[D13] = Seq(implD13)

object D14 extends Implementations[D14]:
  val implD14 = new D14 {
    override def longestLine(lines: List[String]): String =
      lines.foldLeft(String())({ (acc, string) =>
        if string.length > acc.length then string else acc
      })
  }
  val correct: Seq[D14] = Seq(implD14)

object D15 extends Implementations[D15]:
  val implD15 = new D15 {
    override def compress[A](ls: List[A]): List[A] =
      ls.foldRight(List[A]())((value, acc) =>
        if (acc.isEmpty || acc.head != value) then value :: acc else acc,
      )
  }
  val correct: Seq[D15] = Seq(implD15)

object D16 extends Implementations[D16]:
  val implD16= new D16 {
    override def averageOfDoubles(l: List[Double]): Double = {
      require(l.nonEmpty)
      l.foldLeft(0.0)((acc, value) => acc + value)/l.length
    }
  }
  val correct: Seq[D16] = Seq(implD16)
