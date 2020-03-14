package pmd.di.ubi.pt.projectofinal;

public class Comentario {
    private String comentario;
    private Float classificacao;
    private String comentadorId;
    private String comentadorNome;
    private String comentarioData;

    public Comentario() {
    }

    public Comentario(String comentario, Float classificacao, String comentadorId, String comentadorNome, String comentarioData) {
        this.comentario = comentario;
        this.classificacao = classificacao;
        this.comentadorId = comentadorId;
        this.comentadorNome = comentadorNome;
        this.comentarioData = comentarioData;
    }

    public String getComentarioData() {
        return comentarioData;
    }

    public void setComentarioData(String comentarioData) {
        this.comentarioData = comentarioData;
    }

    public String getComentadorId() {
        return comentadorId;
    }

    public void setComentadorId(String comentadorId) {
        this.comentadorId = comentadorId;
    }

    public String getComentadorNome() {
        return comentadorNome;
    }

    public void setComentadorNome(String comentadorNome) {
        this.comentadorNome = comentadorNome;
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

}
