package pmd.di.ubi.pt.projectofinal;

import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PersonalTrainer extends Pessoa implements Comparable <PersonalTrainer> {

    private String especialidade;
    private Float classificacao;
    private Float preco;

    public Float getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Float classificacao) {
        this.classificacao = classificacao;
    }

    public Float getPreco() {
        return preco;
    }

    public void setPreco(Float preco) {
        this.preco = preco;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }


    @Override
    public int compareTo(PersonalTrainer o) {
        return this.getPreco().compareTo(o.getPreco());
    }
}
