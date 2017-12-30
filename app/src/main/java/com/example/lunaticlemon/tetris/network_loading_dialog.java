package com.example.lunaticlemon.tetris;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

/**
 * Created by lemon on 2017-07-05.
 */

public class network_loading_dialog extends Dialog {

    Context con;
    Button btn_cancel;

    public network_loading_dialog(@NonNull Context context) {
        super(context);

        this.setCanceledOnTouchOutside(false); // 다이알로그 바깥영역 터치시, 다이알로그 닫히지 않기
        this.setCancelable(false); // 백키로 다이알로그 닫기

        con = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rank_loading_dialog);

        View.OnClickListener ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        dismiss();
                        break;
                    default:
                        break;
                }
            }
        };

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(ClickListener);
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
}