package com.github.nagyesta.abortmission.testkit.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import java.util.function.Supplier;

@SpringBootApplication
public class StaticFire {

    public static void main(final String[] args) {
        SpringApplication.run(StaticFire.class, args);
    }

    @Bean
    public Supplier<Booster> boosterSupplier() {
        return Booster::new;
    }

    @Bean
    public Booster centerCore() {
        return boosterSupplier().get();
    }

    @Bean
    @Lazy
    public Booster sideBooster() {
        throw new UnsupportedOperationException("Side boosters are not supported.");
    }
}
