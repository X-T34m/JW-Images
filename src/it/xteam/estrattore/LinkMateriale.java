package it.xteam.estrattore;

public class LinkMateriale {

	private String tipo = "";
	private String url = "";

	public LinkMateriale(String tipo, String url) {
		this.tipo = tipo;
		this.url = url;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
