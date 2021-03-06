package me.hollasch.slopefieldgenerator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

/**
 * @author Connor Hollasch
 * @since 3/9/2016
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class SlopeFieldMenu extends JMenuBar
{
    public SlopeFieldMenu ()
    {
        final JMenu file = new JMenu("File");

        JMenuItem photo = new JMenuItem("Save As Image");
        photo.addActionListener(e -> {
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
        });

        JMenuItem renderPhoto = new JMenuItem("Render Image And Save");
        renderPhoto.addActionListener(e -> {
            final int width, height;

            final String widthIn = JOptionPane.showInputDialog("Enter the width of the image");

            try {
                width = Integer.parseInt(widthIn);
            } catch (final NumberFormatException ex) {
                JOptionPane.showConfirmDialog(
                        null,
                        "Invalid width entered, must be an integer.",
                        "Error when getting width",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            final String heightIn = JOptionPane.showInputDialog("Enter the height of the image");

            try {
                height = Integer.parseInt(heightIn);
            } catch (final NumberFormatException ex) {
                JOptionPane.showConfirmDialog(
                        null,
                        "Invalid height entered, must be an integer.",
                        "Error when getting height",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

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

                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                SlopeFieldMain.slopeField.paint(image.getGraphics(), width, height);

                try {
                    ImageIO.write(image, (png ? "png" : "jpg"), chosen);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        JMenuItem backgroundColor = new JMenuItem("Background Color");
        backgroundColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Select a Background Color", SlopeFieldMain.backgroundColor);
            SlopeFieldMain.backgroundColor = color;

            SlopeFieldMain.drawingPanel.repaint();
        });

        JMenuItem tickColor = new JMenuItem("Slope Line Color");
        tickColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(null, "Select a Slope Line Color", SlopeFieldMain.tickColor);
            SlopeFieldMain.tickColor = color;

            SlopeFieldMain.drawingPanel.repaint();
        });

        file.add(photo);
        file.add(renderPhoto);
        file.add(backgroundColor);
        file.add(tickColor);

        final JMenu heatmap = new JMenu("Heatmap");

        final JCheckBoxMenuItem heatmapEnabled = new JCheckBoxMenuItem("Enable Heatmap", SlopeFieldMain.isUsingHeatmap);

        final JSlider heatmapSensitivity = new JSlider(JSlider.HORIZONTAL, 0, 100, 75);
        heatmapSensitivity.setPaintTicks(true);
        heatmapSensitivity.setMajorTickSpacing(10);
        heatmapSensitivity.setMinorTickSpacing(5);
        heatmapSensitivity.setPaintLabels(true);

        heatmapSensitivity.addChangeListener(e -> SlopeFieldMain.applicationTimer.schedule(new TimerTask()
        {
            @Override
            public void run ()
            {
                SlopeFieldMain.heatmapSensitivity = heatmapSensitivity.getValue() / 100d;
                SlopeFieldMain.drawingPanel.repaint();
            }
        }, 50));

        heatmap.add(heatmapEnabled);
        heatmap.add(new JLabel(" "));
        heatmap.add(new JLabel("Heatmap Sensitivity"));
        heatmap.add(heatmapSensitivity);

        final JMenu bounds = new JMenu("Boundaries");

        final JTextField xMin = new JTextField("-10");
        xMin.addKeyListener(new KeyListener()
        {
            public void keyTyped (KeyEvent e)
            {
                SlopeFieldMain.applicationTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run ()
                    {
                        try {
                            double val = Double.parseDouble(xMin.getText());
                            SlopeFieldMain.xMin = val;
                            SlopeFieldMain.drawingPanel.repaint();
                        } catch (NumberFormatException ex) {
                        }
                    }
                }, 50);
            }

            public void keyPressed (KeyEvent e)
            {
            }

            public void keyReleased (KeyEvent e)
            {
            }
        });

        final JTextField xMax = new JTextField("10");
        xMax.addKeyListener(new KeyListener()
        {
            public void keyTyped (KeyEvent e)
            {
                SlopeFieldMain.applicationTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run ()
                    {
                        try {
                            double val = Double.parseDouble(xMax.getText());
                            SlopeFieldMain.xMax = val;
                            SlopeFieldMain.drawingPanel.repaint();
                        } catch (NumberFormatException ex) {
                        }
                    }
                }, 50);
            }

            public void keyPressed (KeyEvent e)
            {
            }

            public void keyReleased (KeyEvent e)
            {
            }
        });

        final JTextField yMin = new JTextField("-10");
        yMin.addKeyListener(new KeyListener()
        {
            public void keyTyped (KeyEvent e)
            {
                SlopeFieldMain.applicationTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run ()
                    {
                        try {
                            double val = Double.parseDouble(yMin.getText());
                            SlopeFieldMain.yMin = val;
                            SlopeFieldMain.drawingPanel.repaint();
                        } catch (NumberFormatException ex) {
                        }
                    }
                }, 50);
            }

            public void keyPressed (KeyEvent e)
            {
            }

            public void keyReleased (KeyEvent e)
            {
            }
        });

        final JTextField yMax = new JTextField("10");
        yMax.addKeyListener(new KeyListener()
        {
            public void keyTyped (KeyEvent e)
            {
                SlopeFieldMain.applicationTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run ()
                    {
                        try {
                            double val = Double.parseDouble(yMax.getText());
                            SlopeFieldMain.yMax = val;
                            SlopeFieldMain.drawingPanel.repaint();
                        } catch (NumberFormatException ex) {
                        }
                    }
                }, 50);
            }

            public void keyPressed (KeyEvent e)
            {
            }

            public void keyReleased (KeyEvent e)
            {
            }
        });

        bounds.add(new JLabel("X Minimum"));
        bounds.add(xMin);
        bounds.add(new JLabel("X Maximum"));
        bounds.add(xMax);
        bounds.add(new JLabel("Y Minimum"));
        bounds.add(yMin);
        bounds.add(new JLabel("Y Maximum"));
        bounds.add(yMax);

        final JMenu otherSettings = new JMenu("Other Settings");

        final JCheckBoxMenuItem drawCirclesInsteadOfLines = new JCheckBoxMenuItem("Circles Instead of Lines");
        heatmapEnabled.setSelected(SlopeFieldMain.drawCirclesInsteadOfLines);

        drawCirclesInsteadOfLines.addActionListener(e -> {
            SlopeFieldMain.drawCirclesInsteadOfLines = drawCirclesInsteadOfLines.isSelected();

            heatmapEnabled.setState(true);
            SlopeFieldMain.isUsingHeatmap = true;

            SlopeFieldMain.drawingPanel.repaint();
        });

        heatmapEnabled.addActionListener(e -> {
            SlopeFieldMain.isUsingHeatmap = heatmapEnabled.isSelected();

            if (drawCirclesInsteadOfLines.isSelected() && !heatmapEnabled.isSelected()) {
                drawCirclesInsteadOfLines.setState(false);
                SlopeFieldMain.drawCirclesInsteadOfLines = false;
            }

            SlopeFieldMain.drawingPanel.repaint();
        });

        final JCheckBoxMenuItem useAntialiasing = new JCheckBoxMenuItem("Antialiasing Enabled");
        useAntialiasing.setSelected(SlopeFieldMain.useAntialiasing);

        useAntialiasing.addActionListener(e -> {
            SlopeFieldMain.useAntialiasing = useAntialiasing.isSelected();
            SlopeFieldMain.drawingPanel.repaint();
        });

        final JSlider lineLengthDivisor = new JSlider(JSlider.HORIZONTAL, 0, 50, 20);
        lineLengthDivisor.setPaintTicks(true);
        lineLengthDivisor.setMajorTickSpacing(10);
        lineLengthDivisor.setMinorTickSpacing(5);
        lineLengthDivisor.setPaintLabels(true);

        lineLengthDivisor.addChangeListener(e -> SlopeFieldMain.applicationTimer.schedule(new TimerTask()
        {
            @Override
            public void run ()
            {
                SlopeFieldMain.lineLengthDivisor = Math.max(0.5, lineLengthDivisor.getValue()) / 10d;
                SlopeFieldMain.drawingPanel.repaint();
            }
        }, 50));

        otherSettings.add(drawCirclesInsteadOfLines);
        otherSettings.add(useAntialiasing);
        otherSettings.add(new JLabel(" "));
        otherSettings.add(new JLabel("Line Length Divisor"));
        otherSettings.add(lineLengthDivisor);

        add(file);
        add(heatmap);
        add(bounds);
        add(otherSettings);
    }
}
