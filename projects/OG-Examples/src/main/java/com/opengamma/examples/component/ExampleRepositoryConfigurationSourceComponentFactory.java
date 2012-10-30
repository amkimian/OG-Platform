/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.examples.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.component.factory.source.RepositoryConfigurationSourceComponentFactory;
import com.opengamma.engine.function.config.RepositoryConfigurationSource;
import com.opengamma.examples.function.ExampleCubeFunctionConfiguration;
import com.opengamma.examples.function.ExampleCurveFunctionConfiguration;
import com.opengamma.examples.function.ExampleStandardFunctionConfiguration;
import com.opengamma.examples.function.ExampleSurfaceFunctionConfiguration;

/**
 * Component factory for the function configuration source.
 */
@BeanDefinition
public class ExampleRepositoryConfigurationSourceComponentFactory extends RepositoryConfigurationSourceComponentFactory {

  @Override
  protected List<RepositoryConfigurationSource> initSources() {
    List<RepositoryConfigurationSource> sources = new ArrayList<RepositoryConfigurationSource>();
    
    RepositoryConfigurationSource standardFunctionSource = ExampleStandardFunctionConfiguration.constructRepositoryConfigurationSource();
    sources.add(standardFunctionSource);
    
    ExampleCurveFunctionConfiguration curveFunctionConfig = new ExampleCurveFunctionConfiguration();
    curveFunctionConfig.setConfigMaster(getConfigMaster());
    curveFunctionConfig.setConventionBundleSource(getConventionBundleSource());
    RepositoryConfigurationSource curveFunctionSource = curveFunctionConfig.constructRepositoryConfigurationSource();
    sources.add(curveFunctionSource);
    
    ExampleSurfaceFunctionConfiguration surfaceFunctionConfig = new ExampleSurfaceFunctionConfiguration();
    surfaceFunctionConfig.setConfigMaster(getConfigMaster());
    RepositoryConfigurationSource surfaceFunctionSource = surfaceFunctionConfig.constructRepositoryConfigurationSource();
    sources.add(surfaceFunctionSource);
    
    ExampleCubeFunctionConfiguration cubeFunctionConfig = new ExampleCubeFunctionConfiguration();
    RepositoryConfigurationSource cubeFunctionSource = cubeFunctionConfig.constructRepositoryConfigurationSource();
    sources.add(cubeFunctionSource);
    
    return sources;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ExampleRepositoryConfigurationSourceComponentFactory}.
   * @return the meta-bean, not null
   */
  public static ExampleRepositoryConfigurationSourceComponentFactory.Meta meta() {
    return ExampleRepositoryConfigurationSourceComponentFactory.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(ExampleRepositoryConfigurationSourceComponentFactory.Meta.INSTANCE);
  }

  @Override
  public ExampleRepositoryConfigurationSourceComponentFactory.Meta metaBean() {
    return ExampleRepositoryConfigurationSourceComponentFactory.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      return super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ExampleRepositoryConfigurationSourceComponentFactory}.
   */
  public static class Meta extends RepositoryConfigurationSourceComponentFactory.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
      this, (DirectMetaPropertyMap) super.metaPropertyMap());

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    public BeanBuilder<? extends ExampleRepositoryConfigurationSourceComponentFactory> builder() {
      return new DirectBeanBuilder<ExampleRepositoryConfigurationSourceComponentFactory>(new ExampleRepositoryConfigurationSourceComponentFactory());
    }

    @Override
    public Class<? extends ExampleRepositoryConfigurationSourceComponentFactory> beanType() {
      return ExampleRepositoryConfigurationSourceComponentFactory.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}