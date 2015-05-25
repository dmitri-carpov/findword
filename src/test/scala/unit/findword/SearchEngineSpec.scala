package unit.findword

import findword.SearchEngine
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import generator.{Source, SourceGenerator, TextGenerator}

import scala.util.Random

/**
 * @author Dmitri Carpov
 */
class SearchEngineSpec extends WordSpec with GeneratorDrivenPropertyChecks with Matchers {
  implicit override val generatorDrivenConfig = PropertyCheckConfig(minSuccessful = 100)

  "SearchEngine.search" when {
    "all sources contain the searched word" must {
      "return same amount of sources" in {
        val gen = for {
          word <- TextGenerator.word
          sources <- SourceGenerator.sourcesWithWord(word)
        } yield (word, sources)

        forAll(gen) {
          case (word: String, sources: Seq[Source]) =>
            assert(SearchEngine.search[Source](sources, _.content, word).size == sources.size)
        }
      }
    }

    "all sources DO NOT contain the searched word" must {
      "return empty collection" in {
        val gen = for {
          word <- TextGenerator.word
          sources <- SourceGenerator.sourcesWithoutWord(word)
        } yield (word, sources)

        forAll(gen) {
          case (word: String, sources: Seq[Source]) =>
            assert(SearchEngine.search[Source](sources, _.content, word).isEmpty)
        }
      }
    }

    "all sources are invalid" must {
      "return empty collection" in {
        val gen = for {
          word <- TextGenerator.word
          sources <- SourceGenerator.invalidSources
        } yield (word, sources)

        forAll(gen) {
          case (word: String, sources: Seq[Source]) =>
            assert(SearchEngine.search[Source](sources, _.content, word).isEmpty)
        }
      }
    }

    "no sources provided" must {
      "return empty collection" in {
        forAll(TextGenerator.word) { word =>
          assert(SearchEngine.search[Source](Seq.empty[Source], _.content, word).isEmpty)
        }
      }
    }

    "all kind of sources provided" must {
      "return only those sources which contain the searched word" in {
        val gen = for {
          word <- TextGenerator.word
          sourcesWithWord <- SourceGenerator.sourcesWithWord(word)
          sourcesWithoutWord <- SourceGenerator.sourcesWithoutWord(word)
          invalidSources <- SourceGenerator.invalidSources
        } yield (word, sourcesWithWord, sourcesWithoutWord, invalidSources)

        forAll(gen) {
          case (word, sourcesWithWord, sourcesWithoutWord, invalidSources) =>
            val sources = Random.shuffle(sourcesWithWord ++ sourcesWithoutWord ++ invalidSources)
            val result = SearchEngine.search[Source](sources, _.content, word)

            assert(result.size == sourcesWithWord.size)
            assert(sourcesWithWord.forall(source => result.contains(source)))
        }
      }
    }
  }
}
