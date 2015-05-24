package findword

/**
 * @author Dmitri Carpov
 */
object Abstractions {
  def fmap[A, B](f: A => B): Seq[A] => Seq[B] = seq => seq.map(f)
  def left[A](pair: (A, A)): A = pair._1
}
