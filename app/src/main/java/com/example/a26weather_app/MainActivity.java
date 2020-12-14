package com.example.a26weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    TextView resultTextView;

    public class DownloadJson extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection connection;
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather",weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);
                String message = "";
                for (int i=0;i<arr.length();i++){

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                     if(!main.equals("") && !description.equals("")){
                         message += main + " : " + description + "\r\n";
                     }
                }
                if(!message.equals("")){
                    resultTextView.setText(message);
                }else{
                    Toast.makeText(MainActivity.this, "Could not get city weather :(", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Could not get city weather :(", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void getWeather(View view){
        try {
            DownloadJson task = new DownloadJson();
            String encodedName =  URLEncoder.encode( editText.getText().toString(),"UTF-8");
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" +encodedName+ "&appid=352d91a9bea0eb1eb77c5132e98d3fdc").get();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Could not get city weather :(", Toast.LENGTH_SHORT).show();
        }

        InputMethodManager mngr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mngr.hideSoftInputFromWindow(editText.getWindowToken(),0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        editText = findViewById(R.id.editText);


    }
}