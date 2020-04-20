package pmd.di.ubi.pt.projectofinal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.wallet.PaymentsClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;


public class SharedDataModel extends ViewModel {

    MutableLiveData<Boolean> user;
    MutableLiveData<Boolean> atualizarFragmentMarcaoes;
    private  MutableLiveData<ArrayList<Map<String,Object>>> personalList = new MutableLiveData<>();
    private  MutableLiveData<ArrayList<Map<String,Object>>> marcacoesList = new MutableLiveData<>();
    private MutableLiveData<PaymentsClient> paymentsClient = new MutableLiveData<>();



    public void init(){
        user = new MutableLiveData<>();
        atualizarFragmentMarcaoes = new MutableLiveData<>();
        atualizarFragmentMarcaoes.setValue(false);
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

    public MutableLiveData<Boolean> getAtualizarFragmentMarcaoes() {
        return atualizarFragmentMarcaoes;
    }

    public void setAtualizarFragmentMarcaoes(Boolean valor) {
        this.atualizarFragmentMarcaoes.setValue(valor);
    }
}
