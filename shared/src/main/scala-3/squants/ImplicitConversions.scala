package squants

import squants.time.Frequency

import scala.language.implicitConversions

/**
 * Provides implicit conversions that allow Doubles to lead in * and / by Time operations
 * {{{
 *    1.5 * Kilometers(10) should be(Kilometers(15))
 * }}}
 *
 * @param d Double
 */
extension [A <: Quantity[A]](d: Double) {
  def *(that: Quantity[A]): A = that.times(d)
  def *(that: Price[A]): Price[A] = that * d
}

extension [A](d: Double) {
  def *(that: SVector[A]): SVector[A] = that * d
}

extension (d: Double) {
  def /(that: Time): Frequency = Each(d) / that
  def per(that: Time): Frequency = Each(d) / (that)
}

/**
 * Provides implicit conversions that allow Longs to lead in * and / by Time operations
 * {{{
 *    5 * Kilometers(10) should be(Kilometers(15))
 * }}}
 *
 * @param l Long
 */
extension [A <: Quantity[A]](l: Long) {
  def *(that: A): A = that * l.toDouble
  def *(that: Price[A]): Price[A] = that * l.toDouble
}

extension [A](l:Long) {
  def *(that: SVector[A]): SVector[A] = that * l.toDouble
}

extension (l: Long) {
  def /(that: Time) = Each(l) / that
  def per(that: Time): Frequency = /(that)
}

/**
  * Provides implicit conversions that allow Int to lead in * and / by Time operations
  * {{{
  *    5 * Kilometers(10) should be(Kilometers(15))
  * }}}
  *
  * @param l Int
  */
extension [A <: Quantity[A]](l: Int) {
  def *(that: A): A = that * l.toDouble
  def *(that: Price[A]): Price[A] = that * l.toDouble
}

extension [A](l: Int) {
  def *(that: SVector[A]): SVector[A] = that * l.toDouble
}

extension (l: Int) {
  def /(that: Time) = Each(l) / that
  def per(that: Time): Frequency = /(that)
}

/**
 * Provides implicit conversions that allow BigDecimals to lead in * and / by Time operations
 * {{{
 *    BigDecimal(1.5) * Kilometers(10) should be(Kilometers(15))
 * }}}
 *
 * @param bd BigDecimal
 */
extension [A <: Quantity[A]](l: BigDecimal) {
  def *(that: A): A = that * l.toDouble
  def *(that: Price[A]): Price[A] = that * l.toDouble

}

extension [A](l: BigDecimal) {
  def *(that: SVector[A]): SVector[A] = that * l.toDouble
}

extension (l: BigDecimal) {
  def /(that: Time) = Each(l) / that
  def per(that: Time): Frequency = /(that)
}

trait SquantsImplicitConversions {
  
}

object SquantsImplicitConversions extends SquantsImplicitConversions {
  val x= 10L / squants.time.Hours(25)

  val y = 10L * Kilograms(50)
}
