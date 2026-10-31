<div align="center">

# 🪓 Timber

**Chop down entire trees at once – just sneak while breaking a log.**

A lightweight, vanilla-friendly tree feller for Paper servers.

[![Build](https://github.com/1870s/Timber/actions/workflows/build.yml/badge.svg)](https://github.com/1870s/Timber/actions/workflows/build.yml)
[![Latest release](https://img.shields.io/github/v/release/1870s/Timber?label=release)](https://github.com/1870s/Timber/releases/latest)
[![Paper](https://img.shields.io/badge/Paper-26.1--26.3-2b7bb9)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-25-e76f00)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/license-MIT-3da639)](LICENSE)

</div>

---

## ✨ Features

- 🌲 **Whole tree in one hit** – sneak, break a log, and every connected log of the same wood type (at or above that block) falls instantly.
- 🍃 **Leaves included** – the tree's leaves are cleared too, dropping saplings, sticks and apples as usual. Leaves don't cost axe durability.
- ⛏️ **Vanilla-accurate drops** – every log is broken as if you mined it yourself: Fortune, Silk Touch, Unbreaking, tool durability and statistics all apply.
- 🏠 **Your builds are safe** – Timber only triggers when *natural* leaves are attached, so log cabins and other builds won't be torn down.
- 🛡️ **Protection-plugin friendly** – a break event is fired for every single block, so WorldGuard, GriefPrevention, CoreProtect & co. keep working.
- 🔧 **Tool protection** – stops right before your axe would break.
- 🌳 **All tree types** – oak, spruce, birch, jungle, acacia, dark oak, mangrove, cherry, pale oak, azalea, plus crimson & warped fungi in the Nether.
- ⚙️ **Configurable** – change everything in `config.yml` and reload without restarting.

## 📦 Installation

1. Download the latest `Timber-x.y.z.jar` from [**Releases**](https://github.com/1870s/Timber/releases/latest).
2. Drop it into your server's `plugins/` folder.
3. Restart the server.

**Requirements:** [Paper](https://papermc.io/downloads/paper) 26.1 or newer · Java 25

### Supported versions

One JAR works on all of these – no separate downloads needed.

| Paper   | Status |
|---------|:------:|
| 26.3    | ✅     |
| 26.2    | ✅     |
| 26.1.x  | ✅     |

Every build is checked against the latest Paper builds of each version.

## 🎮 Usage

1. Hold an axe.
2. Sneak (hold <kbd>Shift</kbd>).
3. Break any log of a tree – done. 🌲💥

## 🔐 Commands & Permissions

| Command          | Permission        | Default    | Description                       |
|------------------|-------------------|------------|-----------------------------------|
| –                | `timber.use`      | everyone   | Allows chopping whole trees       |
| `/timber reload` | `timber.reload`   | operators  | Reloads `config.yml`              |

## ⚙️ Configuration

The config lives at `plugins/Timber/config.yml`. After editing, run `/timber reload`.

| Option                   | Default | Description                                                                 |
|--------------------------|:-------:|-----------------------------------------------------------------------------|
| `require-sneak`          | `true`  | Only fell the whole tree while sneaking. `false` = always active.           |
| `require-axe`            | `true`  | Only trigger with an axe in the main hand.                                  |
| `break-leaves`           | `true`  | Also break the tree's leaves.                                               |
| `require-natural-leaves` | `true`  | Only trigger if natural (not player-placed) leaves are attached.            |
| `protect-tool`           | `true`  | Stop before the axe breaks (leaves 1 durability).                           |
| `max-logs`               | `256`   | Maximum number of logs per tree.                                            |
| `max-leaves`             | `1024`  | Maximum number of leaves per tree.                                          |
| `leaf-radius`            | `6`     | How far (in blocks) leaves may be from a log. Vanilla leaves reach up to 6. |
| `disabled-worlds`        | `[]`    | Worlds where Timber is disabled, e.g. `[world_nether, spawn]`.              |

## 🛠️ Building from source

```bash
git clone https://github.com/1870s/Timber.git
cd Timber
./gradlew build
```

The plugin JAR ends up in `build/libs/`. Gradle needs Java 17+ to run; if no JDK 25 is installed, it is downloaded automatically via Gradle toolchains.

Timber compiles against the oldest supported Paper API (set in `gradle.properties`) so the JAR runs on every newer version. To check against a newer API, pass it on the command line:

```bash
./gradlew compileJava -PpaperApi=26.3.build.+
```

## 📄 License

Timber is released under the [MIT License](LICENSE).
