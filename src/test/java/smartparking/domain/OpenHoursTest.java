package smartparking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import smartparking.web.rest.TestUtil;

class OpenHoursTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OpenHours.class);
        OpenHours openHours1 = new OpenHours();
        openHours1.setId(1L);
        OpenHours openHours2 = new OpenHours();
        openHours2.setId(openHours1.getId());
        assertThat(openHours1).isEqualTo(openHours2);
        openHours2.setId(2L);
        assertThat(openHours1).isNotEqualTo(openHours2);
        openHours1.setId(null);
        assertThat(openHours1).isNotEqualTo(openHours2);
    }
}
