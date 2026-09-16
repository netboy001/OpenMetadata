package org.umetadata.service.search.opensearch.dataInsightAggregator;

import java.util.LinkedHashMap;
import org.umetadata.schema.dataInsight.custom.DataInsightCustomChart;
import org.umetadata.schema.dataInsight.custom.LineChart;
import org.umetadata.schema.dataInsight.custom.SummaryCard;

public class OpenSearchDynamicChartAggregatorFactory {
  public static OpenSearchDynamicChartAggregatorInterface getAggregator(
      DataInsightCustomChart diChart) {
    if (((LinkedHashMap) diChart.getChartDetails())
        .get("type")
        .equals(LineChart.Type.LINE_CHART.value())) {
      return new OpenSearchLineChartAggregator();
    } else if (((LinkedHashMap) diChart.getChartDetails())
        .get("type")
        .equals(SummaryCard.Type.SUMMARY_CARD.value())) {
      return new OpenSearchSummaryCardAggregator();
    }
    return null;
  }
}
