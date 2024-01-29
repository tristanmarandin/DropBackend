package projet.dev_web_advanced.Generation_image;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class Image implements Serializable {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long Id;
    private String prompt;
    private String negative_prompt;
    private String model;
    private String seed;
    private String step;
    private String cfg_scale;
    private String url_image;
    private Float note;
    private int height;
    private int width;
    private boolean visible;

    @ManyToOne(optional = false)
    private User creator;
    
    public Long getId() {
        return Id;
    }
    public User getCreator() {
        return creator;
    }
    public String getPrompt() {
        return prompt;
    }
    public String getNegative_prompt() {
        return negative_prompt;
    }
    public String getModel() {
        return model;
    }
    public String getSeed() {
        return seed;
    }
    public String getStep() {
        return step;
    }
    public String getCfg_scale() {
        return cfg_scale;
    }
    public String getUrl_image() {
        return url_image;
    }
    public Float getNote() {
        return note;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setId(Long id) {
        Id = id;
    }
    public void setCreator(User creator) {
        this.creator = creator;
    }
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    public void setNegative_prompt(String negative_prompt) {
        this.negative_prompt = negative_prompt;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public void setSeed(String seed) {
        this.seed = seed;
    }
    public void setStep(String step) {
        this.step = step;
    }
    public void setCfg_scale(String cfg_scale) {
        this.cfg_scale = cfg_scale;
    }
    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }
    public void setNote(Float note) {
        this.note = note;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
