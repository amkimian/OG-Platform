/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.analytics.formatting;

import com.opengamma.analytics.financial.forex.method.PresentValueForexBlackVolatilitySensitivity;
import com.opengamma.engine.value.ValueSpecification;

/**
 *
 */
/* package */ class PresentValueForexBlackVolatilitySensitivityFormatter
    extends AbstractFormatter<PresentValueForexBlackVolatilitySensitivity> {

  /* package */ PresentValueForexBlackVolatilitySensitivityFormatter() {
    super(PresentValueForexBlackVolatilitySensitivity.class);
  }

  @Override
  public Object formatCell(PresentValueForexBlackVolatilitySensitivity value, ValueSpecification valueSpec) {
    return "Vector (" + value.getVega().getMap().size() + ")";
  }

  @Override
  public DataType getDataType() {
    return DataType.LABELLED_MATRIX_1D;
  }
}
