package com.campandroid.cardtest;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/*
*     화면의 내용을 이미지로 만들고
*     파일로 저장한 후,
*     공유를 하는 예제
*
*     활용:
*     "나만의 card 보내기 앱"
*
* */

public class MainActivity extends Activity {

	// 1. 화면에 보여지는 3개의 버튼을 위한 변수를 선언
    Button btnShare  = null;   // 공유버튼
	Button btnDelete = null;   // 지우기 버튼
	Button btnImage  = null;   // 이미지 변경버튼

    // 6. Activity가 생성될 때, 1번 실행되는 메소드
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // activity_main.XML의 정보로 메모리에 위젯들을 생성함
        setContentView(R.layout.activity_main);

        // 버튼을 가져오고. click 핸들러를 구현한다.
        btnShare = (Button)findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new OnClickListener(){

            // click시 실행됨
			@Override
			public void onClick(View v) {

                // EditText의 화면을 File로 저장하고
                // 저장된 파일을 공유한다.
                File f = SaveImage();
				ShareImage(f);
				
			}}
        
        );

        // 버튼을 가져오고. click 핸들러를 구현한다.
        btnDelete = (Button)findViewById(R.id.btnClear);
        btnDelete.setOnClickListener(new OnClickListener(){

            // click시 실행됨
            @Override
			public void onClick(View v) {

                // EditText의 배경과 Text를 지운다.
                DeleteTextAndImageFile();
			}}
        
        );

        // 버튼을 가져오고. click 핸들러를 구현한다.
        btnImage = (Button)findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SelectBackground();
			}}
        
        );

    }

    // 2. EditText의 화면을 File로 저장하는 메소드 구현
    File SaveImage(){

        // EditText를 가져와 et에 저장한다.
    	EditText et = (EditText) findViewById(R.id.editText1);

        // Android에서 반드시하라고 하는 규격임. EditText의 이미지를 가져가려면
        // 아래 2개의 메소드를 실행하며 true 값을 넘겨주어야 함.
        et.setDrawingCacheEnabled(true);
    	et.buildDrawingCache(true);

        // et(EditText)의 메소드인 getDrawingCache()를 호출하고 그 결과값을
        // Bitmap 클래스의 createBitmap()을 호출한다.
        // 결과값은 Bitmap 객체이다.
    	Bitmap b = Bitmap.createBitmap(et.getDrawingCache());

        // Bitmap 객체 b를 얻었으므로 bt의 화면메모리를 다시 닫아야 한다.
        // 아래의 코드는 반드시 실행되어야 한다.
    	et.setDrawingCacheEnabled(false);
    	et.buildDrawingCache(false);


    	String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
    			"/campandroid";
    	File dir = new File(file_path);
    	if(!dir.exists())
    	    dir.mkdirs();
    	File file = new File(dir, "test.png");
    	FileOutputStream fOut;
    	try {
    		fOut = new FileOutputStream(file);
    		b.compress(Bitmap.CompressFormat.PNG, 85, fOut);
    		fOut.flush();
    		fOut.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return file;
    }

    //3. 파일을 공유하는 메소드 구현
    void ShareImage(File f){

        // 파일정보를 uri로 만든다.
    	Uri uri = Uri.fromFile(f);

        // "image/*" type으로 지정하고 action은 Intent.ACTION_SEND인 Intent를 만든다.
    	Intent intent = new Intent();
    	intent.setAction(Intent.ACTION_SEND);
    	intent.setType("image/*");

        // 추가정보를 저장한다.
    	intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
    	intent.putExtra(android.content.Intent.EXTRA_TEXT, "");

        // 파일정보를 저장한다.
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // 저장된 intent 정보로 Activity를 실행한다.
        startActivity(Intent.createChooser(intent, "Share Cover Image"));
    }

    // 4. EditText의 배경을 바꾸는 메소드 구현
    void SelectBackground(){
		final CharSequence[] items  =  { "하트", "곰", "천사"};
		final int[]          Images = { R.drawable.heart, R.drawable.bear, R.drawable.angel};

        // Dialog를 만들어주는 builder를 생성.
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);     
    	// Dialog의 타이틀바 지정
        builder.setTitle("배경설정")
        // item의 값을 넣고 리스트로 보여주며 선택 시, 행동을 지정
    	.setItems(items, new DialogInterface.OnClickListener(){
    	    // click이 되었을 경우,
            public void onClick(DialogInterface dialog, int index){
    	    	
    	    	// EditText를 가져온다.
    	    	EditText et = (EditText) findViewById(R.id.editText1);
    	    	// 선택된 리스트 번호에 맞는 Images 배열내의 이미지 ID 값을 가져온다.
                // 그리고 setBackgroundResource로 EditText인 et의 배경을 바꾼다.
    	    	et.setBackgroundResource(Images[index]);

                // Dialog를 종료한다.
                dialog.dismiss();
    	    }
    	});

        // Dialog 생성 및 show <- 보이기
    	AlertDialog dialog = builder.create();
    	dialog.show();

	}

    // 5. EditText의 글자와 배경이미지를 지운다.
	void DeleteTextAndImageFile(){

        // 경로명 설정
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
    			"/campandroid";
    	File dir = new File(file_path);

        // 경로명 + 파일명
        File file = new File(dir, "test.png");
    	
    	try{
            // 지우기 <-- Error가 발생할 수 있으므로 try catch안에 코딩해야 한다.
            file.delete();
    	} catch(Exception e){
    		
    	}

    	// EditText의 문자를 지운다.
    	EditText et = (EditText) findViewById(R.id.editText1);
    	et.setText("");
    }
}
