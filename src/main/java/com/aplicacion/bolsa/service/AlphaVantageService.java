package com.aplicacion.bolsa.service;

import java.time.LocalDate;
import com.aplicacion.bolsa.model.PrecioHistorico;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AlphaVantageService {

    private final String API_KEY = "HC28MI4OD7H7RNGG";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<PrecioHistorico> obtenerPrecios(String symbol) {
        List<PrecioHistorico> lista = new ArrayList<>();

        try {
            String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol +
                         "&apikey=" + API_KEY + "&outputsize=compact";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            JsonNode timeSeries = root.path("Time Series (Daily)");

            if (!timeSeries.isMissingNode()) {
                Iterator<Map.Entry<String, JsonNode>> fields = timeSeries.fields();

                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    String fechaStr = entry.getKey();
                    JsonNode datosDia = entry.getValue();

                    double cierre = datosDia.get("4. close").asDouble();

                    LocalDate fecha = LocalDate.parse(fechaStr);
                    lista.add(new PrecioHistorico(fecha, cierre));

                }

                // Ordenar por fecha ascendente
                lista.sort(Comparator.comparing(PrecioHistorico::getFecha));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Map<LocalDate, Double> obtenerDatosHistoricos(String symbol) {
        Map<LocalDate, Double> historico = new TreeMap<>();

        try {
            String url = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol +
                         "&apikey=" + API_KEY + "&outputsize=full";

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            String body = response.getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            JsonNode timeSeries = root.path("Time Series (Daily)");

            if (!timeSeries.isMissingNode()) {
                Iterator<Map.Entry<String, JsonNode>> fields = timeSeries.fields();

                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    String fechaStr = entry.getKey();
                    JsonNode datosDia = entry.getValue();

                    double cierre = datosDia.get("4. close").asDouble();
                    LocalDate fecha = LocalDate.parse(fechaStr);

                    historico.put(fecha, cierre);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return historico;
    }


}
