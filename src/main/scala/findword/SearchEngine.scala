package findword

/**
 * @author Dmitri Carpov
 */
object SearchEngine {
  /**
   * Search for the sources which contain the given word.
   *
   * @param sources Collection of content sources.
   * @param content Function which can extract content from sources.
   * @param word Word to search for.
   * @tparam A Source type
   * @return Collection of sources which contain the given word.
   */
  def search[A](sources: Seq[A], content: A => Option[String], word: String): Seq[A] = {
    sources.filter(source => content(source).fold(false)(content => TextProcessor.contains(content, word)))
  }
}
