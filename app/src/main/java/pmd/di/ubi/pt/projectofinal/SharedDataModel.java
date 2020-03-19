package pmd.di.ubi.pt.projectofinal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class SharedDataModel extends ViewModel {

    MutableLiveData<Boolean> user;

    public void init(){
        user = new MutableLiveData<>();
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
}
