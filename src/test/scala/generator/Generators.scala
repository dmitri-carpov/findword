package generator

import org.scalacheck.Gen

/**
 * @author Dmitri Carpov
 */
object Generators {
  def singleValue[A](gen: Gen[A]): A = gen.sample.getOrElse(throw new RuntimeException("cannot generate value"))
}
