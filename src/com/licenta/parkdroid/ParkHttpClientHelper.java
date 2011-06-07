/**
 * 
 */
package com.licenta.parkdroid;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.restlet.Client;

import com.licenta.park.utils.EasySSLSocketFactory;

/**
 * @author vladucu
 *
 */
public class ParkHttpClientHelper extends org.restlet.ext.httpclient.HttpClientHelper {

	public ParkHttpClientHelper(Client client) {
		super(client);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.restlet.ext.httpclient.HttpClientHelper#configure(org.apache.http.conn.scheme.SchemeRegistry)
	 */
	@Override
	protected void configure(SchemeRegistry schemeRegistry) {
		// TODO Auto-generated method stub
		super.configure(schemeRegistry);
		
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 8443));
	}

}
