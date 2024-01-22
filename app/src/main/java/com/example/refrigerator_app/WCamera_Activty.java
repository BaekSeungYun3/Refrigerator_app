package com.example.refrigerator_app;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.refrigerator_app.ml.ModelFood;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WCamera_Activty extends AppCompatActivity {
    // true  : Camera On  : 카메라로 직접 찍어 문자 인식
    // false : Camera Off : 샘플이미지를 로드하여 문자 인식

    private boolean CameraOnOffFlag = true;

    private TessBaseAPI m_Tess; //Tess API reference
    private MessageHandler m_messageHandler;

    private String mDataPath = ""; //언어데이터가 있는 경로
    private String mCurrentPhotoPath; // 사진 경로
    private final String[] mLanguageList = {"eng","kor"}; // 언어
    // View
    private Context mContext;
    private ImageButton m_btnOCR; // 인식하기 위해 사진찍는 버튼
    private TextView m_ocrTextView; // 결과 변환 텍스트
    private ImageView m_ivImage; // 찍은 사진
    private Bitmap image; //사용되는 이미지

    public static WCamera_Activty context;

    TextView result, confidence;
    ImageView imageView;
    int imageSize = 64;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wcamera_activty);

        context = this;

        // 유통기한
        mContext = this;
        m_ivImage = findViewById(R.id.ocrView); // 하단 뷰
        m_ocrTextView = findViewById(R.id.resultOCR); // 빌림 : ocr 결과값

        m_messageHandler = new MessageHandler();

        if(CameraOnOffFlag) //카메라로
        {
            // 사전체크
            PermissionCheck();
            Tesseract();
        }

        // 카메라
        result = findViewById(R.id.resulttxt);
        /*        confidence = findViewById(R.id.resultOCR);*/
        imageView = findViewById(R.id.imgView);

        // 화면 전환 시 바로 촬영
        //@RequiresApi(api = Build.VERSION_CODES.M)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            } else {
                //Request camera permission if we don't have it.
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        }

        //뒤로가기 버튼
        ImageButton backicon2 = (ImageButton) findViewById(R.id.backicon2);
        backicon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // 다시 촬영 버튼 눌렀을 때 재촬영
        TextView camerBtn = (TextView) findViewById(R.id.cameraBtn);
        camerBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        // 유통기한 인식 버튼
        TextView ocr_btn = (TextView) findViewById(R.id.ocr_btn);
        ocr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCustomDialog();
                /*                dispatchTakePictureIntent();*/
            }
        });
    }

    // 유통기한 버튼 누를 때 다이얼로그
    public void onCustomDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.activity_dialogadd);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton camerabtn = (ImageButton) dialog.findViewById(R.id.camraButton);
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        ImageButton writebtn = (ImageButton) dialog.findViewById(R.id.writeButton);
        writebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = result.getText().toString();
                Intent intent = new Intent(getApplicationContext(),Wadd_Activity.class);
                Bundle b = new Bundle();
                b.putString("Items", item);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    public void PermissionCheck() {
        /**
         * 6.0 마시멜로우 이상일 경우에는 권한 체크후 권한을 요청한다.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // 권한 없음
                ActivityCompat.requestPermissions(WCamera_Activty.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ConstantDefine.PERMISSION_CODE);
            } else {
                // 권한 있음
            }
        }
    }


    //기본 카메라를 실행하여 접근 권한을 요청하고 사진 촬영
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 사진파일을 생성한다.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // 사진파일이 정상적으로 생성되었을때
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName()+".fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ConstantDefine.ACT_TAKE_PIC); //SubActivity 실행
            }
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void Tesseract() {
        //언어파일 경로
        mDataPath = getFilesDir() + "/tesseract/";

        //트레이닝데이터가 카피되어 있는지 체크
        String lang = "";
        for (String Language : mLanguageList) {
            checkFile(new File(mDataPath + "tessdata/"), Language);
            lang += Language + "+";
        }
        m_Tess = new TessBaseAPI();
        m_Tess.init(mDataPath, lang);
    }

    //언어 데이터 파일의 존재 유무 확인
    private void checkFile(File dir, String Language) {
        //디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(Language);
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if (dir.exists()) {
            String datafilepath = mDataPath + "tessdata/" + Language + ".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(Language);
            }
        }
    }

    //언어 데이터 파일 카피
    private void copyFiles(String Language) {
        try {
            String filepath = mDataPath + "/tessdata/" + Language + ".traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/"+Language+".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class OCRThread extends Thread
    {
        private Bitmap rotatedImage;
        OCRThread(Bitmap rotatedImage)
        {
            this.rotatedImage = rotatedImage;
        }

        @Override
        public void run() {
            super.run();
            // 사진의 글자를 인식해서 옮긴다
            String OCRresult = null;
            m_Tess.setImage(rotatedImage);
            OCRresult = m_Tess.getUTF8Text();

            Message message = Message.obtain();
            message.what = ConstantDefine.RESULT_OCR;
            message.obj = OCRresult;
            m_messageHandler.sendMessage(message);

        }
    }
    //endregion

    //region Handler
    public class MessageHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case ConstantDefine.RESULT_OCR:
                    TextView OCRTextView = findViewById(R.id.resultOCR);
                    OCRTextView.setText(String.valueOf(msg.obj)); //텍스트 변경

                    break;

            }
            loadingEnd();
        }
    }

    // 카메라, 유통기한 합친 onActivityResult 코드
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            classifyImage(image);
        }

        // 유통기한
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantDefine.PERMISSION_CODE) {
            Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == ConstantDefine.ACT_TAKE_PIC) {
            if ((resultCode == RESULT_OK)) {
                try {
                    File file = new File(mCurrentPhotoPath);
                    Bitmap rotatedBitmap = null;
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            FileProvider.getUriForFile(WCamera_Activty.this,
                                    getApplicationContext().getPackageName() + ".fileprovider", file));

                    // 회전된 사진을 원래대로 돌려 표시한다.
                    if (bitmap != null) {
                        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);
                        switch (orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(bitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(bitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(bitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = bitmap;
                        }
                        OCRThread ocrThread = new OCRThread(rotatedBitmap);
                        ocrThread.setDaemon(true);
                        ocrThread.start();
                        m_ivImage.setImageBitmap(rotatedBitmap);// 카메라로 찍은 사진을 뷰에 표시한다.
                        m_ocrTextView.setText(getResources().getString(R.string.LoadingMessage)); //인식된텍스트 표시
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    // 이미지를 원본과 같게 회전시킨다.
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void classifyImage(Bitmap image) {
        try {
            ModelFood model = ModelFood.newInstance(context);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 64, 64, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i ++) {
                for (int j = 0; j < imageSize; j ++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelFood.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i ++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {"계란", "고구마", "깻잎", "당근", "대파", "두부", "마늘", "바나나", "새송이 버섯",
                    "아보카도", "애호박", "양상추", "양파", "오이", "우유", "콩나물", "토마토", "파프리카", "팽이버섯"};

            result.setText(classes[maxPos]);
            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }
        // 텍스트 출력이 완료되면 add 페이지로 값을 넘겨줌
    public void loadingEnd(){
        String item = result.getText().toString();
        String ep = m_ocrTextView.getText().toString();
        Intent intent_CameraOcr = new Intent(getApplicationContext(),Wadd_Activity.class);
        Bundle b = new Bundle();
        b.putString("Items", item);
        b.putString("EP", ep);
        intent_CameraOcr.putExtras(b);
        startActivity(intent_CameraOcr);
    }
}