// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uca.iw.hoteloasis.web;

import es.uca.iw.hoteloasis.domain.Bebida_consumo;
import es.uca.iw.hoteloasis.domain.Estancia;
import es.uca.iw.hoteloasis.domain.Habitacion;
import es.uca.iw.hoteloasis.domain.Llamada;
import es.uca.iw.hoteloasis.domain.Reserva;
import es.uca.iw.hoteloasis.domain.Usuario;
import es.uca.iw.hoteloasis.web.EstanciaController;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect EstanciaController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String EstanciaController.create(@Valid Estancia estancia, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, estancia);
            return "estancias/create";
        }
        uiModel.asMap().clear();
        estancia.persist();
        return "redirect:/estancias/" + encodeUrlPathSegment(estancia.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String EstanciaController.createForm(Model uiModel) {
        populateEditForm(uiModel, new Estancia());
        return "estancias/create";
    }
    
    @RequestMapping(value = "/{id}", produces = "text/html")
    public String EstanciaController.show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("estancia", Estancia.findEstancia(id));
        uiModel.addAttribute("itemId", id);
        return "estancias/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String EstanciaController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("estancias", Estancia.findEstanciaEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Estancia.countEstancias() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("estancias", Estancia.findAllEstancias(sortFieldName, sortOrder));
        }
        addDateTimeFormatPatterns(uiModel);
        return "estancias/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String EstanciaController.update(@Valid Estancia estancia, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, estancia);
            return "estancias/update";
        }
        uiModel.asMap().clear();
        estancia.merge();
        return "redirect:/estancias/" + encodeUrlPathSegment(estancia.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String EstanciaController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Estancia.findEstancia(id));
        return "estancias/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String EstanciaController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Estancia estancia = Estancia.findEstancia(id);
        estancia.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/estancias";
    }
    
    void EstanciaController.addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("estancia_fecha_check_in_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
        uiModel.addAttribute("estancia_fecha_check_out_date_format", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }
    
    void EstanciaController.populateEditForm(Model uiModel, Estancia estancia) {
        uiModel.addAttribute("estancia", estancia);
        addDateTimeFormatPatterns(uiModel);
        uiModel.addAttribute("bebida_consumoes", Bebida_consumo.findAllBebida_consumoes());
        uiModel.addAttribute("habitacions", Habitacion.findAllHabitacions());
        uiModel.addAttribute("llamadas", Llamada.findAllLlamadas());
        uiModel.addAttribute("reservas", Reserva.findAllReservas());
        uiModel.addAttribute("usuarios", Usuario.findAllUsuarios());
    }
    
    String EstanciaController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}