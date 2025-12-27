package com.lauracercas.moviecards.service.movie;


import com.lauracercas.moviecards.model.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Autor: Laura Cercas Ramos
 * Proyecto: TFM Integración Continua con GitHub Actions
 * Fecha: 04/06/2024
 */
@Service
public class MovieServiceImpl implements MovieService {

    private final RestTemplate template;

    @Value("${moviecards.url}")
    private String url;

    public MovieServiceImpl(RestTemplate template) {
        this.template = template;
    }


    @Override
    public List<Movie> getAllMovies() {
        Movie[] movies = template.getForObject(url + "/movies", Movie[].class);
        return Arrays.asList(movies);
    }

    @Override
    public Movie save(Movie movie) {
        if (movie.getId() != null && movie.getId() > 0) {
            template.put(url + "/movies", movie);
        } else {
            movie.setId(0);
            template.postForObject(url + "/movies", movie, String.class);
        }
        return movie;
    }

    @Override
    public Movie getMovieById(Integer movieId) {
        Movie movie = template.getForObject(url + "/movies/" + movieId, Movie.class);
        return movie;
    }
}
