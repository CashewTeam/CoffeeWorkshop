#!/usr/bin/env python3
"""Replace English placeholder values in ja_jp.json with proper Japanese translations."""

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LANG_DIR = ROOT / "src" / "main" / "resources" / "assets" / "coffeework" / "lang"

JA_TRANSLATIONS = {
    # Machines
    "block.coffeework.grinder": "火力粉砕機",
    "block.coffeework.coffee_machine": "コーヒーマシン",
    "block.coffeework.icecream_machine": "アイスクリームマシン",
    "block.coffeework.roller": "火力圧延機",
    "block.coffeework.oven": "粘土オーブン",
    "item.coffeework.grinder_off": "火力粉砕機",
    "item.coffeework.coffee_machine": "コーヒーマシン",
    "item.coffeework.icecream_machine": "アイスクリームマシン",
    "item.coffeework.roller": "火力圧延機",
    "item.coffeework.oven_off": "粘土オーブン",
    
    # Containers
    "container.coffeework.grinder": "粉砕機",
    "container.coffeework.coffee_machine": "コーヒーマシン",
    "container.coffeework.icecream_machine": "アイスクリームマシン",
    "container.coffeework.roller": "圧延機",
    "container.coffeework.oven": "オーブン",
    
    # GUI
    "gui.coffeework.coffee_machine.base": "主原料",
    "gui.coffeework.coffee_machine.modifier": "液体",
    "gui.coffeework.coffee_machine.additive": "添加物",
    "gui.coffeework.coffee_machine.container": "カップ",
    
    # Plants
    "block.coffeework.coffee_tree": "コーヒーの木",
    "item.coffeework.coffee_tree": "コーヒーの木",
    "block.coffeework.blueberry_bush": "ブルーベリーの低木",
    "item.coffeework.blueberry_bush": "ブルーベリーの低木",
    "block.coffeework.vanilla_crop": "バニラ作物",
    "item.coffeework.coffee_seeds": "コーヒーの種",
    "item.coffeework.vanilla_seeds": "バニラの種",
    
    # Materials
    "item.coffeework.coffee_bean_raw": "生コーヒー豆",
    "item.coffeework.coffee_bean": "焙煎コーヒー豆",
    "item.coffeework.coffee_powder": "コーヒー粉",
    "item.coffeework.cocoa_bean": "焙煎カカオ豆",
    "item.coffeework.cocoa_powder": "ココアパウダー",
    "item.coffeework.cocoa_batter": "ココア生地",
    "item.coffeework.flour": "小麦粉",
    "item.coffeework.dough": "生地",
    "item.coffeework.dough_pastry": "パイ生地",
    "item.coffeework.dough_cookie": "クッキー生地",
    "item.coffeework.dough_ginger": "ジンジャー生地",
    "item.coffeework.dough_bread": "パン生地",
    "item.coffeework.dough_bread_round": "丸パン生地",
    "item.coffeework.dough_baguette": "バゲット生地",
    "item.coffeework.dough_bagel": "ベーグル生地",
    "item.coffeework.dough_toast": "トースト生地",
    "item.coffeework.plate_dough": "生地シート",
    "item.coffeework.plate_dough_pastry": "パイ生地シート",
    "item.coffeework.plate_dough_ginger": "ジンジャー生地シート",
    "item.coffeework.plate_iron": "鉄板",
    "item.coffeework.ice_slag": "氷滓",
    "item.coffeework.soda": "ソーダ",
    "item.coffeework.yeast": "酵母",
    "item.coffeework.spices": "スパイス",
    "item.coffeework.gelatin": "ゼラチン",
    "item.coffeework.bag_cloth": "袋の布",
    "item.coffeework.vanilla": "バニラ",
    "item.coffeework.bag": "袋",
    "item.coffeework.cup": "カップ",
    "item.coffeework.cup_glass": "ガラスカップ",
    "item.coffeework.chocolate_bar": "チョコレートバー",
    "item.coffeework.chocolate_chip": "チョコチップ",
    "item.coffeework.butter": "バター",
    "item.coffeework.cheese": "チーズ",
    "item.coffeework.blueberry": "ブルーベリー",
    "item.coffeework.tea_leaf": "茶葉",
    "item.coffeework.black_tea_leaf": "紅茶の葉",
    
    # Syrups
    "item.coffeework.syrup_empty": "空き瓶",
    "item.coffeework.syrup_caramel": "キャラメルシロップ",
    "item.coffeework.syrup_chocolate": "チョコレートシロップ",
    "item.coffeework.syrup_fruit": "フルーツシロップ",
    "item.coffeework.syrup_mint": "ミントシロップ",
    "item.coffeework.syrup_vanilla": "バニラシロップ",
    "item.coffeework.syrup_sakura": "桜シロップ",
    
    # Cold Brew
    "block.coffeework.coldbrew_pot": "水出しポット",
    "item.coffeework.coldbrew_pot": "水出しポット",
    "item.coffeework.empty_coldbrew_pot": "空の水出しポット",
    "item.coffeework.coldbrew_bottle": "水出しコーヒーボトル",
    
    # Hot Drinks
    "item.coffeework.espresso": "エスプレッソ",
    "item.coffeework.coffee_americano": "アメリカーノ",
    "item.coffeework.coffee_latte": "カフェラテ",
    "item.coffeework.coffee_cappuccino": "カプチーノ",
    "item.coffeework.coffee_macchiato": "マキアート",
    "item.coffeework.coffee_mochaccino": "モカチーノ",
    "item.coffeework.coffee_green_tea": "緑茶",
    "item.coffeework.coffee_black_tea": "紅茶",
    "item.coffeework.coffee_milk_tea": "ミルクティー",
    "item.coffeework.coffee_mandarin_drink": "マンダリンドリンク",
    "item.coffeework.coffee_coldbrew": "水出しコーヒー",
    "item.coffeework.cocoa": "ホットココア",
    "item.coffeework.cocoa_strong": "濃厚ココア",
    
    # Iced Drinks
    "item.coffeework.coffee_americano_ice": "アイスアメリカーノ",
    "item.coffeework.coffee_latte_ice": "アイスカフェラテ",
    "item.coffeework.coffee_cappuccino_ice": "アイスカプチーノ",
    "item.coffeework.coffee_macchiato_ice": "アイスマキアート",
    "item.coffeework.coffee_mochaccino_ice": "アイスモカチーノ",
    "item.coffeework.coffee_green_tea_ice": "アイス緑茶",
    "item.coffeework.coffee_black_tea_ice": "アイス紅茶",
    "item.coffeework.coffee_milk_tea_ice": "アイスミルクティー",
    "item.coffeework.coffee_mandarin_drink_ice": "アイスマンダリンドリンク",
    "item.coffeework.coffee_coldbrew_ice": "アイス水出しコーヒー",
    "item.coffeework.cocoa_ice": "アイスココア",
    "item.coffeework.cocoa_strong_ice": "アイス濃厚ココア",
    
    # Flavored Lattes
    "item.coffeework.coffee_latte_caramel": "キャラメルラテ",
    "item.coffeework.coffee_latte_chocolate": "チョコレートラテ",
    "item.coffeework.coffee_latte_fruit": "フルーツラテ",
    "item.coffeework.coffee_latte_mint": "ミントラテ",
    "item.coffeework.coffee_latte_vanilla": "バニララテ",
    "item.coffeework.coffee_latte_sakura": "桜ラテ",
    "item.coffeework.coffee_latte_caramel_ice": "アイスキャラメルラテ",
    "item.coffeework.coffee_latte_chocolate_ice": "アイスチョコレートラテ",
    "item.coffeework.coffee_latte_fruit_ice": "アイスフルーツラテ",
    "item.coffeework.coffee_latte_mint_ice": "アイスミントラテ",
    "item.coffeework.coffee_latte_vanilla_ice": "アイスバニララテ",
    "item.coffeework.coffee_latte_sakura_ice": "アイス桜ラテ",
    
    # Americano Extensions
    "item.coffeework.coffee_americano_fruit": "フルーツアメリカーノ",
    "item.coffeework.coffee_americano_fruit_ice": "アイスフルーツアメリカーノ",
    "item.coffeework.coffee_americano_nitro_ice": "スパークリングアメリカーノ",
    "item.coffeework.coffee_americano_nitro_fruit_ice": "スパークリングフルーツアメリカーノ",
    
    # Cold Brew Extensions
    "item.coffeework.coffee_coldbrew_fruit": "フルーツ水出しコーヒー",
    "item.coffeework.coffee_coldbrew_fruit_ice": "アイスフルーツ水出しコーヒー",
    "item.coffeework.coffee_coldbrew_latte": "水出しコーヒーラテ",
    "item.coffeework.coffee_coldbrew_latte_ice": "アイス水出しコーヒーラテ",
    "item.coffeework.coffee_coldbrew_latte_caramel": "キャラメル水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_caramel_ice": "アイスキャラメル水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_chocolate": "チョコレート水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_chocolate_ice": "アイスチョコレート水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_fruit": "フルーツ水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_fruit_ice": "アイスフルーツ水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_mint": "ミント水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_mint_ice": "アイスミント水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_vanilla": "バニラ水出しラテ",
    "item.coffeework.coffee_coldbrew_latte_vanilla_ice": "アイスバニラ水出しラテ",
    
    # Instant Coffee
    "item.coffeework.coffee_instant": "インスタントコーヒー",
    "item.coffeework.coffee_instant_stick": "インスタントコーヒースティック",
    "item.coffeework.coffee_instant_box": "インスタントコーヒーボックス",
    
    # Foods
    "item.coffeework.bread_round": "丸パン",
    "item.coffeework.baguette": "バゲット",
    "item.coffeework.bagel": "ベーグル",
    "item.coffeework.toast": "トースト",
    "item.coffeework.brownie": "ブラウニー",
    "item.coffeework.cake_sponge_slice": "スポンジケーキスライス",
    "item.coffeework.pie_cream": "クリームパイ",
    "item.coffeework.sandwich_blt": "BLTサンドイッチ",
    "item.coffeework.field_ration": "フィールドレーション",
    "item.coffeework.icecream_vanilla": "バニラアイスクリーム",
    "item.coffeework.icecream_mix_vanilla": "バニラアイスクリームミックス",
    
    # Bags
    "block.coffeework.bag_coffee": "コーヒー豆の袋",
    "block.coffeework.bag_coffee_raw": "生コーヒー豆の袋",
    "block.coffeework.bag_cocoa": "カカオ豆の袋",
    "block.coffeework.bag_cocoa_powder": "ココアパウダーの袋",
    "block.coffeework.bag_flour": "小麦粉の袋",
    "block.coffeework.bag_coffee_powder": "コーヒー粉の袋",
    "block.coffeework.bag_sugar": "砂糖の袋",
    "item.coffeework.bag_coffee": "コーヒー豆の袋",
    "item.coffeework.bag_coffee_raw": "生コーヒー豆の袋",
    "item.coffeework.bag_cocoa": "カカオ豆の袋",
    "item.coffeework.bag_cocoa_powder": "ココアパウダーの袋",
    "item.coffeework.bag_flour": "小麦粉の袋",
    "item.coffeework.bag_coffee_powder": "コーヒー粉の袋",
    "item.coffeework.bag_sugar": "砂糖の袋",
    "block.coffeework.double_bag_coffee": "コーヒー豆の大袋",
    "block.coffeework.double_bag_coffee_raw": "生コーヒー豆の大袋",
    "block.coffeework.double_bag_cocoa": "カカオ豆の大袋",
    "block.coffeework.double_bag_cocoa_powder": "ココアパウダーの大袋",
    "block.coffeework.double_bag_flour": "小麦粉の大袋",
    "block.coffeework.double_bag_coffee_powder": "コーヒー粉の大袋",
    "block.coffeework.double_bag_sugar": "砂糖の大袋",
    "item.coffeework.double_bag_coffee": "コーヒー豆の大袋",
    "item.coffeework.double_bag_coffee_raw": "生コーヒー豆の大袋",
    "item.coffeework.double_bag_cocoa": "カカオ豆の大袋",
    "item.coffeework.double_bag_cocoa_powder": "ココアパウダーの大袋",
    "item.coffeework.double_bag_flour": "小麦粉の大袋",
    "item.coffeework.double_bag_coffee_powder": "コーヒー粉の大袋",
    "item.coffeework.double_bag_sugar": "砂糖の大袋",
    
    # Cakes
    "block.coffeework.cake_sponge": "スポンジケーキ生地",
    "item.coffeework.cake_sponge": "スポンジケーキ生地",
    "block.coffeework.cake_sponge_chocolate": "チョコスポンジ生地",
    "item.coffeework.cake_sponge_chocolate": "チョコスポンジ生地",
    "block.coffeework.cake_sponge_coffee": "コーヒースポンジ生地",
    "item.coffeework.cake_sponge_coffee": "コーヒースポンジ生地",
    "block.coffeework.cake_sponge_pumpkin": "パンプキンスポンジ生地",
    "item.coffeework.cake_sponge_pumpkin": "パンプキンスポンジ生地",
    "block.coffeework.cake_sponge_carrot": "ニンジンスポンジ生地",
    "item.coffeework.cake_sponge_carrot": "ニンジンスポンジ生地",
    "block.coffeework.cake_sponge_redvelvet": "レッドベルベットスポンジ生地",
    "item.coffeework.cake_sponge_redvelvet": "レッドベルベットスポンジ生地",
    "block.coffeework.cake_sponge_lemon": "レモンスポンジ生地",
    "item.coffeework.cake_sponge_lemon": "レモンスポンジ生地",
    "block.coffeework.cake_sponge_tea": "抹茶スポンジ生地",
    "item.coffeework.cake_sponge_tea": "抹茶スポンジ生地",
    "block.coffeework.cake_sponge_berry": "ベリースポンジ生地",
    "item.coffeework.cake_sponge_berry": "ベリースポンジ生地",
    "block.coffeework.cake_coffee": "コーヒーケーキ",
    "item.coffeework.cake_coffee": "コーヒーケーキ",
    "block.coffeework.cake_harvest": "収穫祭ケーキ",
    "item.coffeework.cake_harvest": "収穫祭ケーキ",
    "block.coffeework.cake_lemon": "レモンケーキ",
    "item.coffeework.cake_lemon": "レモンケーキ",
    "block.coffeework.cake_tea": "抹茶ケーキ",
    "item.coffeework.cake_tea": "抹茶ケーキ",
    "block.coffeework.cake_berry": "ミックスベリーケーキ",
    "item.coffeework.cake_berry": "ミックスベリーケーキ",
    "block.coffeework.cake_cheese": "チーズケーキ",
    "item.coffeework.cake_cheese": "チーズケーキ",
    "block.coffeework.cake_schwarzwald": "シュヴァルツヴァルトケーキ",
    "item.coffeework.cake_schwarzwald": "シュヴァルツヴァルトケーキ",
    "block.coffeework.cake_redvelvet": "レッドベルベットケーキ",
    "item.coffeework.cake_redvelvet": "レッドベルベットケーキ",
    "block.coffeework.tiramisu": "ティラミス",
    "item.coffeework.tiramisu": "ティラミス",
    
    # Mousses
    "block.coffeework.mousse_berry": "ベリームース",
    "item.coffeework.mousse_berry": "ベリームース",
    "block.coffeework.mousse_lemon": "レモンムース",
    "item.coffeework.mousse_lemon": "レモンムース",
    "block.coffeework.mousse_chocolate": "チョコレートムース",
    "item.coffeework.mousse_chocolate": "チョコレートムース",
    "block.coffeework.mousse_coffee": "コーヒームース",
    "item.coffeework.mousse_coffee": "コーヒームース",
    
    # Decor
    "block.coffeework.plate": "皿",
    "item.coffeework.plate": "皿",
    "block.coffeework.soda_ore": "ソーダ鉱石",
    "item.coffeework.soda_ore": "ソーダ鉱石",
    "block.coffeework.xmas_tree": "クリスマスツリー",
    "item.coffeework.xmas_tree": "クリスマスツリー",
    "block.coffeework.ginger_house": "ジンジャーブレッドハウス",
    "item.coffeework.ginger_house": "ジンジャーブレッドハウス",
    
    # Tools
    "item.coffeework.iron_bowl": "鉄ボウル",
    "item.coffeework.mixing_bowl": "ミキシングボウル",
    "item.coffeework.cake_model": "丸型ケーキ型",
    "item.coffeework.cake_model_square": "四角ケーキ型",
    "item.coffeework.cake_model_plate": "平皿ケーキ型",
    "item.coffeework.small_model": "スフレ型",
    "item.coffeework.mooncake_model": "月餅型",
    
    # Records
    "item.coffeework.record_blank": "空白のレコード",
    "item.coffeework.record_kusa_noshi_to_ne": "レコード",
    "item.coffeework.record_lazy_lady_kaguya": "レコード",
    "item.coffeework.record_the_grimoire_of_marisa": "レコード",
    "item.coffeework.record_kusa_noshi_to_ne.desc": "Kusa Noshi To Ne",
    "item.coffeework.record_lazy_lady_kaguya.desc": "Lazy Lady Kaguya",
    "item.coffeework.record_the_grimoire_of_marisa.desc": "The Grimoire of Marisa",
    
    # Effects
    "effect.coffeework.caffeine": "カフェイン",
    "effect.coffeework.relax": "リラックス",
    "effect.coffeework.golden_heart": "黄金の心",
    
    # Professions
    "entity.minecraft.villager.coffeework.coffee_barista": "コーヒーバリスタ",
    "entity.minecraft.villager.coffeework.coffee_materials_trader": "コーヒー材料商人",
    "entity.minecraft.villager.coffeework.food_trader": "食品商人",
    
    # Creative tab
    "itemGroup.coffee_workshop": "コーヒーワークショップ",
    
    # JEI
    "jei.coffeework.category.grinder": "粉砕",
    "jei.coffeework.category.coffee_machine": "コーヒー抽出",
    "jei.coffeework.category.icecream_machine": "アイスクリーム製造",
    "jei.coffeework.category.roller": "圧延",
    "jei.coffeework.category.oven": "焼成",
    "jei.coffeework.category.cooling": "冷却",
    "jei.coffeework.cooling.tip": "氷滓を加えて冷たい飲み物に",
    
    # Tooltip
    "tooltip.coffeework.cups_remaining": "残り %1$s / %2$s 杯",
}


def main():
    path = LANG_DIR / "ja_jp.json"
    data = json.loads(path.read_text(encoding="utf-8"))
    
    changed = 0
    for key, ja_value in JA_TRANSLATIONS.items():
        if key in data:
            old = data[key]
            data[key] = ja_value
            if old != ja_value:
                changed += 1
    
    # Sort and save
    data = dict(sorted(data.items()))
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    
    print(f"ja_jp.json: {changed} values updated")
    return 0


if __name__ == "__main__":
    sys.exit(main())
