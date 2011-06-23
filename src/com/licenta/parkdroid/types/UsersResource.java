/**
 * 
 */
package com.licenta.parkdroid.types;

import org.restlet.resource.Post;

import com.licenta.parkdroid.types.User;

/**
 * @author vladucu
 *
 */
public interface UsersResource {
	
	@Post("json")
	public User createUser(User user);

}
