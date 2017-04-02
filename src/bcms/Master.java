package bcms;

public class Master {
	/**
	 * Aqui irei realizar todos os testes e observações do estudo de meu código.
	 */
	public static void main(String[] args) {
		
		Util.getJSON(URLMarvel.getURLMontada(URLMarvel.Recurso.characters, 0, 1));
		Util.getJSON(URLMarvel.getURLMontada(URLMarvel.Recurso.characters, 1, 1));
		
		Util.getJSON(URLMarvel.getURLMontada(URLMarvel.Recurso.characters, 0, 1));
		Util.getJSON(URLMarvel.getURLMontada(URLMarvel.Recurso.characters, 1, 1));
	}
}
