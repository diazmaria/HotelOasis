package es.uca.iw.hoteloasis.web;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import es.uca.iw.hoteloasis.domain.Bebida;
import es.uca.iw.hoteloasis.domain.Bebida_consumo;
import es.uca.iw.hoteloasis.domain.Categoria;
import es.uca.iw.hoteloasis.domain.Estancia;
import es.uca.iw.hoteloasis.domain.Habitacion;
import es.uca.iw.hoteloasis.domain.Habitacion_estado;
import es.uca.iw.hoteloasis.domain.Habitacion_tipo;
import es.uca.iw.hoteloasis.domain.Hotel;
import es.uca.iw.hoteloasis.domain.Llamada;
import es.uca.iw.hoteloasis.domain.Reserva;
import es.uca.iw.hoteloasis.domain.Rol;
import es.uca.iw.hoteloasis.domain.Tarifa;
import es.uca.iw.hoteloasis.domain.Usuario;

@RequestMapping("/estancias")
@Controller
@RooWebScaffold(path = "estancias", formBackingObject = Estancia.class)
public class EstanciaController {
	
	/******************************** CHECK-IN ********************************/
	@RequestMapping(params = { "find=checkin", "form" }, method = RequestMethod.GET)
	public String findcheckin(Model uiModel, Principal principal) {
		return "estancias/checkin";
	}

	@RequestMapping(params = { "checkin", "id" }, method = RequestMethod.GET)
	public String checkin(Model uiModel, Principal principal,
			@RequestParam("id") Long id) {
		Reserva reserva = Reserva.findReserva(id);
		// Comprobamos que la reserva existe
		if (reserva == null) {
			uiModel.addAttribute("error",
					"reserva_erroneo");
			return "estancias/checkin";
		}
		Categoria categoria = reserva.getCategoria();
		
		Habitacion_estado estado = Habitacion_estado.FREE;

		Habitacion_tipo tipo = reserva.getTipo();
		Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal
				.getName());
		List<Habitacion> habitaciones = Habitacion
				.findHabitacionsByTipoAndCategoriaAndEstado(tipo, categoria,
						estado).getResultList();
		// Comprobamos que la reserva pertenece al usuario logueado en ese
		// momento
		if (usuario.getRol() == (Rol.findRol(3l))) {
			if (!reserva.getUsuario().equals(usuario)) {
				uiModel.addAttribute("error",
						"reserva_no_pertenece");
				return "estancias/checkin";
			}
		}
		// Comprobamos que la reserva no haya sido cancelada
		if (reserva.getFecha_cancelacion() != null) {
			uiModel.addAttribute("error",
					"reserva_cancelada");
			return "estancias/checkin";
		}
		// Intentamos hacer el check-in de nuevo
		if (Estancia.countFindEstanciasByReserva(reserva) != 0) {
			uiModel.addAttribute("error",
					"reserva_hizo_checkin2");
			return "estancias/checkin";
		}
		// Comprobamos que se hace el check-in el día de la reserva
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -1);
		if (reserva.getFecha_entrada().after(new Date())
				|| reserva.getFecha_entrada().before(c.getTime())) {
			uiModel.addAttribute("error", "reserva_no_hoy");
			return "estancias/checkin";
		}
		// Comprobamos que hay habitaciones disponibles
		if (habitaciones.isEmpty()) {
			uiModel.addAttribute("error",
					"no_habitaciones");
			return "estancias/checkin";
		}
		Habitacion habitacion = habitaciones.get(0);
		Estancia estancia = new Estancia(reserva, habitacion, usuario);
		estancia.setFecha_check_in(new Date());
		estancia.persist();

		habitacion.setEstado(Habitacion_estado.TAKEN);

	
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
		
		if (reserva == null) {
			uiModel.addAttribute("error",
					"reserva_erroneo");
			return "estancias/checkout";
		}
		
		Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal
				.getName());
		
		if (usuario.getRol() == (Rol.findRol(3l))) {
			if (!reserva.getUsuario().equals(usuario)) {
				uiModel.addAttribute("error",
						"reserva_no_pertenece");
				return "estancias/checkin";
			}
		}
		
		List<Estancia> estancia = Estancia.findEstanciasByReserva(reserva)
				.getResultList();
		if (estancia.isEmpty()) {
			uiModel.addAttribute("error",
					"reserva_no_checkin");
			return ("estancias/checkout");
		}
		// llamo a la función que comprueba que todo esta correcto

		Estancia est = estancia.get(0);
		// Actualizo los datos
		
		if (est.getFecha_check_out()  == null){
		
			est.setFecha_check_out(new Date());
			est.persist();
			Habitacion habitacion = est.getHabitacion();
			
			habitacion.setEstado(Habitacion_estado.FREE);

			habitacion.merge();
			
			// funcion que hace las gestiones de la factura
			return facturaPrivado(uiModel, principal, id, "checkout");
		}
		else{
			uiModel.addAttribute("error",
					"reserva_hizo_checkout");
			return ("estancias/checkout");
		}
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
		
		Reserva reserva = Reserva.findReserva(id);
		
		if (reserva == null) {
			uiModel.addAttribute("error",
					"reserva_erroneo");
			return "estancias/factura";
		}
		
		Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal
				.getName());
		
		if (usuario.getRol() == (Rol.findRol(3l))) {
			if (!reserva.getUsuario().equals(usuario)) {
				uiModel.addAttribute("error",
						"reserva_no_pertenece");
				return "estancias/factura";
			}
		}
		
		List<Estancia> estancia = Estancia.findEstanciasByReserva(reserva)
				.getResultList();
		if (estancia.isEmpty()) {
			uiModel.addAttribute("error",
					"reserva_no_checkin");
			return ("estancias/factura");
		}
		
		if (estancia.get(0).getFecha_check_out() == null) {
			uiModel.addAttribute("error",
					"reserva_no_checkout");
			return ("estancias/factura");
		}
		
		
		// funcion que hace las gestiones de la factura
		return facturaPrivado(uiModel, principal, id, "factura");
	}

	/******************************** FUNCIONES PRIVADAS FACTURA *******************************/
	/**
	 * función encargada de mostrar la vista de factura
	 * 
	 * @param uiModel
	 * @param principal
	 * @param id
	 * @param origen
	 * @return
	 */
	private String facturaPrivado(Model uiModel, Principal principal, long id,
			String origen) {
		// llamo a la función que comprueba que todo esta correcto
	
		Reserva reserva = Reserva.findReserva(id);
		List<Estancia> estancia = Estancia.findEstanciasByReserva(reserva)
				.getResultList();

		Estancia est = estancia.get(0);
		// Calculo coste de la estancia
		double total = 0;
		// la variable aux controla si el usr ha hecho el checkout diferente de
		// su fecha de salida
		int aux = 0;
		int dias = 0;
		// diferencio ya que cuando hace checkout es que se va, cuando ve la
		// factura
		// se calcula hasta el día que tiene previsto irse
		if (origen == "checkout") {
			dias = Days.daysBetween(new LocalDate(reserva.getFecha_entrada()),
					new LocalDate(est.getFecha_check_out())).getDays();
		} else {
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
		if (tipo.equals(Habitacion_tipo.SINGLE)) {
			precio_hab = reserva.getHotel().getPrecio_hab_simple();
		} else {
			precio_hab = reserva.getHotel().getPrecio_hab_doble();
		}
		total = dias * (reserva.getCategoria().getPrecio_categoria() * precio_hab);
		if (reserva.getCama_supletoria() != null) {
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
		// OBTENGO LOS DATOS DE LOS SERVICIOS
		
		double total_servicios = 0;
		
		Hotel h = est.getReserva().getHotel();
		Tarifa t = Tarifa.findTarifasByHotel(h).getSingleResult();
		
		double coste_cama = reserva.getHotel().getPrecio_cama_sup();
		double coste_noches = total;
		double total_nac = 0;
		double total_inter = 0;
		double total_internet = 0;
		int min_nac=0;
		int min_inter=0;
		int min_internet=0;
		double pre_nac=0;
		double pre_inter=0;
		double pre_internet=0;
		if (est.getLlamadas() != null) {

			if (est.getLlamadas().getMinutos_nacionales() != 0)
				min_nac=(int)est.getLlamadas().getMinutos_nacionales();
				pre_nac=t.getLlamada_nacional();
				total_nac = min_nac * pre_nac;
			if (est.getLlamadas().getMinutos_internacionales() != 0)
				min_inter=(int) est.getLlamadas().getMinutos_internacionales();
				pre_inter=t.getLlamada_internacional();
				total_inter = min_inter	* pre_inter;
			if (est.getLlamadas().getMinutos_internet() != 0)
				min_internet=(int) est.getLlamadas().getMinutos_internet();
				pre_internet=t.getInternet();
				total_internet = min_internet * pre_internet;
		}
		Map<Bebida, Integer> bar = null;
		
		total_servicios = total_nac + total_inter + total_internet;
		double total_bar = 0;
		if (!est.getBebida_consumo().isEmpty()) {
			Set<Bebida_consumo> bebidas = est.getBebida_consumo();
			bar = new HashMap<Bebida, Integer>(
					bebidas.size());

			for (Bebida_consumo consumo : bebidas) {
				Bebida bebida = consumo.getBebida();
				int cantidad = consumo.getCantidad_consumida();
				total_bar += bebida.getCoste() * cantidad;
				bar.put(bebida, cantidad);
			}

			total_servicios = total_servicios + total_bar;
		}
		total = total + total_servicios;
		//dar formato a los doubles 
		total=Math.round(total*100)/100.0d;
		total_nac=Math.round(total_nac*100)/100.0d;
		total_inter=Math.round(total_inter*100)/100.0d;
		total_internet=Math.round(total_internet*100)/100.0d;
		total_bar=Math.round(total_bar*100)/100.0d;
		total_servicios=Math.round(total_servicios*100)/100.0d;
		
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
		uiModel.addAttribute("total_nac", total_nac);
		uiModel.addAttribute("total_inter", total_inter);
		uiModel.addAttribute("total_internet", total_internet);
		uiModel.addAttribute("total_bar", total_bar);
		uiModel.addAttribute("total_servicios", total_servicios);
		uiModel.addAttribute("bar", bar);
		uiModel.addAttribute("min_nac", min_nac);
		uiModel.addAttribute("min_inter", min_inter);
		uiModel.addAttribute("min_internet", min_internet);
		uiModel.addAttribute("pre_nac", pre_nac);
		uiModel.addAttribute("pre_inter", pre_inter);
		uiModel.addAttribute("pre_internet", pre_internet);
		uiModel.addAttribute("coste_noches", coste_noches);
		uiModel.addAttribute("coste_cama", coste_cama);

		return "estancias/detallesCheckout";
	}

	/**
	 * funcion que comprueba que todo esta correcto antes de calcular la factura
	 * es necesario crear este método aqui ya que si las incluyo en
	 * facturaprivado al hacer el checkout se hacen al final, y hay que hacerlas
	 * antes de modificar la BD
	 * 
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

		// comprobar que la reserva existe
		if (reserva == null) {
			uiModel.addAttribute("error",
					"Error: El código de la reserva es erróneo.");
			return ("estancias/" + origen);
		}

		List<Estancia> estancia = Estancia.findEstanciasByReserva(reserva)
				.getResultList();

		if (estancia.isEmpty()) {
			uiModel.addAttribute("error",
					"Error: El check-in aún no se ha realizado.");
			return ("estancias/checkout");
		}
		// comprobar que la estancia existe
		if (estancia.equals(null)) {
			uiModel.addAttribute("error",
					"Error: No existe habitación para esta reserva.");
			return ("estancias/" + origen);
		}

		Estancia est = estancia.get(0);
		// esta comprobación solo se hace para checkout
		if (origen == "checkout") {
			// Se intenta hacer el checkout de nuevo
			if (est.getFecha_check_out() != null) {
				uiModel.addAttribute("error",
						"Error: El check-out de esta reserva ya ha sido realizado.");
				return ("estancias/checkout");
			}

		}

		// comprobar que la reserva pertenece al usuario logueado en ese momento
		if (usuario.getRol() == (Rol.findRol(3l))) {
			if (!reserva.getUsuario().equals(usuario)) {
				uiModel.addAttribute("error",
						"Error: Este código de reserva no le pertenece.");
				return ("estancias/" + origen);
			}
		}

		return null;
	}

	// ------CU5 ------ ELEGIR HABITACIÓN PARA CONSUMO DE SERVICIO
	@RequestMapping(method = RequestMethod.GET, params = "servicios", produces = "text/html") 
	public String servicios(Principal p, Model uiModel){
		
		//Obtener estancias del usuario y almacenar en una lista de estancias
		List<Estancia> estancias = Estancia.findEstanciasByUsuario(Usuario.findUsuariosByNombreUsuarioEquals(p.getName())).getResultList();
		List<Habitacion> habitaciones = null;
		
		//Si la lista no está vacía creamos un ArrayList de habitaciones y recorremos la lista para obtener las habitaciones
		if (!estancias.isEmpty()){
			habitaciones = new ArrayList<Habitacion>(estancias.size());
			for (Estancia e : estancias){
				if(e.getFecha_check_out() == null && e.getReserva().getFecha_cancelacion() == null){
					habitaciones.add(e.getHabitacion());
					uiModel.addAttribute("reserva_id",  e.getReserva().getId());
				}
			}
		}
		uiModel.addAttribute("habitaciones", habitaciones);
		return "estancias/elegirHabitacionServicios";
	}

	// ------CU5 ------ ELEGIR SERVICIO
	@RequestMapping(method = RequestMethod.GET, params = {"servicios", "habitacion"}, produces = "text/html")
	public String elegirServicio(Principal p, @RequestParam("habitacion") Habitacion habitacion, Model uiModel){
		
		//Buscar la estancias de un usuario según Nº de habitación
		List<Estancia> estancias = Estancia.findEstanciasByUsuarioAndHabitacion(Usuario.findUsuariosByNombreUsuarioEquals(p.getName()), habitacion).getResultList();
		
		//Coger la estancia más reciente
		Estancia estancia = estancias.get(0);
		
		uiModel.addAttribute("estancia", estancia);
		return "estancias/elegirServicio";
	}

	// ------CU5 ------ MOSTRAR FORMULARIO DE INCIAR EL CONSUMO
	@RequestMapping(method = RequestMethod.GET, params = {"servicios", "estancia"}, produces = "text/html")
	public String iniciarServicio(Principal p, HttpServletRequest httpservletrequest, Model uiModel, @RequestParam("estancia") Estancia estancia){
		
		//Mostrar formulario bebidas, llamadas o internet
		Hotel hotel = estancia.getReserva().getHotel();
		Tarifa tarifa = Tarifa.findTarifasByHotel(hotel).getSingleResult();
		uiModel.addAttribute("estancia", estancia);
		
		if (httpservletrequest.getParameter("llamada_nacional") != null){
			uiModel.addAttribute("servicio", "llamada_nacional");
			uiModel.addAttribute("tarifa", tarifa.getLlamada_nacional());
			return "estancias/servicioLlamada";
			
		} else if (httpservletrequest.getParameter("llamada_internacional") != null){
			uiModel.addAttribute("servicio", "llamada_internacional");
			uiModel.addAttribute("tarifa", tarifa.getLlamada_internacional());
			return "estancias/servicioLlamada";
			
		} else if (httpservletrequest.getParameter("internet") != null){
			uiModel.addAttribute("servicio", "internet");
			uiModel.addAttribute("tarifa", tarifa.getInternet());
			return "estancias/servicioInternet";
			
		} else{
			
			popularBebidas(uiModel, estancia);
			return "estancias/servicioMinibar";
	}
	}
	
	
		// --- FUNCION MOSTRAR BEBIDAS DISPONIBLES EN MINIBAR ---
		void popularBebidas(Model uiModel, Estancia estancia) {
	
		Categoria c = estancia.getHabitacion().getCategoria();
		
		//Bebidas en el minibar de la categoría c (minibar inicial)
		List<Bebida> bebidas = Bebida.findBebidasByCategoria(c).getResultList();
		
		////Bebidas consumidas hasta ahora
		List<Bebida_consumo> bebida_consumida = null;
		
		Set<Bebida_consumo> consumo = estancia.getBebida_consumo();
		
		
		Map<Bebida, Integer> minibar_actual = new HashMap<Bebida, Integer>();
		
		int cantidad_actual;
	
		for(int i=0; i<bebidas.size(); i++){
			//Buscas si la primera bebida del minibar ya había sido consumida antes y devuelve la bebida y la cantidad
			bebida_consumida = Bebida_consumo.findBebida_consumoesByBebidaAndEstancia((bebidas.get(i)), estancia).getResultList();
			
			if(!bebida_consumida.isEmpty()){
				cantidad_actual = bebidas.get(i).getCantidad_minibar() - bebida_consumida.get(0).getCantidad_consumida();
			minibar_actual.put(bebidas.get(i), cantidad_actual);
			}
			else{
				minibar_actual.put(bebidas.get(i), bebidas.get(i).getCantidad_minibar());
			}
		}
		
		Map<Bebida, Integer> treeMap = new TreeMap<Bebida, Integer>(minibar_actual);
		
		uiModel.addAttribute("bebidas", treeMap);
	
		}
		
		//CONSUMO DE BEBIDA ANTERIOR
		
		Bebida_consumo bebidaYaConsumida(Set<Bebida_consumo> consumos, Bebida bebida){
			for (Bebida_consumo consumo : consumos)
				if (consumo.getBebida().getId() == bebida.getId())
					return consumo;
				
					return null;
		}
		

		// ------CU5 ------ CONSUMIR MINIBAR
		@RequestMapping(method = RequestMethod.POST, params = {"estancia", "cantidad"}, produces = "text/html")
		public String consumirMinibar(Principal p, Model uiModel, @RequestParam("cantidad") int cantidad, @RequestParam("estancia") Estancia estancia, HttpServletRequest httpservletrequest){
			
				uiModel.addAttribute("estancia", estancia);
				
				Bebida bebida = Bebida.findBebida(Long.parseLong(httpservletrequest.getParameter("bebida")));

				
				//Cantidad tiene que ser mayor que uno
				if (cantidad < 1){
					uiModel.addAttribute("error", "Error: Cantidad errónea.");
					popularBebidas(uiModel, estancia);
					return "estancias/servicioMinibar";
				}
				
				Set<Bebida_consumo> consumos = estancia.getBebida_consumo();
				Categoria c = estancia.getHabitacion().getCategoria();
				
				
				//Si ya ha consumido antes
				if (!consumos.isEmpty()){
					Bebida_consumo consumo = bebidaYaConsumida(consumos, bebida);
					
					if (consumo != null){
						int cantidad_actual = consumo.getCantidad_consumida() + cantidad;
						
						int total = bebida.getCantidad_minibar() - cantidad - consumo.getCantidad_consumida();
						if ( total >= 0){
							consumo.setCantidad_consumida(cantidad_actual);
							consumo.merge();
						}
						else{
							uiModel.addAttribute("error", "Error: No puede consumir más bebidas de las disponibles.");
							popularBebidas(uiModel, estancia);
							return "estancias/servicioMinibar";
						}
					 }else {
						//Consumir más de las que hay
						int cantidad_minibar = bebida.getCantidad_minibar() ;
						if ((cantidad_minibar - cantidad) < 0){
							uiModel.addAttribute("error", "Error: No puede consumir más bebidas de las disponibles.");
							popularBebidas(uiModel, estancia);
							return "estancias/servicioMinibar";
						}
						else {
							consumo = new Bebida_consumo(bebida, cantidad, estancia);
							consumos.add(consumo);
							consumo.persist();
						}
					}
				}else{
					
					if (bebida.getCantidad_minibar() - cantidad < 0){
						uiModel.addAttribute("error", "Error: No puede consumir más bebidas de las disponibles.");
						popularBebidas(uiModel, estancia);
						return "estancias/servicioMinibar";
					}
					else{
						Bebida_consumo consumo = new Bebida_consumo(bebida, cantidad, estancia);
						consumos.add(consumo);
						consumo.persist();
					}
				}
				
				uiModel.addAttribute("compra", "Compra realizada con éxito.");
				popularBebidas(uiModel, estancia);
				return "estancias/servicioMinibar";
		}
		
		
	
	// ------CU5 ------ INICIAR LLAMADA
	@RequestMapping(method = RequestMethod.POST, params = {"servicio", "estancia", "tarifa"}, produces = "text/html")
	public String realizarLlamada(@RequestParam("servicio") String servicio, @RequestParam("estancia") long estancia, @RequestParam("tarifa") double tarifa,
			HttpServletRequest hhtpservletrequest, Model uiModel){
		
		long inicio = System.currentTimeMillis();
		
		uiModel.addAttribute("inicio", inicio);
		uiModel.addAttribute("estancia", estancia);
		uiModel.addAttribute("tarifa", tarifa);
		uiModel.addAttribute("servicio", servicio);
		return "llamadas/colgarLlamada";
	}

	//------CU5 ------ COLGAR LLAMADA
	@RequestMapping(method = RequestMethod.POST, params = {"servicio", "estancia", "inicio"}, produces = "text/html")
	public String colgarLlamada(@RequestParam("servicio") String servicio, @RequestParam("estancia") long estancia_id, HttpServletRequest httpservletrequest,
	@RequestParam("inicio") long inicio, Model uiModel){

		long fin = System.currentTimeMillis();
		Estancia estancia = Estancia.findEstancia(estancia_id);
		double coste = 0;

		double duracion = 1 + ((fin - inicio) / (1000 * 60));

		Hotel hotel = estancia.getReserva().getHotel();
		Tarifa tarifa = Tarifa.findTarifasByHotel(hotel).getSingleResult();
		
		//Comprobamos si el usuario hizo llamadas antes
		List<Llamada> llamadas = Llamada.findLlamadasByEstancia(estancia).getResultList();
		
		//Almacenamos en la BD la duración de la llamada o acceso a internet
		if(servicio.equals("llamada_nacional")){
			coste = duracion * tarifa.getLlamada_nacional();

			if(llamadas.isEmpty()){
				Llamada llam = new Llamada();
				llam.setMinutos_nacionales(duracion);
				llam.persist();
				estancia.setLlamadas(llam);
				llam.setEstancia(estancia);
				estancia.merge();
			} else{
				Llamada llam = llamadas.get(0);

				llam.setMinutos_nacionales(llam.getMinutos_nacionales() + duracion);
				llam.merge();
			}
		}else if(servicio.equals("llamada_internacional")){
			coste = duracion * tarifa.getLlamada_internacional();

			if(llamadas.isEmpty()){
				Llamada llam = new Llamada();
				llam.setMinutos_internacionales(duracion);
				llam.persist();
				estancia.setLlamadas(llam);
				llam.setEstancia(estancia);
				estancia.merge();
			 }else{
				Llamada llam = llamadas.get(0);

				llam.setMinutos_internacionales(llam.getMinutos_internacionales() + duracion);
				llam.merge();
			}
		}else if (servicio.equals("internet")){
			coste = duracion * tarifa.getInternet();
			
			if(llamadas.isEmpty()){
				Llamada llam = new Llamada();
				llam.setMinutos_internet(duracion);
				llam.persist();
				estancia.setLlamadas(llam);
				llam.setEstancia(estancia);
				estancia.merge();
			}else{
				Llamada llam = llamadas.get(0);

				llam.setMinutos_internet(llam.getMinutos_internet() + duracion);
				llam.merge();
			}
		}

		uiModel.addAttribute("duracion", (int)duracion);
		uiModel.addAttribute("coste", coste);
		uiModel.addAttribute("servicio", servicio);
		return "estancias/consumoComunicacion";
	}
	
	

	
	/******************************** PDF *******************************/

	@RequestMapping(params = { "formpdf", "id", "total", "dias","precio_hab","aux","origen","hotel","total_nac","total_inter","total_internet","total_bar","total_servicios"}, method = RequestMethod.POST, produces = "text/html")
	public String pdf(Model uiModel, Principal principal,
			HttpServletRequest hhtpservletrequest, @RequestParam("id") long id,
			@RequestParam("total") double total,
			@RequestParam("dias") int dias,
			@RequestParam("precio_hab") double precio_hab,
			@RequestParam("aux") int aux, 
			@RequestParam("origen") String origen,
			@RequestParam("hotel") String hotel,
			@RequestParam("total_nac") double total_nac,
			@RequestParam("total_inter") double total_inter,
			@RequestParam("total_internet") double total_internet,
			@RequestParam("total_bar") double total_bar,
			@RequestParam("total_servicios") double total_servicios,
			@RequestParam("min_nac") double min_nac,
			@RequestParam("min_inter") double min_inter,
			@RequestParam("min_internet") double min_internet,
			@RequestParam("pre_nac") double pre_nac,
			@RequestParam("pre_inter") double pre_inter,
			@RequestParam("pre_internet") double pre_internet) {
		
		

		Reserva reserva = Reserva.findReserva(id);
		List<Estancia> estancia = Estancia.findEstanciasByReserva(reserva)
				.getResultList();
		Estancia est = estancia.get(0);

		// Calculo coste de la estancia
		
		double cama = 0;
		Habitacion_tipo tipo = reserva.getTipo();

		Usuario usuario = Usuario.findUsuariosByNombreUsuarioEquals(principal
				.getName());
		Habitacion habitacion = est.getHabitacion();
		// Se crea el documento
		Document documento = new Document();
		// Se crea el OutputStream para el fichero donde queremos dejar el pdf.
		FileOutputStream ficheroPdf;
		try {
			ficheroPdf = new FileOutputStream("reserva_" + id + ".pdf");
			// Se asocia el documento al OutputStream y se indica que el
			// espaciado entre
			// lineas sera de 20. Esta llamada debe hacerse antes de abrir el
			// documento
			PdfWriter.getInstance(documento, ficheroPdf).setInitialLeading(20);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Se abre el documento.
		documento.open();

		// Edición del documento
		String locale = LocaleContextHolder.getLocale().getLanguage();
		
		if(locale.equals("es")){
		try {

			Image foto = Image
					.getInstance("http://s1.postimg.org/f41c87ojz/Logo.png");

			foto.setAlignment(Chunk.ALIGN_MIDDLE);
			foto.scaleToFit(200, 200);
			documento.add(foto);

			documento.add(new Paragraph(
					"\n\tFactura", FontFactory
							.getFont("body-font", // fuente
									16, // tamaño
									Font.BOLD,  new BaseColor(24,64,110))));

			documento.add(new Paragraph("\n\tDatos personales:", FontFactory
					.getFont("body-font", // fuente
							14, // tamaño
							Font.BOLD, BaseColor.BLACK)));

			documento.add(new Paragraph("\n\t\tNombre: " + usuario.getNombre()
					+ "\n\t\tPrimer Apellido: " + usuario.getPrimer_apellido()
					+ "\n\t\tSegundo Apellido: "
					+ usuario.getSegundo_apellido(), FontFactory.getFont(
					"arial", // fuente
					10, // tamaño
					BaseColor.BLACK)));

			documento.add(new Paragraph("\n\n\tDatos de la estancia:",
					FontFactory.getFont("body-font", // fuente
							14, // tamaño
							Font.BOLD, BaseColor.BLACK)));

			String fout;
			if (est.getFecha_check_out() != null) {
				fout = new SimpleDateFormat("dd/MM/yyyy", LocaleContextHolder.getLocale()).format(est
						.getFecha_check_out());
			} else {
				fout = "No realizado";
			}
			documento.add(new Paragraph("\n\t\tHotel: "
					+ reserva.getHotel().getNombre()
					+ "\n\t\tNº Habitación: "
					+ est.getHabitacion().getNumero()
					+ "\n\t\tCategoría: "
					+ reserva.getCategoria().getNombre()
					+ "\n\t\tTipo: "
					+ reserva.getTipo()
					+ "\n\t\tFecha check-in: "
					+ new SimpleDateFormat("dd/MM/yyyy", LocaleContextHolder.getLocale()).format(est
							.getFecha_check_in()) + "\n\t\tFecha check-out: "
					+ fout, FontFactory.getFont("body-font", // fuente
					10, // tamaño
					BaseColor.BLACK)));

			documento.add(new Paragraph("\n\n\tDatos de los servicios:",
					FontFactory.getFont("body-font", // fuente
							14, // tamaño
							Font.BOLD, BaseColor.BLACK)));

			
			documento.add(new Paragraph("\n\t\t "
					+ reserva.getHotel().getNombre()
					+ "\n\t\tLlamadas nacionales: "+min_nac+" * "+pre_nac+" = " + total_nac
					+ " €\n\t\tLlamadas internacionales: "+min_inter+" * "+pre_inter+" = " + total_inter
					+ " €\n\t\tInternet: " +min_internet+" * "+pre_internet+" = "+ total_internet
					+ " €\n\t\tTotal minibar: " + total_bar
					+ " €\n\t\tTotal servicios: " + total_servicios+" €", FontFactory
					.getFont("body-font", // fuente
							10, // tamaño
							BaseColor.BLACK)));
			
			Paragraph par;
			documento
					.add(new Paragraph(
							"\t------------------------------------------------------------------------------------------------",
							FontFactory.getFont("body-font", // fuente
									14, // tamaño
									Font.BOLD, BaseColor.DARK_GRAY)));

			documento.add(new Paragraph("\tDatos de facturación:",
					FontFactory.getFont("body-font", // fuente
							16, // tamaño
							Font.BOLD, new BaseColor(24,64,110))));

			par = (new Paragraph("\n\t\tDías: " + dias
					+ "\n\t\tPrecio habitación: " + precio_hab
					+ " €\n\t\tSuplemento por categoría: "
					+ reserva.getCategoria().getPrecio_categoria()
					+ "\n\t\tCama supletoria: " + cama
					+ " €\n\t\tTotal servicios: " + total_servicios
					+ " €\n",
					FontFactory.getFont("body-font", // fuente
							10, // tamaño
							BaseColor.BLACK)));

		
			par.add(new Paragraph("\n\t\tTotal: " + total +" €",
					FontFactory.getFont("body-font", // fuente
							16, // tamaño
							Font.BOLD, new BaseColor(203, 0, 29))));

			par.setAlignment(Element.ALIGN_LEFT);

			documento.add(par);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    else
    {
		try {

			Image foto = Image
					.getInstance("http://s1.postimg.org/f41c87ojz/Logo.png");

			foto.setAlignment(Chunk.ALIGN_MIDDLE);
			foto.scaleToFit(200, 200);
			documento.add(foto);

			documento.add(new Paragraph(
					"\n\tBill", FontFactory
							.getFont("body-font", // fuente
									18, // tamaño
									Font.BOLD, new BaseColor(24,64,110))));

			documento.add(new Paragraph("\n\tPersonal details:", FontFactory
					.getFont("body-font", // fuente
							14, // tamaño
							Font.BOLD, BaseColor.BLACK)));

			documento.add(new Paragraph("\n\t\tName: " + usuario.getNombre()
					+ "\n\t\tFirst surname: " + usuario.getPrimer_apellido()
					+ "\n\t\tSecond surname: "
					+ usuario.getSegundo_apellido(), FontFactory.getFont(
					"arial", // fuente
					10, // tamaño
					BaseColor.BLACK)));

			documento.add(new Paragraph("\n\tStay details:",
					FontFactory.getFont("body-font", // fuente
							14, // tamaño
							Font.BOLD, BaseColor.BLACK)));

			String fout;
			if (est.getFecha_check_out() != null) {
				fout = new SimpleDateFormat("dd/MM/yyyy", LocaleContextHolder.getLocale()).format(est
						.getFecha_check_out());
			} else {
				fout = "No";
			}
			documento.add(new Paragraph("\n\t\tHotel: "
					+ reserva.getHotel().getNombre()
					+ "\n\t\tRoom Nº: "
					+ est.getHabitacion().getNumero()
					+ "\n\t\tCategory: "
					+ reserva.getCategoria().getNombre()
					+ "\n\t\tTipe: "
					+ reserva.getTipo()
					+ "\n\t\tCheck-in date: "
					+ new SimpleDateFormat("dd/MM/yyyy", LocaleContextHolder.getLocale()).format(est
							.getFecha_check_in()) + "\n\t\tCheck-out date: "
					+ fout, FontFactory.getFont("body-font", // fuente
					10, // tamaño
					BaseColor.BLACK)));

			documento.add(new Paragraph("\n\tServices details:",
					FontFactory.getFont("body-font", // fuente
							14, // tamaño
							Font.BOLD, BaseColor.BLACK)));

			
			documento.add(new Paragraph("\n\t\t "
					+ reserva.getHotel().getNombre()
					+ "\n\t\tInternational phone calls: "+min_nac+" * "+pre_nac+" = " + total_nac
					+ " €\n\t\tNational phone calls: "+min_inter+" * "+pre_inter+" = " + total_inter
					+ " €\n\t\tInternet: " +min_internet+" * "+pre_internet+" = "+ total_internet
					+ " €\n\t\tMinibar total: " + total_bar
					+ " €\n\t\tServices total: " + total_servicios+" €", FontFactory
					.getFont("body-font", // fuente
							10, // tamaño
							BaseColor.BLACK)));
			
			Paragraph par;
			documento
					.add(new Paragraph(
							"\t------------------------------------------------------------------------------------------------",
							FontFactory.getFont("body-font", // fuente
									14, // tamaño
									Font.BOLD, BaseColor.DARK_GRAY)));

			documento.add(new Paragraph("\tBilling details:",
					FontFactory.getFont("body-font", // fuente
							16, // tamaño
							Font.BOLD, new BaseColor(24,64,110))));

			par = (new Paragraph("\n\t\tNights: " + dias
					+ "\n\t\tRoom price: " + precio_hab
					+ " €\n\t\tCategory rate: "
					+ reserva.getCategoria().getPrecio_categoria()
					+ "\n\t\tExtra bed: " + cama
					+ " €\n\t\tTotal services: " + total_servicios + "€",
					FontFactory.getFont("body-font", // fuente
							10, // tamaño
							BaseColor.BLACK)));
			
			
			par.add(new Paragraph("\n\t\tTotal amount: " + total +" €",
					FontFactory.getFont("body-font", // fuente
							16, // tamaño
							Font.BOLD, new BaseColor(203, 0, 29))));
			
			

			par.setAlignment(Element.ALIGN_LEFT);
			documento.add(par);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

		// cierro el documento
		documento.close();

		// muestro el documento generado
		try {
			File path = new File("reserva_" + id + ".pdf");
			Desktop.getDesktop().open(path);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		Categoria categoria = reserva.getCategoria();
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
		uiModel.addAttribute("total_nac", total_nac);
		uiModel.addAttribute("total_inter", total_inter);
		uiModel.addAttribute("total_internet", total_internet);
		uiModel.addAttribute("total_bar", total_bar);
		uiModel.addAttribute("total_servicios", total_servicios);

		return "estancias/detallesCheckout";
	}
}

