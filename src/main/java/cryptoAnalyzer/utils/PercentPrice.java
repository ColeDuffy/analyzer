package cryptoAnalyzer.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.json.impl.JSONObject;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

//import scala.collection.mutable.Set;

public class PercentPrice {
    LocalDate start;
    LocalDate end;
    ArrayList<String> selectedList;

    public PercentPrice(LocalDate start, LocalDate end, ArrayList<String> selectedList) {
        this.start = start;
        this.end = end;
        this.selectedList = selectedList;
    }

    private List<LocalDate> dateList() {

        LocalDate s = this.start;
        LocalDate e = this.end;
        List<LocalDate> totalDates = new ArrayList<>();
        while (!s.isAfter(e)) {
            totalDates.add(s);
            s = s.plusDays(1);
        }
        return totalDates;
    }

    public JsonObject fetchData() {
        DataFetcher fetcher = new DataFetcher();
        JsonArray currencyArray = new JsonArray();
        List<LocalDate> dates = dateList();
        JsonObject out = new JsonObject();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");

        for (int j = 0; j < this.selectedList.size(); j++) {
            JsonObject priceList = new JsonObject();
            JsonArray jsonlist = new JsonArray();
            for (int i = 0; i < dates.size(); i++) {
                JsonObject priceAtDate = new JsonObject();
                String formattedString = dates.get(i).format(formatter);
                double price = fetcher.getPriceForCoin(selectedList.get(j).toLowerCase(), formattedString);
                priceAtDate.addProperty("metric", price);
                priceAtDate.addProperty("date", formattedString);

                jsonlist.add(priceAtDate);
            }

            priceList.add("values", jsonlist);
            priceList.addProperty("currency", this.selectedList.get(j));
            currencyArray.add(priceList);

        }
        out.add("data", currencyArray);
        return out;

    }

    public ArrayList<ArrayList<ArrayList<String>>> getTableData() {
        JsonObject test = this.fetchData();
        JsonArray test2 = test.get("data").getAsJsonArray();
        ArrayList outPrice = new ArrayList<>();
        ArrayList outDates = new ArrayList<>();
        ArrayList out = new ArrayList<>();

        for (JsonElement t : test2) {
            JsonArray price = t.getAsJsonObject().get("values").getAsJsonArray();
            ArrayList<String> priceList = new ArrayList<>();
            ArrayList<String> dateList = new ArrayList<>();
            priceList.add(t.getAsJsonObject().get("currency").getAsString());
            dateList.add(t.getAsJsonObject().get("currency").getAsString());

            for (JsonElement val : price) {
                String p = val.getAsJsonObject().get("metric").getAsString();
                priceList.add(p);

                String d = val.getAsJsonObject().get("date").getAsString();
                dateList.add(d);

            }
            outPrice.add(priceList);
            outDates.add(dateList);
        }

        out.add(outPrice);
        out.add(outDates);
        return out;

    }
    public TimeSeriesCollection createTimeSeries(){
        PercentPrice analysis = new PercentPrice(this.start, this.end, this.selectedList);
        JsonObject test = analysis.fetchData();
        JsonArray data = test.get("data").getAsJsonArray();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (JsonElement t :data){
            JsonArray values = t.getAsJsonObject().get("values").getAsJsonArray();
            String currency = t.getAsJsonObject().get("currency").getAsString();
            TimeSeries series = new TimeSeries(currency);
            for(JsonElement j : values){
                String date = j.getAsJsonObject().get("date").getAsString();
                float metric = j.getAsJsonObject().get("metric").getAsFloat();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy", Locale.ENGLISH);
                LocalDate dateTime = LocalDate.parse(date, formatter);
                Integer day = dateTime.getDayOfMonth();
                Integer month = dateTime.getMonthValue();
                Integer year = dateTime.getYear();
                series.add(new Day(day,month,year),metric);

            }
            dataset.addSeries(series);
        }
        return dataset;


    }
    public DefaultCategoryDataset createBarGraph(){
        PercentPrice analysis = new PercentPrice(this.start, this.end, this.selectedList);
        JsonObject test = analysis.fetchData();
        JsonArray data = test.get("data").getAsJsonArray();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (JsonElement t :data){
            JsonArray values = t.getAsJsonObject().get("values").getAsJsonArray();
            String currency = t.getAsJsonObject().get("currency").getAsString();

            for(JsonElement j : values){
                String date = j.getAsJsonObject().get("date").getAsString();
                float metric = j.getAsJsonObject().get("metric").getAsFloat();
                dataset.setValue(metric,currency,date);

            }
        }
        return dataset;
    }
    
    public static void main(String args[]) {
        ArrayList<String> in = new ArrayList<String>();
        in.add("bitcoin");
        in.add("ethereum");
        LocalDate start = LocalDate.parse("2021-11-20");
        LocalDate end = LocalDate.parse("2021-11-20");
        PercentPrice analysis = new PercentPrice(start, end, in);
        TimeSeriesCollection test = analysis.createTimeSeries();

    }

}
