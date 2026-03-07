# FIAP X - Microsserviço de Usuário

[![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?logo=github-actions&logoColor=white)](https://github.com/FIAPxHack/fiap-x-microsservice-user/actions)
[![SonarCloud](https://img.shields.io/badge/Quality-SonarCloud-F3702A?logo=sonarcloud&logoColor=white)](https://sonarcloud.io/project/overview?id=FIAPxHack_fiap-x-microsservice-user)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Microsserviço responsável pelo gerenciamento de usuários do sistema FIAP X, desenvolvido com **Kotlin**, **Spring Boot** e seguindo princípios de **Clean Architecture**.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação e Execução](#-instalação-e-execução)
- [Endpoints da API](#-endpoints-da-api)
- [Testes](#-testes)
- [CI/CD](#-cicd)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Contribuição](#-contribuição)
- [Licença](#-licença)

---

## 🎯 Sobre o Projeto

O **fiap-x-microsservice-user** é um microsserviço RESTful que gerencia o ciclo de vida completo de usuários, incluindo operações de CRUD (Create, Read, Update, Delete), autenticação e autorização por perfis.

### Funcionalidades Principais

- ✅ Busca de usuário por email (integração com microsserviço de autenticação)
- ✅ Consulta de usuários (individual e paginada)
- ✅ Atualização de dados de usuário
- ✅ Exclusão lógica (soft delete) de usuários
- ✅ Sistema de perfis/roles (SYSTEM, ADMIN, USER)
- ✅ Validação de email único
- ✅ Auditoria com rastreamento de criação e atualização

---

## 🏗️ Arquitetura

O projeto segue os princípios da **Clean Architecture** (Arquitetura Limpa), dividindo o código em camadas bem definidas:

```
┌─────────────────────────────────────────┐
│          Adapter Layer (API)            │  ← Controllers, DTOs, Mappers
├─────────────────────────────────────────┤
│       Application Layer (Use Cases)     │  ← Business Logic, Commands, Queries
├─────────────────────────────────────────┤
│          Domain Layer (Core)            │  ← Entities, Value Objects, Interfaces
├─────────────────────────────────────────┤
│    Infrastructure Layer (External)      │  ← Database, JPA, Repositories
└─────────────────────────────────────────┘
```

### Camadas

- **Adapter**: Controllers REST, Request/Response DTOs, Mappers
- **Application**: Use Cases (Commands e Queries) - lógica de aplicação
- **Domain**: Entidades de domínio, enums, interfaces de repositório
- **Infrastructure**: Implementação de persistência (JPA), configurações, adapters

---

## 🚀 Tecnologias

### Core
- **[Kotlin](https://kotlinlang.org/)** 2.2.0 - Linguagem de programação
- **[Spring Boot](https://spring.io/projects/spring-boot)** 4.0.2 - Framework base
- **[Java](https://www.oracle.com/java/)** 17 (Temurin JRE)

### Persistência
- **[Spring Data JPA](https://spring.io/projects/spring-data-jpa)** - Abstração de dados
- **[Hibernate](https://hibernate.org/)** - ORM
- **[PostgreSQL](https://www.postgresql.org/)** 16 - Banco de dados
- **[Flyway](https://flywaydb.org/)** - Migrations de banco de dados

### Testes
- **[JUnit 5](https://junit.org/junit5/)** - Framework de testes
- **[MockK](https://mockk.io/)** 1.13.13 - Mocking para Kotlin
- **[Cucumber](https://cucumber.io/)** 7.18.1 - BDD (Behavior-Driven Development)
- **[Testcontainers](https://www.testcontainers.org/)** 1.19.3 - Testes de integração com containers
- **[JaCoCo](https://www.jacoco.org/jacoco/)** - Cobertura de código

### Qualidade e DevOps
- **[SonarCloud](https://sonarcloud.io/)** - Análise estática de código
- **[GitHub Actions](https://github.com/features/actions)** - CI/CD
- **[Docker](https://www.docker.com/)** - Containerização
- **[Docker Compose](https://docs.docker.com/compose/)** - Orquestração local

### Documentação
- **[SpringDoc OpenAPI](https://springdoc.org/)** 2.6.0 - Documentação Swagger/OpenAPI 3

---

## 📦 Pré-requisitos

Antes de começar, você vai precisar ter instalado em sua máquina:

- **[Docker](https://www.docker.com/get-started)** 20+ e **Docker Compose** 2+
- **[Java JDK](https://adoptium.net/)** 17+ (opcional, apenas para desenvolvimento local sem Docker)
- **[Maven](https://maven.apache.org/)** 3.9+ (incluído via Maven Wrapper)
- **[Git](https://git-scm.com/)** (para clonar o repositório)

---

## 🔧 Instalação e Execução

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/FIAPxHack/fiap-x-microsservice-user.git
cd fiap-x-microsservice-user
```

### 2️⃣ Configurar Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto (copie do `.env.example` se disponível):

```bash
# Banco de Dados PostgreSQL
POSTGRES_USER=fiapx_user
POSTGRES_PASSWORD=fiapx_pass_123
POSTGRES_DB=fiapx_user_db

# Spring Datasource
SPRING_DATASOURCE_URL=jdbc:postgresql://db-fiap-x-user:5432/fiapx_user_db
```

### 3️⃣ Executar com Docker Compose (Recomendado)

```bash
# Subir todos os serviços (banco + aplicação)
docker compose up -d

# Verificar logs
docker compose logs -f app-fiap-x-user
```

A aplicação estará disponível em: **http://localhost:8081**

### 4️⃣ Executar Localmente (Sem Docker)

#### Passo 1: Subir apenas o banco de dados
```bash
docker compose up -d db-fiap-x-user
```

#### Passo 2: Executar a aplicação com Maven
```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### 5️⃣ Verificar Saúde da Aplicação

```bash
# Health check
curl http://localhost:8081/actuator/health

# Informações da aplicação
curl http://localhost:8081/actuator/info
```

---

## 📡 Endpoints da API

### Base URL
```
http://localhost:8081/api/users
```

### Documentação Interativa (Swagger UI)
Acesse: **http://localhost:8081/swagger-ui.html**

### Endpoints Principais

| Método | Endpoint                      | Descrição                    | Body                          |
|--------|-------------------------------|------------------------------|-------------------------------|
| POST   | `/api/users`                  | Criar novo usuário           | `CreateUserRequest`           |
| GET    | `/api/users`                  | Listar todos (paginado)      | Query params: page, size      |
| GET    | `/api/users/{id}`             | Buscar usuário por ID        | -                             |
| GET    | `/api/users/by-email/{email}` | Buscar usuário por email     | -                             |
| PUT    | `/api/users/{id}`             | Atualizar usuário            | `UpdateUserRequest`           |
| DELETE | `/api/users/{id}`             | Deletar usuário (soft)       | -                             |

### Exemplos de Requisições

#### Criar Usuário
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao.silva@example.com",
    "password": "senha123",
    "birthDate": "1990-01-15",
    "phone": "11987654321",
    "role": 2
  }'
```

#### Listar Usuários (Paginado)
```bash
curl "http://localhost:8081/api/users?page=0&size=10"
```

#### Buscar por ID
```bash
curl http://localhost:8081/api/users/by-email/joao.silva@example.com
```

#### Atualizar Usuário
```bash
curl -X PUT http://localhost:8081/api/users/{uuid} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva Santos",
    "phone": "11999887766"
  }'
```

#### Deletar Usuário
```bash
curl -X DELETE http://localhost:8081/api/users/{uuid}
```

### Estrutura de Response

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "João Silva",
  "email": "joao.silva@example.com",
  "birthDate": "1990-01-15",
  "phone": "11987654321",
  "role": 2,
  "createdAt": "2026-02-16T10:30:00",
  "createdBy": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "updatedAt": null,
  "updatedBy": null,
  "deleted": false
}
```

### User Roles (Perfis)

| Código | Role     | Descrição                        |
|--------|----------|----------------------------------|
| 0      | SYSTEM   | Usuário do sistema (interno)     |
| 1      | ADMIN    | Administrador                    |
| 2      | USER     | Usuário padrão                   |

---

## 🧪 Testes

O projeto possui cobertura completa de testes em 3 níveis:

- **Unitários**: Use Cases, Mappers, Controllers
- **Integração**: Testes com Testcontainers (PostgreSQL real)
- **BDD**: Cucumber com cenários em Gherkin (pt-BR)

### Executar Todos os Testes

```bash
# Linux/Mac
./mvnw clean verify

# Windows
mvnw.cmd clean verify
```

### Executar Apenas Testes Unitários

```bash
./mvnw test
```

### Executar Apenas Testes BDD (Cucumber)

```bash
./mvnw test -Dtest=CucumberRunnerTest
```

### Relatório de Cobertura (JaCoCo)

Após executar os testes, abra o relatório em:
```
target/site/jacoco/index.html
```

### Cenários BDD Implementados

Os testes BDD estão em `src/test/resources/features/user-management.feature`:

- ✅ Criar usuário com sucesso
- ✅ Buscar usuário por ID existente
- ✅ Buscar usuário por ID inexistente
- ✅ Atualizar dados de usuário
- ✅ Deletar usuário (soft delete)
- ✅ Listar usuários com paginação
- ✅ Validações de campos obrigatórios
- ✅ Validação de email duplicado

---

## 🔄 CI/CD

O projeto possui pipeline automatizado no **GitHub Actions** com as seguintes etapas:

### Pipeline - Entrega Contínua (`main` branch)

```yaml
1. Build e Testes
   ├── Checkout do código
   ├── Setup JDK 17 (Temurin)
   ├── Cache de dependências Maven
   ├── Execução de testes (mvn verify)
   └── Relatório de cobertura JaCoCo

2. Análise SonarCloud
   ├── Análise estática de código
   ├── Verificação de cobertura de testes
   ├── Detecção de code smells
   ├── Verificação de vulnerabilidades
   └── Quality Gate

3. Build e Push Docker
   ├── Build da imagem Docker
   ├── Push para GitHub Container Registry (GHCR)
   ├── Tag: ghcr.io/fiapxhack/fiap-x-microsservice-user:latest
   └── Assinatura com Cosign (Sigstore)
```

### Badges de Status

[![CI/CD](https://github.com/FIAPxHack/fiap-x-microsservice-user/actions/workflows/cd-delivery-main.yml/badge.svg)](https://github.com/FIAPxHack/fiap-x-microsservice-user/actions)

---

## 📁 Estrutura do Projeto

```
fiap-x-microsservice-user/
├── .github/
│   └── workflows/
│       └── cd-delivery-main.yml          # Pipeline CI/CD
├── src/
│   ├── main/
│   │   ├── kotlin/br/com/fiapx/fiapxuser/
│   │   │   ├── adapter/                  # Camada de adaptadores (Controllers, DTOs)
│   │   │   │   ├── controller/           # REST Controllers
│   │   │   │   ├── request/              # Request DTOs
│   │   │   │   ├── response/             # Response DTOs
│   │   │   │   └── mapper/               # Mappers Adapter <-> Domain
│   │   │   ├── application/              # Camada de aplicação (Use Cases)
│   │   │   │   └── usecase/
│   │   │   │       ├── commands/         # Commands (Create, Update, Delete)
│   │   │   │       └── queries/          # Queries (GetAll, GetById)
│   │   │   ├── domain/                   # Camada de domínio (Core)
│   │   │   │   ├── model/                # Entidades de domínio
│   │   │   │   ├── repository/           # Interfaces de repositório
│   │   │   │   ├── enums/                # Enumerações (UserRole)
│   │   │   │   └── common/               # Classes base (BaseEntity, Paged)
│   │   │   └── infrastructure/           # Camada de infraestrutura
│   │   │       ├── config/               # Configurações Spring
│   │   │       └── persistence/          # JPA, Entities, Adapters
│   │   │           ├── entity/           # JPA Entities
│   │   │           ├── repository/       # Spring Data Repositories
│   │   │           ├── adapter/          # Implementação de repositórios
│   │   │           └── mapper/           # Mappers Entity <-> Domain
│   │   └── resources/
│   │       ├── application.yml           # Configuração principal
│   │       ├── application-local.yml     # Configuração desenvolvimento local
│   │       └── db/migration/             # Flyway migrations
│   │           └── V1__create_user.sql
│   └── test/
│       ├── kotlin/br/com/fiapx/fiapxuser/
│       │   ├── adapter/                  # Testes de Controllers e Mappers
│       │   ├── application/              # Testes de Use Cases
│       │   └── bdd/                      # Testes BDD (Cucumber)
│       │       ├── config/               # Configuração Cucumber/Spring
│       │       └── steps/                # Step Definitions (Gherkin)
│       └── resources/
│           └── features/                 # Arquivos .feature (Gherkin)
│               └── user-management.feature
├── docker-compose.yml                    # Orquestração de containers
├── Dockerfile                            # Multi-stage build
├── pom.xml                               # Dependências Maven
├── sonar-project.properties              # Configuração SonarCloud
├── .env                                  # Variáveis de ambiente (não versionado)
├── LICENSE                               # Licença MIT
└── README.md                             # Este arquivo
```

---

## 🔐 Variáveis de Ambiente

### Banco de Dados

| Variável                     | Descrição                          | Exemplo                                              |
|------------------------------|------------------------------------|------------------------------------------------------|
| `POSTGRES_USER`              | Usuário do PostgreSQL              | `fiapx_user`                                         |
| `POSTGRES_PASSWORD`          | Senha do PostgreSQL                | `fiapx_pass_123`                                     |
| `POSTGRES_DB`                | Nome do banco de dados             | `fiapx_user_db`                                      |
| `SPRING_DATASOURCE_URL`      | JDBC URL de conexão                | `jdbc:postgresql://db-fiap-x-user:5432/fiapx_user_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuário Spring (igual POSTGRES_USER) | `fiapx_user`                                       |
| `SPRING_DATASOURCE_PASSWORD` | Senha Spring (igual POSTGRES_PASSWORD) | `fiapx_pass_123`                                |

### Logging (Opcional)

| Variável                   | Padrão  | Descrição                     |
|----------------------------|---------|-------------------------------|
| `LOGGING_LEVEL_ROOT`       | `INFO`  | Nível raiz de log             |
| `LOGGING_LEVEL_SPRING`     | `INFO`  | Logs do Spring Framework      |
| `LOGGING_LEVEL_SQL`        | `INFO`  | Logs SQL (Hibernate)          |
| `LOGGING_LEVEL_FIAP_X_USER`| `DEBUG` | Logs da aplicação             |

### CI/CD (GitHub Secrets)

| Secret              | Descrição                              |
|---------------------|----------------------------------------|
| `SONAR_TOKEN`       | Token de autenticação SonarCloud       |
| `SONAR_HOST_URL`    | URL do SonarCloud (`https://sonarcloud.io`) |
| `GITHUB_TOKEN`      | Token automático (push GHCR)           |

---

## 🤝 Contribuição

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'feat: Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

### Padrão de Commits

Utilizamos [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Nova funcionalidade
- `fix:` Correção de bug
- `docs:` Documentação
- `test:` Testes
- `refactor:` Refatoração de código
- `chore:` Tarefas de build/CI

---

## 📄 Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👥 Autores

**FIAPxHack Team**

- GitHub: [@FIAPxHack](https://github.com/FIAPxHack)

---

## 📞 Suporte

Para reportar bugs ou solicitar features:

- Abra uma [Issue](https://github.com/FIAPxHack/fiap-x-microsservice-user/issues)
- Entre em contato via [Discussions](https://github.com/FIAPxHack/fiap-x-microsservice-user/discussions)

---

## 🔗 Links Úteis

- [Documentação Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Kotlin Reference](https://kotlinlang.org/docs/reference/)
- [Clean Architecture (Uncle Bob)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Cucumber BDD](https://cucumber.io/docs/guides/)
- [Testcontainers](https://www.testcontainers.org/quickstart/junit_5_quickstart/)

---

<div align="center">
  
**⭐ Se este projeto foi útil, considere dar uma estrela!**

Made with ❤️ by FIAPxHack Team

</div>

