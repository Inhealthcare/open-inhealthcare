package uk.co.inhealthcare.open.itk.util;

import uk.co.inhealthcare.open.itk.payload.ITKMessage;

public class ITKLogFormatter {

	public static String getFormattedLog(String logMsg, ITKMessage itkMsg){
		String fMsg = "[MESSAGE]"+logMsg
				+"[TRKID]"+getTrackingId(itkMsg)
				+"[PAYLID]"+getItkPayloadId(itkMsg)
				+"[SVCID]"+getServiceId(itkMsg);
		
		return fMsg;
	}
	
	// safe accessors
	private static String getTrackingId(ITKMessage itkMsg){
		String id = "NOTFOUND";
		try {
			id = itkMsg.getMessageProperties().getTrackingId();
		} catch (Exception e){
		}
		return id;
	}
	private static String getItkPayloadId(ITKMessage itkMsg){
		String id = "NOTFOUND";
		try {
			id = itkMsg.getMessageProperties().getItkPayloadId();
		} catch (Exception e){
		}
		return id;
	}
	private static String getServiceId(ITKMessage itkMsg){
		String id = "NOTFOUND";
		try {
			id = itkMsg.getMessageProperties().getServiceId();
		} catch (Exception e){
		}
		return id;
	}
}
