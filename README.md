# Better Crit (Crit Stun) – Fabric 1.21.11

Critical hits on mobs (not players, not the Wither/Ender Dragon) freeze the mob and store the knockback.
After `maxHits` crits – or one crit that deals at least the mob's max health – the stored knockback is
released in a single big hit. Install on **both client and server**.

Config: `config/bettercrit.json` (restart to apply)

| key | default | meaning |
|---|---|---|
| enableCrits | true | master switch |
| critMultiplier | 2.0 | cap / scale for the released knockback |
| maxHits | 3 | crits needed before release |
| stunTicks | 20 | stun ticks for non sword/axe weapons |
| tierStunTicks | wooden 10, stone 15, golden 12, copper 18, iron 22, diamond 30, netherite 40 | stun ticks per sword/axe material |

## Build

GitHub: push the repo, open the **Actions** tab, download the `bettercrit` artifact.

Locally: `gradle build` (Gradle 9.2+, JDK 21) -> `build/libs/bettercrit-1.0.0.jar`
