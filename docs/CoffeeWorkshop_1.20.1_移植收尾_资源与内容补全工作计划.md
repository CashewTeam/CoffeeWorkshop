# Coffee Workshop 1.20.1 移植收尾：资源完整性与内容补全工作计划

> **基线提交：** `11cdd8a3b74058848b29bb1b102816f391baea0a`  
> **目标分支：** `1.20.1`  
> **建议仓库路径：** `docs/COFFEEWORK_1_20_1_PORTING_CLOSURE_PLAN.md`  
> **计划性质：** 1.12.2 → 1.20.1 移植最终收尾  
> **测试策略：** 测试相关工作继续后移，不作为本轮资源与内容收尾阻断项  
> **存档策略：** 不处理旧存档迁移，不为旧 ID 或旧库存布局保留额外兼容层

---

## 1. 执行结论

当前代码已经具备可用的现代 Forge 1.20.1 基础：

- 五台加工机器已经统一为现代 BlockEntity、Menu、RecipeManager 架构；
- 咖啡主循环、Phase 4 热饮/冰饮/冷萃/糖浆链路已经建立；
- Coffee Machine 五槽 GUI 已经完成人工验证；
- 当前构建、资源审计与配方可达性检查能够通过。

接下来的工作不应继续围绕机器核心重构，而应转为两个目标：

1. **保证当前注册并面向玩家的内容完整。**
   - 每个物品、方块、效果、职业和声音都必须具备正确文本、模型、贴图与获取方式。
   - 不允许出现未解释的紫黑模型、原始翻译键、无来源物品或交易经济漏洞。

2. **对原版 1.12.2 遗留内容完成最终分类。**
   - 每个遗留内容必须被标记为立即移植、后续移植、合并替代、重新设计或正式移除。
   - 不再允许大量旧模型、旧翻译和旧贴图长期处于“文件存在但代码未注册”的不确定状态。

建议最终状态分为：

```text
Phase 4: verified core complete
Porting Closure: resource and content completion in progress
```

---

## 2. 本轮范围

### 2.1 纳入范围

- `en_us.json`、`zh_cn.json`、`ja_jp.json` 文本完整性与术语整理；
- 注册表与语言键的一致性；
- Item Model、Blockstate、Block Model、Texture 完整性；
- Mob Effect 图标；
- Villager Profession 外观资源；
- 机器工作状态模型差异；
- 创造模式物品栏完整性与排序；
- 生存获取来源；
- 村民职业、工作站与交易经济；
- 原版 1.12.2 内容缺失分类；
- README、版本元数据、内容清单和移植决策文档；
- 清理或归档孤立资源。

### 2.2 明确后移

以下工作不作为本轮封板阻断：

- 新增 GameTest；
- 客户端自动化截图测试；
- Menu Shift-click 集成测试；
- Cooling NBT 回归测试；
- Cold Brew Pot 更严格行为测试；
- 性能压测；
- 旧存档和旧 ID 迁移。

本轮仅在视觉资源发生变化时进行必要的人工查看。

---

## 3. 审计方法升级

当前 `tools/audit_resources.py` 主要验证：

- JSON 是否可解析；
- 是否残留旧 `.lang`；
- Blockstate 模型引用是否存在；
- 模型引用的贴图是否存在；
- 是否残留旧 Forge Blockstate 字段；
- 是否使用旧版 Vanilla 贴图路径。

这些检查可以发现“已有引用是否损坏”，但无法回答以下问题：

- 一个注册物品是否根本没有 Item Model；
- 一个注册方块是否没有 Blockstate；
- 一个注册效果是否缺少图标；
- 一个注册职业是否缺少外观贴图；
- 一个注册物品是否没有任何获取来源；
- 一个旧模型是否已经没有任何注册内容使用；
- 三种语言是否覆盖同一组当前有效键；
- `_on` 和 `_off` 模型是否实际上完全相同。

因此需要新增内容表面审计工具：

```text
tools/audit_content_surface.py
```

它应建立以下五向关系：

```text
Java Registry
↕
Language
↕
Model / Blockstate
↕
Texture / Sound / Icon
↕
Recipe / Loot / Trade / World Source
```

建议输出：

```text
build/reports/coffeework/content-surface.md
build/reports/coffeework/content-surface.json
build/reports/coffeework/orphan-assets.md
```

---

## 4. 文本资源审计结果

## 4.1 三语键集合不一致

当前资源报告显示三种语言文件规模明显不同：

```text
en_us：约 252 键
ja_jp：约 484 键
zh_cn：约 515 键
```

这并不意味着中文和日文更完整，因为其中包含大量旧版未注册内容、旧 ID 和旧成就文本；英文则同时存在当前键缺失和大量未润色占位值。

收尾目标不是简单地让三份文件行数相同，而是建立两类键：

### 当前必需键

由当前注册表和当前 UI 自动推导，包括：

- `item.coffeework.*`
- `block.coffeework.*`
- `container.coffeework.*`
- `gui.coffeework.*`
- `jei.coffeework.*`
- `effect.coffeework.*`
- `entity.minecraft.villager.coffeework.*`
- `itemGroup.coffee_workshop`
- 唱片描述和其他真实代码引用。

当前必需键必须在 `en_us`、`zh_cn`、`ja_jp` 中全部存在。

### 遗留候选键

例如：

- 未注册苏打饮料；
- 未注册三明治；
- 未注册冰淇淋、奶油与烘焙家族；
- `coffee_instant_cup` 系列；
- 旧 `achievement.coffeework.*`；
- 旧机器 `_on/_off` Block 翻译键；
- 旧放置式饮品和盘装饮品键。

这些键不能继续混在正式语言文件中，必须先进入遗留内容分类表，再决定保留、移植或移除。

---

## 4.2 已确认的文本问题

### 旧机器 ID 残留

英文仍使用：

```text
block.coffeework.grinder_off
block.coffeework.grinder_on
block.coffeework.icecreammachine_off
block.coffeework.icecreammachine_on
block.coffeework.roller_off
block.coffeework.roller_on
block.coffeework.oven_off
block.coffeework.oven_on
```

当前 Block 注册 ID 已经是：

```text
grinder
coffee_machine
icecream_machine
roller
oven
```

需要补齐当前 Block 翻译键，并清理不再使用的旧 Block 键。

### 英文占位文本

英文中仍有大量值直接等于 Registry Path，例如：

```text
bag_cocoa
cake_berry
cake_sponge
mousse_berry
field_ration
record_kusa_noshi_to_ne
```

这些内容虽然不会显示缺失翻译键，但不具备发布质量。

### 中文旧键与错别字

已确认需要统一：

```text
item.coffeework.coffee_seed
```

应对应当前：

```text
item.coffeework.coffee_seeds
```

术语建议：

```text
冰淇凌 → 冰淇淋
烘培 → 烘焙
姜饼面版 → 姜饼面片 / 姜饼面皮
ColdBrew → 冷萃
```

旧糖浆命名：

```text
syrup_full
syrup_brown
```

应与当前：

```text
syrup_empty
syrup_caramel
syrup_chocolate
syrup_fruit
syrup_mint
syrup_vanilla
syrup_sakura
```

统一。

### 旧成就文本

语言文件中仍保留 `achievement.coffeework.*`，但当前没有对应的现代自定义 Advancement 树。

必须二选一：

1. 将旧成就移植为现代 Advancement；
2. 正式删除这些无效文本。

推荐移植一套精简教程型 Advancement。

---

## 4.3 文本收尾任务

新增：

```text
tools/audit_lang_surface.py
```

输入：

- 当前 Item、Block、MobEffect、Profession、Menu、JEI 注册键；
- 代码内 `Component.translatable()` 引用；
- 明确允许的特殊键。

输出：

- 三语缺失键；
- 三语多余旧键；
- 值等于 Registry Path 的占位文本；
- 重复键或术语不一致；
- 当前代码未引用的旧 UI 键。

验收：

- 当前必需键三语覆盖率 100%；
- 英文不得以 Registry Path 作为最终显示值；
- 中文术语表统一；
- 未注册内容的文本全部完成分类。

---

## 5. 贴图与模型资源审计结果

## 5.1 当前审计未覆盖“注册对象没有模型”

当前脚本只遍历已经存在的模型文件，因此无法发现 Item Model 文件本身缺失。

已确认至少有以下当前注册内容需要补资源或建立旧资源别名：

```text
tea_leaf
black_tea_leaf
syrup_caramel
syrup_sakura
```

其中旧焦糖糖浆资源仍以：

```text
syrup_brown
```

存在，应正式重命名或通过新模型引用旧贴图。

所有 Phase 4 新物品都必须逐一检查：

```text
models/item/<id>.json
textures/item/<id>.png
```

不能仅依赖旧元数据变体资源。

---

## 5.2 Mob Effect 图标缺失

当前注册了三个自定义效果：

```text
caffeine
relax
golden_heart
```

需要确认并补齐现代路径：

```text
assets/coffeework/textures/mob_effect/caffeine.png
assets/coffeework/textures/mob_effect/relax.png
assets/coffeework/textures/mob_effect/golden_heart.png
```

若图标缺失，药水效果 HUD 和物品 Tooltip 会使用缺失纹理。

---

## 5.3 Villager Profession 外观资源缺失

当前注册：

```text
coffee_barista
coffee_materials_trader
food_trader
```

需要补齐：

```text
assets/coffeework/textures/entity/villager/profession/coffee_barista.png
assets/coffeework/textures/entity/villager/profession/coffee_materials_trader.png
assets/coffeework/textures/entity/villager/profession/food_trader.png
```

同时检查资源包是否需要对应的帽子/层级配置；若不制作专属外观，则应明确记录“使用无职业覆盖层”而不是让资源缺失。

---

## 5.4 机器工作状态视觉不完整

机器 Blockstate 已区分：

```text
lit=false
lit=true
```

但至少 Coffee Machine 的：

```text
coffee_machine.json
coffee_machine_on.json
```

内容完全相同，工作状态只能通过发光和粒子判断，模型本身没有视觉变化。

收尾时应检查五台机器：

```text
grinder
coffee_machine
icecream_machine
roller
oven
```

对每一组 `_off` / `_on` 模型做内容哈希比较。

建议：

- 为工作状态添加亮起的指示灯、加热口、蒸汽口或活动面板；
- 不要求复杂发光渲染，但至少在普通光照下可见；
- 若某台机器确实不需要模型变化，应删除重复模型并让两种状态引用同一个模型，避免制造虚假的资源差异。

---

## 5.5 孤立模型与旧版资源规模过大

当前资源审计扫描到约：

```text
121 个 Blockstate
763 个模型文件
```

这一规模远大于当前 Java 注册内容。

主要原因是原版 1.12.2 大量 metadata 内容被拆成独立模型，但并未在 1.20.1 注册，例如：

- 约 70 种盘装或放置式饮品；
- 多种苏打饮料；
- 完整冰淇淋系列；
- 完整三明治系列；
- 奶油、曲奇圣代；
- 派、可颂、姜饼、泡芙、千层酥；
- 月饼、舒芙蕾、玛芬；
- 蛋糕胚、蛋糕卷、摇晃蛋糕等完整流水线。

这些文件不能继续保持“可能以后使用”的模糊状态。

每个孤立资源必须分类：

```text
PORT_NOW
PORT_LATER
MERGED
REDESIGN
REMOVED
ASSET_ARCHIVE
```

被标记为 `ASSET_ARCHIVE` 的文件应移入：

```text
reference/legacy-assets/
```

而不是继续留在正式 `assets/coffeework/` 中被打进发布 JAR。

---

## 5.6 声音资源

当前三张唱片已经注册 SoundEvent，并在 `sounds.json` 中有对应条目。

新增声音审计：

```text
SoundEvent Registry
→ sounds.json event
→ ogg file
```

还应检查：

- 三个唱片 Item Model 和贴图；
- 三个 `.desc` 翻译；
- OGG 是否为空文件或异常短；
- `stream=true` 是否保留。

---

## 6. 内容与玩法缺失审计

## 6.1 创造模式物品栏不完整

当前已经注册但没有加入 Coffee Workshop 创造模式页的内容至少包括：

```text
syrup_caramel
syrup_chocolate
syrup_fruit
syrup_mint
syrup_vanilla
syrup_sakura
tea_leaf
black_tea_leaf
coldbrew_bottle
icecream_mix_vanilla
```

这会导致开发者和玩家难以发现 Phase 4 新内容。

建议重新整理顺序：

```text
机器
→ 作物与世界资源
→ 基础材料
→ 容器与工具
→ 热饮
→ 冰饮
→ 冷萃与糖浆
→ 烘焙食品
→ 装饰与储存
→ 唱片
```

---

## 6.2 村民交易存在直接套利

当前咖啡师交易中存在：

```text
6 绿宝石购买 4 咖啡粉
16 绿宝石出售 4 咖啡粉
```

以及：

```text
8 绿宝石购买 4 可可粉
16 绿宝石出售 4 可可粉
```

玩家可在同一职业内无成本反复套利。

跨职业还有：

```text
4 绿宝石购买 2 咖啡豆
材料商以 4 绿宝石收购 1 咖啡豆
```

同样构成稳定套利。

这是本轮明确的玩法 P0。

修复原则：

- 村民收购物品的绿宝石价值必须显著低于出售价值；
- 对可通过工作台、袋装拆包或机器批量生产的物品设置更低收购价；
- 检查单袋、双袋、拆包、加工和交易之间是否存在价值复制；
- 饮品交易获得的 Stack 必须初始化为满杯状态；
- 交易不应成为绕过核心种植与加工循环的最低成本路径。

建议增加静态交易经济报告：

```text
tools/audit_trade_economy.py
```

测试继续后移，但脚本可先检查同一物品的直接买卖倒挂。

---

## 6.3 两个职业共用同一个 Coffee Machine POI

当前：

```text
coffee_barista
coffee_materials_trader
```

都使用：

```text
coffee_poi
```

这会使同一工作站可匹配两个职业，职业分配意图不够清晰。

必须选择一个正式方案：

### 方案 A：合并职业

将材料交易并入 Coffee Barista，不再注册 Materials Trader。

优点：

- 最简单；
- 无需新增方块；
- 职业语义集中。

### 方案 B：新增材料商工作站

例如：

```text
coffee_crate
coffee_counter
coffee_roaster
```

材料商使用独立 POI。

优点：

- 保留原版职业分工；
- 提供新的装饰与玩法节点。

推荐方案 B，但若本轮严格控制范围，方案 A 更适合快速收尾。

---

## 6.4 当前注册内容必须完成来源闭环

现有 Reachability 工具只分析 Recipe 输出，因此无法发现：

- 注册了但没有 Recipe、Loot、Trade 或 World Source 的物品；
- 只存在于创造模式的正式物品；
- 放入创造模式页但无生存来源的内容。

新增来源类型：

```text
CRAFTING
MACHINE
LOOT
WORLDGEN
CROP
BLOCK_INTERACTION
VILLAGER_TRADE
CREATIVE_ONLY_INTENTIONAL
REMOVED
```

所有当前注册物品必须至少拥有一个来源，或者被明确标记为：

```text
CREATIVE_ONLY_INTENTIONAL
```

并写明原因。

重点核对：

```text
coffee_americano_nitro_fruit_ice
record_blank
三张唱片
所有糖浆
所有 Cooling 输出
所有模具和容器
所有蛋糕 BlockItem
```

建议为 Nitro Fruit Americano 增加第二阶段 NBT 保留配方：

```text
Nitro Americano + Fruit Syrup
→ Nitro Fruit Americano
```

而不是长期保留无法解释的创造模式专属饮品。

---

## 6.5 原版内容缺失分类

### A 级：建议在 1.20.1 收尾阶段恢复

这些内容价值高、依赖少、可复用现有系统：

#### 速溶咖啡杯

```text
coffee_instant_cup_unopen
coffee_instant_cup
```

可形成：

```text
速溶条 + 杯子
→ 未开启杯装速溶咖啡
→ 右键加水 / 工作台加水
→ 可饮用速溶咖啡
```

#### 剩余冰淇淋口味

```text
chocolate
coffee
apple
berry
melon
lemon
```

使用现有 Icecream Machine，不需要新机器架构。

#### 剩余三明治

```text
sandwich_club
sandwich_blt_large
sandwich_club_large
sandwich_bacon_egg
sandwich_beef_cheese
sandwich_ham_cheese
```

主要是物品、配方和文本工作。

#### 基础派和糕点

优先恢复：

```text
pie_chocolate
pie_coffee
pie_apple
pie_berry
croissant
croissant_chocolate
ginger_bread
ginger_bread_man
puff
mille_feuille
```

这些能显著补充当前偏薄的烘焙内容面。

#### 苏打饮料

恢复 8 种苏打饮料前，先统一容器与空瓶返还方式。可以复用 `DrinkCoffee` 的多次饮用框架，也可以建立轻量 `DrinkBottled`。

---

### B 级：建议后续内容阶段恢复

这些内容需要较长的中间产物链：

```text
7 种 cream
7 种 cookie icecream / sundae
8 种 mooncake
4 种 souffle
完整 muffin 系列
cake roll 系列
jiggly cake 系列
更多 pie 与 pastry
```

在本轮只要求：

- 完成 ID 清单；
- 记录原版配方和模型来源；
- 标记 `PORT_LATER`；
- 将资源移入可追踪的遗留资产目录。

---

### C 级：建议重新设计，而不是 1:1 注册

#### 约 70 种放置式饮品/盘装饮品

不建议为每种饮品重新注册一个独立 Block。

推荐设计一个通用展示系统：

```text
DrinkDisplayBlock
+ BlockEntity / Data Component
+ drink_id / remaining_cups
+ 动态模型或有限模型 Variant
```

目标：

- 手持饮品可放置到桌面；
- 右键饮用；
- 破坏后保留饮品状态；
- 避免 70 个方块、70 组 Loot Table 和 70 组 Blockstate。

该系统应作为单独的后续功能，不阻塞当前 1.20.1 资源收尾。

---

## 6.6 Advancement 与引导缺失

建议将旧成就改为精简现代 Advancement：

```text
root：制作或获得咖啡种子
→ 收获咖啡生豆
→ 烘焙咖啡豆
→ 研磨咖啡粉
→ 制作第一杯美式
→ 制作拿铁
→ 制作五种不同饮品
→ 完成一次冷萃
→ 制作一款冰饮
→ 成为咖啡大师
```

Advancement 的目标是引导玩家理解：

```text
种植
→ 烘焙
→ 研磨
→ 咖啡机
→ 糖浆 / 冰饮 / 冷萃
```

若本轮不实现，则删除旧 `achievement.*` 文本并将 Advancement 标记为 `PORT_LATER`。

---

## 7. 文档与元数据收尾

## 7.1 README

当前 README 仍只有简短旧说明，并提到旧辅助工具。

需要重写，至少包含：

- 模组简介；
- 支持版本；
- Forge 版本；
- 当前核心玩法；
- 安装方式；
- 咖啡生产流程；
- 五台机器介绍；
- JEI 建议；
- 构建命令；
- 许可证；
- 原作者与移植维护者；
- 当前已知未移植内容。

## 7.2 CONTENT_INVENTORY

现有 `CONTENT_INVENTORY.md` 是 Phase 0 快照，已经与当前代码严重不一致。

建议替换为新的机器可读清单：

```text
docs/CONTENT_MANIFEST.md
docs/content_manifest.json
```

字段：

```text
id
type
legacy_id
port_status
registered
translated
model
texture
survival_source
creative_tab
notes
```

旧文件保留为：

```text
reference/reports/CONTENT_INVENTORY_PHASE0.md
```

## 7.3 PORTING_DECISIONS

当前文件仍包含已经失效的说明，例如：

- Phase 4 饮品仍为创造模式限定；
- Coffee Machine Shift-click 尚未修复。

需要追加最终收尾条目，并清理或标记过时段落。

## 7.4 mods.toml 与 pack.mcmeta

检查：

- 模组版本是否由 Gradle 统一注入；
- `version="1.2.8.1"` 是否仍是预期发布版本；
- 增加主页或源码 URL；
- 更新作者和维护者信息；
- `pack.mcmeta` 描述改为正式内容；
- Logo 在游戏内 Mods 页面显示正常。

---

## 8. 优先级矩阵

## P0：发布资源阻断

1. 新增注册表—资源—来源一致性审计；
2. 补齐所有当前注册物品的 Item Model 和 Texture；
3. 补齐三个 Mob Effect 图标；
4. 补齐三个 Villager Profession 外观或明确取消专属外观；
5. 修复三语当前必需键缺失；
6. 清理英文 Registry Path 占位值；
7. 修复咖啡豆/咖啡粉/可可粉交易套利；
8. 补齐创造模式页遗漏物品；
9. 为所有当前注册物品确认生存来源或明确分类；
10. 解决两个职业共用同一 POI 的最终设计。

## P1：核心内容完整性

1. 五台机器工作状态视觉差异；
2. README、CONTENT_MANIFEST、PORTING_DECISIONS 更新；
3. 精简 Advancement 教程；
4. 恢复速溶杯装流程；
5. 恢复剩余冰淇淋；
6. 恢复剩余三明治；
7. 恢复第一批派和糕点；
8. 恢复苏打饮料；
9. 清理 BlockItem 与 Block ID 的遗留命名不一致，例如 `grinder_off`、`oven_off` 是否继续保留。

## P2：可选的原版完整度

1. 奶油与曲奇圣代；
2. 月饼；
3. 舒芙蕾；
4. 玛芬、蛋糕卷、摇晃蛋糕；
5. 通用放置式饮品展示系统；
6. 完整旧资产归档与仓库瘦身。

---

## 9. PR 拆分建议

## PR C0：内容清单与审计工具

新增：

```text
tools/audit_content_surface.py
tools/audit_lang_surface.py
tools/audit_trade_economy.py
docs/content_manifest.json
```

输出：

```text
注册但缺文本
注册但缺模型
注册但缺贴图
注册但缺来源
旧资源无注册对象
交易直接套利
```

不修改游戏内容，只建立真实基线。

---

## PR C1：文本与文档收尾

修改：

```text
en_us.json
zh_cn.json
ja_jp.json
README.md
CONTENT_MANIFEST.md
PORTING_DECISIONS.md
mods.toml
pack.mcmeta
```

完成：

- 当前键三语覆盖；
- 术语统一；
- 删除无效旧 UI/成就键，或标记待移植；
- README 和移植状态可对外发布。

---

## PR C2：当前注册内容资源补齐

补齐：

- Phase 4 新材料模型与贴图；
- Mob Effect 图标；
- Villager Profession 外观；
- 唱片模型/贴图/声音关联；
- 创造模式页遗漏；
- 当前 BlockItem 模型。

这是资源层最关键的 PR。

---

## PR C3：机器与方块视觉收尾

处理：

- 五台机器 `_on/_off` 模型；
- 机器粒子贴图；
- 不合理的 Vanilla 临时贴图；
- Block Model 与碰撞/轮廓一致性；
- 孤立 Blockstate 和重复模型。

Coffee Machine GUI 已通过人工验证，不在此 PR 重做。

---

## PR C4：玩法来源与经济闭环

处理：

- 村民交易套利；
- Barista/Materials Trader 工作站；
- 当前注册但无生存来源物品；
- Nitro Fruit Americano 路径；
- 唱片来源；
- 创造模式专属内容显式分类。

---

## PR C5：高价值原版内容恢复

建议第一批：

```text
杯装速溶咖啡
6 种冰淇淋
6 种剩余三明治
第一批派与糕点
8 种苏打饮料
```

所有新增内容必须同时提交：

```text
Registry
+ Translation
+ Model
+ Texture
+ Recipe/Source
+ Creative Tab
+ JEI visibility
```

---

## PR C6：孤立资源归档与发布清理

完成：

- 将 `PORT_LATER` 资源移动到 `reference/legacy-assets/`；
- 删除正式资源目录中的无效模型；
- 删除旧 `.lang` 和一次性迁移残留；
- 删除 `.bak` 工作流等非发布文件；
- 生成最终内容清单；
- 更新版本和 ChangeLog。

---

## 10. 最终验收标准

### 当前注册内容

- [ ] 每个注册 Item 有三语翻译；
- [ ] 每个注册 Item 有有效 Item Model；
- [ ] 每个 Item Model 的贴图存在；
- [ ] 每个注册 Block 有 Blockstate；
- [ ] 每个 Blockstate 的模型和贴图存在；
- [ ] 每个生存方块有合理 Loot Table；
- [ ] 每个 Mob Effect 有图标；
- [ ] 每个 Villager Profession 有明确工作站、翻译和外观策略；
- [ ] 每个 SoundEvent 有 `sounds.json` 和 OGG；
- [ ] 每个面向玩家的物品有生存来源或明确分类；
- [ ] 创造模式页包含全部当前内容；
- [ ] 不存在直接村民交易套利。

### 遗留内容

- [ ] 每个原版内容 ID 已分类；
- [ ] 无未分类旧语言键；
- [ ] 无未分类孤立模型；
- [ ] 无未分类孤立贴图；
- [ ] `PORT_LATER` 资源已归档；
- [ ] `REMOVED` 内容已从正式资源目录删除；
- [ ] 放置式饮品已确定“通用系统”或“正式移除”。

### 文档

- [ ] README 与当前玩法一致；
- [ ] CONTENT_MANIFEST 与当前代码一致；
- [ ] PORTING_DECISIONS 不再包含失效结论；
- [ ] mods.toml、pack.mcmeta、版本信息适合发布；
- [ ] 生成最终移植完成报告。

### 构建门禁

测试继续后移，本轮只要求：

```bash
python tools/audit_resources.py
python tools/audit_content_surface.py
python tools/audit_lang_surface.py
python tools/audit_trade_economy.py
./gradlew runData build --no-daemon --stacktrace
python tools/report_recipe_reachability.py
```

视觉内容采用人工抽查，不新增客户端自动测试阻断。

---

## 11. 推荐最终状态定义

完成 P0 与 P1 后，项目可以正式标记：

```text
Coffee Workshop 1.20.1 Port:
resource-complete and content-closure complete
```

这不代表 1:1 恢复了原版全部数百种食品与放置式饮品，而代表：

1. 当前发布内容全部完整；
2. 当前内容全部可发现、可显示、可获取；
3. 原版未移植内容全部有明确决策；
4. 正式 JAR 不再携带大量不确定的旧资源；
5. 后续内容扩展建立在清晰清单上，而不是继续从遗留文件中随机恢复。
