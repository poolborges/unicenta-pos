#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script utilitário para correção e restauração de caracteres UTF-8 e eliminação
de mojibake/double-encoding em ficheiros de propriedades (.properties), XML, etc.

Autor: KriolOS
"""

import os
import re
import sys

def corrigir_texto_quebrado(conteudo):
    """
    Corrige texto com dupla codificação UTF-8 (mojibake) e converte
    escapes unicode para caracteres UTF-8 nativos limpos.
    """
    if not conteudo:
        return conteudo

    # 1. Decodificar sequências consecutivas de \\u00XX que representam bytes de UTF-8 interpretados como Latin-1
    def decodificar_utf8_escapado(match):
        raw = match.group(0)
        hex_codes = raw.split("\\u")[1:]
        try:
            byte_vals = bytes(int(h, 16) for h in hex_codes)
            return byte_vals.decode("utf-8")
        except UnicodeDecodeError:
            return raw

    # Procura sequências contínuas de \\u00XX ou \\uXXXX
    conteudo = re.sub(r"(?:\\u[0-9a-fA-F]{4})+", decodificar_utf8_escapado, conteudo)

    # 2. Decodificar escapes unicode individuais padrão remanescentes (ex: \\u00ed -> í, \\u00e7 -> ç)
    def decodificar_escape_unico(match):
        try:
            return chr(int(match.group(1), 16))
        except (ValueError, OverflowError):
            return match.group(0)

    conteudo = re.sub(r"\\u([0-9a-fA-F]{4})", decodificar_escape_unico, conteudo)

    # 3. Mapeamentos diretos para mojibake em texto puro (1º e 2º grau) que possam ter sobrado
    substituicoes_diretas = {
        # Dupla quebra específica (2º grau)
        "ÃƒÂ§": "ç", "ÃƒÂ£": "ã", "ÃƒÂµ": "õ", "ÃƒÂ¡": "á", "ÃƒÂ©": "é",
        "ÃƒÂ­": "í", "ÃƒÂ³": "ó", "ÃƒÂº": "ú", "ÃƒÂ¢": "â", "ÃƒÂª": "ê",
        "ÃƒÂ´": "ô", "ÃƒÂ€": "À",
        # Mojibake comum (1º grau - bytes UTF-8 lidos como ISO-8859-1)
        "Ã¡": "á", "Ã ": "à", "Ã¢": "â", "Ã£": "ã",
        "Ã©": "é", "Ã¨": "é", "Ãª": "ê",
        "Ã­": "í",
        "Ã³": "ó", "Ã´": "ô", "Ãµ": "õ",
        "Ãº": "ú",
        "Ã§": "ç",
        "Ã": "Á", "Ã€": "À", "Ã‚": "Â", "Ãƒ": "Ã",
        "Ã‰": "É", "ÃŠ": "Ê",
        "Ã": "Í",
        "Ã“": "Ó", "Ã”": "Ô", "Ã•": "Õ",
        "Ãš": "Ú",
        "Ã‡": "Ç",
        "Âª": "ª", "Âº": "º",
        "âˆš": "√"
    }

    for quebrado, correto in substituicoes_diretas.items():
        conteudo = conteudo.replace(quebrado, correto)

    return conteudo


def processar_diretorios(diretorio_alvo):
    """
    Percorre o diretório recursivamente e corrige todos os ficheiros .properties.
    """
    arquivos_modificados = 0
    total_verificados = 0

    print(f"A analisar ficheiros .properties em: {diretorio_alvo}\n")

    for raiz, dirs, arquivos in os.walk(diretorio_alvo):
        # Ignorar pastas de compilação temporárias e git
        dirs[:] = [d for d in dirs if d not in ("target", ".git", "build", "dist", "node_modules", "venv", ".venv")]

        for arquivo in arquivos:
            if arquivo.endswith(".properties"):
                total_verificados += 1
                caminho_completo = os.path.join(raiz, arquivo)

                try:
                    with open(caminho_completo, "r", encoding="utf-8", errors="ignore") as f:
                        conteudo_original = f.read()

                    conteudo_corrigido = corrigir_texto_quebrado(conteudo_original)

                    if conteudo_original != conteudo_corrigido:
                        with open(caminho_completo, "w", encoding="utf-8") as f:
                            f.write(conteudo_corrigido)
                        rel_path = os.path.relpath(caminho_completo, diretorio_alvo)
                        print(f" [CORRIGIDO] {rel_path}")
                        arquivos_modificados += 1

                except Exception as e:
                    print(f" [ERRO] Falha ao processar {arquivo}: {e}")

    print(f"\nConcluído!")
    print(f" - Ficheiros analisados: {total_verificados}")
    print(f" - Ficheiros corrigidos: {arquivos_modificados}")


if __name__ == "__main__":
    if len(sys.argv) > 1:
        diretorio = sys.argv[1]
    else:
        # Por defeito usa o diretório do projeto
        diretorio = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))

    processar_diretorios(diretorio)
