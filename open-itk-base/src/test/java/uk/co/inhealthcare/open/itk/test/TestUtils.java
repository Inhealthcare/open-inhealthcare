package uk.co.inhealthcare.open.itk.test;

import java.util.Properties;

import uk.co.inhealthcare.open.itk.service.ITKSimpleDOSImpl;
import uk.co.inhealthcare.open.itk.service.ITKSimpleDOSImplUnitTest;
import uk.co.inhealthcare.open.itk.util.ITKDirectoryProperties;
import uk.co.inhealthcare.open.itk.util.ITKServiceProperties;

public class TestUtils {

	public static void loadTestProperties(ITKSimpleDOSImpl itkSimpleDOS)
			throws Exception {

		ITKDirectoryProperties itkDirectoryProperties = new ITKDirectoryProperties();
		Properties directoryProps = new Properties();
		directoryProps.load(ITKSimpleDOSImplUnitTest.class
				.getResourceAsStream("/directory.properties"));
		itkDirectoryProperties.setProps(directoryProps);
		itkSimpleDOS.setItkDirectoryProperties(itkDirectoryProperties);

		ITKServiceProperties itkServiceProperties = new ITKServiceProperties();
		Properties serviceProps = new Properties();
		serviceProps.load(ITKSimpleDOSImplUnitTest.class
				.getResourceAsStream("/service.properties"));
		itkServiceProperties.setProps(serviceProps);
		itkSimpleDOS.setItkServiceProperties(itkServiceProperties);
	}

}
