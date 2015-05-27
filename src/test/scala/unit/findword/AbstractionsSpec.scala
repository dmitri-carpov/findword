package unit.findword

import findword.Abstractions
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

/**
 * @author Dmitri Carpov
 */
class AbstractionsSpec extends WordSpec with GeneratorDrivenPropertyChecks with Matchers {
  "Abstractions.fmap" when {
    "a trasformation function from type A to type B is provided" must {
      "return a function which takes a collection of A and returns a collection of B" in {
        def f(number: Int): String = number.toString

        forAll { numbers: Seq[Int] =>
          val result = Abstractions.fmap(f)(numbers)

          assert(result.size == numbers.size)
          assert(numbers.map(f) == result)
        }
      }
    }
  }

  "Abstractions.left" must {
    "return left value of tuple" in {
      forAll { (a: Int, b: Int) =>
        assert(Abstractions.left(a, b) == a)
      }
    }
  }

  "Abstractions.removeDuplicates" when {
    "an empty collection is provided" must {
      "return empty collection" in {
        assert(Abstractions.removeDuplicates[Int, Int](Seq.empty[Int], x => x).isEmpty)
      }
    }

    "a collection without duplicates is provided" must {
      "return same collection collection" in {
        forAll { numbers: Seq[Int] =>
          val noDuplicatesSeq = numbers.distinct
          val result = Abstractions.removeDuplicates[Int, Int](noDuplicatesSeq, x => x)
          assert(result.sorted == noDuplicatesSeq.sorted) // sorted is important here, for removeDuplicates may break order.
        }
      }
    }
  }
}
