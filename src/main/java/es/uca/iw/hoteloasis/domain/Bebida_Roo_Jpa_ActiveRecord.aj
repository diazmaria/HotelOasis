// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uca.iw.hoteloasis.domain;

import es.uca.iw.hoteloasis.domain.Bebida;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Bebida_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Bebida.entityManager;
    
    public static final List<String> Bebida.fieldNames4OrderClauseFilter = java.util.Arrays.asList("nombre", "coste");
    
    public static final EntityManager Bebida.entityManager() {
        EntityManager em = new Bebida().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Bebida.countBebidas() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Bebida o", Long.class).getSingleResult();
    }
    
    public static List<Bebida> Bebida.findAllBebidas() {
        return entityManager().createQuery("SELECT o FROM Bebida o", Bebida.class).getResultList();
    }
    
    public static List<Bebida> Bebida.findAllBebidas(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Bebida o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Bebida.class).getResultList();
    }
    
    public static Bebida Bebida.findBebida(Long id) {
        if (id == null) return null;
        return entityManager().find(Bebida.class, id);
    }
    
    public static List<Bebida> Bebida.findBebidaEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Bebida o", Bebida.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static List<Bebida> Bebida.findBebidaEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Bebida o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Bebida.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Bebida.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Bebida.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Bebida attached = Bebida.findBebida(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Bebida.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Bebida.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Bebida Bebida.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Bebida merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}