package com.example.refrigerator_app;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
public class CustomAdapter extends ArrayAdapter<String> {
    private ArrayList<String> listItem;
    private Context context;
    private DBHelper db;
    private RelativeLayout cartRelative;

    public CustomAdapter(Context context, ArrayList<String> listItem) {
        super(context, 0, listItem);
        this.context = context;
        this.listItem = listItem;
        db = new DBHelper(context); // DBHelper 객체 초기화
        this.cartRelative = cartRelative; // RelativeLayout 초기화
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_listview_item, parent, false);
        }
        String item = listItem.get(position);
        TextView textViewItem = convertView.findViewById(R.id.textView_item);
        ImageButton imageButtonItem = convertView.findViewById(R.id.imageButton_item);

        textViewItem.setText(item);

        imageButtonItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteData(item);
                updateData();
            }
        });
        return convertView;
    }
    public String getItem(int position) {
        return listItem.get(position);
    }

    public void updateData() {
        listItem.clear(); // 기존 목록 초기화
        // 데이터베이스에서 아이템 다시 불러오기
        Cursor cursor = db.viewData();
        if (cursor != null) {
            int nameColumnIndex = cursor.getColumnIndex(DBHelper.getName());
            if (nameColumnIndex != -1 && cursor.moveToFirst()) {
                do {
                    if (nameColumnIndex != -1) {
                        String item = cursor.getString(nameColumnIndex);
                        listItem.add(item); // 새로운 아이템 추가
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        notifyDataSetChanged(); // 어댑터에 데이터 변경 알림

        ListView listView = ((Activity) context).findViewById(R.id.cart_list);
        RelativeLayout cartRelative = ((Activity) context).findViewById(R.id.cart_Relative);
        if (listItem.isEmpty()) {
            listView.setVisibility(View.GONE); // ListView 숨김
            cartRelative.setVisibility(View.VISIBLE); // RelativeLayout 보임
        } else {
            listView.setVisibility(View.VISIBLE); // ListView 보임
            cartRelative.setVisibility(View.GONE); // RelativeLayout 숨김
        }

    }

}