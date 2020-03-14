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

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityMarcacoes extends AppCompatActivity {
    private ArrayList<Marcacao> marcacaoArrayList;
    private AdapterMarcacao adapterMarcacao;
    private FirebaseUser user;
    private String estadoMarcacao, tipoDeConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview);
        marcacaoArrayList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent vimDasOpcoes = getIntent();
        estadoMarcacao = vimDasOpcoes.getStringExtra("estado");
        tipoDeConta = vimDasOpcoes.getStringExtra("conta");
        initView();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("marcaçoes");

        NavigationView nv = (NavigationView) findViewById(R.id.nv_user);

        initMarcacoes();
        nv.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;
            switch (id) {
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
                case R.id.nav_opcao_marcacao_pendente:
                    intent = new Intent(ActivityMarcacoes.this, ActivityMarcacoes.class);
                    intent.putExtra("estado", "pedente");
                    intent.putExtra("conta", "usuario");
                    startActivity(intent);
                    break;
                case R.id.nav_opcao_marcacao_aceite:
                    intent = new Intent(ActivityMarcacoes.this, ActivityMarcacoes.class);
                    intent.putExtra("estado", "aceite");
                    intent.putExtra("conta", "usuario");
                    startActivity(intent);
                    break;
                case R.id.nav_opcao_marcacao_paga:
                    intent = new Intent(ActivityMarcacoes.this, ActivityMarcacoes.class);
                    intent.putExtra("estado", "paga");
                    intent.putExtra("conta", "usuario");
                    startActivity(intent);
                    break;

                case R.id.nav_opcao_historico:
                    intent = new Intent(ActivityMarcacoes.this, ActivityMarcacoes.class);
                    intent.putExtra("estado", "default");
                    intent.putExtra("conta", "usuario");
                    startActivity(intent);
                    break;
                default:
                    return true;
            }
            return true;
        });
    }

    private void initView() {
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(1);
        adapterMarcacao = new AdapterMarcacao(this, marcacaoArrayList, tipoDeConta);
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

    public void initMarcacoes() {
        FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid())
                .collection("marcacoes").get().addOnCompleteListener(task -> {
                    QuerySnapshot documentSnapshots = task.getResult();
                    if (documentSnapshots != null) {
                        Marcacao marcacao;
                        for (DocumentSnapshot document : documentSnapshots) {
                            marcacao = document.toObject(Marcacao.class);
                            if (marcacao!=null){
                                if (estadoMarcacao.equals("default") && (marcacao.getEstado().equals("cancelada") || marcacao.getEstado().equals("terminada"))) {
                                    marcacaoArrayList.add(marcacao);
                                }
                                if (marcacao.getEstado().equals(estadoMarcacao)) {
                                    marcacaoArrayList.add(marcacao);
                                }
                            }
                        }
                        Log.d("FirebaseFirestore", marcacaoArrayList.toString());
                        adapterMarcacao.notifyDataSetChanged();
                    }
                });
    }
}
