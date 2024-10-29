package br.com.alura.screenMatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(
        @JsonAlias ("Title") String nomeEpisodio,
        @JsonAlias ("Episode") Integer episodio,
        @JsonAlias ("imdbRating") String avaliacao,
        @JsonAlias ("Released") String dataLancamento
        ) { }
