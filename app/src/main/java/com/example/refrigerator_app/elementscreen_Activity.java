package com.example.refrigerator_app;

import androidx.annotation.Dimension;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

public class elementscreen_Activity extends AppCompatActivity {
    private DbOpenHelper mDbOpenHelper;
    private String tableName;
    private String name;
    private String ep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elementscreen);

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Get the data from the intent extras
        name = intent.getStringExtra("name");
        ep = intent.getStringExtra("ep");

        // Find the TextViews in the layout
        TextView ele_nametx = findViewById(R.id.ele_nametx);
        TextView ele_eptx = findViewById(R.id.ele_eptx);

        // Set the data to the TextViews
        ele_nametx.setText(name);
        ele_eptx.setText(ep);

        // Get the table name based on the fragment
        String fragmentType = intent.getStringExtra("fragmentType");
        if (fragmentType != null && fragmentType.equals("frozenFragment")) {
            tableName =  DbOpenHelper._TABLENAME1;
        } else if (fragmentType != null && fragmentType.equals("room_temperatureFragment")) {
            tableName =  DbOpenHelper._TABLENAME2;
        } else {
            tableName = DbOpenHelper._TABLENAME0;
        }

        // Initialize the database helper
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();

        TextView delete_btn = findViewById(R.id.delete_btn);
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform deletion of the card view and data here
                deleteDataFromTable();

                // Create an intent to go back to MainActivity
                Intent intent = new Intent(elementscreen_Activity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Optional, to finish the current activity
            }
        });

        // 아이콘, 보관방법 설정
        ImageView imageView8 = (ImageView) findViewById(R.id.imageView8);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        textView4.setTextSize(Dimension.SP, 13); //글씨 크기조절
        String idname = ele_nametx.getText().toString();
        switch(idname) {
            case("바나나") :
                imageView8.setImageResource(R.drawable.banana);
                textView4.setText("\n"+
                        "[녹색 바나나 보관법]\n" +
                        "녹색 바나나는 실온에 보관해주세요.\n" +
                        "\n" +
                        "[익은 바나나 보관법]\n" +
                        "1. 바나나를 낱개로 분리한다.\n" +
                        "2. 익은 바나나를 익지 않은 과일과 함께 보관하기\n" +
                        "☝Tip. 익지 않은 아보카도나 과일을 옆에 두면 그 과일은 빨리 익고 바나나의 익는 속도를 낮춰워요.\n" +
                        "바나나 꼭지를 비닐랩으로 감싸주세요.\n"
                );
                break;
            case("콩나물") :
                imageView8.setImageResource(R.drawable.bean_sprout);
                textView4.setText("\n"+
                        "[냉장 보관법]\n" +
                        "1. 콩나물을 손질한 후 밀페용기에 담아주세요.\n" +
                        "2. 콩나물이 잠길만큼 물을 부어주세요.\n" +
                        "☝Tip. 무르거나 마르는 걸 방지해줘요.\n" +
                        "3. 뚜겅을 닫고 냉장 보관해 주세요.\n" +
                        "☝Tip. 매일 물을 교체하면 1주일정도 보관 가능해요.\n" +
                        "\n" +
                        "[삶은 콩나물 보관법]\n" +
                        "1. 끓는 물에 콩나물을 넣고 2~3분 정도 데쳐 주세요.\n" +
                        "2. 삶아진 콩나물을 얼음물에 건져 열기를 식혀 주세요.\n" +
                        "3. 밀폐 용기에 담아 냉장 보관해 주세요. (1주일간 보관이 가능해요)");
                break;
            case("양상추") :
                imageView8.setImageResource(R.drawable.cabbage);
                textView4.setText("\n"+
                        "[통 양상추 보관법]\n" +
                        "1. 양상추의 밑동을 물에 적신 키친타월로 감싸주세요.\n" +
                        "2. 양상추를 랩으로 감싸 냉장 보관해 주세요.\n" +
                        "☝Tip. 물에 적신 키친타월과 랩을 사용하면 수분 증발과 공기접촉을 막아 주어 좀 더 싱싱하게 보관이 가능해요.\n" +
                        "☝Tip. 양상추 겉잎에 농약이 많이 묻어 있으니 떼어내거나 꼼꼼히 씻어 사용하세요.(보관 기간 : 2~3주 정도)\n" +
                        "\n"+
                        "[남은 양상추 보관법]\n" +
                        "1. 밑동에 살짝 칼집을 넣어 손으로 쪼개주세요.\n" +
                        "☝Tip. 칼이 닿으면 갈변되기 쉬워 칼 사용은 최소화해주세요.\n" +
                        "2. 양상추의 밑동을 물에 적신 키친타월로 감싼 뒤 랩으로 한 번 더 감싸 냉장 보관해 주세요. (보관 기간 : 2주 정도)\n" +
                        "\n" +
                        "[손질한 양상추 보관법]\n" +
                        "1. 밀폐 용기에 키친타월을 깔고 손질한 양상추를 담아주세요.\n" +
                        "2. 키친타월로 양상추를 한 번 더 덮고, 뚜껑을 닫아 냉장 보관해 주세요. (보관 기간 : 1주 정도)");
                break;
            case("당근") :
                imageView8.setImageResource(R.drawable.carrot);
                textView4.setText("\n"+
                        "[흙당근 보관법]\n" +
                        "1.  당근을 세척하지 않고 신문지에 감싸 야채 칸에 냉장 보관해 주세요.\n" +
                        "2.  보관 시 당근이 자란 모양대로 세워서 보관해 주세요.\n" +
                        "☝Tip. 당근을 세워서 보관하면 신선도가 오래 유지된답니다.\n" +
                        "\n" +
                        "\n" +
                        "[세척당근 보관법]\n" +
                        "1. 당근의 껍질을 솔로 가볍게 문지른 후 흐르는 물에 씻어 주세요.\n" +
                        "☝Tip. 솔로 제거 되지 않는 상처나 흙은 칼로 긁어 제거하세요.\n" +
                        "2. 세척한 당근의 물기를 제거하고, 키친타올로 감싸 밀폐용기에 담아주세요.\n" +
                        "3. 당근이 자란 모양대로 세워서 냉장 보관해 주세요.\n" +
                        "☝Tip. 당근을 세워서 보관하면 신선도가 오래 유지 된답니다.");
                break;
            case("오이") :
                imageView8.setImageResource(R.drawable.cucumber);
                textView4.setText("\n"+
                        "[실온 보관법]\n" +
                        "- 바구니에 담아 서늘하고 통풍이 잘되는 곳에 보관해 주세요.\n" +
                        "(보관 기간 : 2~3일 정도 *여름철 제외)\n" +
                        "\n" +
                        "[냉장 보관법]\n" +
                        "1. 오이를 신문지로 감싼 후 밀폐 용기에 담아 냉장 보관해 주세요.\n" +
                        "☝Tip. 꼭지가 위로 오도록 세워서 보관하면 신선함이 더 오래 가요\n" +
                        "\n" +
                        "[냉동 보관법]\n" +
                        "1. 사용 후 남은 오이가 있다면, 0.5cm 두께로 썰어 소금 1작은술을 넣고 5~10분간 절여 주세요.\n" +
                        "2. 절인 오이의 물기를 꽉 짜서 밀폐 용기에 담아 냉동 보관해 주세요.\n" +
                        "☝Tip. 절여서 냉동하면 오이의 식감을 살릴 수 있어요.\n");
                break;
            case("계란") :
                imageView8.setImageResource(R.drawable.eggs);
                textView4.setText("\n"+
                        "1. 달걀의 둥근 부분이 위로 가게 하여 달걀판과 뚜껑을 지퍼백이나 밀폐용기 크기에 맞게 잘라 넣어 보관해 주세요.\n" +
                        "☝Tip .달걀은 냉장고 냄새를 흡수하기 때문에 밀폐용기에 담아 보관하는 것이 좋아요.\n" +
                        "☝Tip .둥근 부분에 신선도를 유지시키는 호흡 공간이 있어 둥근 부분을 위로 보관하는 것이 좋아요.\n" +
                        "\n" +
                        "2. 씻어서 보관하지 않고 먹기 직전에 씻는 것이 좋아요.\n" +
                        "☝Tip .달걀 껍질 표면에는 보호막이 있어 씻어서 보관하면 세균 침투가 쉬워져요.\n" +
                        "\n" +
                        "3.냉장고 안쪽에 보관하는 것이 싱싱하고 오래 보관할 수 있어요.\n" +
                        "☝Tip .냉장고 문쪽은 온도 변화가 심해 안쪽에 보관하는 것이 좋아요.");
                break;
            case("팽이버섯") :
                imageView8.setImageResource(R.drawable.enoki);
                textView4.setText("\n"+
                        "[냉장 보관법]\n" +
                        "1. 흙을 손으로 가볍게 털어낸 후, 신문지로 감싸 주세요.\n" +
                        "2. 밀폐 용기에 팽이버섯을 넣어 냉장 보관해 주세요.\n" +
                        "(1~2주 정도 보관 가능해요)\n" +
                        "☝Tip. 팽이송이버섯은 장기간 보관할 시 향과 맛이 떨어질 수 있어요\n" +
                        "\n" +
                        "[냉동 보관법]\n" +
                        "1. 끝부분을 칼로 자른 뒤, 흐르는 물에 가볍게 헹궈 주세요.\n" +
                        "2. 밀폐 용기에 손질한 버섯을 담아 냉동 보관해 주세요.\n" +
                        "(1~2개월 정도 보관 가능해요)\n" +
                        "☝Tip. 찬물 1L에 소금 1작은술을 녹인 후 냉동된 팽이버섯을 넣고 천천히 해동시키면 향과 풍미를 보존할 수 있어요.");
                break;
            case("마늘") :
                imageView8.setImageResource(R.drawable.garlic);
                textView4.setText("\n"+
                        "[깐 마늘 보관법]\n" +
                        "\n" +
                        " 냉장 보관일 경우\n" +
                        "1. 밀폐용기에 설탕을 0.5~1cm 정도 깔고 키친타월을 2~3장 정도 깔아주세요.\n" +
                        "2.  깐 마늘을 넣고 키친타월로 한번 덮어준 후 뚜껑을 닫아 냉장 보관해 주세요.\n" +
                        "☝Tip. 설탕이 수분을 잡아주어 마늘이 무르는 것을 방지해줘요.\n" +
                        "☝Tip. 깐 마늘은 쉽게 무르고 곰팡이가 생길 수 있으니 가급적 빨리 (7일 이내) 사용해 주시고, 중간중간 키친타월을 교체하면 더 오래 보관이 가능해요.\n" +
                        "\n" +
                        "냉동 보관일 경우\n" +
                        "1. 깐 마늘을 물에 깨끗이 씻어 꼭지를 칼로 제거해 주세요.\n" +
                        "2. 마늘의 물기를 제거한 후 밀폐용기에 담아 냉동 보관해 주세요.");
                break;
            case("대파") :
                imageView8.setImageResource(R.drawable.leek);
                textView4.setText("\n"+
                        "[실온 보관법]\n" +
                        "1. 뿌리 째 신문지에 싸서 햇빛이 들지 않고 통풍이 잘 되는 상온에 세워 보관해 주세요.\n" +
                        "\n" +
                        "[냉장 보관법]\n" +
                        "1. 흙이 묻은 채로 신문지에 싸서 냉장 보관해 주세요.\n" +
                        "2. 씻은 대파의 흰 대와 줄기 부분은 따로 밀폐용기에 담아 냉장 보관해 주세요.\n" +
                        "☝ Tip. 물에 닿을 경우 쉽게 상할 수 있으므로 되도록 빨리 이용하는 것이 좋아요.\n" +
                        "\n" +
                        "[냉동 보관법]\n" +
                        "1. 용도에 맞게 썰은 파는 밀폐용기에 담아 냉동실에 보관해 주세요.\n" +
                        "☝ Tip.길게 썰기 - 육수, 수육용 / 어슷 썰기 - 국, 찌개용 / 파 뿌리 - 육수용");
                break;
            case("우유") :
                imageView8.setImageResource(R.drawable.milk);
                textView4.setText("\n"+
                        "1. 선반 위쪽보다는 비교적 온도가 낮은 아래쪽에 보관해 주세요.\n" +
                        "☝Tip. 우유를 냉장고 문에 보관하지 마세요");
                break;
            case("양파") :
                imageView8.setImageResource(R.drawable.onion);
                textView4.setText("\n"+
                        "[냉장 보관법]\n" +
                        "1. 양파 껍질을 제거 한 뒤 물로 씻어 주세요.\n" +
                        "2. 키친타올로 수분을 닦은 후 랩으로 싸주세요.\n" +
                        "☝Tip.랩으로 감싸면 수분 증발을 막아주고 냉장고 냄새가 배는 것을 방지해 줘요.\n" +
                        "\n" +
                        "[냉동 보관법]\n" +
                        "1. 양파를 원하는 모양으로 썰어 주세요.\n" +
                        "2. 밀폐용기에 담아 냉동 보관해 주세요.");
                break;
            case("파프리카") :
                imageView8.setImageResource(R.drawable.paprika);
                textView4.setText("\n"+
                        "[냉장 보관법]\n" +
                        "1. 파프리카를 씻지 않은 채로 신문지로 감싸 밀폐용기에 담아 냉장보관해 주세요.\n" +
                        "☝Tip. 파프리카는 오래 보관하기 어려우므로 소량만 구입해서 사용하세요.\n" +
                        "\n" +
                        "[손질된 파프리카 보관법]\n" +
                        "1. 깨끗이 씻은 파프리카는 씨를 제거하고 키친타올로 물기를 닦아주세요.\n" +
                        "2. 용도에 맞게 손질 후 밀폐용기에 담아 냉장 보관해 주세요.");
                break;
            case("감자") :
                imageView8.setImageResource(R.drawable.potato);
                textView4.setText("\n"+
                        "[실온 보관법]\n" +
                        "1. 상처가 나거나 썩은 감자가 섞여 있으면 다른 감자까지 썩을 수 있으니 보관 전에 모두 골라내주세요.\n" +
                        "2. 서늘하고 바람이 잘 통하며 밝은 곳에 두되, 직사광선을 받지 않도록 주의해 주세요.\n" +
                        "☝ Tip. 감자를 하나씩 신문지에 싸서 보관하면 더욱 오래 보관이 가능해요.");
                break;
            case("깻잎") :
                imageView8.setImageResource(R.drawable.sesame_leaf);
                textView4.setText("\n"+
                        "[냉장 보관법]\n" +
                        "1. 깻잎을 물에 5분간 담근 후 꺼내어 흐르는 물에 씻어주세요. \n" +
                        "2. 물기를 제거한 깻잎의 줄기 끝을 잘라주세요. \n" +
                        "3. 컵에 생수를 1~2cm 정도 채우고 깻잎 줄기가 아래로 가도록 넣어주세요. \n" +
                        "☝ Tip. 물을 적신 키친타월로 줄기를 감싸는 방법도 있어요 \n" +
                        "4. 지퍼백에 밀봉하여 냉장고 문에 세워서 보관해 주세요. \n" +
                        "☝Tip. 냉기가 강하면 쉽게 얼기 때문에 가급적 냉기가 약한 쪽에 보관하세요.");
                break;
            case("고구마") :
                imageView8.setImageResource(R.drawable.sweet_potato);
                textView4.setText("\n"+
                        "[실온 보관법]\n" +
                        "1. 냉장고보다 10℃ 정도 기온의 서늘하고 어두운 곳에 보관하는 것이 좋아요.\n" +
                        "2. 신문지나 키친타월 등으로 감싸고 통풍이 잘 되는 곳에 보관하는 것이 좋아요.\n" +
                        "\n" +
                        "[냉동 보관하는 방법]\n" +
                        "1. 찐 고구마를 냉장 보관하면 맛과 풍미가 떨어질 수 있으므로, 비닐팩에 담아 냉동 보관하는 것이 좋아요\n");
                break;
            case("두부") :
                imageView8.setImageResource(R.drawable.tofu);
                textView4.setText("\n"+
                        "[냉장 보관법]\n" +
                        "1. 밀폐 용기에 생수 2~300ml와 소금 1작은술을 넣어 녹여주세요.\n" +
                        "2. 흐르는 물에 두부를 헹군 뒤, 소금물에 담아 냉장 보관해 주세요.\n" +
                        "☝Tip. 소금은 두부의 부드러운 식감을 유지해주고 미생물 번식을 막아줘요\n" +
                        "☝Tip. 보관기간 : 약 5일\n" +
                        "\n" +
                        "[냉동 보관법]\n" +
                        "1. 밀폐 용기에 담아 냉동 보관해 주세요.\n" +
                        "☝Tip. 용기를 끓는 물에 넣어 20분 정도 두면 간편하게 해동 할 수 있어요.\n" +
                        "2. 해동된 두부는 손으로 물기를 짜주세요.\n" +
                        "☝Tip. 보관기간 : 약 1~2개월");
                break;
            case("토마토") :
                imageView8.setImageResource(R.drawable.tomato);
                textView4.setText("\n"+
                        "[냉장 보관법]\n" +
                        "1. 깨끗이 씻은 토마토는 물기를 없애고 꼭지를 떼어내 주세요.\n" +
                        "2. 랩으로 하나씩 감싼 뒤 간격을 띄워 야채 칸에 보관해 주세요.\n" +
                        "☝Tip. 곰팡이가 잘 생길 수 있어 꼭지를 제거하는 게 좋아요.\n" +
                        "\n" +
                        "[냉동 보관]\n" +
                        "1. 십자로 칼집을 낸 뒤 끓는 물에 살짝 데쳐 껍질을 제거해주세요.\n" +
                        "2. 지퍼백이나 보관용기에 한 번 먹을 양만큼 소분해서 보관해 주세요.\n" +
                        "☝Tip. 냉동 저장을 할 때는 1~2개월 정도 보관하시는 게 좋아요. 토마토 주스나 빙수 등을 만들 때 사용해요.");
                break;
        }

        ImageButton backicon = findViewById(R.id.backicon);
        backicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void deleteDataFromTable() {
        Log.d("DeleteData", "Deleting data from table: " + tableName);
        Log.d("DeleteData", "Deleting data with name: " + name);

        mDbOpenHelper.deleteColumn(name);
        mDbOpenHelper.deleteColumnfrz(name);
        mDbOpenHelper.deleteColumnroom(name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDbOpenHelper != null) {
            mDbOpenHelper.close();
        }
    }
}

