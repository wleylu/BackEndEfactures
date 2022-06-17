package com.efacture.dev.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PayFactureGS2E {
	private String CodeTraitement="1";
	private String MessageTraitement;
	private String ReferenceContratClient;
	private String ReferenceDeTransaction;
	private String TypeTansaction;
	private String MontantReglement;
	private String NumeroRecu;
	private String NumeroDeFacture;
	
}
