package findword

/**
 * @author Dmitri Carpov
 */
object TextProcessor {
  /**
   * Check if content contains the given word.
   *
   * @param text Text to check.
   * @param word Word to find.
   * @return true if the content contains the word, false otherwise.
   */
  def contains(text: String, word: String): Boolean = {
    if(!word.trim.isEmpty) {
      text.split("\\W+").contains(word)
    } else {
      false
    }
  }
}
