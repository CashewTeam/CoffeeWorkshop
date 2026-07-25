#!/usr/bin/env python3
"""Convert a Minecraft SNBT structure file to binary compressed NBT (.nbt).

Usage:
    python tools/convert_structure_nbt.py <input.snbt> <output.nbt>

The structure format used by Forge GameTest needs binary .nbt files at
runtime.  .snbt is the human-readable development format; this script
produces the binary equivalent without requiring a running Minecraft
instance.

Only supports the minimal subset of NBT needed for empty structure
templates: TAG_Compound, TAG_Int, TAG_String, TAG_List(Int/Compound).
"""
import gzip
import re
import struct
import sys
import os

TAG_END = 0
TAG_BYTE = 1
TAG_SHORT = 2
TAG_INT = 3
TAG_LONG = 4
TAG_FLOAT = 5
TAG_DOUBLE = 6
TAG_BYTE_ARRAY = 7
TAG_STRING = 8
TAG_LIST = 9
TAG_COMPOUND = 10
TAG_INT_ARRAY = 11
TAG_LONG_ARRAY = 12


class NBTOutput:
    def __init__(self):
        self.buf = bytearray()

    def write_byte(self, v):
        self.buf.append(v & 0xFF)

    def write_short(self, v):
        self.buf += struct.pack('>h', v)

    def write_int(self, v):
        self.buf += struct.pack('>i', v)

    def write_long(self, v):
        self.buf += struct.pack('>q', v)

    def write_float(self, v):
        self.buf += struct.pack('>f', v)

    def write_double(self, v):
        self.buf += struct.pack('>d', v)

    def write_string(self, s):
        encoded = s.encode('utf-8')
        self.write_short(len(encoded))
        self.buf += encoded

    def write_tag_header(self, tag_type, name):
        self.write_byte(tag_type)
        self.write_string(name)

    def to_bytes(self):
        return bytes(self.buf)


def parse_snbt(s):
    """Parse a simplified SNBT string into a Python structure."""
    s = s.strip()

    if s.startswith('{'):
        # Strip outer braces
        inner = s[1:-1].strip()
        return parse_compound_content(inner)

    if s.startswith('[') and s.endswith(']'):
        return parse_list_content(s[1:-1])

    # Quoted string
    if s.startswith('"') or s.startswith("'"):
        quote = s[0]
        end = s.index(quote, 1)
        return ('string', s[1:end])

    # Numbers
    if s.endswith('b') or s.endswith('B'):
        return ('byte', int(s[:-1]))
    if s.endswith('s') or s.endswith('S'):
        return ('short', int(s[:-1]))
    if s.endswith('l') or s.endswith('L'):
        return ('long', int(s[:-1]))
    if s.endswith('f') or s.endswith('F'):
        return ('float', float(s[:-1]))
    if s.endswith('d') or s.endswith('D'):
        return ('double', float(s[:-1]))
    if '.' in s or 'e' in s.lower():
        return ('double', float(s))

    try:
        return ('int', int(s))
    except ValueError:
        pass

    # Bare string
    return ('string', s)


def split_top_level(content):
    """Split comma-separated top-level key:value pairs/items."""
    parts = []
    depth = 0
    current = []
    in_string = False
    quote_char = None

    for ch in content:
        if in_string:
            current.append(ch)
            if ch == quote_char:
                in_string = False
            continue
        if ch in ('"', "'"):
            in_string = True
            quote_char = ch
            current.append(ch)
            continue
        if ch in ('{', '['):
            depth += 1
            current.append(ch)
            continue
        if ch in ('}', ']'):
            depth -= 1
            current.append(ch)
            continue
        if ch == ',' and depth == 0:
            parts.append(''.join(current).strip())
            current = []
            continue
        current.append(ch)
    if current:
        parts.append(''.join(current).strip())
    return [p for p in parts if p]


def parse_compound_content(content):
    """Parse compound content into a dict."""
    result = {}
    if not content:
        return result
    parts = split_top_level(content)
    for part in parts:
        # key:value
        m = re.match(r'^\s*([a-zA-Z0-9_]+)\s*:\s*(.+)$', part, re.DOTALL)
        if m:
            key = m.group(1)
            value = m.group(2).strip()
            result[key] = parse_snbt(value)
    return result


def parse_list_content(content):
    """Parse list content into a list of items."""
    result = []
    if not content:
        return result
    parts = split_top_level(content)
    for part in parts:
        result.append(parse_snbt(part))
    return result


def infer_list_type(items):
    """Infer the NBT tag type for a list. Returns TAG_COMPOUND if mixed."""
    if not items:
        return TAG_COMPOUND
    types = set()
    for item in items:
        if isinstance(item, tuple):
            types.add(item[0])
    if len(types) == 1:
        t = types.pop()
        type_map = {
            'byte': TAG_BYTE, 'short': TAG_SHORT, 'int': TAG_INT,
            'long': TAG_LONG, 'float': TAG_FLOAT, 'double': TAG_DOUBLE,
            'string': TAG_STRING
        }
        return type_map.get(t, TAG_COMPOUND)
    return TAG_COMPOUND


def write_value(out, value):
    """Write a parsed NBT value."""
    t = value[0]
    v = value[1]

    if t == 'byte':
        out.write_byte(v)
    elif t == 'short':
        out.write_short(v)
    elif t == 'int':
        out.write_int(v)
    elif t == 'long':
        out.write_long(v)
    elif t == 'float':
        out.write_float(v)
    elif t == 'double':
        out.write_double(v)
    elif t == 'string':
        out.write_string(v)
    elif t == 'dict':
        write_compound(out, v)
    elif t == 'list':
        write_list(out, v)
    else:
        raise ValueError(f"Unknown type: {t}")


def write_compound(out, data, name=''):
    """Write a TAG_Compound."""
    out.write_tag_header(TAG_COMPOUND, name)
    for key, value in data.items():
        if isinstance(value, tuple):
            write_tagged(out, key, value)
        elif isinstance(value, dict):
            write_compound(out, value, key)
        elif isinstance(value, list):
            write_list(out, value, key)
    out.write_byte(TAG_END)


def write_list(out, items, name=''):
    """Write a TAG_List."""
    list_type = infer_list_type(items)
    out.write_tag_header(TAG_LIST, name)
    out.write_byte(list_type)
    out.write_int(len(items))
    for item in items:
        if isinstance(item, tuple):
            # Write the value directly (no name for list items)
            if item[0] == 'dict':
                write_compound(out, item[1])
            elif item[0] == 'list':
                write_list(out, item[1])
            else:
                write_value(out, item)
        elif isinstance(item, dict):
            write_compound(out, item)
        elif isinstance(item, list):
            write_list(out, item)


def write_tagged(out, name, value):
    """Write a named tag with appropriate type."""
    t = value[0]
    v = value[1]

    if t == 'byte':
        out.write_byte(TAG_BYTE)
        out.write_string(name)
        out.write_byte(v)
    elif t == 'short':
        out.write_byte(TAG_SHORT)
        out.write_string(name)
        out.write_short(v)
    elif t == 'int':
        out.write_byte(TAG_INT)
        out.write_string(name)
        out.write_int(v)
    elif t == 'long':
        out.write_byte(TAG_LONG)
        out.write_string(name)
        out.write_long(v)
    elif t == 'float':
        out.write_byte(TAG_FLOAT)
        out.write_string(name)
        out.write_float(v)
    elif t == 'double':
        out.write_byte(TAG_DOUBLE)
        out.write_string(name)
        out.write_double(v)
    elif t == 'string':
        out.write_byte(TAG_STRING)
        out.write_string(name)
        out.write_string(v)
    elif t == 'dict':
        write_compound(out, v, name)
    elif t == 'list':
        write_list(out, v, name)
    else:
        raise ValueError(f"Unknown tagged type: {t}")


def main():
    if len(sys.argv) != 3:
        print("Usage: python convert_structure_nbt.py <input.snbt> <output.nbt>")
        sys.exit(1)

    input_path = sys.argv[1]
    output_path = sys.argv[2]

    with open(input_path, 'r', encoding='utf-8') as f:
        snbt = f.read().strip()

    if snbt.startswith('{') and snbt.endswith('}'):
        data = parse_compound_content(snbt[1:-1])
        inferred = {}
        # Rebuild with proper type tagging
        for k, v in data.items():
            if isinstance(v, tuple):
                inferred[k] = v
            elif isinstance(v, dict):
                inferred[k] = ('dict', v)
            elif isinstance(v, list):
                inferred[k] = ('list', v)
        data = inferred
    else:
        print("Error: top-level must be a compound tag")
        sys.exit(1)

    out = NBTOutput()
    out.write_byte(TAG_COMPOUND)
    out.write_string('')  # root name is empty for structure files

    for key, value in data.items():
        write_tagged(out, key, value)

    out.write_byte(TAG_END)

    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    with gzip.open(output_path, 'wb') as f:
        f.write(out.to_bytes())

    print(f"Converted {input_path} → {output_path} ({len(out.to_bytes())} bytes uncompressed)")


if __name__ == '__main__':
    main()
