package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.HBox;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

import com.google.gson.Gson;


/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    HBox searchBox;
    private TilePane resultTilePane;

    private static final String GEOCODING_API_ENDPOINT =
        "https://nominatim.openstreetmap.org/search";

    private static final String WEATHER_API_ENDPOINT = "https://api.weather.gov/";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();




    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
    } // ApiApp



    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        TextField locationField = createLocationField();
        Button searchButton = createSearchButton(locationField);
        resultTilePane = createResultTilePane();

        root = createRootLayout(locationField, searchButton, resultTilePane);

        // setup scene

        scene = new Scene(root, 400, 200);

        // setup stage
        stage.setTitle("Weather App!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start




//creating layout


    /**
     * organizes vbox layout.
     * @param locationField
     *@param searchButton
     *@param resultTilePane
     *@return root
     */
    private VBox
        createRootLayout(TextField locationField, Button searchButton, TilePane resultTilePane) {
        Label titleLabel = new Label("Input either Atlanta or another Georgia city"
            + " followed by georgia to retrieve shortcast info!");
        searchBox = createSearchBox(locationField, searchButton);
        root = new VBox(10, titleLabel, searchBox, resultTilePane);
        root.setPadding(new Insets(10));
        return root;
    } //createRootLayout


    /**
     * creates top hbox.
     * @param locationField
     * @param searchButton
     * @return searchBox
     */
    private HBox createSearchBox(TextField locationField, Button searchButton) {
        searchBox = new HBox(10, new Label("Location: "), locationField, searchButton);
        return searchBox;
    } //createSearchBox





//Setup search button action



    /**
     * handle search action.
     *@param locationField
     */
    private void handleSearch(TextField locationField) {
        String location = locationField.getText();
        if (!location.isEmpty()) {
            try {
                GeocodingResponse[] geolocationResponses = performGeolocationRequest(location);
                if (geolocationResponses.length > 0) {
                    WeatherResponse weather = performWeatherRequest(geolocationResponses[0]);
                    updateUIWithWeatherInfo(weather, resultTilePane);
                } else {
                    System.out.println("No results found for the location: " + location);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please enter a location");
        }
    } //handleSearch



    /**
     * handled grabbing location from geo api.
     * @param location
     *@return GeocodingResponse[]
     */
    private GeocodingResponse[] performGeolocationRequest(String location)
        throws IOException, InterruptedException {
        HttpClient newHttpClient = HttpClient.newHttpClient();
        HttpRequest geolocationRequest = HttpRequest.newBuilder()
            .uri(URI.create(GEOCODING_API_ENDPOINT +
            "?q=" + URLEncoder.encode(location, StandardCharsets.UTF_8) +
            "&format=json"))
            .build();

        HttpResponse<String> geolocationResponse =
            newHttpClient.send(geolocationRequest, HttpResponse.BodyHandlers.ofString());




        if (geolocationResponse.statusCode() == 302) {
            String redirectUrl = geolocationResponse.headers().firstValue("Location").orElse("");
            System.out.println("Redirecting to: " + redirectUrl);

            // Create a new request to follow the redirect
            HttpRequest redirectRequest = HttpRequest.newBuilder()
                .uri(URI.create(redirectUrl))
                .build();

            // Send the redirect request using the newHttpClient
            geolocationResponse =
                newHttpClient.send(redirectRequest, HttpResponse.BodyHandlers.ofString());
        }





        if (geolocationResponse.statusCode() == 200) {
            Gson gson = new Gson();
            return gson.fromJson(geolocationResponse.body(), GeocodingResponse[].class);
        } else {
            System.out.println("Failed to fetch geolocation data: " +
                geolocationResponse.statusCode());
            return new GeocodingResponse[0]; // Return an empty array if request fails
        }
    } //performGeolocationRequest




    /**
     * handles weather api request/response.
     * @param geolocation
     * @return null
     */
    private WeatherResponse performWeatherRequest(GeocodingResponse geolocation)
        throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest weatherRequest = HttpRequest.newBuilder()
            .uri(URI.create(WEATHER_API_ENDPOINT +
            "points/" + geolocation.getLat() + "," + geolocation.getLon()))
            .build();
        HttpResponse<String> weatherResponse =
            httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());

        if (weatherResponse.statusCode() == 301) {
            // Get the new location from the 'Location' header
            String newLocation = weatherResponse.headers().firstValue("Location").orElse("");
            System.out.println("New Location: " + newLocation);
            if (!newLocation.startsWith("https")) {
                newLocation = "https://" + "api.weather.gov" + newLocation;
            }
// Create a new request with the updated URL
            weatherRequest = HttpRequest.newBuilder()
            .uri(URI.create(newLocation))
            .build();
        // Send the request again
            weatherResponse = httpClient.send(weatherRequest, HttpResponse.BodyHandlers.ofString());
        }
        if (weatherResponse.statusCode() == 200) {
        // Extract grid coordinates from the response manually
            String responseBody = weatherResponse.body();
            System.out.println(weatherResponse.body());
            int gridXIndex = responseBody.indexOf("gridX\":") + 7;
            int gridYIndex = responseBody.indexOf("gridY\":") + 7;
            int commaIndex = responseBody.indexOf(',', gridXIndex);
            int endBracketIndex = responseBody.indexOf(',', gridYIndex);
            String gridX = responseBody.substring(gridXIndex, commaIndex).trim();
            String gridY = responseBody.substring(gridYIndex, endBracketIndex).trim();
            String encodedGridX = URLEncoder.encode(gridX, StandardCharsets.UTF_8);
            String encodedGridY = URLEncoder.encode(gridY, StandardCharsets.UTF_8);
            // Make a second request for weather forecast data based on grid coordinates
            HttpRequest forecastRequest = HttpRequest.newBuilder()
                .uri(URI.create(WEATHER_API_ENDPOINT +
                "gridpoints/" + "FFC/" + encodedGridX + "," + encodedGridY + "/forecast"))
                .build();
            System.out.println(encodedGridY);
            HttpResponse<String> forecastResponse =
                httpClient.send(forecastRequest, HttpResponse.BodyHandlers.ofString());
            if (forecastResponse.statusCode() == 200) {
                Gson gson = new Gson();
                return gson.fromJson(forecastResponse.body(), WeatherResponse.class);
            } else {
                System.out.println("Failed to fetch weather data: " +
                    forecastResponse.statusCode());
                return null;
            }
        } else {
            System.out.println("Failed to fetch weather data: " + weatherResponse.statusCode());
            return null; // Return null if request fails
        }
    }



    /**
     *populates ui with weather.
     *@param weather
     *@param resultTilePane
     */
    private void updateUIWithWeatherInfo(WeatherResponse weather, TilePane resultTilePane) {
        Platform.runLater(() -> {

            resultTilePane.getChildren().clear();

            WeatherProperties properties = weather.getProperties();
            WeatherPeriod[] periods = properties.getPeriods();
            for (WeatherPeriod period : periods) {

                VBox weatherBox = new VBox();
                weatherBox.setSpacing(5);


                weatherBox.getChildren().addAll(
                    new Label("Name: " + period.getName()),
                    new Label("Short Forecast: " + period.getShortForecast()),
                    new Label("Detailed Forecast: " + period.getDetailedForecast()),
                    new Label("Temperature: " + period.getTemperature()),
                    new Label("Wind Speed: " + period.getWindSpeed()),
                    new Label("Wind Direction: " + period.getWindDirection()),
                    new Label("Humidity: " + period.getHumidity())
                ) ;


                resultTilePane.getChildren().add(weatherBox);
            }
        });
    }






// create UI




    /**
     *provides TextField.
     *@return TextField.
     */
    private TextField createLocationField() {
        return new TextField();
    } //createLocationField


    /**
     * initiates action with search button.
     *@param locationField
     *@return searchButton
     */
    private Button createSearchButton(TextField locationField) {
        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> handleSearch(locationField));
        return searchButton;
    } //createSearchButton


    /**
     * provides tile pane for weather info.
     *@return TilePane
     */
    private TilePane createResultTilePane() {
        return new TilePane();
    } //createResultTilePane


} // ApiApp
