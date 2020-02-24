package pmd.di.ubi.pt.projectofinal;

public class Modalidade {
    private String img;
    private String nome;

    public Modalidade() {
    }

    public Modalidade(String img, String nome) {
        this.img = img;
        this.nome = nome;
    }

    public String getImg() {
        return img;
    }

    public String getNome() {
        return nome;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
