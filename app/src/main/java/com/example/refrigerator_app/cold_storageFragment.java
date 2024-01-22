package com.example.refrigerator_app;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class cold_storageFragment extends Fragment {
    private DbOpenHelper dbOpenHelper;
    private LinearLayout coldItemsLayout;
    private ImageButton coldBtn;

    public cold_storageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_cold_storage, container, false);

        coldItemsLayout = view.findViewById(R.id.coldItemsLayout);
        coldBtn = view.findViewById(R.id.cold_btn);

        coldBtn.setOnClickListener(new View.OnClickListener() {
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
        coldItemsLayout.removeAllViews();

        dbOpenHelper.open();
        Cursor cursor = dbOpenHelper.selectColumns();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int nameIndex = cursor.getColumnIndex("name");
                int epIndex = cursor.getColumnIndex("ep");
                int quantityIndex = cursor.getColumnIndex("quantity");

                if (nameIndex != -1 && epIndex != -1 && quantityIndex != -1) {
                    String name = cursor.getString(nameIndex);
                    String ep = cursor.getString(epIndex);
                    String quantity = cursor.getString(quantityIndex);

                    ColdItem roomItem = new ColdItem(name, ep, quantity);
                    createcoldItemView(roomItem);
                }
            }
            cursor.close();
        }
        dbOpenHelper.close();
    }

    private void createcoldItemView(ColdItem item) {
        View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.card_item, coldItemsLayout, false);

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

        coldItemsLayout.addView(itemView);
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

    private static class ColdItem {
        private String name;
        private String ep;
        private String quantity;

        public ColdItem(String name, String ep, String quantity) {
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
