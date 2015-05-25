package generator

import org.scalacheck.Gen

/**
 * Use this generator in cases where it's not required to generate sophisticated texts.
 * This one is a lot faster than TextGenerator.
 *
 * Motivation:
 *   Using TextGenerator in combination with other generators may keep tests busy for minutes.
 *
 * @author Dmitri Carpov
 */
object SimplifiedTextGenerator {

  def word = Gen.listOfN(5, Gen.alphaNumChar).map(_.mkString(""))

  /**
   * Simplified text generator. Used for more complex generation logic to avoid overhead.
   *
   */
  def randomText = Gen.listOfN(20, Gen.alphaStr).map(_.mkString(""))

  /**
   * Simplified text generator. Used for more complex generation logic to avoid overhead.
   *
   */
  def withWordText(word: String) = Gen.listOfN(5, Gen.alphaStr).map(_.mkString(s" ${word} "))

  /**
   * Simplified text generator. Used for more complex generation logic to avoid overhead.
   *
   */
  def withoutWordText(word: String) = Gen.listOfN(5, Gen.alphaStr.suchThat(w => w != word)).map(_.mkString(" "))
}
