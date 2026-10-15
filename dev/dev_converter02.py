import os


def converter_para_utf8_nativo(diretorio_alvo):
    extensoes_validas = (".properties", ".txt", ".xml")
    total_modificados = 0

    print(f"A converter recursos para UTF-8 nativo (Java 17) em: {diretorio_alvo}")

    for raiz, dirs, ficheiros in os.walk(diretorio_alvo):
        # Ignora a pasta temporária de compilação target
        if "target" in dirs:
            dirs.remove("target")

        for nome_ficheiro in ficheiros:
            if nome_ficheiro.endswith(extensoes_validas):
                caminho_completo = os.path.join(raiz, nome_ficheiro)

                try:
                    # 1. Ler o ficheiro interpretando os bytes brutos como Latin-1 (para capturar os <E7>, <E3>, etc.)
                    with open(caminho_completo, "r", encoding="latin-1") as f:
                        conteudo_latin1 = f.read()

                    # 2. Gravar o ficheiro convertendo o texto de forma limpa para UTF-8 puro
                    with open(caminho_completo, "w", encoding="utf-8") as f:
                        f.write(conteudo_latin1)

                    caminho_relativo = caminho_completo.replace(diretorio_alvo, "")
                    print(f"[UTF-8 CONFIGURADO]: {caminho_relativo}")
                    total_modificados += 1

                except Exception as e:
                    print(f"Erro ao converter {nome_ficheiro}: {e}")

    print(f"\nConversão concluída. Total de ficheiros em UTF-8: {total_modificados}")


if __name__ == "__main__":
    pasta_recursos = "/home/dev/PDEV/kriolos-obiz/kriolos-pos/kriolos-opos-i18n_pt/src/main/resources/"
    converter_para_utf8_nativo(pasta_recursos)

