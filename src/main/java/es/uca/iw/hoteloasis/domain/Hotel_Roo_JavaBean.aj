// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uca.iw.hoteloasis.domain;

import es.uca.iw.hoteloasis.domain.Hotel;
import es.uca.iw.hoteloasis.domain.Tarifa;

privileged aspect Hotel_Roo_JavaBean {
    
    public String Hotel.getNombre() {
        return this.nombre;
    }
    
    public void Hotel.setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String Hotel.getProvincia() {
        return this.provincia;
    }
    
    public void Hotel.setProvincia(String provincia) {
        this.provincia = provincia;
    }
    
    public String Hotel.getPoblacion() {
        return this.poblacion;
    }
    
    public void Hotel.setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }
    
    public String Hotel.getDireccion() {
        return this.direccion;
    }
    
    public void Hotel.setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String Hotel.getTelefono() {
        return this.telefono;
    }
    
    public void Hotel.setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public int Hotel.getEstrellas() {
        return this.estrellas;
    }
    
    public void Hotel.setEstrellas(int estrellas) {
        this.estrellas = estrellas;
    }
    
    public double Hotel.getPrecio_hab_simple() {
        return this.precio_hab_simple;
    }
    
    public void Hotel.setPrecio_hab_simple(double precio_hab_simple) {
        this.precio_hab_simple = precio_hab_simple;
    }
    
    public double Hotel.getPrecio_hab_doble() {
        return this.precio_hab_doble;
    }
    
    public void Hotel.setPrecio_hab_doble(double precio_hab_doble) {
        this.precio_hab_doble = precio_hab_doble;
    }
    
    public double Hotel.getPrecio_cama_sup() {
        return this.precio_cama_sup;
    }
    
    public void Hotel.setPrecio_cama_sup(double precio_cama_sup) {
        this.precio_cama_sup = precio_cama_sup;
    }
    
    public int Hotel.getDias_maximos() {
        return this.dias_maximos;
    }
    
    public void Hotel.setDias_maximos(int dias_maximos) {
        this.dias_maximos = dias_maximos;
    }
    
    public int Hotel.getDias_antelacion() {
        return this.dias_antelacion;
    }
    
    public void Hotel.setDias_antelacion(int dias_antelacion) {
        this.dias_antelacion = dias_antelacion;
    }
    
    public Tarifa Hotel.getTarifa() {
        return this.tarifa;
    }
    
    public void Hotel.setTarifa(Tarifa tarifa) {
        this.tarifa = tarifa;
    }
    
}
