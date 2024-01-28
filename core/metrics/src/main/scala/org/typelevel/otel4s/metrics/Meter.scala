/*
 * Copyright 2022 Typelevel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.typelevel.otel4s.metrics

import cats.Applicative
import cats.effect.kernel.Resource

@annotation.implicitNotFound("""
Could not find the `Meter` for ${F}. `Meter` can be one of the following:

1) No-operation (a.k.a. without measurements)

import Meter.Implicits.noop

2) Manually from MeterProvider

val meterProvider: MeterProvider[IO] = ???
meterProvider
  .get("com.service.runtime")
  .flatMap { implicit meter: Meter[IO] => ??? }
""")
trait Meter[F[_]] {

  /** Creates a builder of [[Counter]] instrument that records values of type
    * `A`.
    *
    * The [[Counter]] is monotonic. This means the aggregated value is nominally
    * increasing.
    *
    * @note
    *   the `A` type must be provided explicitly, for example
    *   `meter.counter[Long]` or `meter.counter[Double]`
    *
    * @example
    *   {{{
    * val meter: Meter[F] = ???
    *
    * val doubleCounter: F[Counter[F, Double]] =
    *   meter.counter[Double]("double-counter").create
    *
    * val longCounter: F[Counter[F, Long]] =
    *   meter.counter[Long]("long-counter").create
    *   }}}
    *
    * @see
    *   See [[upDownCounter]] for non-monotonic alternative
    *
    * @param name
    *   the name of the instrument
    *
    * @tparam A
    *   the type of the measurement. [[scala.Long]] and [[scala.Double]] are
    *   supported out of the box
    */
  def counter[A: MeasurementValue](name: String): Counter.Builder[F, A]

  /** Creates a builder of [[Histogram]] instrument that records values of type
    * `A`.
    *
    * [[Histogram]] metric data points convey a population of recorded
    * measurements in a compressed format. A histogram bundles a set of events
    * into divided populations with an overall event count and aggregate sum for
    * all events.
    *
    * @note
    *   the `A` type must be provided explicitly, for example
    *   `meter.histogram[Long]` or `meter.histogram[Double]`
    *
    * @example
    *   {{{
    * val meter: Meter[F] = ???
    *
    * val doubleHistogram: F[Histogram[F, Double]] =
    *   meter.histogram[Double]("double-histogram").create
    *
    * val longHistogram: F[Histogram[F, Long]] =
    *   meter.histogram[Long]("long-histogram").create
    *   }}}
    *
    * @param name
    *   the name of the instrument
    *
    * @tparam A
    *   the type of the measurement. [[scala.Long]] and [[scala.Double]] are
    *   supported out of the box
    */
  def histogram[A: MeasurementValue](name: String): Histogram.Builder[F, A]

  /** Creates a builder of [[UpDownCounter]] instrument that records values of
    * type `A`.
    *
    * The [[UpDownCounter]] is non-monotonic. This means the aggregated value
    * can increase and decrease.
    *
    * @note
    *   the `A` type must be provided explicitly, for example
    *   `meter.upDownCounter[Long]` or `meter.upDownCounter[Double]`
    *
    * @example
    *   {{{
    * val meter: Meter[F] = ???
    *
    * val doubleUpDownCounter: F[UpDownCounter[F, Double]] =
    *   meter.upDownCounter[Double]("double-up-down-counter").create
    *
    * val longUpDownCounter: F[UpDownCounter[F, Long]] =
    *   meter.upDownCounter[Long]("long-up-down-counter").create
    *   }}}
    *
    * @see
    *   See [[counter]] for monotonic alternative
    *
    * @param name
    *   the name of the instrument
    *
    * @tparam A
    *   the type of the measurement. [[scala.Long]] and [[scala.Double]] are
    *   supported out of the box
    */
  def upDownCounter[A: MeasurementValue](
      name: String
  ): UpDownCounter.Builder[F, A]

  /** Creates a builder of [[ObservableGauge]] instrument that collects values
    * of type `A` from the given callback.
    *
    * @note
    *   the `A` type must be provided explicitly, for example
    *   `meter.observableGauge[Long]` or `meter.observableGauge[Double]`
    *
    * @example
    *   {{{
    * val meter: Meter[F] = ???
    *
    * val doubleGauge: Resource[F, ObservableGauge] =
    *   meter
    *     .observableGauge[Double]("double-gauge")
    *     .create(Sync[F].delay(List(Measurement(1.0))))
    *
    * val longGauge: Resource[F, ObservableGauge] =
    *   meter
    *     .observableGauge[Long]("long-gauge")
    *     .create(Sync[F].delay(List(Measurement(1L))))
    *   }}}
    *
    * @param name
    *   the name of the instrument
    *
    * @tparam A
    *   the type of the measurement. [[scala.Long]] and [[scala.Double]] are
    *   supported out of the box
    */
  def observableGauge[A: MeasurementValue](
      name: String
  ): ObservableGauge.Builder[F, A]

  /** Creates a builder of [[ObservableCounter]] instrument that collects values
    * of type `A` from the given callback.
    *
    * The [[ObservableCounter]] is monotonic. This means the aggregated value is
    * nominally increasing.
    *
    * @note
    *   the `A` type must be provided explicitly, for example
    *   `meter.observableCounter[Long]` or `meter.observableCounter[Double]`
    *
    * @example
    *   {{{
    * val meter: Meter[F] = ???
    *
    * val doubleObservableCounter: Resource[F, ObservableCounter] =
    *   meter
    *     .observableCounter[Double]("double-counter")
    *     .create(Sync[F].delay(List(Measurement(1.0))))
    *
    * val longObservableCounter: Resource[F, ObservableCounter] =
    *   meter
    *     .observableCounter[Long]("long-counter")
    *     .create(Sync[F].delay(List(Measurement(1L))))
    *   }}}
    *
    * @see
    *   See [[observableUpDownCounter]] for non-monotonic alternative
    *
    * @param name
    *   the name of the instrument
    *
    * @tparam A
    *   the type of the measurement. [[scala.Long]] and [[scala.Double]] are
    *   supported out of the box
    */
  def observableCounter[A: MeasurementValue](
      name: String
  ): ObservableCounter.Builder[F, A]

  /** Creates a builder of [[ObservableUpDownCounter]] instrument that collects
    * [[scala.Long]] values from the given callback.
    *
    * The [[ObservableUpDownCounter]] is non-monotonic. This means the
    * aggregated value can increase and decrease.
    *
    * @see
    *   See [[observableCounter]] for monotonic alternative
    *
    * @param name
    *   the name of the instrument
    */
  def observableUpDownCounter(
      name: String
  ): ObservableInstrumentBuilder[F, Long, ObservableUpDownCounter]

}

object Meter {

  def apply[F[_]](implicit ev: Meter[F]): Meter[F] = ev

  /** Creates a no-op implementation of the [[Meter]].
    *
    * All meter instruments ([[Counter]], [[Histogram]], etc) have no-op
    * implementation too.
    *
    * @tparam F
    *   the higher-kinded type of a polymorphic effect
    */
  def noop[F[_]](implicit F: Applicative[F]): Meter[F] =
    new Meter[F] {
      def counter[A: MeasurementValue](
          name: String
      ): Counter.Builder[F, A] =
        new Counter.Builder[F, A] {
          def withUnit(unit: String): Counter.Builder[F, A] = this
          def withDescription(description: String): Counter.Builder[F, A] = this
          def create: F[Counter[F, A]] = F.pure(Counter.noop)
        }

      def histogram[A: MeasurementValue](
          name: String
      ): Histogram.Builder[F, A] =
        new Histogram.Builder[F, A] {
          def withUnit(unit: String): Histogram.Builder[F, A] = this
          def withDescription(description: String): Histogram.Builder[F, A] =
            this
          def withExplicitBucketBoundaries(
              boundaries: BucketBoundaries
          ): Histogram.Builder[F, A] = this
          def create: F[Histogram[F, A]] = F.pure(Histogram.noop)
        }

      def upDownCounter[A: MeasurementValue](
          name: String
      ): UpDownCounter.Builder[F, A] =
        new UpDownCounter.Builder[F, A] {
          def withUnit(unit: String): UpDownCounter.Builder[F, A] = this
          def withDescription(
              description: String
          ): UpDownCounter.Builder[F, A] = this
          def create: F[UpDownCounter[F, A]] = F.pure(UpDownCounter.noop)
        }

      def observableGauge[A: MeasurementValue](
          name: String
      ): ObservableGauge.Builder[F, A] =
        new ObservableGauge.Builder[F, A] {
          def withUnit(unit: String): ObservableGauge.Builder[F, A] = this
          def withDescription(
              description: String
          ): ObservableGauge.Builder[F, A] = this
          def createWithCallback(
              cb: ObservableMeasurement[F, A] => F[Unit]
          ): Resource[F, ObservableGauge] =
            Resource.pure(new ObservableGauge {})
          def create(
              measurements: F[Iterable[Measurement[A]]]
          ): Resource[F, ObservableGauge] =
            Resource.pure(new ObservableGauge {})
        }

      def observableCounter[A: MeasurementValue](
          name: String
      ): ObservableCounter.Builder[F, A] =
        new ObservableCounter.Builder[F, A] {
          def withUnit(unit: String): ObservableCounter.Builder[F, A] = this
          def withDescription(
              description: String
          ): ObservableCounter.Builder[F, A] = this
          def createWithCallback(
              cb: ObservableMeasurement[F, A] => F[Unit]
          ): Resource[F, ObservableCounter] =
            Resource.pure(new ObservableCounter {})
          def create(
              measurements: F[Iterable[Measurement[A]]]
          ): Resource[F, ObservableCounter] =
            Resource.pure(new ObservableCounter {})
        }

      def observableUpDownCounter(
          name: String
      ): ObservableInstrumentBuilder[F, Long, ObservableUpDownCounter] =
        new ObservableInstrumentBuilder[F, Long, ObservableUpDownCounter] {
          type Self = this.type

          def withUnit(unit: String): Self = this
          def withDescription(description: String): Self = this
          def create(
              measurements: F[List[Measurement[Long]]]
          ): Resource[F, ObservableUpDownCounter] =
            Resource.pure(new ObservableUpDownCounter {})
          def createWithCallback(
              cb: ObservableMeasurement[F, Long] => F[Unit]
          ): Resource[F, ObservableUpDownCounter] =
            Resource.pure(new ObservableUpDownCounter {})
        }
    }

  object Implicits {
    implicit def noop[F[_]: Applicative]: Meter[F] = Meter.noop
  }
}
