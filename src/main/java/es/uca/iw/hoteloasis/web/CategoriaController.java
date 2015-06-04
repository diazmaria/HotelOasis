package es.uca.iw.hoteloasis.web;
import es.uca.iw.hoteloasis.domain.Categoria;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/categorias")
@Controller
@RooWebScaffold(path = "categorias", formBackingObject = Categoria.class)
public class CategoriaController {

    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("a", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
        uiModel.addAttribute("a", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
        uiModel.addAttribute("a", DateTimeFormat.patternForStyle("M-", LocaleContextHolder.getLocale()));
    }
}
