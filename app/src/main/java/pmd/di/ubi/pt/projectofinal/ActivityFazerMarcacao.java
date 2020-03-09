package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityFazerMarcacao extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    private Spinner spHoras;
    private TextView tvPreco;
    private static TextView tvHoraInicio, tvDiaTreino;
    private PersonalTrainer personalTrainer;
    private String uuidPersonal;
    private Button btnTempoInicio, btnDiaTreino;
    private ExtendedFloatingActionButton btnMarcar;
    private String tempoDemora;
    private Float preco;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerar_preco);
        getSupportActionBar().setTitle("Marcaçao");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = FirebaseAuth.getInstance().getCurrentUser();
        spHoras = findViewById(R.id.sp_horas);
        tvPreco = findViewById(R.id.tv_preco);
        btnMarcar = findViewById(R.id.btn_marcar);
        tvHoraInicio = findViewById(R.id.tv_hora_inicio);
        btnDiaTreino  =findViewById(R.id.btn_dia_treino);
        tvDiaTreino = findViewById(R.id.tv_dia_treino);
        btnTempoInicio = findViewById(R.id.btn_tempo_inicio);
        uuidPersonal = getIntent().getStringExtra("uuid");
        preco = null;

        ArrayAdapter<CharSequence> horaAdapter = ArrayAdapter.createFromResource(this,
                R.array.horas_array, android.R.layout.simple_spinner_item);

        spHoras.setAdapter(horaAdapter);
        spHoras.setOnItemSelectedListener(this);

        FirebaseFirestore.getInstance().collection("pessoas").document(uuidPersonal).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        personalTrainer = document.toObject(PersonalTrainer.class);
                        if(personalTrainer!=null){
                            preco =personalTrainer.getPreco();
                            tvPreco.setText(""+(preco*0.5));
                            btnMarcar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    gerarDetalhesMarcacao();
                                }
                            });
                        }
                    }else{
                        Log.d("FirestoreFirebase", "No such document");
                    }
                }
            }
        });

        btnMarcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    gerarDetalhesMarcacao();
            }
        });

        btnTempoInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        btnDiaTreino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

    }

    public void gerarDetalhesMarcacao(){
        String diaTreino = tvDiaTreino.getText().toString();
        String horaTreino = tvHoraInicio.getText().toString();
        Float preco = Float.parseFloat(tvPreco.getText().toString());

        if(horaTreino.isEmpty()){
            Toast.makeText(this,"selecione a hora de inicio do treino",Toast.LENGTH_LONG).show();
            return;
        }

        if(diaTreino.isEmpty()){
            Toast.makeText(this,"selecione o dia em que deseja treinar",Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(ActivityFazerMarcacao.this,ActivityDetalhesMarcacao.class);
        intent.putExtra("preco",preco);
        intent.putExtra("tempoInicio",horaTreino);
        intent.putExtra("diaTreino",diaTreino);
        intent.putExtra("TempoDemora",tempoDemora);
        intent.putExtra("estado","temporario");
        intent.putExtra("tipoConta","usuario");
        intent.putExtra("uuidPersonal",uuidPersonal);
        finish();
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        tempoDemora = item;
        if(preco !=null){
            switch (item){
                case "00:30":
                    tvPreco.setText(""+(preco*0.5));
                    break;
                case "01:00":
                    tvPreco.setText(""+preco);
                    break;
                case "01:30":
                    tvPreco.setText(""+preco*1.5);
                    break;
                case "02:00":
                    tvPreco.setText(""+preco*2);
                    break;
                case "02:30":
                    tvPreco.setText(""+preco*2.5);
                    break;
                case "03:00":
                    tvPreco.setText(""+preco*3);
                    break;
                default:
                    tvPreco.setText(""+(preco*0.5));
            }
        }

        // Showing selected spinner item
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent ;
        switch (item.getItemId()) {
            case R.id.bar_opcao_conta:
                intent = new Intent(this, ActivityDefinicoesConta.class);
                startActivity(intent);
                return true;

            case android.R.id.home:
                intent = new Intent(this, ActivityPersonalList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // criar uma nova instancia do DatePickerDialog e retornar
            return new TimePickerDialog(getActivity(), this, hour, minute, true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            tvHoraInicio.setText(""+hourOfDay+":"+minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // criar uma nova instancia do DatePickerDialog e retornar
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            tvDiaTreino.setText(""+day+"/"+(month+1)+"/"+year);
        }
    }
}

