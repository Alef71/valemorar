package br.com.valemorar.domain.anucio.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevisaoAnuncioServiceTest {

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    @Test
    void medianaImparEPar() {
        assertEquals(0, bd("500").compareTo(RevisaoAnuncioService.mediana(List.of(bd("900"), bd("500"), bd("300")))));
        assertEquals(0, bd("400").compareTo(RevisaoAnuncioService.mediana(List.of(bd("300"), bd("500")))));
    }

    @Test
    void indiceValor() {
        // Mercado: R$ 500/quarto; anúncio de 2 quartos por R$ 500 => R$ 250/quarto => índice 2
        assertEquals(0, bd("2").compareTo(RevisaoAnuncioService.indiceValor(bd("500"), bd("500"), 2)));
        assertEquals(0, bd("1").compareTo(RevisaoAnuncioService.indiceValor(bd("500"), bd("1000"), 2)));
    }

    @Test
    void linguagemDeGolpeIgnoraAcentosEMaiusculas() {
        assertEquals("deposito antecipado",
                RevisaoAnuncioService.termoGolpe("Casa linda, DEPÓSITO ANTECIPADO garante a vaga").orElseThrow());
        assertTrue(RevisaoAnuncioService.termoGolpe("Casa com 2 quartos perto da UFVJM").isEmpty());
    }
}
