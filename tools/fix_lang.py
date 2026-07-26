#!/usr/bin/env python3
"""
Bulk-fix language files based on audit_content_surface.py findings.

Fixes:
1. en_us.json: Replace 43 placeholder values with proper English names
2. en_us.json: Add 9 missing required keys
3. zh_cn.json: Fix typos (冰淇凌→冰淇淋, 烘培→烘焙, coffee_seed→coffee_seeds)
4. zh_cn.json: Add ~65 missing keys
5. ja_jp.json: Add ~65 missing keys
"""

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LANG_DIR = ROOT / "src" / "main" / "resources" / "assets" / "coffeework" / "lang"


def load_json(path):
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


def save_json(path, data):
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write("\n")


# ── en_us.json fixes ──────────────────────────────────────────────

EN_PLACEHOLDERS = {
    # Bags
    "item.coffeework.bag_cocoa": "Cocoa Bean Bag",
    "item.coffeework.bag_cocoa_powder": "Cocoa Powder Bag",
    "item.coffeework.bag_coffee": "Coffee Bean Bag",
    "item.coffeework.bag_coffee_powder": "Coffee Powder Bag",
    "item.coffeework.bag_coffee_raw": "Raw Coffee Bean Bag",
    "item.coffeework.bag_flour": "Flour Bag",
    "item.coffeework.bag_sugar": "Sugar Bag",
    # Double Bags
    "item.coffeework.double_bag_cocoa": "Double Cocoa Bean Bag",
    "item.coffeework.double_bag_cocoa_powder": "Double Cocoa Powder Bag",
    "item.coffeework.double_bag_coffee": "Double Coffee Bean Bag",
    "item.coffeework.double_bag_coffee_powder": "Double Coffee Powder Bag",
    "item.coffeework.double_bag_coffee_raw": "Double Raw Coffee Bean Bag",
    "item.coffeework.double_bag_flour": "Double Flour Bag",
    "item.coffeework.double_bag_sugar": "Double Sugar Bag",
    # Cakes
    "item.coffeework.cake_berry": "Berry Cake",
    "item.coffeework.cake_cheese": "Cheese Cake",
    "item.coffeework.cake_coffee": "Coffee Cake",
    "item.coffeework.cake_harvest": "Harvest Cake",
    "item.coffeework.cake_lemon": "Lemon Cake",
    "item.coffeework.cake_redvelvet": "Red Velvet Cake",
    "item.coffeework.cake_schwarzwald": "Schwarzwald Cake",
    "item.coffeework.cake_tea": "Matcha Cake",
    # Sponge Cakes
    "item.coffeework.cake_sponge": "Sponge Cake",
    "item.coffeework.cake_sponge_berry": "Berry Sponge Cake",
    "item.coffeework.cake_sponge_carrot": "Carrot Sponge Cake",
    "item.coffeework.cake_sponge_chocolate": "Chocolate Sponge Cake",
    "item.coffeework.cake_sponge_coffee": "Coffee Sponge Cake",
    "item.coffeework.cake_sponge_lemon": "Lemon Sponge Cake",
    "item.coffeework.cake_sponge_pumpkin": "Pumpkin Sponge Cake",
    "item.coffeework.cake_sponge_redvelvet": "Red Velvet Sponge Cake",
    "item.coffeework.cake_sponge_slice": "Sponge Cake Slice",
    "item.coffeework.cake_sponge_tea": "Matcha Sponge Cake",
    # Mousses
    "item.coffeework.mousse_berry": "Berry Mousse",
    "item.coffeework.mousse_chocolate": "Chocolate Mousse",
    "item.coffeework.mousse_coffee": "Coffee Mousse",
    "item.coffeework.mousse_lemon": "Lemon Mousse",
    # Other
    "item.coffeework.field_ration": "Field Ration",
    "item.coffeework.tiramisu": "Tiramisu",
    "item.coffeework.coffee_seeds": "Coffee Seeds",
    "block.coffeework.vanilla_crop": "Vanilla Crop",
    # Records
    "item.coffeework.record_kusa_noshi_to_ne": "Music Disc",
    "item.coffeework.record_lazy_lady_kaguya": "Music Disc",
    "item.coffeework.record_the_grimoire_of_marisa": "Music Disc",
}

EN_MISSING = {
    # Missing block translation keys (blocks use _off suffix items, so clean block names need their own keys)
    "block.coffeework.grinder": "Thermal Grinder",
    "block.coffeework.coffee_machine": "Coffee Machine",
    "block.coffeework.icecream_machine": "Ice Cream Machine",
    "block.coffeework.roller": "Thermal Roller",
    "block.coffeework.oven": "Clay Oven",
    # JEI cooling tip (shown in JEI cooling category)
    "jei.coffeework.cooling.tip": "Add ice slag to cool hot drinks",
    # Record descriptions (shown on music disc tooltip)
    "item.coffeework.record_kusa_noshi_to_ne.desc": "Kusa Noshi To Ne",
    "item.coffeework.record_lazy_lady_kaguya.desc": "Lazy Lady Kaguya",
    "item.coffeework.record_the_grimoire_of_marisa.desc": "The Grimoire of Marisa",
}

# ── zh_cn.json translations for missing keys ──────────────────────

ZH_NEW = {
    # Machines
    "item.coffeework.grinder_off": "火力研磨机",
    "item.coffeework.oven_off": "黏土烤炉",
    "item.coffeework.coffee_machine": "咖啡机",
    "item.coffeework.icecream_machine": "冰淇淋机",
    "item.coffeework.roller": "火力辊压机",
    "block.coffeework.grinder": "火力研磨机",
    "block.coffeework.coffee_machine": "咖啡机",
    "block.coffeework.icecream_machine": "冰淇淋机",
    "block.coffeework.roller": "火力辊压机",
    "block.coffeework.oven": "黏土烤炉",
    # Bags
    "item.coffeework.bag_cocoa": "可可豆袋",
    "item.coffeework.bag_cocoa_powder": "可可粉袋",
    "item.coffeework.bag_coffee": "咖啡豆袋",
    "item.coffeework.bag_coffee_powder": "咖啡粉袋",
    "item.coffeework.bag_coffee_raw": "生咖啡豆袋",
    "item.coffeework.bag_flour": "面粉袋",
    "item.coffeework.bag_sugar": "糖袋",
    # Double Bags
    "item.coffeework.double_bag_cocoa": "双可可豆袋",
    "item.coffeework.double_bag_cocoa_powder": "双可可粉袋",
    "item.coffeework.double_bag_coffee": "双咖啡豆袋",
    "item.coffeework.double_bag_coffee_powder": "双咖啡粉袋",
    "item.coffeework.double_bag_coffee_raw": "双生咖啡豆袋",
    "item.coffeework.double_bag_flour": "双面粉袋",
    "item.coffeework.double_bag_sugar": "双糖袋",
    # Blocks
    "item.coffeework.blueberry_bush": "蓝莓丛",
    "item.coffeework.coffee_tree": "咖啡树",
    "item.coffeework.coffee_seeds": "咖啡种子",
    "item.coffeework.coldbrew_pot": "冷萃壶",
    "item.coffeework.ginger_house": "姜饼屋",
    "item.coffeework.plate": "盘子",
    "item.coffeework.soda_ore": "苏打矿石",
    "item.coffeework.xmas_tree": "圣诞树",
    "block.coffeework.vanilla_crop": "香草作物",
    # Cakes
    "item.coffeework.cake_berry": "浆果蛋糕",
    "item.coffeework.cake_cheese": "芝士蛋糕",
    "item.coffeework.cake_coffee": "咖啡蛋糕",
    "item.coffeework.cake_harvest": "丰收蛋糕",
    "item.coffeework.cake_lemon": "柠檬蛋糕",
    "item.coffeework.cake_redvelvet": "红丝绒蛋糕",
    "item.coffeework.cake_schwarzwald": "黑森林蛋糕",
    "item.coffeework.cake_tea": "抹茶蛋糕",
    "item.coffeework.tiramisu": "提拉米苏",
    # Sponge Cakes
    "item.coffeework.cake_sponge": "海绵蛋糕胚",
    "item.coffeework.cake_sponge_berry": "浆果海绵蛋糕胚",
    "item.coffeework.cake_sponge_carrot": "胡萝卜海绵蛋糕胚",
    "item.coffeework.cake_sponge_chocolate": "巧克力海绵蛋糕胚",
    "item.coffeework.cake_sponge_coffee": "咖啡海绵蛋糕胚",
    "item.coffeework.cake_sponge_lemon": "柠檬海绵蛋糕胚",
    "item.coffeework.cake_sponge_pumpkin": "南瓜海绵蛋糕胚",
    "item.coffeework.cake_sponge_redvelvet": "红丝绒海绵蛋糕胚",
    "item.coffeework.cake_sponge_slice": "海绵蛋糕片",
    "item.coffeework.cake_sponge_tea": "抹茶海绵蛋糕胚",
    # Mousses
    "item.coffeework.mousse_berry": "浆果慕斯",
    "item.coffeework.mousse_chocolate": "巧克力慕斯",
    "item.coffeework.mousse_coffee": "咖啡慕斯",
    "item.coffeework.mousse_lemon": "柠檬慕斯",
    # Misc
    "item.coffeework.field_ration": "野战口粮",
    "item.coffeework.icecream_mix_vanilla": "香草冰淇淋混合料",
    "item.coffeework.roller": "火力辊压机",
    # Records
    "item.coffeework.record_kusa_noshi_to_ne": "音乐唱片",
    "item.coffeework.record_lazy_lady_kaguya": "音乐唱片",
    "item.coffeework.record_the_grimoire_of_marisa": "音乐唱片",
    "item.coffeework.record_kusa_noshi_to_ne.desc": "Kusa Noshi To Ne",
    "item.coffeework.record_lazy_lady_kaguya.desc": "Lazy Lady Kaguya",
    "item.coffeework.record_the_grimoire_of_marisa.desc": "The Grimoire of Marisa",
    # JEI
    "jei.coffeework.category.coffee_machine": "咖啡冲泡",
    "jei.coffeework.cooling.tip": "加入冰渣冷却热饮",
}

# ── ja_jp.json translations for missing keys ──────────────────────

JA_NEW = {
    # Machines
    "item.coffeework.grinder_off": "火力粉砕機",
    "item.coffeework.oven_off": "粘土オーブン",
    "item.coffeework.coffee_machine": "コーヒーマシン",
    "item.coffeework.icecream_machine": "アイスクリームマシン",
    "item.coffeework.roller": "火力圧延機",
    "block.coffeework.grinder": "火力粉砕機",
    "block.coffeework.coffee_machine": "コーヒーマシン",
    "block.coffeework.icecream_machine": "アイスクリームマシン",
    "block.coffeework.roller": "火力圧延機",
    "block.coffeework.oven": "粘土オーブン",
    # Bags
    "item.coffeework.bag_cocoa": "カカオ豆の袋",
    "item.coffeework.bag_cocoa_powder": "ココアパウダーの袋",
    "item.coffeework.bag_coffee": "コーヒー豆の袋",
    "item.coffeework.bag_coffee_powder": "コーヒー粉の袋",
    "item.coffeework.bag_coffee_raw": "生コーヒー豆の袋",
    "item.coffeework.bag_flour": "小麦粉の袋",
    "item.coffeework.bag_sugar": "砂糖の袋",
    # Double Bags
    "item.coffeework.double_bag_cocoa": "カカオ豆の大袋",
    "item.coffeework.double_bag_cocoa_powder": "ココアパウダーの大袋",
    "item.coffeework.double_bag_coffee": "コーヒー豆の大袋",
    "item.coffeework.double_bag_coffee_powder": "コーヒー粉の大袋",
    "item.coffeework.double_bag_coffee_raw": "生コーヒー豆の大袋",
    "item.coffeework.double_bag_flour": "小麦粉の大袋",
    "item.coffeework.double_bag_sugar": "砂糖の大袋",
    # Blocks
    "item.coffeework.blueberry_bush": "ブルーベリーの低木",
    "item.coffeework.coffee_tree": "コーヒーの木",
    "item.coffeework.coffee_seeds": "コーヒーの種",
    "item.coffeework.coldbrew_pot": "水出しポット",
    "item.coffeework.ginger_house": "ジンジャーブレッドハウス",
    "item.coffeework.plate": "皿",
    "item.coffeework.soda_ore": "ソーダ鉱石",
    "item.coffeework.xmas_tree": "クリスマスツリー",
    "block.coffeework.vanilla_crop": "バニラ作物",
    # Cakes
    "item.coffeework.cake_berry": "ベリーケーキ",
    "item.coffeework.cake_cheese": "チーズケーキ",
    "item.coffeework.cake_coffee": "コーヒーケーキ",
    "item.coffeework.cake_harvest": "収穫祭ケーキ",
    "item.coffeework.cake_lemon": "レモンケーキ",
    "item.coffeework.cake_redvelvet": "レッドベルベットケーキ",
    "item.coffeework.cake_schwarzwald": "シュヴァルツヴァルトケーキ",
    "item.coffeework.cake_tea": "抹茶ケーキ",
    "item.coffeework.tiramisu": "ティラミス",
    # Sponge Cakes
    "item.coffeework.cake_sponge": "スポンジケーキ生地",
    "item.coffeework.cake_sponge_berry": "ベリースポンジ生地",
    "item.coffeework.cake_sponge_carrot": "ニンジンスポンジ生地",
    "item.coffeework.cake_sponge_chocolate": "チョコスポンジ生地",
    "item.coffeework.cake_sponge_coffee": "コーヒースポンジ生地",
    "item.coffeework.cake_sponge_lemon": "レモンスポンジ生地",
    "item.coffeework.cake_sponge_pumpkin": "パンプキンスポンジ生地",
    "item.coffeework.cake_sponge_redvelvet": "レッドベルベットスポンジ生地",
    "item.coffeework.cake_sponge_slice": "スポンジスライス",
    "item.coffeework.cake_sponge_tea": "抹茶スポンジ生地",
    # Mousses
    "item.coffeework.mousse_berry": "ベリームース",
    "item.coffeework.mousse_chocolate": "チョコレートムース",
    "item.coffeework.mousse_coffee": "コーヒームース",
    "item.coffeework.mousse_lemon": "レモンムース",
    # Misc
    "item.coffeework.field_ration": "フィールドレーション",
    "item.coffeework.icecream_mix_vanilla": "バニラアイスクリームミックス",
    "item.coffeework.black_tea_leaf": "紅茶の葉",
    "item.coffeework.tea_leaf": "茶葉",
    "item.coffeework.syrup_caramel": "キャラメルシロップ",
    "item.coffeework.syrup_chocolate": "チョコレートシロップ",
    "item.coffeework.syrup_mint": "ミントシロップ",
    "item.coffeework.syrup_sakura": "桜シロップ",
    "item.coffeework.roller": "火力圧延機",
    # Records
    "item.coffeework.record_kusa_noshi_to_ne": "レコード",
    "item.coffeework.record_lazy_lady_kaguya": "レコード",
    "item.coffeework.record_the_grimoire_of_marisa": "レコード",
    "item.coffeework.record_kusa_noshi_to_ne.desc": "Kusa Noshi To Ne",
    "item.coffeework.record_lazy_lady_kaguya.desc": "Lazy Lady Kaguya",
    "item.coffeework.record_the_grimoire_of_marisa.desc": "The Grimoire of Marisa",
    # JEI
    "jei.coffeework.category.coffee_machine": "コーヒー抽出",
    "jei.coffeework.cooling.tip": "氷滓を加えて冷たい飲み物に",
}

# ── zh_cn typo fixes ──────────────────────────────────────────────

ZH_TYPO_FIXES = {
    "block.coffeework.icecream_machine": "冰淇淋机",  # was 冰淇凌机
    "item.coffeework.icecream_vanilla": "香草冰淇淋",  # was 香草冰淇凌
    "item.coffeework.icecream_chocolate": "巧克力冰淇淋",
    "item.coffeework.icecream_coffee": "咖啡冰淇淋",
    "item.coffeework.icecream_apple": "苹果冰淇淋",
    "item.coffeework.icecream_berry": "浆果冰淇淋",
    "item.coffeework.icecream_melon": "蜜瓜冰淇淋",
    "item.coffeework.coffee_bean": "烘焙咖啡豆",  # was 烘培
    "item.coffeework.cocoa_bean": "烘焙可可豆",  # was 烘培
    "item.coffeework.coffee_bean_light": "轻度烘焙咖啡豆",  # was 烘培
    "item.coffeework.coffee_powder_light": "轻度烘焙咖啡粉",  # was 烘培
    "item.coffeework.coffee_seeds": "咖啡种子",  # fix key name: was coffee_seed (singular)
}

# Also fix the singular key: rename coffee_seed → coffee_seeds
# This is a key rename, handled separately below


def fix_en_us():
    path = LANG_DIR / "en_us.json"
    data = load_json(path)
    changed = 0

    # Replace placeholder values
    for key, new_val in EN_PLACEHOLDERS.items():
        if key in data and data[key] != new_val:
            data[key] = new_val
            changed += 1

    # Add missing keys
    for key, val in EN_MISSING.items():
        if key not in data:
            data[key] = val
            changed += 1

    # Sort keys
    data = dict(sorted(data.items()))

    save_json(path, data)
    print(f"en_us.json: {changed} changes applied")
    return changed


def fix_zh_cn():
    path = LANG_DIR / "zh_cn.json"
    data = load_json(path)
    changed = 0

    # Fix typos
    for key, new_val in ZH_TYPO_FIXES.items():
        if key in data and data[key] != new_val:
            data[key] = new_val
            changed += 1

    # Fix coffee_seed (singular) → coffee_seeds (plural) key rename
    if "item.coffeework.coffee_seed" in data:
        val = data.pop("item.coffeework.coffee_seed")
        data["item.coffeework.coffee_seeds"] = val
        changed += 1

    # Fix 烘培 → 烘焙 in all values
    for key in list(data.keys()):
        if "烘培" in data[key]:
            data[key] = data[key].replace("烘培", "烘焙")
            changed += 1

    # Fix 冰淇凌 → 冰淇淋 in all values
    for key in list(data.keys()):
        if "冰淇凌" in data[key]:
            data[key] = data[key].replace("冰淇凌", "冰淇淋")
            changed += 1

    # Add missing keys
    for key, val in ZH_NEW.items():
        if key not in data:
            data[key] = val
            changed += 1

    # Sort keys
    data = dict(sorted(data.items()))

    save_json(path, data)
    print(f"zh_cn.json: {changed} changes applied")
    return changed


def fix_ja_jp():
    path = LANG_DIR / "ja_jp.json"
    data = load_json(path)
    changed = 0

    # Add missing keys
    for key, val in JA_NEW.items():
        if key not in data:
            data[key] = val
            changed += 1

    # Sort keys
    data = dict(sorted(data.items()))

    save_json(path, data)
    print(f"ja_jp.json: {changed} changes applied")
    return changed


def main():
    total = 0
    total += fix_en_us()
    total += fix_zh_cn()
    total += fix_ja_jp()
    print(f"\nTotal changes: {total}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
