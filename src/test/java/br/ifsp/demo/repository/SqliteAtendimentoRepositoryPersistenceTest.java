package br.ifsp.demo.repository;

import br.ifsp.demo.exception.AtendimentoNaoEncontradoException;
import br.ifsp.demo.model.abertura.*;
import br.ifsp.demo.model.clinico.Diagnostico;
import br.ifsp.demo.model.clinico.TipoDiagnostico;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
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

    @Test
    void salvar_doisAtendimentosParaMesmoAgendamento_violaUnicidade() {
        AgendamentoId agendamento = AgendamentoId.of(UUID.randomUUID());
        repository.salvar(atendimento(novoAnimal(), StatusAtendimento.EM_ANDAMENTO,
                LocalDateTime.now(), null, agendamento));

        assertThatThrownBy(() -> repository.salvar(atendimento(novoAnimal(), StatusAtendimento.EM_ANDAMENTO,
                LocalDateTime.now(), null, agendamento)))
                .isInstanceOf(UncategorizedSQLException.class)
                .hasMessageContaining("UNIQUE constraint failed");
    }

    @Test
    void salvar_variosProntosAtendimentosSemAgendamento_saoPermitidos() {
        repository.salvar(Atendimento.abrirProntoAtendimento(novoAnimal()));
        repository.salvar(Atendimento.abrirProntoAtendimento(novoAnimal()));
        repository.salvar(Atendimento.abrirProntoAtendimento(novoAnimal()));

        assertThat(contar("atendimento")).isEqualTo(3);
    }

    @Test
    void salvar_concluidoMaisNovoEmAndamentoParaMesmoAnimal_ePermitido() {
        AnimalId animal = novoAnimal();
        repository.salvar(atendimento(animal, StatusAtendimento.CONCLUIDO,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), null));
        repository.salvar(emAndamento(animal));

        assertThat(repository.buscarPorAnimalId(animal)).hasSize(2);
    }

    @Test
    void existeEmAndamentoParaAnimal_comAtendimentoEmAndamento_retornaTrue() {
        AnimalId animal = novoAnimal();
        repository.salvar(emAndamento(animal));

        assertThat(repository.existeEmAndamentoParaAnimal(animal)).isTrue();
    }

    @Test
    void existeEmAndamentoParaAnimal_somenteConcluidoOuCancelado_retornaFalse() {
        AnimalId animal = novoAnimal();
        repository.salvar(atendimento(animal, StatusAtendimento.CONCLUIDO,
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusHours(1), null));
        repository.salvar(atendimento(animal, StatusAtendimento.CANCELADO,
                LocalDateTime.now().minusHours(4), LocalDateTime.now().minusHours(3), null));

        assertThat(repository.existeEmAndamentoParaAnimal(animal)).isFalse();
    }

    @Test
    void existeEmAndamentoParaAnimal_animalSemAtendimentos_retornaFalse() {
        assertThat(repository.existeEmAndamentoParaAnimal(novoAnimal())).isFalse();
    }

    @Test
    void existeEmAndamentoParaAnimal_emAndamentoDeOutroAnimal_retornaFalse() {
        repository.salvar(emAndamento(novoAnimal()));
        assertThat(repository.existeEmAndamentoParaAnimal(novoAnimal())).isFalse();
    }

    @Test
    void existeAtendimentoParaAgendamento_comAtendimento_retornaTrue() {
        AgendamentoId ag = AgendamentoId.of(UUID.randomUUID());
        repository.salvar(atendimento(novoAnimal(), StatusAtendimento.EM_ANDAMENTO,
                LocalDateTime.now(), null, ag));

        assertThat(repository.existeAtendimentoParaAgendamento(ag)).isTrue();
    }

    @Test
    void existeAtendimentoParaAgendamento_semAtendimento_retornaFalse() {
        assertThat(repository.existeAtendimentoParaAgendamento(AgendamentoId.of(UUID.randomUUID()))).isFalse();
    }

    @Test
    void existeAtendimentoParaAgendamento_prontoAtendimento_naoCasaComNenhumAgendamento() {
        repository.salvar(Atendimento.abrirProntoAtendimento(novoAnimal()));
        assertThat(repository.existeAtendimentoParaAgendamento(AgendamentoId.of(UUID.randomUUID()))).isFalse();
    }

    @Test
    void existeAtendimentoParaAgendamento_parametroNulo_naoDeveLancarExcecao() {
        assertThatCode(() -> repository.existeAtendimentoParaAgendamento(null))
                .doesNotThrowAnyException();
    }

    @Test
    void salvar_comDiagnosticos_recuperaTodosOsCampos() {
        Atendimento original = Atendimento.reconstituir(AtendimentoId.novo(), novoAnimal(), null,
                StatusAtendimento.EM_ANDAMENTO, LocalDateTime.now(), null, null, null, null,
                List.of(new Diagnostico("A01", "Gastrite", TipoDiagnostico.PRESUNTIVO),
                        new Diagnostico("B02", "Infeccao", TipoDiagnostico.DEFINITIVO)),
                List.of(), List.of());
        repository.salvar(original);

        Atendimento lido = repository.buscarUmPorAtendimentoId(original.getId());

        assertThat(lido.getDiagnosticos()).containsExactly(
                new Diagnostico("A01", "Gastrite", TipoDiagnostico.PRESUNTIVO),
                new Diagnostico("B02", "Infeccao", TipoDiagnostico.DEFINITIVO));
    }

    @Test
    void salvar_comDiagnosticos_preservaOrdemDeInsercao() {
        Atendimento original = Atendimento.reconstituir(AtendimentoId.novo(), novoAnimal(), null,
                StatusAtendimento.EM_ANDAMENTO, LocalDateTime.now(), null, null, null, null,
                List.of(new Diagnostico("Z", "ultimo-alfabeto", TipoDiagnostico.EMPIRICO),
                        new Diagnostico("A", "primeiro-alfabeto", TipoDiagnostico.EMPIRICO),
                        new Diagnostico("M", "meio", TipoDiagnostico.EMPIRICO)),
                List.of(), List.of());
        repository.salvar(original);

        Atendimento lido = repository.buscarUmPorAtendimentoId(original.getId());

        assertThat(lido.getDiagnosticos())
                .extracting(Diagnostico::codigo)
                .containsExactly("Z", "A", "M");
    }
}

