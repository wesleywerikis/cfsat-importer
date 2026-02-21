package br.com.wesleysilva.cfsat.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cupom_item",
        indexes = {
                @Index(name = "ix_item_cfop", columnList = "cfop"),
                @Index(name = "ix_item_codigo", columnList = "codigo")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_id", nullable = false)
    private Cupom cupom;

    @Column(nullable = false, length = 60)
    private String codigo;

    @Column(nullable = false, length = 300)
    private String descricao;

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal quantidade;

    @Column(nullable = false, length = 10)
    private String cfop;

    @Column(name = "valor_total_bruto", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalBruto;
}