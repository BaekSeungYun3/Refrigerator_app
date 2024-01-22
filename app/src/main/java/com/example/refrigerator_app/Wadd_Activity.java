package com.example.refrigerator_app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Wadd_Activity extends AppCompatActivity implements View.OnClickListener {

    public static Wadd_Activity context;

    EditText editTextTextPersonName, editTextDate, edit_quantity;
    RadioGroup radioGroup;
    RadioButton cold_radiobtn, frz_radiobtn, room_radiobtn;
    TextView BtnAdd;

    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wadd);

        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        editTextDate = findViewById(R.id.editTextDate);
        edit_quantity = findViewById(R.id.edit_quantity);

        radioGroup = findViewById(R.id.radioGroup);
        cold_radiobtn = findViewById(R.id.radioButton);
        frz_radiobtn = findViewById(R.id.radioButton2);
        room_radiobtn = findViewById(R.id.radioButton3);

        BtnAdd = findViewById(R.id.BtnAdd);
        BtnAdd.setOnClickListener(this);

        context = this;

        mDbOpenHelper = new DbOpenHelper(this);
        try {
            mDbOpenHelper.open();
            mDbOpenHelper.create();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setInsertMode();

        Intent secondIntent = getIntent();
        String message_item = secondIntent.getStringExtra("Items");
        String message_ep = secondIntent.getStringExtra("EP");
        String message_quantity = secondIntent.getStringExtra("quantity");

        if (secondIntent.hasExtra("Items")) {
            editTextTextPersonName.setText(message_item);
        } else {
            editTextTextPersonName.setText("");
        }

        if (secondIntent.hasExtra("EP")) {
            editTextDate.setText(message_ep);
        } else {
            editTextDate.setText("");
        }

        if (secondIntent.hasExtra("quantity")) {
            edit_quantity.setText(message_quantity);
        } else {
            edit_quantity.setText("");
        }

        ImageButton backicon = findViewById(R.id.backicon);
        backicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void setInsertMode() {
        editTextTextPersonName.setText("");
        editTextDate.setText("");
        edit_quantity.setText("");
        cold_radiobtn.setChecked(false);
        frz_radiobtn.setChecked(false);
        room_radiobtn.setChecked(false);
        BtnAdd.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnAdd:
                String name = editTextTextPersonName.getText().toString();
                String ep = editTextDate.getText().toString();
                String quantity = edit_quantity.getText().toString();
                String keep = "보관법";

                if (cold_radiobtn.isChecked()) {
                    mDbOpenHelper.insertColumn(name, ep, quantity, keep);
                    setInsertMode();
                    Toast.makeText(getApplicationContext(), "추가하였습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Wadd_Activity.this, MainActivity.class);
                    startActivity(intent);
                } else if (frz_radiobtn.isChecked()) {
                    mDbOpenHelper.insertColumnfrz(name, ep, quantity, keep);
                    setInsertMode();
                    Toast.makeText(getApplicationContext(), "추가하였습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Wadd_Activity.this, MainActivity.class);
                    startActivity(intent);
                } else if (room_radiobtn.isChecked()) {
                    mDbOpenHelper.insertColumnroom(name, ep, quantity, keep);
                    setInsertMode();
                    Toast.makeText(getApplicationContext(), "추가하였습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Wadd_Activity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "보관법을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}