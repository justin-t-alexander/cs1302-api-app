package cs1302.api;




/**
 *deals with getting lat and lon form geo api.
 */
public class GeocodingResponse {
    private String displayName;
    private double lat;
    private double lon;


    /**
     *constructor.
     *@param displayName
     *@param lat
     *@param lon
     */
    public GeocodingResponse(String displayName, double lat, double lon) {
        this.displayName = displayName;
        this.lat = lat;
        this.lon = lon;
    }


    /**
     *gets displayName.
     *@return displayName
     */
    public String getDisplayName() {
        return displayName;
    }


    /**
     *gets lat.
     *@return lat
     */
    public double getLat() {
        return lat;
    }


    /**
     *gets lon.
     *@return lon
     */
    public double getLon() {
        return lon;
    }
}
