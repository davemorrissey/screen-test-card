import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.geom._
import java.awt._
import java.io.File

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

  val imW = 720 // Preferably divisible by 30
  val imH = 580 // About 0.75-0.8 of height
  val cX = imW/2
  val cY = imH/2
  val grid = imW/30
  val gridOffset = (imH - (19 * grid))/2

  val canvas = new BufferedImage(imW, imH, BufferedImage.TYPE_INT_RGB)
  val g = canvas.createGraphics()
  g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
  g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

  // Clear background to dark grey
  g.setColor(Color.decode("#424242"))
  g.fillRect(0, 0, imW, imH)

  // Draw test patterns
  drawHeader()
  drawFooter()
  drawGrid()
  drawCentralCircles()
  drawMaterialPalette()
  drawMaterialPaletteGraphics()
  drawGreyShapes()
  drawLetters()

  g.dispose()

  ImageIO.write(canvas, "png", new java.io.File("testcard.png"))

  /**
    * Draws a grid of dotted lines over the grey background, in the central portion of the image.
    */
  def drawGrid() {
    g.setColor(Color.decode("#9E9E9E"))
    g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, Array(1.0f, 4.0f), 0.5f))
    for (x <- 11 to 19) {
      g.draw(new Line2D.Double((x * grid) + 0.5d, gridOffset + grid, (x * grid) + 0.5d, imH - gridOffset - grid))
    }
    for (y <- 2 to 17) {
      g.draw(new Line2D.Double(0, gridOffset + (y * grid) + 0.5d, imW, gridOffset + (y * grid) + 0.5d))
    }
  }

  /**
    * Draws the SSIV icon centered in the header area.
    */
  def drawHeader() {
    val image = ImageIO.read(new File("icon.png"))
    val radius = (gridOffset/2.5).toInt
    g.drawImage(image, cX - radius, (gridOffset/2) - radius, 2 * radius, 2 * radius, Color.decode("#424242"), null)
  }

  /**
    * Draws github link and image dimensions in the footer area.
    */
  def drawFooter() {
    val link = "github.com/davemorrissey/screen-test-card"
    val size = imW + " x " + imH
    g.setColor(Color.decode("#9E9E9E"))
    g.setFont(new Font("Roboto Slab", Font.PLAIN, Math.max(8, (imW * 0.0075).toInt)))

    val linkWidth = g.getFontMetrics.getStringBounds(link, canvas.getGraphics).getWidth
    val sizeWidth = g.getFontMetrics.getStringBounds(size, canvas.getGraphics).getWidth
    val ascent = g.getFontMetrics.getLineMetrics(link, canvas.getGraphics).getAscent

    g.drawString(link, (cX - (linkWidth/2f)).toFloat, imH - (gridOffset/2f) - (ascent * 0.6).toInt)
    g.drawString(size, (cX - (sizeWidth/2f)).toFloat, imH - (gridOffset/2f) + (ascent * 0.9).toInt)
  }

  /**
    * Renders a symmetrical palette grid at left and right of the image
    */
  def drawMaterialPalette() {

    // For each color in the palette...
    for ((color, ci) <- palette.zipWithIndex) {

      // At left and right, fill the whole column with a middle shade of the color
      g.setColor(Color.decode(color(1)))
      g.fillRect(ci * grid, 0, grid, imH) // Left
      g.fillRect(imW - (ci * grid) - grid, 0, grid, imH) // Right

      // For each shade of the color...
      for ((shade, si) <- color.zipWithIndex) {
        val rowY = gridOffset + (si * grid)
        val colX = ci * grid

        // Draw a tall block of the shade. Later shades overlay the middle.
        g.setColor(Color.decode(shade))
        g.fillRect(colX, rowY, grid, imH - (2 * rowY)) // Left
        g.fillRect(imW - colX - grid, rowY, grid, imH - (2 * rowY)) // Right

        // For top left and bottom right, turn half the shade square into a gradient between this color and the next
        if (si < color.length - 1) {
          val paint = g.getPaint
          // Top left
          g.setPaint(new GradientPaint(colX, rowY, Color.decode(shade), colX, rowY + grid, Color.decode(color(si + 1))))
          g.fillRect(colX, rowY, grid, grid)
          // Bottom right
          g.setPaint(new GradientPaint(imW - colX, imH - rowY, Color.decode(shade), imW - colX, imH - rowY - grid, Color.decode(color(si + 1))))
          g.fillRect(imW - colX - grid, imH - rowY - grid, grid, grid)

          g.setPaint(paint)
        }
      }
    }
  }

  /**
    * Renders a variety of test patterns on top of the material palette grid
    */
  def drawMaterialPaletteGraphics() {

    for ((color, ci) <- palette.zipWithIndex) {

      val rad = (grid * 0.3).toInt
      val defaultStroke = Math.max(1, grid * (0.05f * (ci+1)/palette.length))
      for ((shade, si) <- color.zipWithIndex) {
        g.setColor(Color.WHITE)

        val rowY = gridOffset + (si * grid)
        val colX = ci * grid

        val leftCX = colX + (grid/2d)
        val rightCX = imW - colX - (grid/2)
        val topCY = rowY + (grid/2d)
        val bottomCY = imH - rowY - (grid/2)

        if (si > 0) {
          if (ci < palette.length - 1 && si < palette.length - 1 && ci == si) {

            // Diagonals
            drawCircles(leftCX, topCY, rad, defaultStroke)
            drawPlusses(leftCX, bottomCY, rad, defaultStroke)
            drawCrosses(rightCX, topCY, rad, defaultStroke)
            drawSquares(rightCX, bottomCY, rad, defaultStroke)

          } else if (ci == palette.length - 1 && si < palette.length - 1) {

            // Decreasing size shapes in last (middle) color column
            val r = grid * 0.3 * ((si + 2f)/color.length)
            val s = grid * (0.05f * si/color.length)
            drawCircles(leftCX, topCY, r, s)
            drawPlusses(leftCX, bottomCY, r, s)
            drawCrosses(rightCX, topCY, r, s)
            drawSquares(rightCX, bottomCY, r, s)

          } else if (si == palette.length - 1) {

            // Coloured shapes on lightest shade middle row
            val darkShade = Color.decode(color(4))
            drawCircles(leftCX, cY, rad, defaultStroke, darkShade)
            drawPlusses(leftCX, cY, rad, defaultStroke, darkShade)
            drawCrosses(rightCX, cY, rad, defaultStroke, darkShade, inside = true)
            drawSquares(rightCX, cY, rad, defaultStroke, darkShade)

          }
        } else {

          // Concentric shapes in darkest shade at top/bottom
          drawCircles(leftCX, topCY, rad, defaultStroke, nested = true)
          drawPlusses(leftCX, bottomCY, rad, defaultStroke, Color.WHITE, nested = true)
          drawCrosses(rightCX, topCY, rad, defaultStroke, Color.WHITE, inside = false, nested = true)
          drawSquares(rightCX, bottomCY, rad, defaultStroke, nested = true)

        }
      }
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
    * @param rad radius of the shape
    * @param stroke pen stroke width
    * @param color shape line color
    * @param nested whether to draw nested shapes
    */
  def drawPlusses(scX: Double, scY: Double, rad: Double, stroke: Float, color: Color = Color.WHITE, nested: Boolean = false) {
    var radOffset = 0d
    val r = if (nested) 1.4d * rad else rad
    val background = new Color(canvas.getRGB(scX.toInt, scY.toInt))
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
      radOffset = radOffset + (stroke * 2)
    } while (radOffset < r && nested)
    if (nested) {
      val overlayStroke = grid * 0.15
      g.setColor(background)
      g.setStroke(new BasicStroke(overlayStroke.toFloat))
      g.draw(new Rectangle2D.Double(scX - (grid * 0.375), scY - (grid * 0.375), grid * 0.75, grid * 0.75))
    }
  }

  /**
    * Draw crosses centered on a point. Either a single cross or a nested cross pattern.
    * @param scX center X coord
    * @param scY center Y coord
    * @param rad radius of the shape
    * @param stroke pen stroke width
    * @param color shape line color
    * @param inside if this cross is being drawn inside a square
    * @param nested whether to draw nested shapes
    */
  def drawCrosses(scX: Double, scY: Double, rad: Double, stroke: Float, color: Color = Color.WHITE, inside: Boolean = false, nested: Boolean = false) {
    var radOffset = 0d
    val r = if (nested) 1.3d * rad else rad
    val background = new Color(canvas.getRGB(scX.toInt, scY.toInt))
    do {
      g.setColor(color)
      g.setStroke(new BasicStroke(stroke, if (inside) BasicStroke.CAP_ROUND else BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f))
      if (radOffset == 0d && !inside) {
        dLine(scX - r, scY - r, scX + r, scY + r)
        dLine(scX - r, scY + r, scX + r, scY - r)
      } else if (radOffset == 0d) {
        dLine(scX - r + (stroke/2), scY - r + (stroke/2), scX + r - (stroke/2), scY + r - (stroke/2))
        dLine(scX - r + (stroke/2), scY + r - (stroke/2), scX + r - (stroke/2), scY - r + (stroke/2))
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
      radOffset = radOffset + (stroke * 3)
    } while (radOffset < r && nested)
    val overlayStroke = rad * 0.5
    g.setColor(background)
    g.setStroke(new BasicStroke(overlayStroke.toFloat))
    g.draw(new Rectangle2D.Double(scX - (rad * 1.25), scY - (rad * 1.25), rad * 2.5, rad * 2.5))
  }

  /**
    * Draws letters and numbers of varying size in the top and bottom of each material
    * palette column.
    */
  def drawLetters() {
    val minSize = 10
    val maxSize = (imH * 0.04).toInt
    val increment = (maxSize - minSize)/(palette.length - 1)
    val stringTop = "ABCDEFGHIJ"
    val stringBottom = "0123456789"

    val tCY = gridOffset * 0.5f
    var tCX = grid/2f
    for ((color, ci) <- palette.zipWithIndex) {
      g.setColor(Color.WHITE)
      g.setFont(new Font("Roboto Slab", Font.PLAIN, minSize + ((palette.length - ci - 1) * increment)))

      val letterL = stringTop.substring(ci, ci + 1)
      val widthL = g.getFontMetrics.getStringBounds(letterL, canvas.getGraphics).getWidth
      val ascentL = g.getFontMetrics.getLineMetrics(letterL, canvas.getGraphics).getAscent
      g.drawString(letterL, (tCX - (widthL / 2)).toFloat, (tCY + (ascentL * 0.35)).toFloat)

      val letterR = stringTop.reverse.substring(ci, ci + 1)
      val widthR = g.getFontMetrics.getStringBounds(letterR, canvas.getGraphics).getWidth
      val ascentR = g.getFontMetrics.getLineMetrics(letterR, canvas.getGraphics).getAscent
      g.drawString(letterR, (imW - tCX - (widthR / 2)).toFloat, (tCY + (ascentR * 0.35)).toFloat)

      val letterBL = stringBottom.substring(ci, ci + 1)
      val widthBL = g.getFontMetrics.getStringBounds(letterBL, canvas.getGraphics).getWidth
      val ascentBL = g.getFontMetrics.getLineMetrics(letterBL, canvas.getGraphics).getAscent
      g.drawString(letterBL, (tCX - (widthBL / 2)).toFloat, (imH - tCY + (ascentBL * 0.35)).toFloat)

      val letterBR = stringBottom.reverse.substring(ci, ci + 1)
      val widthBR = g.getFontMetrics.getStringBounds(letterBR, canvas.getGraphics).getWidth
      val ascentBR = g.getFontMetrics.getLineMetrics(letterBR, canvas.getGraphics).getAscent
      g.drawString(letterBR, (imW - tCX - (widthBR / 2)).toFloat, (imH - tCY + (ascentBR * 0.35)).toFloat)

      tCX += grid
    }
  }

  /**
    * Draw concentric circles starting from the center, with stroke thickness increasing until
    * the radius is 10% of the image width, then decreasing thickness after. Then overdraw two
    * quadrants of the circle with radial lines.
    */
  def drawCentralCircles() {
    // Perform a dry run to calculate the final radius
    var stroke = 0.5f
    var radius = 2f
    var multiplier = 1.2f
    while ((radius < cX || radius < cY) && stroke >= 0.5f) {
      if (radius > imW * 0.1) {
        radius += (1 + (1/multiplier)) * stroke
        stroke /= multiplier
      } else {
        radius += (1 + multiplier) * stroke
        stroke *= multiplier
      }
    }
    val outerRadius = (radius * 1.05).toInt

    // Draw a white circle that provides a background and outline for the concentric circles
    g.setColor(Color.WHITE)
    g.fillOval(cX - outerRadius, cY - outerRadius, outerRadius * 2, outerRadius * 2)

    // Draw the concentric circles
    stroke = 0.5f
    radius = 2f
    multiplier = 1.2f
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
    g.fillArc(cX - outerRadius, cY - outerRadius, outerRadius * 2, outerRadius * 2, 180, 90)
    g.fillArc(cX - outerRadius, cY - outerRadius, outerRadius * 2, outerRadius * 2, 0, 90)

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
    * Draw the first row of the grid top and bottom with shapes of varying stroke width,
    * and add hairline borders.
    */
  def drawGreyShapes() {
    g.setColor(Color.decode("#313131"))
    g.fillRect(grid * 10, gridOffset, grid * 10, grid)
    g.fillRect(grid * 10, imH - gridOffset - grid, grid * 10, grid)

    val rad = (grid * 0.3).toInt
    for (i <- 0 to 4) {
      val stroke = Math.max(1, grid * (0.05f - (i * 0.01f)))
      drawCircles(grid * (10.5d + i), gridOffset + (grid * 0.5d), rad, stroke)
      drawCrosses(imW - (grid * (10.5d + i)), gridOffset + (grid * 0.5d), rad, stroke)
      drawPlusses(grid * (10.5d + i), imH - gridOffset - (grid * 0.5d), rad, stroke)
      drawSquares(imW - (grid * (10.5d + i)), imH - gridOffset - (grid * 0.5d), rad, stroke)
    }

    val lineW = 1
    g.setColor(Color.WHITE)
    g.fillRect(0, gridOffset, imW, lineW)
    g.fillRect(grid * 10, gridOffset + grid - lineW, grid * 10, lineW)
    g.fillRect(grid * 10, imH - gridOffset - grid, grid * 10, lineW)
    g.fillRect(0, imH - gridOffset - lineW, imW, lineW)
  }

  def dLine(x1: Double, y1: Double, x2: Double, y2: Double) {
    g.draw(new Line2D.Double(x1, y1, x2, y2))
  }

}
