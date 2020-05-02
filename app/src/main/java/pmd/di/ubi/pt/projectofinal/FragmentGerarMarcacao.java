package pmd.di.ubi.pt.projectofinal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FragmentGerarMarcacao extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM3 = "preco";
    private static final String ARG_PARAM1 = "uidPersonal";

    private TextView tvPreco;
    private String uidPersonal;
    private Button btnMarcar, btnComentario;
    private String tempoDemora;
    private float preco;
    private String nomePersonal;
    private List<String> horasList;
    private String rating;
    private TextInputEditText etDia, etHora;
    private String diasIndisponiveis;

    private int posPersonal;
    private Map<String, Object> personal;
    CollectionReference marcacoesRef = FirebaseFirestore.getInstance().collection("marcacoes");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


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
            posPersonal = getArguments().getInt("posPersonal");

            if(getArguments().getBoolean("favorito")){
                personal = Main.sharedDataModel.getPersonalListFavorito().getValue().get(posPersonal);

            }else {
                personal = Main.sharedDataModel.getPersonalList().getValue().get(posPersonal);
                Log.i("PERSONAL", personal.toString());
            }



            uidPersonal = (String) personal.get("uid");
            preco = Float.parseFloat(Objects.requireNonNull((String) personal.get(ARG_PARAM3)));
            rating = (String) personal.get("rating");
            diasIndisponiveis = (String) personal.get("diasIndisponiveis");
            nomePersonal = (String) personal.get("nome");


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

        if (rating != null && rating.equals("0")) {
            btnComentario.setVisibility(View.GONE);
        }

        horasList = Arrays.asList("00:30", "01:00", "01:30", "02:00", "02:30");


        for (String h : horasList) {
            Chip chip = new Chip(getContext());
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Filter);
            chip.setChipDrawable(chipDrawable);
            chip.setText(h);
            chipGroup.addView(chip);
        }

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = view.findViewById(checkedId);
            if (chip != null) {
                tempoDemora = chip.getText().toString();
                switch (tempoDemora) {
                    case "00:30":
                        tvPreco.setText("" + (preco * 0.5) + "€");
                        break;
                    case "01:00":
                        tvPreco.setText("" + preco + "€");
                        break;
                    case "01:30":
                        tvPreco.setText("" + preco * 1.5 + "€");
                        break;
                    case "02:00":
                        tvPreco.setText("" + preco * 2 + "€");
                        break;
                    case "02:30":
                        tvPreco.setText("" + preco * 2.5 + "€");
                        break;
                    default:
                        tvPreco.setText("" + (preco * 0.5 + "€"));
                }
            }

        });


        tvPreco.setText("0.0");


        btnMarcar.setOnClickListener(v -> gerarDetalhesMarcacao());


        Bundle bundle = new Bundle();
        bundle.putString("uidPersonal", uidPersonal);

        btnComentario.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_fragmentGerarMarcacao_to_fragmentComentariosGerarPreco, bundle));


        btnMarcar.setOnClickListener(v -> gerarDetalhesMarcacao());


        etHora.setOnClickListener(v -> {
            DialogFragment newFragment = new TimePickerFragment(etHora);
            newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        });


        etDia.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etDia, diasIndisponiveis, nomePersonal);
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        Log.i("PERSONAL", "imagebaytes");

        return view;
    }

    public void gerarDetalhesMarcacao() {
        final String diaTreino = etDia.getText().toString();
        final String horaTreino = etHora.getText().toString();
        final String preco = tvPreco.getText().toString();

        if (horaTreino.isEmpty()) {
            Toast.makeText(getActivity(), "selecione a hora de inicio do treino", Toast.LENGTH_LONG).show();
            return;
        }

        if (diaTreino.isEmpty()) {
            Toast.makeText(getActivity(), "selecione o dia em que deseja treinar", Toast.LENGTH_LONG).show();
            return;
        }

        if (tempoDemora == null) {
            Toast.makeText(getActivity(), "selecione o tempo de treino", Toast.LENGTH_LONG).show();
            return;


        }

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hora = c.get(Calendar.HOUR_OF_DAY);
        int minutos = c.get(Calendar.MINUTE);
        final String marcacaoId = marcacoesRef.document().getId();

        String message = "preço a pagar: " + preco
                + "\nhora de inicio: " + horaTreino
                + "\ndia do treino: " + diaTreino
                + "\ntempo do treino:" + tempoDemora;


        new android.app.AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("Sim", (dialog, which) -> {
                    Map<String, Object> marcacaoData;
                    marcacaoData = new HashMap<>();
                    marcacaoData.put("diaMarcacao", "" + day + "/" + month + "/" + year);
                    marcacaoData.put("diaTreino", diaTreino);
                    marcacaoData.put("estado", "pendente");
                    marcacaoData.put("horaTreino", horaTreino);
                    marcacaoData.put("uidPersonal", uidPersonal);
                    marcacaoData.put("preco", preco.replace("€", ""));
                    marcacaoData.put("tempoDuracao", tempoDemora);
                    marcacaoData.put("uidUsuario", user.getUid());
                    marcacaoData.put("horaMarcacao", "" + hora + ":" + minutos);
                    marcacaoData.put("marcacaoId", marcacaoId);
                    marcacoesRef.document(marcacaoId).set(marcacaoData);

                    Map<String, Object> notificacaoData;
                    notificacaoData = new HashMap<>();
                    notificacaoData.put("titulo", "Nova marcação");
                    notificacaoData.put("mensagem", user.getDisplayName() + " criou uma marcação consigo");
                    notificacaoData.put("data", System.currentTimeMillis());
                    notificacaoData.put("vista", false);

                    notificacaoData.put("marcacaoId", marcacaoId);
                    DocumentReference notificacaoRef = FirebaseFirestore.getInstance().collection("pessoas")
                            .document(uidPersonal).collection("notificacoes").document();

                    notificacaoData.put("id", notificacaoRef.getId());
                    notificacaoRef.set(notificacaoData);

                    Toast.makeText(getActivity(), "Marcaçao realizada com sucesso", Toast.LENGTH_LONG).show();
                    btnMarcar.setText("marcar mais uma");


                }).setNegativeButton("Nao", null)
                .create().show();


    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private TextInputEditText etHora;

        public TimePickerFragment(TextInputEditText etHora) {
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
            if (minute < 10) {
                etHora.setText(+hourOfDay + ":0" + minute);
            } else {
                etHora.setText(hourOfDay + ":" + minute);

            }
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextInputEditText etDia;
        private String diasIndisponiveis;
        private String nomePersonal;

        public DatePickerFragment(TextInputEditText etDia, String diasIndisponiveis, String nomePersonal) {
            this.etDia = etDia;
            this.diasIndisponiveis = diasIndisponiveis;
            this.nomePersonal = nomePersonal;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // criar uma nova instancia do DatePickerDialog e retornar
            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {

            String dia = "" + day + "/" + (month + 1) + "/" + year;

            if (diasIndisponiveis != null && diasIndisponiveis.contains(dia)) {
                etDia.setText("");

                new android.app.AlertDialog.Builder(getActivity())
                        .setMessage(nomePersonal + " encontra-se indisponível neste dia!")
                        .setPositiveButton("OK", (dialog, which) -> {

                        }).create().show();

            } else {
                etDia.setText(dia);

            }
        }
    }


}


