package me.hollasch.slopefieldgenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
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
public class SlopeFieldMain
{

    public static boolean isUsingHeatmap = true;
    public static int slopeIntervals = 25;

    public static Timer applicationTimer = new Timer();

    public static double xMin = -10;
    public static double xMax = 10;

    public static double yMin = -10;
    public static double yMax = 10;

    public static double heatmapSensitivity = 3 / 4d;
    public static Color backgroundColor = Color.black;
    public static Color tickColor = Color.white;

    public static boolean drawCirclesInsteadOfLines = false;
    public static boolean useAntialiasing = true;
    public static double lineLengthDivisor = 2d;

    public static SlopeField slopeField;
    public static JPanel drawingPanel;

    public static void main (String[] args)
    {
        JFrame frame = new JFrame("Slope Field Generator - Connor Hollasch");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setFont(new Font("TabbedPane", Font.PLAIN, 20));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        final JTextField inputExpressionTestField = new JTextField("x + y");
        inputExpressionTestField.setFont(frame.getFont());

        final JTextField intervalsTextField = new JTextField("25");
        intervalsTextField.setFont(frame.getFont());

        slopeField = new SlopeField(inputExpressionTestField.getText());
        drawingPanel = new JPanel()
        {
            @Override
            public void paint (Graphics g)
            {
                slopeField.paint(g, getWidth(), getHeight());
            }
        };

        inputExpressionTestField.addKeyListener(new KeyListener()
        {
            public void keyTyped (KeyEvent e)
            {
                applicationTimer.schedule(new TimerTask()
                {
                    @Override
                    public void run ()
                    {
                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                            if (slopeField.setExpression(inputExpressionTestField.getText())) {
                                drawingPanel.repaint();
                            }
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

        frame.setLayout(new BorderLayout());
        frame.add(drawingPanel, BorderLayout.CENTER);

        JPanel textPanel = new JPanel();

        textPanel.setLayout(new FlowLayout());

        inputExpressionTestField.setColumns(20);
        intervalsTextField.setColumns(4);

        JLabel l1 = new JLabel("Equation");
        l1.setFont(frame.getFont());
        JLabel l2 = new JLabel("Grid increments");
        l2.setFont(frame.getFont());

        textPanel.add(l1);
        textPanel.add(inputExpressionTestField);
        textPanel.add(l2);
        textPanel.add(intervalsTextField);

        JButton updateButton = new JButton("Update");
        updateButton.setFont(frame.getFont());
        updateButton.addActionListener(e -> {
            try {
                SlopeFieldMain.slopeIntervals = Integer.parseInt(intervalsTextField.getText());

                if (slopeField.setExpression(inputExpressionTestField.getText())) {
                    drawingPanel.repaint();
                }
            } catch (NumberFormatException ex) {
                SlopeFieldMain.slopeIntervals = 10;
            }

            drawingPanel.repaint();
        });

        textPanel.add(updateButton);

        frame.setJMenuBar(new SlopeFieldMenu());
        frame.add(textPanel, BorderLayout.PAGE_START);
        frame.setVisible(true);

        drawingPanel.repaint();
    }
}
