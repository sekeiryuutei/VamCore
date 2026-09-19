package com.vamcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Punto de entrada de VamCore (ver product.config.yml -> platform.name).
 *
 * VamCore es un monolito modular (ver ADR-001) que agrupa inicialmente
 * los productos comerciales VamStock, VamAsset y VamTrack, además de los
 * módulos core transversales (identity, organization, audit, files,
 * notifications, reporting).
 *
 * Se excluye UserDetailsServiceAutoConfiguration porque la autenticación
 * es 100% propia vía JWT (ver identity.infrastructure.security). Sin esta
 * exclusión, Spring Boot genera un usuario/contraseña por defecto que no
 * se usa nunca y solo genera ruido en los logs de arranque.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableAsync
public class VamCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(VamCoreApplication.class, args);
    }
}
