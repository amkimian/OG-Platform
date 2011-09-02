/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.security.future;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.util.money.Currency;
import com.opengamma.util.time.Expiry;

/**
 * A security for FX futures.
 */
@BeanDefinition
public class FXFutureSecurity extends FutureSecurity {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The numerator currency.
   */
  @PropertyDefinition(validate = "notNull")
  private Currency _numerator;
  /**
   * The denominator currency.
   */
  @PropertyDefinition(validate = "notNull")
  private Currency _denominator;
  /**
   * The multiplication factor.
   */
  @PropertyDefinition
  private double _multiplicationFactor = 1.0;

  /**
   * Creates an empty instance.
   * <p>
   * The security details should be set before use.
   */
  public FXFutureSecurity() {
    super();
  }

  public FXFutureSecurity(Expiry expiry, String tradingExchange, String settlementExchange, Currency currency, double unitAmount,
      Currency numerator, Currency denominator) {
    super(expiry, tradingExchange, settlementExchange, currency, unitAmount);
    setNumerator(numerator);
    setDenominator(denominator);
  }

  //-------------------------------------------------------------------------
  @Override
  public <T> T accept(FutureSecurityVisitor<T> visitor) {
    return visitor.visitFXFutureSecurity(this);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FXFutureSecurity}.
   * @return the meta-bean, not null
   */
  public static FXFutureSecurity.Meta meta() {
    return FXFutureSecurity.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(FXFutureSecurity.Meta.INSTANCE);
  }

  @Override
  public FXFutureSecurity.Meta metaBean() {
    return FXFutureSecurity.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1747334793:  // numerator
        return getNumerator();
      case -1983274394:  // denominator
        return getDenominator();
      case 866676853:  // multiplicationFactor
        return getMultiplicationFactor();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1747334793:  // numerator
        setNumerator((Currency) newValue);
        return;
      case -1983274394:  // denominator
        setDenominator((Currency) newValue);
        return;
      case 866676853:  // multiplicationFactor
        setMultiplicationFactor((Double) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_numerator, "numerator");
    JodaBeanUtils.notNull(_denominator, "denominator");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FXFutureSecurity other = (FXFutureSecurity) obj;
      return JodaBeanUtils.equal(getNumerator(), other.getNumerator()) &&
          JodaBeanUtils.equal(getDenominator(), other.getDenominator()) &&
          JodaBeanUtils.equal(getMultiplicationFactor(), other.getMultiplicationFactor()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getNumerator());
    hash += hash * 31 + JodaBeanUtils.hashCode(getDenominator());
    hash += hash * 31 + JodaBeanUtils.hashCode(getMultiplicationFactor());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the numerator currency.
   * @return the value of the property, not null
   */
  public Currency getNumerator() {
    return _numerator;
  }

  /**
   * Sets the numerator currency.
   * @param numerator  the new value of the property, not null
   */
  public void setNumerator(Currency numerator) {
    JodaBeanUtils.notNull(numerator, "numerator");
    this._numerator = numerator;
  }

  /**
   * Gets the the {@code numerator} property.
   * @return the property, not null
   */
  public final Property<Currency> numerator() {
    return metaBean().numerator().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the denominator currency.
   * @return the value of the property, not null
   */
  public Currency getDenominator() {
    return _denominator;
  }

  /**
   * Sets the denominator currency.
   * @param denominator  the new value of the property, not null
   */
  public void setDenominator(Currency denominator) {
    JodaBeanUtils.notNull(denominator, "denominator");
    this._denominator = denominator;
  }

  /**
   * Gets the the {@code denominator} property.
   * @return the property, not null
   */
  public final Property<Currency> denominator() {
    return metaBean().denominator().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the multiplication factor.
   * @return the value of the property
   */
  public double getMultiplicationFactor() {
    return _multiplicationFactor;
  }

  /**
   * Sets the multiplication factor.
   * @param multiplicationFactor  the new value of the property
   */
  public void setMultiplicationFactor(double multiplicationFactor) {
    this._multiplicationFactor = multiplicationFactor;
  }

  /**
   * Gets the the {@code multiplicationFactor} property.
   * @return the property, not null
   */
  public final Property<Double> multiplicationFactor() {
    return metaBean().multiplicationFactor().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FXFutureSecurity}.
   */
  public static class Meta extends FutureSecurity.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code numerator} property.
     */
    private final MetaProperty<Currency> _numerator = DirectMetaProperty.ofReadWrite(
        this, "numerator", FXFutureSecurity.class, Currency.class);
    /**
     * The meta-property for the {@code denominator} property.
     */
    private final MetaProperty<Currency> _denominator = DirectMetaProperty.ofReadWrite(
        this, "denominator", FXFutureSecurity.class, Currency.class);
    /**
     * The meta-property for the {@code multiplicationFactor} property.
     */
    private final MetaProperty<Double> _multiplicationFactor = DirectMetaProperty.ofReadWrite(
        this, "multiplicationFactor", FXFutureSecurity.class, Double.TYPE);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "numerator",
        "denominator",
        "multiplicationFactor");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1747334793:  // numerator
          return _numerator;
        case -1983274394:  // denominator
          return _denominator;
        case 866676853:  // multiplicationFactor
          return _multiplicationFactor;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends FXFutureSecurity> builder() {
      return new DirectBeanBuilder<FXFutureSecurity>(new FXFutureSecurity());
    }

    @Override
    public Class<? extends FXFutureSecurity> beanType() {
      return FXFutureSecurity.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code numerator} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> numerator() {
      return _numerator;
    }

    /**
     * The meta-property for the {@code denominator} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Currency> denominator() {
      return _denominator;
    }

    /**
     * The meta-property for the {@code multiplicationFactor} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Double> multiplicationFactor() {
      return _multiplicationFactor;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
