/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.bond.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opengamma.analytics.financial.interestrate.bond.definition.BillTransaction;
import com.opengamma.analytics.financial.provider.description.IssuerProviderInterface;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MulticurveSensitivity;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyMulticurveSensitivity;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;
import com.opengamma.util.tuple.Pair;

/**
 * Class with methods related to bill transaction valued by discounting.
 * <P> Reference: Bill pricing, version 1.0. OpenGamma documentation, January 2012.
 */
public final class BillTransactionDiscountingMethod {

  /**
   * The unique instance of the class.
   */
  private static final BillTransactionDiscountingMethod INSTANCE = new BillTransactionDiscountingMethod();

  /**
   * Return the class instance.
   * @return The instance.
   */
  public static BillTransactionDiscountingMethod getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor
   */
  private BillTransactionDiscountingMethod() {
  }

  /**
   * Methods.
   */
  private static final BillSecurityDiscountingMethod METHOD_SECURITY = BillSecurityDiscountingMethod.getInstance();

  /**
   * Computes the bill transaction present value.
   * @param bill The bill.
   * @param issuer The issuer and multi-curves provider.
   * @return The present value.
   */
  public MultipleCurrencyAmount presentValue(final BillTransaction bill, final IssuerProviderInterface issuer) {
    ArgumentChecker.notNull(bill, "Bill");
    ArgumentChecker.notNull(issuer, "Issuer and multi-curves provider");
    Currency ccy = bill.getCurrency();
    MultipleCurrencyAmount pvBill = METHOD_SECURITY.presentValue(bill.getBillPurchased(), issuer);
    double pvSettle = bill.getSettlementAmount() * issuer.getMulticurveProvider().getDiscountFactor(ccy, bill.getBillPurchased().getSettlementTime());
    return pvBill.multipliedBy(bill.getQuantity()).plus(MultipleCurrencyAmount.of(ccy, pvSettle));
  }

  /**
   * Computes the bill present value curve sensitivity.
   * @param bill The bill.
   * @param issuer The issuer and multi-curves provider.
   * @return The sensitivity.
   */
  public MultipleCurrencyMulticurveSensitivity presentValueCurveSensitivity(final BillTransaction bill, final IssuerProviderInterface issuer) {
    ArgumentChecker.notNull(bill, "Bill");
    ArgumentChecker.notNull(issuer, "Issuer and multi-curves provider");
    Currency ccy = bill.getCurrency();
    Pair<String, Currency> issuerCcy = bill.getBillPurchased().getIssuerCcy();
    double dfCreditEnd = issuer.getDiscountFactor(issuerCcy, bill.getBillPurchased().getEndTime());
    double dfDscSettle = issuer.getMulticurveProvider().getDiscountFactor(ccy, bill.getBillPurchased().getSettlementTime());
    // Backward sweep
    double pvBar = 1.0;
    double dfCreditEndBar = bill.getQuantity() * bill.getBillPurchased().getNotional() * pvBar;
    double dfDscSettleBar = bill.getSettlementAmount() * pvBar;
    final Map<String, List<DoublesPair>> resultMapCredit = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listCredit = new ArrayList<DoublesPair>();
    listCredit.add(new DoublesPair(bill.getBillPurchased().getEndTime(), -bill.getBillPurchased().getEndTime() * dfCreditEnd * dfCreditEndBar));
    resultMapCredit.put(issuer.getName(issuerCcy), listCredit);
    MulticurveSensitivity result = MulticurveSensitivity.ofYieldDiscounting(resultMapCredit);
    final Map<String, List<DoublesPair>> resultMapDsc = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listDsc = new ArrayList<DoublesPair>();
    listDsc.add(new DoublesPair(bill.getBillPurchased().getSettlementTime(), -bill.getBillPurchased().getSettlementTime() * dfDscSettle * dfDscSettleBar));
    resultMapDsc.put(issuer.getMulticurveProvider().getName(ccy), listDsc);
    return MultipleCurrencyMulticurveSensitivity.of(ccy, result.plus(MulticurveSensitivity.ofYieldDiscounting(resultMapDsc)));
  }

  /**
   * The par spread for which the present value of the bill transaction is 0. If that spread was added to the transaction yield, the new transaction would have a present value of 0.
   * @param bill The bill transaction.
   * @param issuer The issuer and multi-curves provider.
   * @return The spread.
   */
  public double parSpread(final BillTransaction bill, final IssuerProviderInterface issuer) {
    ArgumentChecker.notNull(bill, "Bill");
    ArgumentChecker.notNull(issuer, "Issuer and multi-curves provider");
    Currency ccy = bill.getCurrency();
    double dfCreditEnd = issuer.getDiscountFactor(bill.getBillPurchased().getIssuerCcy(), bill.getBillPurchased().getEndTime());
    double dfDscSettle = issuer.getMulticurveProvider().getDiscountFactor(ccy, bill.getBillPurchased().getSettlementTime());
    double pricePar = dfCreditEnd / dfDscSettle;
    return METHOD_SECURITY.yieldFromPrice(bill.getBillPurchased(), pricePar)
        - METHOD_SECURITY.yieldFromPrice(bill.getBillPurchased(), -bill.getSettlementAmount() / (bill.getQuantity() * bill.getBillPurchased().getNotional()));
  }

  /**
   * The par spread curve sensitivity.
   * @param bill The bill transaction.
   * @param issuer The issuer and multi-curves provider.
   * @return The curve sensitivity.
   */
  public MulticurveSensitivity parSpreadCurveSensitivity(final BillTransaction bill, final IssuerProviderInterface issuer) {
    ArgumentChecker.notNull(bill, "Bill");
    ArgumentChecker.notNull(issuer, "Issuer and multi-curves provider");
    Currency ccy = bill.getCurrency();
    Pair<String, Currency> issuerCcy = bill.getBillPurchased().getIssuerCcy();
    double dfCreditEnd = issuer.getDiscountFactor(issuerCcy, bill.getBillPurchased().getEndTime());
    double dfDscSettle = issuer.getMulticurveProvider().getDiscountFactor(ccy, bill.getBillPurchased().getSettlementTime());
    double pricePar = dfCreditEnd / dfDscSettle;
    // Backward sweep
    double spreadBar = 1.0;
    double priceParBar = METHOD_SECURITY.yieldFromPriceDerivative(bill.getBillPurchased(), pricePar) * spreadBar;
    double dfDscSettleBar = -dfCreditEnd / (dfDscSettle * dfDscSettle) * priceParBar;
    double dfCreditEndBar = priceParBar / dfDscSettle;
    final Map<String, List<DoublesPair>> resultMapCredit = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listCredit = new ArrayList<DoublesPair>();
    listCredit.add(new DoublesPair(bill.getBillPurchased().getEndTime(), -bill.getBillPurchased().getEndTime() * dfCreditEnd * dfCreditEndBar));
    resultMapCredit.put(issuer.getName(issuerCcy), listCredit);
    MulticurveSensitivity result = MulticurveSensitivity.ofYieldDiscounting(resultMapCredit);
    final Map<String, List<DoublesPair>> resultMapDsc = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listDsc = new ArrayList<DoublesPair>();
    listDsc.add(new DoublesPair(bill.getBillPurchased().getSettlementTime(), -bill.getBillPurchased().getSettlementTime() * dfDscSettle * dfDscSettleBar));
    resultMapDsc.put(issuer.getMulticurveProvider().getName(ccy), listDsc);
    return result.plus(MulticurveSensitivity.ofYieldDiscounting(resultMapDsc));
  }

}