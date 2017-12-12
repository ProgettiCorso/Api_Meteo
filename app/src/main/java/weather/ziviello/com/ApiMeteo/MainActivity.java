package weather.ziviello.com.ApiMeteo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    RequestHttp requestHttp;
    AutoCompleteTextView acNameCity;
    TextView tvTitle;
    TextView tvNameCity;
    TextView tvDescription;
    TextView tvTemperature;
    TextView tvTempMAX;
    TextView tvTempMIN;
    TextView tvWind;
    ImageView imgWeatherIcon;
    JSONObject JSON_City;
    JSONObject JSON_Weather;
    JSONObject JSON_Response;
    JSONObject JSON_ChildNode;
    JSONArray JSON_RisultatoArray;
    JSONObject JSON_RisultatoObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        acNameCity = findViewById(R.id.acNameCity);
        acNameCity.setAdapter(new AutoCompleteGoogleMaps(getApplicationContext(), R.layout.activity_text));

        acNameCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    acNameCity.clearFocus();
                    InputMethodManager in = (InputMethodManager)getApplication().getSystemService(getApplication().INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(acNameCity.getWindowToken(), 0);
                    SearchCity();
                    return true;
                }
                return false;
            }
        });
        tvTitle = findViewById(R.id.tvTitle);
        tvNameCity = findViewById(R.id.tvNameCity);
        tvDescription = findViewById(R.id.tvDescription);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvTempMAX = findViewById(R.id.tvTempMAX);
        tvTempMIN = findViewById(R.id.tvTempMIN);
        tvWind = findViewById(R.id.tvWind);
        imgWeatherIcon = findViewById(R.id.imgWeatherIcon);
    }

    private void SearchCity()
    {
        requestHttp = new RequestHttp();
        JSON_City = new JSONObject();
        JSON_Weather = new JSONObject();
        String cityName;
        String country;
        String controlIcon;
        if (!acNameCity.getText().toString().equals("")) {
            try
            {
                JSON_City.put("q", acNameCity.getText().toString().split(",")[0]);
                JSON_City.put("appid", getResources().getString(R.string.myWeatherApiKey));
                JSON_City.put("units", "metric");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try
            {
                JSON_Weather = requestHttp.execute("http://api.openweathermap.org/data/2.5/weather", String.valueOf(JSON_City), "GET", "").get();
                JSON_Response = new JSONObject(JSON_Weather.getString("response"));
                if(Integer.parseInt(JSON_Response.getString("cod"))<400)
                {
                    JSON_RisultatoArray = new JSONArray(JSON_Response.getString("weather"));
                    JSON_ChildNode = JSON_RisultatoArray.getJSONObject(0);
                    controlIcon = JSON_ChildNode.getString("icon");
                    if (controlIcon.equals("01d") || controlIcon.equals("01n"))
                    {
                        tvDescription.setText(R.string.A);
                    }
                    if (controlIcon.equals("02d") || controlIcon.equals("02n"))
                    {
                        tvDescription.setText(R.string.B);
                    }
                    if (controlIcon.equals("03d") || controlIcon.equals("03n"))
                    {
                        tvDescription.setText(R.string.C);
                    }
                    if (controlIcon.equals("04d") || controlIcon.equals("04n"))
                    {
                        tvDescription.setText(R.string.D);
                    }
                    if (controlIcon.equals("09d") || controlIcon.equals("09n"))
                    {
                        tvDescription.setText(R.string.E);
                    }
                    if (controlIcon.equals("10d") || controlIcon.equals("10n"))
                    {
                        tvDescription.setText(R.string.F);
                    }
                    if (controlIcon.equals("11d") || controlIcon.equals("11n"))
                    {
                        tvDescription.setText(R.string.G);
                    }
                    if (controlIcon.equals("13d") || controlIcon.equals("13n"))
                    {
                        tvDescription.setText(R.string.H);
                    }
                    if (controlIcon.equals("50d") || controlIcon.equals("50n"))
                    {
                        tvDescription.setText(R.string.I);
                    }
                    String UrlIcon = getResources().getString(R.string.iconUrl) + (JSON_ChildNode.getString("icon")) + ".png";
                    Picasso.with(getApplicationContext()).load(UrlIcon).into(imgWeatherIcon);
                    JSON_RisultatoObj = new JSONObject(JSON_Response.getString("main"));
                    tvTemperature.setText("T " + JSON_RisultatoObj.getString("temp") + "°");
                    tvTempMAX.setText("Tmax: " + JSON_RisultatoObj.getString("temp_max") + "°");
                    tvTempMIN.setText("Tmin: " + JSON_RisultatoObj.getString("temp_min") + "°");
                    JSON_RisultatoObj = new JSONObject(JSON_Response.getString("wind"));
                    tvWind.setText("Vento: " + JSON_RisultatoObj.getString("speed") + " Km/h");
                    cityName = JSON_Response.getString("name");
                    JSON_RisultatoObj = new JSONObject(JSON_Response.getString("sys"));
                    country = JSON_RisultatoObj.getString("country");
                    tvNameCity.setText(cityName + " ( " + country + " )");
                }
                else
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.cityNotFound), Toast.LENGTH_LONG).show();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            acNameCity.setText("");
        } else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.noCity), Toast.LENGTH_LONG).show();
        }
    }
}

