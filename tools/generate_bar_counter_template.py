#!/usr/bin/env python3
"""
Generate a 3x3x3 air structure template for Bar Counter GameTests.

The Minecraft 1.20 structure format uses an NBT compound with:
  - DataVersion: int (e.g. 3465 for 1.20.1)
  - size: 3-int array [x, y, z]
  - palette: list of block-state compounds, each with a Name
  - blocks: list of {pos: [x, y, z], state: int}
  - entities: empty list

For our 3x3x3 air template, the palette has a single air entry
and the blocks list enumerates every air block (the structure
manager treats an empty blocks list as an uninitialised template).
The empty.nbt shipped with the project uses the same pattern.

The empty.nbt is gzip-compressed, so we gzip-compress the output
to match the loading convention.
"""
import gzip
import nbtlib
import sys
from pathlib import Path

DATA_VERSION = 3465  # Minecraft 1.20.1

OUT_PATH = Path(__file__).resolve().parent.parent / "src" / "main" / "resources" / "data" / "coffeework" / "structures" / "bar_counter_3x3x3.nbt"


def main():
    blocks = []
    for y in range(3):
        for z in range(3):
            for x in range(3):
                blocks.append(nbtlib.Compound({
                    "pos": nbtlib.IntArray([x, y, z]),
                    "state": nbtlib.Int(0),
                }))

    nbt = nbtlib.Compound({
        "DataVersion": nbtlib.Int(DATA_VERSION),
        "size": nbtlib.IntArray([3, 3, 3]),
        "palette": nbtlib.List([
            nbtlib.Compound({"Name": nbtlib.String("minecraft:air")}),
        ]),
        "blocks": nbtlib.List(blocks),
        "entities": nbtlib.List([]),
    })

    OUT_PATH.parent.mkdir(parents=True, exist_ok=True)
    with gzip.open(OUT_PATH, "wb") as f:
        nbtlib.File(nbt).write(f)
    print(f"Wrote {OUT_PATH} ({len(blocks)} air blocks)")


if __name__ == "__main__":
    main()
