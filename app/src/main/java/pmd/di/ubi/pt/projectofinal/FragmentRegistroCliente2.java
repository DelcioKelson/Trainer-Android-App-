package pmd.di.ubi.pt.projectofinal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class FragmentRegistroCliente2 extends Fragment {

    private String situacoes, morada, codigoPostal;

    private TextInputEditText emailText, passwordText, alturaText, pesoText, numeroTelefone;
    private TextInputEditText nomeText;
    private TextInputEditText nascimentoText;
    private Button btnFoto, btnRegistrar;
    private Uri fSelecteduri;
    private ImageView imgVfoto;
    private ProgressBar progressBar;

    private TextInputLayout emailLayout,passwordLayout,alturaLayout,pesoLayout,numeroLayout,nomeLayout,nascimentoLayout;


    public FragmentRegistroCliente2() {
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
        View view = inflater.inflate(R.layout.fragment_registro_cliente2, container, false);
        nomeText = view.findViewById(R.id.edit_usarname);
        emailText = view.findViewById(R.id.edit_email);
        passwordText = view.findViewById(R.id.edit_password);
        alturaText = view.findViewById(R.id.edit_altura);
        pesoText = view.findViewById(R.id.edit_peso);
        btnRegistrar = view.findViewById(R.id.btn_register);
        btnFoto = view.findViewById(R.id.btn_selected_foto);
        imgVfoto = view.findViewById(R.id.img_foto);
        numeroTelefone = view.findViewById(R.id.edit_telefone);
        nascimentoText = view.findViewById(R.id.edit_data);
        progressBar = view.findViewById(R.id.progressbar_registro);


        view.findViewById(R.id.btn_voltar).setOnClickListener(v -> {
            Navigation.findNavController(v).popBackStack();

        });


        emailLayout = view.findViewById(R.id.layout_email);
        nomeLayout = view.findViewById(R.id.layout_nome);
        pesoLayout = view.findViewById(R.id.layout_peso);
        alturaLayout = view.findViewById(R.id.layout_altura);
        passwordLayout = view.findViewById(R.id.layout_pass);
        numeroLayout = view.findViewById(R.id.layout_telefone);
        nascimentoLayout = view.findViewById(R.id.layout_nascimento);


        TextWatcher tw = new TextWatcher() {

            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1950)?1950:(year>2020)?2020:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    nascimentoText.setText(current);
                    nascimentoText.setSelection(sel < current.length() ? sel : current.length());
                }
            }



            @Override
            public void afterTextChanged(Editable s) {

            }



        };

        nascimentoText.addTextChangedListener(tw);


        //botao para fazer o registro
        btnRegistrar.setOnClickListener(v -> registeruser());

        //botao para escolher foto de perfil
        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
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
    private void registeruser() {
        String nome = nomeText.getText().toString();
        String email = emailText.getText().toString();
        String senha = passwordText.getText().toString();
        String pesoAux = pesoText.getText().toString();
        String alturaAux = alturaText.getText().toString();
        String telefone = numeroTelefone.getText().toString();
        String nascimento = nascimentoText.getText().toString();

        String errorM = "Deve ser preenchido";

        if (imgVfoto.getDrawable() == null) {
            Toast.makeText(getContext(), "Selecione uma imagem", Toast.LENGTH_LONG).show();
            return;
        } else if (nome.isEmpty()) {
            nomeLayout.setError(errorM);
            return;
        } else if (email.isEmpty() ){
            nomeLayout.setError(null);
            nomeText.setError(null);
            emailLayout.setError(errorM);
            return;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email inválido");
            emailLayout.setError(null);
            return;
        } else if (senha.isEmpty()){
            emailText.setError(null);
            passwordLayout.setError(errorM);
            return;
        }
        else if (!isPasswordValid(passwordText.getText())) {
            passwordLayout.setError(null);
            passwordText.setError("palavra passe deve ter 6 digitos ou mais");
            return;
        }
       else if (telefone.isEmpty()) {
            passwordText.setError(null);
            numeroLayout.setError(errorM);
            return;
        } else if ( !Patterns.PHONE.matcher(telefone).matches() || telefone.length()!=9) {
            numeroLayout.setError(null);
            numeroTelefone.setError("número invalido");
            return;
        } else if (nascimento.isEmpty()) {
            passwordText.setError(null);
            nascimentoLayout.setError(errorM);
            return;
        } else if (nascimento.charAt(8)=='Y') {
            nascimentoLayout.setError(null);
            nascimentoText.setError("Data de nascimento invalida");
            return;
        }else if (alturaAux.isEmpty()) {
            nascimentoText.setError(null);
            alturaLayout.setError(errorM);
            return;
        } else if (pesoAux.isEmpty()) {
            alturaLayout.setError(null);
            pesoLayout.setError(errorM);
            return;
        }

        double altura = Double.parseDouble(alturaAux);

        if (altura < 1 || altura > 2.5) {
            pesoText.setError(null);
            alturaText.setError("altura invalida");
            return;
        }

        int peso = Integer.parseInt(pesoAux);
        if (!isNumericInt(pesoAux) && (peso < 20 || peso > 120)) {
            pesoLayout.setError(null);
            pesoText.setError("peso invalido");
            return;
        }

        alturaText.setError(null);
        progressBar.setVisibility(View.VISIBLE);
        btnRegistrar.setClickable(false);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("Tester", task.getResult().getUser().getUid());
                        //salva as informaçoes no firebase
                        UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                        builder.setDisplayName(nome);
                        final UserProfileChangeRequest changeRequest = builder.build();
                        task.getResult().getUser().updateProfile(changeRequest);
                        salvarUsuarioNoFireBase();
                    } else {
                        Toast.makeText(getContext(), "Ja existe uma conta com este E-mail", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static boolean isNumericInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
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

            final StorageReference ref = FirebaseStorage.getInstance().getReference("image/" + uid);
            ref.putBytes(data).addOnSuccessListener(taskSnapshot -> {

                Map<String, Object> usuarioData;
                usuarioData = new HashMap<>();
                usuarioData.put("nome", nomeText.getText().toString());
                usuarioData.put("peso", pesoText.getText().toString());
                usuarioData.put("dataNascimento", nascimentoText.getText().toString());
                usuarioData.put("altura", alturaText.getText().toString());
                usuarioData.put("numeroTelefone", numeroTelefone.getText().toString());
                usuarioData.put("tipoConta", "usuario");
                usuarioData.put("Uid", uid);
                usuarioData.put("situacoes", situacoes);
                usuarioData.put("codigo_postal", codigoPostal);
                usuarioData.put("morada", morada);

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
        } catch (Exception e) {

        }
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text.length() >= 6;
    }

}
