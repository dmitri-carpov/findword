package findword

import java.io.File

/**
 * @author Dmitri Carpov
 */
object FindWord {
  case class SystemIO[A](out: Any => A, err: Any => A)
  private val defaultSystemIO = SystemIO(System.out.println, System.err.println)

  def main(args: Array[String]): Unit = find(defaultSystemIO)(args)

  def find(systemIO: SystemIO[_])(args: Array[String]): Unit = {
    if (args.length == 2) {
      val file = new File(args(1))
      if (!file.exists()) {
        systemIO.err("file or directory does not exist: " + file.getAbsolutePath)
      } else {
        FileUtils.grep(file, args(0)).map(_.getAbsolutePath).foreach(systemIO.out)
      }
    } else {
      systemIO.err("Usage: findword <word> <directory>")
    }
  }
}