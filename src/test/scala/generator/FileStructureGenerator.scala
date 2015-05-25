package generator

import java.io.{File, FileWriter}
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.{Files, Paths}
import java.util.UUID

import findword.{Abstractions, TextProcessor}
import org.scalacheck.Gen

import scala.collection.JavaConversions

/**
 * @author Dmitri Carpov
 */
case class TestFile(name: String, path: String, content: String, containsWord: Boolean, accessible: Boolean)

case class TestDirectory(name: String, path: String, files: Seq[TestFile], directories: Seq[TestDirectory]) {
  def allFiles(f: TestFile => Boolean = _ => true): Seq[TestFile] = {
    files.filter(f) ++ directories.flatMap(_.allFiles(f))
  }
}

object FileStructureGenerator {
  def apply(word: String = ""): TestDirectory = {
    val rootPath = IO.createTempDirectory

    val generatedWord = if (word.isEmpty) Generators.singleValue(SimplifiedTextGenerator.word) else word

    val directories = genDirectories(3, rootPath)(generatedWord).sample.
      getOrElse(throw new RuntimeException("oops, cannot generate directory structure"))

    IO.build(directories)

    directories
  }

  //
  // Generators
  //
  private def genFileWhichContainsWord(path: String)(implicit word: String): Gen[TestFile] = for {
    name <- genName
    content <- SimplifiedTextGenerator.withWordText(word)
  } yield TestFile(name, buildPath(path, name), content, true, true)

  private def genFileWhichDoesNotContainWord(path: String)(implicit word: String): Gen[TestFile] = for {
    name <- genName
    content <- SimplifiedTextGenerator.withoutWordText(word)
  } yield TestFile(name, buildPath(path, name), content, false, true)

  private def genFileNotAccessible(path: String)(implicit word: String): Gen[TestFile] = for {
    name <- genName
    content <- SimplifiedTextGenerator.randomText
  } yield TestFile(name, buildPath(path, name), content, TextProcessor.contains(content, word), false)

  private def genDirectories(depth: Int, path: String)(implicit word: String): Gen[TestDirectory] = for {
    name <- genName
    path <- Gen.const(buildPath(path, name))

    numberOfFiles <- Gen.choose(5, 20)
    files <- Gen.listOfN(numberOfFiles,
      Gen.oneOf(
        genFileWhichContainsWord(path),
        genFileWhichDoesNotContainWord(path),
        genFileNotAccessible(path)))
    filesWithoutDuplicates <- Gen.const(Abstractions.removeDuplicates[TestFile, String](files, _.path.toLowerCase))

    // directories generation
    numberOfDirectories <- Gen.choose(0, depth)
    directories <- Gen.listOfN(numberOfDirectories, genDirectories(depth - 1, path))
    directoriesWithoutNameConflicts <- Gen.const(directories.
      filter(d => !files.map(_.path.toLowerCase()).contains(d.path.toLowerCase)))
    directoriesWithoutDuplicates <- Gen.const(Abstractions.
      removeDuplicates[TestDirectory, String](directoriesWithoutNameConflicts, _.path.toLowerCase))

  } yield TestDirectory(name, path, filesWithoutDuplicates, directoriesWithoutDuplicates)

  private def genName = for {
    size <- Gen.choose(1, 20)
    name <- Gen.listOfN(size, Gen.alphaChar)
  } yield name.mkString("")

  private def buildPath(path: String, name: String) = s"${path}/${name}"
}


/**
 * Object for performing IO operations.
 */
object IO {
  /**
   * Create a temporary directory.
   * Has side-effects.
   *
   * @return
   */
  def createTempDirectory: String = {
    val directory = Files.createTempDirectory(UUID.randomUUID().toString).toFile
    directory.mkdir()
    directory.deleteOnExit()
    directory.getAbsolutePath
  }

  /**
   * Build directories and files based on the given TestDirectory structure.
   * Has side-effects.
   *
   * @param directory Directories structure.
   * @return Unit
   */
  def build(directory: TestDirectory): Unit = {
    val dir = new File(directory.path)
    dir.mkdir()
    directory.files.foreach(createFile(dir))
    directory.directories.foreach(subDirectory => build(subDirectory))
  }

  /**
   * Create file.
   * Has side-effects.
   *
   * @param directory Parent directory.
   * @param testFile File to create.
   * @return Unit
   */
  private def createFile(directory: File)(testFile: TestFile): Unit = {
    val file = new File(directory, testFile.name)
    file.createNewFile()
    val writer = new FileWriter(file)
    try {
      writer.write(testFile.content)
    } finally {
      writer.close()
    }
    if (!testFile.accessible) {
      val permissions = Set(PosixFilePermission.OWNER_WRITE)
      Files.setPosixFilePermissions(Paths.get(testFile.path), JavaConversions.setAsJavaSet(permissions))
    }

//    file.deleteOnExit() // comment it out to trace results
  }
}
