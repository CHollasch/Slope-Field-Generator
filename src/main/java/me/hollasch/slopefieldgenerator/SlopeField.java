package me.hollasch.slopefieldgenerator;

import com.udojava.evalex.Expression;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

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

      private static boolean heatmap = false;
      private static int intervals = 25;
      private static Timer timer = new Timer();
      private static Expression expression;

      public static void main(String[] args) {
            JFrame frame = new JFrame("Slope Field Generator - Connor Hollasch");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 1000);

            final JTextField inputText = new JTextField("x + y");
            inputText.setFont(inputText.getFont().deriveFont(Font.PLAIN, 24f));

            final JTextField intervals = new JTextField("25");
            intervals.setFont(intervals.getFont().deriveFont(Font.PLAIN, 24f));

            final JPanel panel = new JPanel() {
                  {
                        repaint();
                  }

                  @Override
                  public void paint(Graphics g) {
                        g.setColor(Color.black);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.setColor(Color.white);

                        int i = SlopeField.intervals;

                        for (int x = 0; x <= i * 2; ++x) {
                              for (int y = 0; y <= i * 2; ++y) {
                                    int size = (x == i && y == i ? 4 : 2);

                                    int screenX = (getWidth()/(i * 2)) * x;
                                    int screenY = (getHeight()/(i * 2)) * y;

                                    g.fillOval(screenX - (size / 2), screenY - (size / 2), size, size);

                                    if (expression != null) {
                                          int lineSize = (getWidth() + getHeight()) / (2 * i) / 5;

                                          try {
                                                BigDecimal val = expression
                                                        .with("x", String.valueOf(x - i))
                                                        .with("y", String.valueOf(i - y))
                                                        .with("e", String.valueOf(Math.E))
                                                        .eval();

                                                double real = val.doubleValue();

                                                int yOffset = real >= 0 ? (int) Math.min(real * lineSize, lineSize) : (int) (Math.min(-real * lineSize, lineSize));
                                                int xOffset = real >= 0 ? (int) Math.min((1 / real) * lineSize, lineSize) : (int) -Math.min(Math.abs(1/real) * lineSize, lineSize);

                                                if (heatmap) {
                                                      if (Math.abs(real) > 5) {
                                                            g.setColor(Color.red);
                                                      } else {
                                                            g.setColor(Color.getHSBColor(0.5f - (float) Math.abs(real / 10), 1f, 1f));
                                                      }
                                                }

                                                g.drawLine(screenX + xOffset, screenY - yOffset, screenX - xOffset, screenY + yOffset);
                                          } catch (Exception e) {
                                                g.drawLine(screenX, screenY - lineSize, screenX, screenY + lineSize);
                                          }
                                    }
                              }
                        }
                  }
            };

            inputText.addKeyListener(new KeyListener() {
                  public void keyTyped(KeyEvent e) {
                        timer.schedule(new TimerTask() {
                              @Override
                              public void run() {
                                    expression = new Expression(inputText.getText());
                                    panel.repaint();
                              }
                        }, 50);
                  }

                  public void keyPressed(KeyEvent e) {}
                  public void keyReleased(KeyEvent e) {}
            });

            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);

            JPanel textPanel = new JPanel();

            textPanel.setLayout(new FlowLayout());

            inputText.setColumns(20);
            intervals.setColumns(4);

            intervals.addKeyListener(new KeyListener() {
                  public void keyTyped(KeyEvent e) {
                        final String now = intervals.getText();
                        final int slopeIntNow = SlopeField.intervals;

                        timer.schedule(new TimerTask() {
                              @Override
                              public void run() {
                                    try {
                                          SlopeField.intervals = Integer.parseInt(intervals.getText());
                                          if (SlopeField.intervals > 200) {
                                                intervals.setText(now);
                                                SlopeField.intervals = slopeIntNow;
                                          }
                                    } catch (NumberFormatException ex) {
                                          SlopeField.intervals = 10;
                                    }

                                    panel.repaint();
                              }
                        }, 50);
                  }

                  public void keyPressed(KeyEvent e) {}
                  public void keyReleased(KeyEvent e) {}
            });

            JLabel l1 = new JLabel("Equation"); l1.setFont(l1.getFont().deriveFont(Font.PLAIN, 16f));
            JLabel l2 = new JLabel("Grid increments"); l2.setFont(l1.getFont());

            textPanel.add(l1);
            textPanel.add(inputText);
            textPanel.add(l2);
            textPanel.add(intervals);

            final JCheckBox heatmap = new JCheckBox("Enable heatmap");
            heatmap.setFont(l1.getFont());
            heatmap.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        SlopeField.heatmap = heatmap.isSelected();
                        panel.repaint();
                  }
            });

            textPanel.add(heatmap);

            frame.add(textPanel, BorderLayout.PAGE_START);
            frame.setVisible(true);

            expression = new Expression(inputText.getText());
            panel.repaint();
      }
}
