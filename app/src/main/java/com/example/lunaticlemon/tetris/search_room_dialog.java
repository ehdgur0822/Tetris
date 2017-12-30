package com.example.lunaticlemon.tetris;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by lemon on 2017-06-10.
 */

public class search_room_dialog extends Dialog {

    EditText IP;
    Button btn_search;
    Context con;

    public search_room_dialog(@NonNull Context context) {
        super(context);

        this.setCanceledOnTouchOutside(false); // 다이알로그 바깥영역 터치시, 다이알로그 닫히지 않기
        this.setCancelable(true); // 백키로 다이알로그 닫기

        con = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_room_dialog);

        View.OnClickListener ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.button_search:
                        String ip = IP.getText().toString();
                        if(checkIP(ip)) {
                            Toast toast = Toast.makeText(con, "search를 시작합니다.", Toast.LENGTH_SHORT);
                            toast.show();
                            enter_room_dialog room_dialog = new enter_room_dialog(con, ip);
                            room_dialog.setTitle("MakeRoom");
                            room_dialog.show();
                            dismiss();
                        }
                        else
                        {
                            Toast toast = Toast.makeText(con, "잘못된 tag 형식입니다.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        btn_search = (Button) findViewById(R.id.button_search);
        btn_search.setOnClickListener(ClickListener);
        IP = (EditText)findViewById(R.id.editTextIP);
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    protected boolean checkIP(String ip)
    {
        int num = 0;


        for(int i=0;i<ip.length();i++)
        {
            if(ip.charAt(i) == '.')
            {
                num++;
            }
        }

        if(num == 4)
            return true;

        return false;
    }
}
