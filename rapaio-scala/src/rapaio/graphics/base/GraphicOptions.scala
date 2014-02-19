/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package rapaio.graphics.base

import rapaio.data._
import java.awt.Color

/**
 *
 * @author Aurelian Tutuianu <padreati@yahoo.com>
 */
class GraphicOptions {
  var col: ColorOption = GraphicOptions.DefaultColor
  var lwd: LwdOption = GraphicOptions.DefaultLwd
  var pch: PchOption = GraphicOptions.DefaultPch
  var sz: SizeOption = GraphicOptions.DefaultSz
  var xLim: (Double, Double) = GraphicOptions.DefaultXLim
  var yLim: (Double, Double) = GraphicOptions.DefaultYLim
}

/**
 * Provides default values for graphical options.
 */
object GraphicOptions {
  val DefaultColor: ColorOption = Color.BLACK
  val DefaultLwd: LwdOption = 1.2
  val DefaultPch: PchOption = 'o'
  val DefaultSz: SizeOption = 2.7
  val DefaultXLim: (Double, Double) = (Double.NaN, Double.NaN)
  val DefaultYLim: (Double, Double) = (Double.NaN, Double.NaN)
}

/**
 * Graphical option for colors.
 *
 * @param values an array of [[Color]] values
 */
class ColorOption(val values: Array[Color]) {

  def apply(i: Int): Color = {
    require(i >= 0, "color index must be greater than 0")
    if (i >= values.length) values(i % values.length)
    else values(i)
  }


  def canEqual(other: Any): Boolean = other.isInstanceOf[ColorOption]

  override def equals(other: Any): Boolean = other match {
    case that: ColorOption =>
      (that canEqual this) &&
        values.sameElements(that.values)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(values)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object ColorOption {

  implicit def fromOneColor(c: Color): ColorOption = {
    new ColorOption(Array[Color](c))
  }

  implicit def fromOneInt(i: Int): ColorOption = {
    new ColorOption(Array[Color](StandardColorPalette.color(i)))
  }

  implicit def fromFeature(feat: Feature): ColorOption = {
    val values = new Array[Color](feat.rowCount)
    for (i <- 0 until feat.rowCount) {
      values(i) = StandardColorPalette.color(if (feat.missing(i)) 0; else feat.indexes(i))
    }
    new ColorOption(values)
  }
}

class LwdOption(val values: Array[Float]) {

  def apply(i: Int): Float = if (i >= values.length) values(i % values.length) else values(i)


  def canEqual(other: Any): Boolean = other.isInstanceOf[LwdOption]

  override def equals(other: Any): Boolean = other match {
    case that: LwdOption =>
      (that canEqual this) &&
        values.sameElements(that.values)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(values)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

/**
 * Provides implicit conversions for line width graphical option.
 */
object SizeOption {

  implicit def fromOneInt(value: Int): SizeOption = new SizeOption(Array[Double](value.toDouble))

  implicit def fromOneDouble(value: Double): SizeOption = new SizeOption(Array[Double](value.toDouble))
}

class SizeOption(val values: Array[Double]) {

  def apply(i: Int): Double = if (i >= values.length) values(i % values.length) else values(i)

  def canEqual(other: Any): Boolean = other.isInstanceOf[SizeOption]

  override def equals(other: Any): Boolean = other match {
    case that: SizeOption =>
      (that canEqual this) &&
        values.sameElements(that.values)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(values)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

/**
 * Provides implicit conversions for line width graphical option.
 */
object LwdOption {

  implicit def fromOneInt(value: Int): LwdOption = new LwdOption(Array[Float](value.toFloat))

  implicit def fromOneDouble(value: Double): LwdOption = new LwdOption(Array[Float](value.toFloat))
}

class PchOption(val index: Array[Int]) {

  def apply(i: Int): Int = {
    if (i >= index.length) apply(i % index.length)
    else index(i)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[PchOption]

  override def equals(other: Any): Boolean = other match {
    case that: PchOption =>
      (that canEqual this) && index.sameElements(that.index)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(index)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object PchOption {

  implicit def fromOneString(xs: Char): PchOption = {
    new PchOption(Array[Int](PchPalette(xs)))
  }

  implicit def fromOneInt(xs: Int): PchOption = new PchOption(Array[Int](xs))

  implicit def fromIndex(index: Index): PchOption = {
    val pch = new Array[Int](index.rowCount)
    for (i <- 0 until index.rowCount) pch(i) = index.indexes(i)
    new PchOption(pch)
  }
}
