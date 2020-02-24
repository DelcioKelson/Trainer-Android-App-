package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;


import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ActivityModalidades extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<Modalidade> modalidadeList;
    private AdapterModalidades adapterModalidades;
    private ArrayList <Marcacao> marcacaoPedenteArrayList;
    private ArrayList <Marcacao> marcacaoAceitesArrayList;
    private ArrayList <Marcacao> marcacaoPagasArrayList;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private FirebaseUser user;
    private MenuItem itemPendentes;
    private MenuItem itemAceites;
    private MenuItem itemPagas;
    private  ArrayList<String> idMarcacoes;
    private TextView tvNavHeader;
    private Usuario usuario;
    private ImageView imgVfoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modalidadeList = new ArrayList<>();
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        user = FirebaseAuth.getInstance().getCurrentUser();

        // set an exit transition
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_gridview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Modalidades");

        dl = (DrawerLayout)findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(t);
        t.syncState();
        nv = (NavigationView)findViewById(R.id.nv_user);
        View headerView = nv.getHeaderView(0);

        tvNavHeader = headerView.findViewById(R.id.tv_navheader);
        imgVfoto = headerView.findViewById(R.id.img_foto_nav);

        idMarcacoes = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful()){
                   DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot!=null){
                             usuario = documentSnapshot.toObject(Usuario.class);
                            idMarcacoes = usuario.getMarcacoes();
                           numeroMarcacoes();
                           tvNavHeader.setText("Nome: " + usuario.getNome());
                            Glide.with(imgVfoto.getContext())
                                    .load(usuario.getProfileUrl())
                                    .into(imgVfoto);
                        }
               }
            }

        }
        );


        Menu menuNav  = nv.getMenu();
        itemPendentes = menuNav.findItem(R.id.nav_opcao_marcacao_pendente);
        itemAceites = menuNav.findItem(R.id.nav_opcao_marcacao_aceite);
        itemPagas = menuNav.findItem(R.id.nav_opcao_marcacao_paga);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch(id)
                {
                    case R.id.nav_opcao_conta:
                        intent = new Intent(ActivityModalidades.this, ActivityDefinicoesConta.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_opcao_sair:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(ActivityModalidades.this, ActivityLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());

                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_pendente:
                        intent = new Intent(ActivityModalidades.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","pedente");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_aceite:
                        intent = new Intent(ActivityModalidades.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","aceite");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_paga:
                        intent = new Intent(ActivityModalidades.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","paga");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;

                    case  R.id.nav_opcao_historico:
                        intent = new Intent(ActivityModalidades.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","default");
                        intent.putExtra("conta","usuario");

                        startActivity(intent);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        initView();
        FirebaseFirestore.getInstance().collection("/modalidades").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                           modalidadeList.add(document.toObject(Modalidade.class));
                        Log.d("FirebaseFirestore", document.getId() + " => " + modalidadeList);
                    }
                    adapterModalidades.notifyDataSetChanged();
                }
                else {
                    Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        adapterModalidades = new AdapterModalidades(this, modalidadeList);
        gridView.setAdapter(adapterModalidades);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    public void numeroMarcacoes(){
        marcacaoPedenteArrayList = new ArrayList<>();
        marcacaoAceitesArrayList = new ArrayList<>();
        marcacaoPagasArrayList = new ArrayList<>();

        if (idMarcacoes!=null){
            for (final String marcacaoid: idMarcacoes){

                FirebaseFirestore.getInstance().collection("/marcacoes").document(marcacaoid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Marcacao marcacao = document.toObject(Marcacao.class);
                            if (marcacao.getEstado().equals("pendente") && marcacao.getUsuarioUuid().equals(user.getUid())){
                                marcacaoPedenteArrayList.add(marcacao);
                            }
                            if (marcacao.getEstado().equals("aceite") && marcacao.getUsuarioUuid().equals(user.getUid())){
                                marcacaoAceitesArrayList.add(marcacao);
                            }
                            if (marcacao.getEstado().equals("pagas") && marcacao.getUsuarioUuid().equals(user.getUid())){
                                marcacaoPagasArrayList.add(marcacao);
                            }
                        }
                        itemPendentes.setTitle("Marcacoes pendentes " + "(" +marcacaoPedenteArrayList.size() +")");
                        itemAceites.setTitle("Marcacoes aceites " + "(" +marcacaoAceitesArrayList.size() +")");
                        itemPagas.setTitle("Marcacoes pagas " + "(" +marcacaoPagasArrayList.size() +")");

                        Log.i("numeroMarcacaoes",""+marcacaoPedenteArrayList.size());
                    }

                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.bar_opcao_conta:
                Intent intent = new Intent(this, ActivityDefinicoesConta.class);
                startActivity(intent);
                return true;
            default:
                if(t.onOptionsItemSelected(item))
                    return true;
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        numeroMarcacoes();
    }
}