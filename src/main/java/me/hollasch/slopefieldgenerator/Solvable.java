/*
 * Copyright (C) 2017 SlopeFieldGenerator - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, September 2017
 */

package me.hollasch.slopefieldgenerator;

/**
 * @author Connor Hollasch
 * @since Sep 27, 10:58 PM
 */
public interface Solvable
{
    Number solve (final double x, final double y);

    Number abs (final double x);

    Number sin (final double x);

    Number cos (final double x);

    Number tan (final double x);

    Number asin (final double x);

    Number acos (final double x);

    Number atan (final double x);

    Number cosh (final double x);

    Number sinh (final double x);

    Number tanh (final double x);

    Number pow (final double x);

    Number random ();

    Number sqrt (final double x);

    Number floor (final double x);

    Number ceil (final double x);

    Number log (final double x);

    Number ln (final double x);

    Number doif (final boolean b, final double x, final double y);
}
