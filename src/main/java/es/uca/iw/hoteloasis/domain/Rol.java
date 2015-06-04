package es.uca.iw.hoteloasis.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findRolsByNombreEquals" })
public class Rol {

    /**
     */
    @NotNull
    @Size(max = 20)
    private String nombre;
    
	public static TypedQuery<Rol> findRolsByNombreEquals(String nombre) {
        EntityManager em = Rol.entityManager();
        TypedQuery<Rol> q = em.createQuery("SELECT o FROM Rol AS o WHERE o.nombre = :nombre", Rol.class);
        q.setParameter("nombre", nombre);
        return q;
    }
    
}
