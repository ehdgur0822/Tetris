package com.example.lunaticlemon.tetris;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by lemon on 2017-06-23.
 */

public class rank_loading_dialog extends Dialog {

    Context con;
    Button btn_cancel;
    String rank_type;

    public rank_loading_dialog(@NonNull Context context, String _rank_type) {
        super(context);

        this.setCanceledOnTouchOutside(false); // 다이알로그 바깥영역 터치시, 다이알로그 닫히지 않기
        this.setCancelable(false); // 백키로 다이알로그 닫기

        con = context;
        rank_type = _rank_type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rank_loading_dialog);

        final Connect task = new Connect();
        task.execute();

        View.OnClickListener ClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        if(rank_type.startsWith("singlematch"))
                            RankClient.disConnect = true;
                        else if(rank_type.startsWith("teammatch"))
                            TeamClient.disConnect = true;
                        task.cancel(true);
                        dismiss();
                        break;
                    default:
                        break;
                }
            }
        };

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(ClickListener);
        if(!rank_type.equals("singlematch") && !rank_type.equals("teammatch"))
            btn_cancel.setVisibility(View.INVISIBLE);
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

    class Connect extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dismiss();
        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("rank_loading","start");

            if(rank_type.startsWith("single")) {
                while (!RankGameActivity.client.allSet) {

                }
            }
            else if(rank_type.startsWith("team"))
            {
                while (!TeamGameActivity.teamclient.allSet) {

                }
            }

            Log.d("rank_loading","end");
            return null;
        }
    }
}