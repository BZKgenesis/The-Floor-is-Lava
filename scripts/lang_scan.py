#!/usr/bin/env python3

import os
import re
import yaml
from itertools import groupby

# -------------------------------------------------------
# Configuration
# -------------------------------------------------------

JAVA_ROOT = "./src"
LANG_ROOT = "./src/main/resources/lang"
OUTPUT_FILE = "./scripts/translation_candidates.txt"

# -------------------------------------------------------
# Chargement des traductions existantes
# -------------------------------------------------------

translations = {}

def dict_to_paths(d, prefix=""):
    result = []

    for key, value in d.items():
        new_prefix = f"{prefix}.{key}" if prefix else key

        if isinstance(value, dict):
            result.extend(dict_to_paths(value, new_prefix))
        else:
            result.append(new_prefix)

    return result

for file in os.listdir(LANG_ROOT):
    if file.endswith(".yml"):
        with open(os.path.join(LANG_ROOT, file), encoding="utf8") as f:
            data = yaml.safe_load(f)
            if data:
                translations[file] = dict_to_paths(data)


# -------------------------------------------------------
# Méthodes qui affichent du texte
# -------------------------------------------------------

PATTERNS = [

    r'\s*Messages\.send[^\(]*\([^"]*"([^"]*)"',
    r'\s*Messages\.broadcast[^\(]*\(\s*"([^"]*)"',
    r'\s*Messages\.string\([^"]*"([^"]*)"',
    r'\s*Messages\.component\([^"]*"([^"]*)"',
    r'\s*super\("[^"]*",\n\s*"([^\.]*\.[^"]*)",',
    r'\s*new ConfigKey<>\(\n\s*"[^"]*",\n\s*"([^"]*)",',
    
]

regexes = [re.compile(p) for p in PATTERNS]

# -------------------------------------------------------
# Chaines à ignorer
# -------------------------------------------------------

def ignore(s: str):
    s = s.strip()

    if len(s) <= 1:
        return True

    # namespace
    if s.startswith("minecraft:"):
        return True

    if s.startswith("tfl:"):
        return True

    # commandes
    if s.startswith("/"):
        return True

    # regex
    if "\\" in s:
        return True

    # couleur
    if re.fullmatch(r"#[0-9A-Fa-f]{6}", s):
        return True

    # sql
    sql_words = (
        "SELECT",
        "INSERT",
        "UPDATE",
        "DELETE",
        "CREATE TABLE",
        "DROP TABLE",
        "VALUES",
    )

    
    if any(word in s for word in sql_words):
        return True

    return False

# -------------------------------------------------------
# Scan
# -------------------------------------------------------

results = []

for root, _, files in os.walk(JAVA_ROOT):
    for file in files:
        if not file.endswith(".java"):
            continue
        path = os.path.join(root, file)
        with open(path, encoding="utf8") as f:
            text = f.read()
        for regex in regexes:
            for m in regex.finditer(text):
                string = m.group(1)
                if ignore(string):
                    continue
                for lang_file,translation in translations.items():
                    if string in translation:
                        continue
                    line = text.count("\n", 0, m.start()) + 1
                    results.append((lang_file, path, line, string))

# suppression des doublons

results = sorted(set(results))

# -------------------------------------------------------
# Export
# -------------------------------------------------------

with open(OUTPUT_FILE, "w", encoding="utf8") as f:
    
    for lang_file, line in groupby(results, lambda x: x[0]):
        f.write(f"{lang_file}\n")
        for file,item  in groupby(line, lambda x: x[1]):
            f.write(f"  {file}\n")
            for _,_,line,string in item:
                f.write(f'    {line}:"{string}"\n')

print(f"{len(results)} chaînes candidates trouvées.")
print(f"Résultat écrit dans {OUTPUT_FILE}")