# 🦅 Águia Branca - Inovação Corporativa

Olá! Seja bem-vindo(a) ao repositório do nosso aplicativo desenvolvido para o **Challenge da Águia Branca**. 

O nosso principal objetivo aqui foi criar uma plataforma que tire as ideias do papel e as transforme em inovação real. Construímos um fluxo completo: o colaborador na ponta da operação dá a ideia, o gestor recebe essa ideia (já pré-avaliada por uma Inteligência Artificial 🤖) para transformar em projeto, e a liderança consegue ver os resultados financeiros rolando em um dashboard bonitão.

## ✨ O que tem de legal no app?

* **Triagem Inteligente com IA (Google Gemini 2.5 Flash):** Acabou o sofrimento de ler dezenas de ideias inviáveis! Nossa integração com a API do Gemini avalia instantaneamente a ideia do Operador e já entrega para o Gestor uma nota de inovação e um feedback, ordenando a lista das melhores para as piores.
* **Governança de TI:** O app não tem tela de cadastro público. Só o Administrador de TI libera as contas, garantindo máxima segurança.
* **Dashboard Interativo:** Gráficos que calculam o ROI (Retorno sobre Investimento) dos projetos em tempo real para a Diretoria.
* **Design Premium:** Telas construídas inteiramente com **Jetpack Compose** e arquitetura limpa (MVVM).

---

## 👥 Como testar o aplicativo (Já deixamos tudo pronto!)

Para facilitar a vida de quem for corrigir e testar, o banco de dados já está configurado com alguns usuários padrão. Assim você consegue ver a visão de cada cargo.

A senha para **todos** os acessos abaixo é: **`!Pa12345678`**

* **Operador** (Dá as ideias): `operador@aguiabranca.com`
* **Gestor** (Aprova ideias e toca os projetos): `gestor@aguiabranca.com`
* **Liderança** (Cria estratégias e vê o Dashboard): `lider@aguiabranca.com`
* **Admin** (Libera novos usuários): `admin@aguiabranca.com`

---

## 🛠️ Para quem vai rodar o código (Firebase)

Não se preocupe em configurar banco de dados! Nós já deixamos o arquivo `google-services.json` (do nosso ambiente de teste) embutido no projeto. É só clonar o repositório, abrir no **Android Studio**, dar play no emulador (ou no cabo USB) e testar.

*(Se por acaso quiser plugar no seu próprio Firebase, basta substituir o arquivo `/app/google-services.json` pelo seu).*
