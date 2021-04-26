package smartparking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A ParkingSpot.
 */
@Entity
@Table(name = "parking_spot")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "parkingspot")
public class ParkingSpot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "accessable_parking")
    private Boolean accessableParking;

    @NotNull
    @Column(name = "available", nullable = false)
    private Boolean available;

    @Column(name = "floor")
    private Long floor;

    @ManyToOne
    @JsonIgnoreProperties(value = { "openHours", "parkingSpots", "address" }, allowSetters = true)
    private CarPark carPark;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParkingSpot id(Long id) {
        this.id = id;
        return this;
    }

    public Boolean getAccessableParking() {
        return this.accessableParking;
    }

    public ParkingSpot accessableParking(Boolean accessableParking) {
        this.accessableParking = accessableParking;
        return this;
    }

    public void setAccessableParking(Boolean accessableParking) {
        this.accessableParking = accessableParking;
    }

    public Boolean getAvailable() {
        return this.available;
    }

    public ParkingSpot available(Boolean available) {
        this.available = available;
        return this;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getFloor() {
        return this.floor;
    }

    public ParkingSpot floor(Long floor) {
        this.floor = floor;
        return this;
    }

    public void setFloor(Long floor) {
        this.floor = floor;
    }

    public CarPark getCarPark() {
        return this.carPark;
    }

    public ParkingSpot carPark(CarPark carPark) {
        this.setCarPark(carPark);
        return this;
    }

    public void setCarPark(CarPark carPark) {
        this.carPark = carPark;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParkingSpot)) {
            return false;
        }
        return id != null && id.equals(((ParkingSpot) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParkingSpot{" +
            "id=" + getId() +
            ", accessableParking='" + getAccessableParking() + "'" +
            ", available='" + getAvailable() + "'" +
            ", floor=" + getFloor() +
            "}";
    }
}
