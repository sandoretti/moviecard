package com.lauracercas.moviecards.integrationtest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lauracercas.moviecards.model.Movie;
import com.lauracercas.moviecards.service.movie.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Autor: Laura Cercas Ramos
 * Proyecto: TFM Integración Continua con GitHub Actions
 * Fecha: 04/06/2024
 */
@SpringBootTest
public class MovieServiceIT {

    @Autowired
    private MovieService movieService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${moviecards.url}")
    private String moviecardsUrl;

    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetMovieById() throws Exception {
        Movie movie = new Movie();
        movie.setId(1);
        movie.setTitle("Test Movie");
        movie.setReleaseYear(2022);
        movie.setDuration(150);
        movie.setCountry("France");
        movie.setDirector("Test Director");
        movie.setGenre("Thriller");
        movie.setSinopsis("A thrilling test movie");

        mockServer.expect(requestTo(moviecardsUrl + "/movies/1"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(movie), MediaType.APPLICATION_JSON));

        Movie result = movieService.getMovieById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Movie", result.getTitle());
        assertEquals(2022, result.getReleaseYear());
        assertEquals(150, result.getDuration());
        assertEquals("France", result.getCountry());
        assertEquals("Test Director", result.getDirector());
        assertEquals("Thriller", result.getGenre());

        mockServer.verify();
    }

    @Test
    public void testSaveNewMovie() throws Exception {
        Movie newMovie = new Movie();
        newMovie.setId(0);
        newMovie.setTitle("New Movie");
        newMovie.setReleaseYear(2023);
        newMovie.setDuration(110);
        newMovie.setCountry("Italy");
        newMovie.setDirector("New Director");
        newMovie.setGenre("Comedy");
        newMovie.setSinopsis("A comedy movie");

        mockServer.expect(requestTo(moviecardsUrl + "/movies"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("Created", MediaType.TEXT_PLAIN));

        Movie result = movieService.save(newMovie);

        assertNotNull(result);
        assertEquals("New Movie", result.getTitle());
        assertEquals(2023, result.getReleaseYear());

        mockServer.verify();
    }
}