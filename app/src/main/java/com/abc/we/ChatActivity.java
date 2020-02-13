package com.abc.we;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ArrayList<String> chatList = new ArrayList<>();
    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String date = getIntent().getStringExtra("date");
        String data = getIntent().getStringExtra("data");
        String type = getIntent().getStringExtra("type");
        String name1 = getIntent().getStringExtra("name1");
        String name2 = getIntent().getStringExtra("name2");

        date = date.split("/")[1] + " " + months[Integer.valueOf(date.split("/")[0]) - 1] + " '" + date.split("/")[2];
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                chatList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.date_text)).setText(date);
        ChatAdapter chatAdapter = new ChatAdapter(this, 0, chatList, Integer.valueOf(type), name1, name2);
        ListView chatListView = findViewById(R.id.chat_list);
        chatListView.setAdapter(chatAdapter);
    }
}
