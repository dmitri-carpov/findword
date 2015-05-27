package integration

import java.util.UUID

import findword.FindWord
import findword.FindWord.SystemIO
import generator.{FileStructureGenerator, Generators, SimplifiedTextGenerator}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}

/**
 * @author Dmitri Carpov
 */
class FindWordSpec extends WordSpec with GeneratorDrivenPropertyChecks with Matchers {
  type Out = StringBuilder
  type Err = StringBuilder

  // unique separator to avoid false positive results when splitting messages.
  private val separator = UUID.randomUUID().toString

  private def testIO(): (Out, Err, SystemIO[StringBuilder]) = {
    val out = new Out
    val err = new Err
    (out, err, FindWord.SystemIO(msg => out.append(msg).append(separator), msg => err.append(msg).append(separator)))
  }

  "findword" when {
    "no arguments is provided" must {
      "print usage message" in {
        val io = testIO()
        FindWord.find(io._3)(Array())
        assert(io._2.startsWith("Usage"))
      }
    }

    "one argument is provided" must {
      "print usage message" in {
        val io = testIO()
        val randomWord = Generators.singleValue(SimplifiedTextGenerator.word)

        FindWord.find(io._3)(Array(randomWord))
        assert(io._2.startsWith("Usage"))
      }
    }

    "not existing file or directory is provided" must {
      "print error message" in {
        val io = testIO()
        val randomPath = Generators.singleValue(SimplifiedTextGenerator.word)
        val randomWord = Generators.singleValue(SimplifiedTextGenerator.word)

        FindWord.find(io._3)(Array(randomWord, randomPath))
        assert(io._2.startsWith("file or directory does not exist"))
      }
    }

    "a directory with files containing the word is provided" must {
      "print all files containing the word" in {
        val io = testIO()

        val word = Generators.singleValue(SimplifiedTextGenerator.word)
        val directory = FileStructureGenerator(word)
        val files = directory.allFiles(f => f.containsWord && f.accessible)

        FindWord.find(io._3)(Array(word, directory.path))

        val result = io._1.toString.split(separator)

        assert(result.size == files.size)
        assert(files.map(_.path).forall(result.contains(_)))
      }
    }
  }

}
