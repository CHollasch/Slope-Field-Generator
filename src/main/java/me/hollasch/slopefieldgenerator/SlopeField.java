package me.hollasch.slopefieldgenerator;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Connor Hollasch
 * @since 3/8/2016
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
public class SlopeField
{
    private static final ArrayList<String> javaScriptMathReplacements = new ArrayList<String>()
    {
        {
            add("function abs (x) { return Math.abs(x); }");
            add("function sin (x) { return Math.sin(x); }");
            add("function cos (x) { return Math.cos(x); }");
            add("function tan (x) { return Math.tan(x); }");
            add("function asin (x) { return Math.asin(x); }");
            add("function acos (x) { return Math.acos(x); }");
            add("function atan (x) { return Math.atan(x); }");
            add("function cosh (x) { return Math.cosh(x); }");
            add("function sinh (x) { return Math.sinh(x); }");
            add("function tanh (x) { return Math.tanh(x); }");
            add("function pow (x) { return Math.pow(x); }");
            add("function random () { return Math.random(); }");
            add("function sqrt (x) { return Math.sqrt(x); }");
            add("function floor (x) { return Math.floor(x); }");
            add("function ceil (x) { return Math.ceil(x); }");
        }
    };

    private final ScriptEngineManager factory;
    private final ScriptEngine scriptEngine;

    private Solvable solvable;

    private double[][] cachedValues;

    public SlopeField (final String equation)
    {
        this.factory = new ScriptEngineManager();
        this.scriptEngine = factory.getEngineByName("JavaScript");

        setExpression(equation);
    }

    public boolean setExpression (final String equation)
    {
        this.cachedValues = null;
        final String built = transform(equation);

        try {
            this.scriptEngine.eval(built);

            final Invocable inv = (Invocable) this.scriptEngine;

            this.solvable = inv.getInterface(Solvable.class);
            return true;
        } catch (final ScriptException e) {
            this.solvable = null;
            return false;
        }
    }

    private String transform (final String expression)
    {
        final String preExpression = expression
                .replace("e", String.valueOf(Math.E))
                .replace("pi", String.valueOf(Math.PI))
                .replace("unirandom", String.valueOf(Math.random()))
                .replace("random", "random()");

        String building = "function solve(x, y) { return " + preExpression.toLowerCase() + "; }";
        building = building.replace("min", "Math.min");
        building = building.replace("max", "Math.max");

        for (final String function : SlopeField.javaScriptMathReplacements) {
            building += function;
        }

        return building;
    }

    public void paint (final Graphics g, final int canvasWidth, final int canvasHeight)
    {
        if (SlopeFieldMain.useAntialiasing && SlopeFieldMain.slopeIntervals > 1) {
            final RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            ((Graphics2D) g).setRenderingHints(rh);
        }

        g.setColor(SlopeFieldMain.backgroundColor);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        g.setColor(SlopeFieldMain.tickColor);

        final int i = SlopeFieldMain.slopeIntervals;

        final int spanX = (int) Math.ceil((double) canvasWidth / i) + 1;
        final int spanY = (int) Math.ceil((double) canvasHeight / i) + 1;

        final boolean cachingValues =  this.cachedValues == null;

        if (cachingValues) {
            this.cachedValues = new double[spanX][spanY];
        }

        final double xRange = Math.abs(SlopeFieldMain.xMax - SlopeFieldMain.xMin);
        final double yRange = Math.abs(SlopeFieldMain.yMax - SlopeFieldMain.yMin);

        int xCache = 0;
        for (double x = SlopeFieldMain.xMin, screenX = 0; screenX <= canvasWidth; x += (xRange / (canvasWidth / i)), screenX += i) {
            int yCache = 0;
            for (double y = SlopeFieldMain.yMin, screenY = 0; screenY <= canvasHeight; y += (yRange / (canvasHeight / i)), screenY += i) {
                if (this.solvable != null) {
                    int lineSize = (int) (i / 2.5);

                    try {
                        final Number number;
                        if (!cachingValues) {
                            number = this.cachedValues[xCache][yCache++];
                        } else {
                            number = this.solvable.solve(x, y);
                            this.cachedValues[xCache][yCache++] = number.doubleValue();
                        }

                        double real = number.doubleValue();

                        if (SlopeFieldMain.isUsingHeatmap) {
                            // Slope based heatmap coloring
                            double ratio = Math.abs(real);
                            float color;

                            double heatmapScale = SlopeFieldMain.heatmapSensitivity;

                            if (ratio >= 1) {
                                color = (float) ((heatmapScale / 2d) * (1d / ratio));
                            } else {
                                color = (float) (heatmapScale - (ratio * (heatmapScale / 2d)));
                            }

                            g.setColor(Color.getHSBColor(color, 1f, 1f));
                        }

                        if (SlopeFieldMain.slopeIntervals == 1) {
                            g.drawLine((int) screenX, (int) screenY, (int) screenX, (int) screenY);
                        } else {
                            if (SlopeFieldMain.drawCirclesInsteadOfLines) {
                                g.fillOval((int) screenX, (int) screenY, SlopeFieldMain.slopeIntervals, SlopeFieldMain.slopeIntervals);
                            } else {
                                final double yOffset = real >= 0 ? Math.min(real * lineSize, lineSize) : (Math.min(-real * lineSize, lineSize));
                                final double xOffset = real >= 0 ? Math.min((1 / real) * lineSize, lineSize) : -Math.min(Math.abs(1 / real) * lineSize, lineSize);

                                g.drawLine((int) (screenX + xOffset), (int) (screenY - yOffset), (int) (screenX - xOffset), (int) (screenY + yOffset));
                            }
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Invalid equation entered!", "Error", JOptionPane.ERROR_MESSAGE);
                        g.setColor(SlopeFieldMain.backgroundColor);
                        g.fillRect(0, 0, canvasWidth, canvasHeight);
                        return;
                    }
                }
            }
            ++xCache;
        }
    }
}
