package smartparking.web.rest.vm;

import javax.validation.constraints.Size;
import smartparking.service.dto.AdminUserDTO;

import java.time.Instant;

/**
 * View Model extending the AdminUserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends AdminUserDTO {
    private String currentParkingSpot;
    private Instant timeOfParking;

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public String getCurrentParkingSpot() {
        return this.currentParkingSpot;
    }

    public Instant getTimeOfParking() {
        return this.timeOfParking;
    }

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ManagedUserVM{" + super.toString() + "} ";
    }
}
