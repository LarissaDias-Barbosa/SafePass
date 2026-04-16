# 🔐 SafePass

SafePass é um **gerenciador de senhas seguro**. Com ele você consegue gerar senhas fortes e complexas, armazená-las de forma organizada e acessá-las quando precisar — tudo em um só lugar, sem precisar memorizar nada.

A ideia é simples: você tem uma conta protegida e dentro dela guarda todas as suas outras senhas. Precisa de uma senha nova para um site? O SafePass gera uma senha difícil para você. Precisa lembrar a senha do seu banco? Ela está lá, salva com segurança.

---

## 💡 Para que serve?

- Cansado de usar a mesma senha em tudo?
- Esquece as senhas toda hora?
- Não confia em guardar senhas em anotações ou no bloco de notas?

O SafePass resolve isso. Você cria **uma única conta segura** e dentro dela:

- **Gera senhas fortes** com letras, números e símbolos
- **Armazena senhas** de sites, apps e serviços que você usa
- **Organiza tudo** em um só lugar
- **Acessa de qualquer lugar** com segurança, usando sua conta protegida por JWT + Google OAuth2

---


## 🛠 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17+ | Linguagem principal |
| Spring Boot | 3.x | Framework base |
| Spring Security | 6.x | Autenticação e autorização |
| Spring OAuth2 Client | 6.x | Login com Google |
| Spring Data JPA | 3.x | Persistência de dados |
| Spring Mail | 3.x | Envio de e-mails |
| MySQL | 8.x | Banco de dados |
| Hibernate | 6.x | ORM |
| JJWT (io.jsonwebtoken) | 0.11+ | Geração e validação de JWT |
| Lombok | — | Redução de boilerplate |
| SpringDoc OpenAPI | 2.x | Swagger UI |
| BCrypt | — | Hash de senhas |

---

## ⚙️ Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- MySQL 8 rodando localmente
- Conta no [Google Cloud Console](https://console.cloud.google.com/) para as credenciais OAuth2
- Conta Gmail com **Senha de App** gerada para o envio de e-mails


---

## 📡 Endpoints de Autenticação

> Esta documentação cobre apenas o módulo de autenticação. Os demais endpoints do gerenciador de senhas estão documentados no Swagger.

### Autenticação

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/auth/cadastro` | Cadastro manual com foto (multipart) |  ✅ |
| `GET` | `/auth/confirmar?token=` | Confirmação de e-mail via link | ✅ |
| `POST` | `/auth/login` | Login manual com e-mail e senha | ✅  |
| `POST` | `/auth/logout` | Logout (limpa o cookie JWT) | ✅ |
| `GET` | `/oauth2/authorization/google` | Inicia o fluxo de login com Google | ✅ |

### Usuário

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/api/usuario` | Retorna dados do usuário logado | ✅ |
| `GET` | `/api/usuario/autenticado` | Valida token e retorna dados | ✅ |

### Recuperação de Senha

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/auth/esqueceu-senha` | Envia código de 6 dígitos por e-mail | ✅  |
| `POST` | `/api/auth/redefinir-senha` | Redefine a senha com o código recebido | ✅  |

### Documentação interativa

| Endpoint | Descrição |
|---|---|
| `/swagger-ui.html` | Interface Swagger UI com todos os endpoints |
| `/v3/api-docs` | Especificação OpenAPI em JSON |

---

## 🔒 Fluxo de Autenticação

### Login Manual

```
Cliente → POST /auth/login
        ← JWT em cookie HttpOnly (jwt_token)

Próximas requisições → cookie enviado automaticamente pelo browser
                     ← JwtAuthenticationFilter valida o token
```

### Login com Google

```
Cliente → GET /oauth2/authorization/google
        → Redirecionamento para o Google
        → Usuário autoriza
        → Google redireciona para /login/oauth2/code/google
        → OAuth2LoginSuccessHandler cria/atualiza o usuário
        ← JWT em cookie HttpOnly + redirect para o frontend
```

### Recuperação de Senha da Conta

```
Cliente → POST /api/auth/esqueceu-senha  { email }
        ← Código de 6 dígitos enviado por e-mail (expira em 10 min)

Cliente → POST /api/auth/redefinir-senha { email, codigo, novaSenha }
        ← Senha atualizada
```

---

## 🧱 Decisões de Arquitetura do Módulo de Auth

**Operation Pattern** — cada ação de negócio é uma classe separada com um único método `execute()`. O `AuthenticationService` apenas delega, cada operação é testável de forma isolada e novas funcionalidades não alteram código existente.

**JWT em Cookie HttpOnly** — o token nunca fica exposto no `localStorage`, eliminando a vulnerabilidade de roubo via XSS. O browser envia o cookie automaticamente em cada requisição.

**Sessão IF_REQUIRED** — necessário para o fluxo OAuth2 do Google, que usa redirecionamentos e precisa de estado temporário. Após o login o JWT assume completamente.

**CORS centralizado** — isolado em `CorsConfig` e injetado no `SecurityConfig`, garantindo que as mesmas regras valem para todas as requisições sem duplicação.

---

## Licença

Todos os direitos reservados © 2026
Este projeto é de minha autoria e não pode ser copiado, reproduzido ou utilizado sem autorização expressa.

---

## Sobre os Autores

**Larissa Barbosa & Matheus Martins**

Desenvolvedores apaixonado por criar soluções que realmente funcionam na prática. A SafePass nasceu da vontade de aprender construindo algo útil.

- LinkedIn: [@matheusmartnsdev](https://www.linkedin.com/in/matheusmartnsdev/)
- LinkedIn: [@larissa-conceicao](https://www.linkedin.com/in/larissa-concei%C3%A7%C3%A3o-/)
