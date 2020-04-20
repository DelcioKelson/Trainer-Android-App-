package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FragmentSubmeterDocumentosPersonal extends Fragment implements View.OnClickListener {

    private final int PICK_CURRICULO_REQUEST_CODE = 111;
    private final int PICK_ID_REQUEST_CODE = 112;

    private Uri curriculoUri,documentsUri;
    private Map<String, Object> personal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmar_personal, container, false);

        setHasOptionsMenu(true);

        Button btnCurriculo,btnID,btnSub;
        btnCurriculo = view.findViewById(R.id.btn_curriculo);
        btnID = view.findViewById(R.id.btn_documento_id);
        btnSub = view.findViewById(R.id.btn_submeter_doc);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore.getInstance().collection("pessoas").
                document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult()!=null) {
                DocumentSnapshot document = task.getResult();
                if(document!=null){
                    personal  =document.getData();
                    try {
                        if(personal.get("aprovado").equals("sim")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            builder.setMessage("Já tem a conta aprovada, pretende continuar?");

                            builder.setPositiveButton("continuar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Navigation.findNavController(view).navigate(R.id.action_fragmentConfirmarPersonal_to_fragmentSetupPersonalConta);
                                }
                            });
                            builder.setNegativeButton("ficar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });

                            builder.create().show();

                        }
                    }catch (Exception e){ }

                }
            }

        });


        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if(curriculoUri!=null && documentsUri !=null){

                        Log.i("onActivityResult","stream");

                        final StorageReference ref = FirebaseStorage.getInstance().getReference("documents/"+user.getUid());
                        ref.child("curriculo.pdf").putFile(curriculoUri);
                        ref.child("id.pdf").putFile(documentsUri);

                    }

                }catch (Exception e){

                }
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
            Intent intent = new Intent(getActivity(), ActivityMain.class);
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
                }else {
                    curriculoUri = file;
                }


        }
    }


}
