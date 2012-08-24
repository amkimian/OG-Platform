/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.web.server.push.analytics;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.web.server.push.analytics.formatting.ResultsFormatter;

/**
 * Creates a JSON object from an instance of {@link ViewportResults}.
 */
public class ViewportResultsJsonWriter {

  public static final String VERSION = "version";

  private static final String VALUE_KEY = "v";
  private static final String HISTORY_KEY = "h";
  private static final String TYPE_KEY = "t";
  private static final String DATA = "data";

  private final ResultsFormatter _formatter;

  public ViewportResultsJsonWriter(ResultsFormatter formatter) {
    _formatter = formatter;
  }

  public String getJson(ViewportResults results) {
    List<List<ViewportResults.Cell>> viewportCells = results.getResults();
    List<List<Object>> allResults = Lists.newArrayListWithCapacity(viewportCells.size());
    for (List<ViewportResults.Cell> rowCells : viewportCells) {
      List<Object> rowResults = Lists.newArrayListWithCapacity(rowCells.size());
      int viewportColIndex = 0;
      for (ViewportResults.Cell cell : rowCells) {
        Object formattedValue;
        Object cellValue = cell.getValue();
        ValueSpecification cellValueSpec = cell.getValueSpecification();
        if (results.isExpanded()) {
          formattedValue = _formatter.formatForExpandedDisplay(cellValue, cellValueSpec);
        } else {
          formattedValue = _formatter.formatForDisplay(cellValue, cellValueSpec);
        }
        Collection<Object> history = cell.getHistory();
        Class<?> columnType = results.getColumnType(viewportColIndex++);

        if (columnType == null || history != null) {
          // if there is history or we need to send type info then we need to send an object, not just the value
          Map<String, Object> valueMap = Maps.newHashMap();
          valueMap.put(VALUE_KEY, formattedValue);
          // if the the column type isn't known then send the type with the value
          if (columnType == null) {
            Class<?> cellValueClass = cellValue == null ? null : cellValue.getClass();
            valueMap.put(TYPE_KEY, _formatter.getFormatType(cellValueClass).name());
          }
          if (history != null) {
            List<Object> formattedHistory = Lists.newArrayListWithCapacity(history.size());
            for (Object historyValue : history) {
              formattedHistory.add(_formatter.formatForHistory(historyValue, cellValueSpec));
            }
            valueMap.put(HISTORY_KEY, formattedHistory);
          }
          rowResults.add(valueMap);
        } else {
          rowResults.add(formattedValue);
        }
      }
      allResults.add(rowResults);
    }
    ImmutableMap<String, Object> resultsMap = ImmutableMap.of(VERSION, results.getVersion(), DATA, allResults);
    return new JSONObject(resultsMap).toString();
  }
}
