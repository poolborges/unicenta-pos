#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KriolOS POS - Internationalization (i18n) & Translation Automation Tool
========================================================================
- Automatically scans properties from:
    1) kriolos-opos-app (pos_messages.properties)
    2) kriolos-opos-assets-reports (com/openbravo/reports/*_messages.properties)
- Uses Google Translation (with GTX endpoint, robust retry, rate-limiting, variable protection)
  or Gemini API fallback.
- For each supported language, creates/updates a Maven module inside kriolos-opos-i18n:
    * pom.xml inheriting from kriolos-opos-i18n parent pom
    * src/main/java/io/github/kriolos/opos/i18n/<lang>/<Lang>I18nProvider.java (SPI implementation)
    * src/main/java/module-info.java
    * Translated properties in src/main/resources/...
    * Updates kriolos-opos-i18n/pom.xml <modules> automatically.
"""

import os
import re
import sys
import time
import fnmatch
import json
import urllib.request
import urllib.parse
from datetime import datetime
import argparse

# ==============================================================================
# CONFIGURATION & SUPPORTED LANGUAGES
# ==============================================================================
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
I18N_PARENT_DIR = os.path.join(BASE_DIR, "kriolos-opos-i18n")
I18N_PARENT_POM = os.path.join(I18N_PARENT_DIR, "pom.xml")

SOURCE_CONFIGS = [
    {
        "name": "kriolos-opos-app",
        "base_dir": os.path.join(BASE_DIR, "kriolos-opos-app", "src", "main", "resources"),
        "pattern": "pos_messages.properties",
        "dest_subpath": ""  # placed directly in root of resources
    },
    {
        "name": "kriolos-opos-assets-reports",
        "base_dir": os.path.join(BASE_DIR, "kriolos-opos-assets-reports", "src", "main", "resources"),
        "pattern": "*_messages.properties",
        "dest_subpath": os.path.join("com", "openbravo", "reports")
    }
]

SOURCE_LANG_CODE = "en"
SOURCE_LANG_NAME = "English"

TARGET_LANGS = {
    "pt": {
        "name": "Portuguese",
        "native": "Português",
        "module_name": "kriolos-opos-i18n_pt",
        "pkg": "pt",
        "class_prefix": "Pt",
        "selectors": ["i18n:pt", "i18n:por"]
    },
    "es": {
        "name": "Spanish",
        "native": "Español",
        "module_name": "kriolos-opos-i18n_es",
        "pkg": "es",
        "class_prefix": "Es",
        "selectors": ["i18n:es", "i18n:spa"]
    },
    "fr": {
        "name": "French",
        "native": "Français",
        "module_name": "kriolos-opos-i18n_fr",
        "pkg": "fr",
        "class_prefix": "Fr",
        "selectors": ["i18n:fr", "i18n:fra"]
    },
    "ar": {
        "name": "Arabic",
        "native": "العربية",
        "module_name": "kriolos-opos-i18n_ar",
        "pkg": "ar",
        "class_prefix": "Ar",
        "selectors": ["i18n:ar", "i18n:ara"]
    },
    "zh-CN": {
        "name": "Chinese (Simplified)",
        "native": "中文 (简体)",
        "module_name": "kriolos-opos-i18n_zh_cn",
        "pkg": "zh_cn",
        "class_prefix": "ZhCn",
        "selectors": ["i18n:zh-CN", "i18n:zh_CN", "i18n:zho"]
    },
    "ru": {
        "name": "Russian",
        "native": "Русский",
        "module_name": "kriolos-opos-i18n_ru",
        "pkg": "ru",
        "class_prefix": "Ru",
        "selectors": ["i18n:ru", "i18n:rus"]
    },
    "id": {
        "name": "Indonesian",
        "native": "Bahasa Indonesia",
        "module_name": "kriolos-opos-i18n_id",
        "pkg": "id",
        "class_prefix": "Id",
        "selectors": ["i18n:id", "i18n:ind"]
    },
    "hi": {
        "name": "Hindi",
        "native": "हिन्दी",
        "module_name": "kriolos-opos-i18n_hi",
        "pkg": "hi",
        "class_prefix": "Hi",
        "selectors": ["i18n:hi", "i18n:hin"]
    },
    "bn": {
        "name": "Bengali",
        "native": "বাংলা",
        "module_name": "kriolos-opos-i18n_bn",
        "pkg": "bn",
        "class_prefix": "Bn",
        "selectors": ["i18n:bn", "i18n:ben"]
    },
    "jv": {
        "name": "Javanese",
        "native": "Basa Jawa",
        "module_name": "kriolos-opos-i18n_jv",
        "pkg": "jv",
        "class_prefix": "Jv",
        "selectors": ["i18n:jv", "i18n:jav"]
    },
    "ar-KW": {
        "name": "Arabic (Kuwait)",
        "native": "العربية (الكويت)",
        "module_name": "kriolos-opos-i18n_ar_kw",
        "pkg": "ar_kw",
        "class_prefix": "ArKw",
        "selectors": ["i18n:ar-KW", "i18n:ar_KW"]
    }
}

LOG_FILE = os.path.join(os.path.dirname(__file__), "translation_process.log")
BATCH_SIZE = 25
BATCH_SEPARATOR = " ### "
REQUEST_DELAY = 0.35

# ==============================================================================
# LOGGING UTILITIES
# ==============================================================================
def log_message(message):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    formatted = f"[{timestamp}] {message}"
    print(formatted)
    try:
        with open(LOG_FILE, "a", encoding="utf-8") as f:
            f.write(formatted + "\n")
    except Exception:
        pass

def log_progress(message):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    formatted = f"\r[{timestamp}] {message}".ljust(85)
    print(formatted, end="", flush=True)

# ==============================================================================
# PROPERTIES PARSING & WRITING
# ==============================================================================
def parse_properties_with_comments(file_path):
    properties, comments = {}, {}
    if not os.path.exists(file_path):
        return properties, comments

    current_comments = []
    with open(file_path, "r", encoding="utf-8", errors="replace") as f:
        lines = f.readlines()

    i = 0
    while i < len(lines):
        line = lines[i]
        line_stripped = line.strip()

        if not line_stripped or line_stripped.startswith("#") or line_stripped.startswith("!"):
            current_comments.append(line)
            i += 1
            continue

        if "=" in line_stripped:
            key, value = line_stripped.split("=", 1)
            key = key.strip()
            value = value.strip()

            while value.endswith("\\") and (i + 1) < len(lines):
                i += 1
                value = value[:-1] + "\n" + lines[i].strip()

            properties[key] = value
            if current_comments:
                comments[key] = current_comments
                current_comments = []
        i += 1
    return properties, comments

def save_and_sort_properties(file_path, properties_dict, comments_dict):
    sorted_keys = sorted(properties_dict.keys(), key=lambda k: (k.lower(), k))
    os.makedirs(os.path.dirname(file_path), exist_ok=True)
    with open(file_path, "w", encoding="utf-8") as f:
        for key in sorted_keys:
            if key in comments_dict:
                for comment in comments_dict[key]:
                    f.write(comment if comment.endswith("\n") else comment + "\n")
            val = properties_dict[key]
            escaped_value = val.replace("\n", "\\\n")
            f.write(f"{key} = {escaped_value}\n")

# ==============================================================================
# VARIABLE & PLACEHOLDER PROTECTION
# ==============================================================================
def protect_variables(text):
    """Protects formatting tokens: {0}, %s, HTML tags, and line breaks."""
    placeholders = []
    
    def replace_token(match):
        idx = len(placeholders)
        placeholders.append(match.group(0))
        return f" __VAR_{idx}__ "

    # 1. Protect HTML tags
    text = re.sub(r"<[^>]+>", replace_token, text)
    # 2. Protect MessageFormat curly tokens: {0}, {name}
    text = re.sub(r"\{[a-zA-Z0-9_.-]+\}", replace_token, text)
    # 3. Protect printf tokens: %s, %d, %1$s
    text = re.sub(r"%\d*\$?[a-zA-Z]", replace_token, text)
    
    return text, placeholders

def restore_variables(text, placeholders):
    """Restores protected formatting tokens."""
    for i, ph in enumerate(placeholders):
        # Allow optional whitespace around placeholder
        text = re.sub(rf"\s*__\s*VAR_{i}\s*__\s*", ph, text, flags=re.IGNORECASE)
    return text.strip()

# ==============================================================================
# GOOGLE TRANSLATION ENGINE (GTX REST API)
# ==============================================================================
def translate_gtx_single(text, source_lang, target_lang):
    target_clean = target_lang.split("-")[0] if target_lang not in ["zh-CN", "zh-TW", "pt-PT", "pt-BR"] else target_lang
    url = f"https://translate.googleapis.com/translate_a/single?client=gtx&sl={source_lang}&tl={target_clean}&dt=t&q=" + urllib.parse.quote(text)
    req = urllib.request.Request(url, headers={"User-Agent": "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/119.0"})
    
    for attempt in range(3):
        try:
            with urllib.request.urlopen(req, timeout=12) as resp:
                data = json.loads(resp.read().decode("utf-8"))
                return "".join(part[0] for part in data[0] if part and part[0])
        except Exception as e:
            if attempt == 2:
                raise e
            time.sleep(1.0 + attempt)
    return text

def translate_batch_google(batch_keys, base_properties, existing_properties, target_code):
    prepared_texts = []
    all_placeholders = []

    for key in batch_keys:
        orig = base_properties[key]
        if not orig.strip():
            prepared_texts.append("---EMPTY---")
            all_placeholders.append([])
        else:
            safe = orig.replace("\n", " [BR] ")
            prot, phs = protect_variables(safe)
            prepared_texts.append(prot)
            all_placeholders.append(phs)

    combined_text = BATCH_SEPARATOR.join(prepared_texts)
    
    # Try batch translation first
    try:
        translated_combined = translate_gtx_single(combined_text, SOURCE_LANG_CODE, target_code)
        escaped_sep = re.escape(BATCH_SEPARATOR.strip())
        parts = re.split(rf"\s*{escaped_sep}\s*", translated_combined)
    except Exception:
        parts = []

    # If count matches, assign directly
    if len(parts) == len(batch_keys):
        for idx, key in enumerate(batch_keys):
            if prepared_texts[idx] == "---EMPTY---":
                existing_properties[key] = ""
            else:
                raw = parts[idx]
                restored = restore_variables(raw, all_placeholders[idx])
                clean_val = restored.replace(" [BR] ", "\n").replace("[BR]", "\n").replace("[br]", "\n")
                existing_properties[key] = clean_val
    else:
        # Fallback to item-by-item translation
        for idx, key in enumerate(batch_keys):
            if prepared_texts[idx] == "---EMPTY---":
                existing_properties[key] = ""
            else:
                try:
                    single_res = translate_gtx_single(prepared_texts[idx], SOURCE_LANG_CODE, target_code)
                    restored = restore_variables(single_res, all_placeholders[idx])
                    clean_val = restored.replace(" [BR] ", "\n").replace("[BR]", "\n").replace("[br]", "\n")
                    existing_properties[key] = clean_val
                except Exception:
                    existing_properties[key] = base_properties[key]
                time.sleep(REQUEST_DELAY)

# ==============================================================================
# MAVEN SUBPROJECT GENERATION & SYNCHRONIZATION
# ==============================================================================
def ensure_language_project(target_code, lang_config):
    """
    Creates/updates the Maven subproject for a supported language inside kriolos-opos-i18n.
    Generates:
      - pom.xml (with parent kriolos-opos-i18n)
      - module-info.java
      - <Prefix>I18nProvider.java implementing I18nProvider SPI with @AutoService
    Updates:
      - kriolos-opos-i18n/pom.xml (<modules> section)
    """
    module_name = lang_config["module_name"]
    project_dir = os.path.join(I18N_PARENT_DIR, module_name)
    os.makedirs(project_dir, exist_ok=True)

    # 1. Subproject pom.xml
    pom_path = os.path.join(project_dir, "pom.xml")
    if not os.path.exists(pom_path):
        pom_content = f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>io.github.kriolos.opos</groupId>
        <artifactId>kriolos-opos-i18n</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>  
    <groupId>io.github.kriolos.opos</groupId>
    <artifactId>{module_name}</artifactId>
    <packaging>jar</packaging>
    <name>Kriolos.POS.i18N_{lang_config["pkg"].upper()}</name>  
    <properties>
        <skipTests>true</skipTests>
    </properties>
</project>
"""
        with open(pom_path, "w", encoding="utf-8") as f:
            f.write(pom_content)
        log_message(f"   [POM] Criado: {pom_path}")

    # 2. Java Package & I18nProvider SPI Implementation
    pkg_dir = os.path.join(project_dir, "src", "main", "java", "io", "github", "kriolos", "opos", "i18n", lang_config["pkg"])
    os.makedirs(pkg_dir, exist_ok=True)

    class_name = f"{lang_config['class_prefix']}I18nProvider"
    provider_path = os.path.join(pkg_dir, f"{class_name}.java")
    if not os.path.exists(provider_path):
        selectors_str = ", ".join(f'"{s}"' for s in lang_config["selectors"])
        provider_content = f"""package io.github.kriolos.opos.i18n.{lang_config["pkg"]};

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * {lang_config["name"]} ({target_code}) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.{lang_config["pkg"]}",
        service = I18nProvider.class,
        selectors = {{{selectors_str}}}
)
public class {class_name} implements I18nProvider {{

    private static final Locale LOCALE = Locale.forLanguageTag("{target_code}");

    @Override
    public Locale getLocale() {{
        return LOCALE;
    }}

    @Override
    public String getDisplayName() {{
        return "{lang_config["native"]}";
    }}
}}
"""
        with open(provider_path, "w", encoding="utf-8") as f:
            f.write(provider_content)
        log_message(f"   [SPI] Criado: {provider_path}")

    # 3. module-info.java
    module_info_path = os.path.join(project_dir, "src", "main", "java", "module-info.java")
    if not os.path.exists(module_info_path):
        module_info_content = f"""module io.github.kriolos.opos.i18n.{lang_config["pkg"]} {{
    requires kriolos.opos.spi;
    requires static com.google.auto.service;

    provides com.openbravo.pos.spi.i18n.I18nProvider with io.github.kriolos.opos.i18n.{lang_config["pkg"]}.{class_name};
}}
"""
        with open(module_info_path, "w", encoding="utf-8") as f:
            f.write(module_info_content)
        log_message(f"   [JPMS] Criado: {module_info_path}")

    # 4. Synchronize parent pom.xml (<modules>)
    sync_parent_pom_module(module_name)

    # 5. Resources directory
    res_dir = os.path.join(project_dir, "src", "main", "resources")
    os.makedirs(res_dir, exist_ok=True)
    return res_dir

def sync_parent_pom_module(module_name):
    """Ensures module_name is registered in kriolos-opos-i18n/pom.xml <modules> section."""
    if not os.path.exists(I18N_PARENT_POM):
        return
    with open(I18N_PARENT_POM, "r", encoding="utf-8") as f:
        content = f.read()

    module_tag = f"<module>{module_name}</module>"
    if module_tag not in content:
        # Insert before </modules>
        updated = content.replace("</modules>", f"    {module_tag}\n    </modules>")
        with open(I18N_PARENT_POM, "w", encoding="utf-8") as f:
            f.write(updated)
        log_message(f"   [Parent POM] Adicionado módulo '{module_name}' em {I18N_PARENT_POM}")

# ==============================================================================
# SOURCE FILE DISCOVERY
# ==============================================================================
def find_source_files():
    """Finds all properties files matching defined sources."""
    discovered = []
    for cfg in SOURCE_CONFIGS:
        base_dir = cfg["base_dir"]
        pattern = cfg["pattern"]
        dest_subpath = cfg["dest_subpath"]
        
        if not os.path.exists(base_dir):
            log_message(f"⚠️ Diretório fonte inexistente: {base_dir}")
            continue

        for root, _, files in os.walk(base_dir):
            for filename in files:
                if fnmatch.fnmatch(filename, pattern):
                    full_path = os.path.join(root, filename)
                    discovered.append({
                        "source_path": full_path,
                        "source_name": cfg["name"],
                        "dest_subpath": dest_subpath,
                        "filename": filename
                    })
    return discovered

# ==============================================================================
# PROCESS SINGLE FILE FOR TARGET LANGUAGE
# ==============================================================================
def process_file_for_language(file_info, target_code, lang_config, only_sort=False, untranslated=False, force=False):
    source_path = file_info["source_path"]
    filename = file_info["filename"]
    dest_subpath = file_info["dest_subpath"]

    base_name, ext = os.path.splitext(filename)
    # Remove standard suffix if present
    clean_base = base_name.replace("_messages", "")
    java_suffix = target_code.replace("-", "_")
    
    # Destination filename follows Openbravo standard: <base>_messages_<suffix>.properties or pos_messages_<suffix>.properties
    if filename == "pos_messages.properties":
        target_filename = f"pos_messages_{java_suffix}.properties"
    else:
        target_filename = f"{clean_base}_messages_{java_suffix}.properties"

    resources_dir = ensure_language_project(target_code, lang_config)
    target_dir = os.path.join(resources_dir, dest_subpath) if dest_subpath else resources_dir
    os.makedirs(target_dir, exist_ok=True)
    target_path = os.path.join(target_dir, target_filename)

    base_props, base_comments = parse_properties_with_comments(source_path)
    existing_props, existing_comments = parse_properties_with_comments(target_path)

    if only_sort:
        if os.path.exists(target_path):
            save_and_sort_properties(target_path, existing_props, existing_comments)
            log_message(f"   ✅ [Ordenado] {target_path}")
        return

    # Check missing or untranslated keys
    missing_keys = []
    for k in base_props:
        if k not in existing_props:
            missing_keys.append(k)
        elif force:
            missing_keys.append(k)
        elif untranslated and target_code != "pt":
            base_val = base_props[k].strip()
            ext_val = existing_props.get(k, "").strip()
            if not ext_val:
                missing_keys.append(k)
            elif ext_val == base_val and any(c.isalpha() for c in base_val):
                missing_keys.append(k)

    for key in base_props:
        if key in base_comments:
            existing_comments[key] = base_comments[key]

    if not missing_keys:
        save_and_sort_properties(target_path, existing_props, existing_comments)
        return

    log_message(f"   🌐 {target_code} | {target_filename} ({len(missing_keys)} chaves a traduzir)")
    total_batches = (len(missing_keys) - 1) // BATCH_SIZE + 1

    for i in range(0, len(missing_keys), BATCH_SIZE):
        batch = missing_keys[i:i + BATCH_SIZE]
        current_batch = i // BATCH_SIZE + 1
        try:
            translate_batch_google(batch, base_props, existing_props, target_code)
            log_progress(f"      [+] Lote Google {current_batch}/{total_batches} traduzido...")
            time.sleep(REQUEST_DELAY)
        except Exception as e:
            print()
            log_message(f"      ❌ Erro ao traduzir lote: {e}")
            break
        save_and_sort_properties(target_path, existing_props, existing_comments)

    print()
    log_message(f"   💾 Atualizado: {target_path}")

# ==============================================================================
# AUDIT & REPORT
# ==============================================================================
def run_audit(files):
    log_message("=== AUDITORIA DE INTERNACIONALIZAÇÃO (i18n) ===")
    for f_info in files:
        source_path = f_info["source_path"]
        base_props, _ = parse_properties_with_comments(source_path)
        log_message(f"\n📋 Ficheiro Original: {f_info['source_name']}/{f_info['filename']} ({len(base_props)} chaves)")
        
        for target_code, lang_config in TARGET_LANGS.items():
            module_name = lang_config["module_name"]
            java_suffix = target_code.replace("-", "_")
            if f_info["filename"] == "pos_messages.properties":
                target_filename = f"pos_messages_{java_suffix}.properties"
            else:
                clean_base = os.path.splitext(f_info["filename"])[0].replace("_messages", "")
                target_filename = f"{clean_base}_messages_{java_suffix}.properties"

            target_path = os.path.join(I18N_PARENT_DIR, module_name, "src", "main", "resources", f_info["dest_subpath"], target_filename)
            if os.path.exists(target_path):
                ext_props, _ = parse_properties_with_comments(target_path)
                missing = len([k for k in base_props if k not in ext_props])
                status = "✅ 100% Completo" if missing == 0 else f"⚠️ Faltam {missing} chaves"
                log_message(f"   - [{target_code.ljust(5)}] {status}")
            else:
                log_message(f"   - [{target_code.ljust(5)}] ❌ Inexistente (Faltam {len(base_props)} chaves)")

def scaffold_all_projects(files):
    """Scaffolds Maven module structure and seeds properties files for all languages in TARGET_LANGS."""
    log_message("=== INICIALIZANDO TODOS OS MÓDULOS DE IDIOMAS (TARGET_LANGS) ===")
    for target_code, lang_config in TARGET_LANGS.items():
        res_dir = ensure_language_project(target_code, lang_config)
        java_suffix = target_code.replace("-", "_")
        for f_info in files:
            source_path = f_info["source_path"]
            filename = f_info["filename"]
            dest_subpath = f_info["dest_subpath"]
            target_dir = os.path.join(res_dir, dest_subpath) if dest_subpath else res_dir
            os.makedirs(target_dir, exist_ok=True)
            if filename == "pos_messages.properties":
                target_filename = f"pos_messages_{java_suffix}.properties"
            else:
                clean_base = os.path.splitext(filename)[0].replace("_messages", "")
                target_filename = f"{clean_base}_messages_{java_suffix}.properties"
            target_path = os.path.join(target_dir, target_filename)
            if not os.path.exists(target_path):
                props, comments = parse_properties_with_comments(source_path)
                save_and_sort_properties(target_path, props, comments)
        log_message(f"   ✅ Módulo pronto e populado: {lang_config['module_name']} ({target_code})")

# ==============================================================================
# MAIN ENTRY POINT
# ==============================================================================
def main():
    parser = argparse.ArgumentParser(description="KriolOS POS Internationalization (i18n) Tool")
    parser.add_argument("--langs", type=str, default="", help="Comma-separated list of target language codes (e.g. pt,es,fr,ar)")
    parser.add_argument("--audit", action="store_true", help="Audit and report missing keys across all languages")
    parser.add_argument("--sort", action="store_true", help="Sort all properties files alphabetically")
    parser.add_argument("--init-all", action="store_true", help="Scaffold all language projects and seed properties for all TARGET_LANGS")
    parser.add_argument("--all", action="store_true", help="Process all supported languages")
    parser.add_argument("--untranslated", action="store_true", help="Translate keys that are empty or still identical to English source")
    parser.add_argument("--force", action="store_true", help="Force re-translation of all keys even if already present")
    args = parser.parse_args()

    files = find_source_files()
    log_message(f"📂 Encontrados {len(files)} ficheiros de recursos originais.")

    if args.audit:
        run_audit(files)
        return

    if args.init_all:
        scaffold_all_projects(files)
        log_message("\n=== INICIALIZAÇÃO DE TODOS OS MÓDULOS CONCLUÍDA ===")
        return

    # Filter target languages
    if args.langs:
        requested = [l.strip() for l in args.langs.split(",") if l.strip()]
        selected_langs = {k: v for k, v in TARGET_LANGS.items() if k in requested or v["pkg"] in requested}
    elif args.all:
        selected_langs = TARGET_LANGS
    else:
        # Default: process pt and top tier languages (pt, es, fr, ar, zh-CN)
        default_keys = ["pt", "es", "fr", "ar", "zh-CN"]
        selected_langs = {k: v for k, v in TARGET_LANGS.items() if k in default_keys}

    log_message(f"🌐 Idiomas selecionados ({len(selected_langs)}): {', '.join(selected_langs.keys())}")

    for target_code, lang_config in selected_langs.items():
        log_message(f"\n=======================================================")
        log_message(f"▶ Processando idioma: {lang_config['name']} ({target_code})")
        log_message(f"=======================================================")
        for file_info in files:
            process_file_for_language(file_info, target_code, lang_config, only_sort=args.sort, untranslated=args.untranslated, force=args.force)

    log_message("\n=== PROCESSO DE INTERNACIONALIZAÇÃO CONCLUÍDO COM SUCESSO ===")

if __name__ == "__main__":
    main()