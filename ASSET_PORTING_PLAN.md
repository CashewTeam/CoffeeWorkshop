# Coffee Workshop — Asset Porting Plan (1.12.2 → 1.20.1)

> 基于对代码注册表与现有资源文件的完整对比审计  
> 文档日期: 2026-07-25

---

## 一、资产现状总览

| 类别 | 现有 | 需要 | 状态 |
|---|---|---|---|
| **Blockstates** | 126 文件 | 54 方块注册 | ✅ 覆盖，有 72 个旧版遗留 |
| **Block Models** | 287 文件 | ~54 主模型 + 变体 | ⚠️ 需添加 `render_type` |
| **Item Models** | 462 文件 | 111 物品注册 | ⚠️ 缺 10 个 |
| **Textures** | 483 文件 | 多 | ✅ 丰富但分布杂乱 |
| **Lang 文件** | 3 文件 (1659 行) | 3 文件 | ✅ `.lang` 格式可用 |
| **GUI Textures** | 5 文件 | 5 机器 GUI | ✅ |
| **Sounds** | 3 唱片 + sounds.json | 3 音效 | ✅ |
| **pack.mcmeta** | pack_format=15 | 正确 | ✅ |

---

## 二、缺失资产清单

### 2.1 缺少 Item Model (10 项)

这些物品注册了但没有对应的 `models/item/xxx.json`:

| 物品 Registry 名 | 纹理是否存在 | 解决方式 |
|---|---|---|
| `cake_sponge_slice` | `items/cake_sponge_slices.png` 存在 (文件名不同) | 创建 item model 引用 `items/cake_sponge_slices` |
| `coffee_seeds` | ❌ 无纹理 | 需要制作或复用 `coffee_bean_raw` 纹理 |
| `field_ration` | ❌ 无纹理 | 需要制作或复用已有食材纹理 |
| `record_kusa_noshi_to_ne` | ❌ 无纹理 | 需要唱片纹理 (可复用原 1.12.2 资源) |
| `record_lazy_lady_kaguya` | ❌ 无纹理 | 同上 |
| `record_the_grimoire_of_marisa` | ❌ 无纹理 | 同上 |
| `coffee_machine` | `gui/coffee_machine.png` | BlockItem, 需创建 item model 引用方块纹理 |
| `icecream_machine` | `gui/icecream_machine.png` | BlockItem, 同上 |
| `oven_off` | ❌ | BlockItem, 需创建 item model |
| `roller` | `gui/roller.png` | BlockItem, 同上 |

### 2.2 模型需要添加 `render_type`

1.20.1 的 JSON 模型需要在顶层指定渲染层。以下方块需要 `"render_type": "cutout"`:

| 方块 | 理由 |
|---|---|
| `coffee_tree` | 树叶/植株透贴 |
| `blueberry_bush` | 灌木透贴 |
| `vanilla_crop` | 作物透贴 |
| `plate` | 扁平碟子 |
| `coldbrew_pot` | 复杂形状 |
| `bag_*` | 软袋形状 |
| `double_bag_*` | 同上 |
| `cake_*` | 蛋糕纹理 |
| `grinder_*` | 机器可能有镂空 |
| 所有带 `FACING` 属性的方块 | 确保模型正确渲染 |

> 如果所有方块都用 solid，可以不写 `render_type`（默认为 solid）。  
> 上述方块如果原本在 1.12.2 用了 `BlockRenderLayer.CUTOUT`，在 1.20.1 就需要加。

### 2.3 旧版遗留 Blockstates (72 个不需要的)

以下 blockstate 来自 1.12.2 的咖啡杯碟系统（动态注册的 drink plates），在 1.20.1 的代码中不再注册为独立方块。可以删除或留作参考：

```
cocoa_plate.json        coffee_americano_plate.json
cocoa_ice_plate.json    coffee_americano_fruit_plate.json
cocoa_marshmallow_plate.json  ...
... 还有 60 多个 *plate.json
```

**处理方式**: 保留不动，不影响编译，但不注册的方块不会在游戏内出现。

### 2.4 Lang 文件更新项

部分翻译 key 需要更新以匹配 1.20.1 注册名：

| 旧 Key (1.12.2) | 新 Key (1.20.1) | 文件 |
|---|---|---|
| `tile.coffeework.grinder.name` | `block.coffeework.grinder_off` | lang |
| `item.coffeework.coffee_bean_raw.name` | `item.coffeework.coffee_bean_raw` | lang |
| `container.coffeework.grinder` | 新增 | 需要添加到 lang 文件 |
| `itemGroup.coffee_workshop` | 新增 | 需要添加到 lang 文件 |

对于 1.20.1 的 block 和 item，注册名即为翻译 key:
- Blocks: `block.coffeework.<registry_name>`
- Items: `item.coffeework.<registry_name>`
- Container: `container.coffeework.<menu_type_name>`

---

## 三、阶段执行计划

### Phase 1: 修复立即缺失 (10 Item Models)

为 10 个缺少 item model 的注册项创建 JSON 模型文件。简单的直接引用已有纹理。

```json
// model/item/cake_sponge_slice.json
{
    "parent": "item/generated",
    "textures": {
        "layer0": "coffeework:items/cake_sponge_slices"
    }
}
```

对于 BlockItem（coffee_machine, icecream_machine, oven_off, roller）：
```json
{
    "parent": "coffeework:block/<registry_name>"
}
```

### Phase 2: 添加 render_type 到 Block Models

给所有需要半透明渲染的方块模型添加 `"render_type": "cutout"` 或 `"render_type": "cutout_mipped"`。

```json
{
    "parent": "block/cross",
    "render_type": "cutout",
    "textures": {
        "cross": "coffeework:blocks/coffee_tree"
    }
}
```

### Phase 3: Lang 文件更新

1. 保留 `.lang` 格式（1.20.1 仍兼容）
2. 添加新增的翻译 key:
   - 容器标题: `container.coffeework.grinder`, `container.coffeework.coffee_machine` 等
   - 创造标签页: `itemGroup.coffee_workshop`
   - 药水效果: `effect.coffeework.caffeine`, `effect.coffeework.relax`, `effect.coffeework.golden_heart`
   - 职业: `entity.minecraft.villager.coffeework.coffee_barista` 等
   - 唱片描述: `item.coffeework.record_xxx.desc`

### Phase 4: GUI 纹理确认

| Screen | 纹理路径 | 状态 |
|---|---|---|
| GuiGrinder | `textures/gui/grinder.png` | ✅ 存在 |
| GuiCoffeeMachine | `textures/gui/coffee_machine.png` | ✅ 存在 |
| GuiIcecreamMachine | `textures/gui/icecream_machine.png` | ✅ 存在 |
| GuiRoller | `textures/gui/roller.png` | ✅ 存在 |
| GuiOven | 复用 `textures/gui/roller.png` | ⚠️ 原 1.12.2 就复用，如有需要可拆独立纹理 |

### Phase 5: 清理旧版资源 (可选)

删除不再使用的 72 个旧版 drink-plate blockstate 和对应的 model/texture 文件。

---

## 四、已知问题 & 风险

1. **Old model references**: 部分 1.12.2 模型文件引用了其他旧 mod 的纹理或方块状态，可能造成 missing model
2. **Block model variant names**: 1.12.2 blockstate 中使用了 `facing=north` 等格式，1.20.1 兼容
3. **Texture format**: 所有纹理都是 PNG，1.20.1 兼容
4. **Sounds.json**: 格式兼容，但唱片音频文件路径需要确认: `sounds/records/xxx.ogg`
5. **语言文件编码**: `.lang` 文件使用 UTF-8 with BOM，1.20.1 兼容

---

## 五、快速启动指南

```bash
# 1. 创建缺失的 10 个 item model
# 2. 给需要 cutout 的 block model 添加 render_type
# 3. 补全 lang 文件中的新增 key
# 4. ./gradlew runClient 测试
```

> **优先级**: Item Models > render_type > Lang > 清理旧资源
