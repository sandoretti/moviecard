package com.lauracercas.moviecards.service.actor;


import com.lauracercas.moviecards.model.Actor;
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
public class ActorServiceImpl implements ActorService {

    private final RestTemplate template;

    @Value("${moviecards.url}")
    private String url;

    public ActorServiceImpl(RestTemplate template) {
        this.template = template;
    }

    @Override
    public List<Actor> getAllActors() {
        Actor[] actores = template.getForObject(url + "/actors", Actor[].class);
        return Arrays.asList(actores);
    }

    @Override
    public Actor save(Actor actor) {
        if (actor.getId() != null && actor.getId() > 0) {
            template.put(url + "/actors", actor);
        } else {
            actor.setId(0);
            template.postForObject(url + "/actors", actor, String.class);
        }
        return actor;
    }

    @Override
    public Actor getActorById(Integer actorId) {
        return template.getForObject(url + "/actors/" + actorId, Actor.class);
    }
}
