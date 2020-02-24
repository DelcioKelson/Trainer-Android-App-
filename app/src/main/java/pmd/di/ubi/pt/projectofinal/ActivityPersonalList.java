package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ActivityPersonalList extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<PersonalTrainer> personalList;
    private String modalidade;
    private AdapterPersonalTrainers adapterPersonalTrainers;
    private String uuid;
    private LinearLayout llOrdenarOpcoes;
    private Button btnOrdenarPreco,btnOrdenarRating;
    private int jaClicadoRating,jaClicadoPreco;
    private ArrayList<PersonalTrainer> personalListOriginal;
    private NavigationView nv;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
// set an exit transition
        uuid = getIntent().getStringExtra("uuid");
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_gridview);
        Intent comeFromModalidade = getIntent();
        llOrdenarOpcoes = findViewById(R.id.ll_ordenar_opcoes);
        btnOrdenarPreco = findViewById(R.id.btn_ordenar_preco);
        btnOrdenarRating = findViewById(R.id.btn_ordenar_rating);
        llOrdenarOpcoes.setVisibility(View.VISIBLE);
        modalidade = comeFromModalidade.getStringExtra("modalidade");
        getSupportActionBar().setTitle("Personais trainers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        jaClicadoRating = 0;
        jaClicadoPreco = 0;

        user = FirebaseAuth.getInstance().getCurrentUser();
        initView();
        initPersonalList();

        nv = (NavigationView)findViewById(R.id.nv_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Modalidades");

        Menu menuNav  = nv.getMenu();


        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;
                switch(id)
                {
                    case R.id.nav_opcao_conta:
                        intent = new Intent(ActivityPersonalList.this, ActivityDefinicoesConta.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_opcao_sair:
                        FirebaseAuth.getInstance().signOut();
                        intent = new Intent(ActivityPersonalList.this, ActivityLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());

                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_pendente:
                        intent = new Intent(ActivityPersonalList.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","pedente");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_aceite:
                        intent = new Intent(ActivityPersonalList.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","aceite");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;
                    case  R.id.nav_opcao_marcacao_paga:
                        intent = new Intent(ActivityPersonalList.this, ActivityMarcacoes.class);
                        intent.putExtra("estado","paga");
                        intent.putExtra("conta","usuario");
                        startActivity(intent);
                        break;

                    case  R.id.nav_opcao_historico:
                        intent = new Intent(ActivityPersonalList.this, ActivityMarcacoes.class);
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

        btnOrdenarRating.setOnClickListener(v -> {

            if(jaClicadoRating==0){
                personalList.sort((o1, o2) -> o1.getClassificacao().compareTo(o2.getClassificacao()));
                jaClicadoRating =1;
                btnOrdenarRating.setCompoundDrawablesWithIntrinsicBounds(0 , 0, R.drawable.baseline_expand_more_black_18dp, 0);
                jaClicadoPreco =0;
                btnOrdenarPreco.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            }
            else
            if (jaClicadoRating==1){
                Collections.reverse(personalList);
                btnOrdenarRating.setCompoundDrawablesWithIntrinsicBounds(0 , 0, R.drawable.baseline_expand_less_black_18dp, 0);

                jaClicadoRating =2;

            }else
            if (jaClicadoRating==2){
                personalList = ( ArrayList<PersonalTrainer>) personalListOriginal.clone();
                jaClicadoRating =0;
                btnOrdenarRating.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);

            }
            Log.i("jaClicadoRating",jaClicadoRating+"");

            adapterPersonalTrainers = new AdapterPersonalTrainers(ActivityPersonalList.this, personalList);
            gridView.setAdapter(adapterPersonalTrainers);
        });

        btnOrdenarPreco.setOnClickListener(v -> {
            if(jaClicadoPreco==0){
                personalList.sort((o1, o2) -> o1.getPreco().compareTo(o2.getPreco()));
                jaClicadoPreco =1;
                btnOrdenarPreco.setCompoundDrawablesWithIntrinsicBounds(0 , 0, R.drawable.baseline_expand_more_black_18dp, 0);
                jaClicadoRating =0;
                btnOrdenarRating.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            }
            else
            if (jaClicadoPreco==1){
                btnOrdenarPreco.setCompoundDrawablesWithIntrinsicBounds(0 , 0, R.drawable.baseline_expand_less_black_18dp, 0);

                Collections.reverse(personalList);
                jaClicadoPreco =2;
            }else
            if (jaClicadoPreco==2){
                personalList = ( ArrayList<PersonalTrainer>) personalListOriginal.clone();
                jaClicadoPreco =0;
                btnOrdenarPreco.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0);
            }
            adapterPersonalTrainers = new AdapterPersonalTrainers(ActivityPersonalList.this, personalList);
            gridView.setAdapter(adapterPersonalTrainers);
        });
    }

    private void initView() {
        gridView =  findViewById(R.id.gridview);
        gridView.setNumColumns(1);
        personalList = new ArrayList<>();
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
                intent = new Intent(ActivityPersonalList.this, ActivityDefinicoesConta.class);
                intent.putExtra("uuid",uuid);
                startActivity(intent);
                return true;

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void initPersonalList(){
            personalList = new ArrayList<>();
            FirebaseFirestore.getInstance().collection("/pessoas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        PersonalTrainer aux;
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            aux = document.toObject(PersonalTrainer.class);
                            if(aux.tipoDeConta.equals("personal")&& modalidade.equals(aux.getEspecialidade())){
                                personalList.add(aux);
                                Log.d("personalAux", aux.toString());
                            }
                        }
                        personalListOriginal =(ArrayList<PersonalTrainer>) personalList.clone();
                        adapterPersonalTrainers = new AdapterPersonalTrainers(ActivityPersonalList.this, personalList);
                        gridView.setAdapter(adapterPersonalTrainers);
                    }
                    else {
                        Log.d("FirebaseFirestore", "Error getting documents: ", task.getException());
                    }
                }
            });



    }
}
