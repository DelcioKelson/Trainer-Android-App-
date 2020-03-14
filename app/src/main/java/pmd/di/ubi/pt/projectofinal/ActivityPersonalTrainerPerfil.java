package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ActivityPersonalTrainerPerfil extends AppCompatActivity {

    private ImageView ivPerfil;
    private TextView nome,tvInfoPerfil,tvNumeroComentario;
    private RatingBar rbPerfil,comentarioRating;
    private EditText inputComentario;
    private FirebaseUser user;
    private GridView gridView;
    private AlertDialog.Builder alertDialog;
    private ArrayList<Comentario> comentariosList;
    private AdapterComentario adapterComentario;
    private String uuidPersonal;

    private int numeroMarcacaosPedentes;
    private int numeroMarcacaosAceites;
    private int numeroMarcacaosPagas;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private MenuItem itemPendentes;
    private MenuItem itemAceites;
    private MenuItem itemPagas;
    private int userPersonalFlag,jaExisteComentarioFlag;
    private CollectionReference comentariosReference;
    private PersonalTrainer personalTrainer;
    private TextView tvNavHeader;
    private ImageView imgVfoto;
    private CollectionReference pessoaRef =  FirebaseFirestore.getInstance().collection("pessoas");
    private CollectionReference marcacoesUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_trainer_perfil);
        nome = findViewById(R.id.tv_nome_perfil);
        tvInfoPerfil=findViewById(R.id.tv_info_perfil);
        ivPerfil = findViewById(R.id.iv_perfil);
        rbPerfil = findViewById(R.id.rb_perfil);
        nv = (NavigationView)findViewById(R.id.nv_personal);

        ExtendedFloatingActionButton btnComentar = findViewById(R.id.btn_comentar);
        ExtendedFloatingActionButton btnGerarPreco = findViewById(R.id.btn_gerar_preco);

        tvNumeroComentario = findViewById(R.id.tv_numero_comentarios);
        gridView = findViewById(R.id.gridview_comentario);
        comentariosList = new ArrayList<>();
        uuidPersonal = getIntent().getStringExtra("uuid");

        user = FirebaseAuth.getInstance().getCurrentUser();

        marcacoesUserRef = pessoaRef.document(user.getUid()).collection("marcacoes");

        Objects.requireNonNull(getSupportActionBar()).setTitle("Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        comentariosReference = FirebaseFirestore.getInstance().collection("comentarios").document(uuidPersonal).collection("comentadores");

        initPerfil();

        userPersonalFlag=0;

        btnComentar.setVisibility(View.GONE);

        if(uuidPersonal==null){
            uuidPersonal = user.getUid();
            userPersonalFlag=1;
            DrawerLayout dl = (DrawerLayout) findViewById(R.id.activity_perfil_personal);
            t = new ActionBarDrawerToggle(this, dl,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            dl.addDrawerListener(t);
            t.syncState();
            View headerView = nv.getHeaderView(0);
            tvNavHeader = headerView.findViewById(R.id.tv_navheader);
            imgVfoto = headerView.findViewById(R.id.img_foto_nav);
            Menu menuNav  = nv.getMenu();
            itemPendentes = menuNav.findItem(R.id.nav_opcao_marcacao_pendente);
            itemAceites = menuNav.findItem(R.id.nav_opcao_marcacao_aceite);
            itemPagas = menuNav.findItem(R.id.nav_opcao_marcacao_paga);
            numeroMarcacoes();
            btnGerarPreco.setVisibility(View.GONE);
        }else {
            marcacoesUserRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult()!=null){
                    QuerySnapshot querySnapshot = task.getResult();
                    for(DocumentSnapshot documentSnapshot: querySnapshot){
                        if (documentSnapshot.toObject(Marcacao.class).getEstado().equals("terminada")){
                           btnComentar.setVisibility(View.VISIBLE);
                            btnComentar.setOnClickListener(v -> {
                                criarDialogoComentario();
                            });
                        }
                    }
                }
            });

            btnGerarPreco.setOnClickListener(v -> {
                Intent intent = new Intent(ActivityPersonalTrainerPerfil.this, ActivityFazerMarcacao.class);
                intent.putExtra("uuid",uuidPersonal);
                startActivity(intent);
            });
        }
        setNavOption();

    }

    public void criarDialogoComentario(){
        alertDialog = new AlertDialog.Builder(ActivityPersonalTrainerPerfil.this);
        alertDialog.setTitle("adicione uma avaliaçao");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.avaliacao_layout_dialog,null);
        alertDialog.setView(dialogView);
        inputComentario = dialogView.findViewById(R.id.comentario_dialog_txtv);
        comentarioRating =dialogView.findViewById(R.id.comentario_rating);
        Comentario comentario;
        jaExisteComentarioFlag =-1;
        for (int i = 0;i<comentariosList.size();i++) {
            comentario = comentariosList.get(i);
            if (comentario.getComentadorId().equals(user.getUid())){
                inputComentario.setText(comentario.getComentario());
                comentarioRating.setRating(comentario.getClassificacao());
                jaExisteComentarioFlag = i;
                break;
            }
        }

        alertDialog.setPositiveButton("Adicionar", (dialog, whichButton) -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            Comentario comentario1 = new Comentario(inputComentario.getText().toString(),comentarioRating.getRating(),user.getUid(),user.getDisplayName(),"data:"+day+"/"+month+"/"+year);
            comentariosReference.document(user.getUid()).set(comentario1);

            if (jaExisteComentarioFlag!=-1){
                comentariosList.set(jaExisteComentarioFlag, comentario1);
            }

            comentariosReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    if(task.getResult()!=null){
                        Float soma=0.0f;
                        int numeroComentarios = task.getResult().size();
                        Comentario comentario11;
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            comentario11 = documentSnapshot.toObject(Comentario.class);
                            soma = comentario11.getClassificacao() + soma;
                        }
                        personalTrainer.setClassificacao(soma/numeroComentarios);
                        pessoaRef.document(uuidPersonal).set(personalTrainer);
                    }
                }
            });
            gridView.setAdapter(new AdapterComentario(ActivityPersonalTrainerPerfil.this, comentariosList));
        });
        alertDialog.setNegativeButton("Cancelar", (dialog, whichButton) -> {
        });
        alertDialog.show();

    }

    public void initPerfil(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image/"+ (uuidPersonal+".jpeg"));
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            if(bytes.length!=0){
                Glide.with(getApplicationContext()).load(bytes).into(ivPerfil);
                if(userPersonalFlag==1){
                    Glide.with(getApplicationContext())
                        .load(bytes)
                        .into(imgVfoto);
                }
            }
        });

        pessoaRef.document(uuidPersonal).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult() ;
                if (document!=null) {
                     personalTrainer = document.toObject(PersonalTrainer.class);
                            if(userPersonalFlag==1){
                                tvNavHeader.setText("Nome: " + personalTrainer.getNome());
                            }
                    nome.setText(personalTrainer.getNome());
                }
            }
        });

        comentariosReference.get().addOnCompleteListener(task12 -> {
            if (task12.isSuccessful()){
                if(task12.getResult()!=null){
                    Float soma=0.0f;
                    int numeroComentarios = task12.getResult().size();
                    Comentario comentario;
                    for (QueryDocumentSnapshot documentSnapshot : task12.getResult()){
                        comentario = documentSnapshot.toObject(Comentario.class);
                        comentariosList.add(comentario);
                        soma = comentario.getClassificacao() + soma;
                    }
                    rbPerfil.setVisibility(View.VISIBLE);
                    rbPerfil.setRating(soma/numeroComentarios);
                    tvNumeroComentario.setVisibility(View.VISIBLE);
                    if(numeroComentarios==1){
                        tvNumeroComentario.setText("1 comentario");
                    }else {
                        tvNumeroComentario.setText(numeroComentarios + " comentarios");
                    }
                    adapterComentario = new AdapterComentario(ActivityPersonalTrainerPerfil.this, comentariosList);
                    gridView.setAdapter(adapterComentario);
                }else {
                    tvInfoPerfil.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent ;
        switch (item.getItemId()) {
            case R.id.bar_opcao_conta:
                 intent = new Intent(this, ActivityDefinicoesConta.class);
                 startActivity(intent);
                return true;
            case android.R.id.home:
                if(userPersonalFlag==0){
                    finish();
                    return true;
                }else {
                    if(t.onOptionsItemSelected(item))
                        return true;
                }
            default:
                if(t.onOptionsItemSelected(item))
                    return true;
                return super.onOptionsItemSelected(item);
        }
    }

    public void numeroMarcacoes(){
        numeroMarcacaosPedentes = 0;
        numeroMarcacaosAceites = 0;
        numeroMarcacaosPagas = 0;
        marcacoesUserRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot document : queryDocumentSnapshots){
                    Marcacao marcacao = document.toObject(Marcacao.class);
                    if (marcacao!=null){
                        if (marcacao.getEstado().equals("pedente")){
                            numeroMarcacaosPedentes = numeroMarcacaosPedentes +1;
                        }
                        if (marcacao.getEstado().equals("aceite")){
                            numeroMarcacaosAceites=1+ numeroMarcacaosAceites;
                        }
                        if (marcacao.getEstado().equals("pagas")){
                            numeroMarcacaosPagas = 1+ numeroMarcacaosPagas;
                        }
                    }
                    itemPendentes.setTitle("Marcacoes pendentes " + "(" +numeroMarcacaosPedentes +")");
                    itemAceites.setTitle("Marcacoes aceites " + "(" +numeroMarcacaosAceites +")");
                    itemPagas.setTitle("Marcacoes pagas " + "(" +numeroMarcacaosPagas +")");
                }
            }
        });
    }

    public void setNavOption() {
        nv.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;
            switch(id)
            {
                case R.id.nav_opcao_conta:
                    intent = new Intent(ActivityPersonalTrainerPerfil.this, ActivityDefinicoesConta.class);
                    startActivity(intent);
                    break;
                case R.id.nav_opcao_sair:
                    FirebaseAuth.getInstance().signOut();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());
                    intent = new Intent(ActivityPersonalTrainerPerfil.this, ActivityLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case  R.id.nav_opcao_marcacao_pendente:
                    intent = new Intent(ActivityPersonalTrainerPerfil.this, ActivityMarcacoes.class);
                    intent.putExtra("estado","pedente");
                    intent.putExtra("conta","personal");
                    startActivity(intent);
                    break;
                case  R.id.nav_opcao_marcacao_aceite:
                    intent = new Intent(ActivityPersonalTrainerPerfil.this, ActivityMarcacoes.class);
                    intent.putExtra("estado","aceite");
                    intent.putExtra("conta","personal");
                    startActivity(intent);
                    break;
                case  R.id.nav_opcao_marcacao_paga:
                    intent = new Intent(ActivityPersonalTrainerPerfil.this, ActivityMarcacoes.class);
                    intent.putExtra("estado","paga");
                    intent.putExtra("conta","personal");
                    startActivity(intent);
                    break;
                case  R.id.nav_opcao_historico:
                    intent = new Intent(ActivityPersonalTrainerPerfil.this, ActivityMarcacoes.class);
                    intent.putExtra("estado","default");
                    intent.putExtra("conta","personal");
                    startActivity(intent);
                    break;
                default:
                    return true;
            }
            return true;
        });
    }
}
