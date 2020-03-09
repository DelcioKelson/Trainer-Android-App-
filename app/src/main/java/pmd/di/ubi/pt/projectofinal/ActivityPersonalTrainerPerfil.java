package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
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
    private ExtendedFloatingActionButton btnComentar,btnGerarPreco;
    private ArrayList<Comentario> comentariosList;
    private AdapterComentario adapterComentario;
    private String uuidPersonal;

    private int numeroMarcacaosPedentes;
    private int numeroMarcacaosAceites;
    private int numeroMarcacaosPagas;
    private DrawerLayout dl;
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
    private CollectionReference pessoaRef =  FirebaseFirestore.getInstance().collection("/pessoas");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_trainer_perfil);
        nome = findViewById(R.id.tv_nome_perfil);
        tvInfoPerfil=findViewById(R.id.tv_info_perfil);
        ivPerfil = findViewById(R.id.iv_perfil);
        rbPerfil = findViewById(R.id.rb_perfil);
        btnComentar = findViewById(R.id.btn_comentar);
        tvNumeroComentario = findViewById(R.id.tv_numero_comentarios);
        gridView = findViewById(R.id.gridview_comentario);
        btnGerarPreco = findViewById(R.id.btn_gerar_preco);
        comentariosList = new ArrayList<>();
        uuidPersonal = getIntent().getStringExtra("uuid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        getSupportActionBar().setTitle("Perfil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        comentariosReference = FirebaseFirestore.getInstance().collection("comentarios").document(uuidPersonal).collection("comentadores");

        initPerfil();

        userPersonalFlag=0;

        nv = (NavigationView)findViewById(R.id.nv_personal);


        if(uuidPersonal.equals(user.getUid())){
            userPersonalFlag=1;

            dl = (DrawerLayout)findViewById(R.id.activity_perfil_personal);
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
            setNavOption();

            btnComentar.setVisibility(View.GONE);
            btnGerarPreco.setVisibility(View.GONE);

        }else {
            setNavOption();
            btnComentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    criarDialogoComentario();
                    alertDialog.show();
                }
            });


            btnGerarPreco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ActivityPersonalTrainerPerfil.this, ActivityFazerMarcacao.class);
                    intent.putExtra("uuid",uuidPersonal);
                    startActivity(intent);
                }
            });
        }
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
            if (comentario.getComentador().equals(user.getUid())){
                inputComentario.setText(comentario.getComentario());
                comentarioRating.setRating(comentario.getClassificacao());
                jaExisteComentarioFlag = i;
                break;
            }
        }

        alertDialog.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Comentario comentario = new Comentario(inputComentario.getText().toString(),comentarioRating.getRating(),user.getUid());
                comentariosReference.document(user.getUid()).set(comentario);
                if (jaExisteComentarioFlag!=-1){
                    comentariosList.set(jaExisteComentarioFlag,comentario);
                }
                comentariosReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if(task.getResult()!=null){
                                Float soma=0.0f;
                                int numeroComentarios = task.getResult().size();
                                Comentario comentario;
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    comentario = documentSnapshot.toObject(Comentario.class);
                                    soma = comentario.getClassificacao() + soma;
                                }
                                personalTrainer.setClassificacao(soma/numeroComentarios);
                                FirebaseFirestore.getInstance().collection("pessoas").document(uuidPersonal).set(personalTrainer);
                            }
                        }
                    }
                });
                gridView.setAdapter(new AdapterComentario(ActivityPersonalTrainerPerfil.this, comentariosList));

            }});
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
    }


    public void initPerfil(){
        FirebaseFirestore.getInstance().collection("/pessoas").document(uuidPersonal).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult() ;
                    if (document.exists()) {
                         personalTrainer = document.toObject(PersonalTrainer.class);
                         if(userPersonalFlag==1){
                             Glide.with(imgVfoto.getContext())
                                     .load(personalTrainer.getProfileUrl())
                                     .into(imgVfoto);
                             tvNavHeader.setText("Nome: " + personalTrainer.getNome());
                         }

                        nome.setText(personalTrainer.getNome());
                        Glide.with(ivPerfil.getContext())
                                .load(personalTrainer.getProfileUrl())
                                .into(ivPerfil);
                        comentariosReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if(task.getResult()!=null){
                                        Float soma=0.0f;
                                        int numeroComentarios = task.getResult().size();
                                        Comentario comentario;
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
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
                            }
                        });
                    }
                }
                else {
                    Log.d("FirebaseFirestore", "Error getting documents: ");
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

        pessoaRef.document(user.getUid()).collection("marcacoes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
            }
        });
    }

    public void setNavOption() {
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
    }

}
