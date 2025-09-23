package ar.edu.uncuyo.gimnasio_sport;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GimnasioSportApplication {

    @Value("${MP_TOKEN}")
    private static String tokenMercadoPago;

	public static void main(String[] args) {
        String tokenMercadoPago = System.getenv("MP_TOKEN");
        MercadoPagoConfig.setAccessToken(tokenMercadoPago);
        SpringApplication.run(GimnasioSportApplication.class, args);
	}

}
