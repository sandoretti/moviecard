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
    private final String MOVIES_ENDPOINT = "/movies";

    @Value("${moviecards.url}")
    private String url;

    public MovieServiceImpl(RestTemplate template) {
        this.template = template;
    }


    @Override
    public List<Movie> getAllMovies() {
        Movie[] movies = template.getForObject(url + MOVIES_ENDPOINT, Movie[].class);
        return Arrays.asList(movies);
    }

    @Override
    public Movie save(Movie movie) {
        if (movie.getId() != null && movie.getId() > 0) {
            template.put(url + MOVIES_ENDPOINT, movie);
        } else {
            movie.setId(0);
            template.postForObject(url + MOVIES_ENDPOINT, movie, String.class);
        }
        return movie;
    }

    @Override
    public Movie getMovieById(Integer movieId) {
        return template.getForObject(url + MOVIES_ENDPOINT + "/" + movieId, Movie.class);
    }
}
