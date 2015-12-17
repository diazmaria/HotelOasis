package es.uca.iw.hoteloasis.web;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.LocaleUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import es.uca.iw.hoteloasis.domain.Estancia;
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

    //------CU1 ------ MOSTRAR FORMULARIO BÚSQUEDA HABITACIONES DISPONIBLES
    @RequestMapping(params = { "find=HabitacionesDisponibles", "form" }, method = RequestMethod.GET)
    public String findHabitacionesDisponiblesForm(Model uiModel) {
        uiModel.addAttribute("hotels", Hotel.findAllHotels());
        uiModel.addAttribute("categorias", Categoria.findAllCategorias());
        uiModel.addAttribute("tipos", java.util.Arrays.asList(Habitacion_tipo.class.getEnumConstants()));
        uiModel.addAttribute("formato_fecha", "dd-MMM-yyyy");
        return "reservas/findHabitacionesDisponibles";
    }

    //------CU1 ------ BUSCAR HABITACIONES DISPONIBLES Y MOSTRAR LOS DETALLES
    @RequestMapping(params = "find=HabitacionesDisponibles", method = RequestMethod.GET)
    public String findHabitacionesDisponibles(@RequestParam("hotel") Hotel hotel, @RequestParam("categoria") Categoria categoria, @RequestParam("tipo") Habitacion_tipo tipo, @DateTimeFormat(pattern="dd-MMM-yyyy") @RequestParam("fecha_entrada") Date fecha_entrada, @DateTimeFormat(pattern="dd-MMM-yyyy") @RequestParam("fecha_salida") Date fecha_salida, @RequestParam(value = "cama_supletoria", required = false) Boolean cama_supletoria, Model uiModel) {
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
            uiModel.addAttribute("error", "Error: No está permitido hacer reservas de mas de");
            uiModel.addAttribute("dias_maximos", hotel.getDias_maximos());
            return "reservas/findHabitacionesDisponibles";
        } else if (Days.daysBetween(new LocalDate(hoy), new LocalDate(fecha_entrada)).getDays() > hotel.getDias_antelacion()) {
            uiModel.addAttribute("error", "Error: No se puede realizar una reserva con una antelacion superior");
            uiModel.addAttribute("dias_antelacion", hotel.getDias_antelacion());
            return "reservas/findHabitacionesDisponibles";
        }
        //Llamada al finder de habitaciones disponibles
        Long habitacionesDisponibles = Reserva.findHabitacionesDisponibles(hotel, categoria, tipo, fecha_entrada, fecha_salida);
        String mensaje;
        Boolean precioSimpleporDoble = false;
        if (habitacionesDisponibles <= 0) {
            if (tipo.name().equals("SINGLE")) {
                tipo = Habitacion_tipo.DOUBLE;
                habitacionesDisponibles = Reserva.findHabitacionesDisponibles(hotel, categoria, tipo, fecha_entrada, fecha_salida);
                if (habitacionesDisponibles <= 0) {
                    mensaje = "no_hay_ninguna";
                    tipo = Habitacion_tipo.SINGLE;
                } else {
                    precioSimpleporDoble = true;
                    if (habitacionesDisponibles == 1) mensaje = "hay_doble"; else mensaje = "hay_dobles";
                }
            } 

            else mensaje = "no_hay";
        } 
        else {
            if (habitacionesDisponibles == 1) mensaje = "hay_habitacion"; else mensaje = "hay_habitaciones";
        }
        //Cálculo del coste de alojamiento
        int dias = Days.daysBetween(new LocalDate(fecha_entrada), new LocalDate(fecha_salida)).getDays();
        double coste_habitacion = 0, coste_cama_supletoria = 0, coste_reserva = 0;
        if (tipo.name().equals("SINGLE") || precioSimpleporDoble) {
            coste_habitacion = hotel.getPrecio_hab_simple();
        } else {
            coste_habitacion = hotel.getPrecio_hab_doble();
        }
        if (cama_supletoria != null) {
            cama_supletoria = true;
            coste_cama_supletoria = hotel.getPrecio_cama_sup();
        }
        coste_reserva = (coste_habitacion + coste_cama_supletoria) * dias * categoria.getPrecio_categoria();
        double factor_categoria = categoria.getPrecio_categoria();
        //Parámetros que hay que pasar a la vista
        uiModel.addAttribute("formato_fecha", "dd-MMM-yyyy");
        uiModel.addAttribute("hotel", hotel);
        uiModel.addAttribute("categoria", categoria);
        uiModel.addAttribute("tipo", tipo);
        uiModel.addAttribute("fecha_entrada", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(fecha_entrada));
        uiModel.addAttribute("fecha_salida", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(fecha_salida));
        uiModel.addAttribute("cama_supletoria", cama_supletoria);
        uiModel.addAttribute("coste_habitacion", coste_habitacion);
        uiModel.addAttribute("coste_cama_supletoria", coste_cama_supletoria);
        uiModel.addAttribute("habitacionesDisponibles", habitacionesDisponibles);
        uiModel.addAttribute("coste_reserva", coste_reserva);
        uiModel.addAttribute("factor_categoria", factor_categoria);
        uiModel.addAttribute("dias", dias);
        uiModel.addAttribute("mensaje", mensaje);
        return "reservas/detallesHabitacionDisponible";
    }

    //------CU2 ------ RESERVAR HABITACIÓN (COMPROBAR AUTENTICACIÓN USUARIO, MOSTRAR FORMULARIO REGISGTRO SI NO AUTENTICADO)
    @RequestMapping(params = { "completar" }, method = RequestMethod.POST, produces = "text/html")
    public String reservarHabitacion(@RequestParam("hotel") Hotel hotel, @RequestParam("categoria") Categoria categoria, @RequestParam("tipo") Habitacion_tipo tipo, @RequestParam(value = "cama_supletoria", required = false) Boolean cama_supletoria, @DateTimeFormat(pattern="dd-MMM-yyyy") @RequestParam("fecha_entrada") Date fecha_entrada, @DateTimeFormat(pattern="dd-MMM-yyyy") @RequestParam("fecha_salida") Date fecha_salida, @RequestParam("coste_reserva") double coste_reserva, HttpServletRequest httpServletRequest, Model uiModel) {
    	uiModel.addAttribute("hotel", hotel);
        uiModel.addAttribute("categoria", categoria);
        uiModel.addAttribute("tipo", tipo);
        uiModel.addAttribute("coste_reserva", coste_reserva);
        uiModel.addAttribute("cama_supletoria", cama_supletoria);
        uiModel.addAttribute("fecha_entrada", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(fecha_entrada));
        uiModel.addAttribute("fecha_salida", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(fecha_salida));
        //Comprobar usuario autenticado
        Principal usuario = SecurityContextHolder.getContext().getAuthentication();
        Usuario u = Usuario.findUsuariosByNombreUsuarioEquals(usuario.getName());
        if (u != null) {
            Rol rol = u.getRol();
            if (rol.getNombre().equals("Administrador") || rol.equals("Recepcionista")) {
                uiModel.addAttribute("staff", usuario);
                uiModel.addAttribute("usuarios", Usuario.findAllUsuarios());
            } else if (rol.getNombre().equals("Usuario")) {
                uiModel.addAttribute("usuario", u);
            }
        } else {
            uiModel.addAttribute("visitante", "visitante");
        }
        return "reservas/reservarHabitacion";
    }

    //------CU2 ------ RESERVAR HABITACIÓN (COMPLETAR RESERVA, CREAR USUARIO SI EL USUARIO NO ESTABA AUTENTICADO Y MOSTRAR CÓDIGO)
    @RequestMapping(params = { "confirmar" }, method = RequestMethod.POST, produces = "text/html")
    public String confirmarReserva(@RequestParam("hotel") Hotel hotel, @RequestParam("categoria") Categoria categoria, @RequestParam("tipo") Habitacion_tipo tipo, @DateTimeFormat(pattern="dd-MMM-yyyy") @RequestParam("fecha_entrada") Date fecha_entrada, @DateTimeFormat(pattern="dd-MMM-yyyy") @RequestParam("fecha_salida") Date fecha_salida, @RequestParam(value = "cama_supletoria", required = false) Boolean cama_supletoria, @RequestParam("coste_reserva") double coste_reserva, HttpServletRequest httpServletRequest, Model uiModel) throws NoSuchAlgorithmException {
        String id_usuario = httpServletRequest.getParameter("usuario");
        //Usuario visitante realiza la reserva
        if (id_usuario.equals("visitante")) {
            String nombre = httpServletRequest.getParameter("nombre");
            String primer_apellido = httpServletRequest.getParameter("primer_apellido");
            String segundo_apellido = httpServletRequest.getParameter("segundo_apellido");
            String email = httpServletRequest.getParameter("email");
            String nombre_usuario = httpServletRequest.getParameter("nombre_usuario");
            String clave = httpServletRequest.getParameter("clave");
            Rol rol = Rol.findRolsByNombreEquals("Usuario").getSingleResult();
            Set<String> errores = new HashSet<String>();
            Pattern p = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
            Matcher m = p.matcher(email);
            //Validación del formulario de registro
            if (Usuario.findUsuariosByNombreUsuarioEquals(nombre_usuario) != null) errores.add("username_uso");
            if (nombre.length() < 2 || nombre.length() > 30 || nombre.trim().length() == 0) errores.add("tamano_nombre");
            if (primer_apellido.length() < 2 || primer_apellido.length() > 30 || primer_apellido.trim().length() == 0) errores.add("tamano_primer_apell");
            if (nombre_usuario.length() < 2 || nombre_usuario.length() > 30 || nombre_usuario.trim().length() == 0) errores.add("tamano_username");
            if (clave.length() < 2 || clave.length() > 20 || clave.trim().length() == 0) errores.add("tamano_clave");
            if (!m.matches()) errores.add("email_valido");
            if (!errores.isEmpty()) {
                uiModel.addAttribute("errores", errores);
                uiModel.addAttribute("usuario", null);
                uiModel.addAttribute("visitante", "visitante");
                uiModel.addAttribute("hotel", hotel);
                uiModel.addAttribute("categoria", categoria);
                uiModel.addAttribute("tipo", tipo);
                uiModel.addAttribute("coste_reserva", coste_reserva);
                uiModel.addAttribute("cama_supletoria", cama_supletoria);
                uiModel.addAttribute("fecha_entrada", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(fecha_entrada));
                uiModel.addAttribute("fecha_salida", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(fecha_salida));
                uiModel.addAttribute("nombre", nombre);
                uiModel.addAttribute("primer_apellido", primer_apellido);
                uiModel.addAttribute("segundo_apellido", segundo_apellido);
                uiModel.addAttribute("email", email);
                uiModel.addAttribute("nombre_usuario", nombre_usuario);
                return "reservas/reservarHabitacion";
            }
            clave = Usuario.sha256(clave);
            //Llama a los constructores de Usuario y Reserva
            Usuario us = new Usuario(nombre, primer_apellido, segundo_apellido, email, nombre_usuario, clave, true, rol);
            Reserva reserva = new Reserva(new Date(), fecha_entrada, fecha_salida, cama_supletoria, hotel, categoria, tipo, us, coste_reserva);
            //Inserción en la BD
            us.persist();
            reserva.persist();
            long codigo = reserva.getId();
            uiModel.addAttribute("codigo", codigo);
        } else {
            Usuario us = Usuario.findUsuario(Long.parseLong(id_usuario));
            Reserva reserva = new Reserva(new Date(), fecha_entrada, fecha_salida, cama_supletoria, hotel, categoria, tipo, us, coste_reserva);
            reserva.persist();
            long codigo = reserva.getId();
            uiModel.addAttribute("codigo", codigo);
        }
        return "reservas/exitoReserva";
    }

    //------CU3 ------ MOSTRAR FORMULARIO CANCELAR RESERVA
    @RequestMapping(params = { "cancelarform" }, method = RequestMethod.GET, produces = "text/html")
    public String cancelarform(HttpServletRequest httpServletRequest, Model uiModel) {
        return "reservas/cancelarReserva";
    }
    
    //------CU3 ------ CANCELAR RESERVA Y MOSTRAR ÉXITO/FRACASO CANCELACIÓN
    @RequestMapping(params = { "cancelar" }, method = { RequestMethod.GET, RequestMethod.POST }, produces = "text/html")
    public String cancelarReserva(@RequestParam(value = "id", required = false) Long id, Principal principal, HttpServletRequest httpServletRequest, Model uiModel) {
    	
    	if(httpServletRequest.getParameter("id") == null)
    		return "reservas/cancelarReserva";
    		
    	long id_reserva = Long.parseLong(httpServletRequest.getParameter("id"));
    	
        Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal.getName());
        Rol rol = usuario.getRol();
        if (Reserva.findReserva(id_reserva) == null) {
            uiModel.addAttribute("error", "reserva_erroneo");
            return "reservas/cancelarReserva";
        }
        Usuario usuario_reserva = Reserva.findReserva(id_reserva).getUsuario();
        if (Reserva.findReserva(id).getFecha_cancelacion() != null) {
            uiModel.addAttribute("error", "reserva_cancelada");
            return "reservas/cancelarReserva";
        }
        if (!usuario_reserva.equals(usuario) && rol.equals(Rol.findRolsByNombreEquals("Usuario").getSingleResult())) {
            uiModel.addAttribute("error", "reserva_no_pertenece");
            return "reservas/cancelarReserva";
        }
        if (!Estancia.findEstanciasByReserva(Reserva.findReserva(id)).getResultList().isEmpty()) {
            uiModel.addAttribute("error", "reserva_hizo_checkin");
            return "reservas/cancelarReserva";
        }
        int dias = Days.daysBetween(new LocalDate(new Date()), new LocalDate(Reserva.findReserva(Long.parseLong(httpServletRequest.getParameter("id"))).getFecha_entrada())).getDays();
        if (dias < 0) {
            uiModel.addAttribute("error", "reserva_fecha_pasada");
            return "reservas/cancelarReserva";
        }
        Reserva r = Reserva.findReserva(Long.parseLong(httpServletRequest.getParameter("id")));
        double coste_reserva = r.getCoste_reserva();
        uiModel.addAttribute("hotel", r.getHotel());
        uiModel.addAttribute("categoria", r.getCategoria());
        uiModel.addAttribute("tipo", r.getTipo());
        uiModel.addAttribute("coste_reserva", coste_reserva);
        String cama;
        if (r.getCama_supletoria() == null) cama = "No"; else cama = "Si";
        uiModel.addAttribute("cama_supletoria", cama);
        
        uiModel.addAttribute("fecha_entrada", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(r.getFecha_entrada()));
        uiModel.addAttribute("fecha_salida", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(r.getFecha_salida()));
        uiModel.addAttribute("fecha_reserva", new SimpleDateFormat("dd-MMM-yyyy", LocaleContextHolder.getLocale()).format(r.getFecha_reserva()));
        
        int compensacion;
        if (dias >= 5) compensacion = 0; else if (dias >= 2 && dias < 5) compensacion = 10; else if (dias >= 1 && dias < 2) compensacion = 30; else compensacion = 100;
        uiModel.addAttribute("id", id);
        uiModel.addAttribute("dias", dias);
        uiModel.addAttribute("compensacion", compensacion);
        double importe_total = (((double) compensacion / 100) * coste_reserva);
        uiModel.addAttribute("importe_total", importe_total);
        return "reservas/detallesCancelarReserva";
    }

    //------CU3 ------ CANCELAR RESERVA (AÑADIR FECHA DE CANCELACIÓN BD)
    @RequestMapping(params = { "cancelarReservaOk" }, method = RequestMethod.POST, produces = "text/html")
    public String cancelarReservaOk(@RequestParam("id") long id, @RequestParam("compensacion") double compensacion, HttpServletRequest httpServletRequest, Model uiModel) {
        Reserva reserva = Reserva.findReserva(id);
        reserva.setFecha_cancelacion(new Date());
        reserva.setCoste_reserva(compensacion);
        reserva.persist();
        return "reservas/exitoCancelar";
    }

    //------ EXTRA ------ LISTADO CON LAS RESERVAS DE UN USUARIO
    @RequestMapping(method = RequestMethod.GET, params = "reservasActivas", produces = "text/html")
    public String misreservas(Principal principal, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("reservas", Reserva.findReservasByUsuario(Usuario.findUsuariosByNombreUsuarioEquals(principal.getName())).getResultList());
        return "reservas/reservasUsuario";
    }
}
