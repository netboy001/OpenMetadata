package org.umetadata.service.search.elasticsearch.dataInsightAggregators;

import java.util.LinkedHashMap;
import org.umetadata.schema.dataInsight.custom.DataInsightCustomChart;
import org.umetadata.schema.dataInsight.custom.LineChart;
import org.umetadata.schema.dataInsight.custom.SummaryCard;

public class ElasticSearchDynamicChartAggregatorFactory {
  public static ElasticSearchDynamicChartAggregatorInterface getAggregator(
      DataInsightCustomChart diChart) {
    if (((LinkedHashMap) diChart.getChartDetails())
        .get("type")
        .equals(LineChart.Type.LINE_CHART.value())) {
      return new ElasticSearchLineChartAggregator();
    } else if (((LinkedHashMap) diChart.getChartDetails())
        .get("type")
        .equals(SummaryCard.Type.SUMMARY_CARD.value())) {
      return new ElasticSearchSummaryCardAggregator();
    }
    return null;
  }
}
