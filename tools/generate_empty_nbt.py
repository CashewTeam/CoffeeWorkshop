#!/usr/bin/env python3
"""Generate a valid empty Minecraft structure .nbt file for GameTest.

Writes binary NBT (gzip-compressed) directly without parsing SNBT.
The output is a minimal 1x1x1 structure with a single air block.
"""
import gzip
import struct
import sys
import os

def write_byte(buf, v):
    buf.append(v & 0xFF)

def write_short(buf, v):
    buf.extend(struct.pack('>h', v))

def write_int(buf, v):
    buf.extend(struct.pack('>i', v))

def write_long(buf, v):
    buf.extend(struct.pack('>q', v))

def write_float(buf, v):
    buf.extend(struct.pack('>f', v))

def write_double(buf, v):
    buf.extend(struct.pack('>d', v))

def write_string(buf, s):
    encoded = s.encode('utf-8')
    write_short(buf, len(encoded))
    buf.extend(encoded)

def write_tag_compound(buf):
    """Write empty-named TAG_Compound header."""
    buf.append(0x0A)  # TAG_Compound
    write_short(buf, 0)  # empty name (0 length)

def write_tag_end(buf):
    buf.append(0x00)  # TAG_End

def write_named_int(buf, name, value):
    buf.append(0x03)  # TAG_Int
    write_string(buf, name)
    write_int(buf, value)

def write_named_string(buf, name, value):
    buf.append(0x08)  # TAG_String
    write_string(buf, name)
    write_string(buf, value)

def write_named_list(buf, name, items, element_type):
    """Write a named TAG_List with homogeneous element_type."""
    buf.append(0x09)  # TAG_List
    write_string(buf, name)
    buf.append(element_type)
    write_int(buf, len(items))

    for item in items:
        if element_type == 0x03:  # TAG_Int
            write_int(buf, item)
        elif element_type == 0x0A:  # TAG_Compound
            # Compound items in a list have no name
            for sub_name, sub_value in item.items():
                if isinstance(sub_value, int):
                    write_named_int_no_header(buf, sub_name, sub_value)
                elif isinstance(sub_value, str):
                    write_named_string_no_header(buf, sub_name, sub_value)
                elif isinstance(sub_value, list):
                    write_named_list_no_header(buf, sub_name, sub_value, 0x03)
            buf.append(0x00)  # TAG_End for this compound
        else:
            raise ValueError(f"Unsupported element type: 0x{element_type:02X}")

def write_named_int_no_header(buf, name, value):
    """Write TAG_Int without list header (for list items)."""
    buf.append(0x03)
    write_string(buf, name)
    write_int(buf, value)

def write_named_string_no_header(buf, name, value):
    """Write TAG_String without list header."""
    buf.append(0x08)
    write_string(buf, name)
    write_string(buf, value)

def write_named_list_no_header(buf, name, items, element_type):
    """Write TAG_List without list header."""
    buf.append(0x09)
    write_string(buf, name)
    buf.append(element_type)
    write_int(buf, len(items))
    for item in items:
        if element_type == 0x03:
            write_int(buf, item)


def generate_empty_structure(width=1, height=3, depth=1):
    """Generate an empty structure NBT of the given size."""
    buf = bytearray()

    # Root TAG_Compound with empty name
    write_tag_compound(buf)

    # DataVersion
    write_named_int(buf, "DataVersion", 3465)

    # size: [width, height, depth]
    write_named_list(buf, "size", [width, height, depth], 0x03)

    # palette: [{Name: "minecraft:air"}]
    palette_items = [{"Name": "minecraft:air"}]
    write_named_list(buf, "palette", palette_items, 0x0A)

    # entities: [] (empty list of compounds)
    buf.append(0x09)
    write_string(buf, "entities")
    buf.append(0x0A)  # element type = compound
    write_int(buf, 0)  # count = 0
    # no elements

    # blocks: [{pos: [x,y,z], state: 0}] for each position
    block_items = []
    for y in range(height):
        for x in range(width):
            for z in range(depth):
                block_items.append({
                    "pos": [x, y, z],
                    "state": 0
                })
    write_named_list(buf, "blocks", block_items, 0x0A)

    # End root compound
    write_tag_end(buf)

    return bytes(buf)


def main():
    output_path = sys.argv[1] if len(sys.argv) > 1 else \
        "src/main/resources/data/coffeework/structures/empty.nbt"

    nbt_bytes = generate_empty_structure(1, 3, 1)
    print(f"Generated {len(nbt_bytes)} bytes uncompressed NBT")

    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    with gzip.GzipFile(output_path, 'wb', mtime=0) as f:
        f.write(nbt_bytes)

    compressed_size = os.path.getsize(output_path)
    print(f"Wrote {output_path} ({compressed_size} bytes gzip-compressed)")


if __name__ == '__main__':
    main()
