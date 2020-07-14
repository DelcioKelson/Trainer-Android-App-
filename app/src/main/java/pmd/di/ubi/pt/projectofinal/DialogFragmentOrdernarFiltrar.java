package pmd.di.ubi.pt.projectofinal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DialogFragmentOrdernarFiltrar extends DialogFragment {
    private static Button btnDiasDisponives;
    private RadioButton button;
    private int idOp;

    public DialogFragmentOrdernarFiltrar() {

    }

    public static DialogFragmentOrdernarFiltrar newInstance(int id, boolean disponiveis, boolean diaDisponiveis, String diaDisponibilidade) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putBoolean("disponivel", disponiveis);
        bundle.putBoolean("diaDisponiveis", diaDisponiveis);
        bundle.putString("diaDisponibilidade", diaDisponibilidade);
        DialogFragmentOrdernarFiltrar dialog = new DialogFragmentOrdernarFiltrar();
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_odernar_filtrar, container, false);
        RadioGroup opcoes = view.findViewById(R.id.radio_opcoes);

        Switch sw1 = view.findViewById(R.id.switch_disponives);
        Switch sw2 = view.findViewById(R.id.switch_disponives_dia);
        btnDiasDisponives = view.findViewById(R.id.btn_dia_disponivel);
        Button btnAplicar = view.findViewById(R.id.btn_aplicar_filtro);

        idOp = getArguments().getInt("id");
        boolean disponiveis = getArguments().getBoolean("disponivel");
        boolean diaDisponiveis = getArguments().getBoolean("diaDisponiveis");
        String diaDisponibilidade = getArguments().getString("diaDisponibilidade");


        sw1.setChecked(disponiveis);
        sw2.setChecked(diaDisponiveis);
        btnDiasDisponives.setText(diaDisponibilidade);
        if (idOp != 0) {
            button = (RadioButton) opcoes.findViewById(idOp);
            button.setChecked(true);
        } else {
            opcoes.clearCheck();
        }

        opcoes.setOnCheckedChangeListener((group, checkedId) -> {
            button = (RadioButton) group.findViewById(checkedId);
            DialogFragmentOrdernarMenuDialogListener listener = (DialogFragmentOrdernarMenuDialogListener) getTargetFragment();
            listener.onFinishOrdernarMenuDialog(checkedId);
            dismiss();
        });

        btnDiasDisponives.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnDiasDisponives.setVisibility(View.VISIBLE);
                } else {
                    btnDiasDisponives.setVisibility(View.GONE);
                }
            }
        });

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentFiltrarMenuDialogListener listener = (DialogFragmentFiltrarMenuDialogListener) getTargetFragment();
                listener.onFinishFiltrarMenuDialog(sw1.isChecked(), sw2.isChecked(), btnDiasDisponives.getText().toString());
                dismiss();
            }
        });
        return view;
    }

    public interface DialogFragmentOrdernarMenuDialogListener {
        void onFinishOrdernarMenuDialog(int selectedBtn);
    }

    public interface DialogFragmentFiltrarMenuDialogListener {
        void onFinishFiltrarMenuDialog(boolean disponiveis, boolean disponiveisDia, String diaMesAno);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @NonNull
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
            btnDiasDisponives.setText("" + day + "/" + (month + 1) + "/" + year);
        }
    }
}
