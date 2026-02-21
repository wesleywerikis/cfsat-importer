package br.com.wesleysilva.cfsat.infrastructure.xml;

import br.com.wesleysilva.cfsat.domain.model.Cupom;

import java.nio.file.Path;
import java.util.Optional;

public interface CfeSatXmlParser {
    /**
     * @return Optional.empty() quando for cupom cancelado ou inválido.
     */
    Optional<Cupom> parse(Path xmlPath);
}