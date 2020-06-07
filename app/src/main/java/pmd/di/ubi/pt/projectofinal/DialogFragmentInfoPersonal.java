package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DialogFragmentInfoPersonal extends DialogFragment {

    private String diasFerias;
    private String disponivel;
    private ChipGroup chipGroup;

    public DialogFragmentInfoPersonal() {
        // Required empty public constructor
    }


    public static DialogFragmentInfoPersonal newInstance(String diasFerias, String disponivel) {
        DialogFragmentInfoPersonal fragment = new DialogFragmentInfoPersonal();
        Bundle args = new Bundle();
        args.putString("diasFerias", diasFerias);
        args.putString("disponivel", disponivel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            diasFerias = getArguments().getString("diasFerias");
            disponivel = getArguments().getString("disponivel");
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreanDialogFragmentheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.dialogfragment_info_personal, container, false);
        chipGroup = view.findViewById(R.id.chip_group_dias_info);
        final TextView tvDisponivel = view.findViewById(R.id.tv_disponivel);

        ImageButton btnClose = view.findViewById(R.id.ib_close);

        btnClose.setOnClickListener(v -> dismiss());

        tvDisponivel.setText("Disponivel: " + disponivel);
        if (!(diasFerias == null || diasFerias.isEmpty())) {
            List<String> dias = new LinkedList<String>(Arrays.asList(diasFerias.split("-")));
            chipGroup.removeAllViews();
            for (String dia : dias) {
                if (!dia.isEmpty()){
                    Chip chip = new Chip(requireContext());
                    chip.setText(dia);
                    chipGroup.addView(chip);
                }
            }
        }

        return view;
    }

}