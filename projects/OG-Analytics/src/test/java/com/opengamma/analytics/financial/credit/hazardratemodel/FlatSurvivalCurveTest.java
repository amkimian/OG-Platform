/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.hazardratemodel;

import org.testng.annotations.Test;

/**
 * Class to test the implementation of the flat survival curve object
 */
public class FlatSurvivalCurveTest {

  // ---------------------------------------------------------------------------------------

  // Flag to control if any test results are output to the console
  private static final boolean outputResults = false;

  // ----------------------------------------------------------------------------------

  // Parameters to build the simple (flat) survival curve
  private static final double parSpread = 60.0;
  private static final double recoveryRate = 0.40;

  // Construct a survival curve based on a flat hazard rate term structure (for testing purposes only)
  private static final FlatSurvivalCurve survivalCurve = new FlatSurvivalCurve(parSpread, recoveryRate);

  // ----------------------------------------------------------------------------------

  @Test
  public void testFlatSurvivalCurve() {

    double hazardRate = survivalCurve.getFlatHazardRate();

    for (double t = 0.0; t <= 5.0; t += 0.25) {

      double survivalProbabilty = survivalCurve.getSurvivalProbability(hazardRate, t);

      if (outputResults) {
        System.out.println("t = " + t + "\t\t" + "S(0, t) = " + survivalProbabilty);
      }
    }
  }

  // ---------------------------------------------------------------------------------------
}
