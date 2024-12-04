# Sistema de Pedidos - Microsserviços

Este projeto implementa um sistema de pedidos utilizando arquitetura de microsserviços. O sistema possui microsserviços separados para o gerenciamento de clientes, produtos e pedidos.

## Estrutura do Projeto

O projeto consiste em três principais microsserviços:

- **Cliente Service**: Gerencia informações de clientes.
- **Produto Service**: Gerencia informações sobre produtos e o estoque.
- **Pedido Service**: Gerencia os pedidos realizados pelos clientes.

A comunicação entre os microsserviços é feita via chamadas HTTP utilizando o Feign Client, um cliente HTTP declarativo para facilitar a integração entre os serviços.

## Dependências e Tecnologias

- **Spring Boot**: Framework para a construção dos microsserviços.
- **PostgreSQL**: Banco de dados utilizado para persistência.
- **Feign Client**: Usado para fazer chamadas HTTP para outros microsserviços.
- **JPA (Java Persistence API)**: Para persistência de dados.
- **Swagger**: Documentação da API.
- **Hibernate**: Para mapeamento objeto-relacional (ORM).
- **Spring Data**: Para interações com o banco de dados.
- **Spring Web**: Para a construção das APIs REST.

## Como Rodar o Projeto

### Requisitos

- JDK 17 ou superior
- PostgreSQL em execução no localhost
- Docker (se preferir rodar o banco de dados via Docker)

### Configuração do Banco de Dados

Certifique-se de que o banco de dados PostgreSQL está rodando localmente e que a URL de conexão no arquivo `application.properties` está configurada corretamente.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fase4
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.platform=postgres

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

Iniciando os Microsserviços

Cliente Service: Inicie o microsserviço responsável pelos clientes (normalmente na porta 8081).

Produto Service: Inicie o microsserviço responsável pelos produtos (normalmente na porta 8082).

Pedido Service: Inicie o microsserviço de pedidos (normalmente na porta 8080).

Banco de Dados

O banco de dados utilizado é o PostgreSQL. A configuração está no arquivo application.properties, e a estrutura do banco é automaticamente gerenciada pelo Spring JPA, com o hibernate.ddl-auto configurado para update.

Endpoints Principais

Pedido Service

POST /pedidos: Cria um novo pedido.

Corpo da requisição:

{
  "clienteId": 1,
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 2
    }
  ]
}

GET /pedidos/{id}: Obtém detalhes de um pedido pelo ID, incluindo o total calculado do pedido.

GET /pedidos: Obtém todos os pedidos com seus respectivos totais.

DELETE /pedidos/{id}: Exclui um pedido pelo ID.

Produto Service

GET /produtos/{id}: Obtém detalhes de um produto pelo ID.

PUT /produtos/verificar-estoque/{id}/quantidade/{quantidade}: Atualiza o estoque de um produto.

Cliente Service

GET /clientes/{id}: Obtém os detalhes de um cliente pelo ID.

Estrutura de Entidades

Pedido

Um pedido é composto por um clienteId, uma lista de itens, que contém produtos e quantidades.

O pedido tem um status inicial de "Aguardando atribuição" e uma data de criação.

ItemPedido

Cada item do pedido contém um produtoId e a quantidade do produto.

ClienteDTO

Contém apenas o id do cliente.

ProdutoDTO

Contém informações sobre o produto, como nome, descricao, preco, quantidadeestoque e createdatetime.

Arquitetura de Microsserviços

Os microsserviços são independentes e comunicam-se entre si via REST:


O Pedido Service faz requisições para o Produto Service para verificar a disponibilidade de estoque e para o Cliente Service para validar o cliente.

O Produto Service verifica e atualiza o estoque de produtos quando um pedido é realizado.

O Cliente Service valida a existência de um cliente ao realizar um pedido.

Exemplo de Fluxo de Pedido

O cliente realiza um pedido via Pedido Service.

O sistema verifica o estoque de cada item do pedido com o Produto Service.

Se o estoque for suficiente, o pedido é confirmado e o estoque é atualizado.

O sistema valida se o cliente existe através do Cliente Service.

O pedido é salvo no banco de dados e a resposta é enviada ao cliente.