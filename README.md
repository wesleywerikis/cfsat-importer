# CF-e SAT Importer

Aplicação Java desenvolvida para processar múltiplos arquivos XML de
**CF-e SAT**, extrair dados fiscais relevantes e persistir em banco de
dados relacional.

## 🚀 Tecnologias Utilizadas

-   Java 17
-   Spring Boot 3.3.2
-   Spring Data JPA
-   Hibernate 6
-   H2 Database (modo arquivo)
-   Lombok
-   Maven

------------------------------------------------------------------------

## 📌 Funcionalidades

✔ Processamento de múltiplos XMLs CF-e SAT\
✔ Ignora cupons cancelados\
✔ Evita duplicidade por chave de acesso\
✔ Persistência em banco relacional\
✔ Listagem ordenada por número do CF-e\
✔ Listagem ordenada por valor total\
✔ Relatório detalhado (Cupom → Itens)

------------------------------------------------------------------------

## 🏗 Arquitetura

    br.com.wesleysilva.cfsat
    │
    ├── application        # Regras de negócio
    │   └── service
    │
    ├── domain             # Entidades JPA
    │   └── model
    │
    ├── infrastructure
    │   ├── persistence    # Repositórios
    │   └── xml            # Parser XML
    │
    └── presentation       # Interface Console

Separação clara de responsabilidades:

-   **Parser** → XML para Entidade
-   **Service** → Regras de negócio
-   **Repository** → Persistência
-   **ConsoleApp** → Interface

------------------------------------------------------------------------

## 🔄 Fluxo Completo da Aplicação

```
┌─────────────────────────────────────────────────────────────────────┐
│ 1) BOOT                                                              │
└─────────────────────────────────────────────────────────────────────┘
   main(args)
      │
      v
   SpringApplication.run()
      │
      ├─► AutoConfig:
      │     - DataSource (Hikari)
      │     - JPA/Hibernate (ddl-auto=update)
      │     - Repositories / Beans / DI
      │     - H2 arquivo: ./data/cfsat.mv.db
      │
      v
   CommandLineRunner.run() ──► ConsoleApp.start()

┌─────────────────────────────────────────────────────────────────────┐
│ 2) MENU (ConsoleApp)                                                 │
└─────────────────────────────────────────────────────────────────────┘
   Opção "1" Importar  ─┐
   Opção "2" Listar Nº  ├─► chama ConsultarCuponsService
   Opção "3" Listar R$  │
   Opção "4" Relatório  ┘

┌─────────────────────────────────────────────────────────────────────┐
│ 3) OPÇÃO 1 — IMPORTAÇÃO (fluxo principal)                            │
└─────────────────────────────────────────────────────────────────────┘
ConsoleApp.importar()
   │  lê caminho da pasta (String p)
   │  Path pasta = Path.of(p)
   v
ImportarCuponsService.importarPasta(pasta)  (@Transactional)
   │
   ├─► valida pasta (exists + isDirectory)
   ├─► contadores: encontrados / importados / duplicados / ignorados
   │
   v
Files.walk(pasta) → filtra *.xml → LOOP por xmlPath
   │
   ├─► encontrados++
   │
   ├─► parser.parse(xmlPath)  (DomCfeSatXmlParser)
   │      │
   │      ├─► protege XXE (SECURE_PROCESSING + bloqueia DTD/Schema)
   │      ├─► parse XML → Document
   │      ├─► se CANCELADO (CFeCanc ou cancCFe) → Optional.empty
   │      ├─► extrai chave/numero/data/total
   │      ├─► monta Cupom
   │      └─► monta itens <det> → cupom.addItem(item)
   │
   ├─► se Optional.empty → ignorados++ → continue
   │
   ├─► repo.existsByChaveAcesso(chave)?
   │      ├─► SIM → duplicados++ → continue
   │      └─► NÃO → repo.save(cupom) → importados++
   │
   v
FIM LOOP → retorna ResultadoImportacao
   │
   v
ConsoleApp imprime resumo → volta ao MENU

┌─────────────────────────────────────────────────────────────────────┐
│ 4) OPÇÕES 2/3/4 — CONSULTAS                                           │
└─────────────────────────────────────────────────────────────────────┘
(2) listar por número
   ConsoleApp → ConsultarCuponsService → repo.findAll(Sort ASC numeroCFe)
   → imprime resumo

(3) listar por valor
   ConsoleApp → ConsultarCuponsService → repo.findAll(Sort DESC valorTotal)
   → imprime resumo

(4) relatório detalhado (cadeia)
   ConsoleApp → ConsultarCuponsService → repo.findAllOrderByChaveComItens()
   (join fetch itens)
   → imprime Cupom -> Itens
```

------------------------------------------------------------------------

## 🔐 Segurança

Parser DOM com proteção contra XXE:

``` java
dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
```

------------------------------------------------------------------------

## 🗄 Banco de Dados

Configurado como H2 em modo arquivo:

    jdbc:h2:file:./data/cfsat;MODE=PostgreSQL;AUTO_SERVER=TRUE

Console H2 disponível em:

    http://localhost:8080/h2-console

------------------------------------------------------------------------

## ▶️ Como Executar

``` bash
mvn spring-boot:run
```

ou

``` bash
./mvnw spring-boot:run
```

------------------------------------------------------------------------

## 🧪 Menu da Aplicação

    === CF-e SAT Importer ===
    1) Importar XMLs de uma pasta
    2) Listar cupons (ordenado por NÚMERO)
    3) Listar cupons (ordenado por VALOR TOTAL)
    4) Relatório detalhado (Cupom -> Itens)
    0) Sair

------------------------------------------------------------------------

## 👨‍💻 Autor

Wesley Silva
