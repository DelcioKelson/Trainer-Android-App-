package pmd.di.ubi.pt.projectofinal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FragmentDefinicoesConta extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Button btnFotoPerfil,btnSalvarFoto;
    private ImageView imgVfoto;
    private Uri fSelecteduri;
    private String novoNome;
    private ProgressBar progressBar;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference ref ;

    public FragmentDefinicoesConta() {
        // Required empty public constructor
    }

    public static FragmentDefinicoesConta newInstance() {
        return new FragmentDefinicoesConta();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definicoes_conta, container, false);

        SharedDataModel modelData = new ViewModelProvider(requireActivity()).get(SharedDataModel.class);
        boolean isUser = modelData.isUser().getValue();

        if (isUser){
            ref = FirebaseStorage.getInstance().getReference("image/"+user.getUid());

        }else {
           ref = FirebaseStorage.getInstance().getReference("image/"+user.getUid()+".jpeg");
            Log.i("refdefinicoes", "hgvjh");

        }

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_conta);
        Button btnAlterarNome = view.findViewById(R.id.btn_alterar_nome);
        Button btnAlterarPassword = view.findViewById(R.id.btn_alterar_password);
        Button btnDiasIndisponiveis = view.findViewById(R.id.btn_dias_indispinives);
        imgVfoto = view.findViewById(R.id.img_foto_definicoes);
        btnFotoPerfil = view.findViewById(R.id.btn_selected_foto_definiceos);
        TextView tvAlterarFoto = view.findViewById(R.id.tv_alterar_foto);
        btnSalvarFoto = view.findViewById(R.id.btn_salvar_foto);
        Button btnSairDaConta = view.findViewById(R.id.btn_sair_conta);

        //Titulo e Botao para voltar

        btnFotoPerfil.setAlpha(0);
        final long ONE_MEGABYTE = 1024 * 1024;
      try {
          ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
              if(bytes.length!=0){
                  Glide.with(imgVfoto.getContext())
                          .load(bytes)
                          .into(imgVfoto);
              }
          });
      }catch (Exception ignored){

      }


        tvAlterarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,0);
        });

        btnSalvarFoto.setOnClickListener(v -> salvarFoto());

        btnSairDaConta.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(user.getUid());
            Intent intent = new Intent(getActivity(), ActivityMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });

        //botao para escolher foto de perfil
        btnFotoPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,0);
        });

        btnAlterarNome.setOnClickListener(v -> criarDialogMudarNome());

        btnAlterarPassword.setOnClickListener(v ->
            Navigation.findNavController(getView()).navigate(R.id.action_definicoesContaFragment_to_mudarPasswordFragment));

        if (!isUser){
            btnDiasIndisponiveis.setVisibility(View.VISIBLE);
            btnDiasIndisponiveis.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_fragmentDefinicoesConta_to_fragmentDiasIndisponiveis));
        }
        return view;
    }

    private void salvarFoto(){
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fSelecteduri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                byte[] data = baos.toByteArray();
                ref.putBytes(data).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        btnSalvarFoto.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),"Fotografia salva com sucesso", Toast.LENGTH_LONG).show();
                    }
                });
            }catch (Exception ignored){
            }
        }).start();
    }

    public void criarDialogMudarNome(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Altere o seu nome");
        final EditText input = new EditText(getActivity());
        input.setText(user.getDisplayName());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setPositiveButton("Adicionar", (dialog, whichButton) -> {
            progressBar.setVisibility(View.VISIBLE);

            new Thread(()->
            {
                novoNome = input.getText().toString();
                UserProfileChangeRequest.Builder builder = new UserProfileChangeRequest.Builder();
                builder.setDisplayName(novoNome);
                final UserProfileChangeRequest changeRequest = builder.build();
                user.updateProfile(changeRequest);

                FirebaseFirestore.getInstance().collection("pessoas").document(user.getUid()).update("nome",novoNome);

                CollectionReference CC = FirebaseFirestore.getInstance().collection("comentarios");

                CC.whereEqualTo("uidUsuario",user.getUid()).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult()!=null){
                        for (DocumentSnapshot ds :task.getResult()) {
                            ds.getReference().update("nomeComentador", novoNome);
                        }
                        Toast.makeText(getActivity(), "Nome salvo com sucesso", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }).start();

        }).setNegativeButton("Cancelar", (dialog, which) -> {
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==0) {
            if (data != null) {
                fSelecteduri = data.getData();
                if(fSelecteduri!=null){
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fSelecteduri);
                        imgVfoto.setImageBitmap(bitmap);
                        btnFotoPerfil.setAlpha(0);
                        btnSalvarFoto.setVisibility(View.VISIBLE);
                        btnSalvarFoto.setClickable(true);
                    } catch (IOException ignored) {

                    }
                }
            }
        }
    }

}
