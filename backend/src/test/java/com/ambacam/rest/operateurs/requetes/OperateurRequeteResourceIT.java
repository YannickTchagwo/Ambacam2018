package com.ambacam.rest.operateurs.requetes;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambacam.ItBase;
import com.ambacam.configuration.AppSettings;
import com.ambacam.model.Operateur;
import com.ambacam.model.Pays;
import com.ambacam.model.Requerant;
import com.ambacam.model.Requete;
import com.ambacam.model.RequeteGroupe;
import com.ambacam.model.StatusRequete;
import com.ambacam.model.TypeRequete;
import com.ambacam.repository.OperateurRepository;
import com.ambacam.repository.PaysRepository;
import com.ambacam.repository.RequerantRepository;
import com.ambacam.repository.RequeteGroupeRepository;
import com.ambacam.repository.RequeteRepository;
import com.ambacam.repository.StatusRequeteRepository;
import com.ambacam.repository.TypeRequeteRepository;
import com.ambacam.rest.ApiConstants;
import com.ambacam.service.RequeteService;

public class OperateurRequeteResourceIT extends ItBase {

	@Autowired
	private TypeRequeteRepository typeRequeteRepository;

	@Autowired
	private StatusRequeteRepository statusRequeteRepository;

	@Autowired
	private RequeteGroupeRepository requeteGroupeRepository;

	@Autowired
	private PaysRepository paysRepository;

	@Autowired
	private OperateurRepository operateurRepository;

	@Autowired
	private RequerantRepository requerantRepository;

	@Autowired
	private RequeteRepository requeteRepository;

	@Autowired
	private RequeteService requeteService;

	@Mock
	private AppSettings appSettings;

	private TypeRequete typeRequete1;
	private TypeRequete typeRequete2;
	private TypeRequete typeRequete3;

	private StatusRequete statusRequete;

	private RequeteGroupe requeteGroupe;

	private Pays pays;

	private Operateur operateur1;
	private Operateur operateur2;

	private Requerant requerant1;
	private Requerant requerant2;

	private Requete requete1;
	private Requete requete2;
	private Requete requete3;
	private Requete requete4;

	@Override
	@Before
	public void setup() throws Exception {
		super.setup();

		typeRequete1 = typeRequeteRepository.save(buildTypeRequete());
		typeRequete2 = typeRequeteRepository.save(buildTypeRequete());
		typeRequete3 = typeRequeteRepository.save(buildTypeRequete());

		statusRequete = statusRequeteRepository.save(buildStatusRequete().nom("status1"));

		pays = paysRepository.save(buildPays());

		operateur1 = operateurRepository.save(buildOperateur().nom("operateur1").nationalite(pays));
		operateur2 = operateurRepository.save(buildOperateur().nom("operateur2").nationalite(pays));

		requeteGroupe = buildRequeteGroupe();
		requeteGroupe.setCreePar(operateur1);
		requeteGroupe = requeteGroupeRepository.save(requeteGroupe);

		requerant1 = requerantRepository.save(buildRequerant().nom("requerant1").creePar(operateur1).nationalite(pays));
		requerant2 = requerantRepository.save(buildRequerant().nom("requerant2").creePar(operateur2).nationalite(pays));

		// create requetes
		requete1 = buildRequete();
		requete1.setRequeteGroupe(requeteGroupe);
		requete1.setRequerant(requerant1);
		requete1.setType(typeRequete1);
		requete1.setStatus(statusRequete);
		requete1.setOperateur(operateur1);
		requete1 = requeteRepository.save(requete1);

		Thread.sleep(1000);

		requete2 = buildRequete();
		requete2.setRequeteGroupe(requeteGroupe);
		requete2.setRequerant(requerant2);
		requete2.setType(typeRequete2);
		requete2.setStatus(statusRequete);
		requete2.setOperateur(operateur1);
		requete2 = requeteRepository.save(requete2);

		Thread.sleep(1000);

		requete3 = buildRequete();
		requete3.setRequeteGroupe(requeteGroupe);
		requete3.setRequerant(requerant1);
		requete3.setType(typeRequete3);
		requete3.setStatus(statusRequete);
		requete3.setOperateur(operateur2);
		requete3 = requeteRepository.save(requete3);

		Thread.sleep(1000);

		requete4 = buildRequete();
		requete4.setRequeteGroupe(requeteGroupe);
		requete4.setRequerant(requerant2);
		requete4.setType(typeRequete1);
		requete4.setStatus(statusRequete);
		requete4.setOperateur(operateur2);
		requete4 = requeteRepository.save(requete4);

		simulateNominalBehaviors();

	}

	@Override
	@After
	public void cleanup() throws Exception {

		requeteRepository.deleteAll();
		requeteGroupeRepository.deleteAll();
		requerantRepository.deleteAll();
		statusRequeteRepository.deleteAll();
		typeRequeteRepository.deleteAll();
		operateurRepository.deleteAll();
		paysRepository.deleteAll();

		super.cleanup();
	}

	@Test
	public void listByOperateurFirstPageDefaultLimit() {
		preLoadedGiven.queryParam("page", 0).get(ApiConstants.OPERATEUR_REQUETE_COLLECTION, operateur1.getId()).then()
				.log().body().statusCode(200).body("size()", is(equalTo(1)))
				.body("id", containsInAnyOrder(requete1.getId().intValue()))
				.body("find{it.id==" + requete1.getId().intValue() + "}.type.id",
						is(equalTo(typeRequete1.getId().intValue())))
				.body("find{it.id==" + requete1.getId().intValue() + "}.status.id",
						is(equalTo(statusRequete.getId().intValue())))
				.body("find{it.id==" + requete1.getId().intValue() + "}.date",
						is(equalTo(requete1.getCreeLe().getTime())))
				.body("find{it.id==" + requete1.getId().intValue() + "}.requerant.id",
						is(equalTo(requerant1.getId().intValue())))
				.body("find{it.id==" + requete1.getId().intValue() + "}.operateur.id",
						is(equalTo(operateur1.getId().intValue())));
		verify(appSettings, times(1)).getSearchDefaultPageSize();
	}

	@Test
	public void listByOperateurLastPageDefaultLimit() {
		preLoadedGiven.queryParam("page", 1).get(ApiConstants.OPERATEUR_REQUETE_COLLECTION, operateur1.getId()).then()
				.log().body().statusCode(200).body("size()", is(equalTo(1)))
				.body("id", containsInAnyOrder(requete2.getId().intValue()));
		verify(appSettings, times(1)).getSearchDefaultPageSize();
	}

	@Test
	public void listByOperateur() {
		preLoadedGiven.queryParam("limit", 2).get(ApiConstants.OPERATEUR_REQUETE_COLLECTION, operateur1.getId()).then()
				.log().body().statusCode(200).body("size()", is(equalTo(2)))
				.body("id", containsInAnyOrder(requete1.getId().intValue(), requete2.getId().intValue()))
				.body("find{it.id==" + requete1.getId().intValue() + "}.type.id",
						is(equalTo(typeRequete1.getId().intValue())))
				.body("find{it.id==" + requete1.getId().intValue() + "}.status.id",
						is(equalTo(statusRequete.getId().intValue())))
				.body("find{it.id==" + requete1.getId().intValue() + "}.date",
						is(equalTo(requete1.getCreeLe().getTime())))
				.body("find{it.id==" + requete1.getId().intValue() + "}.requerant.id",
						is(equalTo(requerant1.getId().intValue())))
				.body("find{it.id==" + requete1.getId().intValue() + "}.operateur.id",
						is(equalTo(operateur1.getId().intValue())));
		verify(appSettings, times(1)).getSearchDefaultPageSize();
	}

	private void simulateNominalBehaviors() {

		// Simulate the page size to simplify the tests
		when(appSettings.getSearchDefaultPageSize()).thenReturn(1);
		requeteService.setAppSettings(appSettings);
	}

}
