import os


def limpar_mojibake_avancado(texto):
    if not texto:
        return texto

    # Mapeamento exato estruturado
    substituicoes = {
        # --- 2º Grau ---
        "ÃƒÂ§": "ç",
        "ÃƒÂ£": "ã",
        "ÃƒÂµ": "õ",
        "ÃƒÂ¡": "á",
        "ÃƒÂ©": "é",
        "ÃƒÂ­": "í",
        "ÃƒÂ³": "ó",
        "ÃƒÂº": "ú",
        "ÃƒÂ¢": "â",
        "ÃƒÂª": "ê",
        "ÃƒÂ´": "ô",
        "ÃƒÂ ": "É",
        "ÃƒÂ ": "Í",
        "ÃƒÂ ": "Ó",
        "ÃƒÂ ": "Ú",
        # --- 1º Grau ---
        "Ã§": "ç",
        "Ã£": "ã",
        "Ãµ": "õ",
        "Ã¡": "á",
        "Ã©": "é",
        "Ã­": "í",
        "Ã³": "ó",
        "Ãº": "ú",
        "Ã¢": "â",
        "Ãª": "ê",
        "Ã´": "ô",
        "Ã‰": "É",
        "Ã ": "Í",
        "Ã ": "Ó",
        "Ã ": "Ú",
        "Ã ": "À",
    }

    for quebrado, correto in substituicoes.items():
        texto = texto.replace(quebrado, correto)

    return texto


def executar_conversao_segura(diretorio_alvo):
    extensoes_validas = (".properties", ".txt", ".xml")
    total_modificados = 0

    print(f"A varrer diretório: {diretorio_alvo}")

    # followlinks=True garante o varrimento caso haja links simbólicos ou mounts
    for raiz, dirs, ficheiros in os.walk(diretorio_alvo, followlinks=True):
        # Ignora a pasta temporária de compilação target se o caminho subir
        if "target" in dirs:
            dirs.remove("target")

        for nome_ficheiro in ficheiros:
            if nome_ficheiro.endswith(extensoes_validas):
                caminho_completo = os.path.join(raiz, nome_ficheiro)

                try:
                    # Lemos como bytes puros
                    with open(caminho_completo, "rb") as f:
                        bytes_puros = f.read()

                    # Técnica mestre: Descodifica como latin-1 para preservar os bytes isolados em formato string de memória
                    texto_memoria = bytes_puros.decode("latin-1")

                    # Aplica a limpeza
                    texto_corrigido = limpar_mojibake_avancado(texto_memoria)

                    # Se houve modificação, codifica de volta e grava
                    if texto_corrigido != texto_memoria:
                        bytes_corrigidos = texto_corrigido.encode("latin-1")
                        with open(caminho_completo, "wb") as f:
                            f.write(bytes_corrigidos)

                        caminho_relativo = caminho_completo.replace(
                            diretorio_alvo, ""
                        )
                        print(f"[MODIFICADO]: {caminho_relativo}")
                        total_modificados += 1

                except Exception as e:
                    print(f"Erro ao processar {nome_ficheiro}: {e}")

    print(
        f"\nProcessamento concluído. Total de ficheiros alterados: {total_modificados}"
    )


if __name__ == "__main__":
    # Aponta para a pasta do módulo i18n principal (ele vai ignorar a pasta target automaticamente)
    # Desta forma, se os ficheiros estiverem emresources, java ou qualquer outra subpasta, serão apanhados.
    pasta_modulo = (
        "/home/dev/PDEV/kriolos-obiz/kriolos-pos/kriolos-opos-i18n_pt/"
    )
    executar_conversao_segura(pasta_modulo)
