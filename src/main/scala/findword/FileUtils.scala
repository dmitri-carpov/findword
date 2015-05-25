package findword


import java.io.File

import scala.io.Source

/**
 *
 * @author Dmitri Carpov
 */
object FileUtils {
  /**
   * Find all files in the given directory recursively.
   *
   * Warning: number of nested directories is limited by JVM number of maximum recursions.
   *
   * @param root Root directory. If a file is provided then this file will be returned.
   * @return A collection of all files inside the given directory.
   *         The given file if the root parameter is a file.
   *         An empty collection if there is no single file deep in the directory hierarchy or directory does not exists.
   */
  def unfold(root: File): Seq[File] = {
    if (root.isDirectory) {
      val content = root.listFiles()
      val files = content.filter(_.isFile)
      val directories = content.filter(_.isDirectory)

      files ++ directories.flatMap(d => unfold(d))
    } else {
      if (root.exists()) Seq(root) else Seq.empty
    }
  }

  /**
   * Extract content from a file.
   *
   * @param file File to extract content from.
   * @return File's content or None if content is not extractable (for ex.: permissions exception or
   *         the given file is a directory or link etc.)
   */
  def content(file: File): Option[String] = {
    try {
      Some(Source.fromFile(file).getLines().mkString("\n"))
    } catch {
      case t: Throwable => None
    }
  }

  /**
   * Find files in the given directory which contain the given word.
   * Files are searched recursively.
   * If the give root is a file it will be processed.
   *
   *
   * @param root Directory or file to scan for the given word.
   * @param word Word to search for.
   * @return Collection of files which contain the given word. Empty collection if no files found.
   *
   * @see unfold(File): Seq[File]
   */
  def grep(root: File, word: String): Seq[File] = {
    SearchEngine.search(FileUtils.unfold(root), FileUtils.content, word)
  }
}
