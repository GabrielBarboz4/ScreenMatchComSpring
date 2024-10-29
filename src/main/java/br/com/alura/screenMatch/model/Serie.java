package br.com.alura.screenMatch.model;

import br.com.alura.screenMatch.service.ConsultaChatGPT;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity @Table(name = "series")
public class Serie {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_Serie;

    @Column(unique = true)
    private String titulo;
    private Integer totalTemporadas;
    private Double imdbrating;

    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String atores;
    private String poster;
    private String sinopse;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

    public Serie() {}

    public Serie (DadosSerie dadosSerie) {
        this.titulo = dadosSerie.title();
        this.totalTemporadas = dadosSerie.totalSeasons();
        this.imdbrating = OptionalDouble.of(dadosSerie.imdbRating()).orElse(0);
        this.genero = Categoria.fromString(dadosSerie.genre().split(",")[0].trim());
        this.atores = dadosSerie.actors();
        this.poster = dadosSerie.poster();
        this.sinopse = dadosSerie.plot();
    }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e-> e.setSerie(this));
        this.episodios = episodios;
    }

    public Long getId_Serie() {
        return id_Serie;
    }

    public void setId_Serie(Long id_Serie) {
        this.id_Serie = id_Serie;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getImdbrating() {
        return imdbrating;
    }

    public void setImdbrating(Double imdbrating) {
        this.imdbrating = imdbrating;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return "\nCategoira: " + genero +
                "\nTitulo: " + titulo +
                "\nTemporadas: " + totalTemporadas +
                "\nNota IMDb: " + imdbrating +
                "\nAtores: " + atores +
                "\nPoster: " + poster +
                "\nSinopse: " + sinopse +
                "\nEpisodios: " + episodios;
    }
}
