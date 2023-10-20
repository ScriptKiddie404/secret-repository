package com.fernando.puentes.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fernando.puentes.app.exceptions.NombreInvalidoException;
import com.fernando.puentes.app.model.Alfabeto;
import com.fernando.puentes.app.model.Persona;
import com.fernando.puentes.app.model.Resultado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AlldatumService {
    private final Logger LOG = LoggerFactory.getLogger(AlldatumService.class);

    @Value("${get.api.url}")
    private String getApiUrl;

    @Value("${get.api.token}")
    private String getToken;

    @Value("${post.api.url}")
    private String postApiUrl;

    @Value("${post.api.token}")
    private String postToken;

    @Value("${archivoParamName}")
    private String archivoParamName;

    @Value("${archivoParamValue}")
    private String archivoParamValue;

    @Value("${extensionParamName}")
    private String extensionParamName;

    @Value("${extensionParamValue}")
    private String extensionParamValue;

    @Value("${nameParamName}")
    private String miNombreParamName;

    @Value("${nameParamValue}")
    private String miNombreParamValue;

    @Value("${pruebaParamName}")
    private String pruebaParamName;

    @Value("${pruebaParamValue}")
    private String pruebaParamValue;
    private final RestTemplate restTemplate;
    HttpHeaders headers;
    MultiValueMap<String, String> queryParams;
    HttpEntity<String> entity;
    List<Persona> personas;
    Long resultado;

    @Autowired
    public AlldatumService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.headers = new HttpHeaders();
        this.queryParams = new LinkedMultiValueMap<>();
    }

    /**
     * Método encargado de hacer una petición GET al servicio de Alldatum
     */
    public void obtenerYMostrarPersonas() {

        setQueryAndParams();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getApiUrl).queryParams(queryParams);

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

//      Convertimos JSON a List<Personas>:
        List<Persona> personas = mapearRespuesta(responseBody);
//      Crear validaciones necesarias:
        validarPersonas(personas);
//      Hacemos un sort alfabético. Complejidad O (n log n):
        personas.sort(Comparator.comparing(Persona::getName));

//      Log info debugging and tracking
        LOG.info("Respuesta ordenada: {}", personas);
        LOG.info("Total de personas: {}", personas.size());

        resultado = obtenerSuma(personas);

        LOG.info("Resultado: {}",  resultado);

        mandarResultados();

    }

    public void mandarResultados() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        try {

            String payload = objectWriter.writeValueAsString(new Resultado(resultado));
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(postApiUrl)
                    .queryParam(archivoParamName, archivoParamValue)
                    .queryParam(extensionParamName, extensionParamValue)
                    .queryParam(miNombreParamName, miNombreParamValue)
                    .queryParam(pruebaParamName, pruebaParamValue);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + postToken);
            HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

            LOG.info("Payload: {}", payload);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    builder.build().toUri(),
                    requestEntity,
                    String.class
            );

            LOG.info("Respuesta del servidor: {}", response.getBody());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void validarPersonas(List<Persona> personas) {

        for (Persona persona : personas) {
            String nombre = persona.getName();

            // 1. Convertir a mayúsculas todos los nombres (se asume que puede existir el caso).
            String nombreMayusculas = nombre.toUpperCase();
            persona.setName(nombreMayusculas);

            // 2. Verificar que tengan caracteres válidos (solo letras del alfabeto inglés)
            if (!nombre.matches("^[A-Z]+$")) {
                throw new NombreInvalidoException("El nombre contiene caracteres no válidos.");
            }

            // 3. Verificar que no haya nombres vacíos
            if (nombre.isEmpty()) {
                throw new NombreInvalidoException("El nombre no puede estar vacío.");
            }

            // 4. Verificar que no haya espacios en blanco al principio o al final
            if (nombre.trim().length() != nombre.length()) {
                throw new NombreInvalidoException("El nombre no puede contener espacios en blanco al principio o al final.");
            }
        }

    }

    private List<Persona> mapearRespuesta(String responseBody) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {

            List<Persona> personas = objectMapper.readValue(responseBody, new TypeReference<>() {
            });
            LOG.info("Respuesta API: " + personas);
            LOG.info("Registros totales: " + personas.size());
            return personas;

        } catch (IOException ex) {
            LOG.error("Error al momento de deserializar el JSON", ex);
        }

        return new ArrayList<>();
    }

    private void setQueryAndParams() {

        headers.set("Authorization", "Bearer " + getToken);
        queryParams.clear();
        queryParams.add(archivoParamName, archivoParamValue);
        queryParams.add(extensionParamName, extensionParamValue);
        entity = new HttpEntity<>(headers);

    }


    private long obtenerSuma(List<Persona> personas) {

        long suma = 0;

        for (int i = 0; i < personas.size(); i++) {
            suma += obtenerValorNumericoNombre(personas.get(i), i);
        }

        return suma;
    }

    private int obtenerValorNumericoNombre(Persona persona, int position) {
        int valor = 0;
        for (char c : persona.getName().toCharArray()) {
            valor += Alfabeto.obtenerValorNumerico(c);
        }

        return valor * (position + 1);
    }

}
