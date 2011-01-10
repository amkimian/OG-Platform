/**
 * Copyright (C) 2009 - Present by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.web.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.joda.beans.BeanDefinition;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicMetaBean;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectMetaProperty;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.master.config.ConfigDocument;
import com.opengamma.master.config.ConfigMaster;
import com.opengamma.master.config.ConfigTypeMaster;

/**
 * Data class for web-based configuration management.
 * @param <T>  the config element type
 */
@BeanDefinition
public class WebConfigData<T> extends DirectBean {

  /**
   * The config master.
   */
  @PropertyDefinition
  private ConfigMaster _configMaster;
  /**
   * The valid map of types.
   */
  @PropertyDefinition(set = "setClearPutAll")
  private final BiMap<String, Class<?>> _typeMap = HashBiMap.create();
  /**
   * The JSR-311 URI information.
   */
  @PropertyDefinition
  private UriInfo _uriInfo;
  /**
   * The type of data being stored.
   */
  @PropertyDefinition
  private Class<T> _type;
  /**
   * The config id from the input URI.
   */
  @PropertyDefinition
  private String _uriConfigId;
  /**
   * The version id from the URI.
   */
  @PropertyDefinition
  private String _uriVersionId;
  /**
   * The config.
   * The {@code Object} type is necessary to handle generics in a practical way.
   */
  @PropertyDefinition
  private ConfigDocument<T> _config;
  /**
   * The versioned config.
   * The {@code Object} type is necessary to handle generics in a practical way.
   */
  @PropertyDefinition
  private ConfigDocument<T> _versioned;

  /**
   * Creates an instance.
   */
  public WebConfigData() {
  }

  /**
   * Creates an instance.
   * @param uriInfo  the URI information
   */
  public WebConfigData(final UriInfo uriInfo) {
    setUriInfo(uriInfo);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the best available config id.
   * @param overrideId  the override id, null derives the result from the data
   * @return the id, may be null
   */
  public String getBestConfigUriId(final UniqueIdentifier overrideId) {
    if (overrideId != null) {
      return overrideId.toLatest().toString();
    }
    return getConfig() != null ? getConfig().getUniqueId().toLatest().toString() : getUriConfigId();
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the config master.
   * @return the value of the property
   */
  public ConfigTypeMaster<T> getConfigTypeMaster() {
    return _configMaster.typed(getType());
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code WebConfigData}.
   * @param <R>  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R> WebConfigData.Meta<R> meta() {
    return WebConfigData.Meta.INSTANCE;
  }

  @SuppressWarnings("unchecked")
  @Override
  public WebConfigData.Meta<T> metaBean() {
    return WebConfigData.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName) {
    switch (propertyName.hashCode()) {
      case 10395716:  // configMaster
        return getConfigMaster();
      case -853107774:  // typeMap
        return getTypeMap();
      case -173275078:  // uriInfo
        return getUriInfo();
      case 3575610:  // type
        return getType();
      case -2037268087:  // uriConfigId
        return getUriConfigId();
      case 666567687:  // uriVersionId
        return getUriVersionId();
      case -1354792126:  // config
        return getConfig();
      case -1407102089:  // versioned
        return getVersioned();
    }
    return super.propertyGet(propertyName);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue) {
    switch (propertyName.hashCode()) {
      case 10395716:  // configMaster
        setConfigMaster((ConfigMaster) newValue);
        return;
      case -853107774:  // typeMap
        setTypeMap((BiMap<String, Class<?>>) newValue);
        return;
      case -173275078:  // uriInfo
        setUriInfo((UriInfo) newValue);
        return;
      case 3575610:  // type
        setType((Class<T>) newValue);
        return;
      case -2037268087:  // uriConfigId
        setUriConfigId((String) newValue);
        return;
      case 666567687:  // uriVersionId
        setUriVersionId((String) newValue);
        return;
      case -1354792126:  // config
        setConfig((ConfigDocument<T>) newValue);
        return;
      case -1407102089:  // versioned
        setVersioned((ConfigDocument<T>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the config master.
   * @return the value of the property
   */
  public ConfigMaster getConfigMaster() {
    return _configMaster;
  }

  /**
   * Sets the config master.
   * @param configMaster  the new value of the property
   */
  public void setConfigMaster(ConfigMaster configMaster) {
    this._configMaster = configMaster;
  }

  /**
   * Gets the the {@code configMaster} property.
   * @return the property, not null
   */
  public final Property<ConfigMaster> configMaster() {
    return metaBean().configMaster().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the valid map of types.
   * @return the value of the property
   */
  public BiMap<String, Class<?>> getTypeMap() {
    return _typeMap;
  }

  /**
   * Sets the valid map of types.
   * @param typeMap  the new value of the property
   */
  public void setTypeMap(BiMap<String, Class<?>> typeMap) {
    this._typeMap.clear();
    this._typeMap.putAll(typeMap);
  }

  /**
   * Gets the the {@code typeMap} property.
   * @return the property, not null
   */
  public final Property<BiMap<String, Class<?>>> typeMap() {
    return metaBean().typeMap().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the JSR-311 URI information.
   * @return the value of the property
   */
  public UriInfo getUriInfo() {
    return _uriInfo;
  }

  /**
   * Sets the JSR-311 URI information.
   * @param uriInfo  the new value of the property
   */
  public void setUriInfo(UriInfo uriInfo) {
    this._uriInfo = uriInfo;
  }

  /**
   * Gets the the {@code uriInfo} property.
   * @return the property, not null
   */
  public final Property<UriInfo> uriInfo() {
    return metaBean().uriInfo().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the type of data being stored.
   * @return the value of the property
   */
  public Class<T> getType() {
    return _type;
  }

  /**
   * Sets the type of data being stored.
   * @param type  the new value of the property
   */
  public void setType(Class<T> type) {
    this._type = type;
  }

  /**
   * Gets the the {@code type} property.
   * @return the property, not null
   */
  public final Property<Class<T>> type() {
    return metaBean().type().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the config id from the input URI.
   * @return the value of the property
   */
  public String getUriConfigId() {
    return _uriConfigId;
  }

  /**
   * Sets the config id from the input URI.
   * @param uriConfigId  the new value of the property
   */
  public void setUriConfigId(String uriConfigId) {
    this._uriConfigId = uriConfigId;
  }

  /**
   * Gets the the {@code uriConfigId} property.
   * @return the property, not null
   */
  public final Property<String> uriConfigId() {
    return metaBean().uriConfigId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the version id from the URI.
   * @return the value of the property
   */
  public String getUriVersionId() {
    return _uriVersionId;
  }

  /**
   * Sets the version id from the URI.
   * @param uriVersionId  the new value of the property
   */
  public void setUriVersionId(String uriVersionId) {
    this._uriVersionId = uriVersionId;
  }

  /**
   * Gets the the {@code uriVersionId} property.
   * @return the property, not null
   */
  public final Property<String> uriVersionId() {
    return metaBean().uriVersionId().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the config.
   * The {@code Object} type is necessary to handle generics in a practical way.
   * @return the value of the property
   */
  public ConfigDocument<T> getConfig() {
    return _config;
  }

  /**
   * Sets the config.
   * The {@code Object} type is necessary to handle generics in a practical way.
   * @param config  the new value of the property
   */
  public void setConfig(ConfigDocument<T> config) {
    this._config = config;
  }

  /**
   * Gets the the {@code config} property.
   * The {@code Object} type is necessary to handle generics in a practical way.
   * @return the property, not null
   */
  public final Property<ConfigDocument<T>> config() {
    return metaBean().config().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the versioned config.
   * The {@code Object} type is necessary to handle generics in a practical way.
   * @return the value of the property
   */
  public ConfigDocument<T> getVersioned() {
    return _versioned;
  }

  /**
   * Sets the versioned config.
   * The {@code Object} type is necessary to handle generics in a practical way.
   * @param versioned  the new value of the property
   */
  public void setVersioned(ConfigDocument<T> versioned) {
    this._versioned = versioned;
  }

  /**
   * Gets the the {@code versioned} property.
   * The {@code Object} type is necessary to handle generics in a practical way.
   * @return the property, not null
   */
  public final Property<ConfigDocument<T>> versioned() {
    return metaBean().versioned().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code WebConfigData}.
   */
  public static class Meta<T> extends BasicMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code configMaster} property.
     */
    private final MetaProperty<ConfigMaster> _configMaster = DirectMetaProperty.ofReadWrite(this, "configMaster", ConfigMaster.class);
    /**
     * The meta-property for the {@code typeMap} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<BiMap<String, Class<?>>> _typeMap = DirectMetaProperty.ofReadWrite(this, "typeMap", (Class) BiMap.class);
    /**
     * The meta-property for the {@code uriInfo} property.
     */
    private final MetaProperty<UriInfo> _uriInfo = DirectMetaProperty.ofReadWrite(this, "uriInfo", UriInfo.class);
    /**
     * The meta-property for the {@code type} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<Class<T>> _type = DirectMetaProperty.ofReadWrite(this, "type", (Class) Class.class);
    /**
     * The meta-property for the {@code uriConfigId} property.
     */
    private final MetaProperty<String> _uriConfigId = DirectMetaProperty.ofReadWrite(this, "uriConfigId", String.class);
    /**
     * The meta-property for the {@code uriVersionId} property.
     */
    private final MetaProperty<String> _uriVersionId = DirectMetaProperty.ofReadWrite(this, "uriVersionId", String.class);
    /**
     * The meta-property for the {@code config} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ConfigDocument<T>> _config = DirectMetaProperty.ofReadWrite(this, "config", (Class) ConfigDocument.class);
    /**
     * The meta-property for the {@code versioned} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ConfigDocument<T>> _versioned = DirectMetaProperty.ofReadWrite(this, "versioned", (Class) ConfigDocument.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<Object>> _map;

    @SuppressWarnings({"unchecked", "rawtypes" })
    protected Meta() {
      LinkedHashMap temp = new LinkedHashMap();
      temp.put("configMaster", _configMaster);
      temp.put("typeMap", _typeMap);
      temp.put("uriInfo", _uriInfo);
      temp.put("type", _type);
      temp.put("uriConfigId", _uriConfigId);
      temp.put("uriVersionId", _uriVersionId);
      temp.put("config", _config);
      temp.put("versioned", _versioned);
      _map = Collections.unmodifiableMap(temp);
    }

    @Override
    public WebConfigData<T> createBean() {
      return new WebConfigData<T>();
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends WebConfigData<T>> beanType() {
      return (Class) WebConfigData.class;
    }

    @Override
    public Map<String, MetaProperty<Object>> metaPropertyMap() {
      return _map;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code configMaster} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ConfigMaster> configMaster() {
      return _configMaster;
    }

    /**
     * The meta-property for the {@code typeMap} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<BiMap<String, Class<?>>> typeMap() {
      return _typeMap;
    }

    /**
     * The meta-property for the {@code uriInfo} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<UriInfo> uriInfo() {
      return _uriInfo;
    }

    /**
     * The meta-property for the {@code type} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<Class<T>> type() {
      return _type;
    }

    /**
     * The meta-property for the {@code uriConfigId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uriConfigId() {
      return _uriConfigId;
    }

    /**
     * The meta-property for the {@code uriVersionId} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> uriVersionId() {
      return _uriVersionId;
    }

    /**
     * The meta-property for the {@code config} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ConfigDocument<T>> config() {
      return _config;
    }

    /**
     * The meta-property for the {@code versioned} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<ConfigDocument<T>> versioned() {
      return _versioned;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
