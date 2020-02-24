package pmd.di.ubi.pt.projectofinal;

public class Comentario {
    private String comentario;
    private Float classificacao;
    private String comentador;

    public Comentario() {
    }

    public Comentario(String comentario, Float classificacao, String comentador) {
        this.comentario = comentario;
        this.classificacao = classificacao;
        this.comentador = comentador;
    }

    public Float getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(Float classificacao) {
        this.classificacao = classificacao;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }



    public String getComentador() {
        return comentador;
    }

    public void setComentador(String comentador) {
        this.comentador = comentador;
    }
}
