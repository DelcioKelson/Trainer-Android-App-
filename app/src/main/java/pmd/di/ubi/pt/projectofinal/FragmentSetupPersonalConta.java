package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSetupPersonalConta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSetupPersonalConta extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentSetupPersonalConta() {
        // Required empty public constructor
    }

     private ChipGroup chipGroup;
    private Button btnSetup;
    private Map<String,Object> personal;
    private  Map<String,Object> modalidades;
    private TextInputEditText textInputEditText;
    private DocumentReference personalRef;
    private float preco;
    private Uri fSelecteduri;
    private ImageView imgVfoto;
    private Button btnFoto;



    // TODO: Rename and change types and number of parameters
    public static FragmentSetupPersonalConta newInstance(String param1, String param2) {
        FragmentSetupPersonalConta fragment = new FragmentSetupPersonalConta();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_setup_personal_conta, container, false);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
         personalRef = FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid());

        personal = new HashMap<>();
        modalidades = new HashMap<>();



        personalRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful() && task.getResult()!=null){
                    personal = task.getResult().getData();
                }
            }
        });

        imgVfoto = view.findViewById(R.id.img_foto_setup);
        chipGroup = view.findViewById(R.id.chip_group_modalidade);
        btnSetup = view.findViewById(R.id.btn_setup);
        textInputEditText  = view.findViewById(R.id.edit_preco);
        btnFoto = view.findViewById(R.id.btn_selected_foto_setup);




        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,0);
        });

        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Integer> chipids =  chipGroup.getCheckedChipIds();

                if(chipids.size()!=0){
                        for ( int chipsId :chipids ){
                            Chip chip = view.findViewById(chipsId);
                            if(chip!=null){
                                modalidades.put(chip.getText().toString(),true);
                            }
                        }

                        if(fSelecteduri!=null){

                            if (textInputEditText.getText()!=null){
                                try {
                                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fSelecteduri);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                                    byte[] data = baos.toByteArray();
                                    FirebaseStorage.getInstance().getReference("image/" + user.getUid()).putBytes(data)
                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { personal.put("preco",""+textInputEditText.getText().toString());
                                                    personal.put("modalidades",modalidades);
                                                    personal.put("rating",""+0);
                                                    personal.put("disponivel","sim");
                                                    personal.put("tipoConta","personal");
                                                    personalRef.set(personal);

                                                    Intent intent = new Intent(getActivity(), ActivityMain.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            });
                                }catch (Exception e){

                                }
                            }
                     }else {
                            Toast.makeText(getActivity(),"Selecione uma fotografia",Toast.LENGTH_LONG).show();
                        }
                }else {
                    Toast.makeText(getActivity(),"Selecione uma modalidade",Toast.LENGTH_LONG).show();
                }
            }
        });

        inicilizarChips();
        return view;
    }


    private  void inicilizarChips(){
        FirebaseFirestore.getInstance().collection("modalidades").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful() && task.getResult()!=null){

                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String modalidade = (String) documentSnapshot.get("nome");
                        if (!modalidade.isEmpty()){
                            Chip chip = new Chip(getContext());
                            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(), null, 0, R.style.Widget_MaterialComponents_Chip_Filter);

                            chip.setChipDrawable(chipDrawable);

                            chip.setText(modalidade);

                            chipGroup.addView(chip);
                        }
                    }
                }

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0) {
            if (data != null) {
                fSelecteduri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fSelecteduri);
                    imgVfoto.setImageBitmap(bitmap);
                    imgVfoto.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
                    btnFoto.setAlpha(0);
                } catch (IOException ignored) {

                }
            }
        }
    }
}

