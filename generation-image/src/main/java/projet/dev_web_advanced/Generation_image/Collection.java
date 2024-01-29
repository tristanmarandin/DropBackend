package projet.dev_web_advanced.Generation_image;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Collection implements Serializable {

    public Collection() {
        super();
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long Id;
    private String name;
    
    @ManyToOne(optional = false)
    private User creator;
    
    @ManyToMany 
    private List<Image> list_images;
    
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
    public User getCreator() {
        return creator;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }
    public List<Image> getList_images() {
        return list_images;
    }
    public void setList_images(List<Image> list_images) {
        this.list_images = list_images;
    }    
}
