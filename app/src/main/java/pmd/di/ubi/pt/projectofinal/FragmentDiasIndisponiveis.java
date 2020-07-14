package pmd.di.ubi.pt.projectofinal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class FragmentDiasIndisponiveis extends Fragment {

    private static String diasFerias;
    private static ChipGroup chipGroup;
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static DocumentReference userDocumentReference = FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid());
    private String disponivel;

    public FragmentDiasIndisponiveis() {
        // Required empty public constructor
    }

    public static FragmentDiasIndisponiveis newInstance() {
        return new FragmentDiasIndisponiveis();
    }

    private static void atualizarChips(Context context) {
        if (!(diasFerias == null || diasFerias.isEmpty())) {
            List<String> dias = new LinkedList<String>(Arrays.asList(diasFerias.split("-")));
            chipGroup.removeAllViews();
            for (String dia : dias) {
                if (!dia.isEmpty()) {
                    Chip chip = new Chip(context);
                    chip.setText(dia);
                    chip.setCloseIconResource(R.drawable.ic_close_black_24dp);
                    chip.setCloseIconVisible(true);
                    chip.setOnCloseIconClickListener(v -> {
                        dias.remove(dia);
                        userDocumentReference.update("diasIndisponiveis", diasFerias.replace(dia, ""));
                        chipGroup.removeView(chip);
                    });
                    chipGroup.addView(chip);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dias_indisponiveis, container, false);

        ExtendedFloatingActionButton btnAddDia = view.findViewById(R.id.btn_add_dia);
        chipGroup = view.findViewById(R.id.chip_group_dias);
        final Switch swdisponivel = view.findViewById(R.id.switch_disponivel);

        userDocumentReference.get().addOnSuccessListener(documentSnapshot -> {
            diasFerias = documentSnapshot.getString("diasIndisponiveis");
            if (diasFerias == null) {
                diasFerias = "";
            }
            disponivel = documentSnapshot.getString("disponivel");
            if (disponivel.equals("sim")) {
                swdisponivel.setChecked(true);
            } else {
                swdisponivel.setChecked(false);
            }
            atualizarChips(getContext());
        });

        swdisponivel.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                userDocumentReference.update("disponivel", "sim");
            } else {
                userDocumentReference.update("disponivel", "não");
            }
        });

        btnAddDia.setOnClickListener(v -> {
            try {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            } catch (Exception ignored) {

            }
        });
        return view;
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
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (diasFerias.isEmpty()) {
                diasFerias = diasFerias.concat(day + "/" + (month + 1) + "/" + year);
            } else {
                diasFerias = diasFerias.concat("-" + day + "/" + (month + 1) + "/" + year);
            }
            atualizarChips(getContext());
            userDocumentReference.update("diasIndisponiveis", diasFerias);


        }
    }
}