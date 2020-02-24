package pmd.di.ubi.pt.projectofinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class ActivityRegistro extends AppCompatActivity {
    private ArrayList<Desporto> desportoList;
    private Button btnProximoPasso;
    private CheckBox c1,c2,c3,c4,c5,c6;
    private Spinner spinner ;
    private Boolean c6IsCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        c1 = findViewById(R.id.cb1);
        c2 = findViewById(R.id.cb2);
        c3 = findViewById(R.id.cb3);
        c4 = findViewById(R.id.cb4);
        c5 = findViewById(R.id.cb5);
        c6 = findViewById(R.id.cb6);
        spinner= (Spinner) findViewById(R.id.spinner);
        btnProximoPasso = (Button)findViewById(R.id.btn_proximo_passo);
        c6IsCheck= false;
        c6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    spinner.setVisibility(View.VISIBLE);
                    inicializarSpinnerDesporto();
                }else {
                    spinner.setVisibility(View.GONE);
                }
                c6IsCheck= isChecked;
            }
        });

        btnProximoPasso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProximoPasso = new Intent(ActivityRegistro.this,ActivityRegistro2.class);
                StringBuilder desportoString = new StringBuilder();
                if(c6IsCheck){
                        for (Desporto desporto :desportoList){
                            if(desporto.isSelected()){
                                desportoString.append("/").append(desporto.getTitle());
                            }
                        }
                        intentProximoPasso.putExtra("Doencas",getDoencas()+ "(" + desportoString.toString() + ")");
                    //Log.i("DOENCASuser",getDoencas()+ "(" + desportoString.toString() + ")");
                }else {
                    intentProximoPasso.putExtra("Doencas",getDoencas());
                    //Log.i("DOENCASuser",getDoencas());
                }
                startActivity(intentProximoPasso);
            }
        });
    }

    public void inicializarSpinnerDesporto(){
        final String[] spinnerElementos = {
                "Desportos","Voleibol", "Andebol", "Futebol ou Futsal", "Basquetebol", "Outro?"};

        desportoList = new ArrayList<>();

        for (int i = 0; i < spinnerElementos.length; i++) {
            Desporto desporto = new Desporto();
            desporto.setTitle(spinnerElementos[i]);
            desporto.setSelected(false);
            desportoList.add(desporto);
        }
        spinner.setAdapter(new SpinnerAdapterDesporto(ActivityRegistro.this, 0, desportoList));
    }


    public String getDoencas(){
        String doencas = "";

        if(c1.isChecked()){
            doencas = c1.getText().toString();
        }
        if(c2.isChecked()){
            doencas = doencas +","+ c2.getText().toString();
        }
        if(c3.isChecked()){
            doencas = doencas +","+ c3.getText().toString();
        }
        if(c4.isChecked()){
            doencas = doencas +","+ c4.getText().toString();

        }
        if(c5.isChecked()){
            doencas = doencas +","+ c5.getText().toString();
        }
        if(c6.isChecked()){
            doencas = doencas +","+ c6.getText().toString();
        }
        return doencas;
    }
}
