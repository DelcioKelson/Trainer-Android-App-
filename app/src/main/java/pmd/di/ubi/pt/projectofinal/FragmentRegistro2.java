package pmd.di.ubi.pt.projectofinal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FragmentRegistro2 extends Fragment {

    private String situacoes, morada, codigoPostal;

    private EditText emailText,passwordText,alturaText,pesoText,numeroTelefone ;
    private TextInputEditText nomeText;
    private static Button anivBtn;
    private Button btnFoto,btnRegistrar;
    private Uri fSelecteduri;
    private ImageView imgVfoto;
    private ProgressBar progressBar;


    public FragmentRegistro2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            situacoes = getArguments().getString("situacoes");
            morada = getArguments().getString("morada");
            codigoPostal = getArguments().getString("codigo_postal");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro2, container, false);
        nomeText =  view.findViewById(R.id.edit_usarname);
        emailText =  view.findViewById(R.id.edit_email);
        passwordText =  view.findViewById(R.id.edit_password);
        alturaText = view.findViewById(R.id.edit_altura);
        pesoText = view.findViewById(R.id.edit_peso);
        btnRegistrar = view.findViewById(R.id.btn_register);
        btnFoto =  view.findViewById(R.id.btn_selected_foto);
        imgVfoto =  view.findViewById(R.id.img_foto);
        numeroTelefone = view.findViewById(R.id.edit_telefone);
        anivBtn = view.findViewById(R.id.edit_data);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_registro);

        //botao para fazer o registro
        btnRegistrar.setOnClickListener(v -> registeruser());

        //botao para escolher foto de perfil
        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,0);
        });

        //botao para escolher data de aniversario
        anivBtn.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
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
            anivBtn.setText(""+day+"/"+(month+1)+"/"+year);
        }
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

    //funçao chamada pelo botao registrar
    private void registeruser(){
        String nome = nomeText.getText().toString();
        String email = emailText.getText().toString();
        String senha = passwordText.getText().toString();
        String pesoAux = pesoText.getText().toString();
        String alturaAux = pesoText.getText().toString();

        if(imgVfoto.getDrawable()==null){
            Toast.makeText(getContext(),"Selecione uma imagem", Toast.LENGTH_LONG).show();
            return;
        }

        else if(nome.isEmpty()){
            Toast.makeText(getContext(),"Nome deve ser preenchido", Toast.LENGTH_LONG).show();
            return;
        }
        else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Insira um email válido");
            return;
        }
        else if(senha.isEmpty() || senha.length() < 6 ){
            Toast.makeText(getContext(),"Preencha com mais de 5 caracteres", Toast.LENGTH_LONG).show();
            return;
        }
        else if(anivBtn.getText().toString().isEmpty() ){
            Toast.makeText(getContext(),"Selecione uma data", Toast.LENGTH_LONG).show();
            return;
        }

        else if(alturaAux.isEmpty()){
            Toast.makeText(getContext(),"Insira uma altura", Toast.LENGTH_LONG).show();
            return;
        }

        else if(pesoAux.isEmpty()){
            Toast.makeText(getContext(),"insira um peso", Toast.LENGTH_LONG).show();
            return;
        }

        double altura = Double.parseDouble(alturaAux);
        if(!isNumericInt(pesoAux)){
            Toast.makeText(getContext(),"o peso tem que ser inteiro", Toast.LENGTH_LONG).show();
            return;
        }
        int peso = Integer.parseInt(pesoAux);

        if(peso<20 || peso>120){
            Toast.makeText(getContext(),"Selecione um peso valido", Toast.LENGTH_LONG).show();
            return;
        }
        else if(altura>1.5 && altura<2.2){
            Toast.makeText(getContext(),"Selecione uma altura valida", Toast.LENGTH_LONG).show();
            return;
        }
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
                    }
                });
    }
    public static boolean isNumericInt(String str) {
        try
        {
            Integer.parseInt(str);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
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
                usuarioData.put("peso", pesoText.getText().toString());
                usuarioData.put("dataNascimento", anivBtn.getText().toString());
                usuarioData.put("altura", alturaText.getText().toString());
                usuarioData.put("numeroTelefone", numeroTelefone.getText().toString());
                usuarioData.put("tipoConta","usuario");
                usuarioData.put("Uid",uid);
                usuarioData.put("situacoes",situacoes);
                usuarioData.put("codigo_postal",codigoPostal);
                usuarioData.put("morada",morada);

                // adicionar dados do usario na base de dados
                FirebaseFirestore.getInstance().collection("pessoas")
                        .document(uid)
                        .set(usuarioData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(getActivity(), ActivityMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            });
        }catch (Exception e){

        }
    }


}
