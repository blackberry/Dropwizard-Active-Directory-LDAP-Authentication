/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.dwauth.ldap;

/**
 *
 * @author dariens
 */
public class AccessDeniedException extends Exception {
	public AccessDeniedException(String message) {
		super(message);
	}	
}
