package smartparking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A CarPark.
 */
@Entity
@Table(name = "car_park")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "carpark")
public class CarPark implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "owner")
    private String owner;

    @OneToMany(mappedBy = "carPark")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "carPark" }, allowSetters = true)
    private Set<OpenHours> openHours = new HashSet<>();

    @OneToMany(mappedBy = "carPark")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "carPark" }, allowSetters = true)
    private Set<ParkingSpot> parkingSpots = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "carParks" }, allowSetters = true)
    private Address address;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarPark id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public CarPark name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return this.owner;
    }

    public CarPark owner(String owner) {
        this.owner = owner;
        return this;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Set<OpenHours> getOpenHours() {
        return this.openHours;
    }

    public CarPark openHours(Set<OpenHours> openHours) {
        this.setOpenHours(openHours);
        return this;
    }

    public CarPark addOpenHours(OpenHours openHours) {
        this.openHours.add(openHours);
        openHours.setCarPark(this);
        return this;
    }

    public CarPark removeOpenHours(OpenHours openHours) {
        this.openHours.remove(openHours);
        openHours.setCarPark(null);
        return this;
    }

    public void setOpenHours(Set<OpenHours> openHours) {
        if (this.openHours != null) {
            this.openHours.forEach(i -> i.setCarPark(null));
        }
        if (openHours != null) {
            openHours.forEach(i -> i.setCarPark(this));
        }
        this.openHours = openHours;
    }

    public Set<ParkingSpot> getParkingSpots() {
        return this.parkingSpots;
    }

    public CarPark parkingSpots(Set<ParkingSpot> parkingSpots) {
        this.setParkingSpots(parkingSpots);
        return this;
    }

    public CarPark addParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpots.add(parkingSpot);
        parkingSpot.setCarPark(this);
        return this;
    }

    public CarPark removeParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpots.remove(parkingSpot);
        parkingSpot.setCarPark(null);
        return this;
    }

    public void setParkingSpots(Set<ParkingSpot> parkingSpots) {
        if (this.parkingSpots != null) {
            this.parkingSpots.forEach(i -> i.setCarPark(null));
        }
        if (parkingSpots != null) {
            parkingSpots.forEach(i -> i.setCarPark(this));
        }
        this.parkingSpots = parkingSpots;
    }

    public Address getAddress() {
        return this.address;
    }

    public CarPark address(Address address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarPark)) {
            return false;
        }
        return id != null && id.equals(((CarPark) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarPark{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", owner='" + getOwner() + "'" +
            "}";
    }
}
