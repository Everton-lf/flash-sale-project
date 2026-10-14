# Flash Sale Project

Projeto com microsserviços em Java + Quarkus, usando PostgreSQL, Redis e RabbitMQ com Docker.

Este guia mostra como subir o ambiente em qualquer máquina com Docker, incluindo:
- Docker Desktop (Windows/macOS)
- Docker CLI (Linux e Windows/macOS via terminal)
- ambiente local com `docker compose`

## Requisitos

- Docker Desktop ou Docker Engine instalado
- Docker Compose v2 (`docker compose`)
- Git
- 4GB+ de RAM recomendados
- Porta livre:
  - 5432 (PostgreSQL)
  - 6379 (Redis)
  - 5672 / 15672 (RabbitMQ)
  - 8080 (checkout-service)
  - 8081 (order-processor-service)

## Estrutura do projeto

- `checkout-service/` – microsserviço de checkout
- `order-processor-service/` – microsserviço de processamento do pedido
- `docker-compose.yml` – orquestração dos serviços
- `init-db.sql` – criação dos bancos PostgreSQL

## 1) Clonar o projeto

```bash
git clone https://github.com/<seu-usuario>/flash-sale-project.git
cd flash-sale-project
```

## 2) Verificar Docker

### Windows/macOS com Docker Desktop

```bash
docker --version
docker compose version
```

### Linux

```bash
docker --version
docker compose version
```

Se o `docker compose` não existir, use:

```bash
sudo apt-get update
sudo apt-get install docker-compose-plugin
```

ou instale a versão do Docker com Compose habilitado.

## 3) Subir todos os serviços

Na raiz do projeto:

```bash
docker compose up --build -d
```

Esse comando:
- baixa as imagens necessárias
- constrói os serviços locais
- inicia PostgreSQL, Redis, RabbitMQ, checkout e order-processor

## 4) Verificar se todos estão rodando

```bash
docker compose ps
```

Você deve ver algo como:
- `redis` -> `running`
- `rabbitmq` -> `running`
- `postgres` -> `running`
- `checkout-service` -> `running`
- `order-processor-service` -> `running`

## 5) Ver logs dos serviços

```bash
docker compose logs -f
```

Ou por serviço:

```bash
docker compose logs -f checkout-service
docker compose logs -f order-processor-service
```

## 6) Testar o fluxo de pedido

### Definir estoque inicial no Redis

```bash
docker exec -it flashsale-redis redis-cli SET stock:PROD-001 10
```

### Fazer pedido

#### PowerShell / Windows

```powershell
$body = '{"produto_id":"PROD-001","quantidade":2,"cliente_cpf":"50329291076","cliente_nome":"Fernando Augusto"}'
Invoke-RestMethod -Uri "http://localhost:8080/v1/orders" -Method Post -ContentType "application/json" -Body $body
```

#### Bash / Linux/macOS

```bash
curl -X POST http://localhost:8080/v1/orders \
  -H "Content-Type: application/json" \
  -d '{"produto_id":"PROD-001","quantidade":2,"cliente_cpf":"50329291076","cliente_nome":"Fernando Augusto"}'
```

### Verificar estoque após o pedido

```bash
docker exec -it flashsale-redis redis-cli GET stock:PROD-001
```

Resultado esperado:

```text
8
```

Isso confirma que a reserva do estoque funcionou corretamente.

## 7) Parar e remover tudo

### Parar os serviços

```bash
docker compose stop
```

### Parar e remover containers/networks/volumes

```bash
docker compose down --volumes
```

### Rebuild completo (limpando imagens do compose)

```bash
docker compose down --rmi all --volumes --remove-orphans
docker compose up --build -d
```

## 8) Reiniciar tudo do zero

Se quiser resetar o ambiente completo:

```bash
docker compose down --rmi all --volumes --remove-orphans
docker compose up --build -d
```

## 9) Dicas para Docker Desktop e Docker CLI

### Docker Desktop

- Abra o Docker Desktop antes de rodar os comandos
- Caso a porta estiver em uso, verifique se outra aplicação não está usando 8080, 5432, 6379 ou 5672
- O ambiente funciona a partir do terminal do sistema operacional

### Docker CLI / Linux

- Certifique-se de que o Docker daemon está rodando:

```bash
sudo systemctl status docker
```

- Se estiver parado:

```bash
sudo systemctl start docker
```

## 10) Problemas comuns

### Porta ocupada

```bash
docker ps
```

Veja quais containers estão ativos e identifique o conflito. Depois pare o processo que usa a porta ou altere as portas no arquivo `docker-compose.yml`.

### Serviço não subiu

```bash
docker compose logs -f <servico>
```

Exemplo:

```bash
docker compose logs -f checkout-service
```

### Redis não responde

```bash
docker exec -it flashsale-redis redis-cli ping
```

Resultado esperado:

```text
PONG
```

### PostgreSQL não responde

```bash
docker exec -it flashsale-postgres pg_isready -U root
```

### RabbitMQ não responde

```bash
docker exec -it flashsale-rabbitmq rabbitmq-diagnostics -q ping
```

## 11) Observações importantes

- O projeto usa `docker compose` e não depende de IDE específica
- O build usa Maven dentro dos containers para gerar os artefatos dos serviços
- O Redis é usado para controle de estoque em tempo real
- O RabbitMQ é usado para comunicação assíncrona entre serviços
- O PostgreSQL é separado em dois bancos:
  - `db_checkout`
  - `db_order_processor`

## 12) Fluxo de execução esperado

1. O `checkout-service` inicia
2. O `order-processor-service` inicia
3. O Redis recebe o estoque inicial
4. Você chama `POST /v1/orders`
5. O checkout reserva estoque em Redis
6. O pedido é salvo no banco `db_checkout`
7. O evento de pedido é enviado para RabbitMQ
8. O `order-processor-service` recebe a mensagem
9. O pedido é persistido em `db_order_processor`
10. O estoque fica reduzido no Redis

## 13) Comando rápido de referência

```bash
docker compose up --build -d
docker compose ps
docker compose logs -f
docker compose down --volumes
```

## 14) Conclusão

Com os passos acima, o projeto deve subir e funcionar em qualquer máquina com Docker instalado, tanto em Docker Desktop quanto em Docker CLI no Linux.

Se algum serviço falhar, use `docker compose logs -f` para diagnosticar e confira se as portas e dependências estão disponíveis.
