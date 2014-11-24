package uk.co.inhealthcare.open.itk.source;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import uk.co.inhealthcare.open.itk.infrastructure.ITKMessagingException;
import uk.co.inhealthcare.open.itk.infrastructure.ITKUnavailableException;
import uk.co.inhealthcare.open.itk.payload.ITKMessage;
import uk.co.inhealthcare.open.itk.payload.ITKSimpleMessageImpl;
import uk.co.inhealthcare.open.itk.source.ITKMessageSender;

public class ITKMessageSenderImpl_Mock implements ITKMessageSender {
	
	private String responseType;
	public final static String BUSY = "BUSY";
	public final static String FAIL = "FAIL";
	
	public void primeResponse(String responseType) {
		this.responseType = responseType;
	}
	
	public ITKMessage parm_Request = null;
	@Override
	public ITKMessage sendSync(ITKMessage request) throws ITKMessagingException {

		this.parm_Request = request;
		
		ITKMessage response = null;
		
		if (this.responseType==null){
			throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Service not primed.");
		} else if (this.responseType.equals(BUSY)){
			throw new ITKUnavailableException("Primed to be busy");
		} else if (this.responseType.equals(FAIL)){
			throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Primed to fail");
		} else {
			// Construct the Response message
			response = new ITKSimpleMessageImpl(request.getConversationId());
			String responseString;
			try {
				responseString = readInput(this.responseType);
			} catch (IOException e) {
				throw new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Error Reading Test File.");
			}
			response.setBusinessPayload(responseString);
		}
		return response;

	}
	
	@Override
	public void send(ITKMessage request) throws ITKMessagingException {

		ITKMessagingException notImplementedException = 
				new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Not Yet Implemented");
		throw notImplementedException;
		
	}
	
	@Override
	public void sendAsync(ITKMessage request) throws ITKMessagingException {

		ITKMessagingException notImplementedException = 
				new ITKMessagingException(ITKMessagingException.PROCESSING_ERROR_NOT_RETRYABLE_CODE, "Not Yet Implemented");
		throw notImplementedException;
		
	}
	private String readInput(String testFileName) throws IOException {
		
	    InputStream tis = ITKMessageSenderImpl_Mock.class.getResourceAsStream(testFileName);

		BufferedInputStream bis = new BufferedInputStream(tis);
		byte[] contents = new byte[1024];
		int bytesRead = 0;
		String responseString = "";
		while ((bytesRead = bis.read(contents)) != -1) {
			responseString = responseString + new String(contents, 0, bytesRead);
		}
		bis.close();
		
		return responseString ;
	}

}
