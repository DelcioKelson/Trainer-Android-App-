package pmd.di.ubi.pt.projectofinal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegistroPersonal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegistroPersonal extends Fragment {


    private TextInputEditText emailText,passwordText,numeroTelefone ;
    private TextInputLayout emailLayout,passwordLayout,numeroLayout,nomeLayout;

    private TextInputEditText nomeText;
    private Button btnFoto,btnRegistrar;
    private Uri fSelecteduri;
    private ImageView imgVfoto;
    private ProgressBar progressBar;
    private boolean isFotoValida = true;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_registro_personal1, container, false);



        nomeText =  view.findViewById(R.id.edit_usarname_personal);
        emailText =  view.findViewById(R.id.edit_email_personal);
        passwordText =  view.findViewById(R.id.edit_password_personal);

        btnRegistrar = view.findViewById(R.id.btn_register_personal);
        btnFoto =  view.findViewById(R.id.btn_selected_foto_personal);
        imgVfoto =  view.findViewById(R.id.img_foto_personal);
        numeroTelefone = view.findViewById(R.id.edit_telefone_personal);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_registro_personal);



        emailLayout = view.findViewById(R.id.layout_email);
        nomeLayout = view.findViewById(R.id.layout_nome);
        passwordLayout = view.findViewById(R.id.layout_pass);
        numeroLayout = view.findViewById(R.id.layout_telefone);


        //botao para fazer o registro
        btnRegistrar.setOnClickListener(v -> registeruser());

        //botao para escolher foto de perfil
        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,0);
        });

        return view;
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

                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                            .getVisionFaceDetector();
                    isFotoValida = true;

                    detector.detectInImage(image)
                            .addOnSuccessListener(
                                    faces -> {
                                        if (faces.size() != 1) {
                                            Toast.makeText(getContext(), "Fotografia inválida, precisa de ter uma face", Toast.LENGTH_LONG).show();
                                            isFotoValida = false;
                                        }

                                    })
                            .addOnFailureListener(
                                    e -> Toast.makeText(getContext(), "não Tem face", Toast.LENGTH_LONG).show());
                } catch (IOException ignored) {

                }
            }
        }
    }

    //funçao chamada pelo botao registrar
    private void registeruser(){
        String nome = nomeText.getText().toString();
        String email = emailText.getText().toString();
        String senha = passwordText.getText().toString();
        String telefone = numeroTelefone.getText().toString();

        String errorM = "Deve ser preenchido";

        if (imgVfoto.getDrawable() == null) {
            Toast.makeText(getContext(), "Selecione uma imagem", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isFotoValida) {
            Toast.makeText(getContext(), "Selecione uma fotografia válida, precisa de ter uma face", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (nome.isEmpty()) {
            nomeLayout.setError(errorM);
            return;
        }

        nomeLayout.setError(null);

        if (email.isEmpty()) {
            emailLayout.setError(errorM);
            return;
        }
        emailLayout.setError(null);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Email inválido");

            return;
        }
        emailText.setError(null);

        if (senha.isEmpty()) {
            passwordLayout.setError(errorM);

            return;
        }

        passwordLayout.setError(null);


        if (!isPasswordValid(passwordText.getText())) {
            passwordText.setError("palavra passe deve ter 6 digitos ou mais");
            return;
        }

        passwordText.setError(null);


        if (telefone.isEmpty()) {
            numeroLayout.setError(errorM);
            return;
        }

        numeroLayout.setError(null);

        if (!Patterns.PHONE.matcher(telefone).matches() || telefone.length() != 9) {
            numeroTelefone.setError("número invalido");
            return;
        }
        numeroTelefone.setError(null);


        progressBar.setVisibility(View.VISIBLE);
        btnRegistrar.setClickable(false);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.i("Tester",task.getResult().getUser().getUid());
                        //salva as informaçoes no firebase
                        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                        builder.setDisplayName(nome);
                        final UserProfileChangeRequest changeRequest = builder.build();
                        task.getResult().getUser().updateProfile(changeRequest);
                        salvarUsuarioNoFireBase();
                    }
                    else {
                        Toast.makeText(getContext(), "Ja existe uma conta com este E-mail", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text.length() >= 6;
    }

    //funçao para salvar dados no firebase
    private void salvarUsuarioNoFireBase() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try {
            final String uid = user.getUid();
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fSelecteduri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
            byte[] data = baos.toByteArray();

            final StorageReference ref = FirebaseStorage.getInstance().getReference("image/"+uid);
            ref.putBytes(data).addOnSuccessListener(taskSnapshot -> {

                Map<String, Object> usuarioData;
                usuarioData = new HashMap<>();
                usuarioData.put("nome", nomeText.getText().toString());;
                usuarioData.put("numeroTelefone", numeroTelefone.getText().toString());
                usuarioData.put("tipoConta","pendente");
                usuarioData.put("submeteu",false);
                usuarioData.put("aprovado","nao");
                usuarioData.put("uid",uid);
                // adicionar dados do usario na base de dados
                FirebaseFirestore.getInstance().collection("pessoas")
                        .document(uid)
                        .set(usuarioData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(getActivity(), Main.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            });
        }catch (Exception e){

        }
    }


}
