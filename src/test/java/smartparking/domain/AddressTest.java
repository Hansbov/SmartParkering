package smartparking.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import smartparking.web.rest.TestUtil;

class AddressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Address.class);
        Address address1 = new Address();
        address1.setId(1L);
        Address address2 = new Address();
        address2.setId(address1.getId());
        assertThat(address1).isEqualTo(address2);
        address2.setId(2L);
        assertThat(address1).isNotEqualTo(address2);
        address1.setId(null);
        assertThat(address1).isNotEqualTo(address2);
    }
}
