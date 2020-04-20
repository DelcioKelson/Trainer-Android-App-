package pmd.di.ubi.pt.projectofinal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class FragmentGerarMarcacao extends Fragment implements DialogConfirmarFragmentMarcacao.FlagMarcacaoConfirmadaDialogListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM2 = "nome";
    private static final String ARG_PARAM3 = "preco";
    private static final String ARG_PARAM1 = "uidPersonal";

    private TextView tvPreco;
    private String uidPersonal;
    private Button btnMarcar,btnComentario;
    private String tempoDemora;
    private float preco;
    private String nomePersonal;
    private List<String> horasList;
    private String rating;
    private TextInputEditText etDia,etHora;

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
            rating = getArguments().getString("rating");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gerar_preco, container, false);
        tvPreco = view.findViewById(R.id.tv_preco);
        btnMarcar = view.findViewById(R.id.btn_marcar);
        btnComentario = view.findViewById(R.id.btn_comentarios);
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_horas_treino);

        etDia = view.findViewById(R.id.et_dia);
        etHora = view.findViewById(R.id.et_hora);

        if(rating!=null && rating.equals("0")){
            btnComentario.setVisibility(View.GONE);
        }

        horasList = Arrays.asList(new String[]{"00:30","01:00","01:30","02:00","02:30"});


        for (String h: horasList){
            Chip chip = new Chip(getContext());
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Filter);
            chip.setChipDrawable(chipDrawable);
            chip.setText(h);
            chipGroup.addView(chip); }

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = view.findViewById(checkedId);
            if(chip!=null){
                tempoDemora = chip.getText().toString();
                switch (tempoDemora){
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
                    default:
                        tvPreco.setText(""+(preco*0.5+"€"));
                }
            }

        });



        tvPreco.setText("0.0");


        btnMarcar.setOnClickListener(v -> gerarDetalhesMarcacao());


        btnComentario.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_fragmentGerarMarcacao_to_fragmentComentariosGerarPreco,getArguments()));


        btnMarcar.setOnClickListener(v -> gerarDetalhesMarcacao());

        etHora.setOnClickListener(v -> {
            DialogFragment newFragment = new TimePickerFragment(etHora);
            newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        });



        etDia.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etDia);
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        return view;
    }

    public void gerarDetalhesMarcacao(){
        String diaTreino = etDia.getText().toString();
        String horaTreino = etHora.getText().toString();
        String preco = tvPreco.getText().toString();

        if(horaTreino.isEmpty()){
            Toast.makeText(getActivity(),"selecione a hora de inicio do treino",Toast.LENGTH_LONG).show();
            return;
        }

        if(diaTreino.isEmpty()){
            Toast.makeText(getActivity(),"selecione o dia em que deseja treinar",Toast.LENGTH_LONG).show();
            return;
        }

        if(tempoDemora==null){
            Toast.makeText(getActivity(),"selecione o tempo de treino",Toast.LENGTH_LONG).show();
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

        DialogFragment newFragment = DialogConfirmarFragmentMarcacao.newInstance(bundle);
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

        private  TextInputEditText etHora;

        public TimePickerFragment(TextInputEditText etHora){
            this.etHora = etHora;
        }


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
                etHora.setText(+hourOfDay+":0"+minute);
            }else {
                etHora.setText(hourOfDay+":"+minute);

            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextInputEditText etDia;
        public DatePickerFragment(TextInputEditText etDia){
            this.etDia = etDia;
        }
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
            etDia.setText(""+day+"/"+(month+1)+"/"+year);
        }
    }
}


