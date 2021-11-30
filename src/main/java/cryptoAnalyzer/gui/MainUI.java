package cryptoAnalyzer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField.AbstractFormatter;

import com.google.gson.JsonObject;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeriesCollection;

import cryptoAnalyzer.utils.AvailableCryptoList;
import cryptoAnalyzer.utils.DataVisualizationCreator;
import cryptoAnalyzer.utils.PercentPrice;

public class MainUI extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static MainUI instance;
	private JPanel stats, chartPanel, tablePanel;
	
	// Should be a reference to a separate object in actual implementation
	private List<String> selectedList;
	private List<String> dateList;
	
	private JTextArea selectedCryptoList;
	private JComboBox<String> cryptoList;
	private JComboBox<String> analysisList;
	private UtilDateModel dateModel;

	public static MainUI getInstance() {
		if (instance == null)
			instance = new MainUI();

		return instance;
	}

	private MainUI() {
		
		// Set window title
		super("Crypto Analysis Tool");

		// Set top bar
		JLabel chooseCountryLabel = new JLabel("Choose a cryptocurrency: ");
		
		//gathers crypto list 
		String[] cryptoNames = AvailableCryptoList.getInstance().getAvailableCryptos();
		cryptoList = new JComboBox<String>(cryptoNames);
		String[] analysisNames = {"volume","price"};
		analysisList = new JComboBox<String>(analysisNames);
		
		selectedList = new ArrayList<>();
		dateList = new ArrayList<>();
		
		JButton addCrypto = new JButton("+");
		addCrypto.setActionCommand("add");
		addCrypto.addActionListener(this);
		
		JButton removeCrypto = new JButton("-");
		removeCrypto.setActionCommand("remove");
		removeCrypto.addActionListener(this);

		JButton selectAnalysis = new JButton("select");
		selectAnalysis.setActionCommand("select");
		selectAnalysis.addActionListener(this);
		


		JPanel north = new JPanel();
		north.add(chooseCountryLabel);
		north.add(cryptoList);
		north.add(addCrypto);
		north.add(removeCrypto);
		north.add(analysisList);
		north.add(selectAnalysis);
		

		// Set bottom bar
		JLabel from = new JLabel("From");
		dateModel = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(dateModel, p);
		@SuppressWarnings("serial")
		JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new AbstractFormatter() {
			private String datePatern = "dd/MM/yyyy";

		    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePatern);

		    @Override
		    public Object stringToValue(String text) throws ParseException {
		        return dateFormatter.parseObject(text);
		    }

		    @Override
		    public String valueToString(Object value) throws ParseException {
		        if (value != null) {
		            Calendar cal = (Calendar) value;
		            return dateFormatter.format(cal.getTime());
		        }

		        return "";
		    }
		});
		
		
		JButton refresh = new JButton("Refresh");
		refresh.setActionCommand("refresh");
		refresh.addActionListener(this);

		JLabel metricsLabel = new JLabel("        Metrics: ");

		Vector<String> metricsNames = new Vector<String>();
		metricsNames.add("Price");
		metricsNames.add("MarketCap");
		metricsNames.add("Volume");
		metricsNames.add("Coins in circulation");
		JComboBox<String> metricsList = new JComboBox<String>(metricsNames);

		JLabel intervalLabel = new JLabel("        Choose interval: ");

		Vector<String> intervalNames = new Vector<String>();
		intervalNames.add("Daily");
		intervalNames.add("Weekly");
		intervalNames.add("Monthly");
		intervalNames.add("Yearly");

		JComboBox<String> intervalList = new JComboBox<String>(intervalNames);

		JPanel south = new JPanel();
		south.add(from);
		south.add(datePicker);
		
		south.add(metricsLabel);
		south.add(metricsList);

		south.add(intervalLabel);
		south.add(intervalList);
		south.add(refresh);

		
		JLabel selectedCryptoListLabel = new JLabel("List of selected cryptocurrencies: ");
		selectedCryptoList = new JTextArea(16, 10);
		JScrollPane selectedCryptoScrollPane = new JScrollPane(selectedCryptoList);
		JPanel east = new JPanel();
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
		east.add(selectedCryptoListLabel);
		east.add(selectedCryptoScrollPane);

		// Set charts region
		JPanel west = new JPanel();
		west.setPreferredSize(new Dimension(1250,650));
		stats = new JPanel();
		stats.setLayout(new GridLayout(2, 2));
		
		west.add(stats);

		getContentPane().add(north, BorderLayout.NORTH);
		getContentPane().add(east, BorderLayout.EAST);
		getContentPane().add(south, BorderLayout.SOUTH);
		getContentPane().add(west, BorderLayout.WEST);
	}
	
	public void updateStats(JComponent component) {
		stats.add(component);
		stats.revalidate();
	}

	public static LocalDate getDate(UtilDateModel dateModel){
		String date = dateModel.getValue().toString();
		String month = date.substring(4, 8);
		String day = date.substring(8, 11);
		String year = date.substring(24,28);
		String formattedDate = day.concat(month).concat(year);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);
		LocalDate dateTime = LocalDate.parse(formattedDate, formatter);
		return dateTime;
	}

	public static void main(String[] args) {
		JFrame frame = MainUI.getInstance();
		frame.setSize(900, 600);
		frame.pack();
		frame.setVisible(true);
	}


	public static ArrayList<ArrayList<ArrayList<String>>> getTable(LocalDate date,String Analysis, ArrayList<String> selectedList){
			PercentPrice analysis = new PercentPrice(date,LocalDate.now(),selectedList);
			ArrayList<ArrayList<ArrayList<String>>> out = analysis.getTableData();
			return out;
			

	}
	public static TimeSeriesCollection getTimeSeries(LocalDate date,String Analysis, ArrayList<String> selectedList){
		PercentPrice analysis = new PercentPrice(date,LocalDate.now(),selectedList);
		TimeSeriesCollection out = analysis.createTimeSeries();
		return out;
		

}

public static DefaultCategoryDataset getDataBar(LocalDate date,String Analysis, ArrayList<String> selectedList){
	PercentPrice analysis = new PercentPrice(date,LocalDate.now(),selectedList);
	DefaultCategoryDataset out = analysis.createBarGraph();
	return out;
	

}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if ("refresh".equals(command)) {

			
			LocalDate date = getDate(dateModel);
			DataVisualizationCreator creator = new DataVisualizationCreator();
			ArrayList<ArrayList<ArrayList<String>>> table = getTable(date, "test", (ArrayList<String>) selectedList);
			TimeSeriesCollection series = getTimeSeries(date, "test", (ArrayList<String>) selectedList);
			DefaultCategoryDataset dataBar = getDataBar(date, "test", (ArrayList<String>) selectedList);
			creator.createCharts(table,series,dataBar);


		} else if ("add".equals(command)) {
			selectedList.add(cryptoList.getSelectedItem().toString());
			String text = "";
			for (String crypto: selectedList)
				text += crypto + "\n";
			
			selectedCryptoList.setText(text);
		} else if ("remove".equals(command)) {
			selectedList.remove(cryptoList.getSelectedItem());
			String text = "";
			for (String crypto: selectedList)
				text += crypto + "\n";
			
			selectedCryptoList.setText(text);
		}
	}

	

}
