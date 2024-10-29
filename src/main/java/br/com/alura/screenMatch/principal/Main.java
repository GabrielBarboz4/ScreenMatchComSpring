package br.com.alura.screenMatch.principal;

import br.com.alura.screenMatch.model.*;
import br.com.alura.screenMatch.repository.SerieRepository;
import br.com.alura.screenMatch.service.ConsumoAPI;
import br.com.alura.screenMatch.service.ConverterDados;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final Scanner in = new Scanner(System.in);
    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConverterDados conversor = new ConverterDados();

    private final String enderecoAPI = "https://www.omdbapi.com/?t=";
    private final String privateKeyAPI = "&apikey=7e63ef72";

    private final SerieRepository repository;
    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBusca;

    public Main(SerieRepository repository) {
        this.repository = repository;
    }

    public void exibeMenu() {
        System.out.println("Bem vindo ao ScreenMatch");
        while (true) {
            var menu = """
                    1  -  Buscar séries
                    2  -  Buscar episódios
                    3  -  Listar séries
                    4  -  Buscar série por titulo
                    5  -  Buscar séries por ator
                    6  -  Buscar top 5 séries
                    7  -  Buscar séries por categoria
                    8  -  Filtrar séries
                    9  -  Buscar episódio por trecho
                    10 -  Top Episodios por série
                    11 -  Episodios a partir de uma data

                    0 - Sair""";

            System.out.println(menu);
            var opcao = in.nextInt();
            in.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    filtrarSeries();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosPorData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repository.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = in.nextLine();
        var json = consumoAPI.obterDados(enderecoAPI + nomeSerie.replace(" ", "+") + privateKeyAPI);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void listarSeriesBuscadas() {
        series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();

        System.out.println("Qual a série que você deseja buscar os episodios?");
        var nomeSerie = in.nextLine();

        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoAPI.obterDados(enderecoAPI + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + privateKeyAPI);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.temporada(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Qual a série que você deseja buscar os episodios?");
        var nomeSerie = in.nextLine();

        serieBusca = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome do Ator que você deseja buscar?");
        var nomeAtor = in.nextLine();
        System.out.println("Qual a nota média das notas que você deseja buscar?");
        var imdbrating = in.nextDouble();

        Optional<Serie> serieBuscada = repository.findByAtoresContainingIgnoreCaseAndImdbratingGreaterThanEqual(nomeAtor, imdbrating);

        if (serieBuscada.isPresent()) {
            System.out.println("Series em que " + nomeAtor + " trabalhou");
            serieBuscada.stream()
                    .forEach(serie -> System.out.println(serie.getTitulo() + " Avaliação: " + serie.getImdbrating()));
        } else {
            System.out.println("Não encontramos nenhuma série que " + nomeAtor + " autou com a nota " + imdbrating);
        }
    }

    private void buscarTop5series() {
        List<Serie> top5Series = repository.findTop5ByOrderByImdbratingDesc();
        top5Series.forEach(serie -> System.out.println(serie.getTitulo() + " Avaliação: " + serie.getImdbrating()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Qual o nome da categoria que você deseja buscar?");
        var genero = in.nextLine();

        Categoria categoria = Categoria.fromPortugues(genero);

        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        if (!seriesPorCategoria.isEmpty()) {
            System.out.println("Séries da categoria " + genero);
            seriesPorCategoria.forEach(System.out::println);
        } else {
            System.out.println("Não foram encontradas séries com esse genero");
        }
    }
    private void filtrarSeries() {

        System.out.println("Filtrar séires até quantas temporadas?");
        var totalTemporadas = in.nextInt();
        System.out.println("Qual a nota média das notas que você deseja buscar?");
        var imdbrating = in.nextDouble();
        List<Serie> seriesFiltradas = repository.seriesPorTemporadaEAvalicao(totalTemporadas, imdbrating);
        seriesFiltradas.forEach(serie -> System.out.println(serie.getTitulo() + " Avaliação> " + serie.getImdbrating()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Qual o nome do episódio que você deseja buscar?");
        var nomeDoEpisodio = in.nextLine();
        List<Episodio> episodiosEncontrados = repository.episodiosPorTrecho(nomeDoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getEpisodio(), e.getNomeEpisodio()));
    }

    private void topEpisodiosPorSerie() {
       buscarSeriePorTitulo();
       if (serieBusca.isPresent()) {
          Serie serie = serieBusca.get();
          List<Episodio> topEpisodios = repository.topEpisodiosPorSerie(serie);
          topEpisodios.forEach(episodio ->
                  System.out.printf("Série: %s Temporada %s - Episódio %s - %s  Avalicação %s\n",
                          episodio.getSerie().getTitulo(), episodio.getTemporada(),
                          episodio.getEpisodio(), episodio.getNomeEpisodio(), episodio.getAvaliacao()));
       }
    }

    private void buscarEpisodiosPorData() {
    buscarSeriePorTitulo();
    if (serieBusca.isPresent()) {
        Serie serie = serieBusca.get();
        System.out.println("Digite o ano limite de lançamento");
        var anolancamento = in.nextInt();
        in.nextLine();

        List<Episodio> episodiosAno = repository.episodiosPorSerieEAno(serie, anolancamento);
        episodiosAno.forEach(System.out::println);
    }
    }
}