package com.ambacam.rest.requerants.requetes;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ambacam.rest.ApiConstants;
import com.ambacam.service.RequeteService;
import com.ambacam.transfert.requetes.RequeteReadTO;

@RestController
@RequestMapping(ApiConstants.REQUERANT_REQUETE_ITEM)
@CrossOrigin(origins = "${ambacam2018.app.settings.cross-origin}")
@Validated
public class RequerantRequeteResource {
	private static final Logger log = LoggerFactory.getLogger(RequerantRequeteResource.class);

	@Autowired
	private RequeteService requeteService;

	@RequestMapping(method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public RequeteReadTO get(@PathVariable("requerantId") Long requerantId, @PathVariable("requeteId") Long requeteId) {
		log.info("Get a requete [requerantId={}, requeteId={}]", requerantId, requeteId);

		return requeteService.get(requeteId);

	}
}
