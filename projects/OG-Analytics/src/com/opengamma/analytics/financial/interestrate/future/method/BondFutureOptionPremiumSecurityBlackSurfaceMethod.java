/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.future.method;

import com.opengamma.analytics.financial.interestrate.InterestRateCurveSensitivity;
import com.opengamma.analytics.financial.interestrate.YieldCurveBundle;
import com.opengamma.analytics.financial.interestrate.future.derivative.BondFutureOptionPremiumSecurity;
import com.opengamma.analytics.financial.model.option.definition.YieldCurveWithBlackCubeBundle;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.BlackFunctionData;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.BlackPriceFunction;
import com.opengamma.analytics.financial.model.option.pricing.analytic.formula.EuropeanVanillaOption;
import com.opengamma.analytics.util.surface.SurfaceValue;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.tuple.DoublesPair;

/**
 * Method for the pricing of bond future options. The pricing is done with a Black approach on the bond future.
 * The Black parameters are represented by (expiration-strike) surfaces.  
 */
public final class BondFutureOptionPremiumSecurityBlackSurfaceMethod {
  // TODO: Change to a surface when available.

  /**
   * Creates the method unique instance.
   */
  private static final BondFutureOptionPremiumSecurityBlackSurfaceMethod INSTANCE = new BondFutureOptionPremiumSecurityBlackSurfaceMethod();

  /**
   * Return the method unique instance.
   * @return The instance.
   */
  public static BondFutureOptionPremiumSecurityBlackSurfaceMethod getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  private BondFutureOptionPremiumSecurityBlackSurfaceMethod() {
  }

  /**
   * The Black function used in the pricing.
   */
  private static final BlackPriceFunction BLACK_FUNCTION = new BlackPriceFunction();

  /**
   * The method used to compute the future price. 
   */
  private static final BondFutureDiscountingMethod METHOD_FUTURE = BondFutureDiscountingMethod.getInstance();

  /**
   * Computes the option security price from future price.
   * @param security The future option security, not null
   * @param blackData The curve and Black volatility data, not null
   * @param priceFuture The price of the underlying future.
   * @return The security price.
   */
  public double optionPriceFromFuturePrice(final BondFutureOptionPremiumSecurity security, final YieldCurveWithBlackCubeBundle blackData, final double priceFuture) {
    ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(blackData, "Black data");
    final double strike = security.getStrike();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(strike, security.getExpirationTime(), security.isCall());
    final double volatility = blackData.getVolatility(security.getExpirationTime(), security.getStrike());
    final BlackFunctionData dataBlack = new BlackFunctionData(priceFuture, 1.0, volatility);
    final double priceSecurity = BLACK_FUNCTION.getPriceFunction(option).evaluate(dataBlack);
    return priceSecurity;
  }

  public double optionPriceFromFuturePrice(final BondFutureOptionPremiumSecurity security, final YieldCurveBundle curves, final double priceFuture) {
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackCubeBundle, "Yield curve bundle should contain Black cube");
    return optionPriceFromFuturePrice(security, (YieldCurveWithBlackCubeBundle) curves, priceFuture);
  }

  /**
   * Computes the option security price. 
   * @param security The bond future option security, not null
   * @param blackData The curve and Black volatility data, not null
   * @return The security price.
   */
  public double optionPrice(final BondFutureOptionPremiumSecurity security, final YieldCurveWithBlackCubeBundle blackData) {
    ArgumentChecker.notNull(security, "security");
    final double priceFuture = METHOD_FUTURE.price(security.getUnderlyingFuture(), blackData);
    return optionPriceFromFuturePrice(security, blackData, priceFuture);
  }

  public double optionPrice(final BondFutureOptionPremiumSecurity security, final YieldCurveBundle curves) {
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackCubeBundle, "Yield curve bundle should contain Black cube");
    return optionPrice(security, (YieldCurveWithBlackCubeBundle) curves);
  }

  /**
   * Computes the option security price curve sensitivity. 
   * It is supposed that for a given strike the volatility does not change with the curves.
   * @param security The future option security, not null
   * @param blackData The curve and Black volatility data, not null
   * @return The security price curve sensitivity.
   */
  public InterestRateCurveSensitivity priceCurveSensitivity(final BondFutureOptionPremiumSecurity security, final YieldCurveWithBlackCubeBundle blackData) {
    ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(blackData, "Black data");
    // Forward sweep
    final double priceFuture = METHOD_FUTURE.price(security.getUnderlyingFuture(), blackData);
    final double strike = security.getStrike();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(strike, security.getExpirationTime(), security.isCall());
    final double volatility = blackData.getVolatility(security.getExpirationTime(), security.getStrike());
    final BlackFunctionData dataBlack = new BlackFunctionData(priceFuture, 1.0, volatility);
    final double[] priceAdjoint = BLACK_FUNCTION.getPriceAdjoint(option, dataBlack);
    // Backward sweep
    final double priceBar = 1.0;
    final double priceFutureBar = priceAdjoint[1] * priceBar;
    final InterestRateCurveSensitivity priceFutureDerivative = METHOD_FUTURE.priceCurveSensitivity(security.getUnderlyingFuture(), blackData);
    return priceFutureDerivative.multiply(priceFutureBar);
  }

  public InterestRateCurveSensitivity priceCurveSensitivity(final BondFutureOptionPremiumSecurity security, final YieldCurveBundle curves) {
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackCubeBundle, "Yield curve bundle should contain Black cube");
    return priceCurveSensitivity(security, (YieldCurveWithBlackCubeBundle) curves);
  }

  /**
   * Computes the option security price volatility sensitivity. 
   * @param security The future option security, not null
   * @param blackData The curve and Black volatility data, not null
   * @return The security price Black volatility sensitivity.
   */
  public SurfaceValue priceBlackSensitivity(final BondFutureOptionPremiumSecurity security, final YieldCurveWithBlackCubeBundle blackData) {
    ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(blackData, "YieldCurveWithBlackCubeBundle was unexpectedly null");
    // Forward sweep
    final double priceFuture = METHOD_FUTURE.price(security.getUnderlyingFuture(), blackData);
    final double strike = security.getStrike();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(strike, security.getExpirationTime(), security.isCall());
    final double volatility = blackData.getVolatility(security.getExpirationTime(), security.getStrike());
    final BlackFunctionData dataBlack = new BlackFunctionData(priceFuture, 1.0, volatility);
    final double[] priceAdjoint = BLACK_FUNCTION.getPriceAdjoint(option, dataBlack);
    // Backward sweep
    final double priceBar = 1.0;
    final double volatilityBar = priceAdjoint[2] * priceBar;
    final DoublesPair expiryStrikeDelay = new DoublesPair(security.getExpirationTime(), strike);
    final SurfaceValue sensitivity = SurfaceValue.from(expiryStrikeDelay, volatilityBar);
    return sensitivity;
  }

  public SurfaceValue priceBlackSensitivity(final BondFutureOptionPremiumSecurity security, final YieldCurveBundle curves) {
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackCubeBundle, "Yield curve bundle should contain Black cube");
    return priceBlackSensitivity(security, (YieldCurveWithBlackCubeBundle) curves);
  }

  /**
   * Computes the option's value delta, the first derivative of the security price wrt underlying futures rate. 
   * @param security The future option security, not null
   * @param blackData The curve and Black volatility data, not null
   * @return The security value delta.
   */
  public double optionPriceDelta(final BondFutureOptionPremiumSecurity security, final YieldCurveWithBlackCubeBundle blackData) {
    ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(blackData, "YieldCurveWithBlackCubeBundle was unexpectedly null");
    // Forward sweep
    final double priceFuture = METHOD_FUTURE.price(security.getUnderlyingFuture(), blackData);
    final double strike = security.getStrike();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(strike, security.getExpirationTime(), security.isCall());

    final double volatility = blackData.getVolatility(security.getExpirationTime(), security.getStrike());
    final BlackFunctionData dataBlack = new BlackFunctionData(priceFuture, 1.0, volatility);

    // TODO This is overkill. We only need one value, but it provides extra calculations while doing testing
    final double[] firstDerivs = new double[3];
    final double[][] secondDerivs = new double[3][3];
    BLACK_FUNCTION.getPriceAdjoint2(option, dataBlack, firstDerivs, secondDerivs);
    return firstDerivs[0];
  }

  /**
   * Computes the option's value delta, the first derivative of the security price wrt underlying futures rate. 
   * @param security The future option security, not null
   * @param curves The curve and Black volatility data, not null
   * @return The security value delta.
   */
  public double optionPriceDelta(final BondFutureOptionPremiumSecurity security, final YieldCurveBundle curves) {
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackCubeBundle, "Yield curve bundle should contain Black cube");
    return optionPriceDelta(security, (YieldCurveWithBlackCubeBundle) curves);
  }

  /**
   * Computes the option's value gamma, the second derivative of the security price wrt underlying futures rate. 
   * @param security The future option security, not null
   * @param blackData The curve and Black volatility data, not null
   * @return The security price gamma.
   */
  public double optionPriceGamma(final BondFutureOptionPremiumSecurity security, final YieldCurveWithBlackCubeBundle blackData) {
    ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(blackData, "YieldCurveWithBlackCubeBundle was unexpectedly null");
    // Forward sweep
    final double priceFuture = METHOD_FUTURE.price(security.getUnderlyingFuture(), blackData);
    final double strike = security.getStrike();
    final EuropeanVanillaOption option = new EuropeanVanillaOption(strike, security.getExpirationTime(), security.isCall());

    final double volatility = blackData.getVolatility(security.getExpirationTime(), security.getStrike());
    final BlackFunctionData dataBlack = new BlackFunctionData(priceFuture, 1.0, volatility);

    // TODO This is overkill. We only need one value, but it provides extra calculations while doing testing
    final double[] firstDerivs = new double[3];
    final double[][] secondDerivs = new double[3][3];
    BLACK_FUNCTION.getPriceAdjoint2(option, dataBlack, firstDerivs, secondDerivs);
    return secondDerivs[0][0];
  }

  /**
   * Computes the option's value gamma, the second derivative of the security price wrt underlying futures rate. 
   * @param security The future option security, not null
   * @param curves The curve and Black volatility data, not null
   * @return The security price gamma.
   */
  public double optionPriceGamma(final BondFutureOptionPremiumSecurity security, final YieldCurveBundle curves) {
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackCubeBundle, "Yield curve bundle should contain Black cube");
    return optionPriceGamma(security, (YieldCurveWithBlackCubeBundle) curves);
  }

  /**
   * Interpolates and returns the option's implied volatility 
   * @param security The future option security, not null
   * @param curves The curve and Black volatility data, not null
   * @return Lognormal Implied Volatility
   */
  public double impliedVolatility(final BondFutureOptionPremiumSecurity security, final YieldCurveBundle curves) {
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackCubeBundle, "Yield curve bundle should contain Black cube");
    return impliedVolatility(security, (YieldCurveWithBlackCubeBundle) curves);
  }

  /**
   * Interpolates and returns the option's implied volatility 
   * @param security The future option security, not null
   * @param blackData The curve and Black volatility data, not null
   * @return Lognormal Implied Volatility.
   */
  public double impliedVolatility(final BondFutureOptionPremiumSecurity security, final YieldCurveWithBlackCubeBundle blackData) {
    ArgumentChecker.notNull(security, "security");
    ArgumentChecker.notNull(blackData, "Black data");
    return blackData.getVolatility(security.getExpirationTime(), security.getStrike());
  }

  public double underlyingFuturePrice(final BondFutureOptionPremiumSecurity security, final YieldCurveBundle curves) {
    ArgumentChecker.isTrue(curves instanceof YieldCurveWithBlackCubeBundle, "Yield curve bundle should contain Black cube");
    return underlyingFuturePrice(security, (YieldCurveWithBlackCubeBundle) curves);
  }

  /**
   * Computes the underlying future security price. 
   * @param security The future option security, not null
   * @param blackData The curve and Black volatility data, not null
   * @return The security price.
   */
  public double underlyingFuturePrice(final BondFutureOptionPremiumSecurity security, final YieldCurveWithBlackCubeBundle blackData) {
    ArgumentChecker.notNull(security, "security");
    return METHOD_FUTURE.price(security.getUnderlyingFuture(), blackData);
  }

}