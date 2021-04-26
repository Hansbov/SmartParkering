package smartparking.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * A OpenHours.
 */
@Entity
@Table(name = "open_hours")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "openhours")
public class OpenHours implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "weekday")
    private String weekday;

    @Column(name = "opening_hour")
    private String openingHour;

    @Column(name = "closing_hour")
    private String closingHour;

    @Column(name = "date")
    private LocalDate date;

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

    public OpenHours id(Long id) {
        this.id = id;
        return this;
    }

    public String getWeekday() {
        return this.weekday;
    }

    public OpenHours weekday(String weekday) {
        this.weekday = weekday;
        return this;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getOpeningHour() {
        return this.openingHour;
    }

    public OpenHours openingHour(String openingHour) {
        this.openingHour = openingHour;
        return this;
    }

    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }

    public String getClosingHour() {
        return this.closingHour;
    }

    public OpenHours closingHour(String closingHour) {
        this.closingHour = closingHour;
        return this;
    }

    public void setClosingHour(String closingHour) {
        this.closingHour = closingHour;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public OpenHours date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public CarPark getCarPark() {
        return this.carPark;
    }

    public OpenHours carPark(CarPark carPark) {
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
        if (!(o instanceof OpenHours)) {
            return false;
        }
        return id != null && id.equals(((OpenHours) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OpenHours{" +
            "id=" + getId() +
            ", weekday='" + getWeekday() + "'" +
            ", openingHour='" + getOpeningHour() + "'" +
            ", closingHour='" + getClosingHour() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
