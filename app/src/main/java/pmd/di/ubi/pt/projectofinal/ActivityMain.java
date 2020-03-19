package pmd.di.ubi.pt.projectofinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class ActivityMain extends AppCompatActivity {

    FirebaseUser user ;
    private DrawerLayout drawer;

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private NavController navController;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user =  FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.i("activityInicial","activityInicial");
                    DocumentSnapshot document = task.getResult();
                    if (document!=null) {

                        SharedDataModel modelData = new ViewModelProvider(this).get(SharedDataModel.class);
                        modelData.init();

                        FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
                        if(document.getString("tipoConta").equals("usuario")){
                            modelData.usuario();
                            iniciarSessaoUsuario();
                        }else {
                            modelData.personal();
                            iniciarSessaoPersonal();
                        }
                        setSupportActionBar(toolbar);
                        mAppBarConfiguration =
                                new AppBarConfiguration.Builder(navController.getGraph()).setDrawerLayout(drawer).build();

                        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
                        NavigationUI.setupWithNavController(navigationView, navController);
                    }
                }
            });
        }
        else {
            {
                setContentView(R.layout.activity_main_login);
            }
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void iniciarSessaoUsuario(){
        setContentView(R.layout.activity_main_usuario);
        toolbar = findViewById(R.id.toolbar_usuario);
        drawer = findViewById(R.id.drawer_layout_user);
        navigationView = findViewById(R.id.nav_usuario);
        navController = Navigation.findNavController(this, R.id.container_usuario);

    }

    public void iniciarSessaoPersonal(){
        setContentView(R.layout.activity_main_personal);
        toolbar = findViewById(R.id.toolbar_personal);
        drawer = findViewById(R.id.drawer_layout_personal);
        navigationView = findViewById(R.id.nav_personal);
        navController = Navigation.findNavController(this, R.id.container_personal);
    }
}


