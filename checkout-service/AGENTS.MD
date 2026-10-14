# AGENTS.MD - Padrões de Arquitetura e Desenvolvimento

Este documento define os padrões técnicos, convenções e diretrizes de design obrigatórios para o desenvolvimento do **Sistema de Checkout e Reserva de Estoque Relâmpago (Flash Sale)**.

---

## 1. Visão Geral da Tech Stack

* **Linguagem:** Java 21 (LTS)
* **Framework:** Quarkus 3.x
* **Gerenciamento de Dependências:** Maven
* **Persistência Relacional:** Quarkus Hibernate ORM com Panache (Padrão Repository)
* **Cache & In-Memory Storage:** Quarkus Redis Client (Mutiny / Programmatic API)
* **Mensageria:** Quarkus SmallRye Reactive Messaging (RabbitMQ connector)
* **Mapeamento de Objetos:** MapStruct
* **Validação:** Jakarta Bean Validation
* **Banco de Dados Relacional:** PostgreSQL (Bancos segregados por serviço)
* **Documentação de API:** OpenAPI / SmallRye OpenAPI

---

## 2. Padrões DDD e Estrutura de Pastas (Clean Architecture)

A aplicação segue a divisão em camadas orientada ao DDD (*Domain-Driven Design*). Cada microsserviço possui o seguinte layout de pacotes:

```text
br.com.flashsale.checkout-service
├── domain
│   ├── exceptions/         # Exceções de negócio (ex: InsufficientStockException)
│   ├── model/              # Entidades, Value Objects e Enums do domínio
│   └── repository/         # Interfaces de repositório do domínio (Portas de Saída)
├── application
│   ├── dto/                # Data Transfer Objects (Requests/Responses)
│   ├── mapper/             # Interfaces MapStruct
│   ├── service/            # Casos de uso e serviços da aplicação
│   └── consumer/           # Consumidores de filas/eventos (Entrypoints assíncronos)
└── infrastructure
    ├── persistence/        # Implementações Panache Repository, Entities JPA
    ├── redis/              # Scripts Lua, Clientes e adaptadores Redis
    ├── messaging/          # Producers e conectores RabbitMQ
    └── rest/               # Controladores JAX-RS (Endpoints REST)