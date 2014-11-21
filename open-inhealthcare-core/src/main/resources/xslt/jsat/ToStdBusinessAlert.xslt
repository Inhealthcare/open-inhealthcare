<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" exclude-result-prefixes="xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
	
		<HTML>
			<Head>
			</Head>
			<Body>
				<h1>ALERT: A business error has occurred tracing patient details on SPINE.</h1>
				<h2><xsl:value-of select="Email/Headline"/></h2>
				<p><xsl:value-of select="Email/Message"/></p>
				<p>Demographic Details
				<li>NHS Number:<xsl:value-of select="Email/BusinessContext/DemographicUpdate/NhsNumber"/></li>
				<li>NHS Number Status:<xsl:value-of select="Email/BusinessContext/DemographicUpdate/NhsNumberStatus"/></li>
				<li>Given Name:<xsl:value-of select="Email/BusinessContext/DemographicUpdate/GivenName"/></li>
				<li>Family Name:<xsl:value-of select="Email/BusinessContext/DemographicUpdate/FamilyName"/></li>
				<li>Date Of Birth:<xsl:value-of select="Email/BusinessContext/DemographicUpdate/DateOfBirth"/></li>
				<li>Gender:<xsl:value-of select="Email/BusinessContext/DemographicUpdate/Gender"/></li>
				<li>Postcode:<xsl:value-of select="Email/BusinessContext/DemographicUpdate/Postcode"/></li>
				<li>Local Patient Identifier:<xsl:value-of select="Email/BusinessContext/DemographicUpdate/LocalPatientIdentifier"/></li>
				</p>
				<p>Please quote the following if technical support is required:
				<li>JSAT Conversation Id:<xsl:value-of select="Email/ConversationId"/></li>
				<li>Date/Time:<xsl:value-of select="Email/DateTime"/></li>
				<li>Business Process:<xsl:value-of select="Email/BusinessProcess"/></li>
				</p>
				<br/>
				<p>Please respond to this alert in accordance with defined business processes for your Trust.</p>
			</Body>
		</HTML>
	
	</xsl:template>
</xsl:stylesheet>