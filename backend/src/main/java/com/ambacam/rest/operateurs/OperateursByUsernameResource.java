package com.ambacam.rest.operateurs;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ambacam.rest.ApiConstants;
import com.ambacam.service.OperateurService;
import com.ambacam.transfert.ValueTO;
import com.ambacam.transfert.operateurs.OperateurReadTO;

@RestController
@RequestMapping(ApiConstants.OPERATEUR_ITEM_BY_USERNAME)
@CrossOrigin(origins = "${ambacam2018.app.settings.cross-origin}")
@Validated
public class OperateursByUsernameResource {

	private static final Logger log = LoggerFactory.getLogger(OperateursByUsernameResource.class);

	@Autowired
	private OperateurService operateurService;

	@RequestMapping(method = RequestMethod.POST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public OperateurReadTO findByUsername(@RequestBody @Valid ValueTO<String> valueTO) {
		log.info("find operateur by username [username={}]", valueTO.getValue());

		return operateurService.findByUsername(valueTO.getValue());

	}
}
