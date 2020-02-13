package com.abc.we;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class SelectChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chat);

        ListView selectChatListView = findViewById(R.id.select_chat_listview);
        TextView exportChatWarningText = findViewById(R.id.export_chat_warning);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Previously Loaded Chats");
        }
        final SharedPreferences preferences = this.getSharedPreferences(getResources().getString(R.string.prefName), MODE_PRIVATE);
        Map<String, ?> prefData = preferences.getAll();

        if (!prefData.isEmpty()) {

            selectChatListView.setVisibility(View.VISIBLE);
            exportChatWarningText.setVisibility(View.GONE);

            ArrayList<String> chatEntries = new ArrayList<>();
            for (Map.Entry<String, ?> entry : prefData.entrySet()) {
                chatEntries.add(entry.getKey());
            }
            ArrayAdapter<String> selectChatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatEntries);
            selectChatListView.setAdapter(selectChatAdapter);

            selectChatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String key = (String) parent.getItemAtPosition(position);
                    Intent i = new Intent(SelectChatActivity.this, MainActivity.class);
                    i.putExtra("chatKey", key);
                    startActivity(i);
                }
            });
        }
    }
}
