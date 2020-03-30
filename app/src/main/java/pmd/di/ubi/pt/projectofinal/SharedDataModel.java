package pmd.di.ubi.pt.projectofinal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class SharedDataModel extends ViewModel {

    MutableLiveData<Boolean> user;
    MutableLiveData<Boolean> atualizarFragmentMarcaoes;



    public void init(){
        user = new MutableLiveData<>();
        atualizarFragmentMarcaoes = new MutableLiveData<>();
        atualizarFragmentMarcaoes.setValue(false);
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
