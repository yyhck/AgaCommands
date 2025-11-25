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
- **tab 🚫 Anti-Tab Inteligente:** Bloqueia o `TAB` para tudo, exceto comandos na Whitelist (como `/tpa`, `/tell`).
- **⚡ Logs Assíncronos:** Sistema de registro de tentativas de comandos proibidos que roda em thread separada (sem lag).
- **🎨 100% Configurável:** Todas as mensagens, listas e permissões são editáveis.

---

## 📦 Instalação

1. Baixe o arquivo `AgaCommands-1.0.jar` na aba [Releases].
2. Coloque o arquivo na pasta `/plugins/` do seu **Waterfall** ou **BungeeCord**.
3. Reinicie o Proxy.
4. Configure o arquivo `config.yml` conforme sua necessidade.

---

## 🛠️ Comandos e Permissões

### Comando Principal
O comando base é `/agacommands` (ou os atalhos `/aga`, `/acmd`).

| Comando | Descrição | Permissão |
| :--- | :--- | :--- |
| `/aga reload` | Recarrega as configurações e mensagens. | `agacommands.admin` |
| `/aga <comando> <atalho>` | Cria um novo atalho in-game. | `agacommands.admin` |
| `/aga` | Mostra a lista de ajuda. | Nenhuma |

### Permissões Especiais

- **`agacommands.admin`**: 
  - Acesso total aos comandos de administração.
  - **Bypass:** Permite ver plugins (`/pl`), usar TAB livremente e executar comandos bloqueados.

---

## ⚙️ Configuração Padrão (`config.yml`)

```yaml
# AgaCommands - Configuração

# Aliases para o comando principal de admin
main-command-aliases:
  - agacommands
  - aga
  - acmd

messages:
  reload: "&a&l[AgaCommands] &fConfiguração recarregada com sucesso!"
  blocked-command: "&c&lERRO! &fComando desconhecido ou inexistente."
  no-permission: "&c&lERRO! &fVocê não tem permissão para gerenciar o AgaCommands."
  help:
    - "&e&m--------------------------------"
    - "&6&lAgaCommands &f- &7Ajuda"
    - "&f/aga reload &7- Recarrega a configuração."
    - "&f/aga <comando> <atalho> &7- Cria um atalho."
    - "&e&m--------------------------------"

# Configurações do Anti-Tab
tab-settings:
  enabled: true
  # Comandos permitidos no TAB (Whitelist)
  allowed-commands:
    - "/tpa"
    - "/tell"
    - "/spawn"

aliases:
  voar: fly
  lobby: server lobby

blocked-commands:
  - "/pl"
  - "/plugins"
  - "/ver"
  - "/icanhasbukkit"
