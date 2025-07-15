import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        boolean stop = false;
        while (!stop) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Please type 'stop' if you would like to end the program.");
            String eventType = scan.nextLine();
            if(!eventType.equalsIgnoreCase("stop")) {
                System.out.println("Please enter the date and time of your event in the form yyyy-MM-dd");
                String eventDate = scan.nextLine();
                int day = Integer.parseInt(eventDate.substring(eventDate.length() - 2));
                String notDay = eventDate.substring(0, eventDate.length() - 2);
                System.out.println("Please enter the time of your event in the format hh:mm");
                String eventTime = scan.nextLine() + ":00";
                String url = "https://iswa.ccmc.gsfc.nasa.gov/IswaSystemWebApp/hapi/data?id=ace_swepam_P1M&parameters=ProtonDensity&time.min=" + notDay + (day - 1) + "T" + eventTime + "&time.max=" + eventDate + "T" + eventTime + "&format=json";
                JSONObject data = URLToJSON(url);
                JSONArray specData = data.getJSONArray("data");
                double total = 0;
                int points = 0;
                for (int i = 0; i < specData.length(); i++) {
                    JSONArray point = specData.getJSONArray(i);
                    if (point.getDouble(1) > 0) {
                        total += point.getDouble(1);
                        points++;
                    }

                }
                double finalVal = total / points;
                System.out.println("Final 24 Hr Avg Proton Density: " + finalVal);

                String url2 = "https://iswa.ccmc.gsfc.nasa.gov/IswaSystemWebApp/hapi/data?id=ace_swepam_P1M&parameters=BulkSpeed&time.min=" + notDay + (day - 1) + "T" + eventTime + "&time.max=" + eventDate + "T" + eventTime + "&format=json";
                JSONObject data2 = URLToJSON(url2);
                JSONArray specData2 = data2.getJSONArray("data");
                double total2 = 0;
                int points2 = 0;
                for (int i = 0; i < specData2.length(); i++) {
                    JSONArray point2 = specData2.getJSONArray(i);
                    if (point2.getDouble(1) > 0) {
                        total2 += point2.getDouble(1);
                        points2++;
                    }

                }
                double finalVal2 = total2 / points2;
                System.out.println("Final 24 Hr Avg Bulk Speed: " + finalVal2);
                //System.out.println(data);
            }else {
                stop = true;
            }
            System.out.println();
        }


    }

    //Gets JSONObject from provided URL
    public static JSONObject URLToJSON(String URLString){
        try {
            System.out.println(URLString);
            //Convert string to URL
            URI uri = URI.create(URLString);
            URL url = uri.toURL();

            //Connect and pull data from URL and store in String
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                String newJSONString = response.toString();
                //System.out.println(newJSONString);

                //convert String back to JSON Array
                try {
                    return new JSONObject(newJSONString);
                } catch (JSONException e) {
                    System.out.println("JSON creation failed: "+e);
                    return null;
                }

            } else {
                System.out.println("GET request failed. Response code: " + responseCode);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace(); /*Okay because this is not production software*/
            return null;
        }
    }

}
