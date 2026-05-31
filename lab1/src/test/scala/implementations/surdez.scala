package labTestSuites.testSuite.implementations.surdez

import labTestSuites.testSuite.ConfiguredSuite
import org.scalacheck.Prop.*
import labTestSuites.*

import scala.Double.NaN

/** Référence all the implementations so that they can be used in the automated
  * tests. SHOULD NOT BE MODIFIED !!!
  */
val all = Seq(
  classOf[XSuite],
  classOf[B1Suite],
  classOf[B2Suite],
  classOf[B3Suite],
  classOf[B4Suite],
  classOf[C1Suite],
  classOf[C2Suite],
  classOf[C3Suite],
  classOf[C4Suite],
  classOf[C5Suite],
  classOf[C6Suite],
  classOf[C7Suite],
  classOf[C8Suite],
  classOf[C9Suite],
  classOf[C10Suite],
  classOf[D1Suite],
  classOf[D2Suite],
  classOf[D3Suite],
  classOf[D4Suite],
  classOf[D5Suite],
  classOf[D6Suite],
  classOf[D7Suite],
  classOf[D8Suite],
  classOf[D9Suite],
  classOf[D10Suite],
  classOf[D11Suite],
  classOf[D12Suite],
  classOf[D13Suite],
  classOf[D14Suite],
  classOf[D15Suite],
  classOf[D16Suite],
)

class XSuite extends ConfiguredSuite {

  /** From all the implementation retrieve only the first one about the current
    * exercice.
    *
    * Using collect allows us to do a filter while specializing the type of the
    * collection.
    */
  val impl: X = getImplementations().collect { case x: X => x }.head

  /** A simple test that will succeed.
    */
  test("2 to the power of 2 should be 4") {
    assertEquals(impl.power(2, 2), 4.0)
  }

  /** A simple test that will fail (because the implementation is not general
    * enough).
    *
    * In your submission, ideally none of your tests should fail for your own
    * implementation.
    */
  test("2 to the power of -2 should be 0.25".fail) {
    assertEquals(impl.power(2, -2), 0.25)
  }

  /** A test (using scalacheck) that will check that a random selection of
    * double value to the power of 0 is equal to 1.
    *
    * The random selection of value is decided at runtime.
    */
  property("any value to the power of 0 should be 1") {
    forAll { (x: Double) =>
      assertEquals(impl.power(x, 0), 1.0)
    }
  }
}

class B1Suite extends ConfiguredSuite {
  val impl: B1 = getImplementations().collect { case x: B1 => x }.head

  test("Sum of empty list should be 0") {
    assertEquals(impl.func(List()), 0)
  }

  test("Sum of singleton should be itself") {
    assertEquals(impl.func(List(1)), 1)
  }

  test("Sum of list [1, 2, 3] should be 6") {
    assertEquals(impl.func(List(1, 2, 3)), 6)
  }

  test("Sum of list [-1, 1] should be 0") {
    assertEquals(impl.func(List(-1, 1)), 0)
  }

}

class B2Suite extends ConfiguredSuite {
  val impl: B2 = getImplementations().collect { case x: B2 => x }.head

  test("Vector should be initialised 1 to 1000") {
    // We're not creating a vector with the constructor Vector( Int(1), ... )
    // because I think this is explicit enough
    // otherwise we would assert with the same expression we created it
    // impl.vector == (1 to 1000).toVector which seems not good to my eyes
    // same comment for the last test
    val v = impl.vector
    assert(v.size == 1000)
    assert(v.head == 1)
    assert(v.last == 1000)
  }

  test("Func should return cube if multiple 3") {
    assert(impl.func(3) == 27)
  }

  test("Func should prioritise multiple of 3 over multiple of 2") {
    assert(impl.func(6) == 216)
  }

  test("Func should return square if multiple 2") {
    assert(impl.func(2) == 4)
  }

  test("Func should return -1 if is odd") {
    assert(impl.func(1) == -1)
  }

  test("Func should return negative cube if multiple 3") {
    assert(impl.func(-3) == -27)
  }

  test("Func should return square if negative multiple of 2") {
    assert(impl.func(-2) == 4)
  }

  test("Func should return -1 if negative odd") {
    assert(impl.func(-13) == -1)
  }

  test("Apply should correctly apply the func to the vector") {
    val v = impl.apply
    assert(v.size == 1000)
    assert(v.head == -1)
    assert(v(2) == 27)
    assert(v.last == 1000 * 1000)
  }
}

class B3Suite extends ConfiguredSuite {
  val impl: B3 = getImplementations().collect { case x: B3 => x }.head

  test("Vector should be size 10 and start with 1 and end with 10") {
    val v = impl.vector
    assert(v.size == 10)
    assert(v.head == 1)
    assert(v.last == 10)
  }

  test("Fact of negative number should throw") {
    intercept(impl.fact(-1))
  }

  test("Fact of 0 is 1") {
    assert(impl.fact(0) == 1)
  }

  test("Fact of 1 is 1") {
    assert(impl.fact(1) == 1)
  }

  test("Fact of 3 is 6") {
    assert(impl.fact(3) == 6)
  }

  test("Fib of negative number should throw") {
    intercept(impl.fib(-1))
  }

  test("Fib of 0 is 0") {
    assert(impl.fib(0) == 0)
  }

  test("Fib of 1 is 1") {
    assert(impl.fib(1) == 1)
  }

  test("Fib of 2 is 1") {
    assert(impl.fib(2) == 1)
  }

  test("FibRec of negative number should throw") {
    intercept(impl.fibRec(-1))
  }

  test("FibRec of 0 is 0") {
    assert(impl.fibRec(0) == 0)
  }

  test("FibRec of 1 is 1") {
    assert(impl.fibRec(1) == 1)
  }

  test("FibRec of 2 is 1") {
    assert(impl.fibRec(2) == 1)
  }

  test("Apply computes the correct values for the vector") {
    assert(
      impl.apply == Vector(
        BigInt(1),
        BigInt(2),
        BigInt(2),
        BigInt(24),
        BigInt(5),
        BigInt(720),
        BigInt(13),
        BigInt(40320),
        BigInt(34),
        BigInt(3628800),
      ),
    )

  }
}

class B4Suite extends ConfiguredSuite {
  val impl: B4 = getImplementations().collect { case x: B4 => x }.head

  test("FastExp with exp 0 whould be 1") {
    assert(impl.fastExp(2, 0) == 1)
  }

  test("FastExp with base 0 should be 0") {
    assert(impl.fastExp(0, 12) == 0)
  }

  test("FastExp with even exp") {
    assert(impl.fastExp(2, 4) == 16)
  }

  test("FastExp with odd exp") {
    assert(impl.fastExp(2, 3) == 8)
  }

  test("FastExp with negative base and even exp") {
    assert(impl.fastExp(-3, 2) == 9)
  }

  test("FastExp with negative base and odd exp") {
    assert(impl.fastExp(-3, 3) == -27)
  }

  test("FastExp with negative exp") {
    intercept(impl.fastExp(2, -2))
  }
}

class C1Suite extends ConfiguredSuite {
  val impl: C1 = getImplementations().collect { case x: C1 => x }.head

  test("Zip should combine vectors and lists correctly") {
    val vector = Vector("a", "b", "c")
    val list = List(1, 2, 3)
    val expected = List((1, "a"), (2, "b"), (3, "c"))
    assert(impl.zip(vector, list) == expected)
  }

  test("Zip should handle gracefully empty vectors") {
    val vector = Vector.empty[String]
    val list = List(1, 2, 3)
    assert(impl.zip(vector, list) == List.empty[(Int, String)])
  }

  test("Zip should handle gracefully empty lists") {
    val vector = Vector("a", "b", "c")
    val list = List.empty[Int]
    assert(impl.zip(vector, list) == List.empty[(Int, String)])
  }

  test("Zip should handle gracefully vectors with bigger size than list") {
    val vector = Vector("a", "b", "c", "d")
    val list = List(1, 2, 3)
    val expected = List((1, "a"), (2, "b"), (3, "c"))
    assert(impl.zip(vector, list) == expected)
  }

  test("Zip should handle gracefully lists with bigger size than vector") {
    val vector = Vector("a", "b", "c")
    val list = List(1, 2, 3, 4, 5)
    val expected = List((1, "a"), (2, "b"), (3, "c"))
    assert(impl.zip(vector, list) == expected)
  }

  test("ToMap should convert list of tuples to map") {
    val list = List((1, "a"), (2, "b"), (3, "c"))
    val expected = Map(1 -> "a", 2 -> "b", 3 -> "c")
    assert(impl.toMap(list) == expected)
  }

  test("ToMap should handle key duplicates like the Map default") {
    val list = List((1, "a"), (2, "b"), (3, "c"), (1, "d"))
    val expected = Map(1 -> "d", 2 -> "b", 3 -> "c")
    assert(impl.toMap(list) == expected)
  }

  test("ToMap should handle empty lists gracefully") {
    val list = List.empty[(Int, String)]
    assert(impl.toMap(list) == Map.empty[Int, String])
  }

  test("Integration of zip and toMap funcs") {
    val vector = Vector("a", "b")
    val list = List(1, 2, 3, 4)
    val expected = Map(1 -> "a", 2 -> "b")
    assert(impl.toMap(impl.zip(vector, list)) == expected)
  }

}

class C2Suite extends ConfiguredSuite {
  val impl: C2 = getImplementations().collect { case x: C2 => x }.head

  test("Init of empty list throws exception") {
    intercept(impl.init(List()))
  }

  test("Init of a list of size one is empty list") {
    val list = List(1)
    val expected = List.empty[Int]
    assert(impl.init(list) == expected)
  }

  test("Init of list of size > 1 behaves accordingly") {
    val list = List(1, 2, 3, 4, 5)
    val expected = List(1, 2, 3, 4)
    assert(impl.init(list) == expected)
  }

}

class C3Suite extends ConfiguredSuite {
  val impl: C3 = getImplementations().collect { case x: C3 => x }.head

  test("Penultimate throws for a list of size 0") {
    val list = List.empty[Int]
    intercept(impl.penultimate(list))
  }

  test("Penultimate throws for a list of size 1") {
    val list = List(1)
    intercept(impl.penultimate(list))
  }

  test("Penultimate returns first elem of a list of size == 2") {
    val list = List(1, 2)
    assert(impl.penultimate(list) == 1)
  }

  test("Penultimate returns second-to-last element on list of size >= 2") {
    val list = List(1, 2, 3, 4)
    assert(impl.penultimate(list) == 3)
  }
}

class C4Suite extends ConfiguredSuite {
  val impl: C4 = getImplementations().collect { case x: C4 => x }.head

  test("Palindrome returns true for a palindrome") {
    val list = List(1, 2, 3, 2, 1)
    assert(impl.isPalindrome(list))
  }

  test("Palindrome returns true for a list of 1") {
    val list = List(1)
    assert(impl.isPalindrome(list))
  }

  test("Palindrome returns true for an empty list") {
    val list = List.empty[Int]
    assert(impl.isPalindrome(list))
  }

  test("Palindrome returns false for not a palindrome") {
    val list = List(1, 2, 3, 4, 5)
    assert(!impl.isPalindrome(list))
  }
}

class C5Suite extends ConfiguredSuite {
  val impl: C5 = getImplementations().collect { case x: C5 => x }.head

  test("RemoveAt returns empty list when empty") {
    val list = List.empty[Int]
    assert(impl.removeAt(1, list) == list)
  }

  test("RemoveAt returns list when index out of bound") {
    val list = List(1, 2, 3)
    assert(impl.removeAt(5, list) == list)
  }

  test("RemoveAt returns list when index is negative") {
    val list = List(1, 2, 3)
    assert(impl.removeAt(-4, list) == list)
  }

  test("RemoveAt returns new list without element at index x") {
    val list = List(1, 2, 3, 4)
    val index = 2
    val expected = List(1, 2, 4)
    assert(impl.removeAt(index, list) == expected)
  }
}

class C6Suite extends ConfiguredSuite {
  val impl: C6 = getImplementations().collect { case x: C6 => x }.head

  test("Pack of empty list should be empty list") {
    val list = List()
    val expected = List()
    assert(impl.pack(list) == expected)
  }

  test(
    "Pack of singleton list should be a list containing that singleton list",
  ) {
    val list = List(1)
    val expected = List(List(1))
    assert(impl.pack(list) == expected)
  }

  test("Pack of list with all identical elements should return one group") {
    val list = List(1, 1, 1, 1, 1)
    val expected = List(List(1, 1, 1, 1, 1))
    assert(impl.pack(list) == expected)
  }

  test(
    "Pack of list with no consecutive duplicates should return all singletons",
  ) {
    val list = List(1, 2, 3, 1, 2, 3)
    val expected = List(List(1), List(2), List(3), List(1), List(2), List(3))
    assert(impl.pack(list) == expected)
  }

  test("Pack of list with consecutive duplicates should group them correctly") {
    val list = List("a", "a", "a", "b", "c", "c", "a")
    val expected =
      List(List("a", "a", "a"), List("b"), List("c", "c"), List("a"))
    assert(impl.pack(list) == expected)
  }

  test(
    "Pack of list with mixed types should group consecutive identical elements",
  ) {
    val list = List(1, 1, "a", "a", "b", 2, 2)
    val expected = List(List(1, 1), List("a", "a"), List("b"), List(2, 2))
    assert(impl.pack(list) == expected)
  }
}

class C7Suite extends ConfiguredSuite {
  val impl: C7 = getImplementations().collect { case x: C7 => x }.head

  test("Encode of empty list returns empty list") {
    val list = List()
    val expected = List()
    assert(impl.encode(list) == expected)
  }

  test(
    "Encode of list with consecutive duplicates returns run-length encoded list",
  ) {
    val list = List("a", "a", "a", "b", "c", "c", "a")
    val expected = List(("a", 3), ("b", 1), ("c", 2), ("a", 1))
    assert(impl.encode(list) == expected)
  }
}

class C8Suite extends ConfiguredSuite {
  val impl: C8 = getImplementations().collect { case x: C8 => x }.head

  test("SplitRecursive of empty list returns two empty lists") {
    val list = List()
    val expected = (List(), List())
    assert(impl.splitRecursive(2, list) == expected)
  }

  test("SplitRecursive with size 0 returns empty list and the original list") {
    val list = List(1, 2, 3)
    val expected = (List(), List(1, 2, 3))
    assert(impl.splitRecursive(0, list) == expected)
  }

  test("SplitRecursive splits list at given index correctly") {
    val list = List(1, 2, 3, 4)
    val expected = (List(1, 2), List(3, 4))
    assert(impl.splitRecursive(2, list) == expected)
  }

  test(
    "SplitRecursive with negative index returns empty list and the original list",
  ) {
    val list = List(1, 2, 3)
    val expected = (List(), List(1, 2, 3))
    assert(impl.splitRecursive(-4, list) == expected)
  }
}

class C9Suite extends ConfiguredSuite {
  val impl: C9 = getImplementations().collect { case x: C9 => x }.head

  test("Decode of empty list returns empty list") {
    val list = List()
    val expected = List()
    assert(impl.decode(list) == expected)
  }

  test("Decode of encoded list returns correct repeated elements") {
    val list = List(("a", 3), ("b", 1))
    val expected = List("a", "a", "a", "b")
    assert(impl.decode(list) == expected)
  }

  test("Decode of tuple with count <= 0 returns empty list") {
    val list = List(("a", -3), ("b", 0))
    val expected = List.empty[String]
    assert(impl.decode(list) == expected)
  }
}

class C10Suite extends ConfiguredSuite {
  val impl: C10 = getImplementations().collect { case x: C10 => x }.head

  test("TakeWhileStrictlyIncreasing of empty list returns empty list") {
    val list = List()
    val expected = List()
    assert(impl.takeWhileStrictlyIncreasing(list) == expected)
  }

  test(
    "TakeWhileStrictlyIncreasing of singleton list returns the list itself",
  ) {
    val list = List(1)
    val expected = List(1)
    assert(impl.takeWhileStrictlyIncreasing(list) == expected)
  }

  test("TakeWhileStrictlyIncreasing stops at equal consecutive elements") {
    val list = List(5, 5, 6, 7)
    val expected = List(5)
    assert(impl.takeWhileStrictlyIncreasing(list) == expected)
  }

  test("TakeWhileStrictlyIncreasing stops at decreasing element") {
    val list = List(1, 8, 9, 5, 10, 2, 3)
    val expected = List(1, 8, 9)
    assert(impl.takeWhileStrictlyIncreasing(list) == expected)
  }

  test(
    "TakeWhileStrictlyIncreasing of strictly increasing list returns the full list",
  ) {
    val list = List(1, 2, 3, 4, 5)
    val expected = List(1, 2, 3, 4, 5)
    assert(impl.takeWhileStrictlyIncreasing(list) == expected)
  }
}

class D1Suite extends ConfiguredSuite {
  val impl: D1 = getImplementations().collect { case x: D1 => x }.head

  test("Func of empty vector returns empty vector") {
    val vector = Vector()
    val list = List(1, 2, 3)
    val expected = Vector()
    assert(impl.func(vector, list) == expected)
  }

  test("Func of empty list returns vector of empty lists") {
    val vector = Vector(1, 2, 3)
    val list = List()
    val expected = Vector(List(), List(), List())
    assert(impl.func(vector, list) == expected)
  }

  test("Func multiplies each element of vector by each element of list") {
    val vector = Vector(1, 2, 3)
    val list = List(1, 2, 3)
    val expected = Vector(List(1, 2, 3), List(2, 4, 6), List(3, 6, 9))
    assert(impl.func(vector, list) == expected)
  }
}

class D2Suite extends ConfiguredSuite {
  val impl: D2 = getImplementations().collect { case x: D2 => x }.head

  test("Max of normal list returns the largest element") {
    val list = List(1, 5, 3, 9, 2)
    val expected = 9
    assert(impl.max(list) == expected)
  }

  test("Max of empty list should throw") {
    intercept(impl.max(List()))
  }

  test("Max of singleton list returns that element") {
    val list = List(7)
    val expected = 7
    assert(impl.max(list) == expected)
  }

  test("Max of list with duplicates returns the largest element") {
    val list = List(2, 2, 2, 3, 3, 3, 1, 1, 1)
    val expected = 3
    assert(impl.max(list) == expected)
  }
}

class D3Suite extends ConfiguredSuite {
  val impl: D3 = getImplementations().collect { case x: D3 => x }.head

  test("Fact of 0 returns 1") {
    assert(impl.func(0) == BigInt(1))
  }

  test("Fact of 1 returns 1") {
    assert(impl.func(1) == BigInt(1))
  }

  test("Fact of 2 returns 2") {
    assert(impl.func(2) == BigInt(2))
  }

  test("Fact of 14 returns correct BigInt") {
    assert(impl.func(14) == BigInt("87178291200"))
  }

  test("Fact of negative numbers throws") {
    intercept(impl.func(-4))
  }
}

class D4Suite extends ConfiguredSuite {
  val impl: D4 = getImplementations().collect { case x: D4 => x }.head

  test("BaseTwoPower of 0 returns 1") {
    assert(impl.baseTwoPower(0).toInt == 1)
  }

  test("BaseTwoPower of normal value returns correct power of 2") {
    assert(impl.baseTwoPower(10).toInt == 1024)
  }

  test("BaseTwoPower of large value returns correct BigInt") {
    assert(impl.baseTwoPower(35) == BigInt(34359738368L))
  }

  test("BaseTwoPower of negative exponent throws") {
    intercept(impl.baseTwoPower(-1))
  }
}

class D5Suite extends ConfiguredSuite {
  val impl: D5 = getImplementations().collect { case x: D5 => x }.head

  test("CatSpace of empty seq returns empty string") {
    val seq = Seq()
    val expected = ""
    assert(impl.catSpace(seq) == expected)
  }

  test("CatSpace of one word returns the word") {
    val seq = Seq("Hello")
    val expected = "Hello"
    assert(impl.catSpace(seq) == expected)
  }

  test("CatSpace of words returns space-separated string") {
    val seq = Seq("I", "have", "a", "dream")
    val expected = "I have a dream"
    assert(impl.catSpace(seq) == expected)
  }

  test("CatSpace of words with trailing spaces preserves them") {
    val seq = Seq("I ", "have")
    val expected = "I  have"
    assert(impl.catSpace(seq) == expected)
  }
}

class D6Suite extends ConfiguredSuite {
  val impl: D6 = getImplementations().collect { case x: D6 => x }.head

  test("Reverse of empty list returns empty list") {
    val list = List()
    val expected = List()
    assert(impl.reverse(list) == expected)
  }

  test("Reverse of singleton list returns the list itself") {
    val list = List(42)
    val expected = List(42)
    assert(impl.reverse(list) == expected)
  }

  test("Reverse of normal list returns reversed list") {
    val list = List(1, 2, 3, 4, 5)
    val expected = List(5, 4, 3, 2, 1)
    assert(impl.reverse(list) == expected)
  }

  test("Reverse of list with mixed types returns reversed list") {
    val list = List(1, "two", 3.0, 'a')
    val expected = List('a', 3.0, "two", 1)
    assert(impl.reverse(list) == expected)
  }
}

class D7Suite extends ConfiguredSuite {
  val impl: D7 = getImplementations().collect { case x: D7 => x }.head
  val mat = List(List(11, 12, 13), List(21, 22, 23), List(31, 32, 33))

  test("FirstColumn of empty matrix throws") {
    intercept(impl.firstColumn(List(List())))
  }

  test("FirstColumn of normal matrix returns first column") {
    val expected = List(11, 21, 31)
    assert(impl.firstColumn(mat) == expected)
  }

  test("Column of empty matrix throws") {
    intercept(impl.column(List(List()), 0))
  }

  test("Column with negative index throws") {
    intercept(impl.column(mat, -1))
  }

  test("Column of normal matrix returns correct column") {
    assert(impl.column(mat, 1) == List(12, 22, 32))
  }

  test("FirstColumn of empty matrix returns empty list") {
    val expected = List()
    assert(impl.firstColumn(List()) == expected)
  }

  test("Column of empty matrix returns empty list") {
    val expected = List()
    assert(impl.column(List(), 0) == expected)
  }

}

class D8Suite extends ConfiguredSuite {
  val impl: D8 = getImplementations().collect { case x: D8 => x }.head

  test("Diagonal of empty matrix returns empty list") {
    val mat = List()
    val expected = List()
    assert(impl.diagonal(mat) == expected)
  }

  test("Diagonal of single-element matrix returns that element") {
    val mat = List(List(42))
    val expected = List(42)
    assert(impl.diagonal(mat) == expected)
  }

  test("Diagonal of square matrix returns main diagonal") {
    val mat = List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9))
    val expected = List(1, 5, 9)
    assert(impl.diagonal(mat) == expected)
  }

  test(
    "Diagonal of matrix with more rows than columns returns shorter diagonal",
  ) {
    val mat = List(List(1, 2), List(3, 4), List(5, 6))
    val expected = List(1, 4)
    assert(impl.diagonal(mat) == expected)
  }

  test(
    "Diagonal of matrix with more columns than rows returns shorter diagonal",
  ) {
    val mat = List(List(1, 2, 3), List(4, 5, 6))
    val expected = List(1, 5)
    assert(impl.diagonal(mat) == expected)
  }
}

class D9Suite extends ConfiguredSuite {
  val impl: D9 = getImplementations().collect { case x: D9 => x }.head

  test("HasZeroRow of empty list returns false") {
    assert(!impl.hasZeroRow(List()))
  }

  test("HasZeroRow of empty matrix returns false") {
    assert(!impl.hasZeroRow(List(List())))
  }

  test("HasZeroRow of matrix without zero row returns false") {
    val mat = List(List(1, 0), List(0, 1))
    assert(!impl.hasZeroRow(mat))
  }

  test("HasZeroRow of matrix with zero row returns true") {
    val mat = List(List(0, 0), List(1, 1))
    assert(impl.hasZeroRow(mat))
  }
}

class D10Suite extends ConfiguredSuite {
  val impl: D10 = getImplementations().collect { case x: D10 => x }.head

  test("IsPrime returns true for small prime numbers") {
    assert(impl.isPrime(2))
  }

  test("IsPrime returns false for small non-prime numbers") {
    assert(!impl.isPrime(4))
  }

  test("IsPrime returns true for large prime numbers") {
    assert(impl.isPrime(29))
  }

  test("IsPrime returns false for large non-prime numbers") {
    assert(!impl.isPrime(100))
  }

  test("IsPrime returns false for zero and negative numbers") {
    assert(!impl.isPrime(0))
  }
}

class D11Suite extends ConfiguredSuite {
  val impl: D11 = getImplementations().collect { case x: D11 => x }.head

  test("LinesLonger of empty list returns empty list") {
    val lines = List[String]()
    val expected = List()
    assert(impl.linesLonger(lines, 1) == expected)
  }

  test("LinesLonger filters nothing when threshold is below 0") {
    val lines = List("abc", "")
    val expected = List("abc", "")
    assert(impl.linesLonger(lines, -5) == expected)
  }

  test("LinesLonger filters out empty strings when threshold is above 0") {
    val lines = List("abc", "")
    val expected = List("abc")
    assert(impl.linesLonger(lines, 1) == expected)
  }

  test("LinesLonger returns lines strictly longer than threshold") {
    val lines = List("01", "012", "0123")
    val expected = List("0123")
    assert(impl.linesLonger(lines, 3) == expected)
  }
}

class D12Suite extends ConfiguredSuite {
  val impl: D12 = getImplementations().collect { case x: D12 => x }.head

  test("LongestLineLength of empty list returns 0") {
    val lines = List()
    val expected = 0
    assert(impl.longestLineLength(lines) == expected)
  }

  test("LongestLineLength of singleton list returns its length") {
    val lines = List("single")
    val expected = 6
    assert(impl.longestLineLength(lines) == expected)
  }

  test("LongestLineLength of normal list returns length of longest string") {
    val lines = List("Scala", "is", "awesome!")
    val expected = 8
    assert(impl.longestLineLength(lines) == expected)
  }
}

class D13Suite extends ConfiguredSuite {
  val impl: D13 = getImplementations().collect { case x: D13 => x }.head

  test("ElimEmptyLines of empty list returns empty list") {
    val lines = List()
    val expected = List()
    assert(impl.elimEmptyLines(lines) == expected)
  }

  test("ElimEmptyLines removes empty strings from list") {
    val lines = List("", "salut", "", "comment")
    val expected = List("salut", "comment")
    print(impl.elimEmptyLines(lines))
    assert(impl.elimEmptyLines(lines) == expected)
  }
}

class D14Suite extends ConfiguredSuite {
  val impl: D14 = getImplementations().collect { case x: D14 => x }.head

  test("LongestLine of empty list returns empty string") {
    val lines = List()
    val expected = ""
    assert(impl.longestLine(lines) == expected)
  }

  test("LongestLine of singleton list returns that element") {
    val lines = List("single")
    val expected = "single"
    assert(impl.longestLine(lines) == expected)
  }

  test("LongestLine of normal list returns longest string") {
    val lines = List("Scala", "is", "awesome!")
    val expected = "awesome!"
    assert(impl.longestLine(lines) == expected)
  }
}

class D15Suite extends ConfiguredSuite {
  val impl: D15 = getImplementations().collect { case x: D15 => x }.head

  test("Compress of empty list returns empty list") {
    val list = List()
    val expected = List()
    assert(impl.compress(list) == expected)
  }

  test("Compress removes consecutive duplicates") {
    val list =
      List('a', 'a', 'a', 'a', 'b', 'c', 'c', 'a', 'a', 'd', 'e', 'e', 'e', 'e')
    val expected = List('a', 'b', 'c', 'a', 'd', 'e')
    assert(impl.compress(list) == expected)
  }
}

class D16Suite extends ConfiguredSuite {
  val impl: D16 = getImplementations().collect { case x: D16 => x }.head

  test("AverageOfDoubles of normal list returns correct average") {
    val list = List(2.0, 2.5, 4.5)
    val expected = 3.0
    assert(impl.averageOfDoubles(list) == expected)
  }

  test("AverageOfDoubles of singleton list returns that element") {
    val list = List(5.0)
    val expected = 5.0
    assert(impl.averageOfDoubles(list) == expected)
  }

  test(
    "AverageOfDoubles of list with negative numbers returns correct average",
  ) {
    val list = List(-1.0, -2.0, -3.0)
    val expected = -2.0
    assert(impl.averageOfDoubles(list) == expected)
  }

  test("AverageOfDoubles of empty list throws") {
    val list = List.empty[Double]
    intercept(impl.averageOfDoubles(list))
  }
}
