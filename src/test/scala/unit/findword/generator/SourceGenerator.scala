package unit.findword.generator

import org.scalacheck.Gen

/**
 * @author Dmitri Carpov
 */
case class Source(content: Option[String])

object SourceGenerator {
  def sourcesWithWord(word: String): Gen[List[Source]] = Gen.listOf(TextGenerator.simplifiedWithWordText(word))
  def sourcesWithoutWord(word: String): Gen[List[Source]] = Gen.listOf(TextGenerator.simplifiedWithoutWordText(word))
  def invalidSources = Gen.listOf(Gen.const(Source(None)))

  private implicit def textToSource(textGen: Gen[String]): Gen[Source] = textGen.map(content => Source(Some(content)))
}
