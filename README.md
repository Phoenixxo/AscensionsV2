# Better Rebirth 

Better Rebirth (previously Ascensions) is a modular core plugin designed to extend the functionality of [X-Prison](https://www.spigotmc.org/resources/x-prison-1-8-1-20-all-in-one-prison-core.83058/) by adding an **upgraded** rebirth-based progression system. Players can reset their rank, prestige, and balance to gain long-term benefits, visual ranks, and unlockable perks.

This plugin is designed as a foundational core that will support future gameplay enhancements, such as stat boosts, custom enchants, and cosmetic progression.

---

## Requirements

- [X-Prison](https://www.spigotmc.org/resources/x-prison-1-8-1-20-all-in-one-prison-core.83058/) by **Drawethree**
- Minecraft 1.20+ (Paper)
- PlaceholderAPI (optional, for placeholders)

---

## Features

- Configurable rebirth requirements (rank, prestige, balance, etc.)
- Rebirth GUI using Adventure API
- Level tracking with persistent data
- Dynamic prefix system based on rebirth level
- PlaceholderAPI support (`%rebirth_*%`)
- Reloadable configuration and messages
- Clean and modular command system

---

## Installation

1. Download and install [X-Prison](https://www.spigotmc.org/resources/x-prison-1-8-1-20-all-in-one-prison-core.83058/) by Drawethree.
2. Place `better_rebirth.jar` in your server’s `/plugins/` folder.
3. Start your server to generate the config files.
4. Customize `config.yml`, `messages.yml`, and `prefixes.yml`.

---

## Commands

| Command                               | Description                          | Permission                  |
|---------------------------------------|--------------------------------------|-----------------------------|
| `/rebirth` or `/rebirth gui`          | Open the Ascension GUI               | `ascensionsv2.ascend`       |
| `/rebirth reload`                     | Reload configs and data              | `ascensionsv2.reload`       |
| `/rebirth level get`                  | View your ascension level            | `ascensionsv2.ascend`       |
| `/rebirth level set <player> <level>` | Set another player's level     | `ascensionsv2.setlevel`     |

---

## Placeholders

If PlaceholderAPI is installed, the following placeholders are available:

- `%rebirth_level%`
- `%rebirth_next_level%`
- `%rebirth_can_ascend%`
- `%rebirth_ready%`
- `%rebirth_progress%`
- `%rebirth_status_colored%`
- `%rebirth_prefix%`

---

## Development Roadmap

### Core System (v1.0.0)
- [x] Rebirth mechanics
- [x] Configurable requirements
- [x] GUI interaction
- [x] Prefix system
- [x] PlaceholderAPI support
- [x] Data saving
- [x] Reload support

### Planned Expansions
- [ ] ⏳ Perk tree (active/passive abilities) 
- [ ] Rebirth Contracts (quests/missions)
- [ ] Custom enchantments system
- [ ] Unlockable zones tied to rebirth
- [ ] Cosmetics, titles, glow effects
- [ ] Optional MySQL support

---

## License

This plugin is a personal development project and not licensed for resale or commercial redistribution. You may fork and contribute as any help is much appreciated.

---

## Credits

- **Developer:** Phoenixxo  
- **X-Prison API Author:** [Drawethree](https://www.spigotmc.org/members/drawethree.520091/)  
- Built using the X-Prison API with permission
