package es.uca.iw.hoteloasis.web;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.uca.iw.hoteloasis.domain.Categoria;
import es.uca.iw.hoteloasis.domain.Estancia;
import es.uca.iw.hoteloasis.domain.Habitacion;
import es.uca.iw.hoteloasis.domain.Habitacion_estado;
import es.uca.iw.hoteloasis.domain.Habitacion_tipo;
import es.uca.iw.hoteloasis.domain.Reserva;
import es.uca.iw.hoteloasis.domain.Rol;
import es.uca.iw.hoteloasis.domain.Usuario;

@RequestMapping("/estancias")
@Controller
@RooWebScaffold(path = "estancias", formBackingObject = Estancia.class)
public class EstanciaController {
	
	@RequestMapping(params = {"find=checkin", "form"}, method = RequestMethod.GET)
	public String findcheckin(Model uiModel, Principal principal) {
		return "estancias/checkin";
	}
	
	@RequestMapping(params = { "checkin", "id" }, method = RequestMethod.GET)
	public String checkin(Model uiModel, Principal principal,
			@RequestParam("id") Long id) {

		Reserva reserva = Reserva.findReserva(id);

		//comprobar que la reserva existe
		if(reserva==null)
		{
			uiModel.addAttribute("error", "El numero de reserva es incorrecto");
			return "estancias/checkin";
		}
		
		Categoria categoria = reserva.getCategoria();
		Habitacion_estado estado = Habitacion_estado.LIBRE;
		Habitacion_tipo tipo = reserva.getTipo();
		Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal.getName());
		List<Habitacion> habitaciones = Habitacion.findHabitacionsByTipoAndCategoriaAndEstado(tipo, categoria, estado).getResultList();
		
		//comprobar que la reserva pertenece al usuario logueado en ese momento
		if (usuario.getRol()==(Rol.findRol(3l))) {
			if (!reserva.getUsuario().equals(usuario)) {
				uiModel.addAttribute("error", "Este número de reserva no le pertenece");
				return "estancias/checkin"; 
			}
		}
		
		// Se intenta hacer el checkin de nuevo
		if (Estancia.countFindEstanciasByReserva(reserva)!=0) {
			uiModel.addAttribute("error", "El check-in de esta reserva ya ha sido realizado");
			return "estancias/checkin"; 
		}
		//compruebo que se hace el chekin el día de la reserva
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -1);
		if (reserva.getFecha_entrada().after(new Date()) || reserva.getFecha_entrada().before(c.getTime())) {
			uiModel.addAttribute("error", "Su reserva no es para hoy");
			return "estancias/checkin"; 
		}
		//compruebo que hay habitaciones libres
		if(habitaciones.isEmpty()){
			uiModel.addAttribute("error", "No hay habitaciones libres");
			return "estancias/checkin";
		}
		
		Habitacion habitacion = habitaciones.get(0);
		Estancia estancia = new Estancia(reserva, habitacion, usuario);
		estancia.setFecha_check_in(new Date());
		estancia.persist();
		habitacion.setEstado(Habitacion_estado.OCUPADA);
		habitacion.merge();
		uiModel.addAttribute("habitacion", habitacion);
		
		return "estancias/exitoCheckin";
	}
}
