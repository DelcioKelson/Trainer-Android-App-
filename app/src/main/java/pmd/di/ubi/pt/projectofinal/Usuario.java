package pmd.di.ubi.pt.projectofinal;

public class Usuario extends Pessoa {

    private String peso;
    private String altura;
    private String doencas;


    public Usuario(){

    }

    public Usuario(String uuid, String nome, String profileUrl, String data, String peso, String altura, String doencas, String numeroTelefone) {
        this.uuid = uuid;
        this.nome = nome;
        this.profileUrl = profileUrl;
        this.data = data;
        this.peso = peso;
        this.altura = altura;
        this.doencas = doencas;
        this.numeroTelefone = numeroTelefone;
    }

    public String getDoencas() {
        return doencas;
    }

    public void setDoencas(String doencas) {
        this.doencas = doencas;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }







}
