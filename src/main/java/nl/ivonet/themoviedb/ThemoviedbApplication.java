package nl.ivonet.themoviedb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@EnableAsync
@RestController
@SpringBootApplication
public class ThemoviedbApplication {

    private final MovieService movieService;

    public ThemoviedbApplication(final MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/find/{id}")
    public Movie findMovie(@PathVariable("id") final String movieId) {
        log.info("Hello World");
        return this.movieService.findMovie(movieId).block();
    }

    public static void main(String[] args) {
        SpringApplication.run(ThemoviedbApplication.class, args);
    }
}


@Slf4j
@Service
class MovieService {
    private final String apiUrl = "https://api.themoviedb.org/3/find/%s?api_key=b1f69dc55822a50951f22f1fc6745fa5&language=en-US&external_source=imdb_id";

    @Async
    public Mono<Movie> findMovie(final String movieId) {
        log.info("ook hallo");
        return WebClient.create(format(apiUrl, movieId))
                        .get()
                        .exchange()
                        .flatMap(clientResponse -> clientResponse.bodyToMono(Movies.class))
                        .map(movies -> movies.getMovies()
                                             .get(0));

    }


}

@Data
class Movies {
    @JsonProperty("movie_results")
    private List<Movie> movies;

}

@Data
class Movie {
    @JsonSetter("original_title")
    private String title;
    private String overview;
}