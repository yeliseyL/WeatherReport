package elisey.lobanov.weatherreport;

public final class MainPresenter {

    private static MainPresenter instance = null;
    private static final Object syncObj = new Object();
    private boolean showWindSpeed;
    private boolean showAtmPressure;
    private String cityName;

    private MainPresenter(){
        showWindSpeed = false;
        showAtmPressure = false;
        cityName = "Moscow";
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public boolean isShowWindSpeed() {
        return showWindSpeed;
    }

    public void setShowWindSpeed(boolean showWindSpeed) {
        this.showWindSpeed = showWindSpeed;
    }

    public boolean isShowAtmPressure() {
        return showAtmPressure;
    }

    public void setShowAtmPressure(boolean showAtmPressure) {
        this.showAtmPressure = showAtmPressure;
    }

    public static MainPresenter getInstance(){
        synchronized (syncObj) {
            if (instance == null) {
                instance = new MainPresenter();
            }
            return instance;
        }
    }

}
