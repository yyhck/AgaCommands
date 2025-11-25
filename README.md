# 🛡️ AgaCommands

> O gerenciador de comandos e segurança definitivo para redes Waterfall/BungeeCord.

![Version](https://img.shields.io/badge/version-1.0-blue)
![Java](https://img.shields.io/badge/java-8+-orange)
![Author](https://img.shields.io/badge/author-yyHcK-purple)

**AgaCommands** é uma solução leve e otimizada desenvolvida para gerenciar atalhos de comandos, bloquear sintaxes indesejadas e proteger a lista de plugins do seu servidor em modo Proxy, tudo isso com zero impacto na performance.

---

## ✨ Funcionalidades

- **🚀 Alias Dinâmico:** Crie atalhos para comandos longos (ex: `/loja` -> `/server loja`) diretamente pelo jogo ou config.
- **🔒 Bloqueador de Comandos:** Impeça que jogadores vejam seus plugins ou segredos (`/pl`, `/about`, `/ver`).
- **🚫 Anti-Tab Inteligente:** Bloqueia o `TAB` para tudo, exceto comandos na Whitelist (como `/tpa`, `/tell`).
- **⚡ Logs Assíncronos:** Sistema de registro de tentativas de comandos proibidos que roda em thread separada (sem lag).
- **🎨 100% Configurável:** Todas as mensagens, listas e permissões são editáveis.

---

## 📦 Instalação

1. Baixe o arquivo `AgaCommands-1.0.jar` na aba [Releases].
2. Coloque o arquivo na pasta `/plugins/` do seu **Waterfall** ou **BungeeCord**.
3. Reinicie o seu Proxy.
4. Configure o arquivo `config.yml` conforme a sua necessidade.

---

## 🛠️ Comandos e Permissões

### Comando Principal
O comando base é `/agacommands` (ou os atalhos `/aga`, `/acmd`) porém você pode alterar como preferir.

| Comando | Descrição | Permissão |
| :--- | :--- | :--- |
| `/aga reload` | Recarrega as configurações e mensagens. | `agacommands.admin` |
| `/aga <comando> <atalho>` | Cria um novo atalho in-game. | `agacommands.admin` |
| `/aga` | Mostra a lista de ajuda. | `agacommands.admin` |

### Permissões Especiais

- **`agacommands.admin`**: 
  - Acesso total aos comandos de administração.
  - **Bypass:** Permite ver conteúdos (`/pl`), usar TAB livremente e executar comandos bloqueados.

---

## ⚙️ Configuração Padrão (`config.yml`)

```yaml
# AgaCommands - Configuração
# Aliases para o comando principal
main-command-aliases:
  - agacommands
  - hcommands

messages:
  reload: "&fAga&bCommands &8- &fConfiguração recarregada com sucesso!"
  blocked-command: "&cComando desconhecido ou inexistente."
  no-permission: "&cVocê não tem permissão para fazer isso."

  help:
    - ""
    - "&f Aga&bCommands &8- &7Lista de Comandos &c(STAFF)"
    - ""
    - "&8 ➡ &f/agacommands reload &8- &7Recarrega a configuração."
    - "&8 ➡ &f/agacommands <comando> <atalho> &8- &7Cria um atalho."
    - ""

aliases:
  voar: fly
  lobby: server lobby

blocked-commands:
  - "/pl"
  - "/plugins"
  - "/ver"
  - "/version"
  - "/about"

# Sistema de Anti-Tab
tab-settings:
  enabled: true

  # Lista de comandos onde o TAB será PERMITIDO (Whitelist)
  # Coloque apenas o início do comando.
  allowed-commands:
    - "/tpa"
    - "/tpaccept"
    - "/tell"
    - "/msg"
    - "/g"
    - "/l"
    - "/money"
