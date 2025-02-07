package com.ambacam.rest.typerequetes;

import com.ambacam.model.TypeRequete;
import com.ambacam.rest.ApiConstants;
import com.ambacam.service.TypeRequeteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RestController
@RequestMapping(ApiConstants.TYPE_REQUETE_ITEM)
@CrossOrigin(origins = "${ambacam2018.app.settings.cross-origin}")
public class TypeRequeteResource {

	private static final Logger log = LoggerFactory.getLogger(TypeRequeteResource.class);

	@Autowired
	private TypeRequeteService typeRequeteService;

	@RequestMapping(method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public TypeRequete get(@PathVariable("typeRequeteId") Long typeRequeteId) {
		log.info("Get a typeRequete [id={}]", typeRequeteId);

		return typeRequeteService.get(typeRequeteId);

	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void delete(@PathVariable("typeRequeteId") Long typeRequeteId) {
		log.info("Delete a typeRequete [id={}]", typeRequeteId);

		typeRequeteService.delete(typeRequeteId);

	}

	@RequestMapping(method = RequestMethod.PUT)
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(@PathVariable("typeRequeteId") Long typeRequeteId,
			@RequestBody @Valid TypeRequete update) {
		log.info("Update a typeRequete [id={}]", typeRequeteId);

		typeRequeteService.update(typeRequeteId, update);
	}

}
