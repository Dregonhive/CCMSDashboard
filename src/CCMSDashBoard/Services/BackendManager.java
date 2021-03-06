package CCMSDashBoard.Services;

import java.net.URL;


import java.net.HttpURLConnection;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.BufferedReader;

import CCMSDashBoard.Model.Accident;
import CCMSDashBoard.Utilities.Constants.Objects;
import CCMSDashBoard.Utilities.Protocols.Parsers;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by Meave Dregonhive on 22/08/2019.
 */
public class BackendManager implements Runnable
{

    @Override
    public void run()
    {
        try
        {
            pollFromBackend();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private void pollFromBackend() throws Exception
    {
        String USER_AGENT = "Mozilla/5.0";

        String url = "https://geniustraffic.herokuapp.com/poll_web"; //URL to send request to
        URL objurl = new URL(url);



        while(true)
        {
            HttpURLConnection connection = (HttpURLConnection) objurl.openConnection();
            //connection.setConnectTimeout(4000);
            connection.setRequestMethod("GET"); //request type = GET, POST, DELETE, ...
            connection.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = 0;
            try
            {
                responseCode = connection.getResponseCode(); //Server response code (200, 404, ...)

                System.out.println("timeout = " + connection.getConnectTimeout());
                //Will reach this point only after the server res.shit (i.e. if long-polling for instance, it will wait)
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) //Reading the response (in snips because it's a buffer)
                {
                    response.append(inputLine);
                }
                in.close(); //Closing the buffer
                //print full result
                System.out.println(response.toString());
                if (responseCode == 200)
                {

                    JSONParser parser = new JSONParser();
                    System.out.println("dile");
                    JSONObject accidentObjectToParse = (JSONObject)parser.parse(response.toString());
                    Accident accident = Parsers.parseAccidentObject(accidentObjectToParse);
                    accident.setJsonObject(accidentObjectToParse);
                    System.out.println("backend = " + accident.getJsonObject().toString());
                    Objects.Accidents.add(accident);
                }

                System.out.println("Showing alert");
            }
            catch(Exception exception)
            {
                if (responseCode < 503)
                    exception.printStackTrace();
            }
            finally
            {

            }

        }
    }
}
