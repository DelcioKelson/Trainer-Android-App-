package pmd.di.ubi.pt.projectofinal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Objects;

public class FragmentGerarMarcacao extends Fragment implements DialogFragmentMarcacao.FlagMarcacaoConfirmadaDialogListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM2 = "nome";
    private static final String ARG_PARAM3 = "preco";
    private static final String ARG_PARAM1 = "uidPersonal";

    private TextView tvPreco;
    private static TextView tvHoraInicio, tvDiaTreino;
    private String uidPersonal;
    private Button btnMarcar,btnFechar;
    private String tempoDemora;
    private float preco;
    private String nomePersonal;
    // TODO: Rename and change types of parameters

    public FragmentGerarMarcacao() {
        // Required empty public constructor
    }

    public static FragmentGerarMarcacao newInstance(Bundle bundle) {

        FragmentGerarMarcacao fragmentGerarMarcacao = new FragmentGerarMarcacao();
        fragmentGerarMarcacao.setArguments(bundle);
        return fragmentGerarMarcacao;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uidPersonal = getArguments().getString(ARG_PARAM1);
            preco = Float.parseFloat(Objects.requireNonNull(getArguments().getString(ARG_PARAM3)));
            nomePersonal = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gerar_preco, container, false);
        Spinner spHoras = view.findViewById(R.id.sp_horas);
        tvPreco = view.findViewById(R.id.tv_preco);
        btnMarcar = view.findViewById(R.id.btn_marcar);
        btnFechar = view.findViewById(R.id.btn_fechar);
        tvHoraInicio = view.findViewById(R.id.tv_hora_inicio);
        Button btnDiaTreino = view.findViewById(R.id.btn_dia_treino);
        tvDiaTreino = view.findViewById(R.id.tv_dia_treino);
        Button btnTempoInicio = view.findViewById(R.id.btn_tempo_inicio);


        tvPreco.setText(""+(preco*0.5));
        btnMarcar.setOnClickListener(v -> gerarDetalhesMarcacao());


        ArrayAdapter<CharSequence> horaAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.horas_array, android.R.layout.simple_spinner_item);

        btnFechar.setOnClickListener(v -> getFragmentManager().popBackStack());

        spHoras.setAdapter(horaAdapter);
        spHoras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();

                tempoDemora = item;
                if(preco !=0){
                    switch (item){
                        case "00:30":
                            tvPreco.setText(""+(preco*0.5)+"€");
                            break;
                        case "01:00":
                            tvPreco.setText(""+preco+"€");
                            break;
                        case "01:30":
                            tvPreco.setText(""+preco*1.5+"€");
                            break;
                        case "02:00":
                            tvPreco.setText(""+preco*2+"€");
                            break;
                        case "02:30":
                            tvPreco.setText(""+preco*2.5+"€");
                            break;
                        case "03:00":
                            tvPreco.setText(""+preco*3+"€");
                            break;
                        default:
                            tvPreco.setText(""+(preco*0.5+"€"));
                    }
                }
                // Showing selected spinner item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnMarcar.setOnClickListener(v -> gerarDetalhesMarcacao());

        btnTempoInicio.setOnClickListener(v -> {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        });

        btnDiaTreino.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        return view;
    }

    public void gerarDetalhesMarcacao(){
        String diaTreino = tvDiaTreino.getText().toString();
        String horaTreino = tvHoraInicio.getText().toString();
        String preco = tvPreco.getText().toString();

        if(horaTreino.isEmpty()){
            Toast.makeText(getActivity(),"selecione a hora de inicio do treino",Toast.LENGTH_LONG).show();
            return;
        }

        if(diaTreino.isEmpty()){
            Toast.makeText(getActivity(),"selecione o dia em que deseja treinar",Toast.LENGTH_LONG).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("preco", preco);
        bundle.putString("horaTreino",horaTreino);
        bundle.putString("diaTreino",diaTreino);
        bundle.putString("tempoDemora",tempoDemora);
        bundle.putString("tipoConta","usuario");
        bundle.putString("uidPersonal",uidPersonal);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment newFragment = DialogFragmentMarcacao.newInstance(bundle);
        newFragment.setTargetFragment(this, 300);
        newFragment.show(ft, "dialog");
    }


    @Override
    public void onFinishEditDialog(int flag) {
        if (flag==1){
            btnMarcar.setText("marcar mais uma");
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
            if(minute<10){
                tvHoraInicio.setText(+hourOfDay+":0"+minute);
            }else {
                tvHoraInicio.setText(hourOfDay+":"+minute);

            }
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


