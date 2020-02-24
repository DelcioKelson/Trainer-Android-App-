package pmd.di.ubi.pt.projectofinal;

import java.util.ArrayList;

public class Pessoa {
      String uuid;
      String nome;
      String profileUrl;
      String data;
      String numeroTelefone;
      String tipoDeConta;
      ArrayList<String> marcacoes;

    public String getTipoDeConta() {
        return tipoDeConta;
    }

    public void setTipoDeConta(String tipoDeConta) {
        this.tipoDeConta = tipoDeConta;
    }

    public ArrayList<String> getMarcacoes() {
        return marcacoes;
    }

    public void setMarcacoes(ArrayList<String> marcacoes) {
        this.marcacoes = marcacoes;
    }

    public void addMarcacao(String idMarcacao){
        this.marcacoes.add(idMarcacao);
    }
    public String getNumeroTelefone() {
        return numeroTelefone;
    }

    public void setNumeroTelefone(String numeroTelefone) {
        this.numeroTelefone = numeroTelefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
