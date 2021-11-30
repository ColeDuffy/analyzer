package cryptoAnalyzer.utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeriesCollection;
import cryptoAnalyzer.gui.MainUI;
public class DataVisualizationCreator {
	
	public void createCharts(ArrayList<ArrayList<ArrayList<String>>> data, TimeSeriesCollection series,DefaultCategoryDataset barData) {
		createTableOutput(data);
		createTimeSeries(series);
		createScatter(series);
		createBar(barData);
	}

	private void createTableOutput(ArrayList<ArrayList<ArrayList<String>>> data) {
		ArrayList dates = data.get(1).get(0);
		Object[] columnNames = dates.toArray();
		
		// Dummy data for demo purposes. These should come from actual fetcher
		ArrayList<ArrayList<String>> metrics = data.get(0);
		String[][] array = new String[metrics.size()][];
		for (int i = 0; i < metrics.size(); i++) {
				ArrayList<String> row = metrics.get(i);
				array[i] = row.toArray(new String[row.size()]);
			}
		// Object[][] stringArray = metrics.stream().map(u -> u.toArray(new Object[0])).toArray(Object[][]::new);
		Object[][] parsedData = array;
		

		JTable table = new JTable(parsedData, columnNames);
		table.setPreferredSize(new Dimension(600, 300));
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder (BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                "Daily Price Summary Table",
                TitledBorder.CENTER,
                TitledBorder.TOP));
		
	
		
		scrollPane.setPreferredSize(new Dimension(600, 300));
		table.setFillsViewportHeight(true);;
		
		MainUI.getInstance().updateStats(scrollPane);
	}

	private void createTimeSeries(TimeSeriesCollection data) {

		XYPlot plot = new XYPlot();
		XYSplineRenderer splinerenderer1 = new XYSplineRenderer();
		
		plot.setDataset(0, data);
		plot.setRenderer(0, splinerenderer1);
		DateAxis domainAxis = new DateAxis("");
		plot.setDomainAxis(domainAxis);
		plot.setRangeAxis(new LogAxis("Price(USD)"));
		
		JFreeChart chart = new JFreeChart("Daily Price Line Chart", new Font("Serif", java.awt.Font.BOLD, 18), plot,
				true);

		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(600, 300));
		chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		chartPanel.setBackground(Color.white);
		
		MainUI.getInstance().updateStats(chartPanel);
	}
	
	private void createScatter(TimeSeriesCollection data) {
		XYPlot plot = new XYPlot();
		XYItemRenderer itemrenderer1 = new XYLineAndShapeRenderer(false, true);

		plot.setDataset(0, data);
		plot.setRenderer(0, itemrenderer1);
		DateAxis domainAxis = new DateAxis("");
		plot.setDomainAxis(domainAxis);
		plot.setRangeAxis(new LogAxis("Price(USD)"));
		
		JFreeChart scatterChart = new JFreeChart("Daily Price Scatter Chart",
				new Font("Serif", java.awt.Font.BOLD, 18), plot, true);

		ChartPanel chartPanel = new ChartPanel(scatterChart);
		chartPanel.setPreferredSize(new Dimension(600, 300));
		chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		chartPanel.setBackground(Color.white);
		MainUI.getInstance().updateStats(chartPanel);
	}
	
	private void createBar(DefaultCategoryDataset data) {
		
		CategoryPlot plot = new CategoryPlot();
		BarRenderer barrenderer1 = new BarRenderer();

		plot.setDataset(0, data);
		plot.setRenderer(0, barrenderer1);
		CategoryAxis domainAxis = new CategoryAxis("Date");
		plot.setDomainAxis(domainAxis);
		LogAxis rangeAxis = new LogAxis("Price(USD)");
		rangeAxis.setRange(new Range(1.0, 70000.0));
		plot.setRangeAxis(rangeAxis);

		JFreeChart barChart = new JFreeChart("Daily Price Bar Chart", new Font("Serif", java.awt.Font.BOLD, 18), plot,
				true);

		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new Dimension(600, 300));
		chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		chartPanel.setBackground(Color.white);
		MainUI.getInstance().updateStats(chartPanel);
	}

}
