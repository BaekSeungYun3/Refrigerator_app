package com.example.refrigerator_app;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class room_temperatureFragment extends Fragment {
    private DbOpenHelper dbOpenHelper;
    private LinearLayout roomItemsLayout;
    private ImageButton roomBtn;

    public room_temperatureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbOpenHelper = new DbOpenHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_room_temperature, container, false);

        roomItemsLayout = view.findViewById(R.id.roomItemsLayout);
        roomBtn = view.findViewById(R.id.room_btn);

        roomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCustomDialog();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDatabaseData();
    }

    private void loadDatabaseData() {
        roomItemsLayout.removeAllViews();

        dbOpenHelper.open();
        Cursor cursor = dbOpenHelper.selectColumnsroom();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int nameIndex = cursor.getColumnIndex("name");
                int epIndex = cursor.getColumnIndex("ep");
                int quantityIndex = cursor.getColumnIndex("quantity");

                if (nameIndex != -1 && epIndex != -1 && quantityIndex != -1) {
                    String name = cursor.getString(nameIndex);
                    String ep = cursor.getString(epIndex);
                    String quantity = cursor.getString(quantityIndex);

                    RoomItem roomItem = new RoomItem(name, ep, quantity);
                    createRoomItemView(roomItem);
                }
            }
            cursor.close();
        }
        dbOpenHelper.close();
    }

    private void createRoomItemView(RoomItem item) {
        View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.card_item, roomItemsLayout, false);

        TextView nameText = itemView.findViewById(R.id.nametext);
        TextView epText = itemView.findViewById(R.id.eptext);
        TextView quantityText = itemView.findViewById(R.id.quantitytext);

        nameText.setText(item.getName());
        quantityText.setText(item.getQuantity());

        String dDayValue = calculateDDay(item.getEp());
        epText.setText(dDayValue);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to launch the elementscreen_Activity
                Intent intent = new Intent(getActivity(), elementscreen_Activity.class);

                // Pass the data to the intent using extras
                intent.putExtra("name", item.getName());
                intent.putExtra("ep", item.getEp());
                intent.putExtra("quantity", item.getQuantity());

                // Start the activity
                startActivity(intent);
            }
        });

        roomItemsLayout.addView(itemView);
    }

    private String calculateDDay(String ep) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date endDate = dateFormat.parse(ep);
            Date currentDate = new Date();

            long timeDiff = endDate.getTime() - currentDate.getTime();
            long days = timeDiff / (24 * 60 * 60 * 1000);

            if (days < 0) {
                return "D+" + Math.abs(days);
            } else if (days == 0) {
                return "D-day";
            } else {
                return "D-" + days;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void onCustomDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.activity_dialogadd);

        ImageButton camerabtn = dialog.findViewById(R.id.camraButton);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WCamera_Activty.class);
                startActivity(intent);
            }
        });

        ImageButton writebtn = dialog.findViewById(R.id.writeButton);
        writebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Wadd_Activity.class);
                startActivity(intent);
            }
        });

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private static class RoomItem {
        private String name;
        private String ep;
        private String quantity;

        public RoomItem(String name, String ep, String quantity) {
            this.name = name;
            this.ep = ep;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public String getEp() {
            return ep;
        }

        public void setEp(String ep) {
            this.ep = ep;
        }

        public String getQuantity() {
            return quantity;
        }
    }

}
