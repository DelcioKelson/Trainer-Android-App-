package pmd.di.ubi.pt.projectofinal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.wallet.PaymentsClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SharedDataModel extends ViewModel {

    private MutableLiveData<Boolean> user;
    private MutableLiveData<Boolean> atualizar;
    private MutableLiveData<Boolean> fecharViewPager= new MutableLiveData<>();
    private MutableLiveData<Map<String,Object>> usuarioAtual = new MutableLiveData<>();

    private MutableLiveData<Map<String,String>> personalIdMarcacao = new MutableLiveData<>();


    private  MutableLiveData<ArrayList<Map<String,Object>>> personalList = new MutableLiveData<>();
    private  MutableLiveData<ArrayList<Map<String,Object>>> personalListFavorito = new MutableLiveData<>();


    private  MutableLiveData<ArrayList<Map<String,Object>>> marcacoesList = new MutableLiveData<>();
    private MutableLiveData<PaymentsClient> paymentsClient = new MutableLiveData<>();


    public void init(){
        user = new MutableLiveData<>();
        atualizar = new MutableLiveData<>();
        atualizar.setValue(false);
        fecharViewPager.setValue(false);
    }

    public MutableLiveData<Map<String, String>> getPersonalIdMarcacao() {
        return personalIdMarcacao;
    }

    public void setPersonalIdMarcacao(String idPersonal, String idMarcacao) {
        Map<String, String> aux = new HashMap<>();
        aux.put("idpersonal",idPersonal);
        aux.put("idmarcacao",idMarcacao);
        this.personalIdMarcacao.setValue(aux);
    }

    public MutableLiveData<Boolean> getFecharViewPager() {
        return fecharViewPager;
    }

    public void setFecharViewPager(Boolean fechar) {
        this.fecharViewPager.setValue(fechar);
    }

    public MutableLiveData<PaymentsClient> getPaymentsClient() {
        return paymentsClient;
    }

    public void setPaymentsClient(PaymentsClient paymentsClient) {
        this.paymentsClient.setValue(paymentsClient);
    }

    public void addPersonalList(ArrayList<Map<String,Object>> personalList){
        this.personalList.setValue(personalList);
    }

    public LiveData<ArrayList<Map<String, Object>>> getPersonalList() {
        return personalList;
    }

    public void addMarcacoesList(ArrayList<Map<String,Object>> marcacoesList){
        this.marcacoesList.setValue(marcacoesList);

    }

    public LiveData<ArrayList<Map<String, Object>>> getMarcacoesList() {
        return marcacoesList;
    }


    public MutableLiveData<Boolean> isUser() {
        return user;
    }

    public void usuario() {
        user.setValue(true);
    }

    public void personal(){
        user.setValue(false);
    }

    public MutableLiveData<Boolean> getAtualizar() {
        return atualizar;
    }

    public void setAtualizar(Boolean valor) {
        this.atualizar.setValue(valor);
    }

    public MutableLiveData<ArrayList<Map<String, Object>>> getPersonalListFavorito() {
        return personalListFavorito;
    }

    public void setPersonalListFavorito(ArrayList<Map<String, Object>>  personalListFavorito) {
        this.personalListFavorito.setValue(personalListFavorito);
    }

    public MutableLiveData<Map<String, Object>> getUsuarioAtual() {
        return usuarioAtual;
    }

    public void setUsuarioAtual(Map<String, Object> usuarioAtual) {
        this.usuarioAtual.setValue(usuarioAtual);
    }


}
