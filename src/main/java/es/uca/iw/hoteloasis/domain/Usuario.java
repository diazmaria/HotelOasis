package es.uca.iw.hoteloasis.domain;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Hex;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;


@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findUsuariosByNombreUsuarioEquals", "findUsuariosByRol"})
public class Usuario {

    /**
     */
    @NotNull
    @Size(min = 2, max = 30)
    private String nombre;

    /**
     */
    @NotNull
    @Size(min = 2, max = 30)
    private String primer_apellido;

    /**
     */

    private String segundo_apellido;

    /**
     */
    @NotNull
    @Size(min = 2, max = 30)
    private String nombre_usuario;

    /**
     */
    @NotNull
    @Size(max = 300)
    private String clave;

    /**
     */
    @NotNull
    @Size(max = 50)
    private String email;

    /**
     */
    @NotNull
    private Boolean enabled;

    /**
     */
    @ManyToOne
    private Rol rol;

     
    //CONSTRUCTORES
	public Usuario() {
    }

    public Usuario(String nombre, String primer_apellido, String segundo_apellido, String email, String nombre_usuario, String clave, Boolean enabled, Rol rol) {
        this.nombre = nombre;
        this.primer_apellido = primer_apellido;
        this.segundo_apellido = segundo_apellido;
        this.email = email;
        this.nombre_usuario = nombre_usuario;
        this.clave = clave;
        this.enabled = enabled;
        this.rol = rol;
    }
    
    
    //BUSCADORES PERSONALIZADOS
     
    //BUSCAR POR NOMBRE DE USUARIO
    public static Usuario findUsuariosByNombreUsuarioEquals(String nombre_usuario) {
    	EntityManager em = Usuario.entityManager();
        TypedQuery<Usuario> query = em.createQuery("SELECT o FROM Usuario AS o WHERE o.nombre_usuario = :nombre_usuario", Usuario.class);
        query.setParameter("nombre_usuario", nombre_usuario);
        Usuario u = query.getResultList().isEmpty() ? null : query.getResultList().get(0);
        return u;
    }
    
    //BUSCAR POR ROL
	public static List<Usuario> findUsuariosByRol(Rol rol) {
        EntityManager em = Usuario.entityManager();
        TypedQuery<Usuario> query = em.createQuery("SELECT * FROM Usuario WHERE rol = :rol", Usuario.class);
        query.setParameter("rol", rol);
        return query.getResultList();
    }
	
	//ENCRIPTAR CONTRASEÑA SHA256
	public static String sha256(String original) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(original.getBytes());
        byte[] digest = md.digest();
        return new String(Hex.encodeHexString(digest));
	}
}
