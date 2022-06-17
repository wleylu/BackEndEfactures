package com.efacture.dev.service;

 

import java.util.List;

//import com.efacture.dev.model.Compte;
import com.efacture.dev.model.CompteMarchand;


public interface ServiceCm {
    
	public CompteMarchand ajouterCm(CompteMarchand c);
    public List<CompteMarchand> listMarchands();
       public CompteMarchand getMarchand(String client);
    public CompteMarchand getByEmail(String email);
    public String getByMarchandByMail(String email);
    public List<CompteMarchand> rechercheByLogin(String login);
    public List<CompteMarchand> rechercheByNomAndLogin(String nom,String login);
    public CompteMarchand modifierCm(CompteMarchand cptMarchand);

}