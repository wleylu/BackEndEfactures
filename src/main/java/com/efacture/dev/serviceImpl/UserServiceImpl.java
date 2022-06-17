package com.efacture.dev.serviceImpl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.efacture.dev.model.UserAuditConnect;
import com.efacture.dev.model.UserAudite;
import com.efacture.dev.model.ErreurGenere;
import com.efacture.dev.model.PassInitialiseur;
import com.efacture.dev.model.Utilisateur;
import com.efacture.dev.repository.CmRepository;
import com.efacture.dev.repository.ErreurGenereRepository;
import com.efacture.dev.repository.UserRepository;
import com.efacture.dev.service.CryptageDatas;
import com.efacture.dev.service.UserService;



//import com.efact.domaine.UserEntity;
//import com.efact.repository.UserRepository;
//import com.efact.service.UserService;
@Service
@Transactional
@SuppressWarnings("unused")
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;	
	
	@Autowired
	private UserAuditeImpl aut;
	@Autowired
	private ErreurGenereImpl erreurGenereImpl;
	
	
	 private PasswordEncoder bcryptEncoder;
	 
	 
	

	public UserServiceImpl() {
		bcryptEncoder = new BCryptPasswordEncoder();
	}

	
	@Override
	public Utilisateur utilisateurConnecte(String login){
		//AuditUserConnexionImpl audit = new AuditUserConnexionImpl();
		Utilisateur user = new Utilisateur();
		UserAuditConnect audit = new UserAuditConnect();
		//user = userRepository.findByLogin(login);
		try {
			user = userRepository.findByLogin(login);
			if (user != null) {
//				UserAudite at= aut.ajoutAuditUser(new UserAudite(null, user.getLogin(),
//						user.getHabilitation(),"Connecté",login, login, new Date()));
			}
			
			
			
		} catch (Exception e) {
			ErreurGenere errg= erreurGenereImpl.enregistrerErreurGenerer(new ErreurGenere(new Date(),
					01, login, e.getMessage()));
			
				//erreurRespository.save(new ErreurGenere(0, new Date(), 01, login, e.getMessage()));
				//user= new UserEntity();
			
			//return errg;
		}	
		
		return user;
	}

	//methode d'authentification
	
	
	@Override
	public Utilisateur authentification(String login, String password,String userip,String userHost) {
		Utilisateur user = new Utilisateur();
		//UserAuditConnect audit = new UserAuditConnect();
		try {
			user = userRepository.findByLoginAndPassword(login,password);
			if (user != null) {
				UserAudite at= aut.ajoutAuditUser(new UserAudite(null, user.getLogin(),
						user.getNom(),user.getPrenom(),user.getHabilitation(),"CONNECTE",userip,userHost));
			}
		} catch (Exception e) {
			ErreurGenere errg= erreurGenereImpl.enregistrerErreurGenerer(new ErreurGenere(new Date(),
					01, "UserServiceImpl.authentification",login, e.getMessage(), "Erreur d'authentification"));
		}
		return user;
	}

	//enregistre les utilisateurs dans la base
	
	@Override
	public Utilisateur enregistrerUserEntity(Utilisateur userEntity) {	
			String passdefaut = null;
			CryptageDatas crypt= new CryptageDatas();
			String decryptePass = null;
				
			try {
				passdefaut = generatePassword();
				decryptePass = crypt.decryptageData(passdefaut);
			} catch (Exception e) {
				passdefaut = null;
			}
			
		
		if(userRepository.findByLogin(userEntity.getLogin()) == null && 
				userRepository.findByEmail(userEntity.getEmail())==null &&
				userRepository.findByTel(userEntity.getTel())==null) {

			userEntity.setPassword(passdefaut);
			userEntity.setReinitialiser(false);
			Utilisateur utilisteurSave = userRepository.save(userEntity);			
			return utilisteurSave;
		}
	   return new Utilisateur();
	}
	
	
	private String generatePassword() throws Exception {
		CryptageDatas cryptageDatas = new CryptageDatas();
		String pass = cryptageDatas.cryptageData("code"+RandomStringUtils.randomNumeric(10)+"spas");
        return pass;
    }
	
	//list des utilisateurs
	
	@Override
	public List<Utilisateur> listeUserEntity() {
		return userRepository.findAll();
	}
	
	// liste des profil marchand
	
	@Override
	public List<Utilisateur> listeUserProfilMarchand(String login){
		Utilisateur usserConnect = userRepository.findByLogin(login);
		List<Utilisateur> userProfilMarchand = new ArrayList<Utilisateur>();		
		
		try {
			if (usserConnect.getHabilitation().equals("ROLE_SUPERVISEUR")) {
					
				userProfilMarchand = userRepository.findByUserProfilMarchand(login);
			}
			else if (usserConnect.getHabilitation().equals("ROLE_HELPDESK")) {
				
				userProfilMarchand = userRepository.listeValidClient(usserConnect.getAgence().getId(),login);
			}
			else {
				userProfilMarchand = new ArrayList<Utilisateur>();
			}
			
		} catch (Exception e) {
			userProfilMarchand = new ArrayList<Utilisateur>();
			return userProfilMarchand;
		}
		
				
		return userProfilMarchand;
	}
	
	// liste des back office	
	@Override
	public List<Utilisateur> listeUserProfilBackOffice(){
		try {
			List<Utilisateur> UserProfilBackOffice= userRepository.findByUserProfilBackOffice();
			if (UserProfilBackOffice != null) {
				return UserProfilBackOffice;
			}else {
				
				return new ArrayList<>();
			}
		} catch (Exception e) {
		
			return new ArrayList<>();
		}
	}
	
// modification utilisateur
	
	@Override
	public Utilisateur modifierUserEntity(Utilisateur userEntity) {
		userEntity.setDateMaj(new Date(System.currentTimeMillis()));
		Utilisateur user = userRepository.findByLogin(userEntity.getLogin());
		
		try {
			if (user != null) {
				user.setReinitialiser(user.getReinitialiser()==null ? false:user.getReinitialiser());				
				if (user.getReinitialiser()==true) {
					user.setPassword(generatePassword());
					user.setPassword1(null);
					user.setReinitialiser(false);				
					user.setBloquser(0);
					user.setInit(true);
					user.setLoginMaj(userEntity.getLogin());				
					return userRepository.save(user);
				}else {
				
					
					return userRepository.save(userEntity);
				}	 
			}else {
				return new Utilisateur();
			}
		} catch (Exception e) {
			// TODO: handle exception
			return new Utilisateur();
		}
		
	}

// deconnecion et transabilité audit
	
	@Override
	public void deconnexion(String login,String userIp,String userHost) {
		// TODO Auto-generated method stub
		Utilisateur user = new Utilisateur();
		UserAuditConnect audit = new UserAuditConnect();
		user = userRepository.findByLogin(login);
		if (user != null) {
			UserAudite at= aut.ajoutAuditUser(new UserAudite(null, user.getLogin(),
					user.getNom(),user.getPrenom(),user.getHabilitation(),"DECONNECTE",userIp,userHost));
		}
	}

// liste des utlisateurs par nom
	
	@Override
	public List<Utilisateur> rechercheByNom(String nom) {
		// TODO Auto-generated method stub
		return userRepository.findByNomContaining(nom);
	}

	@Override
	public List<Utilisateur> rechercheByLogin(String login) {
		// TODO Auto-generated method stub
		return userRepository.findByLoginContaining(login);
	}

	@Override
	public Utilisateur getUser(String user) {
		// TODO Auto-generated method stub
		return userRepository.findByLogin(user);
	}

	@Override
	public List<Utilisateur> rechercheByNomAndLogin(String nom, String login) {
		try {
			return userRepository.findByNomContainingIgnoreCaseAndLoginContainingIgnoreCase(nom, login);
		} catch (Exception e) {
			return new ArrayList<>();
		}
		
	}


	public Utilisateur firstAuthentification(Utilisateur userEntity) {
		// TODO Auto-generated method stub
			
			Utilisateur user = new Utilisateur();	
			Utilisateur users = new Utilisateur();
			
			user = userRepository.findByLoginAndPassword(userEntity.getLogin(),userEntity.getPassword());	
			
			
			
			if (user != null) {		
					if(user.getPassword1()==(userEntity.getPassword())) {						
						users= new Utilisateur();
					}else {	
						
						user.setDateMdp(new Date(System.currentTimeMillis()));
						user.setPassword1(bcryptEncoder.encode(userEntity.getPassword1()));						
						users = userRepository.save(user);						
				}				
		   }			
			return users;
				
	}
	
	
	public Utilisateur firstAuthentification1(Utilisateur userEntity) {
		// TODO Auto-generated method stub
			PassInitialiseur monPasse = new PassInitialiseur();
			CryptageDatas cypteData = new CryptageDatas();
			
			Utilisateur user = new Utilisateur();	
			Utilisateur users = new Utilisateur();
			
			user = userRepository.findByLoginAndPassword(userEntity.getLogin(),userEntity.getPassword());	
			monPasse.setPass1(userEntity.getPassword1());
			monPasse.setPass2(user.getPassword2());					
			monPasse.setPass3(user.getPassword3());
			
			boolean valiPass= monPasse.validedPass();
			
			System.out.println("Validité du mot de passe : "+valiPass);
			
			if (user != null) {		
					if(user.getPassword1()==(userEntity.getPassword())) {								
						users= new Utilisateur();
					}else {	
						String pas1=null;
						String pas2 = null;
						String pas3 = null;
						try {
						/*	pas1=cypteData.cryptageData(monPasse.getPass1());
							pas2=cypteData.cryptageData(monPasse.getPass2());
						*/	
							pas1=monPasse.getPass1();					
							pas2=monPasse.getPass2();
							
							System.out.println("Passe 1: "+pas1);
							System.out.println("Passe 2: "+pas2);
						} catch (Exception e) {
							// TODO: handle exception
						}
						user.setDateMdp(new Date(System.currentTimeMillis()));
						user.setPassword1(bcryptEncoder.encode(userEntity.getPassword1()));
						user.setPassword2(pas1);
						user.setPassword3(pas2);
						users = userRepository.save(user);						
				}				
		   }			
			return users;
				
	}
	
	
	public Utilisateur modificationMotPasse(Utilisateur userEntity) {
		// TODO Auto-generated method stub
			Utilisateur user = new Utilisateur();
			//user = userRepository.findByLoginAndPassword(userEntity.getLogin(),userEntity.getPassword());
			user = userRepository.findByLogin(userEntity.getLogin());
			//System.out.println(user);
			if (user != null) {
				
						
				if(userEntity.getPassword1().equals(user.getPassword())) {
					return new Utilisateur();
				}else {					//user.setPassword(userEntity.getPassword());
					
					user.setPassword1(bcryptEncoder.encode(userEntity.getPassword1()));
					user.setMdpOublie(userEntity.getPassword1());
					Utilisateur users = userRepository.save(user);
				return users;
			}
		}
				return new Utilisateur();
	}
	@Override
	public Utilisateur connexion(String login, String password1,String userIp,String userHost) {
		Utilisateur user = new Utilisateur();
		//UserAuditConnect audit = new UserAuditConnect();
		try {
			user = userRepository.findByLoginAndPassword1(login,password1);
			if (user != null) {
				UserAudite at= aut.ajoutAuditUser(new UserAudite(null, user.getLogin(),
						user.getNom(),user.getPrenom(),user.getHabilitation(),"Connecté",userIp,userHost));
			}
		} catch (Exception e) {
			ErreurGenere errg= erreurGenereImpl.enregistrerErreurGenerer(new ErreurGenere(new Date(),
					01, "UserServiceImpl.authentification",login, e.getMessage(), "Erreur d'authentification"));
		}
		return user;
	}
	public String generateForgotPwd() {
		return "{\"code\":\"C"+ RandomStringUtils.randomAlphanumeric(6) + "abi\"}";
	}
	@Override
	public Utilisateur modifierMotDePasseEmail(String email) {
// TODO Auto-generated method stub
		Utilisateur user = userRepository.findByEmail(email);
		if (user != null) {
			//user.setMdpOublie(generatePassword());
			return userRepository.save(user);
		}else {
			return new Utilisateur();
		}
		
	}
	@Override
	public Utilisateur rechercheByEmail(String email) {
// TODO Auto-generated method stub
		try {
			return userRepository.findByEmail(email);
		} catch (Exception e) {
			
			return new Utilisateur();
		}
		
	}
	public Utilisateur rechercheByTel(String tel) {
		// TODO Auto-generated method stub
				try {
					return userRepository.findByTel(tel);
				} catch (Exception e) {
					
					return new Utilisateur();
				}
				
			}


	@Override
	public String getByUserByMail(String email) {
		try {
			return userRepository.getByUserByMail(email);
		} catch (Exception e) {
			return null;
		}
		
	}

	

	
	
	
}
