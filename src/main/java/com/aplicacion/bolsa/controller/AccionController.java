package com.aplicacion.bolsa.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aplicacion.bolsa.model.PrecioHistorico;
import com.aplicacion.bolsa.service.AlphaVantageService;

@Controller
public class AccionController {

    @Autowired
    private AlphaVantageService servicio;

    @PostMapping("/graficar")
    public String graficar(@RequestParam String ticker, @RequestParam String periodo, Model model) {
        Map<LocalDate, Double> historico = servicio.obtenerDatosHistoricos(ticker.toUpperCase());

        // Calcular fecha de inicio según el periodo elegido
        LocalDate hoy = LocalDate.now();
        LocalDate desde;

        switch (periodo) {
            case "1m":
                desde = hoy.minusMonths(1);
                break;
            case "3m":
                desde = hoy.minusMonths(3);
                break;
            case "6m":
                desde = hoy.minusMonths(6);
                break;
            case "1a":
                desde = hoy.minusYears(1);
                break;
            case "2a":
                desde = hoy.minusYears(2);
                break;
            case "3a":
                desde = hoy.minusYears(3);
                break;
            default:
                desde = hoy.minusMonths(1); // por defecto: 1 mes
        }

        // Filtrar los datos según la fecha
        List<PrecioHistorico> datos = historico.entrySet().stream()
                .filter(e -> !e.getKey().isBefore(desde))
                .map(e -> new PrecioHistorico(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(PrecioHistorico::getFecha))
                .collect(Collectors.toList());

        List<String> fechas = datos.stream()
                .map(d -> d.getFecha().toString())
                .collect(Collectors.toList());

        List<Double> precios = datos.stream()
                .map(PrecioHistorico::getPrecio)
                .collect(Collectors.toList());

        model.addAttribute("fechas", fechas);
        model.addAttribute("precios", precios);
        model.addAttribute("ticker", ticker.toUpperCase());

        return "grafico";
    }
    @GetMapping("/grafico")
    public String verGrafico(Model model) {
        String ticker = "AAPL";
        List<PrecioHistorico> datos = servicio.obtenerPrecios(ticker);

        List<String> fechas = datos.stream()
                .map(d -> d.getFecha().toString())
                .collect(Collectors.toList());

        List<Double> precios = datos.stream()
                .map(PrecioHistorico::getPrecio)
                .collect(Collectors.toList());

        System.out.println("Fechas: " + fechas);
        System.out.println("Precios: " + precios);

        model.addAttribute("ticker", ticker);
        model.addAttribute("fechas", fechas);
        model.addAttribute("precios", precios);

        return "grafico";
    }


}
