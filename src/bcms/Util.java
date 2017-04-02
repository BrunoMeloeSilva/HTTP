package bcms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class Util {
	// Ato 0: Definição do cache em um espoco não destruivel até a app ser fechada.
	public static String[][] mCacheValues = null;
	public static LinkedHashMap<String, String[][]> mCache = new LinkedHashMap<String, String[][]>(16, 0.75f, true);
	/**
	 * Retorna a mensagem criptografada conforme o tipo do algoritmo
	 * selecionado. A mensagem de retorno que é a string criptograda com os
	 * algoritmos unidirecionais listados abaixo, são chamadas em estrutura de
	 * dados de "Hash Key", comumente usado como chave de validação para
	 * comunicação entre dispositivos.
	 * 
	 * @author brunomeloesilva
	 * 
	 * @param algoritmo
	 *            O tipo do algoritmo que será usado, ex.: MD5, MD4, SHA-1,
	 *            SHA-224, SHA-256, SHA-384, SHA-512, RIPEMD128, RIPEMD160,
	 *            RIPEMD256, RIPEMD320, Tiger, DHA256, FORK256.
	 * @param mensagem
	 *            A mensagem que será criptografada.
	 * @return A mensagem criptografada.
	 * @throws NoSuchAlgorithmException
	 *             Se o tipo do algoritmo informado não está disponivel.
	 * @throws UnsupportedEncodingException
	 *             Se a codificação para caracteres UTF-8 não estiver
	 *             disponivel.
	 * @see Fontes: https://pt.wikipedia.org/wiki/MD5 ,
	 *      http://www.devmedia.com.br/como-funciona-a-criptografia-hash-em-java
	 *      /31139
	 */
	public static final String getHashKey(String algoritmo, String mensagem) {
		StringBuilder hexString = null;
		try {
			// Obtém uma instancia do algoritmo de codificação informada (do
			// tipo MD5,SSH1, etc..)
			MessageDigest tipoAlgoritmo = MessageDigest.getInstance(algoritmo);
			// Obtém o valor Hash (ex.: o valor criptografado conforme algoritmo
			// setado acima)
			byte[] messageDigest = tipoAlgoritmo.digest(mensagem.getBytes("UTF-8"));
			// Transformando o vetor de bytes (messageDigest) em um vetor de
			// hexadecimais (hexadecimal) representado numa string
			hexString = new StringBuilder();
			for (byte mbyte : messageDigest) {
				// Converte cada byte em hexadecimal com 2 casas completadas com
				// zeros a esquerda
				hexString.append(String.format("%02x", mbyte));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return hexString.toString();
	}

	/**
	 * Faz uma requisição no servidor web em um endereço informado que deverá
	 * retornar uma String JSON.
	 *
	 * @param url
	 *            Endereço URL HTTP da requisição para obter um JSON.
	 * @return JSON requisitado.
	 */
	public static final String getJSON(String url) {
		HttpURLConnection httpURLConnection = null;
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = null;
		String aux;
		try {
			// Informamos a descrição da Localizacão Uniforme do Recurso na web
			// para obtermos uma instância da URL.
			URL mURL = new URL(url);

			System.out.println("** Analise dos métodos URL **");
			System.out.println("getAuthority: \t" + mURL.getAuthority());
			System.out.println("getProtocol: \t" + mURL.getProtocol());
			System.out.println("getFile: \t" + mURL.getFile());
			System.out.println("getHost: \t" + mURL.getHost());
			System.out.println("getPort: \t" + mURL.getPort());
			System.out.println("toString: \t" + mURL.toString());
			System.out.println("toExternalForm: " + mURL.toExternalForm());
			System.out.println("getDefaultPort: " + mURL.getDefaultPort());
			System.out.println("getPath: \t" + mURL.getPath());
			System.out.println("getQuery: \t" + mURL.getQuery());
			System.out.println("getRef: \t" + mURL.getRef());
			System.out.println("getUserInfo: \t" + mURL.getUserInfo());
			System.out.println("hashCode: \t" + mURL.hashCode());

			// Retorna a INSTANCIA QUE REPRESENTA a conexao com servidor,
			// porém aqui ainda NÃO HÁ CONEXÃO REAL com o servidor, é
			// a instância apenas para definir os parametros da comunicação.
			httpURLConnection = (HttpURLConnection) mURL.openConnection();
			// Definindo parametros para comunicação com o servidor
			// httpURLConnection.setRequestMethod("GET"); //Por padrão já é GET,
			// não necessitando explicitar.
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Accept-Encoding", "gzip");
			// Este método setRequestProperty é um Map onde você altera os
			// valores das chaves de recurso,
			// ele pode ser chamado diversas vezes para setar/alterar os
			// diversos recursos.
			// Portanto você deve conhecer os nomes dos recursos (Keys) e os
			// valores permitidos a ser setado (Values).

			// Caso esta requisicao web ja tenha sido solicitada anteriormente,
			// informa a eTag na requisição web, para o conteudo retornar vazio,
			// caso nao tenha sido alterado no servidor.
			// Ato 2 do cache.
			String path = mURL.getFile().substring(0, mURL.getFile().indexOf("&apikey"));
			if (mCache.containsKey(path)) {
				httpURLConnection.setRequestProperty("If-None-Match", mCache.get(path)[0][0]);
			}
			System.out.println("** Analise dos métodos  HttpURLConnection **");
			// Só é possível setar e consultar as propriedades de requisição
			// ANTES de ocorrer a comunicação com o servidor
			System.out.println("getRequestProperties :");
			printMap(httpURLConnection.getRequestProperties());

			// É o código de resposta a solicitação de conexão ao servidor
			// (200-Autorizado, 401-Não autorizado, 404-Não encontrado...etc),
			// aqui há conexão com o servidor para verificar disponibilidade, portanto deve
			// vir depois das configuracoes de comunicacao setRequestProperty.
			System.out.println("getResponseCode : \t" + httpURLConnection.getResponseCode());
			System.out.println("getConnectTimeout : \t" + httpURLConnection.getConnectTimeout());
			System.out.println("getPermission : \t" + httpURLConnection.getPermission());
			System.out.println("getRequestMethod : \t" + httpURLConnection.getRequestMethod());
			System.out.println("getURL : \t" + httpURLConnection.getURL());
			System.out.println("getHeaderFields : ");
			printMap(httpURLConnection.getHeaderFields());
			// Caso deseje retornar somente um item especifico do Map acima,
			// existem metodos como getContentType()
			// , que retorna o conteúdo Content-Type, e outros. Para que você
			// não precise ter o trabalho de extrair do getHeaderFields().

			switch (httpURLConnection.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				System.out.println("** HTTP_OK **");
				// Abre a comunicação/conversação com o servidor se a conexão
				// estiver sido estabelecida.
				httpURLConnection.connect();
				// Obtem um stream para leitura do conteudo no servidor
				if (httpURLConnection.getContentEncoding().equals("gzip")) {
					/*
					 * 1. O inputStream traz os bytes compactados, devido a
					 * solicitação Accept-Encoding = gzip 2. O GZIPInputStream
					 * descompacta esses bytes recebidos 3. O InputStreamReader
					 * ler esses bytes já descompactados e os converte para
					 * caracteres no formato UTF-8 4. O BufferedReader é
					 * alimentado com os caracteres
					 */
					bufferedReader = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(httpURLConnection.getInputStream()),
									httpURLConnection.getRequestProperty("Accept-Charset")));
				} else {
					bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),
							httpURLConnection.getRequestProperty("Accept-Charset")));
				}
				// ler o conteudo atraves do stream obtido
				stringBuilder = new StringBuilder();
				while ((aux = bufferedReader.readLine()) != null) {
					stringBuilder.append(aux);
				}
				// mostrando conteudo lido
				System.out.println("Conteúdo da mensagem de resposta via streamWeb: ");
				System.out.println(stringBuilder.toString());
				// Ato 1 do cache: Inserir novo item no cache de requisições
				mCacheValues = new String[1][2];
				mCacheValues[0][0] = httpURLConnection.getHeaderField("eTag");
				mCacheValues[0][1] = stringBuilder.toString();
				mCache.put(path, mCacheValues);
				
				break;
			case HttpURLConnection.HTTP_NOT_MODIFIED:
				System.out.println("** HTTP_NOT_MODIFIED **");
				aux = mCache.get(path)[0][1];
				// mostrando conteudo lido
				System.out.println("Conteúdo da mensagem de resposta via cache: ");
				System.out.println(aux);
				break;

			default:
				System.out.println(
						"> > > > > Código de status de retorno não tratado: " + httpURLConnection.getResponseCode());
				break;
			}

		} catch (MalformedURLException e) {
			// Por motivo do new Url(..)
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.out.println("Você está sem acesso a internet ou realmente o host informado não pode ser determinado.");
		} catch (IOException e) {
			// Por motivo do mURL.openConnection()
			e.printStackTrace();
		} finally {
			// Liberação de recursos
			System.out.println("\nConexão encerrada e recursos liberados.");
			httpURLConnection.disconnect();
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				// Devido não instanciação do bufferedReader
				e.printStackTrace();
			}
			stringBuilder = null;
			aux = null;
		}

		return null;
	}

	/**
	 * Imprime todos os pares chave-valor de um Map.
	 * 
	 * @param map
	 *            O Map que será impresso.
	 */
	public static final void printMap(Map map) {
		Set chaves = map.keySet();
		for (Object o : chaves) {
			System.out.println("\t" + o + " : " + map.get(o));
		}
	}
}

/* Considerações sobre cache:
 * 1. Se é pra guardar em cache o resultado da requisicao, quarde só a parte da responda que lhe interessa.
 * 		.: Aqui guardei todo o retorno da resposta, pois é um teste e tudo me interessa.
 * 2. Obviamente um cache não deve crescer indefinidamente, quando o cache chegar em um determinado tamanho máximo ideal,
 *  só os itens mais uteis/acessados, devem ser mantidos.
 *  	.: Aqui meu cache é incontrolavel, sem definição de limite de tamanho.
 */