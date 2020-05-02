package pmd.di.ubi.pt.projectofinal;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FragmentRegistroCliente2 extends Fragment {

    private String situacoes;

    private TextInputEditText emailText, passwordText, alturaText, pesoText, numeroTelefone, morada;
    private TextInputEditText nomeText;
    private TextInputEditText nascimentoText;
    private Button btnFoto, btnRegistrar;
    private Uri fSelecteduri;
    private ImageView imgVfoto;
    private ProgressBar progressBar;

    private AutoCompleteTextView generoAutoComplete;


    private TextInputLayout emailLayout, passwordLayout, alturaLayout, pesoLayout,
            numeroLayout, nomeLayout, nascimentoLayout, moradaLayout,generoLayout;


    public FragmentRegistroCliente2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            situacoes = getArguments().getString("situacoes");
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
        morada = view.findViewById(R.id.edit_morada);
        moradaLayout = view.findViewById(R.id.layout_morada);
        generoAutoComplete = view.findViewById(R.id.ac_genero);


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
        generoLayout = view.findViewById(R.id.layout_genero);


        generoAutoComplete.setInputType(0);
        generoAutoComplete.setAdapter(new ArrayAdapter(requireContext(),R.layout.genero_list_element,Arrays.asList("M","F","Outro")));

        TextWatcher tw = new TextWatcher() {

            private String current = "";
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

                    if (clean.length() < 8) {
                        String ddmmyyyy = "DDMMYYYY";
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1950) ? 1950 : (year > 2020) ? 2020 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
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
        final String nome = nomeText.getText().toString();
        final String email = emailText.getText().toString();
        final String senha = passwordText.getText().toString();
        final String pesoAux = pesoText.getText().toString();
        final String alturaAux = alturaText.getText().toString();
        final String telefone = numeroTelefone.getText().toString();
        final String nascimento = nascimentoText.getText().toString();
        final String moradaText = morada.getText().toString();
        final String genero =   generoAutoComplete.getText().toString();


        String errorM = "Deve ser preenchido";
        boolean erro = false;

        if (imgVfoto.getDrawable() == null) {
            Toast.makeText(getContext(), "Selecione uma imagem", Toast.LENGTH_LONG).show();
            return;
        }
        if (nome.isEmpty()) {
            nomeLayout.setError(errorM);
            erro = true;
        }
        else {
            nomeLayout.setError(null);
        }

        if (email.isEmpty()) {
            emailLayout.setError(errorM);
            erro = true;
        }
        else {
            emailLayout.setError(null);
        }

        Log.i("Registro","passei");
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Email inválido");
            erro = true;
        }else {
            emailText.setError(null);
        }

        if (senha.isEmpty()) {
            passwordLayout.setError(errorM);
            erro =true;
        }else {
            passwordLayout.setError(null);

            if (!isPasswordValid(passwordText.getText())) {
                passwordText.setError("palavra passe deve ter 6 digitos ou mais");
                erro = true;
            }else {
                passwordText.setError(null);
            }
        }

        Log.i("Registro","passei");

        if (telefone.isEmpty()) {
            numeroLayout.setError(errorM);
            erro = true;
        }else {
            numeroLayout.setError(null);
            if (!Patterns.PHONE.matcher(telefone).matches() || telefone.length() != 9) {
                numeroTelefone.setError("número invalido");
                erro = true;
            }
            else {
                numeroTelefone.setError(null);
            }
        }

        if (nascimento.isEmpty()) {
            nascimentoLayout.setError(errorM);
            erro = true;
        }else {
            nascimentoLayout.setError(null);
            if (nascimento.charAt(8) == 'Y') {
                nascimentoText.setError("Data de nascimento invalida");
                erro=true;
            }
            else {
                nascimentoText.setError(null);
            }
        }

        if (alturaAux.isEmpty()) {
            alturaLayout.setError(errorM);
            erro = true;
        }else {
            alturaLayout.setError(null);
            double altura = Double.parseDouble(alturaAux);

            if (altura < 1 || altura > 2.5) {
                alturaText.setError("altura invalida");
                erro = true;
            }else {
                alturaText.setError(null);
            }
        }

        if (pesoAux.isEmpty()) {
            pesoLayout.setError(errorM);
            erro = true;
        }
        else {
            pesoLayout.setError(null);
            int peso = Integer.parseInt(pesoAux);
            if (!isNumericInt(pesoAux) && (peso < 20 || peso > 120)) {
                pesoText.setError("peso invalido");
                erro = true;
            }
            else {
                pesoText.setError(null);
            }
        }

        if (moradaText.isEmpty()) {
            moradaLayout.setError(errorM);
            erro = true;
        }else {
            moradaLayout.setError(null);
        }

        if(genero.isEmpty()){
            generoLayout.setError(errorM);
            erro=true;
        }else {
            generoLayout.setError(null);
        }

        if (erro){
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
                        btnRegistrar.setClickable(true);
                        progressBar.setVisibility(View.GONE);


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
                usuarioData.put("morada", morada.getText().toString());
                usuarioData.put("genero",generoAutoComplete.getText().toString());

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
        } catch (Exception e) {

        }
    }

    private boolean isPasswordValid(@Nullable Editable text) {
        return text.length() >= 6;
    }

}
