package squants

import squants.time.Frequency

trait SquantsImplicitConversions { 
     /**
   * Provides implicit conversions that allow Doubles to lead in * and / by Time operations
   * {{{
   *    1.5 * Kilometers(10) should be(Kilometers(15))
   * }}}
   *
   * @param d Double
   */
  implicit class SquantifiedDouble(d: Double) {
    def *[A <: Quantity[A]](that: A): A = that * d
    def *[A](that: SVector[A]): SVector[A] = that * d
    def *[A <: Quantity[A]](that: Price[A]): Price[A] = that * d
    def /(that: Time): Frequency = Each(d) / that
    def per(that: Time): Frequency = /(that)
  }

  /**
   * Provides implicit conversions that allow Longs to lead in * and / by Time operations
   * {{{
   *    5 * Kilometers(10) should be(Kilometers(15))
   * }}}
   *
   * @param l Long
   */
  implicit class SquantifiedLong(l: Long) {
    def *[A <: Quantity[A]](that: A): A = that * l.toDouble
    def *[A](that: SVector[A]): SVector[A] = that * l.toDouble
    def *[A <: Quantity[A]](that: Price[A]): Price[A] = that * l.toDouble
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
  implicit class SquantifiedInt(l: Int) {
    def *[A <: Quantity[A]](that: A): A = that * l.toDouble
    def *[A](that: SVector[A]): SVector[A] = that * l.toDouble
    def *[A <: Quantity[A]](that: Price[A]): Price[A] = that * l.toDouble
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
  implicit class SquantifiedBigDecimal(bd: BigDecimal) {
    def *[A <: Quantity[A]](that: A): A = that * bd.toDouble
    def *[A](that: SVector[A]): SVector[A] = that * bd.toDouble
    def *[A <: Quantity[A]](that: Price[A]): Price[A] = that * bd.toDouble
    def /(that: Time) = Each(bd) / that
    def per(that: Time): Frequency = /(that)
  }
}

object SquantsImplicitConversions extends SquantsImplicitConversions
