# Player Damage UDP Monitor (NeoForge)

适配目标：**Minecraft Java Edition 1.21.11 + NeoForge**（请按官方 MDK 核对精确版本号）。

## 项目结构

```text
minecraft_player_damage_monitor_curseforge/
├─ build.gradle
├─ gradle.properties
├─ settings.gradle
└─ src/
   └─ main/
      ├─ java/com/example/damageudp/
      │  ├─ PlayerDamageUdpMod.java
      │  ├─ DamageUdpConfig.java
      │  └─ DamageEventHandler.java
      └─ resources/
         └─ META-INF/
            └─ neoforge.mods.toml
```

## 功能

- 监听服务端玩家受伤事件，仅处理 `ServerPlayer`。
- 仅处理生存/冒险（可配置仅生存）。
- 输出 JSON 到 UDP（默认 `127.0.0.1:25575`）。
- 字段：
  - `event`
  - `player`
  - `uuid`
  - `damage_points`
  - `damage_hearts`
  - `intensity`
  - `damage_source`
  - `game_mode`
  - `world`
  - `timestamp_ms`

## 配置项（server config）

该 Mod 注册的是 `SERVER` 配置，服务端首次启动后会生成配置文件（典型路径：`<server>/world/serverconfig/player_damage_udp-server.toml`）：

- `enabled=true`
- `udpHost="127.0.0.1"`
- `udpPort=25575`
- `survivalOnly=false`
- `intensityCap=1.0`

## 构建

```bash
./gradlew build
```

产物通常在：

```text
build/libs/player_damage_udp-1.0.0.jar
```

## 安装到 dedicated server

1. 将 JAR 复制到服务端 `mods/` 目录。
2. 启动 NeoForge 服务端。
3. 启动后检查配置文件并按需修改。

## UDP 输出测试

在同机开一个 UDP 监听：

```bash
nc -ul 25575
```

然后让玩家在生存或冒险模式下受到伤害（例如跌落）。你应看到一行 JSON，例如：

```json
{"event":"player_damage","player":"Steve","uuid":"...","damage_points":3.0000,"damage_hearts":1.5000,"intensity":0.1500,"damage_source":"fall","game_mode":"survival","world":"minecraft:overworld","timestamp_ms":1710000000000}
```

## 事件 API 兼容性说明（重要）

针对 NeoForge 1.21.x，`LivingDamageEvent.Post`、`event.getNewDamage()`、`event.getSource()` 是**最可能正确**的一组 API；但不同小版本可能存在命名差异。

若编译报错，请优先核对：

- 事件类 import：
  - `net.neoforged.neoforge.event.entity.living.LivingDamageEvent`
- 事件回调签名：
  - `onLivingDamage(LivingDamageEvent.Post event)`
- 伤害数值读取方法：
  - `getNewDamage()`（若不存在，检查是否改为 `getAmount()` 或同义方法）

本 Mod 仅输出 Minecraft 受伤数据，不涉及任何硬件控制或成人设备相关内容。
