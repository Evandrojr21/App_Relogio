package com.example.apprelogio;

import androidx.appcompat.app.AppCompatActivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.apprelogio.databinding.ActivityFullscreenBinding;

import org.w3c.dom.Text;

import java.util.Calendar;

public class FullscreenActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Handler handler = new Handler();
    private Runnable runnable;
    private boolean runnableStop = false;
    private boolean cb_bateriaChecked;

    private BroadcastReceiver bateriaReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int nivel_bateria = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);

            mViewHolder.tv_nivelBaterioa.setText(String.valueOf(nivel_bateria)+"%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mViewHolder.tv_horasMinutos = findViewById(R.id.tv_horasMinutos);
        mViewHolder.tv_segundos = findViewById(R.id.tv_segundos);
        mViewHolder.cb_nivelBateria = findViewById(R.id.cb_nivelBateria);
        mViewHolder.tv_nivelBaterioa = findViewById(R.id.tv_nivelBateria);
        mViewHolder.img_v_preferencs = findViewById(R.id.img_v_preferencs);
        mViewHolder.img_v_sair = findViewById(R.id.img_v_sair);
        mViewHolder.ln_menu = findViewById(R.id.ln_menu);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        registerReceiver(bateriaReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        mViewHolder.ln_menu.animate().translationY(500);
        mViewHolder.cb_nivelBateria.setChecked(true);
        mViewHolder.cb_nivelBateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_bateriaChecked){
                    cb_bateriaChecked = false;
                    mViewHolder.tv_nivelBaterioa.setVisibility(View.GONE);
                }else{
                    cb_bateriaChecked = true;
                    mViewHolder.tv_nivelBaterioa.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewHolder.img_v_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewHolder.ln_menu.animate()
                        .translationY(mViewHolder.ln_menu.getMeasuredHeight())
                        .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            }
        });

        mViewHolder.img_v_preferencs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewHolder.ln_menu.setVisibility(View.VISIBLE);
                mViewHolder.ln_menu.animate().translationY(0)
                        .setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        runnableStop = false;
        AtualizarHora();
    }

    @Override
    protected void onStop() {
        super.onStop();
        runnableStop = true;
    }

    private void AtualizarHora(){
        runnable = new Runnable() {
            @Override
            public void run() {
                if (runnableStop)
                    return;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());

                String horasMinutosFormatado = String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));
                String segundosFormatados = String.format("%02d",calendar.get(Calendar.SECOND));

                mViewHolder.tv_horasMinutos.setText(horasMinutosFormatado);
                mViewHolder.tv_segundos.setText(segundosFormatados);

                long agora = SystemClock.uptimeMillis();
                long proximo = agora + (1000-(agora%1000));

                handler.postAtTime(runnable,proximo);

            }
        };
     runnable.run();
    }
    private static class ViewHolder{
        TextView tv_horasMinutos;
        TextView tv_segundos;
        CheckBox cb_nivelBateria;
        TextView tv_nivelBaterioa;
        ImageView img_v_preferencs, img_v_sair;
        LinearLayout ln_menu;


    }
}