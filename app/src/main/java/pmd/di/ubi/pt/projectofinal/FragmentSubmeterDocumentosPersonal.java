package pmd.di.ubi.pt.projectofinal;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class FragmentConfirmarPersonal extends Fragment implements View.OnClickListener {




final int PICK_CURRICULO_REQUEST_CODE = 111;
    final int PICK_ID_REQUEST_CODE = 112;

    private Uri curriculoUri,documentsUri;

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

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {


                    if(curriculoUri!=null && documentsUri !=null){
                        InputStream curriculoStream =   getActivity().getContentResolver().openInputStream(curriculoUri);
                        InputStream documentStream =   getActivity().getContentResolver().openInputStream(documentsUri);

                        Log.i("onActivityResult","stream");

                        final StorageReference ref = FirebaseStorage.getInstance().getReference("documents/"+user.getUid()+"/curriculo.pdf");
                        ref.putStream(curriculoStream);
                        ref.putStream(documentStream);

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

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
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
