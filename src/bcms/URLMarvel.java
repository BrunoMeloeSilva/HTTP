package bcms;

public final class URLMarvel {
	//URL = protocolo://domínio:porta/caminho/recurso?query_string#fragmento
	private static final String protocolo = "http://";
	private static final String dominio = "gateway.marvel.com";
	private static final String porta = ":80";
	private static final String caminho = "/v1/public/";
	// Toda enumeração é estatica por naturexa
	// A convencao dos bons costumes de programacao, diz que os itens da enumeracao devem ser maiusculas.
	public enum Recurso { characters, comics, creators, events, series, stories }
    //String fragmento = "#fragmento";
	
	//O tipo de algoritmo de criptografia a ser usado nas requisições.
	private static final String algoritmo = "MD5";
	private static final String chavePublica = "abf83400de30676982d73c6d45f91699";
	//Este ato de escrever a chave privada no código é completamente inseguro !
	private static final String chavePrivada = "b20e53dd0841a1688fb914a289a035c21723811d";
    
	/**
	 * Constrói uma URL válida para determinado recurso na API Marvel e a retorna.
	 * 
	 * @author brunosilva
	 * 
	 * @return
	 * 		Uma URL válida para o recurso desejado na API Marvel.
	 */
    public static final String getURLMontada(Recurso recurso, int offset, int limit) {
    	//limit deve ser > 1 e offset > 0, caso contratio erro.
    	//offset é o indice e começa em 0 e limit é a quantidade a ser retornada apartir do indice.
    	String query = "?limit="+limit+"&offset="+offset+"&";
    	
    	String UrlBase = protocolo + dominio + porta + caminho + recurso + query;
        //hora atual em milesimos de segundos
        Long hora = System.currentTimeMillis();
        String urlMontada = UrlBase
                +"apikey=" + chavePublica
                +"&ts=" + hora.toString()
                +"&hash=" + Util.getHashKey(algoritmo, hora + chavePrivada + chavePublica);

        return urlMontada;
    }    
}