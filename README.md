# Calculadora de Calorias

Uma API backend robusta para rastreamento e cálculo de calorias com arquitetura MVC funcional construída em **Clojure**.

[![License: EPL 2.0](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://www.eclipse.org/legal/epl-2.0/)
[![Clojure](https://img.shields.io/badge/Clojure-1.11.1-brightgreen.svg)](https://clojure.org)

## Descrição

Calculadora de Calorias é uma API REST que permite gerenciar o perfil de usuários e rastrear transações de calorias, incluindo refeições e exercícios. A aplicação integra-se com APIs externas para obter dados nutricionais e de queima de calorias de exercícios.

### Principais Características

-  **Gerenciamento de Perfil**: Armazene e atualize informações pessoais do usuário (peso, altura, idade, sexo)
-  **Registro de Refeições**: Registre alimentos consumidos com cálculo automático de calorias
-  **Registro de Exercícios**: Registre exercícios realizados com cálculo de calorias gastas
-  **Histórico de Transações**: Consulte todas as transações de calorias com filtros por período
-  **Resumo Diário**: Visualize um resumo consolidado com calorias consumidas, gastas e saldo

## Tecnologias

- **Linguagem**: [Clojure](https://clojure.org) 1.11.1
- **Framework Web**: [Ring](https://github.com/ring-clojure/ring) 1.9.6
- **Roteamento**: [Compojure](https://github.com/weavejester/compojure) 1.7.0
- **Servidor**: Jetty (via Ring)
- **Serialização JSON**: [Cheshire](https://github.com/dakrone/cheshire) 5.10.0
- **Cliente HTTP**: [clj-http](https://github.com/dakrone/clj-http) 3.12.3
- **Gerenciador de Dependências**: [Leiningen](https://leiningen.org)

## Pré-requisitos

- [Java](https://www.java.com) (JDK 11+)
- [Clojure](https://clojure.org) 1.11.1+
- [Leiningen](https://leiningen.org) (gerenciador de projetos Clojure)

## Instalação

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/seu-usuario/calculadora-calorias.git
   cd calculadora-calorias
   ```

2. **Baixe as dependências**:
   ```bash
   lein deps
   ```

3. **Inicie o servidor**:
   ```bash
   lein run
   ```

   O servidor estará disponível em `http://localhost:3000`

## Uso da API

### Endpoints Disponíveis

#### 1. **Atualizar Perfil do Usuário**
```http
POST /api/usuario
Content-Type: application/json

{
  "nome": "João Silva",
  "altura": 1.75,
  "peso": 80,
  "idade": 30,
  "sexo": "M"
}
```

**Resposta** (200 OK):
```json
{
  "mensagem": "Perfil atualizado com sucesso!",
  "usuario": {
    "nome": "João Silva",
    "altura": 1.75,
    "peso": 80,
    "idade": 30,
    "sexo": "M"
  }
}
```

---

#### 2. **Registrar Transação (Refeição ou Exercício)**
```http
POST /api/transacoes
Content-Type: application/json

{
  "tipo": "refeicao",
  "nome": "Frango com Arroz",
  "data": "15-06-2026",
  "quantidade": 250
}
```

**Parâmetros**:
- `tipo` (obrigatório): `"refeicao"` ou `"exercicio"`
- `nome` (obrigatório): Nome do alimento ou exercício
- `data` (obrigatório): Data no formato `DD-MM-YYYY`
- `quantidade` (para refeições): Quantidade em gramas
- `duracao` (para exercícios): Duração em minutos

**Resposta** (201 Created):
```json
{
  "mensagem": "Alimento 'Frango com Arroz' registrado.",
  "calorias": 325.5
}
```

---

#### 3. **Listar Transações**
```http
GET /api/transacoes
GET /api/transacoes?inicio=01-06-2026&fim=30-06-2026
```

**Resposta** (200 OK):
```json
[
  {
    "tipo": "refeicao",
    "nome": "Frango com Arroz",
    "calorias": 325.5,
    "data": "15-06-2026"
  },
  {
    "tipo": "exercicio",
    "nome": "Corrida",
    "calorias": 450.0,
    "data": "15-06-2026"
  }
]
```

---

#### 4. **Obter Resumo**
```http
GET /api/resumo
GET /api/resumo?inicio=01-06-2026&fim=30-06-2026
```

**Resposta** (200 OK):
```json
{
  "usuario": "João Silva",
  "consumidas": 325.5,
  "gastas": 450.0,
  "saldo-atual": -124.5,
  "total-transacoes": 2
}
```

## Estrutura do Projeto

```
calculadora-calorias/
├── src/
│   ├── calculadora_calorias/
│   │   ├── core.clj                 # Ponto de entrada da aplicação
│   │   ├── routes.clj               # Definição de rotas e middleware
│   │   ├── api/
│   │   │   ├── conexao.clj         # Integração com APIs externas
│   │   │   └── tratamento.clj      # Processamento de dados das APIs
│   │   ├── controllers/
│   │   │   └── handlers.clj        # Manipuladores de requisições
│   │   ├── models/
│   │   │   └── database.clj        # Armazenamento em memória (atoms)
│   │   ├── services/
│   │   │   └── servicos.clj        # Lógica de negócio
│   │   └── views/
│   │       └── ui.clj              # Interface (futuras extensões)
│   └── Main.java                    # Classe Java auxiliar
├── test/
│   └── calculadora_calorias/
│       └── core_test.clj           # Testes unitários
├── project.clj                      # Configuração do projeto (Leiningen)
└── README.md                        # Este arquivo
```

## Arquitetura

A aplicação segue o padrão **MVC (Model-View-Controller)** com uma abordagem funcional:

- **Models** (`database.clj`): Armazenamento de dados em átomos (estado mutável de forma segura)
- **Services** (`servicos.clj`): Lógica de negócio pura (cálculos de calorias, filtros, resumos)
- **Controllers** (`handlers.clj`): Manipulation de requisições HTTP e orquestração
- **Routes** (`routes.clj`): Definição de endpoints e middleware

##  Testes

Execute os testes com:
```bash
lein test
```

## Integração com APIs Externas

A aplicação integra-se com APIs de terceiros para:

- **Dados nutricionais de alimentos**: Recupera calorias por 100g de alimento
- **Informações de exercícios**: Recupera calorias queimadas por hora

Veja `api/conexao.clj` e `api/tratamento.clj` para mais detalhes.

## Exemplos de Uso

### Criar e usar a calculadora

```bash
# 1. Atualizar perfil do usuário
curl -X POST http://localhost:3000/api/usuario \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria",
    "altura": 1.68,
    "peso": 65,
    "idade": 25,
    "sexo": "F"
  }'

# 2. Registrar uma refeição
curl -X POST http://localhost:3000/api/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "refeicao",
    "nome": "Salada",
    "data": "15-06-2026",
    "quantidade": 200
  }'

# 3. Registrar um exercício
curl -X POST http://localhost:3000/api/transacoes \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "exercicio",
    "nome": "Bicicleta",
    "data": "15-06-2026",
    "duracao": 30
  }'

# 4. Obter resumo
curl http://localhost:3000/api/resumo
```

## Contribuição

Contribuições são bem-vindas! Siga estes passos:

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a **Licença Pública Eclipse 2.0 (EPL 2.0)**.

Copyright © 2026 - Todos os direitos reservados.

Este programa e seus materiais acompanhantes estão disponibilizados sob os termos da Licença Pública Eclipse 2.0 que pode ser encontrada em: https://www.eclipse.org/legal/epl-2.0.

Este código-fonte também pode estar disponível sob as seguintes Licenças Secundárias quando as condições de disponibilidade definidas na Licença Pública Eclipse v. 2.0 forem cumpridas: GNU General Public License conforme publicado pela Free Software Foundation, versão 2 ou posterior (à sua escolha), com a Exceção de Classpath do GNU que está disponível em https://www.gnu.org/software/classpath/license.html.

## Contato

Para dúvidas ou sugestões, abra uma issue no repositório.
