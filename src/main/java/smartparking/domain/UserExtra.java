package smartparking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A UserExtra.
 */
@Entity
@Table(name = "user_extra")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userextra")
public class UserExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "current_parking_spot")
    private String currentParkingSpot;

    @Column(name = "time_of_parking")
    private Instant timeOfParking;

    @JsonIgnoreProperties(value = { "carPark" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private ParkingSpot parkingSpot;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserExtra id(Long id) {
        this.id = id;
        return this;
    }

    public String getCurrentParkingSpot() {
        return this.currentParkingSpot;
    }

    public UserExtra currentParkingSpot(String currentParkingSpot) {
        this.currentParkingSpot = currentParkingSpot;
        return this;
    }

    public void setCurrentParkingSpot(String currentParkingSpot) {
        this.currentParkingSpot = currentParkingSpot;
    }

    public Instant getTimeOfParking() {
        return this.timeOfParking;
    }

    public UserExtra timeOfParking(Instant timeOfParking) {
        this.timeOfParking = timeOfParking;
        return this;
    }

    public void setTimeOfParking(Instant timeOfParking) {
        this.timeOfParking = timeOfParking;
    }

    public ParkingSpot getParkingSpot() {
        return this.parkingSpot;
    }

    public UserExtra parkingSpot(ParkingSpot parkingSpot) {
        this.setParkingSpot(parkingSpot);
        return this;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public User getUser() {
        return this.user;
    }

    public UserExtra user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserExtra)) {
            return false;
        }
        return id != null && id.equals(((UserExtra) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserExtra{" +
            "id=" + getId() +
            ", currentParkingSpot='" + getCurrentParkingSpot() + "'" +
            ", timeOfParking='" + getTimeOfParking() + "'" +
            "}";
    }
}
