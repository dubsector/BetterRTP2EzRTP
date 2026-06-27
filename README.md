# BetterRTP2EzRTP

A one-shot Paper plugin that migrates your [BetterRTP](https://www.spigotmc.org/resources/betterrtp-random-wild-teleport.36081/) configuration to [EzRTP](https://github.com/EZRTP_REPO_HERE) format. Drop it in, run one command, and you're done.

## Requirements

- Paper 1.21.4+
- BetterRTP installed with an existing `plugins/BetterRTP/config.yml`
- EzRTP installed (recommended, for auto-reload after migration)

## Installation

1. Download the latest jar from [Releases](../../releases/latest)
2. Drop it into your `plugins/` folder
3. Start or reload the server

## Usage

```
/migratertp
```

Reads `plugins/BetterRTP/config.yml`, writes equivalent config files to `plugins/EzRTP/`, attempts to reload EzRTP automatically, and prints a migration report to console.

```
/migratertp --dry-run
```

Prints the generated EzRTP config to console without writing any files. Use this to preview the migration before committing.

**Permission:** `betterrtp2ezrtp.migrate` (default: op)

## What gets migrated

| BetterRTP | EzRTP | File |
|---|---|---|
| `Default.MaxRadius` / `MinRadius` | `max-radius` / `min-radius` | `rtp.yml` |
| `Default.CenterX` / `CenterZ` | `center-x` / `center-z` | `rtp.yml` |
| `Default.Shape` (square/circle) | `search-pattern` | `rtp.yml` |
| `Default.Biomes[]` | `biomes.include` | `rtp.yml` |
| `Default.BlacklistedBlocks[]` | `unsafe-blocks` | `rtp.yml` |
| `Cooldown.Time` | `cooldown-seconds` | `limits.yml` |
| `Delay.Time` | noted in `limits.yml` | `limits.yml` |
| `CustomWorlds[]` | per-world overrides | `rtp.yml` |
| `betterrtp.bypass.cooldown` | `ezrtp.bypass.cooldown` | `limits.yml` |

`DisabledWorlds` entries are simply omitted from the output.

## What needs manual attention

The migration report will flag these — no EzRTP equivalent exists:

- **PermissionGroups** — per-group cooldowns are not supported by EzRTP. Replicate with LuckPerms contexts or a permissions-based cooldown plugin.
- **RTPOnDeath** — EzRTP does not teleport players on death. Use a separate death-RTP plugin alongside EzRTP if needed.
- **LockAfter** — infinite cooldown after N uses has no equivalent in EzRTP.

## After migration

BetterRTP's files are **not deleted** by this plugin. Once you have tested EzRTP and confirmed everything works correctly, you can remove `plugins/BetterRTP/` at your discretion.

## Staying compatible

This repository includes an automated weekly check that monitors EzRTP releases:

- **No config changes** → a new compatible release is published automatically.
- **Config changes detected** → a GitHub Issue is opened with a diff so the mapper can be updated before releasing.

You should never need to touch this repo after the initial setup.

## Building from source

```bash
mvn package
```

Output jar: `target/BetterRTP2EzRTP-1.0.0.jar`
