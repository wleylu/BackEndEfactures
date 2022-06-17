package com.efacture.dev.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassInitialiseur {
	private String pass1;
	private String pass2;
	private String pass3;
	
	public boolean validedPass(){
		String pass1= this.pass1;
		String pass2= this.pass2;
		String pass3= this.pass3;
		
		System.out.println(" passe 1 : "+this.pass1);
		System.out.println(" passe 2 : "+this.pass2);
		System.out.println(" passe 3 : "+this.pass3);
		if (pass1.equals(pass2) && pass2.equals(pass3)){
			return false;
		}
		return true;
		
	}

}
