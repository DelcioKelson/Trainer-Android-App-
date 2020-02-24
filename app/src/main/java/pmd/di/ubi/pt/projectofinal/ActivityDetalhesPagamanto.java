package pmd.di.ubi.pt.projectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityDetalhesPagamanto extends AppCompatActivity {

    private TextView tvId, tvValor,tvStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_pagamanto);
        tvId = findViewById(R.id.tv_id);
        tvValor = findViewById(R.id.tv_valor);
        tvStatus = findViewById(R.id.tv_status);

        Intent intent = getIntent();

        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("DetalhesPagamento"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("valor"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDetails(JSONObject response, String valor) {
        try {
            tvId.setText(response.getString("id"));
            tvValor.setText(response.getString(String.format("$%s",valor)));
            tvStatus.setText(response.getString("state"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
