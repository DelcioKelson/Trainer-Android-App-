package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class ActivityDefinicoesConta extends AppCompatActivity {
    private Button btnFotoPerfil,btnSalvarFoto;
    private ImageView imgVfoto;
    private Uri fSelecteduri;
    private String novoNome;

    private ProgressBar progressBar;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference  ref = FirebaseStorage.getInstance().getReference("image/"+user.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes_conta);

        progressBar = (ProgressBar) findViewById(R.id.progressbar_conta);
        Button btnAlterarNome = findViewById(R.id.btn_alterar_nome);
        Button btnAlterarPassword = findViewById(R.id.btn_alterar_password);
        imgVfoto = findViewById(R.id.img_foto_definicoes);
        btnFotoPerfil = findViewById(R.id.btn_selected_foto_definiceos);
        TextView tvAlterarFoto = findViewById(R.id.tv_alterar_foto);
        btnSalvarFoto = findViewById(R.id.btn_salvar_foto);
        Button btnSairDaConta = findViewById(R.id.btn_sair_conta);

        //Titulo e Botao para voltar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Definiçoes de conta");

        btnFotoPerfil.setAlpha(0);
        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            if(bytes.length!=0){
                Glide.with(imgVfoto.getContext())
                        .load(bytes)
                        .into(imgVfoto);
            }
        });

        tvAlterarFoto.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0);
        });

        btnSalvarFoto.setOnClickListener(v -> salvarFoto());

        btnSairDaConta.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ActivityDefinicoesConta.this, ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());
            startActivity(intent);
            finish();
        });

        //botao para escolher foto de perfil
        btnFotoPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,0);
        });

        btnAlterarNome.setOnClickListener(v -> criarDialogMudarNome());

        btnAlterarPassword.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityDefinicoesConta.this, ActivityMudarPassword.class);
            startActivity(intent);
        });
    }

    private void salvarFoto(){
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), fSelecteduri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                byte[] data = baos.toByteArray();
                ref.putBytes(data).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        btnSalvarFoto.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ActivityDefinicoesConta.this,"Fotografia salva com sucesso", Toast.LENGTH_LONG).show();
                    }
                });
            }catch (Exception ignored){
            }
        }).start();
    }

    public void criarDialogMudarNome(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityDefinicoesConta.this);
        alertDialog.setTitle("Altere o seu nome");
        final EditText input = new EditText(ActivityDefinicoesConta.this);
        input.setText(user.getDisplayName());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Adicionar", (dialog, whichButton) -> {
            progressBar.setVisibility(View.VISIBLE);

            new Thread(()->
            {
                novoNome = input.getText().toString();
                UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                builder.setDisplayName(novoNome);
                final UserProfileChangeRequest changeRequest = builder.build();
                user.updateProfile(changeRequest);

                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid());


                CollectionReference CC = FirebaseFirestore.getInstance().collection("comentarios");

                CC.get().addOnCompleteListener(task -> {

                    if(task.isSuccessful() && task.getResult()!=null){
                        for (DocumentSnapshot ds :task.getResult()){
                            ds.getReference().collection("comentadores").document(user.getUid()).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful() && task1.getResult() != null) {
                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                    Comentario comentario = documentSnapshot.toObject(Comentario.class);
                                    if (comentario != null) {
                                        comentario.setComentadorNome(novoNome);
                                        documentSnapshot.getReference().set(comentario);
                                    }
                                    Toast.makeText(ActivityDefinicoesConta.this, "Nome salvo com sucesso", Toast.LENGTH_LONG).show();

                                    progressBar.setVisibility(View.GONE);
                                }

                            });
                        }
                    }

                });
                        FirebaseFirestore.getInstance().collection("pessoas").whereEqualTo("tipoDeConta", "personal")
                        .get().addOnCompleteListener(task -> {

                    if (task.isSuccessful() && task.getResult() != null) {

                        for (DocumentSnapshot ds : task.getResult()) {
                            DocumentReference dr = FirebaseFirestore.getInstance().collection("comentarios").document(ds.getId()).
                                    collection("comentadores").document(user.getUid());

                            dr.get().addOnCompleteListener(task2 -> {

                            });
                        }
                    }

                });
            }).start();

        }).setNegativeButton("Cancelar", (dialog, which) -> {
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0) {
            if (data != null) {
                fSelecteduri = data.getData();
                if(fSelecteduri!=null){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fSelecteduri);
                        imgVfoto.setImageBitmap(bitmap);
                        btnFotoPerfil.setAlpha(0);
                        btnSalvarFoto.setVisibility(View.VISIBLE);
                        btnSalvarFoto.setClickable(true);
                    } catch (IOException ignored) {

                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
