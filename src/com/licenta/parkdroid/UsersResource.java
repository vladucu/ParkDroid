/**
 * 
 */
package com.licenta.parkdroid;

import org.restlet.resource.Post;

/**
 * @author vladucu
 *
 */
public interface UsersResource {
	
	@Post("json")
	public User createUser(User user);

}
