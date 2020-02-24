package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class ActivityRegistro2 extends AppCompatActivity {
    private EditText nomeText,emailText,passwordText,alturaText,pesoText,numeroTelefone ;
    private static Button anivBtn;
    private Button btnRegistrar,btnFoto;
    private Uri fSelecteduri;
    private ImageView imgVfoto;
    private Intent vimDoPrimeiroPasso;
    private ProgressBar progressBar;
    Integer count =1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro2);
        nomeText =  findViewById(R.id.edit_usarname);
        emailText =  findViewById(R.id.edit_email);
        passwordText =  findViewById(R.id.edit_password);
        alturaText = findViewById(R.id.edit_altura);
        pesoText = findViewById(R.id.edit_peso);
        btnRegistrar =  findViewById(R.id.btn_register);
        btnFoto =  findViewById(R.id.btn_selected_foto);
        imgVfoto =  findViewById(R.id.img_foto);
        numeroTelefone = findViewById(R.id.edit_telefone);
        anivBtn = findViewById(R.id.edit_data);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_registro);

        vimDoPrimeiroPasso=getIntent();
        //botao para fazer o registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeruser();
            }
        });

        //botao para escolher foto de perfil
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0);            }
        });

        //botao para escolher data de aniversario
        anivBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==0) {
            if (data != null) {
                fSelecteduri = data.getData();
                Bitmap bitmap = null;
                ImageDecoder.Source source;
                try {
                    source = ImageDecoder.createSource(this.getContentResolver(), fSelecteduri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                    imgVfoto.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
                    btnFoto.setAlpha(0);
                } catch (IOException e) {

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
            Toast.makeText(ActivityRegistro2.this,"Selecione uma imagem", Toast.LENGTH_LONG).show();
            return;
        }

        else if(nome.isEmpty()){
            Toast.makeText(ActivityRegistro2.this,"Nome deve ser preenchido", Toast.LENGTH_LONG).show();
            return;
        }
        else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Insira um email válido");
            return;
        }
        else if(senha.isEmpty() || senha.length() < 6 ){
            Toast.makeText(ActivityRegistro2.this,"Preencha com mais de 5 caracteres", Toast.LENGTH_LONG).show();
            return;
        }
        else if(anivBtn.getText().toString().isEmpty() ){
            Toast.makeText(ActivityRegistro2.this,"Selecione uma data", Toast.LENGTH_LONG).show();
            return;
        }

        else if(alturaAux.isEmpty()){
            Toast.makeText(ActivityRegistro2.this,"Insira uma altura", Toast.LENGTH_LONG).show();
            return;
        }

        else if(pesoAux.isEmpty()){
            Toast.makeText(ActivityRegistro2.this,"insira um peso", Toast.LENGTH_LONG).show();
            return;
        }

        double altura = Double.parseDouble(alturaAux);
        if(!isNumericInt(pesoAux)){

            Toast.makeText(ActivityRegistro2.this,"o peso tem que ser inteiro", Toast.LENGTH_LONG).show();
            return;

        }
        int peso = Integer.parseInt(pesoAux);

        if(peso<20 || peso>120){
            Toast.makeText(ActivityRegistro2.this,"Selecione um peso valido", Toast.LENGTH_LONG).show();
            return;
        }
        else if(altura>1.5 && altura<2.2){
            Toast.makeText(ActivityRegistro2.this,"Selecione uma altura valida", Toast.LENGTH_LONG).show();
            return;
        }



        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.i("Tester",task.getResult().getUser().getUid());
                            //salva as informaçoes no firebase

                            progressBar.setVisibility(View.VISIBLE);

                            new MyTask().execute(100);

                            salvarUsuarioNoFireBase();
                        }
                        else {
                            Toast.makeText(ActivityRegistro2.this, "Ja existe uma conta com este E-mail", Toast.LENGTH_SHORT).show();
                        }
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
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/image/"+filename);
        ref.putFile(fSelecteduri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String uid = FirebaseAuth.getInstance().getUid();
                                String nome = nomeText.getText().toString();
                                String profile = uri.toString();
                                String data = anivBtn.getText().toString();
                                String peso = pesoText.getText().toString();
                                String altura = alturaText.getText().toString();
                                String numeroTel = numeroTelefone.getText().toString();
                                Usuario usuario = new Usuario(uid,nome,profile,data,peso,altura,vimDoPrimeiroPasso.getStringExtra("Doencas"),numeroTel);
                                usuario.setTipoDeConta("usuario");
                                // Cadastrar usuário e criar user id como chave
                                FirebaseFirestore.getInstance().collection("pessoas")
                                        .document(uid)
                                        .set(usuario)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(ActivityRegistro2.this,ActivityInical.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Teste", e.getMessage(),e);
                    }
                });
    }

    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            for (; count <= params[0]; count++) {
                try {
                    Thread.sleep(1000);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

        }
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
}
