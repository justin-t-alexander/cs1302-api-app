package cs1302.api;





/**
 *encapsulates weather api response.
 */
public class WeatherResponse {
    private WeatherProperties properties;



    /**
     *constructor.
     *@param properties
     */
    public WeatherResponse(WeatherProperties properties) {
        this.properties = properties;
    }



    /**
     *gets properties.
     *@return properties
     */
    public WeatherProperties getProperties() {
        return properties;
    }
}
