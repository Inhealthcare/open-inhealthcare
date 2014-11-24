<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:xs="http://www.w3.org/2001/XMLSchema" 
	xmlns:fn="http://www.w3.org/2005/xpath-functions" 
	xmlns:hl7="urn:hl7-org:v3"
	xmlns:itk="urn:nhs-itk:ns:201005"
	exclude-result-prefixes="xs hl7 fn itk">
      <!-- 
		V1 - SMSC
		V2 - 17/06/2014 - Save all telecoms
	-->  
	<xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
	
		<Response>
			<ResponseCode><xsl:value-of select="//hl7:getPatientDetailsResponse-v1-0/hl7:value/@code"/></ResponseCode>
			<NHSNumber><xsl:value-of select="//hl7:patient/hl7:id[@root='2.16.840.1.113883.2.1.4.1']/@extension"/></NHSNumber>
			<LocalIdentifier><xsl:value-of select="//hl7:patient/hl7:id[@root='2.16.840.1.113883.2.1.3.2.4.18.24']/@extension"/></LocalIdentifier>
			<Title><xsl:value-of select="//hl7:patient/hl7:name/hl7:prefix"/></Title>
			<Surname><xsl:value-of select="//hl7:patient/hl7:name/hl7:family"/></Surname>
			<GivenName><xsl:value-of select="//hl7:patient/hl7:name/hl7:given[1]"/></GivenName>
			<OtherGivenName><xsl:value-of select="//hl7:patient/hl7:name/hl7:given[2]"/></OtherGivenName>

			<xsl:if test="//hl7:patient/hl7:addr[@use='H']">
			<Address>
				<AddressLine1><xsl:value-of select="//hl7:patient/hl7:addr[@use='H']/hl7:streetAddressLine[1]"/></AddressLine1>
				<AddressLine2><xsl:value-of select="//hl7:patient/hl7:addr[@use='H']/hl7:streetAddressLine[2]"/></AddressLine2>
				<AddressLine3><xsl:value-of select="//hl7:patient/hl7:addr[@use='H']/hl7:streetAddressLine[3]"/></AddressLine3>
				<AddressLine4><xsl:value-of select="//hl7:patient/hl7:addr[@use='H']/hl7:streetAddressLine[4]"/></AddressLine4>
				<AddressLine5><xsl:value-of select="//hl7:patient/hl7:addr[@use='H']/hl7:streetAddressLine[5]"/></AddressLine5>
				<Postcode><xsl:value-of select="//hl7:patient/hl7:addr[@use='H']/hl7:postalCode"/></Postcode>
			</Address>
			</xsl:if>

			<xsl:if test="//hl7:patient/hl7:addr[@use='TMP']">
			<TemporaryAddress>
				<AddressLine1><xsl:value-of select="//hl7:patient/hl7:addr[@use='TMP']/hl7:streetAddressLine[1]"/></AddressLine1>
				<AddressLine2><xsl:value-of select="//hl7:patient/hl7:addr[@use='TMP']/hl7:streetAddressLine[2]"/></AddressLine2>
				<AddressLine3><xsl:value-of select="//hl7:patient/hl7:addr[@use='TMP']/hl7:streetAddressLine[3]"/></AddressLine3>
				<AddressLine4><xsl:value-of select="//hl7:patient/hl7:addr[@use='TMP']/hl7:streetAddressLine[4]"/></AddressLine4>
				<AddressLine5><xsl:value-of select="//hl7:patient/hl7:addr[@use='TMP']/hl7:streetAddressLine[5]"/></AddressLine5>
				<Postcode><xsl:value-of select="//hl7:patient/hl7:addr[@use='TMP']/hl7:postalCode"/></Postcode>
			</TemporaryAddress>
			</xsl:if>

			<xsl:if test="//hl7:patient/hl7:addr[@use='PST']">
			<CorrespondenceAddress>
				<AddressLine1><xsl:value-of select="//hl7:patient/hl7:addr[@use='PST']/hl7:streetAddressLine[1]"/></AddressLine1>
				<AddressLine2><xsl:value-of select="//hl7:patient/hl7:addr[@use='PST']/hl7:streetAddressLine[2]"/></AddressLine2>
				<AddressLine3><xsl:value-of select="//hl7:patient/hl7:addr[@use='PST']/hl7:streetAddressLine[3]"/></AddressLine3>
				<AddressLine4><xsl:value-of select="//hl7:patient/hl7:addr[@use='PST']/hl7:streetAddressLine[4]"/></AddressLine4>
				<AddressLine5><xsl:value-of select="//hl7:patient/hl7:addr[@use='PST']/hl7:streetAddressLine[5]"/></AddressLine5>
				<Postcode><xsl:value-of select="//hl7:patient/hl7:addr[@use='PST']/hl7:postalCode"/></Postcode>
			</CorrespondenceAddress>
			</xsl:if>

			<DateOfBirth><xsl:value-of select="//hl7:patientPerson/hl7:birthTime/@value"/></DateOfBirth>
			<DateOfDeath><xsl:value-of select="//hl7:patientPerson/hl7:deceasedTime/@value"/></DateOfDeath>
			<Gender><xsl:value-of select="//hl7:patientPerson/hl7:administrativeGenderCode/@code"/></Gender>
 		    
 		    <xsl:for-each select="//hl7:patient/hl7:telecom">
			    <Telecom use="{@use}" value="{@value}" />
			</xsl:for-each>

			<PracticeCode><xsl:value-of select="//hl7:gPPractice/hl7:locationOrganization/hl7:id/@extension"/></PracticeCode>
			<PracticeName><xsl:value-of select="//hl7:gPPractice/hl7:locationOrganization/hl7:name"/></PracticeName>
			<PracticeContactTelephoneNumber><xsl:value-of select="//hl7:gPPractice/hl7:telecom/@value"/></PracticeContactTelephoneNumber>

			<xsl:if test="//hl7:gPPractice/hl7:addr">
			<PracticeAddress>
				<AddressLine1><xsl:value-of select="//hl7:gPPractice/hl7:addr/hl7:streetAddressLine[1]"/></AddressLine1>
				<AddressLine2><xsl:value-of select="//hl7:gPPractice/hl7:addr/hl7:streetAddressLine[2]"/></AddressLine2>
				<AddressLine3><xsl:value-of select="//hl7:gPPractice/hl7:addr/hl7:streetAddressLine[3]"/></AddressLine3>
				<AddressLine4><xsl:value-of select="//hl7:gPPractice/hl7:addr/hl7:streetAddressLine[4]"/></AddressLine4>
				<AddressLine5><xsl:value-of select="//hl7:gPPractice/hl7:addr/hl7:streetAddressLine[5]"/></AddressLine5>
				<Postcode><xsl:value-of select="//hl7:gPPractice/hl7:addr/hl7:postalCode"/></Postcode>
			</PracticeAddress>
			</xsl:if>

		</Response>
	
	</xsl:template>
</xsl:stylesheet>
