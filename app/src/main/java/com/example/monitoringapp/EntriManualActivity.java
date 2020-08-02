package com.example.monitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.monitoringapp.config.StringConfig;

import org.json.JSONObject;

public class EntriManualActivity extends AppCompatActivity {

    RelativeLayout simpan;
    TextView t_nopol;
    EditText e_nama,et_perusahaan;
    ImageView back;
    AlertDialog.Builder dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entri_manual);

        t_nopol             = findViewById(R.id.tv_nopol);
        back                = findViewById(R.id.back);
        simpan              = findViewById(R.id.simpan);
        e_nama              = findViewById(R.id.et_nama);
        et_perusahaan       = findViewById(R.id.et_perusahaan);

        Intent i = getIntent();
        final String nopol = i.getStringExtra("nopol");

        t_nopol.setText(nopol);
//        Log.e("CEKCEK", nopol);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_nama = e_nama.getText().toString().toUpperCase();
                String str_per  = et_perusahaan.getText().toString().toUpperCase();
                ijinkan(nopol,str_nama,str_per);
                finish();
                //onSimpan(nopol, str_nama);
            }
        });
    }

    public void ijinkan(String no_pol,String driver,String perusahaan)
    {
        AndroidNetworking.post(StringConfig.MASUK_MANUAL)
                .addBodyParameter("no_pol", no_pol)
                .addBodyParameter("driver", driver)
                .addBodyParameter("perusahaan", perusahaan)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(),response.optString("message"),Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.i("error",error.getMessage());
                    }
                });
    }
    /*private void onSimpan(String nopol, String nama) {
        dialog = new AlertDialog.Builder(EntriManualActivity.this);
        dialog.setCancelable(true);
        dialog.setMessage("Nopol : "+nopol+"\nNama : "+nama);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }*/
}
