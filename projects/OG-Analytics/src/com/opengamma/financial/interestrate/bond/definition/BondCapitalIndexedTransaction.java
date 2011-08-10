/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.bond.definition;

import com.opengamma.financial.interestrate.InterestRateDerivativeVisitor;
import com.opengamma.financial.interestrate.payments.Coupon;

/**
 * Describes a transaction on a capital indexed bond (inflation bond) issue.
 * @param <C> Type of inflation coupon.
 */
public class BondCapitalIndexedTransaction<C extends Coupon> extends BondTransaction<BondCapitalIndexedSecurity<C>> {

  /**
   * Capital indexed bond transaction constructor from transaction details.
   * @param bondPurchased The bond underlying the transaction.
   * @param quantity The number of bonds purchased (can be negative or positive).
   * @param settlementAmount Transaction settlement amount.
   * @param bondStandard Description of the underlying bond with standard settlement date.
   * @param notionalStandard The notional at the standard spot time.
   */
  public BondCapitalIndexedTransaction(BondCapitalIndexedSecurity<C> bondPurchased, double quantity, double settlementAmount, BondCapitalIndexedSecurity<C> bondStandard, double notionalStandard) {
    super(bondPurchased, quantity, settlementAmount, bondStandard, notionalStandard);
  }

  @Override
  public <S, T> T accept(InterestRateDerivativeVisitor<S, T> visitor, S data) {
    return visitor.visitBondCapitalIndexedTransaction(this, data);
  }

  @Override
  public <T> T accept(InterestRateDerivativeVisitor<?, T> visitor) {
    return visitor.visitBondCapitalIndexedTransaction(this);
  }

}
