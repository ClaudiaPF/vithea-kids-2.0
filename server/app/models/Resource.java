package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.util.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import play.Logger;

@Entity
public class Resource extends Model {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourcePath;

    @ManyToOne
    @JsonUnwrapped
    private ResourceType resourceType;

    @Column(nullable = false)
    private ResourceArea resourceArea;
    
    private Boolean defaultResource;

    public Boolean getDefaultResource() {
        return defaultResource;
    }

    public void setDefaultResource(Boolean defaultResource) {
        this.defaultResource = defaultResource;
    }

    @ManyToOne
    @JsonIgnore
    @Column(nullable = true)
    private Caregiver owner;
    
    public Resource(Caregiver owner, String path, ResourceArea resourceArea, Boolean def) {
        this.owner = owner;
        this.resourceType = new ResourceType("img");
        this.resourcePath = path;
        this.resourceArea = resourceArea;
        this.defaultResource = def;
    }
    
    public Resource(String path, ResourceArea resourceArea, Boolean def) {
        this.resourceType = new ResourceType("img");
        this.resourcePath = path;
        this.resourceArea = resourceArea;
        this.defaultResource = def;
    }
    
    public Long getResourceId() {
        return id;
    }

    public String getResourcePath() {
        return resourcePath;
    }
    
    public ResourceType getResourceType() {
        return resourceType;
    }

    public ResourceArea getResourceArea() {
        return resourceArea;
    }

    public Caregiver getOwner() {
        return owner;
    }
    

    public static final Finder<Long, Resource> find = new Finder<>(Resource.class);

    public static List<Resource> findByOwner(Caregiver owner) {
        Logger.debug("Looking for resources from: " + owner.getCaregiverLogin().getUsername());
        return find.where().eq("owner_id", owner.getCaregiverId()).findList();
    }

    public static Resource findById(Long resourceId) {
        return find.where().eq("id", resourceId).findUnique();
    }
}
