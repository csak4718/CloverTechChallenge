package clover.superhero;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String requestUrl = "http://www.omdbapi.com/?s=";

        List<String> res = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_main_list_view, res);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        
        String[] input = new String[] {"Batman", "Superman", "Fantastic Four"};
        for (String str: input) {
            str = str.replace(' ', '+');
            Log.d("TEST", str);
        }


    }
}
