// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uca.iw.hoteloasis.domain;

import es.uca.iw.hoteloasis.domain.Reserva;
import es.uca.iw.hoteloasis.domain.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect Reserva_Roo_Finder {
    
    public static Long Reserva.countFindReservasByUsuario(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("The usuario argument is required");
        EntityManager em = Reserva.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM Reserva AS o WHERE o.usuario = :usuario", Long.class);
        q.setParameter("usuario", usuario);
        return ((Long) q.getSingleResult());
    }
    
    public static TypedQuery<Reserva> Reserva.findReservasByUsuario(Usuario usuario, String sortFieldName, String sortOrder) {
        if (usuario == null) throw new IllegalArgumentException("The usuario argument is required");
        EntityManager em = Reserva.entityManager();
        String jpaQuery = "SELECT o FROM Reserva AS o WHERE o.usuario = :usuario";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Reserva> q = em.createQuery(jpaQuery, Reserva.class);
        q.setParameter("usuario", usuario);
        return q;
    }
    
}
