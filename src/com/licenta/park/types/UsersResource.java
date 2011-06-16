/**
 * 
 */
package com.licenta.park.types;

import org.restlet.resource.Post;
import com.licenta.park.types.User;

/**
 * @author vladucu
 *
 */
public interface UsersResource {
	
	@Post("json")
	public User createUser(User user);

}
