package elisey.lobanov.weatherreport;

import java.io.Serializable;

public class CityChooserParcel implements Serializable {
    private String cityName;
    private boolean isWindSpeedVisible;
    private boolean isPressureVisible;

    public CityChooserParcel(String cityName, boolean isWindSpeedVisible, boolean isPressureVisible) {
        this.cityName = cityName;
        this.isWindSpeedVisible = isWindSpeedVisible;
        this.isPressureVisible = isPressureVisible;
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
}
