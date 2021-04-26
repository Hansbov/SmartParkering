package smartparking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import smartparking.web.rest.TestUtil;

class ParkingSpotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParkingSpot.class);
        ParkingSpot parkingSpot1 = new ParkingSpot();
        parkingSpot1.setId(1L);
        ParkingSpot parkingSpot2 = new ParkingSpot();
        parkingSpot2.setId(parkingSpot1.getId());
        assertThat(parkingSpot1).isEqualTo(parkingSpot2);
        parkingSpot2.setId(2L);
        assertThat(parkingSpot1).isNotEqualTo(parkingSpot2);
        parkingSpot1.setId(null);
        assertThat(parkingSpot1).isNotEqualTo(parkingSpot2);
    }
}
