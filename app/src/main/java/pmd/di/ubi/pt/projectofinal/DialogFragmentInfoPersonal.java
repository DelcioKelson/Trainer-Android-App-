package pmd.di.ubi.pt.projectofinal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DialogFragmentInfoPersonal extends DialogFragment {

    private String diasFerias;
    private String disponivel;
    private String uidPersonal;

    public DialogFragmentInfoPersonal() {
        // Required empty public constructor
    }


    public static DialogFragmentInfoPersonal newInstance(String diasFerias, String disponivel, String uidPersonal) {
        DialogFragmentInfoPersonal fragment = new DialogFragmentInfoPersonal();
        Bundle args = new Bundle();
        args.putString("diasFerias", diasFerias);
        args.putString("disponivel", disponivel);
        args.putString("uidPersonal",uidPersonal);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            diasFerias = getArguments().getString("diasFerias");
            disponivel = getArguments().getString("disponivel");
            uidPersonal = getArguments().getString("uidPersonal");
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreanDialogFragmentheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.dialogfragment_info_personal, container, false);
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_dias_info);
        final TextView tvDisponivel = view.findViewById(R.id.tv_disponivel);

        ImageButton btnClose = view.findViewById(R.id.ib_close);

        btnClose.setOnClickListener(v -> dismiss());

        final Button btnCurriculo = view.findViewById(R.id.btn_curriculo);

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("documents/"+uidPersonal);

        btnCurriculo.setOnClickListener(v -> storageReference.child("curriculo")
                .getDownloadUrl().addOnCompleteListener(task -> {

                    try {
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(task.getResult(),"application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target, "Open File");

                        DialogFragmentInfoPersonal.this.startActivity(intent);
                        //startActivity(intent);
                    } catch (Exception e) {
                        Log.i("DialogFragment",e.getMessage());

                    }
                }));

                        tvDisponivel.setText("Disponivel: " + disponivel);
        if (!(diasFerias == null || diasFerias.isEmpty())) {
            List<String> dias = new LinkedList<String>(Arrays.asList(diasFerias.split("-")));
            chipGroup.removeAllViews();
            for (String dia : dias) {
                if (!dia.isEmpty()) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(dia);
                    chipGroup.addView(chip);
                }
            }
        }

        return view;
    }

}