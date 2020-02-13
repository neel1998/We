package com.abc.we;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ChatAdapter extends ArrayAdapter<String> {
    private int chatType;
    private String name1, name2;

    public ChatAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects, int type, String name1, String name2) {
        super(context, resource, objects);
        chatType = type;
        this.name1 = name1;
        this.name2 = name2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View chatView = convertView;
        if (chatView == null) {
            chatView = LayoutInflater.from(getContext()).inflate(R.layout.chat_bubble_layout, parent, false);
        }
        String data = getItem(position);
        data = data.trim();
        String time = data.split("-")[0].trim();
        String name = data.substring(time.length() + 3).trim().split(":")[0].trim();
        String msg = data.substring(time.length() + 3).trim().substring(name.length() + 1).trim();
        String mainName = "";
        if (chatType == 0) {
            mainName = this.name1;
        } else {
            mainName = this.name2;
        }
        if (name.equals(mainName)) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(200, 0, 0, 0);
            chatView.findViewById(R.id.chat_parent).setLayoutParams(params);
            chatView.findViewById(R.id.chat_parent).setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.chat_bg1));
            ((TextView) chatView.findViewById(R.id.chat_sender)).setText(name);
            ((TextView) chatView.findViewById(R.id.chat_text)).setText(msg);
            ((TextView) chatView.findViewById(R.id.chat_time)).setText(time);

        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 200, 0);
            chatView.findViewById(R.id.chat_parent).setLayoutParams(params);
            chatView.findViewById(R.id.chat_parent).setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.chat_bg2));
            ((TextView) chatView.findViewById(R.id.chat_sender)).setText(name);
            ((TextView) chatView.findViewById(R.id.chat_text)).setText(msg);
            ((TextView) chatView.findViewById(R.id.chat_time)).setText(time);
        }
        return chatView;
    }
}
