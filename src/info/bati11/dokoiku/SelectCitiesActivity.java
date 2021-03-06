package info.bati11.dokoiku;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class SelectCitiesActivity extends FragmentActivity {

    private final static String YAHOO_API_KEY = "****";
    public final static String YAHOO_PREFECTURE_API = "http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/addressDirectory?appid=" + YAHOO_API_KEY + "&output=json";

    private final static Map<String, String> cache = new HashMap<String, String>();

    private MapFragment map;
    private String goal;

    private enum Prefecture {
        P01("北海道"),
        P02("青森県"),
        P03("岩手県"),
        P04("宮城県"),
        P05("秋田県"),
        P06("山形県"),
        P07("福島県"),
        P08("茨城県"),
        P09("栃木県"),
        P10("群馬県"),
        P11("埼玉県"),
        P12("千葉県"),
        P13("東京都"),
        P14("神奈川県"),
        P15("新潟県"),
        P16("富山県"),
        P17("石川県"),
        P18("福井県"),
        P19("山梨県"),
        P20("長野県"),
        P21("岐阜県"),
        P22("静岡県"),
        P23("愛知県"),
        P24("三重県"),
        P25("滋賀県"),
        P26("京都府"),
        P27("大阪府"),
        P28("兵庫県"),
        P29("奈良県"),
        P30("和歌山県"),
        P31("鳥取県"),
        P32("島根県"),
        P33("岡山県"),
        P34("広島県"),
        P35("山口県"),
        P36("徳島県"),
        P37("香川県"),
        P38("愛媛県"),
        P39("高知県"),
        P40("福岡県"),
        P41("佐賀県"),
        P42("長崎県"),
        P43("熊本県"),
        P44("大分県"),
        P45("宮崎県"),
        P46("鹿児島県"),
        P47("沖縄県"),
        ;

        final String label;
        private Prefecture(String label) {
            this.label = label;
        }
        private String code() {
            return this.name().substring(1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DEBUG", "DisplayMessage START!!");

        setContentView(R.layout.activity_select_cities);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("日本全国");
        for (Prefecture p : Prefecture.values()) {
            adapter.add(p.label);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner_prefecture);
        spinner.setAdapter(adapter);

        Button okButton = (Button)findViewById(R.id.button_ok);
        okButton.setVisibility(View.GONE);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_cities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectSpot(View view) {
        ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Spinner prefectureSpinner = (Spinner) findViewById(R.id.spinner_prefecture);
            int selectIndex = prefectureSpinner.getSelectedItemPosition();

            final Prefecture selectPrefecture;
            Prefecture[] prefectures = Prefecture.values();
            if (selectIndex == 0) {
                Log.d("DEBUG", "都道府県をランダムで選択します");
                int index = new Random().nextInt(prefectures.length);
                selectPrefecture = prefectures[index];
            } else {
                Log.d("DEBUG", "都道府県が選択されました。 selectIndex=" + selectIndex);
                selectPrefecture = prefectures[selectIndex-1];
            }

            String cacheStr = cache.get(selectPrefecture.name());
            if (cacheStr == null) {
                final AQuery aq = new AQuery(this);
                String url = YAHOO_PREFECTURE_API + "&ac=" + selectPrefecture.code();
                aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>(){
                    @Override public void callback(String url, JSONObject json, AjaxStatus status) {
                        if (json == null) Log.d("DEBUG", "Error:" + status.getCode());
                        else render(selectPrefecture, json);
                    }
                });
            } else {
                Log.d("DEBUG", "Use Cache!!");
                render(selectPrefecture, cacheStr);
            }

        } else {
            // display error
        }
    }

    public void determineSpot(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREF_NAME, Activity.MODE_PRIVATE);
        sharedPreferences.edit()
                         .putString(MainActivity.PREF_KEY_GOAL_NAME, goal)
                         .commit();
        finish();
    }

    private void render(Prefecture prefecture, String jsonText) {
        TextView textView = (TextView) findViewById(R.id.result_spot);
        try {
            JSONObject json = new JSONObject(jsonText);
            render(prefecture, json);
        } catch (JSONException e) {
            Log.d("DEBUG", "Jsonへの変換中にエラーが発生しました", e);
            textView.setText("Jsonへの変換に失敗しました");
        }
    }

    private void render(Prefecture prefecture, JSONObject json) {
        TextView textView = (TextView) findViewById(R.id.result_spot);
        try {
            JSONArray cities =
                json.getJSONArray("Feature")
                    .getJSONObject(0)
                    .getJSONObject("Property")
                    .getJSONArray("AddressDirectory");

            int index = new Random().nextInt(cities.length());
            JSONObject city = cities.getJSONObject(index);

            goal = prefecture.label + " " + city.getString("Name");
            StringBuilder message = new StringBuilder();
            message.append("Let's Go To");
            message.append("\n");
            message.append(goal);
            message.append("\n");
            textView.setText(message);
            Button okButton = (Button)findViewById(R.id.button_ok);
            okButton.setVisibility(View.VISIBLE);

            String[] latlng = city.getJSONObject("Geometry").getString("Coordinates").split(",");
            double latitude = Double.valueOf(latlng[1]);
            double longitude = Double.valueOf(latlng[0]);
            Log.d("DEBUG", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            Log.d("DEBUG", "latitude: " + latitude + ", longitude: " + longitude);
            Log.d("DEBUG", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

            if (map == null) {
                GoogleMapOptions options = new GoogleMapOptions();
                options.camera(new CameraPosition(new LatLng(latitude, longitude), 13, 0, 0));
                map = MapFragment.newInstance(options);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.layout_map, map);
                fragmentTransaction.commit();
            } else {
                CameraUpdate newLatLng = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
                map.getMap().moveCamera(newLatLng);
            }

            cache.put(prefecture.name(), json.toString());
        } catch (JSONException e) {
            Log.d("DEBUG", "Jsonへの変換中にエラーが発生しました", e);
            textView.setText("Jsonへの変換に失敗しました");
        }

    }
}
