package br.com.alura.screenMatch.repository;

import br.com.alura.screenMatch.model.Categoria;
import br.com.alura.screenMatch.model.Episodio;
import br.com.alura.screenMatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional <Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    Optional<Serie> findByAtoresContainingIgnoreCaseAndImdbratingGreaterThanEqual(String nomeAtor, Double imdbrating);

    List<Serie> findTop5ByOrderByImdbratingDesc();


    List<Serie> findByGenero(Categoria categoria);


    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.imdbrating >= :imdbrating")
    List<Serie> seriesPorTemporadaEAvalicao(int totalTemporadas, double imdbrating);;


    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.nomeEpisodio ILIKE %:nomeDoEpisodio%")
    List<Episodio> episodiosPorTrecho(String nomeDoEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :dataLancamento")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int dataLancamento);
}
