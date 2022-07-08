package com.example.pricerunner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private CodeScanner codeScanner;
    public ListView listView;
    public TextView textView;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        textView = (TextView) findViewById(R.id.text);
        if (checkPermission()) {
            scanCode();
        } else {
            requestPermission();
            if (checkPermission()) {
                scanCode();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    public void scanCode() {
        textView.setText("Lütfen bir kod taratın..");
        CodeScannerView codeScannerView = (CodeScannerView) this.findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this, codeScannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(result.getText());
                        String url = "https://api.barcodespider.com/v1/lookup?token=a986e0dad1eb10de9238&upc=" + result.getText();
                        codeScanner.startPreview();
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONArray array = response.getJSONArray("Stores");
                                            List<String> list = new ArrayList<>();
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject obj = (JSONObject) array.get(i);
                                                list.add("Ürün Adı: " + obj.getString("title") + " Market: " + obj.getString("store_name") + " Fiyat: " + obj.getString("price"));
                                            }
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
                                            listView.setAdapter(adapter);
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                                    }
                                });
                        queue.add(jsonObjectRequest);
                    }
                });
            }
        });
        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }
}