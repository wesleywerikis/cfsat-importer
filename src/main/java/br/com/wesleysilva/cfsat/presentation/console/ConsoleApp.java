package br.com.wesleysilva.cfsat.presentation.console;

import br.com.wesleysilva.cfsat.application.service.ConsultarCuponsService;
import br.com.wesleysilva.cfsat.application.service.ImportarCuponsService;
import br.com.wesleysilva.cfsat.domain.model.Cupom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ConsoleApp {

    private final ImportarCuponsService importarService;
    private final ConsultarCuponsService consultarService;

    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public void start() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== CF-e SAT Importer ===");
            System.out.println("1) Importar XMLs de uma pasta");
            System.out.println("2) Listar cupons (ordenado por NÚMERO)");
            System.out.println("3) Listar cupons (ordenado por VALOR TOTAL)");
            System.out.println("4) Relatório detalhado (Cupom -> Itens)");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");

            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> importar(sc);
                case "2" -> listar(consultarService.listarOrdenadoPorNumero());
                case "3" -> listar(consultarService.listarOrdenadoPorValorTotal());
                case "4" -> relatorioDetalhado();
                case "0" -> {
                    System.out.println("Saindo...");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void importar(Scanner sc) {
        System.out.print("Informe a pasta onde estão os XMLs: ");
        String p = sc.nextLine().trim();

        var r = importarService.importarPasta(Path.of(p));
        System.out.println("\nImportação concluída:");
        System.out.println("- XMLs encontrados: " + r.encontrados());
        System.out.println("- Importados:       " + r.importados());
        System.out.println("- Duplicados:       " + r.duplicados());
        System.out.println("- Ignorados:        " + r.canceladosOuInvalidos());
    }

    private void listar(List<Cupom> cupons) {
        if (cupons.isEmpty()) {
            System.out.println("\nNenhum cupom encontrado.");
            return;
        }

        System.out.println("\n--- Cupons ---");
        for (Cupom c : cupons) {
            System.out.printf(
                    "Nº %s | Total %s | Emissão %s | Chave %s | Itens: %d%n",
                    c.getNumeroCFe(),
                    BRL.format(c.getValorTotal()),
                    c.getDataEmissao(),
                    c.getChaveAcesso(),
                    c.getItens().size()
            );
        }
    }

    private void relatorioDetalhado() {
        var cupons = consultarService.listarDetalhadoOrdenadoPorChave(); // vamos criar

        if (cupons.isEmpty()) {
            System.out.println("\nNenhum cupom encontrado.");
            return;
        }

        System.out.println("\n=== RELATÓRIO DETALHADO (Cupom -> Itens) ===");

        for (var c : cupons) {
            System.out.printf(
                    "%nChave de acesso: %s%nNº CF-e: %s | Emissão: %s | Total: R$ %s | Itens: %d%n",
                    c.getChaveAcesso(),
                    c.getNumeroCFe(),
                    c.getDataEmissao(),
                    c.getValorTotal(),
                    c.getItens().size()
            );

            System.out.println("  Itens:");
            System.out.println("  ------------------------------------------------------------------------------");
            System.out.println("  CODIGO        | DESCRICAO                              | CFOP  | QTD   | VALOR");
            System.out.println("  ------------------------------------------------------------------------------");

            for (var it : c.getItens()) {
                System.out.printf(
                        "  %-12s | %-38s | %-5s | %5s | %7s%n",
                        safe(it.getCodigo(), 12),
                        safe(it.getDescricao(), 38),
                        safe(it.getCfop(), 5),
                        it.getQuantidade(),
                        it.getValorTotalBruto()
                );
            }
            System.out.println("  ------------------------------------------------------------------------------");
        }
    }

    private String safe(String s, int max) {
        if (s == null) return "";
        s = s.replaceAll("\\s+", " ").trim();
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 3)) + "...";
    }

}