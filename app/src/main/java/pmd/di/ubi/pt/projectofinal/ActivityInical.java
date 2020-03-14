package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class ActivityInical extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inical);
        mAuth = FirebaseAuth.getInstance();
        user =  mAuth.getCurrentUser();
        iniciarSessao();
    }

    public void iniciarSessao(){
        if(user!=null){
            FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.i("activityInicial","activityInicial");
                    DocumentSnapshot document = task.getResult();
                    if (document!=null) {
                        Pessoa pessoa = document.toObject(Pessoa.class);
                        FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
                        Intent intent = new Intent(ActivityInical.this, pessoa.tipoDeConta.equals("usuario")? ActivityModalidades.class:ActivityPersonalTrainerPerfil.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }else {
            Intent intent = new Intent(ActivityInical.this, ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
