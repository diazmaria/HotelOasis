package es.uca.iw.hoteloasis.web;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
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
	
	/********************************  CHECK-IN  ********************************/
	
	@RequestMapping(params = {"find=checkin", "form"}, method = RequestMethod.GET)
	public String findcheckin(Model uiModel, Principal principal) {
		return "estancias/checkin";
	}
	
	@RequestMapping(params = { "checkin", "id" }, method = RequestMethod.GET)
	public String checkin(Model uiModel, Principal principal,
			@RequestParam("id") Long id) {

		Reserva reserva = Reserva.findReserva(id);

		//Comprobamos que la reserva existe
		if(reserva==null)
		{
			uiModel.addAttribute("error", "Error: El número de reserva es erróneo.");
			return "estancias/checkin";
		}
		
		Categoria categoria = reserva.getCategoria();
		Habitacion_estado estado = Habitacion_estado.LIBRE;
		Habitacion_tipo tipo = reserva.getTipo();
		Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal.getName());
		List<Habitacion> habitaciones = Habitacion.findHabitacionsByTipoAndCategoriaAndEstado(tipo, categoria, estado).getResultList();
		
		//Comprobamos que la reserva pertenece al usuario logueado en ese momento
		if (usuario.getRol()==(Rol.findRol(3l))) {
			if (!reserva.getUsuario().equals(usuario)) {
				uiModel.addAttribute("error", "Error: Este número de reserva no le pertenece.");
				return "estancias/checkin"; 
			}
		}
		
		//Comprobamos que la reserva no haya sido cancelada
		if(reserva.getFecha_cancelacion() != null){
			uiModel.addAttribute("error", "Error: Esta reserva ha sido cancelada, no se puede hacer check-in.");
			return "estancias/checkin";
		}
		
		//Intentamos hacer el check-in de nuevo
		if (Estancia.countFindEstanciasByReserva(reserva)!=0) {
			uiModel.addAttribute("error", "Error: El check-in de esta reserva ya ha sido realizado.");
			return "estancias/checkin"; 
		}
		//Comprobamos que se hace el check-in el día de la reserva
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -1);
		if (reserva.getFecha_entrada().after(new Date()) || reserva.getFecha_entrada().before(c.getTime())) {
			uiModel.addAttribute("error", "Error: Su reserva no es para hoy.");
			return "estancias/checkin"; 
		}
		//Comprobamos que hay habitaciones disponibles
		if(habitaciones.isEmpty()){
			uiModel.addAttribute("error", "Error: No hay habitaciones disponibles.");
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
	
	/******************************** CHECK-OUT *******************************/

	// mostrar el formulario checkout
	@RequestMapping(params = { "checkoutform" }, method = RequestMethod.GET, produces = "text/html")
	public String findcheckout(Model uiModel, Principal principal) {
		return "estancias/checkout";
	}

	// acciones checkout y mostrar factura de la estancia
	@RequestMapping(params = { "checkout" }, method = RequestMethod.POST, produces = "text/html")
	public String checkout(Model uiModel, Principal principal,
			@RequestParam("id") Long id) {
		
		Reserva reserva = Reserva.findReserva(id);
		List<Estancia> estancia = Estancia.findEstanciasByReserva(reserva).getResultList();
		Estancia est = estancia.get(0);
		
		//llamo a la función que comprueba que todo esta correcto
		comprobaciones(uiModel,principal,id,"checkout");
		
		
		// Actualizo los datos
		est.setFecha_check_out(new Date());
		est.persist();
		Habitacion habitacion = est.getHabitacion();
		habitacion.setEstado(Habitacion_estado.LIBRE);
		habitacion.merge();

		// funcion que hace las gestiones de la factura
		return facturaPrivado(uiModel, principal, id, "checkout");
	}

	/******************************** FACTURA *******************************/

	// mostrar el formulario factura
	@RequestMapping(params = { "facturaform" }, method = RequestMethod.GET, produces = "text/html")
	public String findfactura(Model uiModel, Principal principal) {
		return "estancias/factura";
	}

	// acciones factura
	@RequestMapping(params = { "factura" }, method = RequestMethod.POST, produces = "text/html")
	public String factura(Model uiModel, Principal principal,
			@RequestParam("id") Long id) {
		//llamo a la función que comprueba que todo esta correcto
		comprobaciones(uiModel,principal,id,"factura");
		// funcion que hace las gestiones de la factura
		return facturaPrivado(uiModel, principal, id, "factura");
	}

	/******************************** FUNCIONES PRIVADAS *******************************/
	/**
	 * función encargada de mostrar la vista de factura
	 * @param uiModel
	 * @param principal
	 * @param id
	 * @param origen
	 * @return
	 */
	private String facturaPrivado(Model uiModel, Principal principal, long id,
			String origen) {

		//llamo a la función que comprueba que todo esta correcto
		comprobaciones(uiModel,principal,id,"factura");
		Reserva reserva = Reserva.findReserva(id);
		List<Estancia> estancia = Estancia.findEstanciasByReserva(reserva)
				.getResultList();
		Estancia est = estancia.get(0);

		// Calculo coste de la estancia
		double total = 0;
		// la variable aux controla si el usr ha hecho el checkout diferente de
		// su fecha de salida
		int aux = 0;
		int dias=0;
		//diferencio ya que cuando hace checkout es que se va, cuando ve la factura
		//se calcula hasta el día que tiene previsto irse
		if (origen=="checkout"){
			dias = Days.daysBetween(new LocalDate(reserva.getFecha_entrada()),
					new LocalDate(est.getFecha_check_out())).getDays();
		}else{
			dias = Days.daysBetween(new LocalDate(reserva.getFecha_entrada()),
					new LocalDate(reserva.getFecha_salida())).getDays();
		}
		Categoria categoria = reserva.getCategoria();
		Habitacion habitacion = est.getHabitacion();
		Habitacion_tipo tipo = reserva.getTipo();
		String hotel = reserva.getHotel().getNombre();
		double precio_hab = 0;
		double cama = 0;
		Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal
				.getName());

		if (tipo.equals(Habitacion_tipo.SIMPLE)) {
			precio_hab = reserva.getHotel().getPrecio_hab_simple();
		} else {
			precio_hab = reserva.getHotel().getPrecio_hab_doble();
		}
		total = dias
				* (reserva.getCategoria().getPrecio_categoria() * precio_hab);
		if (reserva.getCama_supletoria()) {
			cama = (reserva.getHotel().getPrecio_cama_sup() * dias);
			total = total + cama;
		}
		// Si hacen el checkout antes de la fecha de salida, se le cobra el
		// total de días reservados
		if (total < reserva.getCoste_reserva()) {
			total = reserva.getCoste_reserva();
			aux = 1; // sale antes
		} else if (total > reserva.getCoste_reserva()) {
			reserva.setCoste_reserva(total);
			reserva.setFecha_salida(new Date());
			reserva.merge();
			aux = 2; // sale despues
		}

		// envio las variables a la vista
		uiModel.addAttribute("habitacion", habitacion);
		uiModel.addAttribute("categoria", categoria);
		uiModel.addAttribute("reserva", reserva);
		uiModel.addAttribute("usuario", usuario);
		uiModel.addAttribute("estancia", est);
		uiModel.addAttribute("tipo", tipo);
		uiModel.addAttribute("dias", dias);
		uiModel.addAttribute("precio_hab", precio_hab);
		uiModel.addAttribute("total", total);
		uiModel.addAttribute("aux", aux);
		uiModel.addAttribute("cama", cama);
		uiModel.addAttribute("hotel", hotel);
		uiModel.addAttribute("origen", origen);

		return "estancias/detallesCheckout";
	}
	/**
	 * funcion que comprueba que todo esta correcto antes de calcular la factura
	 * es necesario crear este método aqui ya que si las incluyo en facturaprivado
	 * al hacer el checkout se hacen al final, y hay que hacerlas antes de modificar
	 * la BD
	 * @param uiModel
	 * @param principal
	 * @param id
	 * @param origen
	 * @return 
	 */
	private String comprobaciones(Model uiModel, Principal principal, long id,
			String origen) {
		
		Reserva reserva = Reserva.findReserva(id);
		Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal
				.getName());
		List<Estancia> estancia = Estancia.findEstanciasByReserva(reserva)
				.getResultList();		
		Estancia est = estancia.get(0);
		
		//esta comprobación solo se hace para checkout
		if (origen=="checkout"){
			// Se intenta hacer el checkout de nuevo 
			if (est.getFecha_check_out() != null) {
				uiModel.addAttribute("error",
						"El check-out de esta reserva ya ha sido realizado");
				return ("estancias/checkout");
			}
		}
		/*****  COMPROBACIONES GENERICAS  *******/
		// comprobar que la reserva existe
		if (reserva == null) {
			uiModel.addAttribute("error", "El numero de reserva es incorrecto");
			return ("estancias/" + origen);
		}

		

		// comprobar que la reserva pertenece al usuario logueado en ese momento
		if (usuario.getRol() == (Rol.findRol(3l))) {
			if (!reserva.getUsuario().equals(usuario)) {
				uiModel.addAttribute("error",
						"Este número de reserva no le pertenece");
				return ("estancias/" + origen);
			}
		}

		// comprobar que la estancia existe
		if(estancia.equals(null)){
			uiModel.addAttribute("error",
					"No existe habitación para esta reserva");
			return ("estancias/" + origen);
		}
		return null;
	}
}
