package com.efacture.dev.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.efacture.dev.model.UserAudite;
import com.efacture.dev.repository.UserAuditeRespository;
import com.efacture.dev.service.UserAuditService;

@Service
public class UserAuditeImpl implements UserAuditService {
	@Autowired
	private UserAuditeRespository auditeRespository;
	
	public UserAuditeImpl() {
		
	}
	
	public UserAudite ajoutAuditUser(UserAudite user){
		return auditeRespository.save(user);
	}

	@Override
	public List<UserAudite> listAudit() {
		return auditeRespository.findAll();
	}

	@Override
	public List<UserAudite> rechercheByLogin(String login) {
		// TODO Auto-generated method stub
		return auditeRespository.findByLoginContaining(login);
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public List<UserAudite> rechercheByDateBetween(Date firstDate, Date lastDate) {
		// TODO Auto-generated method stub
		ArrayList audit = new ArrayList<>();
		if (firstDate !=null && lastDate != null) {
			return auditeRespository.findByDateBetween(firstDate, lastDate);
		} else {
			return new ArrayList<>();
		}
		
	}

	

	@Override
	public List<UserAudite> rechercheAdit(Date firstDate,Date lastDate,String login,String nom,String role,String statut) {
		return auditeRespository.findByDateBetweenAndLoginContainingIgnoreCaseAndNomContainingIgnoreCaseAndRoleContainingIgnoreCaseAndStatutContainingIgnoreCase(firstDate, lastDate, login, nom, role, statut);
	}

	@Override
	public List<UserAudite> rechercheByDate(Date firstDate) {
		// TODO Auto-generated method stub
		return auditeRespository.findByDateContaining(firstDate);
	}

	@Override
	public List<UserAudite> rechercheAudit(String login, String nom, String role, String statut) {
		try {
			return auditeRespository.findByLoginContainingIgnoreCaseAndNomContainingIgnoreCaseAndRoleContainingIgnoreCaseAndStatutContainingIgnoreCase(login, nom, role, statut);
		} catch (Exception e) {
			return new ArrayList<>();
		}
		
	}

}
