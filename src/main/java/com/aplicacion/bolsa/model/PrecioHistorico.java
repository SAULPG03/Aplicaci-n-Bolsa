package com.aplicacion.bolsa.model;

import java.time.LocalDate;
import java.util.Date;

public class PrecioHistorico {
    private LocalDate fecha;
    private double precio;

    public PrecioHistorico(LocalDate fecha, double precio) {
        this.fecha = fecha;
        this.precio = precio;
    }

    // Getters y setters


	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

    
}

