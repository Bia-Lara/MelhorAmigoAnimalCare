package br.ifsp.demo.repository;

import br.ifsp.demo.model.abertura.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SqliteAtendimentoRepository.class)
@Tag("PersistenceTest")
@Tag("IntegrationTest")
class SqliteAtendimentoRepositoryPersistenceTest {

    @Autowired
    private SqliteAtendimentoRepository repository;
    @Autowired
    private JdbcTemplate jdbc;

    private static AnimalId novoAnimal() {
        return AnimalId.of(UUID.randomUUID());
    }

    private static Atendimento atendimento(AnimalId animal, StatusAtendimento status,
                                           LocalDateTime dataHora, LocalDateTime conclusao,
                                           AgendamentoId agendamento) {
        return Atendimento.reconstituir(AtendimentoId.novo(), animal, agendamento, status,
                dataHora, conclusao, null, null, null,
                List.of(), List.of(), List.of());
    }

    private static Atendimento emAndamento(AnimalId animal) {
        return atendimento(animal, StatusAtendimento.EM_ANDAMENTO, LocalDateTime.now(), null, null);
    }

    private int contar(String tabela) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + tabela, Integer.class);
    }

    @Test
    void buscarPorAnimalId_semAtendimentos_retornaListaVazia() {
        assertThat(repository.buscarPorAnimalId(novoAnimal())).isEmpty();
    }

    @Test
    void salvar_atendimentoMinimo_persisteComCamposNulos() {
        Atendimento original = Atendimento.abrirProntoAtendimento(novoAnimal());
        repository.salvar(original);

        Atendimento lido = repository.buscarUmPorAtendimentoId(original.getId());

        assertThat(lido.getId()).isEqualTo(original.getId());
        assertThat(lido.getStatus()).isEqualTo(StatusAtendimento.EM_ANDAMENTO);
        assertThat(lido.getAgendamentoId()).isNull();
        assertThat(lido.getSinaisVitais()).isNull();
        assertThat(lido.getAnamnese()).isNull();
        assertThat(lido.getDiagnosticos()).isEmpty();
        assertThat(lido.getPrescricoes()).isEmpty();
        assertThat(lido.getExames()).isEmpty();
    }
}

