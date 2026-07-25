# AGENTS.md

## 1. 文档目的

本文件规定所有自动化开发 Agent、Coding Agent 和协作开发者在 Coffee Workshop 移植工作中的行为准则。

项目目标不是让 1.20.1 分支“能够编译”或“注册项存在”，而是：

1. 尽可能恢复 `master` 分支中 Minecraft 1.12.2 版本的可观察功能；
2. 在无法直接等价实现时，采用符合 Minecraft 1.20.1 / Forge 47.x 规范的现代实现；
3. 保持玩法、生产链、交互语义、资源表现和存档数据的一致性；
4. 避免为了缩短开发时间而静默删除、简化或替换旧功能；
5. 所有完成项必须可以通过构建、数据生成、资源审计和测试证明。

本规范适用于仓库根目录及所有子目录。子目录中的补充 `AGENTS.md` 可以增加更具体的规则，但不得降低本文件的移植完整性和测试要求。

---

## 2. 项目基线

### 2.1 分支职责

* `master`：Minecraft 1.12.2 旧版功能基线。
* `1.20.1`：Minecraft 1.20.1 / Forge 47.x 目标实现。
* Mod ID：`coffeework`。
* Java：17。
* 映射：Mojang Official Mappings。
* 数据生成输出：`src/generated/resources`。

除非任务明确指定其他分支，所有新增开发应基于 `1.20.1`。

### 2.2 旧版的角色

`master` 分支不是可以直接复制的代码模板，而是**功能与行为基线**。

Agent 必须从旧版提取：

* 玩家可观察行为；
* 方块和物品状态；
* 配方和生产链；
* GUI 与槽位语义；
* 掉落与容器返还；
* 饮品、食品和药水效果；
* 粒子、声音和动画；
* NBT、TileEntity 数据和存档状态；
* 村民、世界生成与兼容逻辑；
* 资源名称及其实际用途。

旧版 API、metadata、TileEntity、IBlockState、`onBlockActivated`、旧 GUI 网络方式等不能机械复制到 1.20.1。

---

## 3. 指令和事实优先级

发生冲突时，按以下顺序执行：

1. 当前任务中用户明确提出的要求和验收标准；
2. 本 `AGENTS.md`；
3. `docs/PORTING_DECISIONS.md` 中已确认的设计决策；
4. 当前阶段最新执行计划；
5. `master` 分支的可观察行为；
6. `docs/CONTENT_INVENTORY.md` 和其他状态文档；
7. 当前 `1.20.1` 实现；
8. Agent 自行推断。

如果旧版行为与已批准的新设计冲突，以 `PORTING_DECISIONS.md` 为准。

如果旧版行为不明确：

1. 检查旧类的所有调用点；
2. 检查旧资源、配方、语言和 GUI；
3. 检查相同系统中的其他类；
4. 优先保留玩家可观察结果；
5. 采用最小、可逆、可测试的假设；
6. 在 `PORTING_DECISIONS.md` 记录假设及原因。

不得静默发明新规则。

---

## 4. Agent 开始任务前必须执行的工作

### 4.1 阅读上下文

在修改代码前，至少检查：

* 本文件；
* `docs/PORTING_DECISIONS.md`；
* 当前阶段执行计划；
* `docs/CONTENT_INVENTORY.md`；
* 相关 `1.20.1` 源码和资源；
* 对应的 `master` 旧版源码和资源；
* 相关测试；
* 最近影响同一系统的提交。

### 4.2 建立行为对照

每个移植任务开始前，先列出行为对照：

| 项目     | 旧版行为 | 当前行为 | 本次目标 |
| ------ | ---- | ---- | ---- |
| 注册与名称  |      |      |      |
| 获得方式   |      |      |      |
| 交互     |      |      |      |
| 状态     |      |      |      |
| 配方/加工  |      |      |      |
| 掉落/返还  |      |      |      |
| GUI/同步 |      |      |      |
| 粒子/声音  |      |      |      |
| 存档数据   |      |      |      |
| 自动化    |      |      |      |

对于小改动，该表可以写在工作记录或 PR 描述中；对于系统级改动，应更新项目文档。

### 4.3 检查完整调用链

不得只修改最先发现的类。必须检查：

```text
注册
→ 方块/物品类
→ BlockEntity
→ Menu
→ Screen
→ Recipe
→ DataGen
→ JSON 资源
→ Loot/Tags
→ JEI
→ NBT/网络同步
→ 测试
```

一个功能在链路任意环节断裂，都不能视为移植完成。

---

## 5. 移植的核心原则

### 5.1 优先保持行为等价

除非已有明确设计决策，以下行为应尽量与旧版相同：

* 输入和输出物品；
* 产出数量；
* 加工时间；
* 燃料与冷却剂；
* 食物营养和效果；
* 多杯饮品杯数；
* 空杯、桶、碗和模具返还；
* 作物生长阶段；
* 成熟采摘行为；
* 蛋糕进食次数；
* 冷萃发酵阶段；
* 方块破坏掉落；
* 村民职业和工作站；
* 世界生成频率与环境；
* GUI 槽位职责；
* 粒子、声音和运行状态。

允许因现代 API 做内部架构调整，但玩家看到的结果应尽量一致。

### 5.2 不得用临时方案冒充完成

以下行为不允许标记为“已移植”：

* 只注册 Item 或 Block；
* 只添加创造栏入口；
* 只添加模型和语言；
* 只保证代码编译；
* 用普通工作台配方替代旧机器流程；
* 用创造模式作为唯一获取方式；
* 删除旧交互但保留空类；
* 忽略容器返还、经验、粒子或声音；
* 用 TODO 注释代替实现；
* 将旧功能标记为“简化完成”却没有批准决策。

临时 fallback 必须：

1. 明确标记为临时；
2. 写入内容清单；
3. 不得计入行为完成；
4. 指定后续恢复阶段。

### 5.3 现代化不等于改玩法

现代化应优先体现在：

* 注册方式；
* 数据驱动；
* 服务端权威；
* Capability；
* RecipeManager；
* DataGen；
* GameTest；
* 客户端隔离；
* 类型安全；
* 可重载资源；
* 可维护架构。

不得以“现代化”为由随意改变产量、配方、机器职责或游戏平衡。

---

## 6. Minecraft 1.20.1 / Forge 实现规范

### 6.1 注册

必须使用：

* `DeferredRegister`；
* `RegistryObject`；
* 稳定的 `ResourceLocation`；
* 明确的 Mod Event Bus 注册。

不得：

* 在静态初始化中直接访问尚未就绪的 RegistryObject；
* 随意修改已冻结的 registry ID；
* 重复注册 `_on` / `_off` 方块表示运行状态；
* 使用 1.12.2 metadata 模拟现代状态。

机器运行状态应优先使用 `BlockStateProperties.LIT`。

### 6.2 逻辑端与客户端

真实游戏状态只允许在逻辑服务端修改，包括：

* 物品栏；
* 加工进度；
* 燃料；
* 方块状态；
* 掉落；
* 经验；
* 作物成长；
* 饮品杯数；
* NBT。

客户端只负责：

* Screen；
* 渲染；
* 粒子和本地音效表现；
* 预测性动画；
* 展示同步数据。

客户端专用类必须位于客户端包或客户端事件订阅中，不得从通用 Mod 初始化代码直接加载。

### 6.3 BlockEntity

BlockEntity 必须：

* 正确保存和加载 NBT；
* 调用 `setChanged()`；
* 必要时发送 Block Update；
* 管理 Capability 的 `onLoad`、`invalidateCaps` 和 `reviveCaps`；
* 在方块移除时正确处理库存；
* 不在客户端执行服务器加工；
* 不通过替换整个方块丢失 BlockEntity；
* 不保存无法跨重载使用的对象引用。

新增 NBT 字段必须考虑旧开发存档中字段缺失的情况。

### 6.4 ItemStack

始终注意：

* 输出使用 `copy()` 或 `recipe.assemble()`；
* 比较 Item 与 NBT/组件；
* 检查最大堆叠数量；
* 不静默截断结果；
* 不将可变 `ItemStack` 作为 HashMap key；
* 不使用同一个可变 Stack 同时表示配方模板和真实库存；
* 容器返还只消费一个输入；
* 多物品堆叠不能被一个 remainder 覆盖。

### 6.5 Recipe

机器配方必须：

* 数据驱动；
* 由 `RecipeManager` 加载；
* 支持 `/reload`；
* 有合法 Serializer；
* 有明确 RecipeType；
* 对非法时间、经验、数量和空结果进行校验；
* 通过 `assemble()` 生成结果；
* 可以被 JEI 枚举。

不得长期使用静态内存 Map 作为正式配方系统。

不得跨 `/reload` 缓存 `MachineRecipe` 对象。需要缓存时，只保存 Recipe ID，并通过当前 RecipeManager 重新解析。

### 6.6 Capability 与自动化

自动化访问必须遵循槽位职责。

燃料机器默认规则：

* 顶部：输入；
* 水平方向：燃料；
* 底部：输出；
* `side == null`：GUI 和内部代码可访问完整 Handler。

Coffee Machine 等无燃料机器可将顶部和侧面作为输入，底部作为输出。

不得向任意方向暴露完整可写物品栏。

### 6.7 菜单与 Screen

Menu 负责：

* 服务器槽位；
* shift-click；
* `stillValid()`；
* 数据同步；
* 结果槽；
* 玩家背包。

Screen 只负责显示。

槽位编号一旦被现有 GUI、网络数据或存档依赖，不得无理由改变。

所有机器输出槽应使用统一结果槽逻辑，正确处理：

* 禁止插入；
* 玩家取出；
* shift-click；
* crafted callback；
* 配方经验；
* Advancement/Recipe Award。

---

## 7. 资源移植规范

### 7.1 资源完整性

每个发布内容都必须检查：

* blockstate；
* block model；
* item model；
* texture；
* language key；
* Loot Table；
* Block/Item Tags；
* sound；
* particle；
* GUI texture；
* JEI 显示；
* Recipe；
* Advancement。

不得认为“PNG 已复制”就表示资源完成。

### 7.2 现代资源格式

必须使用现代路径：

```text
assets/coffeework/models/block/
assets/coffeework/models/item/
assets/coffeework/textures/block/
assets/coffeework/textures/item/
assets/coffeework/lang/*.json
data/coffeework/recipes/
data/coffeework/loot_tables/
data/coffeework/tags/
```

不得重新引入：

* `.lang`；
* `forge_marker`；
* `defaults`；
* `blocks/...` 和 `items/...` 旧原版路径；
* 错误命名空间 `coffeeworkshop`；
* 已不存在的原版纹理名称。

### 7.3 方块状态

Java 属性与 blockstate JSON 必须一一对应。

重点检查：

* `facing`；
* `lit`；
* `age`；
* `bites`；
* `ferm`；
* 其他布尔或整数状态。

新增或修改属性后必须检查所有可能状态是否有模型。

### 7.4 旧资源复用

允许复用旧版自有模型和纹理，但必须：

* 确认许可证允许；
* 更新原版资源引用；
* 检查 UV 和父模型；
* 检查透明渲染层；
* 检查大小写；
* 检查命名空间；
* 检查运行状态模型。

不得复制 Mojang 不允许分发的原版资源文件作为替代方案。

---

## 8. DataGen 规范

可以由 DataGen 生成的内容应优先由 Provider 生成，包括：

* Recipes；
* Machine Recipes；
* Advancements；
* Loot Tables；
* Block Tags；
* Item Tags；
* Blockstates；
* Item Models；
* Languages。

规则：

1. 不手工编辑 `src/generated/resources` 中可生成文件；
2. 修改 Provider 后执行 `runData`；
3. 提交生成结果；
4. `runData` 后工作区必须保持干净；
5. Provider 与生成 JSON 必须在同一个提交中；
6. 不提交 DataGen 临时缓存；
7. Recipe 解锁条件应基于合理输入，而不是结果本身；
8. 工具、模具、桶、碗和杯具必须正确返还。

DataGen 输出与手工资源冲突时，必须先确定唯一数据源。

---

## 9. 功能完整性检查

每个内容项使用以下状态：

| 标记 | 含义           |
| -- | ------------ |
| R  | Registry 已注册 |
| A  | Assets 完整    |
| O  | 生存模式可获得      |
| B  | 行为达到移植目标     |
| T  | 已通过测试        |

只有 `R + A + O + B + T` 全部完成，才能标记为完成。

### 9.1 可获得性

Agent 必须证明物品可以通过至少一种合理方式获得：

* 作物；
* 掉落；
* 世界生成；
* 工作台配方；
* 机器配方；
* 村民交易；
* 战利品；
* 其他明确系统。

创造栏和 `/give` 不算生存获取方式。

### 9.2 生产链

修改生产内容时，应检查从原料到最终产品的整条链。

例如咖啡：

```text
咖啡树
→ 生咖啡豆
→ 烘焙咖啡豆
→ 研磨
→ 咖啡粉
→ 冲泡
→ 饮品
→ 多杯饮用
→ 空杯返还
```

任意一步断裂，都不能将最终饮品标记为 O/B 完成。

---

## 10. 测试要求

### 10.1 每次修改的最低验证

至少执行：

```bash
./gradlew compileJava --no-daemon --stacktrace
./gradlew runData --no-daemon --stacktrace
python tools/audit_resources.py
./gradlew build --no-daemon --stacktrace
```

如果项目已启用 GameTest，还必须执行：

```bash
./gradlew runGameTestServer --no-daemon --stacktrace
```

检查 DataGen：

```bash
git status --porcelain --untracked-files=all -- src/generated/resources
```

结果必须为空。

### 10.2 需要 GameTest 的改动

以下改动原则上必须新增或更新 GameTest：

* 机器加工；
* 燃料和冷却剂；
* 输入输出；
* 容器返还；
* Sided Capability；
* NBT 保存加载；
* 配方切换；
* LIT 状态；
* 作物成长和收获；
* 蛋糕进食；
* 冷萃发酵；
* 掉落数量；
* 物品复制漏洞；
* 经验结算。

不能自动测试的视觉项目必须提供明确手工测试步骤。

### 10.3 回归测试

修复一个 bug 时，必须添加能在修复前失败、修复后通过的测试，尤其是：

* 无限复制；
* 无限饮用；
* 吞物；
* 配方错误缓存；
* 输出溢出；
* 客户端状态漂移；
* 无效侧面自动化；
* 重复经验。

---

## 11. CI 与跨平台要求

代码必须在 GitHub Actions 的 Linux 环境构建。

不得提交：

* 本机绝对路径；
* Windows 专用 JDK 路径；
* 用户目录；
* IDE 私有配置；
* 本地密钥；
* 平台专用 shell 假设；
* 未声明的外部工具依赖。

Java 位置由 Toolchain、`JAVA_HOME` 或 CI 设置，不得在共享 `gradle.properties` 中写死。

CI 至少应验证：

* Java 17；
* Gradle Wrapper；
* `compileJava`；
* `runData`；
* 生成资源无差异；
* 资源审计；
* GameTest；
* `build`；
* JAR 产物存在。

---

## 12. 代码质量和架构

### 12.1 避免重复逻辑

当两个以上机器出现相同的：

* tick；
* 配方查询；
* 燃料处理；
* 输出检查；
* LIT 切换；
* Capability；
* Menu shift-click；

应优先抽取公共实现，而不是继续复制。

抽象必须保留机器差异，不得为了减少代码而硬编码错误的统一行为。

### 12.2 注释

注释重点解释：

* 为什么这样实现；
* 与旧版行为的关系；
* 现代 API 的替代方案；
* 不明显的兼容约束；
* 不能简化的边界条件。

不要为显而易见的赋值或 getter 添加冗余注释。

### 12.3 错误处理

不得静默忽略：

* 非法 Recipe JSON；
* 丢失 Registry 项；
* 空输出；
* 非法数量；
* 非法加工时间；
* 找不到 BlockEntity；
* Capability 不存在；
* 资源路径缺失。

开发阶段应尽早、清晰失败，并包含资源 ID 或方块位置。

---

## 13. 存档和兼容性

### 13.1 Registry ID

现有 1.20.1 registry ID 默认冻结。

修改 ID 前必须：

1. 证明修改必要；
2. 更新所有资源和数据；
3. 提供 Missing Mapping 或迁移方案；
4. 在 `PORTING_DECISIONS.md` 记录；
5. 测试旧开发存档。

### 13.2 NBT

新增 NBT 字段时：

* 缺失字段要有安全默认值；
* 不删除旧字段而不迁移；
* 不改变同名字段类型；
* 保存前后状态必须一致；
* 客户端同步字段与永久保存字段应区分。

### 13.3 可选依赖

JEI 和其他兼容 Mod 必须是可选的。

没有可选依赖时：

* 客户端可以启动；
* 专用服务器可以启动；
* 通用代码不得加载其类；
* 核心玩法不得失效。

---

## 14. 禁止事项

Agent 不得：

* 仅凭编译成功宣布移植完成；
* 未查看 `master` 就重新设计旧功能；
* 静默删除旧功能；
* 用直接合成永久替代机器加工；
* 修改平衡却不记录决策；
* 在客户端修改真实游戏状态；
* 缓存跨 `/reload` 的 Recipe 对象；
* 使用可变 ItemStack 作为 Map key；
* 向所有方向暴露完整机器物品栏；
* 复制五份相同机器 tick；
* 在共享配置中写死本机路径；
* 手工修改应由 DataGen 生成的文件；
* 重新引入 `.lang` 或旧 blockstate 格式；
* 忽略容器返还；
* 静默截断输出数量；
* 在输出阻塞时吞掉输入；
* 在 shift-click 时重复发经验；
* 通过漏斗直接刷玩家经验；
* 未更新文档就将延期内容标记完成；
* 将“创造栏可取”视为“生存可获得”；
* 创建大量重复 Registry 来规避合理的数据结构设计。

---

## 15. 提交和 PR 规范

每个提交或 PR 应聚焦单一目标。

PR 描述至少包含：

```text
旧版行为：
当前问题：
1.20.1 实现：
有意差异：
影响的 registry/NBT/资源：
验证命令：
测试结果：
未完成项：
```

原则：

* Provider 与生成资源一起提交；
* 功能与对应测试一起提交；
* 不混入无关格式化；
* 不大规模重命名同时修改行为；
* 不删除旧资源而不确认引用；
* 不在没有迁移方案时修改 Registry ID；
* 发现范围外严重问题时记录，不顺手进行未经审计的大改。

---

## 16. Agent 完成任务后的输出要求

完成工作后，Agent 必须汇报：

1. 修改了哪些文件；
2. 恢复了哪些旧版行为；
3. 使用了哪些 1.20.1 替代机制；
4. 哪些行为仍与旧版不同；
5. 是否改变 Registry ID 或 NBT；
6. 是否更新 DataGen；
7. 执行了哪些命令；
8. 哪些测试通过；
9. 哪些测试无法执行；
10. 是否更新内容矩阵和决策文档；
11. 后续仍需处理什么。

不得只回复“完成”“已修复”或“构建通过”。

---

## 17. 当前机器重构阶段的附加规则

在 Phase 2 期间：

1. 五台机器应逐步共享统一加工状态机；
2. 不再为每台机器复制完整 tick；
3. Recipe 缓存只保存 ID；
4. 输出必须通过 `assemble()` 生成；
5. Serializer 必须校验非法字段；
6. 配方变化必须更新 `totalCookTime`；
7. 输出阻塞行为必须统一；
8. 自动化必须按方向限制；
9. 经验字段必须真正结算；
10. Coffee Machine 只保留自供能差异；
11. Icecream Machine 不使用 ItemStack 作为燃料 Map key；
12. 迁移过程中不得改变现有 registry ID 和槽位编号；
13. 每迁移一台机器，都必须运行该机器的回归测试；
14. 公共基类稳定后再开始大规模添加机器配方。

---

## 18. 最终原则

在每次实现选择前，Agent 应依次回答：

1. 旧版玩家实际体验是什么？
2. 当前实现缺失了哪一部分？
3. 1.20.1 中正确的现代实现是什么？
4. 这个修改是否保持了行为等价？
5. 它是否破坏存档、资源、生产链或自动化？
6. 如何通过测试证明它有效？
7. 是否需要记录有意差异？

如果无法回答这些问题，说明尚未完成足够的审计，不应直接提交代码。

Coffee Workshop 的移植完成标准是：

> 不仅能够启动，而且旧版核心玩法在 1.20.1 中以现代、稳定、数据驱动、可测试的方式重新成立。
