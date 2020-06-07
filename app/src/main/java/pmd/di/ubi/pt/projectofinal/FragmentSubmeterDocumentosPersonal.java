package pmd.di.ubi.pt.projectofinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

public class FragmentSubmeterDocumentosPersonal extends Fragment implements View.OnClickListener {

    private final int PICK_CURRICULO_REQUEST_CODE = 111;
    private final int PICK_ID_REQUEST_CODE = 112;

    private Uri curriculoUri,documentsUri;
    private Map<String, Object> personal;
    private Button btnCurriculo;
    private Button btnID;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DocumentReference personalRef;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmar_personal, container, false);

        setHasOptionsMenu(true);

        btnCurriculo = view.findViewById(R.id.btn_curriculo);
        btnID = view.findViewById(R.id.btn_documento_id);
        Button btnSub = view.findViewById(R.id.btn_submeter_doc);
        personalRef =  FirebaseFirestore.getInstance().collection("pessoas").
                document(user.getUid());

        verificarSubimssao();
        btnSub.setOnClickListener(v -> {

            try {
                if(curriculoUri!=null && documentsUri !=null){
                    final StorageReference ref = FirebaseStorage.getInstance().getReference("documents/"+user.getUid());
                    ref.child("curriculo").putFile(curriculoUri);
                    ref.child("id").putFile(documentsUri);
                    personalRef.update("submeteu",true);
                    criarDialogSub();
                }
            }catch (Exception e){

            }
        });

        btnID.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            startActivityForResult(intent, PICK_ID_REQUEST_CODE);
        });

        btnCurriculo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            startActivityForResult(intent, PICK_CURRICULO_REQUEST_CODE);

        });


        if( !user.isEmailVerified()){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            DialogFragment newFragment = DialogFragmentVerificarEmail.newInstance();
            newFragment.setTargetFragment(this, 300);
            newFragment.setCancelable(false);
            newFragment.show(ft, "dialog");
        }

         return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sair, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.sair) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), Main.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        else {
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");

        int resquest_code_aux = 0;
        switch (v.getId()) {
            case R.id.btn_curriculo:
                resquest_code_aux = PICK_CURRICULO_REQUEST_CODE;
                break;
            case R.id.btn_documento_id:
                resquest_code_aux = PICK_ID_REQUEST_CODE;
                break;

        }
        startActivityForResult(intent, resquest_code_aux);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
            if (data != null) {
                Uri file = data.getData();
                Log.i("onActivityResult",""+file);

                if (requestCode==PICK_ID_REQUEST_CODE) {
                    documentsUri = file;
                    btnID.setCompoundDrawablesWithIntrinsicBounds(null,null,requireActivity().getDrawable(R.drawable.ic_check_white),null);
                }else {
                    curriculoUri = file;
                    btnCurriculo.setCompoundDrawablesWithIntrinsicBounds(null,null,requireActivity().getDrawable(R.drawable.ic_check_white),null);
                }
        }
    }

    public void criarDialogSub(){

    new AlertDialog.Builder(requireActivity())
        .setCancelable(false)
        .setMessage("A sua submissao está a ser analizada")
        .setPositiveButton("Actualizar", (dialog, whichButton) -> {
            verificarSubimssao();
        }).show();
    }

    private void verificarSubimssao() {

        personalRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null) {
                DocumentSnapshot document = task.getResult();
                    personal  =document.getData();
                    try {
                        assert personal != null;
                        if(personal.get("aprovado").equals("sim")){
                            new AlertDialog.Builder(requireActivity())
                                    .setCancelable(false)
                            .setTitle("Já tem a conta aprovada!")
                            .setPositiveButton("continuar", (dialog, id) -> Navigation.findNavController(requireView()).navigate(R.id.action_fragmentConfirmarPersonal_to_fragmentSetupPersonalConta))
                           .show();

                        }else if (personal.get("aprovado").equals("reprovado"))
                        {
                            new AlertDialog.Builder(requireActivity())
                                    .setMessage("A sua submissao foi reprovada, pode submeter novamente")
                                    .setPositiveButton("OK", (dialog, whichButton) -> {
                                    }).show();
                        }else {

                            if((Boolean) personal.get("submeteu")){
                                criarDialogSub();
                            }
                        }
                    }catch (Exception e){ }
            }
        });
    }
}
