import os

pasta_projeto = "/home/dev/PDEV/kriolos-obiz/kriolos-pos/kriolos-opos-i18n_pt/src/main/resources/"

print(f"Verificando se o diretório existe: {os.path.exists(pasta_projeto)}")

# Lista tudo o que está no primeiro nível do diretório para validação
try:
    print(f"Conteúdo imediato da pasta:\n{os.listdir(pasta_projeto)}")
except Exception as e:
    print(f"Erro ao listar pasta raiz: {e}")

print("\n--- Iniciando Varredura Recursiva Completa ---")
ficheiro_encontrado = False

for raiz, dirs, ficheiros in os.walk(pasta_projeto):
    # Imprime cada subpasta visitada para sabermos onde o Python está a passar
    print(f"A examinar pasta: {raiz} (Ficheiros encontrados: {len(ficheiros)})")
    
    for nome in ficheiros:
        caminho_completo = os.path.join(raiz, nome)
        print(f"  -> Detetado: {nome}")
        
        # Se encontrarmos qualquer ficheiro de texto/propriedades, analisamos os bytes
        if nome.endswith((".properties", ".txt", ".xml")) and not ficheiro_encontrado:
            try:
                with open(caminho_completo, "rb") as f:
                    bytes_puros = f.read()
                
                print(f"\n[ANÁLISE DE BYTES BRUTOS DO FICHEIRO: {nome}]")
                print(bytes_puros[:400]) # Mostra os primeiros 400 bytes físicos
                
                texto_utf8 = bytes_puros.decode("utf-8", errors="ignore")
                print(f"\n[TEXTO EM UTF-8]:\n{''.join(texto_utf8.splitlines(keepends=True)[:8])}")
                
                ficheiro_encontrado = True
            except Exception as e:
                print(f"  Erro ao ler bytes de {nome}: {e}")

print("\n--- Fim da Varredura ---")
