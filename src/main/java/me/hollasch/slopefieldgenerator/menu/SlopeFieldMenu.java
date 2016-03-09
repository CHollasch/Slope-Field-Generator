package me.hollasch.slopefieldgenerator.menu;

import me.hollasch.slopefieldgenerator.SlopeFieldMain;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

/**
 * @author Connor Hollasch
 * @since 3/9/2016
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
public class SlopeFieldMenu extends JMenuBar {

      public SlopeFieldMenu() {
            final JMenu file = new JMenu("File");

            JMenuItem photo = new JMenuItem("Save As Image");
            photo.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        JFileChooser outputFile = new JFileChooser();
                        outputFile.setFileFilter(new FileNameExtensionFilter("JPG & PNG", "jpg", "png"));
                        int val = outputFile.showSaveDialog(file.getParent());

                        if (val == 0) {
                              File chosen = outputFile.getSelectedFile();

                              if (!(chosen.getName().endsWith("jpg") || chosen.getName().endsWith("png"))) {
                                    chosen = new File(chosen.getParentFile(), chosen.getName() + ".png");
                              }

                              boolean png = (chosen.getName().endsWith("png"));

                              if (!(chosen.exists())) {
                                    try {
                                          chosen.createNewFile();
                                    } catch (IOException e1) {
                                          JOptionPane.showMessageDialog(file.getParent(), "An error occurred when saving your file.", "Save error", JOptionPane.ERROR_MESSAGE);
                                          e1.printStackTrace();
                                    }
                              }

                              JPanel panel = SlopeFieldMain.drawingPanel;
                              BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
                              SlopeFieldMain.slopeField.paint(image.getGraphics(), panel.getWidth(), panel.getHeight());

                              try {
                                    ImageIO.write(image, (png ? "png" : "jpg"), chosen);
                              } catch (IOException e1) {
                                    e1.printStackTrace();
                              }
                        }
                  }
            });

            JMenuItem backgroundColor = new JMenuItem("Background Color");
            backgroundColor.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        Color color = JColorChooser.showDialog(null, "Select a Background Color", SlopeFieldMain.backgroundColor);
                        SlopeFieldMain.backgroundColor = color;

                        SlopeFieldMain.drawingPanel.repaint();
                  }
            });

            JMenuItem tickColor = new JMenuItem("Slope Line Color");
            tickColor.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        Color color = JColorChooser.showDialog(null, "Select a Slope Line Color", SlopeFieldMain.tickColor);
                        SlopeFieldMain.tickColor = color;

                        SlopeFieldMain.drawingPanel.repaint();
                  }
            });

            file.add(photo);
            file.add(backgroundColor);
            file.add(tickColor);

            final JMenu heatmap = new JMenu("Heatmap");

            final JCheckBoxMenuItem heatmapEnabled = new JCheckBoxMenuItem("Enable Heatmap");
            heatmapEnabled.setSelected(true);
            heatmapEnabled.addActionListener(new ActionListener() {
                  @Override
                  public void actionPerformed(ActionEvent e) {
                        SlopeFieldMain.isUsingHeatmap = heatmapEnabled.isSelected();
                        SlopeFieldMain.drawingPanel.repaint();
                  }
            });

            final JSlider heatmapSensitivity = new JSlider(JSlider.HORIZONTAL, 0, 100, 75);
            heatmapSensitivity.setPaintTicks(true);
            heatmapSensitivity.setMajorTickSpacing(10);
            heatmapSensitivity.setMinorTickSpacing(5);

            heatmapSensitivity.setPaintLabels(true);
            heatmapSensitivity.setName("Test");

            heatmapSensitivity.addChangeListener(new ChangeListener() {
                  @Override
                  public void stateChanged(ChangeEvent e) {
                        SlopeFieldMain.applicationTimer.schedule(new TimerTask() {
                              @Override
                              public void run() {
                                    SlopeFieldMain.heatmapSensitivity = heatmapSensitivity.getValue() / 100d;
                                    SlopeFieldMain.drawingPanel.repaint();
                              }
                        }, 50);
                  }
            });

            heatmap.add(heatmapEnabled);
            heatmap.add(new JLabel(" "));
            heatmap.add(heatmapSensitivity);

            add(file);
            add(heatmap);
      }
}
