package elisey.lobanov.weatherreport;

import java.util.ArrayList;

public class HistoryHandler {
    private static HistoryHandler instance;
    private static final Object syncObj = new Object();
    private ArrayList<String> cities = new ArrayList<>();
    private ArrayList<String> temps = new ArrayList<>();

    public ArrayList<String> getCities() {
        return cities;
    }

    public ArrayList<String> getTemps() {
        return temps;
    }

    public void setHistoryEntry(String city, String temp) {
        cities.add(city);
        temps.add(temp);
    }

    public static HistoryHandler getInstance(){
        synchronized (syncObj) {
            if (instance == null) {
                instance = new HistoryHandler();
            }
            return instance;
        }
    }
}
