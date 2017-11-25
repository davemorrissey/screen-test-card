import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.geom._
import java.awt._

object TestCard extends App {

  /** Material color palette. Each element is an array of shades. */
  val palette = Array(
    Array("#FAFAFA", "#F5F5F5", "#EEEEEE", "#E0E0E0", "#BDBDBD", "#9E9E9E", "#757575", "#616161", "#424242", "#212121").reverse,
    Array("#ECEFF1", "#CFD8DC", "#B0BEC5", "#90A4AE", "#78909C", "#607D8B", "#546E7A", "#455A64", "#37474F", "#263238").reverse,
    Array("#E8EAF6", "#C5CAE9", "#9FA8DA", "#7986CB", "#5C6BC0", "#3F51B5", "#3949AB", "#303F9F", "#283593", "#1A237E").reverse,
    Array("#E1F5FE", "#B3E5FC", "#81D4FA", "#4FC3F7", "#29B6F6", "#03A9F4", "#039BE5", "#0288D1", "#0277BD", "#01579B").reverse,
    Array("#E0F2F1", "#B2DFDB", "#80CBC4", "#4DB6AC", "#26A69A", "#009688", "#00897B", "#00796B", "#00695C", "#004D40").reverse,
    Array("#F1F8E9", "#DCEDC8", "#C5E1A5", "#AED581", "#9CCC65", "#8BC34A", "#7CB342", "#689F38", "#558B2F", "#33691E").reverse,
    Array("#FFFDE7", "#FFF9C4", "#FFF59D", "#FFF176", "#FFEE58", "#FFEB3B", "#FDD835", "#FBC02D", "#F9A825", "#F57F17").reverse,
    Array("#FFF3E0", "#FFE0B2", "#FFCC80", "#FFB74D", "#FFA726", "#FF9800", "#FB8C00", "#F57C00", "#EF6C00", "#E65100").reverse,
    Array("#FCE4EC", "#F8BBD0", "#F48FB1", "#F06292", "#EC407A", "#E91E63", "#D81B60", "#C2185B", "#AD1457", "#880E4F").reverse,
    Array("#F3E5F5", "#E1BEE7", "#CE93D8", "#BA68C8", "#AB47BC", "#9C27B0", "#8E24AA", "#7B1FA2", "#6A1B9A", "#4A148C").reverse
  )

  val imW = 600
  val imH = 450
  val cX = imW/2
  val cY = imH/2
  val paletteW = (imW * 0.35).toInt
  val paletteColW = paletteW/palette.length
  val centerW = imW - (2 * paletteW)

  val canvas = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB)
  val g = canvas.createGraphics()
  g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
  g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

  // Clear background
  g.setColor(Color.WHITE)
  g.fillRect(0, 0, imW, imH)

  // Draw test patterns
  drawCentralCircles()
  drawMaterialPalette()
  drawMaterialPaletteGraphics()
  drawBlackWhiteGradients()

  g.dispose()

  ImageIO.write(canvas, "png", new java.io.File("testcard.png"))

  /**
    * Renders a symmetrical palette grid at left and right of the image
    */
  def drawMaterialPalette() {

    // For each color in the palette...
    var colOffset = 0
    for (color <- palette) {

      // At left and right, fill the whole column with a middle shade of the color
      g.setColor(Color.decode(color(4)))
      g.fillRect(colOffset, 0, paletteColW, imH) // Left
      g.fillRect(imW - colOffset - paletteColW, 0, paletteColW, imH) // Right

      // For each shade of the color...
      var rowOffset = (imH * 0.1).toInt
      val rowH = (imH - (2 * rowOffset)) / 19
      for ((shade, si) <- color.zipWithIndex) {

        // Draw a tall block of the shade. Later shades overlay the middle.
        g.setColor(Color.decode(shade))
        g.fillRect(colOffset, rowOffset, paletteColW, imH - (2 * rowOffset)) // Left
        g.fillRect(imW - colOffset - paletteColW, rowOffset, paletteColW, imH - (2 * rowOffset)) // Right

        // For top left and bottom right, turn half the shade square into a gradient between this color and the next
        if (si < color.length - 1) {
          val paint = g.getPaint
          // Top left
          g.setPaint(new GradientPaint(colOffset, rowOffset, Color.decode(shade), colOffset, rowOffset + rowH, Color.decode(color(si + 1))))
          g.fillRect(colOffset, rowOffset, paletteColW, rowH)
          // Bottom right
          g.setPaint(new GradientPaint(imW - colOffset, imH - rowOffset, Color.decode(shade), imW - colOffset, imH - rowOffset - rowH, Color.decode(color(si + 1))))
          g.fillRect(imW - colOffset - paletteColW, imH - rowOffset - rowH, paletteColW, rowH)

          g.setPaint(paint)
        }
        rowOffset += rowH
      }
      colOffset += paletteColW
    }
  }

  /**
    * Renders a variety of test patterns on top of the material palette grid
    */
  def drawMaterialPaletteGraphics() {

    var colOffset = 0
    for ((color, ci) <- palette.zipWithIndex) {

      var rowOffset = (imH * 0.1).toInt
      var rowH = (imH - (2 * rowOffset)) / 19
      val rad = (Math.min(paletteColW, rowH) * 0.3).toInt
      val defaultStroke = Math.max(1, rowH * (0.05f * (ci+1)/palette.length))
      for ((shade, si) <- color.zipWithIndex) {
        g.setColor(Color.WHITE)

        val leftCX = colOffset + (paletteColW/2d)
        val rightCX = imW - colOffset - (paletteColW/2)
        val topCY = rowOffset + (rowH/2d)
        val bottomCY = imH - rowOffset - (rowH/2)

        if (si > 0) {
          if (ci < palette.length - 1 && si < palette.length - 1 && ci == si) {

            // Diagonals
            drawCircles(leftCX, topCY, rad, defaultStroke)
            drawPlusses(leftCX, bottomCY, rowH, rad, defaultStroke)
            drawCrosses(rightCX, topCY, rowH, rad, defaultStroke)
            drawSquares(rightCX, bottomCY, rad, defaultStroke)

          } else if (ci == palette.length - 1 && si < palette.length - 1) {

            // Decreasing size shapes in last (middle) color column
            val r = Math.min(paletteColW, rowH) * 0.3 * ((si + 2f)/color.length)
            val s = rowH * (0.05f * si/color.length)
            drawCircles(leftCX, topCY, r, s)
            drawPlusses(leftCX, bottomCY, rowH, r, s)
            drawCrosses(rightCX, topCY, rowH, r, s)
            drawSquares(rightCX, bottomCY, r, s)

          } else if (si == palette.length - 1) {

            // Coloured shapes on lightest shade middle row
            val darkShade = Color.decode(color(4))
            drawCircles(leftCX, cY, rad, defaultStroke, darkShade)
            drawPlusses(leftCX, cY, rowH, rad, defaultStroke, darkShade)
            drawCrosses(rightCX, cY, rowH, rad, defaultStroke, darkShade)
            drawSquares(rightCX, cY, rad, defaultStroke, darkShade)

          }
        } else {

          // Concentric shapes in darkest shade at top/bottom
          drawCircles(leftCX, topCY, rad, defaultStroke, nested = true)
          drawPlusses(leftCX, bottomCY, rowH, rad, defaultStroke, Color.WHITE, Color.decode(shade), nested = true)
          drawCrosses(rightCX, topCY, rowH, rad, defaultStroke, Color.WHITE, Color.decode(shade), nested = true)
          drawSquares(rightCX, bottomCY, rad, defaultStroke, nested = true)

        }

        rowOffset += rowH
      }
      colOffset += paletteColW
    }
  }

  /**
    * Draw circles centered on a point. Either a single circle or a concentric pattern.
    * @param scX center X coord
    * @param scY center Y coord
    * @param rad radius of the shape
    * @param stroke pen stroke width
    * @param color shape line color
    * @param nested whether to draw nested (concentric) circles
    */
  def drawCircles(scX: Double, scY: Double, rad: Double, stroke: Float, color: Color = Color.WHITE, nested: Boolean = false) {
    g.setStroke(new BasicStroke(stroke))
    g.setColor(color)
    var radOffset = 0d
    do {
      g.draw(new Ellipse2D.Double(scX - rad + radOffset, scY - rad + radOffset, (rad - radOffset) * 2, (rad - radOffset) * 2))
      radOffset = radOffset + (stroke * 2)
    } while (radOffset < rad && nested)
  }

  /**
    * Draw squares centered on a point. Either a single square or a concentric square pattern.
    * @param scX center X coord
    * @param scY center Y coord
    * @param rad radius of the shape
    * @param stroke pen stroke width
    * @param color shape line color
    * @param nested whether to draw nested (concentric) squares
    */
  def drawSquares(scX: Double, scY: Double, rad: Double, stroke: Float, color: Color = Color.WHITE, nested: Boolean = false) {
    g.setStroke(new BasicStroke(stroke))
    g.setColor(color)
    var radOffset = 0d
    do {
      g.draw(new Rectangle2D.Double(scX - rad + radOffset, scY - rad + radOffset, (rad - radOffset) * 2, (rad - radOffset) * 2))
      radOffset = radOffset + (stroke * 2)
    } while (radOffset < rad && nested)
  }

  /**
    * Draw plusses centered on a point. Either a single plus or a nested cross pattern.
    * @param scX center X coord
    * @param scY center Y coord
    * @param rowH height of the palette row
    * @param rad radius of the shape
    * @param stroke pen stroke width
    * @param color shape line color
    * @param background background color, used for cropping back nested plusses overdrawn for completeness
    * @param nested whether to draw nested shapes
    */
  def drawPlusses(scX: Double, scY: Double, rowH: Double, rad: Double, stroke: Float, color: Color = Color.WHITE, background: Color = Color.MAGENTA, nested: Boolean = false) {
    var radOffset = 0d
    val r = if (nested) 1.4d * rad else rad
    do {
      g.setColor(color)
      g.setStroke(new BasicStroke(stroke))
      if (radOffset == 0d) {
        dLine(scX, scY - r, scX, scY + r)
        dLine(scX - r, scY, scX + r, scY)
      } else {
        dLine(scX - radOffset, scY - r, scX - radOffset, scY - radOffset)
        dLine(scX - radOffset, scY - radOffset, scX - r, scY - radOffset)
        dLine(scX + radOffset, scY - r, scX + radOffset, scY - radOffset)
        dLine(scX + radOffset, scY - radOffset, scX + r, scY - radOffset)
        dLine(scX - r, scY + radOffset, scX - radOffset, scY + radOffset)
        dLine(scX - radOffset, scY + radOffset, scX - radOffset, scY + r)
        dLine(scX + r, scY + radOffset, scX + radOffset, scY + radOffset)
        dLine(scX + radOffset, scY + radOffset, scX + radOffset, scY + r)
      }
      if (nested) {
        val bound = Math.min(paletteColW, rowH)
        val overlayStroke = bound * 0.15
        g.setColor(background)
        g.setStroke(new BasicStroke(overlayStroke.toFloat))
        g.draw(new Rectangle2D.Double(scX - (bound * 0.375), scY - (bound * 0.375), bound * 0.75, bound * 0.75))
      }
      radOffset = radOffset + (stroke * 2)
    } while (radOffset < r && nested)
  }

  /**
    * Draw crosses centered on a point. Either a single cross or a nested cross pattern.
    * @param scX center X coord
    * @param scY center Y coord
    * @param rowH height of the palette row
    * @param rad radius of the shape
    * @param stroke pen stroke width
    * @param color shape line color
    * @param background background color, used for cropping back nested crosses overdrawn for completeness
    * @param nested whether to draw nested shapes
    */
  def drawCrosses(scX: Double, scY: Double, rowH: Double, rad: Double, stroke: Float, color: Color = Color.WHITE, background: Color = Color.MAGENTA, nested: Boolean = false) {
    var radOffset = 0d
    val r = if (nested) 1.3d * rad else rad
    do {
      g.setColor(color)
      g.setStroke(new BasicStroke(stroke))
      if (radOffset == 0d) {
        dLine(scX - r, scY - r, scX + r, scY + r)
        dLine(scX - r, scY + r, scX + r, scY - r)
      } else {
        dLine(scX - r + radOffset, scY - r, scX, scY - radOffset)
        dLine(scX, scY - radOffset, scX + r - radOffset, scY - r)
        dLine(scX - r, scY - r + radOffset, scX - radOffset, scY)
        dLine(scX - radOffset, scY, scX - r, scY + r - radOffset)
        dLine(scX + r, scY - r + radOffset, scX + radOffset, scY)
        dLine(scX + radOffset, scY, scX + r, scY + r - radOffset)
        dLine(scX - r + radOffset, scY + r, scX, scY + radOffset)
        dLine(scX, scY + radOffset, scX + r - radOffset, scY + r)
      }
      if (nested) {
        val bound = Math.min(paletteColW, rowH)
        val overlayStroke = bound * 0.15
        g.setColor(background)
        g.setStroke(new BasicStroke(overlayStroke.toFloat))
        g.draw(new Rectangle2D.Double(scX - (bound * 0.375), scY - (bound * 0.375), bound * 0.75, bound * 0.75))
      }
      radOffset = radOffset + (stroke * 3)
    } while (radOffset < r && nested)
  }

  /**
    * Draw concentric circles starting from the center, with stroke thickness increasing until
    * the radius is 10% of the image width, then decreasing thickness after. Then overdraw two
    * quadrants of the circle with radial lines.
    */
  def drawCentralCircles() {
    var stroke = 0.5f
    var radius = 2f
    var multiplier = 1.2f
    g.setColor(Color.BLACK)
    while ((radius < cX || radius < cY) && stroke >= 0.5f) {
      g.setStroke(new BasicStroke(stroke))
      g.draw(new Ellipse2D.Double(cX - radius, cY - radius, radius * 2, radius * 2))
      if (radius > imW * 0.1) {
        radius += (1 + (1/multiplier)) * stroke
        stroke /= multiplier
      } else {
        radius += (1 + multiplier) * stroke
        stroke *= multiplier
      }
    }

    // Black out the top right and bottom left
    g.setColor(Color.BLACK)
    g.fillRect(cX, 0, cX, cY)
    g.fillRect(0, cY, cX, cY)

    // Draw outer radial lines with 1 degree intervals
    g.setColor(Color.WHITE)
    for (i <- 180 to 268 by 2) {
      g.fillArc(cX - radius.toInt, cY - radius.toInt, radius.toInt * 2, radius.toInt * 2, i, 1)
    }
    for (i <- 0 to 88 by 2) {
      g.fillArc(cX - radius.toInt, cY - radius.toInt, radius.toInt * 2, radius.toInt * 2, i, 1)
    }

    // Overlay the central part of the two radial segments
    g.setColor(Color.BLACK)
    g.fillArc(cX - (radius.toInt/2), cY - (radius.toInt/2), radius.toInt, radius.toInt, 180, 90)
    g.fillArc(cX - (radius.toInt/2), cY - (radius.toInt/2), radius.toInt, radius.toInt, 0, 90)

    // Draw inner radial lines with 2 degree intervals
    g.setColor(Color.WHITE)
    for (i <- 180 to 268 by 4) {
      g.fillArc(cX - (radius.toInt/2), cY - (radius.toInt/2), radius.toInt, radius.toInt, i, 2)
    }
    for (i <- 0 to 88 by 4) {
      g.fillArc(cX - (radius.toInt/2), cY - (radius.toInt/2), radius.toInt, radius.toInt, i, 2)
    }

  }

  /**
    * Black to white gradients at top and bottom.
    */
  def drawBlackWhiteGradients() {
    val rowOffset = (imH * 0.1).toInt
    val rowH = (imH - (2 * rowOffset)) / 19
    val paint = g.getPaint
    g.setPaint(new GradientPaint(cX - (centerW/2), rowOffset, Color.BLACK, cX + (centerW/2), rowOffset, Color.WHITE))
    g.fillRect(cX - (centerW/2), rowOffset, centerW, rowH)
    g.fillRect(cX - (centerW/2), imH - rowOffset - rowH, centerW, rowH)
    g.setPaint(new GradientPaint(cX - (centerW/2), rowOffset, Color.WHITE, cX + (centerW/2), rowOffset, Color.BLACK))
    g.fillRect(cX - (centerW/2), rowOffset + (rowH/2), centerW, rowH - (rowH/2))
    g.fillRect(cX - (centerW/2), imH - rowOffset - (rowH/2), centerW, rowH - (rowH/2))
    g.setPaint(paint)
  }

  def dLine(x1: Double, y1: Double, x2: Double, y2: Double) {
    g.draw(new Line2D.Double(x1, y1, x2, y2))
  }

}
