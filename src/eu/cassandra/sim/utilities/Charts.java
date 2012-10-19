package eu.cassandra.sim.utilities;

import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Charts {

	public static void createHistogram (String title, String x, String y,
			Double[] data)
	{

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (int i = 0; i < data.length; i++) {
			dataset.addValue(data[i], y, (Comparable) i);
		}

		PlotOrientation orientation = PlotOrientation.VERTICAL;
		boolean show = false;
		boolean toolTips = false;
		boolean urls = false;

		JFreeChart chart =
				ChartFactory.createBarChart(title, x, y, dataset, orientation, show,
						toolTips, urls);
		int width = 1024;
		int height = 768;

		try {
			ChartUtilities.saveChartAsPNG(new File(title
					+ ".PNG"), chart, width, height);
		}
		catch (IOException e) {
		}

	}

}
