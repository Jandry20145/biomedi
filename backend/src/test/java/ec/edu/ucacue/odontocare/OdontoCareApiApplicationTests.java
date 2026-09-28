package ec.edu.ucacue.odontocare;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class OdontoCareApiApplicationTests {

	@Test
	void applicationEntryPointIsAvailable() {
		assertDoesNotThrow(() -> OdontoCareApiApplication.class.getDeclaredMethod("main", String[].class));
	}

}
