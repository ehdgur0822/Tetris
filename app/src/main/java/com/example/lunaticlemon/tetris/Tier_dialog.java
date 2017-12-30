package com.example.lunaticlemon.tetris;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;

/**
 * Created by lemon on 2017-06-26.
 */

public class Tier_dialog extends Dialog {
    Context con;
    int rank;

    LinearLayout bronze, silver, gold, platinum, dia, master, grand;

    public Tier_dialog(@NonNull Context context, String _rank) {
        super(context);

        this.setCanceledOnTouchOutside(true); // 다이알로그 바깥영역 터치시, 다이알로그 닫기
        this.setCancelable(true); // 백키로 다이알로그 닫기

        con = context;
        rank = Integer.parseInt(_rank);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tier_dialog);

        bronze = (LinearLayout) findViewById(R.id.LinearBronze);
        silver = (LinearLayout) findViewById(R.id.LinearSilver);
        gold = (LinearLayout) findViewById(R.id.LinearGold);
        platinum = (LinearLayout) findViewById(R.id.LinearPlatinum);
        dia = (LinearLayout) findViewById(R.id.LinearDia);
        master = (LinearLayout) findViewById(R.id.LinearMaster);
        grand = (LinearLayout) findViewById(R.id.LinearGrand);

        if(rank >=0 && rank <= 500)
        {
            bronze.setBackgroundColor(0xFFFF5A5A);
        }
        else if(rank >= 501 && rank <= 1000)
        {
            silver.setBackgroundColor(0xFFFF5A5A);
        }
        else if(rank >= 1001 && rank <= 1500)
        {
            gold.setBackgroundColor(0xFFFF5A5A);
        }
        else if(rank >= 1501 && rank <= 2000)
        {
            platinum.setBackgroundColor(0xFFFF5A5A);
        }
        else if(rank >= 2001 && rank <= 3000)
        {
            dia.setBackgroundColor(0xFFFF5A5A);
        }
        else if(rank >= 3001 && rank <= 4500)
        {
            master.setBackgroundColor(0xFFFF5A5A);
        }
        else if(rank > 4500)
        {
            grand.setBackgroundColor(0xFFFF5A5A);
        }
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
