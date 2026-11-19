package br.ifsp.demo.repository;

import br.ifsp.demo.exception.AtendimentoNaoEncontradoException;
import br.ifsp.demo.model.abertura.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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

    @Test
    void buscarUmPorAtendimentoId_inexistente_lancaExcecao() {
        assertThatThrownBy(() -> repository.buscarUmPorAtendimentoId(AtendimentoId.novo()))
                .isInstanceOf(AtendimentoNaoEncontradoException.class);
    }

    @Test
    void buscarPorAnimalId_retornaApenasAtendimentosDoAnimalInformado() {
        AnimalId a1 = novoAnimal();
        AnimalId a2 = novoAnimal();
        repository.salvar(emAndamento(a1));
        repository.salvar(atendimento(a1, StatusAtendimento.CONCLUIDO,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), null));
        repository.salvar(emAndamento(a2));

        assertThat(repository.buscarPorAnimalId(a1))
                .hasSize(2)
                .allMatch(x -> x.getAnimalId().equals(a1));
    }

    @Test
    void salvar_doisEmAndamentoParaMesmoAnimal_violaUnicidade() {
        AnimalId animal = novoAnimal();
        repository.salvar(emAndamento(animal));

        assertThatThrownBy(() -> repository.salvar(emAndamento(animal)))
                .isInstanceOf(UncategorizedSQLException.class)
                .hasMessageContaining("UNIQUE constraint failed");
    }
}

