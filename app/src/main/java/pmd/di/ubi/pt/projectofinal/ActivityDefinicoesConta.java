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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ActivityDefinicoesConta extends AppCompatActivity {
    private Button btnAlterarNome,btnAlterarPassword,btnFotoPerfil,btnSairDaConta;
    private String uuid;
    private AlertDialog.Builder alertDialog;
    private String novoNome;
    private Usuario usuario;
    private ImageView imgVfoto;
    private Uri fSelecteduri;
    private TextView tvAlterarFoto,tvSalvarFoto;
    private ProgressBar progressBar;
    private StorageReference ref ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes_conta);
        uuid =FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar = (ProgressBar) findViewById(R.id.progressbar_conta);

        btnAlterarNome=findViewById(R.id.btn_alterar_nome);
        btnAlterarPassword = findViewById(R.id.btn_alterar_password);
        imgVfoto = findViewById(R.id.img_foto_definicoes);
        btnFotoPerfil = findViewById(R.id.btn_selected_foto_definiceos);
        tvAlterarFoto = findViewById(R.id.tv_alterar_foto);
        tvSalvarFoto = findViewById(R.id.tv_salvar_foto);
        btnSairDaConta = findViewById(R.id.btn_sair_conta);

        tvAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,0);
            }
        });

        tvSalvarFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filename = UUID.randomUUID().toString();

                    ref = FirebaseStorage.getInstance().getReference("/image/"+filename);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ref.putFile(fSelecteduri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    usuario.setProfileUrl(uri.toString());
                                                    FirebaseFirestore.getInstance().collection("pessoas").document(uuid).set(usuario);
                                                    tvSalvarFoto.setText("Salva");
                                                    tvSalvarFoto.setClickable(false);
                                                }
                                            });
                                        }
                                    });
                        }
                    }).start();

                }
        });

        btnSairDaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ActivityDefinicoesConta.this, ActivityLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(uuid);
                startActivity(intent);
                finish();
            }
        });


        //botao para escolher foto de perfil
        btnFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,0);
            }
        });

        FirebaseFirestore.getInstance().collection("pessoas").document(uuid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    usuario = document.toObject(Usuario.class);
                    btnFotoPerfil.setAlpha(0);
                    Glide.with(imgVfoto.getContext())
                            .load(usuario.getProfileUrl())
                            .into(imgVfoto);
                }}
            }
        });

        btnAlterarNome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog = new AlertDialog.Builder(ActivityDefinicoesConta.this);
                        alertDialog.setTitle("Altere o seu nome");
                        final EditText input = new EditText(ActivityDefinicoesConta.this);
                        input.setText(usuario.getNome());
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        input.setLayoutParams(lp);
                        alertDialog.setView(input);
                        alertDialog.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                novoNome = input.getText().toString();
                                setNome();
                            }
                        });
                        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        alertDialog.show();
                    }
                });
        btnAlterarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDefinicoesConta.this, ActivityMudarPassword.class);
                startActivity(intent);
            }
        });
    }

    public void setNome(){
        assert usuario != null;
        usuario.setNome(novoNome);
        FirebaseFirestore.getInstance().collection("pessoas").document(uuid).set(usuario);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==0) {
            if (data != null) {
                fSelecteduri = data.getData();
                if(fSelecteduri!=null){
                    Bitmap bitmap = null;
                    ImageDecoder.Source source;
                    try {
                        source = ImageDecoder.createSource(this.getContentResolver(), fSelecteduri);
                        bitmap = ImageDecoder.decodeBitmap(source);

                        imgVfoto.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
                        btnFotoPerfil.setAlpha(0);
                        tvSalvarFoto.setVisibility(View.VISIBLE);
                        tvSalvarFoto.setClickable(true);
                    } catch (IOException e) {

                    }

                }
            }
        }
    }


}
