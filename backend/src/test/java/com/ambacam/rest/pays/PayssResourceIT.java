package com.ambacam.rest.pays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambacam.ItBase;
import com.ambacam.model.Pays;
import com.ambacam.repository.PaysRepository;
import com.ambacam.rest.ApiConstants;

import io.restassured.http.ContentType;

public class PayssResourceIT extends ItBase {

	@Autowired
	private PaysRepository repository;

	private Pays pays1;

	private Pays pays2;

	@Before
	public void setup() throws Exception {
		super.setup();

		// create pays1
		pays1 = buildPays();
		pays1 = repository.save(pays1);
		// create pays2
		pays2 = repository.save(buildPays());
	}

	@Override
	@After
	public void cleanup() throws Exception {
		operateurRepository.deleteAll();
		repository.deleteAll();
		super.cleanup();
	}

	@Test
	public void create() {
		Pays create = buildPays();
		int id = preLoadedGiven.contentType(ContentType.JSON).body(create).log().body()
				.post(ApiConstants.PAYS_COLLECTION).then().log().body().statusCode(200).extract().body().path("id");

		// check that the pays has been saved
		Pays actual = repository.findOne(Integer.toUnsignedLong(id));
		assertThat(actual, is(notNullValue()));
		assertThat(actual.getNom(), is(equalTo(create.getNom())));
		assertThat(actual.getDescription(), is(equalTo(create.getDescription())));
	}

	@Test
	public void createNomNull() {
		Pays create = buildPays();
		create.setNom(null);
		preLoadedGiven.contentType(ContentType.JSON).body(create).log().body().post(ApiConstants.PAYS_COLLECTION).then()
				.log().body().statusCode(400);
	}

	@Test
	public void createNomVide() {
		Pays create = buildPays();
		create.setNom("");
		preLoadedGiven.contentType(ContentType.JSON).body(create).log().body().post(ApiConstants.PAYS_COLLECTION).then()
				.log().body().statusCode(400);
	}

	@Test
	public void createMemeNom() {
		Pays create = buildPays();
		create.setNom(pays1.getNom());
		preLoadedGiven.contentType(ContentType.JSON).body(create).log().body().post(ApiConstants.PAYS_COLLECTION).then()
				.log().body().statusCode(400);
	}

	@Test
	public void list() {
		preLoadedGiven.get(ApiConstants.PAYS_COLLECTION).then().log().body().statusCode(200)
				.body("size()", is(equalTo(3)))
				.body("id",
						containsInAnyOrder(pays1.getId().intValue(), pays2.getId().intValue(),
								defaultPays.getId().intValue()))
				.body("nom", containsInAnyOrder(pays1.getNom(), pays2.getNom(), defaultPays.getNom()))
				.body("description", containsInAnyOrder(pays1.getDescription(), pays2.getDescription(),
						defaultPays.getDescription()));
	}

	@Test
	public void get() {
		preLoadedGiven.get(ApiConstants.PAYS_ITEM, pays2.getId()).then().log().body().statusCode(200)
				.body("id", is(equalTo(pays2.getId().intValue()))).body("nom", is(equalTo(pays2.getNom())))
				.body("description", is(equalTo(pays2.getDescription())));
	}

	@Test
	public void getNotFound() {
		preLoadedGiven.get(ApiConstants.PAYS_ITEM, random.nextLong()).then().statusCode(404);
	}

	@Test
	public void delete() {
		preLoadedGiven.delete(ApiConstants.PAYS_ITEM, pays1.getId()).then().statusCode(200);

		// check that the pays has been deleted
		Pays actual = repository.findOne(pays1.getId());
		assertThat(actual, is(nullValue()));

	}

	@Test
	public void deleteNotFound() {
		preLoadedGiven.delete(ApiConstants.PAYS_ITEM, random.nextLong()).then().log().body().statusCode(404);
	}

	@Test
	public void update() {
		Pays update = buildPays();
		preLoadedGiven.contentType(ContentType.JSON).body(update).put(ApiConstants.PAYS_ITEM, pays2.getId()).then()
				.log().body().statusCode(200);

		// check that the pays has been saved
		Pays actual = repository.findOne(pays2.getId());
		assertThat(actual.getId(), is(equalTo(pays2.getId())));
		assertThat(actual.getNom(), is((equalTo(update.getNom()))));
		assertThat(actual.getDescription(), is(equalTo(update.getDescription())));

	}

	@Test
	public void updateNotFound() {
		Pays update = buildPays();
		preLoadedGiven.contentType(ContentType.JSON).body(update).put(ApiConstants.PAYS_ITEM, random.nextLong()).then()
				.log().body().statusCode(404);
	}

	@Test
	public void updateMemeNom() {
		Pays update = buildPays();
		update.setNom(pays1.getNom());
		preLoadedGiven.contentType(ContentType.JSON).body(update).log().body()
				.put(ApiConstants.PAYS_ITEM, pays1.getId()).then().log().body().statusCode(200);

		// check that the pays has been saved
		Pays actual = repository.findOne(pays1.getId());
		assertThat(actual.getNom(), is((equalTo(update.getNom()))));
		assertThat(actual.getDescription(), is(equalTo(update.getDescription())));
	}

	@Test
	public void updateNomExists() {
		Pays update = buildPays();
		update.setNom(pays2.getNom());
		preLoadedGiven.contentType(ContentType.JSON).body(update).log().body()
				.put(ApiConstants.PAYS_ITEM, pays1.getId()).then().log().body().statusCode(400);
	}

}
