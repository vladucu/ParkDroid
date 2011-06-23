/**
 * 
 */
package com.licenta.parkdroid.types;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

import com.licenta.parkdroid.types.User;


public interface UserResource {

    @Get("json")
    public User retrieveUser();
    
    @Delete
    public void removeUser();

    @Put("json")
    public User updateUser(User user);
}