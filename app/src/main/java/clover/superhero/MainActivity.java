package clover.superhero;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String requestUrl = "http://www.omdbapi.com/?s=";
    List<String> res = new ArrayList<>();
    ArrayAdapter adapter;
    @Bind(R.id.list_view) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupAdapter();

        new AsyncHttpClient().get(requestUrl + "James Bond", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray results = new JSONObject(new String(responseBody)).getJSONArray("Search");
                    int numJB = results.length();
                    List<Integer> yearsJB = getYearList(results);

                    int previousYear = yearsJB.get(0);
                    int currentYear = 0;
                    int cycleSum = 0;
                    for (int i = 1; i < numJB; i++) {
                        currentYear = yearsJB.get(i);
                        cycleSum += currentYear - previousYear;
                        previousYear = currentYear;
                    }
                    double cycleJB = (double) cycleSum / (numJB - 1);
                    queryOthers(numJB, cycleJB);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "Http request error");
            }
        });
    }

    private void queryOthers(final int numJB, final double cycleJB) {
        String[] heroArr = new String[] {"Batman", "Superman", "Fantastic Four"};
        for (final String hero: heroArr) {
            new AsyncHttpClient().get(requestUrl + hero, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        JSONArray results = new JSONObject(new String(responseBody)).getJSONArray(("Search"));
                        int num = results.length();
                        List<Integer> years = getYearList(results);

                        int previousYear = years.get(0);
                        int currentYear = 0;
                        int cycleSum = 0;
                        for (int i = 1; i < num; i++) {
                            currentYear = years.get(i);
                            cycleSum += currentYear - previousYear;
                            previousYear = currentYear;
                        }
                        double cycle = (double) cycleSum / (num - 1);
                        updateListView(cycle, num, cycleJB, numJB, hero, currentYear);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(TAG, "Http request error");
                }
            });
        }
    }

    private void updateListView(double cycle, int num, double cycleJB, int numJB, String hero, int currentYear) {
        if (cycle < cycleJB) {
            int diff = numJB - num + 1;
            if (diff > 0) {
                double yearNeeded = diff / (1 / cycle - 1 / cycleJB);
                int when = Math.ceil(yearNeeded) == yearNeeded
                        ? (int)(yearNeeded) + currentYear
                        : (int)(yearNeeded) + currentYear + 1;
                res.add(hero + ": Yes and its number of movies will surpass that of James Bond in " + String.valueOf(when));
            } else {
                res.add(hero + ": Yes and its number of movies has already surpassed that of James Bond");
            }
        } else {
            res.add(hero + ": No. Its velocity didn't surpass the velocity of James Bond");
        }
        adapter.notifyDataSetChanged();
    }

    private List<Integer> getYearList(JSONArray results) {
        List<Integer> ret = new ArrayList<>();
        try {
            for (int i = 0; i < results.length(); i++) {
                String year = results.getJSONObject(i).getString("Year");
                ret.add(Integer.parseInt(year.substring(0, 4)));
            }
            Collections.sort(ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void setupAdapter() {
        adapter = new ArrayAdapter<>(this, R.layout.activity_main_list_view, res);
        listView.setAdapter(adapter);
    }
}
