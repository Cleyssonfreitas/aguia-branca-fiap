# Desafio Grupo Águia Branca - Inovação Corporativa

Este aplicativo foi desenvolvido para a Sprint 1 do Challenge. O objetivo do app é conectar os colaboradores da Águia Branca para sugerir ideias de inovação, permitir que gestores priorizem essas ideias transformando-as em projetos, e que a liderança acompanhe os resultados gerais do negócio.

---

## 👥 Como testar o aplicativo (Credenciais inclusas)

Para facilitar a correção do projeto e a navegação entre as diferentes permissões do app, já deixamos pré-configurados no Firebase quatro usuários de teste (um para cada cargo). 

A senha padrão para todos os acessos é: **`!Pa12345678`**

* **Operador** (Cadastra ideias e acompanha status): `operador@aguiabranca.com`
* **Gestor** (Aprova ideias e gerencia projetos): `gestor@aguiabranca.com`
* **Liderança** (Cadastra diretrizes e visualiza Dashboard): `lider@aguiabranca.com`
* **Administrador** (Libera acesso de novos e-mails): `admin@aguiabranca.com`

---

## 🛠️ Configuração do Firebase

O arquivo `google-services.json` com as chaves de conexão ao nosso banco de dados de teste já está incluso na pasta `/app` deste repositório, permitindo rodar o projeto imediatamente no Android Studio.

Caso precise testar com uma conta própria do Firebase:
1. Crie um projeto no console do Firebase.
2. Ative o Authentication (E-mail/Senha) e o Realtime Database.
3. Substitua o arquivo em `/app/google-services.json` pelo seu arquivo gerado.
