# AscensionsV2

AscensionsV2 is a modular core plugin designed to extend the functionality of [X-Prison](https://www.spigotmc.org/resources/x-prison-1-8-1-20-all-in-one-prison-core.83058/) by adding an ascension-based progression system. Players can reset their rank, prestige, and balance to gain long-term benefits, visual ranks, and unlockable perks.

This plugin is designed as a foundational core that will support future gameplay enhancements, such as stat boosts, custom enchants, and cosmetic progression.

---

## Requirements

- [X-Prison](https://www.spigotmc.org/resources/x-prison-1-8-1-20-all-in-one-prison-core.83058/) by **Drawethree**
- Minecraft 1.20+ (Paper recommended)
- PlaceholderAPI (optional, for placeholders)

---

## Features

- Configurable ascension requirements (rank, prestige, balance, etc.)
- Ascension GUI using the Adventure API
- Level tracking with persistent data
- Dynamic prefix system based on ascension level
- PlaceholderAPI support (`%ascensions_*%`)
- Reloadable configuration and messages
- Clean and modular command system

---

## Installation

1. Download and install [X-Prison](https://www.spigotmc.org/resources/x-prison-1-8-1-20-all-in-one-prison-core.83058/) by Drawethree.
2. Place `ascensionsV2.jar` in your server’s `/plugins/` folder.
3. Start your server to generate the config files.
4. Customize `config.yml`, `messages.yml`, and `prefixes.yml`.

---

## Commands

| Command                            | Description                          | Permission                  |
|-----------------------------------|--------------------------------------|-----------------------------|
| `/ascensions` or `/ascensions gui`| Open the Ascension GUI               | `ascensionsv2.ascend`       |
| `/ascensions reload`              | Reload configs and data              | `ascensionsv2.reload`       |
| `/ascensions level get`           | View your ascension level            | `ascensionsv2.ascend`       |
| `/ascensions level set <player> <level>` | Set another player's level     | `ascensionsv2.setlevel`     |

---

## Placeholders

If PlaceholderAPI is installed, the following placeholders are available:

- `%ascensions_level%`
- `%ascensions_next_level%`
- `%ascensions_can_ascend%`
- `%ascensions_ready%`
- `%ascensions_progress%`
- `%ascensions_status_colored%`
- `%ascensions_prefix%`

---

## Development Roadmap

### Core System (v1.0.0)
- [x] Ascension mechanics
- [x] Configurable requirements
- [x] GUI interaction
- [x] Prefix system
- [x] PlaceholderAPI support
- [x] Data saving
- [x] Reload support

### Planned Expansions
- Stat boosts and passive upgrades
- Perk tree (active/passive abilities)
- Ascension Contracts (quests/missions)
- Custom enchantments system
- Unlockable zones tied to ascension
- Cosmetics, titles, glow effects
- Pet system with stat bonuses
- Optional MySQL support

---

## License

This plugin is a personal development project and not licensed for resale or commercial redistribution. You may fork or contribute with attribution.

---

## Credits

- **Developer:** Phoenixxo  
- **X-Prison API Author:** [Drawethree](https://www.spigotmc.org/members/drawethree.520091/)  
- Built using the X-Prison API with permission
