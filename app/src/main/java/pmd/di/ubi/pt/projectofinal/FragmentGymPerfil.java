package pmd.di.ubi.pt.projectofinal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class FragmentGymPerfil extends Fragment {

    private TextView tvPreco, tvNome,tvEndereço;
    private Button btnMarcar;
    private ImageView image;
    private String tempoDemora,endereço;
    private float preco;
    private List<String> horasList;
    private TextInputEditText etDia, etHora;
    private String nomeGym;

    CollectionReference marcacoesRef = FirebaseFirestore.getInstance().collection("marcacoes");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public FragmentGymPerfil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gym_perfil, container, false);
        tvPreco = view.findViewById(R.id.tv_preco);
        btnMarcar = view.findViewById(R.id.btn_marcar);
        tvEndereço = view.findViewById(R.id.tv_endereço);
        tvNome = view.findViewById(R.id.tv_nome_perfil);
        image = view.findViewById(R.id.iv_perfil);

        ChipGroup chipGroup = view.findViewById(R.id.chip_group_horas_treino);


        etDia = view.findViewById(R.id.et_dia);
        etHora = view.findViewById(R.id.et_hora);

        horasList = Arrays.asList("00:30", "01:00", "01:30", "02:00", "02:30");

        for (String h : horasList) {
            Chip chip = new Chip(getContext());
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Filter);
            chip.setChipDrawable(chipDrawable);
            chip.setText(h);
            chipGroup.addView(chip);
        }

        nomeGym = getArguments().getString("gymNome");
        tvNome.setText(nomeGym);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/" + nomeGym);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            if (bytes.length != 0) {
                try {
                    Glide.with(getActivity()).load(bytes).into(image);

                } catch (Exception ignored) {
                }
            }
        });

        FirebaseFirestore.getInstance().collection("gyms").document(nomeGym).get().addOnCompleteListener(task -> {

            if(task.isSuccessful() && task.getResult()!=null){
                preco = Float.parseFloat(Objects.requireNonNull((String) task.getResult().get("preco")));
                endereço = task.getResult().getString("endereço");


                tvEndereço.setText("endereço: "+endereço);
                Log.i("endereço",endereço);
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
                    btnMarcar.setOnClickListener(v -> gerarDetalhesMarcacao());


                });

            }

        });



        tvPreco.setText("0.0");


        etHora.setOnClickListener(v -> {
            DialogFragment newFragment = new TimePickerFragment(etHora);
            newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        });


        etDia.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment(etDia);
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
                    marcacaoData.put("nomeGym", nomeGym);
                    marcacaoData.put("preco", preco.replace("€", ""));
                    marcacaoData.put("tempoDuracao", tempoDemora);
                    marcacaoData.put("uidUsuario", user.getUid());
                    marcacaoData.put("horaMarcacao", "" + hora + ":" + minutos);
                    marcacaoData.put("marcacaoId", marcacaoId);
                    marcacoesRef.document(marcacaoId).set(marcacaoData);

                    Map<String, Object> notificacaoData;
                    notificacaoData = new HashMap<>();
                    notificacaoData.put("titulo", "Nova marcação");
                    notificacaoData.put("mensagem", user.getDisplayName() + " criou uma marcação");
                    notificacaoData.put("data", System.currentTimeMillis());
                    notificacaoData.put("vista", false);

                    notificacaoData.put("marcacaoId", marcacaoId);
                    DocumentReference notificacaoRef = FirebaseFirestore.getInstance().collection("gyms")
                            .document(nomeGym).collection("notificacoes").document();

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

        public DatePickerFragment(TextInputEditText etDia) {
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
            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {

            String dia = "" + day + "/" + (month + 1) + "/" + year;

                int atualAno = Calendar.getInstance().get(Calendar.YEAR);
                int atualdia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int atualMes = Calendar.getInstance().get(Calendar.MONTH);

                if( atualAno > year ) {
                    Toast.makeText(getActivity(), "Ano inválida", Toast.LENGTH_LONG).show();
                    dia = "";
                }

                if( atualAno == year && atualMes >month){
                    Toast.makeText(getActivity(), "mês inválido", Toast.LENGTH_LONG).show();
                    dia = "";
                }

                if( atualAno == year && atualMes ==month && atualdia>day){
                    Toast.makeText(getActivity(), "dia inválido", Toast.LENGTH_LONG).show();
                    dia = "";
                };

                etDia.setText(dia);


        }
    }

}