package View;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.List;

public class MonitorChart {

    private final JFreeChart chart;

    public MonitorChart(JFreeChart chart) {
        this.chart = chart;
    }

    public JFreeChart getChart() {
        return chart;
    }

    public void updateDataset(List<String> items, List<String> categories, List<Double> values) {
        assert items.size() == values.size();
        assert items.size() == categories.size();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < items.size(); i++) {
            dataset.addValue(values.get(i), categories.get(i), items.get(i));
        }
        chart.getCategoryPlot().setDataset(dataset);
    }

    public void updateDataset(List<String> items, List<Double> values) {
        assert items.size() == values.size();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < items.size(); i++) {
            dataset.addValue(values.get(i), "", items.get(i));
        }
        chart.getCategoryPlot().setDataset(dataset);
    }

}
