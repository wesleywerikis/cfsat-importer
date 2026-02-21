package br.com.wesleysilva.cfsat.application.service;

import br.com.wesleysilva.cfsat.domain.model.Cupom;
import br.com.wesleysilva.cfsat.infrastructure.persistence.CupomRepository;
import br.com.wesleysilva.cfsat.infrastructure.xml.CfeSatXmlParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ImportarCuponsService {

    private final CupomRepository cupomRepository;
    private final CfeSatXmlParser parser;

    public record ResultadoImportacao(int encontrados, int importados, int duplicados, int canceladosOuInvalidos) {
    }

    @Transactional
    public ResultadoImportacao importarPasta(Path pasta) {
        if (pasta == null || !Files.isDirectory(pasta)) {
            throw new IllegalArgumentException("Pasta inválida: " + pasta);
        }

        AtomicInteger encontrados = new AtomicInteger();
        AtomicInteger importados = new AtomicInteger();
        AtomicInteger duplicados = new AtomicInteger();
        AtomicInteger ignorados = new AtomicInteger();

        try (var stream = Files.walk(pasta)) {
            stream.filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase().endsWith(".xml"))
                    .forEach(xml -> {
                        encontrados.incrementAndGet();

                        var opt = parser.parse(xml);
                        if (opt.isEmpty()) {
                            ignorados.incrementAndGet();
                            return;
                        }

                        Cupom cupom = opt.get();
                        if (cupomRepository.existsByChaveAcesso(cupom.getChaveAcesso())) {
                            duplicados.incrementAndGet();
                            return;
                        }

                        cupomRepository.save(cupom);
                        importados.incrementAndGet();
                    });

        } catch (Exception e) {
            throw new RuntimeException("Falha ao importar pasta: " + e.getMessage(), e);
        }

        return new ResultadoImportacao(
                encontrados.get(),
                importados.get(),
                duplicados.get(),
                ignorados.get()
        );
    }
}