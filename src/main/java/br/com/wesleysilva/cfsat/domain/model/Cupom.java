package br.com.wesleysilva.cfsat.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cupom",
        uniqueConstraints = @UniqueConstraint(name = "uk_cupom_chave", columnNames = "chave_acesso"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chave_acesso", nullable = false, length = 80)
    private String chaveAcesso;

    @Column(name = "numero_cfe", nullable = false, length = 20)
    private String numeroCFe;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao;

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "cupom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemCupom> itens = new ArrayList<>();

    public void addItem(ItemCupom item) {
        item.setCupom(this);
        itens.add(item);
    }
}