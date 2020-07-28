package com.example.monitoringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.monitoringapp.config.StringConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView info;
    LinearLayout scan, manual;
    AlertDialog.Builder dialog;
    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        info = findViewById(R.id.iv_info);
        scan = findViewById(R.id.lay_2);
        manual = findViewById(R.id.lay_3);

        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TentangActivity.class);
                startActivity(intent);
            }
        });

        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogForm();
            }
        });
        scan.setOnClickListener(this);
    }

    private void DialogForm() {
        dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(true);
        View viewInflated = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
        // Set up the input
        final EditText nopol = viewInflated.findViewById(R.id.et_nopol);

        dialog.setView(viewInflated);

        dialog.setPositiveButton("Cek", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final String str_nopol = nopol.getText().toString().toUpperCase();
                cek_manual(str_nopol);
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*cek manual input*/
    public void cek_manual(final String no_pol)
    {
        AndroidNetworking.post(StringConfig.CEK_MANUAL)
                .addBodyParameter("no_pol", no_pol)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        String success = response.optString("success");
                        androidx.appcompat.app.AlertDialog.Builder al = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                        if (success.equals("1")){
                            /*jika terdapat record yang belum selesai*/
                            final String id_kunjungan = response.optString("id_kunjungan");
                            al.setTitle("Selesaikan Kunjungan?")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage("Detil :\nNomor Polisi : "+response.optString("no_pol")+
                                            "\nDriver/Pemilik  : "+response.optString("driver")+
                                            "\nTanggal Kunjungan : "+response.optString("tgl_kunjungan")+
                                            "\nJam Masuk : "+response.optString("jam_masuk")+
                                            "\nJenis Kunjungan : "+response.optString("jenis_kunjungan"))
                                    .setPositiveButton("Selesaikan Kunjungan", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            selesaikan(id_kunjungan);
                                        }
                                    })
                                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create().show();
                        }else{
                            /*AKSi input manual aktifkan dialog input driver*/
                            Intent i = new Intent(MainActivity.this,EntriManualActivity.class);
                            i.putExtra("nopol",no_pol);
                            startActivity(i);
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                    }
                });
    }

    /*selesaikan Kunjungan Manual*/
    public void selesaikan(String id_kunjungan)
    {
        AndroidNetworking.post(StringConfig.KELUAR_MANUAL)
                .addBodyParameter("id_kunjungan", id_kunjungan)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        androidx.appcompat.app.AlertDialog.Builder al = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                        al.setTitle("Status")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage(response.optString("message"))
                                .setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create().show();
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                scan(result.getContents());
                //get_data(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void scan(String no_pol)
    {
        AndroidNetworking.post(StringConfig.SCAN)
                .addBodyParameter("no_pol", no_pol)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        androidx.appcompat.app.AlertDialog.Builder al = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                        String success = response.optString("success");
                        final String no_pol   = response.optString("no_pol");
                        if (success.equals("1")){
                            al.setTitle("Kunjungan Selesai")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage(response.optString("message"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                        }else if(success.equals("2")){
                            al.setTitle("Kunjungan Baru")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage(
                                            "\nNomor Polisi : "+ response.optString("no_pol") +
                                                    "\nPemilik/Driver : "+response.optString("pemilik")+
                                                    "\n\npesan :\n"+response.optString("message"))
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                        }else{
                            al.setTitle("Detil Kendaraan")
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setMessage(response.optString("message"))
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                        Log.i("error",error.getMessage());
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_2:
                qrScan.initiateScan();
                break;
        }
    }
}
