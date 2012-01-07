package com.codegreen.network;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;
import android.widget.Toast;

import com.codegreen.businessprocess.handler.Handler;
import com.codegreen.common.Task;
import com.codegreen.services.SOAPRequest;
import com.codegreen.util.Constants;
import com.codegreen.util.Utils;

/**
 * @author ravindra.kamble
 * 
 */
public class NetworkTask extends Task {

	/**
	 * SOAP Request.
	 */
	SOAPRequest request;

	/**
	 * Handler
	 */
	Handler handler;

	/**
	 * Connection Timeout
	 * 
	 * @param request
	 */
	String CON_TIME_OUT = "20000";

	/**
	 * Error Code Represents an error code, which describes the nature of the
	 * error/exception
	 * 
	 * @param request
	 */
	byte ERROR_CODE = 0;

	String TAG = "NetworkTask";
	
	public NetworkTask(SOAPRequest request, Handler handler) {
		this.request = request;
		this.handler = handler;
	}
	@Override
	public void deAllocate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		HttpURLConnection connection = null;
		
		URL url = null;
		try {
			url = new URL(Constants.URL);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}
		OutputStream outStream = null;
		byte requestId = request.getRequestID();
		String req = request.getSoapRequest();
		byte[] reqByteData = null;
		try {
			// Check for the connectivity mode, and set the BB Connection
			// parameters.
			if (!Utils.isNetworkAvail() && !Utils.isWifiAvail()) {
				handler.handleCallback(null, requestId, ERROR_CODE);
				return;
			}
			
			ByteArrayOutputStream requestbaos = new ByteArrayOutputStream();
			requestbaos.write(req.getBytes(), 0, req.getBytes().length);
			
			reqByteData = requestbaos.toByteArray();
			
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("soapaction",	Constants.SOAP_ACTION_URL
							+ request.getSoapMethodName());
			
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("ConnectionTimeout", CON_TIME_OUT);
			connection.setRequestProperty("Content-Length", ""+ req.length());
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setAllowUserInteraction(true);
			connection.setUseCaches(false);
			
			request.setSoapRequest(null);
			request = null;

			// Abort Check Point 3
			if (abort) {
				ERROR_CODE = 0;
				return;
			}

			outStream = connection.getOutputStream();
			reqByteData = req.getBytes();
			req = null;
			outStream.write(reqByteData, 0, reqByteData.length);
			outStream.flush();
			reqByteData = null;

			// Abort Check Point 4
			if (abort) {
				ERROR_CODE = Constants.ERR_TASK_CANCELLED;
				return;
			}

			int respCode = connection.getResponseCode();

			if (respCode == 400) { // Client Error
				ERROR_CODE = Constants.ERR_NETWORK_FAILURE;
				handler.handleCallback(null, requestId, ERROR_CODE);
				return;
			} else if (respCode == 500) { // Server Error
				ERROR_CODE = Constants.ERR_NETWORK_FAILURE;
				
				handler.handleCallback(null, requestId, ERROR_CODE);
				return;
			} else if (respCode == 200) {
				int responseLength = connection.getContentLength();
				byte[] responseGot = null;
				if (responseLength > 0) {
					InputStream dataInputStream = connection.getInputStream();
					int readBytes = 0;
					int chunkSize = 64 * 1024;
					byte[] data = new byte[chunkSize];
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					try {
						while ((readBytes = dataInputStream.read(data)) != -1) {
							out.write(data, 0, readBytes);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					data = null;
					if (dataInputStream != null) {
						try {
							dataInputStream.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						dataInputStream = null;
					}
					if (!isAlive()) {
						ERROR_CODE = 0;
						return;
					}
					responseGot = out.toByteArray();
					Log.e("Response","Got : "+ new String(responseGot));
					try {
						out.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						out = null;
					}
					if (!abort) {
						handler.handleCallback(responseGot, requestId, ERROR_CODE);
					}
					responseGot = null;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
}
