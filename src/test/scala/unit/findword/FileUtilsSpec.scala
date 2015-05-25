package unit.findword

import java.io.File
import java.util.UUID

import findword.FileUtils
import generator._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}

/**
 * @author Dmitri Carpov
 */
class FileUtilsSpec extends WordSpec with GeneratorDrivenPropertyChecks with Matchers {
  "FileUtils.unfold" when {
    "a directory contains a hierarchy of directories and files" must {
      "return all found files" in {
        val directory = FileStructureGenerator()
        val result = FileUtils.unfold(new File(directory.path))
        val allFiles = directory.allFiles().map(_.path)
        assert(result.size == allFiles.size)
        assert(result.map(_.getAbsolutePath).forall(name => allFiles.contains(name)))
      }
    }

    "a directory is empty" must {
      "return empty list" in {
        val emptyDirectory = IO.createTempDirectory
        assert(FileUtils.unfold(new File(emptyDirectory)).isEmpty)
      }
    }

    "a directory does not exists" must {
      "return empty list" in {
        assert(FileUtils.unfold(new File(UUID.randomUUID().toString)).isEmpty)
      }
    }
  }

  "FileUtils.content" when {
    "a file has content" must {
      "return file's content" in {
        val directory = FileStructureGenerator()
        directory.allFiles(_.accessible).headOption.fold(assert(false, "no files?")) { file =>
          val result = FileUtils.content(new File(file.path)).getOrElse(assert(false, "empty file?"))
          assert(result == file.content)
        }
      }
    }

    "a file does not exists" must {
      "return None" in {
        assert(FileUtils.content(new File(UUID.randomUUID().toString)).isEmpty)
      }
    }

    "has no access to file" must {
      "return None" in {
        val directory = FileStructureGenerator()
        directory.allFiles(!_.accessible).headOption.fold(assert(false, "no files?")) { file =>
          assert(FileUtils.content(new File(file.path)).isEmpty)
        }
      }
    }

    "a directory is provided" must {
      "return None" in {
        val directory = FileStructureGenerator()
        assert(FileUtils.content(new File(directory.path)).isEmpty)
      }
    }
  }

  "FileUtils.grep" when {

    "a directory is a root" must {
      "find all files which contain the given word" in {
        val word = Generators.singleValue(SimplifiedTextGenerator.word)
        val directory = FileStructureGenerator(word)
        val files = directory.allFiles(f => f.containsWord && f.accessible)
        val result = FileUtils.grep(new File(directory.path), word)

        assert(result.size == files.size)
        assert(result.forall(r => files.map(_.path).contains(r.getAbsolutePath)))
      }

      "return empty collection when directory does not exist" in {
        assert(FileUtils.grep(new File(UUID.randomUUID().toString), "whatever").isEmpty)
      }
    }

    "file is a root" must {
      "return file if it contains the word" in {
        val word = Generators.singleValue(SimplifiedTextGenerator.word)
        val directory = FileStructureGenerator(word)
        val files = directory.allFiles(f => f.containsWord && f.accessible)

        files.headOption.fold(assert(false, "no files which contain the word?")) { file =>
          val result = FileUtils.grep(new File(file.path), word)
          assert(result.size == 1)
          result.headOption.fold(assert(false))(f => assert(f.getAbsolutePath == file.path))
        }

      }

      "return empty list if file does not contain the word" in {
        val word = Generators.singleValue(SimplifiedTextGenerator.word)
        val directory = FileStructureGenerator(word)
        val files = directory.allFiles(f => !f.containsWord && f.accessible)

        files.headOption.fold(assert(false, "no files which does not contain the word?")) { file =>
          assert(FileUtils.grep(new File(file.path), word).isEmpty)
        }
      }

      "return empty list if file is not accessible" in {
        val word = Generators.singleValue(SimplifiedTextGenerator.word)
        val directory = FileStructureGenerator(word)
        val files = directory.allFiles(!_.accessible)

        files.headOption.fold(assert(false, "no files which does not contain the word?")) { file =>
          assert(FileUtils.grep(new File(file.path), word).isEmpty)
        }
      }
    }
  }
}
