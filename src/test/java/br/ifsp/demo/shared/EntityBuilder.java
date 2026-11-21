package br.ifsp.demo.shared;

import br.ifsp.demo.model.clinico.Diagnostico;
import br.ifsp.demo.model.clinico.TipoDiagnostico;
import br.ifsp.demo.model.prescricao.ItemPrescricao;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import com.github.javafaker.Faker;

import java.util.UUID;

public class EntityBuilder {

    private static final Faker faker = Faker.instance();

    public static User createRandomUser(String password) {
        return User.builder()
                .id(UUID.randomUUID())
                .name(faker.name().firstName())
                .lastname(faker.name().lastName())
                .email(faker.internet().emailAddress())
                .password(password)
                .role(Role.USER)
                .build();
    }

    public static Diagnostico createRandomDiagnostico() {
        return new Diagnostico(
                String.valueOf(faker.number().numberBetween(1000, 9999)),
                faker.artist().name(),
                TipoDiagnostico.EMPIRICO
        );
    }

    public static ItemPrescricao createRandomItemPrescricao() {
        return new ItemPrescricao(
                "Dipirona",
                faker.number().numberBetween(1, 500),
                "Oral",
                "8/8h",
                faker.number().numberBetween(1, 10));
    }
}