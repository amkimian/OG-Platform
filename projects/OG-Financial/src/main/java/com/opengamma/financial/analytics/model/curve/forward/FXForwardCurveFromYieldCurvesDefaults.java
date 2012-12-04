/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.model.curve.forward;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.engine.ComputationTarget;
import com.opengamma.engine.ComputationTargetType;
import com.opengamma.engine.function.FunctionCompilationContext;
import com.opengamma.engine.value.ValuePropertyNames;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueRequirementNames;
import com.opengamma.financial.analytics.model.forex.forward.FXForwardFunction;
import com.opengamma.financial.property.DefaultPropertyFunction;
import com.opengamma.id.UniqueId;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.UnorderedCurrencyPair;
import com.opengamma.util.tuple.Pair;

/**
 *
 */
public class FXForwardCurveFromYieldCurvesDefaults extends DefaultPropertyFunction {
  private static final Logger s_logger = LoggerFactory.getLogger(FXForwardCurveFromYieldCurvesDefaults.class);
  private static final String[] VALUE_REQUIREMENTS = new String[] {
    ValueRequirementNames.FORWARD_CURVE
  };
  private final Map<String, Pair<String, String>> _currencyCurveConfigAndDiscountingCurveNames;

  //TODO there are ordering issues in this class (the currency pair) - it makes an assumption about which is pay and which is receive
  public FXForwardCurveFromYieldCurvesDefaults(final String... currencyCurveConfigAndDiscountingCurveNames) {
    super(ComputationTargetType.PRIMITIVE, true);
    ArgumentChecker.notNull(currencyCurveConfigAndDiscountingCurveNames, "currency and curve config names");
    final int nPairs = currencyCurveConfigAndDiscountingCurveNames.length;
    ArgumentChecker.isTrue(nPairs % 3 == 0, "Must have one curve config and discounting curve name per currency");
    _currencyCurveConfigAndDiscountingCurveNames = new HashMap<String, Pair<String, String>>();
    for (int i = 0; i < currencyCurveConfigAndDiscountingCurveNames.length; i += 3) {
      final Pair<String, String> pair = Pair.of(currencyCurveConfigAndDiscountingCurveNames[i + 1], currencyCurveConfigAndDiscountingCurveNames[i + 2]);
      _currencyCurveConfigAndDiscountingCurveNames.put(currencyCurveConfigAndDiscountingCurveNames[i], pair);
    }
  }

  @Override
  public boolean canApplyTo(final FunctionCompilationContext context, final ComputationTarget target) {
    if (target.getType() != ComputationTargetType.PRIMITIVE) {
      return false;
    }
    final UniqueId uniqueId = target.getUniqueId();
    if (!UnorderedCurrencyPair.OBJECT_SCHEME.equals(uniqueId.getScheme())) {
      return false;
    }
    final String currencyPair = uniqueId.getValue();
    final String firstCurrency = currencyPair.substring(0, 3);
    final String secondCurrency = currencyPair.substring(3, 6);
    return _currencyCurveConfigAndDiscountingCurveNames.containsKey(firstCurrency) && _currencyCurveConfigAndDiscountingCurveNames.containsKey(secondCurrency);
  }

  @Override
  protected void getDefaults(final PropertyDefaults defaults) {
    for (final String valueRequirement : VALUE_REQUIREMENTS) {
      defaults.addValuePropertyName(valueRequirement, ValuePropertyNames.PAY_CURVE);
      defaults.addValuePropertyName(valueRequirement, ValuePropertyNames.RECEIVE_CURVE);
      defaults.addValuePropertyName(valueRequirement, FXForwardFunction.PAY_CURVE_CALC_CONFIG);
      defaults.addValuePropertyName(valueRequirement, FXForwardFunction.RECEIVE_CURVE_CALC_CONFIG);
    }
  }

  @Override
  protected Set<String> getDefaultValue(final FunctionCompilationContext context, final ComputationTarget target, final ValueRequirement desiredValue, final String propertyName) {
    final UniqueId uniqueId = target.getUniqueId();
    final String currencyPair = uniqueId.getValue();
    final String firstCurrency = currencyPair.substring(0, 3);
    final String secondCurrency = currencyPair.substring(3, 6);

    if (!_currencyCurveConfigAndDiscountingCurveNames.containsKey(firstCurrency)) {
      s_logger.error("Could not get config for currency " + firstCurrency + "; should never happen");
      return null;
    }
    if (!_currencyCurveConfigAndDiscountingCurveNames.containsKey(secondCurrency)) {
      s_logger.error("Could not get config for currency " + secondCurrency + "; should never happen");
      return null;
    }
    final Pair<String, String> payPair = _currencyCurveConfigAndDiscountingCurveNames.get(firstCurrency);
    final Pair<String, String> receivePair = _currencyCurveConfigAndDiscountingCurveNames.get(secondCurrency);
    if (ValuePropertyNames.PAY_CURVE.equals(propertyName)) {
      return Collections.singleton(payPair.getSecond());
    }
    if (ValuePropertyNames.RECEIVE_CURVE.equals(propertyName)) {
      return Collections.singleton(receivePair.getSecond());
    }
    if (FXForwardFunction.PAY_CURVE_CALC_CONFIG.equals(propertyName)) {
      return Collections.singleton(payPair.getFirst());
    }
    if (FXForwardFunction.RECEIVE_CURVE_CALC_CONFIG.equals(propertyName)) {
      return Collections.singleton(receivePair.getFirst());
    }
    s_logger.error("Could not get default value for {}", propertyName);
    return null;
  }

}