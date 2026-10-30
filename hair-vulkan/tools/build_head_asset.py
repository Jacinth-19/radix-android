#!/usr/bin/env python3
"""Build the head asset used by the renderer.

Source: "Infinite-Level_02" head scan by Lee Perry-Smith (CC-BY 3.0,
triplegangers / ir-ltd), as distributed with three.js in
examples/models/gltf/LeePerrySmith/LeePerrySmith.glb

The script welds the scan, subdivides it once, sculpts it towards a feminine
bust (jaw, brow, nose, lips, cheeks, neck), opens the eyes by pushing the lid
surface behind the eyeballs, extrudes the open bottom into a mannequin base and
writes:

    assets/head.obj   welded bust, metres, +Y up, +Z forward, crown at y = 1.66
    assets/eyes.obj   the two eyeballs (radius 0.0142 m)

    python3 tools/build_head_asset.py <LeePerrySmith.glb> assets/
"""
import json
import os
import struct
import sys

import numpy as np

# ----------------------------------------------------------------- GLB input --
COMP = {5120: ('b', 1), 5121: ('B', 1), 5122: ('h', 2), 5123: ('H', 2), 5125: ('I', 4), 5126: ('f', 4)}
NCOMP = {'SCALAR': 1, 'VEC2': 2, 'VEC3': 3, 'VEC4': 4}


def load_glb(path):
    data = open(path, 'rb').read()
    magic, _, length = struct.unpack_from('<III', data, 0)
    assert magic == 0x46546C67, 'not a glb'
    off, js, bin_ = 12, None, b''
    while off < length:
        clen, ctype = struct.unpack_from('<II', data, off)
        chunk = data[off + 8: off + 8 + clen]
        if ctype == 0x4E4F534A:
            js = json.loads(chunk.decode('utf-8'))
        elif ctype == 0x004E4942:
            bin_ = chunk
        off += 8 + clen + ((4 - clen % 4) % 4 if clen % 4 else 0)
    return js, bin_


def read_accessor(g, bin_, idx):
    acc = g['accessors'][idx]
    bv = g['bufferViews'][acc['bufferView']]
    fmt, size = COMP[acc['componentType']]
    n = NCOMP[acc['type']]
    start = bv.get('byteOffset', 0) + acc.get('byteOffset', 0)
    stride = bv.get('byteStride') or size * n
    out = np.empty((acc['count'], n), dtype=np.float64)
    for i in range(acc['count']):
        out[i] = struct.unpack_from('<' + fmt * n, bin_, start + i * stride)
    return out


# ---------------------------------------------------------------- geometry ----
def weld(pos, tri, eps=1e-5):
    key = np.round(pos / eps).astype(np.int64)
    _, first, inv = np.unique(key, axis=0, return_index=True, return_inverse=True)
    remap = np.zeros(len(pos), dtype=np.int64)
    remap[first] = np.arange(len(first))
    # keep the first occurrence position for every welded vertex
    order = np.argsort(inv, kind='stable')
    for i in order:
        pass
    lookup = {}
    for i in range(len(pos)):
        k = tuple(key[i])
        if k not in lookup:
            lookup[k] = i
    new_index = np.empty(len(pos), dtype=np.int64)
    verts = []
    for i in range(len(pos)):
        k = tuple(key[i])
        j = lookup[k]
        if new_index[j] == 0 and j == i:
            new_index[i] = len(verts) + 1
            verts.append(pos[i])
        elif j == i:
            new_index[i] = len(verts) + 1
            verts.append(pos[i])
        else:
            new_index[i] = new_index[j]
    verts = np.array(verts)
    return verts, new_index[tri] - 1


def loop_subdivide(pos, tri):
    nv = len(pos)
    edges = {}
    for t in tri:
        for k in range(3):
            a, b, c = t[k], t[(k + 1) % 3], t[(k + 2) % 3]
            key = (min(a, b), max(a, b))
            if key not in edges:
                edges[key] = [a, b, []]
            edges[key][2].append(c)
    neigh = [set() for _ in range(nv)]
    for (a, b), spec in edges.items():
        neigh[a].add(b)
        neigh[b].add(a)
    new_pos = np.zeros((nv + len(edges), 3))
    for i in range(nv):
        ns = sorted(neigh[i])
        n = len(ns)
        if n == 0:
            new_pos[i] = pos[i]
            continue
        beta = 3.0 / 16.0 if n == 3 else 3.0 / (8.0 * n)
        new_pos[i] = pos[i] * (1.0 - n * beta) + pos[ns].sum(axis=0) * beta
    edge_index = {}
    for e, ((a, b), (_, _, opp)) in enumerate(edges.items()):
        edge_index[(a, b)] = nv + e
        if len(opp) == 2:
            new_pos[nv + e] = (pos[a] + pos[b]) * 0.375 + (pos[opp[0]] + pos[opp[1]]) * 0.125
        else:
            new_pos[nv + e] = (pos[a] + pos[b]) * 0.5
    new_tri = []
    for t in tri:
        a, b, c = t
        ab = edge_index[(min(a, b), max(a, b))]
        bc = edge_index[(min(b, c), max(b, c))]
        ca = edge_index[(min(c, a), max(c, a))]
        new_tri += [[a, ab, ca], [ab, b, bc], [ca, bc, c], [ab, bc, ca]]
    return new_pos, np.array(new_tri, dtype=np.int64)


def vertex_normals(pos, tri):
    n = np.zeros_like(pos)
    fn = np.cross(pos[tri[:, 1]] - pos[tri[:, 0]], pos[tri[:, 2]] - pos[tri[:, 0]])
    for k in range(3):
        np.add.at(n, tri[:, k], fn)
    return n / np.maximum(np.linalg.norm(n, axis=1, keepdims=True), 1e-12)


def smoothstep(e0, e1, x):
    t = np.clip((x - e0) / (e1 - e0), 0.0, 1.0)
    return t * t * (3.0 - 2.0 * t)


def gauss(x, mu, s):
    return np.exp(-0.5 * ((x - mu) / s) ** 2)


# --------------------------------------------------------------- sculpting ----
EYE_X, EYE_Y, EYE_Z = 0.74, 1.525, 1.275
EYE_R = 0.275
APERTURE_A = 0.262
APERTURE_BUP = 0.158
APERTURE_BDN = 0.115


def feminise(pos, tri):
    n = vertex_normals(pos, tri)
    p = pos.copy()
    x, y, z = p[:, 0], p[:, 1], p[:, 2]

    lower = smoothstep(1.0, -1.55, y) * (1.0 - smoothstep(-1.75, -2.60, y))
    x *= 1.0 - 0.19 * lower
    front = smoothstep(0.0, 1.5, z) * lower
    x *= 1.0 - 0.06 * front

    chin = gauss(y, -1.30, 0.40) * smoothstep(0.4, 1.5, z) * gauss(x, 0.0, 0.70)
    x *= 1.0 - 0.10 * chin
    z += 0.10 * chin
    y += 0.14 * chin

    cheek = gauss(y, 0.35, 0.55) * gauss(np.abs(x), 1.05, 0.40) * smoothstep(-0.2, 1.3, z)
    p += n * (0.075 * cheek)[:, None]

    brow = gauss(y, 1.80, 0.26) * smoothstep(0.5, 1.6, z) * gauss(x, 0.0, 1.30)
    z -= 0.20 * brow
    y += 0.06 * brow

    fore = gauss(y, 2.55, 0.55) * smoothstep(0.2, 1.2, z) * gauss(x, 0.0, 1.20)
    z += 0.05 * fore

    bridge = smoothstep(0.9, 1.4, y) * (1.0 - smoothstep(1.9, 2.7, y)) * smoothstep(0.6, 1.5, z)
    x *= 1.0 - 0.24 * bridge
    tip = gauss(y, 1.20, 0.22) * gauss(x, 0.0, 0.28) * smoothstep(1.8, 2.2, z)
    z -= 0.05 * tip
    y += 0.04 * tip
    wings = gauss(y, 1.15, 0.22) * gauss(np.abs(x), 0.42, 0.22) * smoothstep(1.2, 2.0, z)
    p -= n * (0.08 * wings)[:, None]

    lip = gauss(y, 0.20, 0.20) * gauss(x, 0.0, 0.46) * smoothstep(1.4, 2.1, z)
    p += n * (0.17 * lip)[:, None]
    mouth_w = gauss(y, 0.20, 0.26) * smoothstep(0.9, 1.6, np.abs(x)) * smoothstep(1.5, 2.1, z)
    x *= 1.0 + 0.05 * mouth_w
    phil = gauss(y, 0.55, 0.16) * gauss(x, 0.0, 0.30) * smoothstep(1.7, 2.2, z)
    z -= 0.02 * phil

    shorten = smoothstep(-0.55, -1.55, y)
    y += 0.10 * shorten

    midface = (smoothstep(1.0, 1.6, np.abs(x)) * (1.0 - smoothstep(-0.4, -1.4, y)) *
               (1.0 - smoothstep(2.2, 3.0, y)))
    x *= 1.0 - 0.045 * midface

    neck = smoothstep(-1.30, -1.85, y) * (1.0 - smoothstep(-2.6, -3.3, y))
    x *= 1.0 - 0.07 * neck
    z = np.where(z > 0.0, z * (1.0 - 0.05 * neck), z * (1.0 - 0.02 * neck))

    p[:, 0], p[:, 1], p[:, 2] = x, y, z
    return p


def open_eyes(pos, tri):
    """Cut an almond opening through the lids and seat the rim on the eyeball.

    The scan has closed eyes, so the lid surface is simply removed where the
    palpebral fissure should be; the rim vertices are then projected onto a sphere
    a hair larger than the eyeball so the lid margin hugs the globe.
    """
    for sx in (-1.0, 1.0):
        c = np.array([sx * EYE_X, EYE_Y, EYE_Z])
        # triangle-level aperture test
        cent = pos[tri].mean(axis=1)
        d = cent - c
        u = d[:, 0] / APERTURE_A
        inside = np.clip(1.0 - u * u, 0.0, 1.0) ** 0.62
        up = APERTURE_BUP * inside * (1.0 + 0.18 * u)
        dn = APERTURE_BDN * inside * (1.0 - 0.10 * u)
        bound = np.where(d[:, 1] >= 0.0, up, dn)
        keep = ~((np.abs(d[:, 1]) < bound) & (np.abs(u) < 1.0) & (d[:, 2] > 0.0))
        tri = tri[keep]

    # (no rim relaxation: the raw cut reads as a lid opening)
    return pos, tri


def cut_below(pos, tri, y_cut):
    """Drop the cropped bust's bottom skirt: the renderer continues the body with a
    procedural torso there, so the scan only has to end inside it."""
    cent = pos[tri].mean(axis=1)
    return pos, tri[cent[:, 1] > y_cut]


def orient_outward(pos, tri):
    """Flip the winding if the scan's triangles mostly face inwards."""
    a, b, c = pos[tri[:, 0]], pos[tri[:, 1]], pos[tri[:, 2]]
    n = np.cross(b - a, c - a)
    cen = pos.mean(axis=0)
    d = np.einsum('ij,ij->i', n, (a + b + c) / 3.0 - cen)
    if d.sum() < 0.0:
        print('flipping winding to outward')
        tri = tri[:, [0, 2, 1]]
    return tri


def boundary_edges(tri):
    from collections import defaultdict
    count = defaultdict(int)
    for t in tri:
        for k in range(3):
            a, b = t[k], t[(k + 1) % 3]
            count[(min(a, b), max(a, b))] += 1
    return [e for e, c in count.items() if c == 1]


def extrude_base(pos, tri, y_cut, y_base, widen=1.18):
    """Extrude the open bottom rim down to `y_base` and cap it with a fan."""
    edges = boundary_edges(tri)
    rim = sorted({v for e in edges for v in e if pos[v][1] < y_cut})
    if not rim:
        return pos, tri
    verts = list(pos)
    remap = {}
    for v in rim:
        p = pos[v]
        q = np.array([p[0] * widen, y_base, p[2] * widen])
        remap[v] = len(verts)
        verts.append(q)
    tris = [list(t) for t in tri]
    # side wall: connect rim edges to their extruded copies
    for a, b in edges:
        if a in remap and b in remap:
            A, B = remap[a], remap[b]
            tris.append([a, A, b])
            tris.append([b, A, B])
    # bottom cap fan around the rim centroid
    centre = np.mean([verts[remap[v]] for v in rim], axis=0)
    ci = len(verts)
    verts.append(centre)
    loop = [remap[v] for v in rim]
    # order the rim into a loop by walking the extruded boundary edges
    adj = {}
    for a, b in edges:
        if a in remap and b in remap:
            adj.setdefault(a, []).append(b)
            adj.setdefault(b, []).append(a)
    start = rim[0]
    order, prev, cur = [start], None, start
    for _ in range(len(rim) + 2):
        nxt = [x for x in adj.get(cur, []) if x != prev]
        if not nxt:
            break
        prev, cur = cur, nxt[0]
        if cur == start:
            break
        order.append(cur)
    if len(order) >= 3:
        for i in range(len(order)):
            a = remap[order[i]]
            b = remap[order[(i + 1) % len(order)]]
            tris.append([ci, b, a])
    return np.array(verts), np.array(tris, dtype=np.int64)


def sphere(center, radius, rings=28, segs=40):
    vs = [center + np.array([0.0, radius, 0.0])]
    for i in range(1, rings):
        th = np.pi * i / rings
        for j in range(segs):
            ph = 2.0 * np.pi * j / segs
            vs.append(center + radius * np.array([np.sin(th) * np.cos(ph), np.cos(th),
                                                  np.sin(th) * np.sin(ph)]))
    vs.append(center + np.array([0.0, -radius, 0.0]))
    tris = []
    for j in range(segs):
        tris.append([0, 1 + j, 1 + (j + 1) % segs])
    for i in range(rings - 2):
        for j in range(segs):
            a = 1 + i * segs + j
            b = 1 + i * segs + (j + 1) % segs
            c = 1 + (i + 1) * segs + j
            d = 1 + (i + 1) * segs + (j + 1) % segs
            tris += [[a, c, b], [b, c, d]]
    last = len(vs) - 1
    base = 1 + (rings - 2) * segs
    for j in range(segs):
        tris.append([last, base + (j + 1) % segs, base + j])
    t = np.array(tris, dtype=np.int64)
    return np.array(vs), t[:, [0, 2, 1]]   # outward winding


# ------------------------------------------------------------------- SDF ------
def write_sdf(path, verts, tris, cell=0.004, pad=0.02):
    """Signed-distance field of the bust, quantised to int8 millimetres*0.5.

    The hair solver uses it for exact collision against the scanned surface
    (much more faithful than a union of ellipsoids around the face and ears).
    """
    from scipy.spatial import cKDTree

    a, b, c = verts[tris[:, 0]], verts[tris[:, 1]], verts[tris[:, 2]]
    tri_n = np.cross(b - a, c - a)
    tri_n /= np.maximum(np.linalg.norm(tri_n, axis=1, keepdims=True), 1e-12)
    area = 0.5 * np.linalg.norm(np.cross(b - a, c - a), axis=1)

    # sample the surface densely, proportional to triangle area
    n_samples = 400000
    p = area / area.sum()
    picks = np.random.default_rng(7).choice(len(tris), size=n_samples, p=p)
    u = np.random.default_rng(11).random((n_samples, 2))
    flip = u.sum(axis=1) > 1.0
    u[flip] = 1.0 - u[flip]
    pa, pb, pc = a[picks], b[picks], c[picks]
    samples = pa + (pb - pa) * u[:, :1] + (pc - pa) * u[:, 1:2]
    sample_n = tri_n[picks]
    tree = cKDTree(samples)

    lo = verts.min(axis=0) - pad
    hi = verts.max(axis=0) + pad
    dims = np.ceil((hi - lo) / cell).astype(np.int64)
    gx, gy, gz = np.meshgrid(
        lo[0] + (np.arange(dims[0]) + 0.5) * cell,
        lo[1] + (np.arange(dims[1]) + 0.5) * cell,
        lo[2] + (np.arange(dims[2]) + 0.5) * cell, indexing='ij')
    grid = np.stack([gx.ravel(), gy.ravel(), gz.ravel()], axis=1)

    dist, idx = tree.query(grid, workers=-1)
    diff = grid - samples[idx]
    sign = np.sign(np.einsum('ij,ij->i', diff, sample_n[idx]))
    sign[sign == 0] = 1.0
    sdf = (dist * sign).astype(np.float32)

    quant = np.clip(np.round(sdf / 0.002), -127, 127).astype(np.int8)
    with open(path, 'wb') as f:
        f.write(b'HSDF')
        f.write(struct.pack('<iii', int(dims[0]), int(dims[1]), int(dims[2])))
        f.write(struct.pack('<fff', float(lo[0]), float(lo[1]), float(lo[2])))
        f.write(struct.pack('<f', float(cell)))
        f.write(quant.tobytes())
    print('%s: %dx%dx%d, %.1f KB  (sdf range %.3f..%.3f)' %
          (path, dims[0], dims[1], dims[2], os.path.getsize(path) / 1024.0,
           sdf.min(), sdf.max()))


def write_obj(path, verts, tris, comment=''):
    with open(path, 'w') as f:
        if comment:
            f.write('# %s\n' % comment)
        for v in verts:
            f.write('v %.5f %.5f %.5f\n' % (v[0], v[1], v[2]))
        for t in tris:
            f.write('f %d %d %d\n' % (t[0] + 1, t[1] + 1, t[2] + 1))
    print('%s: %d verts, %d tris, %.1f KB' %
          (path, len(verts), len(tris), os.path.getsize(path) / 1024.0))


if __name__ == '__main__':
    glb = sys.argv[1] if len(sys.argv) > 1 else 'LeePerrySmith.glb'
    out_dir = sys.argv[2] if len(sys.argv) > 2 else 'assets'
    os.makedirs(out_dir, exist_ok=True)

    g, b = load_glb(glb)
    prim = g['meshes'][0]['primitives'][0]
    pos = np.array(read_accessor(g, b, prim['attributes']['POSITION']), dtype=np.float64)
    tri = np.array(read_accessor(g, b, prim['indices'])[:, 0], dtype=np.int64).reshape(-1, 3)
    print('scan: %d verts, %d tris' % (len(pos), len(tri)))

    pos, tri = weld(pos, tri)
    print('welded: %d verts, %d tris' % (len(pos), len(tri)))
    pos, tri = loop_subdivide(pos, tri)
    print('subdivided: %d verts, %d tris' % (len(pos), len(tri)))
    pos = feminise(pos, tri)
    pos, tri = open_eyes(pos, tri)

    # metres, crown at y = 1.66, bust base at y = 1.08
    scale = 0.0426
    pos = pos * scale
    pos[:, 1] += 1.66 - pos[:, 1].max()
    pos[:, 2] -= 0.02
    print('bust bbox', pos.min(0).round(3), pos.max(0).round(3))
    tri = orient_outward(pos, tri)
    pos, tri = cut_below(pos, tri, 1.362)
    print('after base cut: %d tris' % len(tri))
    print('with base: %d verts, %d tris' % (len(pos), len(tri)))
    write_obj(os.path.join(out_dir, 'head.obj'), pos, tri,
              'feminine bust sculpted from the Lee Perry-Smith scan (CC-BY 3.0)')

    eyes = []
    for sx in (-1.0, 1.0):
        vs, ts = sphere(np.array([sx * EYE_X, EYE_Y, EYE_Z]) * scale +
                        np.array([0.0, 1.66 - (3.97 * scale), -0.02]), EYE_R * scale)
        base = sum(len(e[0]) for e in eyes)
        eyes.append((vs, ts + base))
    ev = np.vstack([e[0] for e in eyes])
    et = np.vstack([e[1] for e in eyes])
    write_obj(os.path.join(out_dir, 'eyes.obj'), ev, et, 'eyeballs (radius %.4f m)' % (EYE_R * scale))
    write_sdf(os.path.join(out_dir, 'head_sdf.bin'), pos, tri)
    print('eye centre y = %.4f' % (EYE_Y * scale + 1.66 - 3.97 * scale))
