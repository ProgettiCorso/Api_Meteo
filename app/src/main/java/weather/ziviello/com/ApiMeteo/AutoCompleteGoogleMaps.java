package weather.ziviello.com.ApiMeteo;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by aesys on 11/12/17.
 */

//** DA UTILIZZARE INSIEME A "RequestHttp.java" ****************************************************

    public class AutoCompleteGoogleMaps extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;
    RequestHttp requestHttp;
    Context context;

    public AutoCompleteGoogleMaps(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    try {
                        resultList = autocomplete(constraint.toString());
                    } catch (UnsupportedEncodingException e) {
                        Log.w("err", "Exception: " + e.toString());
                    }

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

//**************************************************************************************************

    public ArrayList<String> autocomplete(String input) throws UnsupportedEncodingException {
        ArrayList<String> resultList = null;
        requestHttp = new RequestHttp();
        JSONObject rispostaJson;

        try {
            JSONObject jboUrl = new JSONObject();

            try {
                jboUrl.put("language", "it");
                jboUrl.put("types", "(regions)");
                jboUrl.put("input", String.valueOf(URLEncoder.encode(input, "utf8")));
                jboUrl.put("key", context.getResources().getString(
                        R.string.myGoogleMapsApiKey));
            } catch (JSONException e) {
                Log.w("err", "Exception: " + e.toString());
            }

            rispostaJson = requestHttp.execute(
                    "https://maps.googleapis.com/maps/api/place/autocomplete/json",
                    String.valueOf(jboUrl), "GET", "").get();
            Log.e("CIAO", String.valueOf(rispostaJson));
            try {
                JSONObject risultatoJson = new JSONObject(rispostaJson.getString("response"));
                JSONArray predsJsonArray = risultatoJson.getJSONArray("predictions");

                resultList = new ArrayList<String>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                }
            } catch (JSONException e) {
                Log.e("Errore:", "Cannot process JSON results", e);
            }


        } catch (InterruptedException e) {
            Log.e("errore:", "Error processing Places API URL", e);
            return resultList;
        } catch (ExecutionException e) {
            Log.e("errore:", "Error connecting to Places API", e);
            return resultList;
        } finally {

        }

        return resultList;
    }

//**************************************************************************************************

}

