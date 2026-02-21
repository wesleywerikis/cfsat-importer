package br.com.wesleysilva.cfsat.infrastructure.persistence;

import br.com.wesleysilva.cfsat.domain.model.Cupom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CupomRepository extends JpaRepository<Cupom, Long> {

    boolean existsByChaveAcesso(String chaveAcesso);

    @EntityGraph(attributePaths = "itens")
    List<Cupom> findAll(Sort sort);

    @Query("""
                select distinct c
                from Cupom c
                left join fetch c.itens i
                order by c.chaveAcesso asc, c.numeroCFe asc, i.id asc
            """)
    List<Cupom> findAllOrderByChaveComItens();
}