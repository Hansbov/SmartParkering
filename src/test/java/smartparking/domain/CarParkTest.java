package smartparking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import smartparking.web.rest.TestUtil;

class CarParkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarPark.class);
        CarPark carPark1 = new CarPark();
        carPark1.setId(1L);
        CarPark carPark2 = new CarPark();
        carPark2.setId(carPark1.getId());
        assertThat(carPark1).isEqualTo(carPark2);
        carPark2.setId(2L);
        assertThat(carPark1).isNotEqualTo(carPark2);
        carPark1.setId(null);
        assertThat(carPark1).isNotEqualTo(carPark2);
    }
}
