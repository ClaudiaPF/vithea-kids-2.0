package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ResourceType extends Model {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
        @JsonIgnore
	private Long id;
	
	private String resourceTypeDescription;
        
        
        public ResourceType(String type) {
            this.resourceTypeDescription = type;
        }

	/**
	 * @return the resourceTypeId
	 */
	public Long getResourceTypeId() {
            return id;
	}

	/**
	 * @param resourceTypeId the resourceTypeId to set
	 */
	public void setResourceTypeId(Long resourceTypeId) {
            this.id = resourceTypeId;
	}

	/**
	 * @return the resourceTypeDescription
	 */
	public String getResourceTypeDescription() {
            return resourceTypeDescription;
	}

	/**
	 * @param resourceTypeDescription the resourceTypeDescription to set
	 */
	public void setResourceTypeDescription(String resourceTypeDescription) {
            this.resourceTypeDescription = resourceTypeDescription;
	}
	
}
