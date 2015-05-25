package findword

/**
 * @author Dmitri Carpov
 */
object Abstractions {
  def fmap[A, B](f: A => B): Seq[A] => Seq[B] = seq => seq.map(f)
  def left[A](pair: (A, A)): A = pair._1

  /**
   * Remove duplicated entries.
   *
   * @param seq Collection to process.
   * @param f Function to extract key which defines duplicates.
   * @tparam A Object types.
   * @tparam B Key type.
   * @return Collection without duplicates.
   */
  def removeDuplicates[A, B](seq: Seq[A], f: A => B): Seq[A] = seq.groupBy(f(_)).map(_._2.head).toSeq
}
