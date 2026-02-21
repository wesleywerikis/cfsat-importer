package br.com.wesleysilva.cfsat.application.service;

import br.com.wesleysilva.cfsat.domain.model.Cupom;
import br.com.wesleysilva.cfsat.infrastructure.persistence.CupomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarCuponsService {

    private final CupomRepository repo;

    public List<Cupom> listarOrdenadoPorNumero() {
        return repo.findAll(Sort.by(Sort.Direction.ASC, "numeroCFe"));
    }

    public List<Cupom> listarOrdenadoPorValorTotal() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "valorTotal"));
    }

    public List<Cupom> listarDetalhadoOrdenadoPorChave() {
        return repo.findAllOrderByChaveComItens();
    }
}