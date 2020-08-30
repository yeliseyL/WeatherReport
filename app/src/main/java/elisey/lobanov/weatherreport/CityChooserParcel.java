package elisey.lobanov.weatherreport;

import java.io.Serializable;

public class CityChooserParcel implements Serializable {
    private static CityChooserParcel instance = null;
    private static final Object syncObj = new Object();
    private String cityName;
    private boolean isWindSpeedVisible;
    private boolean isPressureVisible;

    private CityChooserParcel() {
        this.cityName = "Moscow";
        this.isWindSpeedVisible = false;
        this.isPressureVisible = false;
    }

    public String getCityName() {
        return cityName;
    }

    public boolean isWindSpeedVisible() {
        return isWindSpeedVisible;
    }

    public boolean isPressureVisible() {
        return isPressureVisible;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setWindSpeedVisible(boolean windSpeedVisible) {
        isWindSpeedVisible = windSpeedVisible;
    }

    public void setPressureVisible(boolean pressureVisible) {
        isPressureVisible = pressureVisible;
    }

    public static CityChooserParcel getInstance(){
        synchronized (syncObj) {
            if (instance == null) {
                instance = new CityChooserParcel();
            }
            return instance;
        }
    }
}
