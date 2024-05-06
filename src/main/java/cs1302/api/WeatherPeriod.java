package cs1302.api;



/**
 *Getters to retrieve parsed weather info.
 */

// WeatherPeriod.java
public class WeatherPeriod {
    private String name;
    private String shortForecast;
    private String detailedForecast;
    private int temperature;
    private String windSpeed;
    private String windDirection;
    private int humidity;


    /**
     *Constructor intializing weather info.
     *@param name
     *@param shortForecast
     *@param detailedForecast
     *@param temperature
     @param windSpeed
     @param windDirection
     @param humidity
     */
    public WeatherPeriod(String name, String shortForecast,
        String detailedForecast, int temperature, String
        windSpeed, String windDirection, int humidity) {
        this.name = name;
        this.shortForecast = shortForecast;
        this.detailedForecast = detailedForecast;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.humidity = humidity;
    }


    /**
     *gets name.
     *@return name
     */
    public String getName() {
        return name;
    }


    /**
     *gets shortforecast.
     @return shortForecast
    */
    public String getShortForecast() {
        return shortForecast;
    }


    /**
     *gets fetailed forecast.
     *@return detailedForecast
     */
    public String getDetailedForecast() {
        return detailedForecast;
    }


    /**
     *gets temperature.
     *@return temperature
     */
    public int getTemperature() {
        return temperature;
    }


    /**
     *gets windSpeed.
     *@return windSpeed
     */
    public String getWindSpeed() {
        return windSpeed;
    }


    /**
     *gets windDirection.
     *@return windDirection
     */
    public String getWindDirection() {
        return windDirection;
    }


    /**
     *gets humidity.
     *@return humidity
     */
    public int getHumidity() {
        return humidity;
    }
}
