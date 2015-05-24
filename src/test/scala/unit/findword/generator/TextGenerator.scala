package unit.findword.generator

import org.scalacheck.Gen

/**
 * @author Dmitri Carpov
 */
object TextGenerator {
  import findword.Abstractions._

  /**
   * Generator for words which consist of english alphabet, numbers and alphanumeric words.
   *
   * @return Generator which produce non empty words.
   */
  def word = Gen.frequency(
    (20, Gen.alphaStr.suchThat(!_.isEmpty)),
    (3, Gen.numStr.suchThat(!_.isEmpty)),
    (1, asString(Gen.listOf(Gen.alphaNumChar).suchThat(!_.isEmpty))))

  /**
   * Create random text generator.
   *
   */
  def randomText: Gen[String] = buildText(word)

  /**
   * Text of random size which does not contain the given word.
   *
   * @param aWord Word which should not occur in the generated text.
   * @return Text generator.
   */
  def textWithoutWord(aWord: String): Gen[String] = {
    buildText(word, !_.contains(aWord))
  }

  /**
   * Text of random size which contain the given word.
   *
   * @param aWord Word which must occur in the generated text.
   * @return Text generator.
   */
  def textWithWord(aWord: String): Gen[String] = {
    val wordGen = Gen.frequency((2, aWord), (15, word))
    buildText(wordGen, _.contains(aWord))
  }

  /**
   * Build a generator which produces text from the given word generator and which satisfies the given filter.
   *
   * @param wordGen Words generator.
   * @param filter Filter function. By default does not affect initial generator.
   * @return Text generator.
   */
  private def buildText(wordGen: Gen[String], filter: Seq[String] => Boolean = _ => true): Gen[String] = {
    val separator = Gen.frequency(
      (10, " "),
      (5, ", "),
      (3, ". "),
      (2, ".\n"),
      (1, "; ")
    )
    // build generator which combines words with separators in tuple like (word, separator)
    val wordsWithSeparators = Gen.listOf(Gen.zip(wordGen, separator))
    // apply the given filter only on the list of words, not separators!
    val acceptableListOfWords = wordsWithSeparators.suchThat(wordWithSeparator => filter(fmap(left[String])(wordWithSeparator)))
    // build a text from a collection of strings which is build from concatenation of words with separators
    asString(acceptableListOfWords.map(wordWithSeparator => fmap(concatenate)(wordWithSeparator)))
  }

  /**
   * Converts a generator of collections to a String generator.
   *
   * @param gen Generator to convert to a String generator.
   * @tparam A Collection type.
   * @return String generator.
   */
  private def asString[A](gen: Gen[Seq[A]]): Gen[String] = gen.map(_.mkString(""))


  private def concatenate(strings: (String, String)): String = strings._1 + strings._2
}
