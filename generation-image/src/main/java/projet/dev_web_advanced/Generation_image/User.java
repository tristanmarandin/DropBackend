package projet.dev_web_advanced.Generation_image;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class User implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long Id;
    private String name;
    private String password;
    private String mail_adress;
    private boolean connected;
    private String role;
    private String profile_photo;

    //@OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    //private List<Collection> collections_created;
    
    public Long getId() {
        return Id;
    }
    public void setId(Long id) {
        Id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getMail_adress() {
        return mail_adress;
    }
    public void setMail_adress(String mail_adress) {
        this.mail_adress = mail_adress;
    }
    public boolean isConnected() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    /*
    public Set<Image> getImages()  {
        return images_created;
    }
    public void setImages(Set<Image> list_images) {
        images_created = list_images;
    }
    public List<Collection> getCollections() {
        return collections_created;
    }
    public void setCollections(List<Collection> list_collections) {
        collections_created = list_collections;
    } */   
}
