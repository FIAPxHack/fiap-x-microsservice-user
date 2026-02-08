# language: pt
Funcionalidade: Gerenciamento de Usuários
  Como um administrador do sistema
  Eu quero gerenciar usuários
  Para controlar o acesso ao sistema

  Contexto:
    Dado que o banco de dados está limpo

  Cenário: Criar um novo usuário com sucesso
    Dado que eu tenho os seguintes dados de usuário:
      | nome            | João Silva              |
      | email           | joao.silva@example.com  |
      | senha           | senha123                |
      | dataNascimento  | 1990-01-15              |
      | telefone        | 11987654321             |
      | role            | 2                       |
    Quando eu criar o usuário
    Então o usuário deve ser criado com sucesso
    E o usuário deve ter um ID gerado
    E o usuário deve ter o nome "João Silva"
    E o usuário deve ter o email "joao.silva@example.com"

  Cenário: Buscar usuário por ID existente
    Dado que existe um usuário cadastrado com:
      | nome            | Maria Santos            |
      | email           | maria@example.com       |
      | senha           | senha456                |
      | dataNascimento  | 1985-05-20              |
      | telefone        | 11976543210             |
      | role            | 1                       |
    Quando eu buscar o usuário por ID
    Então o usuário deve ser encontrado
    E o usuário deve ter o nome "Maria Santos"
    E o usuário deve ter o email "maria@example.com"

  Cenário: Buscar usuário por ID inexistente
    Quando eu buscar um usuário com ID inexistente
    Então o usuário não deve ser encontrado

  Cenário: Atualizar dados de um usuário existente
    Dado que existe um usuário cadastrado com:
      | nome            | Pedro Oliveira          |
      | email           | pedro@example.com       |
      | senha           | senha789                |
      | dataNascimento  | 1992-08-10              |
      | telefone        | 11965432109             |
      | role            | 2                       |
    E eu tenho os novos dados:
      | nome            | Pedro Oliveira Silva    |
    Quando eu atualizar o usuário
    Então o usuário deve ser atualizado com sucesso
    E o usuário deve ter o nome "Pedro Oliveira Silva"

  Cenário: Deletar um usuário existente (soft delete)
    Dado que existe um usuário cadastrado com:
      | nome            | Ana Costa               |
      | email           | ana@example.com         |
      | senha           | senha321                |
      | dataNascimento  | 1988-03-25              |
      | telefone        | 11954321098             |
      | role            | 2                       |
    Quando eu deletar o usuário
    Então o usuário deve ser marcado como deletado
    E o usuário deve ter a flag deleted como true

  Cenário: Listar todos os usuários com paginação
    Dado que existem os seguintes usuários cadastrados:
      | nome              | email                  | senha    | dataNascimento | telefone    | role |
      | Carlos Eduardo    | carlos@example.com     | senha111 | 1991-07-12     | 11943210987 | 1    |
      | Juliana Almeida   | juliana@example.com    | senha222 | 1993-11-30     | 11932109876 | 2    |
      | Roberto Ferreira  | roberto@example.com    | senha333 | 1987-02-18     | 11921098765 | 2    |
    Quando eu listar os usuários da página 0 com tamanho 10
    Então devo receber 3 usuários
    E o total de itens deve ser 3
    E o número de páginas deve ser 1

  Cenário: Validar roles de usuário
    Dado que eu tenho os seguintes dados de usuário:
      | nome            | Admin User              |
      | email           | admin@example.com       |
      | senha           | admin123                |
      | dataNascimento  | 1980-01-01              |
      | telefone        | 11900000000             |
      | role            | 1                       |
    Quando eu criar o usuário
    Então o usuário deve ter o role ADMIN

  Cenário: Criar usuário com role SYSTEM
    Dado que eu tenho os seguintes dados de usuário:
      | nome            | System User             |
      | email           | system@example.com      |
      | senha           | system123               |
      | dataNascimento  | 1975-06-15              |
      | telefone        | 11911111111             |
      | role            | 0                       |
    Quando eu criar o usuário
    Então o usuário deve ter o role SYSTEM

  Cenário: Listar usuários em página vazia
    Quando eu listar os usuários da página 10 com tamanho 10
    Então devo receber 0 usuários
    E o total de itens deve ser 0
    E o número de páginas deve ser 0

  Cenário: Atualizar usuário inexistente deve falhar
    Dado que eu tenho um ID de usuário inexistente
    E eu tenho os novos dados:
      | nome            | Nome Atualizado         |
    Quando eu tentar atualizar o usuário
    Então deve ocorrer um erro de usuário não encontrado

  Cenário: Deletar usuário inexistente deve falhar
    Dado que eu tenho um ID de usuário inexistente
    Quando eu tentar deletar o usuário
    Então deve ocorrer um erro de usuário não encontrado

