import os
import mimetypes
from collections import Counter

# Lista de pastas que devem ser totalmente ignoradas no escaneamento
PASTAS_IGNORADAS = ["target", "node_modules", "build", "dist"]

# Extensões explicitamente textuais do ecossistema de desenvolvimento (adicione mais se necessário)
EXTENSOES_TEXTO = {
    ".properties", ".java", ".xml", ".sh", ".bat", ".cmd", ".json", 
    ".txt", ".md", ".html", ".css", ".js", ".ts", ".sql", ".yml", ".yaml", 
    ".jsp", ".tag", ".tld", ".ini", ".conf", ".gradle", ".project", ".classpath"
}

def eh_arquivo_de_texto(caminho, extensao):
    # Verifica se a extensão está na nossa lista explícita
    if extensao in EXTENSOES_TEXTO:
        return True
    # Fallback usando adivinhação de tipo do sistema operacional
    tipo_mime, _ = mimetypes.guess_type(caminho)
    if tipo_mime and tipo_mime.startswith('text/'):
        return True
    return False

def verificar_arquivos_nao_utf8():
    diretorio_atual = os.getcwd()
    arquivos_com_erro = 0
    total_arquivos_texto_verificados = 0
    
    total_diretorios = 0
    extensoes_contador = Counter()
    arquivos_nao_utf8_por_tipo = Counter()

    print(f"Analisando todos os arquivos de texto recursivamente em: {diretorio_atual}\n")
    print("=" * 80)

    # os.walk caminha por todas as pastas e subpastas
    for raiz, diretorios, arquivos in os.walk(diretorio_atual):
        # Aplica os filtros de exclusão de pastas ocultas e da lista negra
        diretorios[:] = [
            d for d in diretorios 
            if not d.startswith('.') and d.lower() not in PASTAS_IGNORADAS
        ]
        
        total_diretorios += len(diretorios)

        for arquivo in arquivos:
            _, ext = os.path.splitext(arquivo)
            ext_lower = ext.lower()
            
            if ext_lower:
                extensoes_contador[ext_lower] += 1

            caminho_completo = os.path.join(raiz, arquivo)

            # Só processa a leitura se for um arquivo de texto para evitar travar em binários
            if eh_arquivo_de_texto(caminho_completo, ext_lower):
                total_arquivos_texto_verificados += 1
                
                try:
                    # Força a leitura estrita em UTF-8
                    with open(caminho_completo, "r", encoding="utf-8") as f:
                        f.read()
                except UnicodeDecodeError:
                    # Capturou um arquivo com codificação inválida para UTF-8 (ex: ISO-8859-1)
                    caminho_relativo = os.path.relpath(caminho_completo, diretorio_atual)
                    print(f"[ALERTA - NÃO É UTF-8] ({ext_lower}) {caminho_relativo}")
                    arquivos_com_erro += 1
                    arquivos_nao_utf8_por_tipo[ext_lower] += 1
                except Exception as e:
                    pass # Ignora arquivos inacessíveis ou bloqueados pelo SO

    print("=" * 80)
    print("ANÁLISE DE CODIFICAÇÃO COMPLETA")
    print(f"Total de arquivos de texto verificados: {total_arquivos_texto_verificados}")
    print(f"Total de arquivos que NÃO estão em UTF-8 válido: {arquivos_com_erro}")
    
    if arquivos_com_erro > 0:
        print("\nDetalhamento dos arquivos incorretos por extensão:")
        for ext, qtd in arquivos_nao_utf8_por_tipo.items():
            print(f"  {ext}: {qtd} arquivo(s) corrompido(s)")
            
    print("=" * 80)
    print("RESUMO ESTATÍSTICO DO PROJETO (Ignorando lista de exclusão)")
    print(f"Quantidade total de diretórios e subdiretórios válidos: {total_diretorios}")
    print("\nQuantidade global de arquivos mapeados por extensão:")
    
    # Exibe todas as extensões do projeto ordenadas pela quantidade
    for ext, qtd in extensoes_contador.most_common():
        print(f"  {ext}: {qtd} arquivo(s)")
    print("=" * 80)

if __name__ == "__main__":
    verificar_arquivos_nao_utf8()
