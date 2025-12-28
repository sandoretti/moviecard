package com.lauracercas.moviecards.integrationtest.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lauracercas.moviecards.model.Actor;
import com.lauracercas.moviecards.service.actor.ActorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Autor: Laura Cercas Ramos
 * Proyecto: TFM Integración Continua con GitHub Actions
 * Fecha: 04/06/2024
 */
@SpringBootTest
public class ActorServiceIT {

    @Autowired
    private ActorService actorService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${moviecards.url}")
    private String moviecardsUrl;

    private MockRestServiceServer mockServer;
    private ObjectMapper objectMapper;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    public void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        objectMapper = new ObjectMapper();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(dateFormat);
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Test
    public void testSaveActor() throws Exception {
        Actor newActor = new Actor();
        newActor.setName("New Actor");
        newActor.setCountry("Italy");
        newActor.setBirthDate(dateFormat.parse("1985-07-10"));
        newActor.setDeadDate(dateFormat.parse("1985-07-10"));

        mockServer.expect(requestTo(moviecardsUrl + "/actors"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("Created", MediaType.TEXT_PLAIN));

        Actor result = actorService.save(newActor);

        assertNotNull(result);
        assertEquals("New Actor", result.getName());

        mockServer.verify();
    }

    @Test
    public void testSaveActorWithoutDeadDate() throws Exception {
        Actor newActor = new Actor();
        newActor.setName("Living Actor");
        newActor.setCountry("Spain");
        newActor.setBirthDate(dateFormat.parse("1990-05-15"));
        newActor.setDeadDate(null);

        mockServer.expect(requestTo(moviecardsUrl + "/actors"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("Created", MediaType.TEXT_PLAIN));

        Actor result = actorService.save(newActor);

        assertNotNull(result);
        assertEquals("Living Actor", result.getName());
        assertNull(result.getDeadDate());

        mockServer.verify();
    }

    @Test
    public void testFindById() throws Exception {
        Actor actor = new Actor();
        actor.setName("Test Actor");
        actor.setCountry("France");
        actor.setBirthDate(dateFormat.parse("1975-03-22"));
        actor.setDeadDate(dateFormat.parse("2002-03-22"));

        mockServer.expect(requestTo(moviecardsUrl + "/actors/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(actor), MediaType.APPLICATION_JSON));

        Actor result = actorService.getActorById(1);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Test Actor", result.getName());
        assertEquals("France", result.getCountry());
        assertEquals(dateFormat.parse("1975-03-22"), result.getBirthDate());
        assertEquals(dateFormat.parse("2002-03-22"), result.getDeadDate());

        mockServer.verify();
    }

    @Test
    public void testFindByIdWithourDeadDate() throws Exception {
        Actor actor = new Actor();
        actor.setName("Test Actor");
        actor.setCountry("France");
        actor.setBirthDate(dateFormat.parse("1975-03-22"));
        actor.setDeadDate(null);

        mockServer.expect(requestTo(moviecardsUrl + "/actors/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(actor), MediaType.APPLICATION_JSON));

        Actor result = actorService.getActorById(1);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("Test Actor", result.getName());
        assertEquals("France", result.getCountry());
        assertEquals(dateFormat.parse("1975-03-22"), result.getBirthDate());
        assertNull(result.getDeadDate());

        mockServer.verify();
    }
}