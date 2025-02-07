package com.ambacam.rest.operateurs.requetegroupes;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambacam.ItBase;
import com.ambacam.model.Operateur;
import com.ambacam.model.Pays;
import com.ambacam.model.Requerant;
import com.ambacam.model.Requete;
import com.ambacam.model.RequeteGroupe;
import com.ambacam.model.StatusHistory;
import com.ambacam.model.StatusRequete;
import com.ambacam.model.StatusRequeteValues;
import com.ambacam.model.TypeRequete;
import com.ambacam.repository.OperateurRepository;
import com.ambacam.repository.PaysRepository;
import com.ambacam.repository.RequerantRepository;
import com.ambacam.repository.RequeteGroupeRepository;
import com.ambacam.repository.RequeteRepository;
import com.ambacam.repository.StatusHistoryRepository;
import com.ambacam.repository.StatusRequeteRepository;
import com.ambacam.repository.TypeRequeteRepository;
import com.ambacam.rest.ApiConstants;
import com.ambacam.transfert.AddRemoveTO;
import com.ambacam.transfert.IdTO;
import com.ambacam.transfert.requetegroupes.RequeteGroupeCreateTO;
import com.ambacam.transfert.requetegroupes.RequeteGroupeUpdateTO;
import com.ambacam.utils.DateUtils;

import io.restassured.http.ContentType;

public class RequeteGroupesResourceIT extends ItBase {

	@Autowired
	private OperateurRepository operateurRepository;

	@Autowired
	private RequeteGroupeRepository requeteGroupeRepository;

	@Autowired
	private RequerantRepository requerantRepository;

	@Autowired
	private StatusRequeteRepository statusRequeteRepository;

	@Autowired
	private TypeRequeteRepository typeRequeteRepository;

	@Autowired
	private RequeteRepository requeteRepository;

	@Autowired
	private PaysRepository paysRepository;

	@Autowired
	private StatusHistoryRepository statusHistoryRepository;

	private Operateur operateur;

	private TypeRequete typeRequete1;
	private TypeRequete typeRequete2;

	private StatusRequete statusRequete1;
	private StatusRequete statusRequete2;

	private Requerant requerant;

	private RequeteGroupe requeteGroupe1;

	private RequeteGroupe requeteGroupe2;

	private Requete requete1;

	private Requete requete2;

	private Pays pays;

	@Override
	@Before
	public void setup() throws Exception {
		super.setup();

		// create pays
		pays = paysRepository.save(buildPays());

		// create typeRequetes
		typeRequete1 = typeRequeteRepository.save(buildTypeRequete());
		typeRequete2 = typeRequeteRepository.save(buildTypeRequete());

		// create statusRequete
		statusRequete1 = statusRequeteRepository.save(buildStatusRequete());
		statusRequete2 = statusRequeteRepository.save(buildStatusRequete());

		// create operateur
		operateur = buildOperateur().nom("operateur");
		operateur.setNationalite(pays);
		operateur = operateurRepository.save(operateur);

		requeteGroupe1 = buildRequeteGroupe();
		requeteGroupe1.setCreePar(operateur);

		requeteGroupe2 = buildRequeteGroupe();
		requeteGroupe2.setCreePar(operateur);

		// create requerant
		requerant = requerantRepository.save(buildRequerant().creePar(operateur).nationalite(pays));

		requeteGroupeRepository.save(Arrays.asList(requeteGroupe1, requeteGroupe2));

		// create requetes
		requete1 = buildRequete().status(statusRequete1).type(typeRequete1).requerant(requerant).operateur(operateur)
				.requeteGroupe(requeteGroupe2);

		requete2 = buildRequete().status(statusRequete2).type(typeRequete2).requerant(requerant).operateur(operateur)
				.requeteGroupe(requeteGroupe1);

		requeteRepository.save(Arrays.asList(requete1, requete2));
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

	@SuppressWarnings("deprecation")
	@Test
	public void create() {

		RequeteGroupeCreateTO requeteGroupeCreateTO = buildRequeteGroupeCreateTO();

		DateTime before = new DateTime();
		Date date = new Date();

		int id = preLoadedGiven.contentType(ContentType.JSON).body(requeteGroupeCreateTO).log().body()
				.post(ApiConstants.OPERATEUR_REQUETE_GROUPE_COLLECTION, operateur.getId()).then().log().body()
				.statusCode(200).extract().body().path("id");

		DateTime after = new DateTime();

		// check that the requete groupe has been saved
		RequeteGroupe actual = requeteGroupeRepository.findOne(Integer.toUnsignedLong(id));
		assertThat(actual, is(notNullValue()));
		assertThat(actual.getNom()
				.contains(String.format("%04d-%02d-%02d-%02d-%02d-", DateUtils.getActualYear(date), date.getMonth(),
						date.getDay(), date.getHours(), date.getMinutes(), date.getSeconds(),
						UUID.randomUUID().toString())),
				is(equalTo(true)));
		// check that the name pattern matched
		Pattern pattern = Pattern
				.compile("^([0-9]{4}\\-[0-9]{2}\\-[0-9]{2}\\-[0-9]{2}\\-[0-9]{2}\\-[0-9]{2}\\-[0-9a-zA-Z\\-]+)$");
		Matcher matcher = pattern.matcher(actual.getNom());
		assertThat(matcher.find(), is(equalTo(true)));
		assertThat(actual.getDescription(), is(equalTo(requeteGroupeCreateTO.getDescription())));
		assertThat(actual.getStatus(), is(equalTo(StatusRequeteValues.DRAFT)));
		assertThat(before.isBefore(actual.getCreeLe().getTime()), is(equalTo(true)));
		assertThat(after.isAfter(actual.getCreeLe().getTime()), is(equalTo(true)));
	}

	@Test
	public void createOperateurNotFound() {

		RequeteGroupeCreateTO requeteGroupeCreateTO = buildRequeteGroupeCreateTO();

		preLoadedGiven.contentType(ContentType.JSON).body(requeteGroupeCreateTO).log().body()
				.post(ApiConstants.OPERATEUR_REQUETE_GROUPE_COLLECTION, random.nextLong()).then().log().body()
				.statusCode(404);
	}

	@Test
	public void list() {
		preLoadedGiven.get(ApiConstants.OPERATEUR_REQUETE_GROUPE_COLLECTION, operateur.getId()).then().log().body()
				.statusCode(200).body("size()", is(equalTo(2)))
				.body("id", containsInAnyOrder(requeteGroupe1.getId().intValue(), requeteGroupe2.getId().intValue()))
				.body("find{it.id==" + requeteGroupe1.getId().intValue() + "}.nom",
						is(equalTo(requeteGroupe1.getNom())))
				.body("find{it.id==" + requeteGroupe1.getId().intValue() + "}.description",
						is(equalTo(requeteGroupe1.getDescription())))
				.body("find{it.id==" + requeteGroupe1.getId().intValue() + "}.creePar.id",
						is(equalTo(requeteGroupe1.getCreePar().getId().intValue())))
				.body("find{it.id==" + requeteGroupe1.getId().intValue() + "}.creeLe",
						is(equalTo(requeteGroupe1.getCreeLe().getTime())))
				.body("find{it.id==" + requeteGroupe1.getId().intValue() + "}.status",
						is(equalTo(requete2.getStatus().getNom())));
	}

	@Test
	public void get() {
		preLoadedGiven.get(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM, operateur.getId(), requeteGroupe1.getId()).then()
				.log().body().statusCode(200).body("id", is(equalTo(requeteGroupe1.getId().intValue())))
				.body("nom", is(equalTo(requeteGroupe1.getNom())))
				.body("description", is(equalTo(requeteGroupe1.getDescription())))
				.body("creePar.id", is(equalTo(requeteGroupe1.getCreePar().getId().intValue())))
				.body("creeLe", is(equalTo(requeteGroupe1.getCreeLe().getTime())))
				.body("status", is(equalTo(requete2.getStatus().getNom())));
	}

	@Test
	public void getWithStatusDraft() {
		requeteRepository.save(requete2.requeteGroupe(requeteGroupe2));
		preLoadedGiven.get(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM, operateur.getId(), requeteGroupe1.getId()).then()
				.log().body().statusCode(200).body("id", is(equalTo(requeteGroupe1.getId().intValue())))
				.body("nom", is(equalTo(requeteGroupe1.getNom())))
				.body("description", is(equalTo(requeteGroupe1.getDescription())))
				.body("creePar.id", is(equalTo(requeteGroupe1.getCreePar().getId().intValue())))
				.body("creeLe", is(equalTo(requeteGroupe1.getCreeLe().getTime())))
				.body("status", is(equalTo(StatusRequeteValues.DRAFT)));
	}

	@Test
	public void getWithStatusMixed() {

		requeteRepository.save(requete1.requeteGroupe(requeteGroupe1));

		preLoadedGiven.get(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM, operateur.getId(), requeteGroupe1.getId()).then()
				.log().body().statusCode(200).body("id", is(equalTo(requeteGroupe1.getId().intValue())))
				.body("nom", is(equalTo(requeteGroupe1.getNom())))
				.body("description", is(equalTo(requeteGroupe1.getDescription())))
				.body("creePar.id", is(equalTo(requeteGroupe1.getCreePar().getId().intValue())))
				.body("creeLe", is(equalTo(requeteGroupe1.getCreeLe().getTime())))
				.body("status", is(equalTo(StatusRequeteValues.MIXED)));
	}

	@Test
	public void getNotFound() {
		preLoadedGiven.get(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM, operateur.getId(), random.nextLong()).then()
				.statusCode(404);
	}

	@Test
	public void delete() {

		preLoadedGiven.delete(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM, operateur.getId(), requeteGroupe1.getId())
				.then().statusCode(200);

		// check that the requete groupe has been deleted
		RequeteGroupe actual = requeteGroupeRepository.findOne(requeteGroupe1.getId());
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void deleteNotFound() {
		preLoadedGiven.delete(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM, operateur.getId(), random.nextLong()).then()
				.statusCode(404);
	}

	@Test
	public void update() {

		RequeteGroupeUpdateTO update = buildRequeteGroupeUpdateTO();

		RequeteGroupe requeteGroupe = requeteGroupeRepository.findOne(requeteGroupe1.getId());

		preLoadedGiven.contentType(ContentType.JSON).body(update)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM, operateur.getId().intValue(), requeteGroupe1.getId())
				.then().log().body().statusCode(200);

		// check that the requete groupe has been saved
		RequeteGroupe actual = requeteGroupeRepository.findOne(requeteGroupe1.getId());
		assertThat(actual, is(notNullValue()));
		assertThat(actual.getNom(), is(equalTo(requeteGroupe.getNom())));
		assertThat(actual.getDescription(), is(equalTo(update.getDescription())));
		assertThat(actual.getCreeLe(), is(equalTo(requeteGroupe.getCreeLe())));
		assertThat(actual.getCreePar().getId(), is(equalTo(requeteGroupe.getCreePar().getId())));
		assertThat(actual.getStatus(), is(equalTo(requeteGroupe.getStatus())));

	}

	@Test
	public void updateNotFound() {

		RequeteGroupeUpdateTO update = buildRequeteGroupeUpdateTO();

		preLoadedGiven.contentType(ContentType.JSON).body(update)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM, operateur.getId(), random.nextLong()).then().log()
				.body().statusCode(404);
	}

	@Test
	public void updateStatus() {

		IdTO<Long> update = new IdTO<Long>(statusRequete1.getId());

		requeteGroupe1 = requeteGroupeRepository.findOne(requeteGroupe1.getId());

		assertThat(requeteGroupe1.getStatus(), is(equalTo(statusRequete2.getNom())));

		preLoadedGiven.contentType(ContentType.JSON).body(update).put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_STATUS,
				operateur.getId().intValue(), requeteGroupe1.getId()).then().log().body().statusCode(200);

		// check that the requete groupe has been saved
		RequeteGroupe actual = requeteGroupeRepository.findOne(requeteGroupe1.getId());
		assertThat(actual, is(notNullValue()));
		assertThat(actual.getStatus(), is(equalTo(statusRequete1.getNom())));

		// not updated
		assertThat(actual.getNom(), is(equalTo(requeteGroupe1.getNom())));
		assertThat(actual.getDescription(), is(equalTo(requeteGroupe1.getDescription())));
		assertThat(actual.getCreeLe(), is(equalTo(requeteGroupe1.getCreeLe())));
		assertThat(actual.getCreePar().getId(), is(equalTo(requeteGroupe1.getCreePar().getId())));

		requete2 = requeteRepository.findOne(requete2.getId());
		List<StatusHistory> history = statusHistoryRepository
				.findByClassNameAndEntityIdOrderByCreeLeAsc(Requete.class.getName(), requete2.getId());
		assertThat(history.stream().map(h -> h.getName()).collect(Collectors.toSet()),
				containsInAnyOrder(statusRequete1.getNom()));

	}

	@Test
	public void updateStatusNotFound() {

		IdTO<Long> update = new IdTO<Long>(statusRequete1.getId());

		preLoadedGiven.contentType(ContentType.JSON).body(update)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_STATUS, operateur.getId(), random.nextLong()).then()
				.log().body().statusCode(404);
	}

	@Test
	public void updateStatusNotExist() {

		IdTO<Long> update = new IdTO<Long>(new Random().nextLong());

		preLoadedGiven.contentType(ContentType.JSON).body(update)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_STATUS, operateur.getId(), requeteGroupe1.getId())
				.then().log().body().statusCode(400);
	}

	@Test
	public void addRemove() {

		AddRemoveTO addRemoveTO = new AddRemoveTO();
		addRemoveTO.getAdd().add(requete1.getId());
		addRemoveTO.getRemove().add(requete2.getId());

		List<Requete> requetes = requeteRepository.findAllByRequeteGroupe(requeteGroupe1);
		// check requeteGroupe1
		assertThat(requetes.stream().map(Requete::getId).collect(Collectors.toList()),
				containsInAnyOrder(requete2.getId()));

		preLoadedGiven.contentType(ContentType.JSON).body(addRemoveTO)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_ASSIGN, operateur.getId().intValue(),
						requeteGroupe1.getId())
				.then().log().body().statusCode(200);

		// check that the requeteGroupe1
		RequeteGroupe actual = requeteGroupeRepository.findOne(requeteGroupe1.getId());
		requetes = requeteRepository.findAllByRequeteGroupe(actual);
		assertThat(requetes.stream().map(Requete::getId).collect(Collectors.toList()),
				containsInAnyOrder(requete1.getId()));

	}

	@Test
	public void addRemoveNotFound() {

		AddRemoveTO addRemoveTO = new AddRemoveTO();
		addRemoveTO.getAdd().add(requete1.getId());
		addRemoveTO.getRemove().add(requete2.getId());

		preLoadedGiven.contentType(ContentType.JSON).body(addRemoveTO)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_ASSIGN, operateur.getId().intValue(), random.nextLong())
				.then().log().body().statusCode(404);
	}

	@Test
	public void addAlreadyExist() {

		AddRemoveTO addRemoveTO = new AddRemoveTO();
		addRemoveTO.getAdd().add(requete2.getId());

		preLoadedGiven.contentType(ContentType.JSON).body(addRemoveTO)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_ASSIGN, operateur.getId().intValue(),
						requeteGroupe1.getId())
				.then().log().body().statusCode(400);
	}

	@Test
	public void addNotFoundRequete() {

		AddRemoveTO addRemoveTO = new AddRemoveTO();
		addRemoveTO.getAdd().add(random.nextLong());

		preLoadedGiven.contentType(ContentType.JSON).body(addRemoveTO)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_ASSIGN, operateur.getId().intValue(),
						requeteGroupe1.getId())
				.then().log().body().statusCode(400);
	}

	@Test
	public void removeDoesNotAppear() {

		AddRemoveTO addRemoveTO = new AddRemoveTO();
		addRemoveTO.getRemove().add(requete1.getId());

		preLoadedGiven.contentType(ContentType.JSON).body(addRemoveTO)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_ASSIGN, operateur.getId().intValue(),
						requeteGroupe1.getId())
				.then().log().body().statusCode(400);
	}

	@Test
	public void removeNotFoundRequete() {

		AddRemoveTO addRemoveTO = new AddRemoveTO();
		addRemoveTO.getRemove().add(random.nextLong());

		preLoadedGiven.contentType(ContentType.JSON).body(addRemoveTO)
				.put(ApiConstants.OPERATEUR_REQUETE_GROUPE_ITEM_ASSIGN, operateur.getId().intValue(),
						requeteGroupe1.getId())
				.then().log().body().statusCode(400);
	}

}
