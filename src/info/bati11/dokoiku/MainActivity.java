package info.bati11.dokoiku;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    public static final String PREF_NAME = "goalPreference";
    public static final String PREF_KEY_GOAL_NAME = "goalName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        String goalName = sharedPreferences.getString(PREF_KEY_GOAL_NAME, "");
        if ("".equals(goalName)) {
            setSeekMode();
        } else {
            setGoalMode(goalName);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        String goalName = sharedPreferences.getString(PREF_KEY_GOAL_NAME, "");
        if ("".equals(goalName)) {
            setSeekMode();
        } else {
            setGoalMode(goalName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void transit(View view) {
        Intent intent = new Intent(this, SelectCitiesActivity.class);
        startActivity(intent);
    }

    public void clearGoal(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        setSeekMode();
    }

    private void setSeekMode() {
        Button goSelectCitiesButton = (Button)findViewById(R.id.button_go_selectcities);
        TextView goalLabelText = (TextView)findViewById(R.id.goal_label);
        TextView goalText = (TextView)findViewById(R.id.goal);
        Button clearButton = (Button)findViewById(R.id.button_clear_goal);
        goSelectCitiesButton.setVisibility(View.VISIBLE);
        goalLabelText.setVisibility(View.GONE);
        goalText.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
    }

    private void setGoalMode(String goalName) {
        Button goSelectCitiesButton = (Button)findViewById(R.id.button_go_selectcities);
        TextView goalLabelText = (TextView)findViewById(R.id.goal_label);
        TextView goalText = (TextView)findViewById(R.id.goal);
        Button clearButton = (Button)findViewById(R.id.button_clear_goal);
        goSelectCitiesButton.setVisibility(View.GONE);
        goalLabelText.setVisibility(View.VISIBLE);
        goalText.setText(goalName);
        goalText.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);
    }
}
