/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.dwauth.ldap;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ExceptionMapper;

@Provider
public class AccessDeniedHandler implements ExceptionMapper<AccessDeniedException> {
	
	@Override
	public javax.ws.rs.core.Response toResponse(AccessDeniedException ade) {
		return Response.status(403).type("text/plain").entity(ade.getMessage()).build();
	}

}
