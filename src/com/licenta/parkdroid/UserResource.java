/**
 * 
 */
package com.licenta.parkdroid;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;


public interface UserResource {

    @Get("json")
    public User retrieveUser();
    
    @Delete
    public void removeUser();

    @Put("json")
    public User updateUser(User user);
}