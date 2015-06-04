package es.uca.iw.hoteloasis.web;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.uca.iw.hoteloasis.domain.Categoria;
import es.uca.iw.hoteloasis.domain.Habitacion_tipo;
import es.uca.iw.hoteloasis.domain.Hotel;
import es.uca.iw.hoteloasis.domain.Reserva;
import es.uca.iw.hoteloasis.domain.Rol;
import es.uca.iw.hoteloasis.domain.Usuario;

@RequestMapping("/reservas")
@Controller
@RooWebScaffold(path = "reservas", formBackingObject = Reserva.class)
public class ReservaController {

    //MOSTRAR FORMULARIO DE BUSQUEDA DE HABITACIONES DISPONIBLES
    @RequestMapping(params = { "find=HabitacionesDisponibles", "form" }, method = RequestMethod.GET)
    public String findHabitacionesDisponiblesForm(Model uiModel) {
        uiModel.addAttribute("hotels", Hotel.findAllHotels());
        uiModel.addAttribute("categorias", Categoria.findAllCategorias());
        uiModel.addAttribute("tipos", java.util.Arrays.asList(Habitacion_tipo.class.getEnumConstants()));
        uiModel.addAttribute("formato_fecha", "dd-MMM-yyyy");
        return "reservas/findHabitacionesDisponibles";
    }

    //MOSTRAR RESULTADOS DE LA BUSQUEDA DE HABITACIONES DISPONIBLES
    @RequestMapping(params = "find=HabitacionesDisponibles", method = RequestMethod.GET)
    public String findHabitacionesDisponibles(@RequestParam("hotel") Hotel hotel, @RequestParam("categoria") Categoria categoria, @RequestParam("tipo") Habitacion_tipo tipo, @RequestParam("fecha_entrada") @DateTimeFormat(style = "M-") Date fecha_entrada, @RequestParam("fecha_salida") @DateTimeFormat(style = "M-") Date fecha_salida, @RequestParam(value = "cama_supletoria", required = false) Boolean cama_supletoria, Model uiModel) {
        uiModel.addAttribute("hotels", Hotel.findAllHotels());
        uiModel.addAttribute("categorias", Categoria.findAllCategorias());
        uiModel.addAttribute("tipos", java.util.Arrays.asList(Habitacion_tipo.class.getEnumConstants()));
        uiModel.addAttribute("formato_fecha", "dd-MMM-yyyy");
        Date hoy = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(hoy);
        c.add(Calendar.DATE, -1);
        hoy = c.getTime();
        if (fecha_entrada.equals(fecha_salida)) {
            uiModel.addAttribute("error", "Error: La fechas de entrada y salida no pueden ser las mismas.");
            return "reservas/findHabitacionesDisponibles";
        } else if (fecha_entrada.before(hoy)) {
            uiModel.addAttribute("error", "Error: La fecha de entrada no puede ser anterior a la fecha actual.");
            return "reservas/findHabitacionesDisponibles";
        } else if (fecha_salida.before(hoy)) {
            uiModel.addAttribute("error", "Error: La fecha de salida no puede ser anterior a la fecha actual.");
            return "reservas/findHabitacionesDisponibles";
        } else if (fecha_salida.before(fecha_entrada)) {
            uiModel.addAttribute("error", "Error: La fecha de entrada no puede ser posterior a la de salida.");
            return "reservas/findHabitacionesDisponibles";
        } else if (Days.daysBetween(new LocalDate(fecha_entrada), new LocalDate(fecha_salida)).getDays() > hotel.getDias_maximos()) {
            uiModel.addAttribute("error", "Error: No está permitido hacer reservas de más de " + hotel.getDias_maximos() + " días.");
            return "reservas/findHabitacionesDisponibles";
        } else if (Days.daysBetween(new LocalDate(hoy), new LocalDate(fecha_entrada)).getDays() > 60) {
            uiModel.addAttribute("error", "Error: No se puede realizar una reserva con un adelanto superior a 2 meses.");
            return "reservas/findHabitacionesDisponibles";
        }
        //LLAMADA AL BUSCADOR DE HABITACIONES DISPONIBLES
        Long habitacionesDisponibles = Reserva.findHabitacionesDisponibles(hotel, categoria, tipo, fecha_entrada, fecha_salida);
        String mensaje;
        Boolean precioSimpleporDoble = false;
        if (habitacionesDisponibles <= 0) {
            if (tipo.name().equals("SIMPLE")) {
                tipo = Habitacion_tipo.DOBLE;
                habitacionesDisponibles = Reserva.findHabitacionesDisponibles(hotel, categoria, tipo, fecha_entrada, fecha_salida);
                if (habitacionesDisponibles <= 0) {
                    mensaje = "No hay habitaciones SIMPLES (ni DOBLES) disponibles con las siguientes características:";
                    tipo = Habitacion_tipo.SIMPLE;
                } else {
                    precioSimpleporDoble = true;
                    if (habitacionesDisponibles == 1) mensaje = "No hay habitaciones SIMPLES disponibles pero hay " + habitacionesDisponibles + " habitación DOBLE disponible con las siguientes características:  <p>  (Se le cobrará el precio de una SIMPLE) </p>"; else mensaje = "No hay habitaciones SIMPLES disponibles pero hay " + habitacionesDisponibles + " habitaciones DOBLES disponibles con las siguientes características: <p>   (Se le cobrará el precio de una SIMPLE) </p>";
                }
            } else mensaje = "No hay habitaciones disponibles con las siguientes características:";
        } else {
            if (habitacionesDisponibles == 1) mensaje = "Hay " + habitacionesDisponibles + " habitación disponible con las siguientes características:"; else mensaje = "Hay " + habitacionesDisponibles + " habitaciones disponibles con las siguientes características:";
        }
        //CALCULO DEL COSTE DE ALOJAMIENTO
        int dias = Days.daysBetween(new LocalDate(fecha_entrada), new LocalDate(fecha_salida)).getDays();
        double coste_habitacion = 0, coste_cama_supletoria = 0, coste_total = 0;
        if (tipo.name().equals("SIMPLE") || precioSimpleporDoble) {
            coste_habitacion = hotel.getPrecio_hab_simple();
            coste_total = dias * (coste_habitacion * categoria.getPrecio_categoria());
        } else {
            coste_habitacion = hotel.getPrecio_hab_doble();
            coste_total = dias * (coste_habitacion * categoria.getPrecio_categoria());
        }
        if (cama_supletoria != null) {
            cama_supletoria = true;
            coste_cama_supletoria = hotel.getPrecio_cama_sup();
            coste_total += (coste_cama_supletoria * dias);
        }

        //PASAR PARÁMETROS A LA VISTA
       
        uiModel.addAttribute("formato_fecha", "dd-MMM-yyyy");
        uiModel.addAttribute("hotel", hotel);
        uiModel.addAttribute("categoria", categoria);
        uiModel.addAttribute("tipo", tipo);
        uiModel.addAttribute("fecha_entrada", new SimpleDateFormat("dd-MMM-yyyy").format(fecha_entrada));
	    uiModel.addAttribute("fecha_salida", new SimpleDateFormat("dd-MMM-yyyy").format(fecha_salida));
        uiModel.addAttribute("cama_supletoria", cama_supletoria);
        uiModel.addAttribute("coste_habitacion", coste_habitacion);
        uiModel.addAttribute("coste_cama_supletoria", coste_cama_supletoria);
        uiModel.addAttribute("habitacionesDisponibles", habitacionesDisponibles);
        uiModel.addAttribute("coste_total", coste_total);
        uiModel.addAttribute("dias", dias);
        uiModel.addAttribute("mensaje", mensaje);
        return "reservas/detallesHabitacionDisponible";
    }
    
    @RequestMapping(params = {"completar"}, method = RequestMethod.POST, produces = "text/html")
    public String reservarHabitacion(@RequestParam("hotel") Hotel hotel, @RequestParam("categoria") Categoria categoria, @RequestParam("tipo") Habitacion_tipo tipo, @RequestParam("fecha_entrada") @DateTimeFormat(style = "M-") Date fecha_entrada, @RequestParam("fecha_salida") @DateTimeFormat(style = "M-") Date fecha_salida, @RequestParam(value = "cama_supletoria", required = false) Boolean cama_supletoria, @RequestParam("coste_total") double coste_total, HttpServletRequest httpServletRequest, Model uiModel) {

	    uiModel.addAttribute("hotel", hotel);
	    uiModel.addAttribute("categoria", categoria);
	    uiModel.addAttribute("tipo", tipo);
	    uiModel.addAttribute("coste_total", coste_total);
	    uiModel.addAttribute("cama_supletoria", cama_supletoria);
	    
	    uiModel.addAttribute("fecha_entrada", new SimpleDateFormat("dd-MMM-yyyy").format(fecha_entrada));
	    uiModel.addAttribute("fecha_salida", new SimpleDateFormat("dd-MMM-yyyy").format(fecha_salida));
	    
    	Principal usuario = SecurityContextHolder.getContext().getAuthentication();
        Usuario u = Usuario.findUsuariosByNombreUsuarioEquals(usuario.getName());
         
        if (u != null) {
        	Rol rol = u.getRol();
         
 	        if (rol.getNombre().equals("Administrador") || rol.equals("Recepcionista")){
 	        	uiModel.addAttribute("staff", usuario);
 	        	uiModel.addAttribute("usuarios", Usuario.findAllUsuarios());
 	        }

 	        else if (rol.getNombre().equals("Usuario")){
 	        	uiModel.addAttribute("usuario", u);
 	        }
         }
         else{
        	 uiModel.addAttribute("visitante", "visitante");
         }
        
        return "reservas/reservarHabitacion";
    }
    
    @RequestMapping(params = {"confirmar"}, method = RequestMethod.POST, produces = "text/html")
    public String confirmarReserva(@RequestParam("hotel") Hotel hotel, @RequestParam("categoria") Categoria categoria, @RequestParam("tipo") Habitacion_tipo tipo, @RequestParam("fecha_entrada") @DateTimeFormat(style = "M-") Date fecha_entrada, @RequestParam("fecha_salida") @DateTimeFormat(style = "M-") Date fecha_salida, @RequestParam(value = "cama_supletoria", required = false) Boolean cama_supletoria, @RequestParam("coste_total") double coste_total, HttpServletRequest httpServletRequest, Model uiModel) {

    	String id_usuario = httpServletRequest.getParameter("usuario");
         	
        if(id_usuario.equals("visitante")){
         	String nombre = httpServletRequest.getParameter("nombre");
        	String primer_apellido = httpServletRequest.getParameter("primer_apellido");
        	String segundo_apellido = httpServletRequest.getParameter("segundo_apellido");
        	String email = httpServletRequest.getParameter("email");
        	String nombre_usuario = httpServletRequest.getParameter("nombre_usuario");
        	String clave = httpServletRequest.getParameter("clave");
        		
        	Rol rol = Rol.findRolsByNombreEquals("Usuario").getSingleResult();
        	
        	Usuario us = new Usuario();
            us.setNombre(nombre);
            us.setPrimer_apellido(primer_apellido);
            us.setSegundo_apellido(segundo_apellido);
            us.setEmail(email);
            us.setNombre_usuario(nombre_usuario);
            us.setClave(clave);
            us.setEnabled(true);
            us.setRol(rol);
                
            us.persist();
                
            Reserva reserva = new Reserva();
            reserva.setHotel(hotel);
            reserva.setCategoria(categoria);
            reserva.setTipo(tipo);
            reserva.setFecha_entrada(fecha_entrada);
            reserva.setFecha_salida(fecha_salida);
            reserva.setFecha_reserva(new Date());
            reserva.setCama_supletoria(cama_supletoria);
            reserva.setCoste_total(coste_total);
            reserva.setUsuario(us);
            reserva.persist();
         }
        
    	 else{
    		Usuario usuario = null;
            usuario = Usuario.findUsuario(Long.parseLong(id_usuario));
            
            Reserva reserva = new Reserva();
            reserva.setHotel(hotel);
            reserva.setCategoria(categoria);
            reserva.setTipo(tipo);
            reserva.setFecha_entrada(fecha_entrada);
            reserva.setFecha_salida(fecha_salida);
            reserva.setFecha_reserva(new Date());
            reserva.setCama_supletoria(cama_supletoria);
            reserva.setCoste_total(coste_total);
            reserva.setUsuario(usuario);
            reserva.persist();
    	 }
        	 return "reservas/exitoReserva";
    }
}