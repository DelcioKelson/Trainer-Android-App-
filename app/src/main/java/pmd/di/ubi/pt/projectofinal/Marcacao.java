package pmd.di.ubi.pt.projectofinal;

public class Marcacao {


    private String diaMarcacao;
    private String diaTreino;
    private String estado;
    private String horaTreino;
    private String personalUuid;
    private Float preco;
    private String tempoDemora;
    private String usuarioUuid;
    private String horaMarcacao;
    private String marcacaoID;

    public Marcacao() {
    }


    public Marcacao(String diaMarcacao, String diaTreino, String estado, String horaTreino, String personalUuid, float preco, String tempoDemora, String usuarioUuid, String horaMarcacao, String marcacaoID) {
        this.diaMarcacao = diaMarcacao;
        this.diaTreino = diaTreino;
        this.estado = estado;
        this.horaTreino = horaTreino;
        this.personalUuid = personalUuid;
        this.preco = preco;
        this.tempoDemora = tempoDemora;
        this.usuarioUuid = usuarioUuid;
        this.horaMarcacao = horaMarcacao;
        this.marcacaoID = marcacaoID;
    }

    public String getHoraMarcacao() {
        return horaMarcacao;
    }

    public void setHoraMarcacao(String horaMarcacao) {
        this.horaMarcacao = horaMarcacao;
    }

    public String getMarcacaoID() {
        return marcacaoID;
    }

    public void setMarcacaoID(String marcacaoID) {
        this.marcacaoID = marcacaoID;
    }

    public String getTempoMarcacao() {
        return horaMarcacao;
    }

    public void setTempoMarcacao(String horaMarcacao) {
        this.horaMarcacao = horaMarcacao;
    }

    public String getDiaMarcacao() {
        return diaMarcacao;
    }

    public void setDiaMarcacao(String diaMarcacao) {
        this.diaMarcacao = diaMarcacao;
    }

    public String getDiaTreino() {
        return diaTreino;
    }

    public void setDiaTreino(String diaTreino) {
        this.diaTreino = diaTreino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHoraTreino() {
        return horaTreino;
    }

    public void setHoraTreino(String horaTreino) {
        this.horaTreino = horaTreino;
    }

    public String getPersonalUuid() {
        return personalUuid;
    }

    public void setPersonalUuid(String personalUuid) {
        this.personalUuid = personalUuid;
    }

    public Float getPreco() {
        return preco;
    }

    public void setPreco(Float preco) {
        this.preco = preco;
    }

    public String getTempoDemora() {
        return tempoDemora;
    }

    public void setTempoDemora(String tempoDemora) {
        this.tempoDemora = tempoDemora;
    }

    public String getUsuarioUuid() {
        return usuarioUuid;
    }

    public void setUsuarioUuid(String usuarioUuid) {
        this.usuarioUuid = usuarioUuid;
    }
}
