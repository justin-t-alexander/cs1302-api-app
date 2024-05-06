package cs1302.api;




/**
 *collects period in an array.
 */
public class WeatherProperties {
    private WeatherPeriod[] periods;


    /**
     *constructor for periods.
     *@param periods
     */
    public WeatherProperties(WeatherPeriod[] periods) {
        this.periods = periods;
    }


    /**
     *gets weather period with weather info.
     *@return periods
     */
    public WeatherPeriod[] getPeriods() {
        return periods;
    }
}
