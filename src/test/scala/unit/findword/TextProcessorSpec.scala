package unit.findword

import findword.TextProcessor
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}
import unit.findword.generator.TextGenerator

/**
 * @author Dmitri Carpov
 */
class TextProcessorSpec extends WordSpec with GeneratorDrivenPropertyChecks with Matchers {

  implicit override val generatorDrivenConfig = PropertyCheckConfig(minSuccessful = 100)

  "TextProcessor.contains" when {
    "an empty text is provided" must {
      "return false" in {
        forAll(TextGenerator.word) { word: String =>
          assert(!TextProcessor.contains("", word))
        }
      }
    }

    "an empty word is provided" must {
      "return false" in {
        forAll(TextGenerator.randomText) { text: String =>
          assert(!TextProcessor.contains(text, ""))
        }
      }
    }

    "the searched word occurs one or more time in the text" must {
      "return true for random text and word" in {
        val gen = for {
          word <- TextGenerator.word
          text <- TextGenerator.textWithWord(word)
        } yield (text, word)

        forAll(gen) {
          case (text: String, word: String) =>
            assert(TextProcessor.contains(text, word))
        }
      }

      "return true for static text and word" in {
        val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
          "Cras rhoncus, magna et hendrerit blandit, diam arcu rhoncus felis, eget molestie ipsum ligula sed \n" +
          "tortor. Mauris sodales metus eu enim imperdiet posuere."

        assert(TextProcessor.contains(text, "hendrerit"))
      }
    }

    "the searched word does not occur in the text" must {
      "return false for random text and word" in {
        val gen = for {
          word <- TextGenerator.word
          text <- TextGenerator.textWithoutWord(word)
        } yield (text, word)

        forAll(gen) {
          case (text: String, word: String) =>
            assert(!TextProcessor.contains(text, word))
        }
      }

      "return false for static text and word" in {
        val text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n" +
          "Cras rhoncus, magna et hendrerit blandit, diam arcu rhoncus felis, eget molestie ipsum ligula sed \n" +
          "tortor. Mauris sodales metus eu enim imperdiet posuere."

        assert(!TextProcessor.contains(text, "endreri"))
      }
    }

    "there is a word which starts with the searched one" must {
      "return false" in {
        val gen = for {
          wordPart1 <- TextGenerator.word
          wordPart2 <- TextGenerator.word
          text <- TextGenerator.textWithWord(wordPart1.concat(wordPart2)).suchThat(!_.split("[\\s]").contains(wordPart1))
        } yield (wordPart1, text)

        forAll(gen) {
          case (wordPart1: String, text: String) =>
            assert(!TextProcessor.contains(text, wordPart1))
        }
      }
    }

    "there is a word which ends with the searched one" must {
      "return false" in {
        val gen = for {
          wordPart1 <- TextGenerator.word
          wordPart2 <- TextGenerator.word
          text <- TextGenerator.textWithWord(wordPart1.concat(wordPart2)).suchThat(!_.split("[\\s]").contains(wordPart2))
        } yield (wordPart2, text)

        forAll(gen) {
          case (wordPart2: String, text: String) =>
            assert(!TextProcessor.contains(text, wordPart2))
        }
      }
    }
  }
}
