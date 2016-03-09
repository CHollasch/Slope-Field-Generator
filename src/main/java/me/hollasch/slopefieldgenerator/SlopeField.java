package me.hollasch.slopefieldgenerator;

import java.awt.*;
import java.math.BigDecimal;
import java.util.EmptyStackException;

/**
 * @author Connor Hollasch
 * @since 3/8/2016
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class SlopeField {

      private Expression expression;

      public SlopeField(String equation) {
            setExpression(equation);
      }

      public boolean setExpression(String equation) {
            Expression old = expression;

            try {
                  expression = new Expression(equation);
                  expression.eval();

                  return true;
            } catch (NumberFormatException | EmptyStackException | Expression.ExpressionException e) {
                  if (old != null) {
                        expression = old;
                  }
            } catch (Exception e) { return true; }

            return false;
      }

      public void paint(Graphics g, int canvasWidth, int canvasHeight) {
            g.setColor(SlopeFieldMain.backgroundColor);
            g.fillRect(0, 0, canvasWidth, canvasHeight);
            g.setColor(SlopeFieldMain.tickColor);

            int i = SlopeFieldMain.slopeIntervals;

            for (int x = 0, screenX = 0; screenX <= canvasWidth; ++x, screenX += i) {
                  for (int y = 0, screenY = 0; screenY <= canvasHeight; ++y, screenY += i) {
                        if (expression != null) {
                              int lineSize = (int) (i / 2.5);

                              try {
                                    BigDecimal val = expression
                                            .with("x", String.valueOf(x - (canvasWidth / i / 2)))
                                            .with("y", String.valueOf((canvasHeight / i / 2) - y)).eval();

                                    double real = val.doubleValue();

                                    int yOffset = real >= 0 ? (int) Math.min(real * lineSize, lineSize) : (int) (Math.min(-real * lineSize, lineSize));
                                    int xOffset = real >= 0 ? (int) Math.min((1 / real) * lineSize, lineSize) : (int) -Math.min(Math.abs(1 / real) * lineSize, lineSize);

                                    if (SlopeFieldMain.isUsingHeatmap) {
                                          if (Math.abs(real) > 5) {
                                                g.setColor(Color.red);
                                          } else {
                                                // Logistic based heatmap coloring
                                                double ratio = Math.abs(yOffset / (double) xOffset);
                                                float color;

                                                if (ratio >= 1) {
                                                      color = (float) ((3 / 8f * SlopeFieldMain.heatmapSensitivity) * (1 / ratio));
                                                } else {
                                                      color = (float) (0.75 - (ratio * (3 / 8f * SlopeFieldMain.heatmapSensitivity)));
                                                }

                                                g.setColor(Color.getHSBColor(color, 1f, 1f));
                                          }
                                    }

                                    g.drawLine(screenX + xOffset, screenY - yOffset, screenX - xOffset, screenY + yOffset);
                              } catch (Exception e) {
                                    if (SlopeFieldMain.isUsingHeatmap) {
                                          g.setColor(Color.red);
                                    }

                                    g.drawLine(screenX, screenY - lineSize, screenX, screenY + lineSize);
                              }
                        }
                  }
            }
      }
}
