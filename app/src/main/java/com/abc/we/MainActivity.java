package com.abc.we;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public JSONObject chatObject;
    String yearStr, monthStr, dayStr, date = "";
    String person1Name, person2Name;
    int type = -1;
    ProgressBar mainProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatObject = new JSONObject();

        getPermissions();

        mainProgressBar = findViewById(R.id.main_progressbar);
        Calendar cal = Calendar.getInstance();
        dayStr = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        monthStr = String.valueOf(cal.get(Calendar.MONTH) + 1);
        yearStr = String.valueOf(cal.get(Calendar.YEAR));
        date = monthStr + "/" + dayStr + "/" + yearStr.substring(2);

        if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())) {
            String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            ArrayList<Uri> fileUri = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            Log.d("file", String.valueOf(fileUri.size()));
            if (sharedText != null) {
                Log.d("data", sharedText);
            } else {
                Log.d("data", "Not found");
            }
            if (fileUri.get(0) != null) {
                try {
                    InputStream is = getContentResolver().openInputStream(fileUri.get(0));
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    String data = new String(buffer);
                    String[] dataList = data.split("\n");
                    Log.d("data", dataList.length + "");
                    new getDataTask().execute(dataList);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("file", "uri is null");
            }
        } else if (getIntent().getStringExtra("chatKey") != null) {
            try {
                final SharedPreferences preferences = this.getSharedPreferences(getResources().getString(R.string.prefName), MODE_PRIVATE);
                String key = getIntent().getStringExtra("chatKey");
                JSONObject mainObject = new JSONObject(preferences.getString(key, null));
                chatObject = new JSONObject(mainObject.getString("chat"));
                person1Name = mainObject.getString("name1");
                person2Name = mainObject.getString("name2");
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("Chat of " + person1Name + " & " + person2Name);
                }
                ((RadioButton) findViewById(R.id.name1_radio)).setText(person1Name);
                ((RadioButton) findViewById(R.id.name2_radio)).setText(person2Name);
                Log.d("got data", chatObject.toString());
                mainProgressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (getIntent().getStringExtra("data_string") != null) {
            Uri uri = Uri.parse(getIntent().getStringExtra("data_string"));
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                String data = new String(buffer);
                String[] dataList = data.split("\n");
                new getDataTask().execute(dataList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else {
            startActivity(new Intent(MainActivity.this, SelectChatActivity.class));
        }

        CalendarView calendarView = findViewById(R.id.calender);
        Button goButton = findViewById(R.id.go_btn);
        RadioGroup senderRadio = findViewById(R.id.sender_radio);
        senderRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (((RadioButton) group.findViewById(R.id.name1_radio)).isChecked()) {
                    type = 0;
                } else if (((RadioButton) group.findViewById(R.id.name2_radio)).isChecked()) {
                    type = 1;
                }
            }
        });
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                yearStr = String.valueOf(year);
                monthStr = String.valueOf(month + 1);
                dayStr = String.valueOf(dayOfMonth);
                date = monthStr + "/" + dayStr + "/" + yearStr.substring(2);

            }
        });
        goButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (date.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                } else if (type == -1) {
                    Toast.makeText(MainActivity.this, "Please select a Sender", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(MainActivity.this, ChatActivity.class);
                    i.putExtra("date", date);
                    i.putExtra("name1", person1Name);
                    i.putExtra("name2", person2Name);
                    if (chatObject.has(date)) {
                        try {
                            JSONArray jsonArray = (JSONArray) chatObject.get(date);
                            i.putExtra("data", jsonArray.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        i.putExtra("type", String.valueOf(type));
                        startActivity(i);
                    } else {
                        Toast.makeText(MainActivity.this, "Ooops!!, No chat for this date", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        switch (menuId) {
            case R.id.saved_chats_menu:
                startActivity(new Intent(MainActivity.this, SelectChatActivity.class));
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected class getDataTask extends AsyncTask<String[], Void, JSONObject> {

        String name1 = "";
        String name2 = "";

        @Override
        protected JSONObject doInBackground(String[]... strings) {
            String[] data = strings[0];
            int n = data.length;
            int i = 1;
            String prevDate = "";
            while (i < n) {
                String d = data[i];
                d = d.replaceFirst("\\[","");
                d = d.replaceFirst("]","");
                String[] temp = d.split(",");
                String date = temp[0];
                if (date.contains("/")) {
                    try{
                        String time = d.substring(date.length() + 1).split("-")[0].trim();
                        String name = d.substring(date.length() + 1).substring(time.length() + 3).trim().split(":")[0].trim();
                        if ("".equals(name1) && "".equals(name2)) {
                            name1 = name;
                        } else if ("".equals(name2) && !name1.equals(name)) {
                            name2 = name;
                        } else if (!"".equals(name1) && !"".equals(name2)) {
                            break;
                        }
                    } catch (Exception e) {}

                }
                i++;
            }
            if (name2.compareTo(name1) < 0) {
                String temp = name1;
                name1 = name2;
                name2 = temp;
            }
            i = 1;
            int t = 1;
            int d1 = -1;
            while ( i < n ) {
                String d = data[i];
                d = d.replaceFirst("\\[", "");
                d = d.replaceFirst("]", "");
                String[] temp = d.split(",");
                String date = temp[0];
                if (date.contains("/") && (d.contains(name1 + ":") || d.contains(name2 + ":"))) {
                    String[] dateElements = date.split("/");
                    if (Integer.valueOf(dateElements[0]) > d1) {
                        d1 = Integer.valueOf(dateElements[0]);
                    }
                }
                i = i + 1;
            }
            if (d1 > 12) {
                t = 0;
            }
            i = 1;
            while (i < n) {
                String d = data[i];
                d = d.replaceFirst("\\[","");
                d = d.replaceFirst("]","");
                String[] temp = d.split(",");
                String date = temp[0];
                if (date.contains("/") && (d.contains(name1 + ":") || d.contains(name2 + ":"))) {
                    String[] dateElements = date.split("/");
                    if (dateElements[2].length() > 2) {
                        dateElements[2] = dateElements[2].substring(2);
                    }
                    String dateKey = "";
                    if (t == 0) {
                        dateKey = Integer.valueOf(dateElements[1]) + "/" + Integer.valueOf(dateElements[0]) + "/" + dateElements[2];
                    } else {
                        dateKey = Integer.valueOf(dateElements[0]) + "/" + Integer.valueOf(dateElements[1]) + "/" + dateElements[2];
                    }
                    prevDate = dateKey;
                    if (chatObject.has(dateKey)) {
                        try {
                            if (d.substring(date.length() + 1).length() > 0) {
                                ((JSONArray) chatObject.get(dateKey)).put(d.substring(date.length() + 1));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ArrayList<String> tempData = new ArrayList<>();
                        tempData.add(d.substring(date.length() + 1));
                        JSONArray array = new JSONArray(tempData);
                        try {
                            chatObject.put(dateKey, array);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                } else {
                    while (!d.contains("/") || (!d.contains(name1 + ":") && !d.contains(name2 + ":"))) {
                        try {
                            JSONArray lastArray = (JSONArray) chatObject.get(prevDate);
                            lastArray.put(lastArray.length() - 1, lastArray.get(lastArray.length() - 1) + "\n" + d);
                            i++;
                            if (i < n) {
                                d = data[i];
                            } else {
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            JSONObject mainObject = new JSONObject();
            ((RadioButton) findViewById(R.id.name1_radio)).setText(name1);
            ((RadioButton) findViewById(R.id.name2_radio)).setText(name2);
            person1Name = name1;
            person2Name = name2;
            mainProgressBar.setVisibility(View.GONE);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Chat of " + name1 + " & " + name2);
            }
            try {
                mainObject.put("name1", name1);
                mainObject.put("name2", name2);
                mainObject.put("chat", chatObject.toString());
                SharedPreferences preferences = MainActivity.this.getSharedPreferences(getResources().getString(R.string.prefName), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(name1 + " with " + name2, mainObject.toString());
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100 );
        }
    }
}
