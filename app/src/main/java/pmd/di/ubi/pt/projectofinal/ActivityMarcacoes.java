package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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

public class ActivityMarcacoes extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<Marcacao> marcacaoArrayList;
    private AdapterMarcacao adapterMarcacao;
    private FirebaseUser user;
    private String estadoMarcacao, tipoDeConta;
    private Pessoa pessoa;
    private NavigationView nv;
    private ArrayList<String> idsMarcacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);
        marcacaoArrayList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent vimDasOpcoes =  getIntent();
        estadoMarcacao =vimDasOpcoes.getStringExtra("estado");
        tipoDeConta = vimDasOpcoes.getStringExtra("conta");
        idsMarcacoes = new ArrayList<>();
        initView();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("marcaçoes");

        FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    pessoa = task.getResult().toObject(Pessoa.class);
                    idsMarcacoes = pessoa.getMarcacoes();
                    if(idsMarcacoes!=null){
                     initMarcacoes();
                    }
                }
            }
        });

        nv = (NavigationView)findViewById(R.id.nv_user);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch(id)
                {
                    case R.id.nav_opcao_conta:
                        intent = new Intent(ActivityMarcacoes.this, ActivityDefinicoesConta.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_opcao_sair:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(ActivityMarcacoes.this, ActivityLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());

                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_pendente:
                        intent = new Intent(ActivityMarcacoes.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","pedente");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_aceite:
                        intent = new Intent(ActivityMarcacoes.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","aceite");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_paga:
                        intent = new Intent(ActivityMarcacoes.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","paga");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;

                    case  R.id.nav_opcao_historico:
                        intent = new Intent(ActivityMarcacoes.this, ActivityMarcacoes.class);
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
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(1);
        adapterMarcacao = new AdapterMarcacao(this, marcacaoArrayList,tipoDeConta);
        gridView.setAdapter(adapterMarcacao);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.bar_opcao_conta:
                Intent intent = new Intent(this, ActivityDefinicoesConta.class);
                startActivity(intent);
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initMarcacoes(){
        for (String idMarcacao: idsMarcacoes){
            FirebaseFirestore.getInstance().collection("/marcacoes").document(idMarcacao).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document!=null){
                            Marcacao marcacao = document.toObject(Marcacao.class);
                            if(tipoDeConta.equals("personal") && marcacao.getEstado().equals(estadoMarcacao) && marcacao.getPersonalUuid().equals(user.getUid())){
                                marcacaoArrayList.add(marcacao);
                            }
                            if (tipoDeConta.equals("usuario") && marcacao.getEstado().equals(estadoMarcacao) && marcacao.getUsuarioUuid().equals(user.getUid())){
                                marcacaoArrayList.add(marcacao);
                            }
                            if (tipoDeConta.equals("personal") && estadoMarcacao.equals("default") && marcacao.getUsuarioUuid().equals(user.getUid())){
                                marcacaoArrayList.add(marcacao);
                            }
                            if (tipoDeConta.equals("usuario") && estadoMarcacao.equals("default") && (marcacao.getEstado().equals("cancelada")||marcacao.getEstado().equals("terminada"))&& marcacao.getUsuarioUuid().equals(user.getUid())){
                                marcacaoArrayList.add(marcacao);
                            }
                        }
                        Log.d("FirebaseFirestore", marcacaoArrayList.toString());
                        adapterMarcacao.notifyDataSetChanged();
                    }
                }
            });
        }

    }
}