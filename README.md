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
