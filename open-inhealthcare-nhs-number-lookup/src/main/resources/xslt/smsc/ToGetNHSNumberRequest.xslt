<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" exclude-result-prefixes="xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
	
		<getNHSNumberRequest-v1-0 xmlns="urn:hl7-org:v3" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" moodCode="EVN" classCode="CACT">
			<id>
				<xsl:attribute name="root">
					<xsl:value-of select="Message/MessageId"/>
				</xsl:attribute>
			</id>
			<code codeSystem="2.16.840.1.113883.2.1.3.2.4.17.284" code="getNHSNumberRequest-v1-0"/>
			<queryEvent>
				<Person.DateOfBirth>
					<value>
						<xsl:attribute name="value">
							<xsl:value-of select="Message/DateOfBirth"/>
						</xsl:attribute>
					</value>
					<semanticsText>Person.DateOfBirth</semanticsText>
				</Person.DateOfBirth>
				<Person.Gender>
					<value codeSystem="2.16.840.1.113883.2.1.3.2.4.16.25">
						<xsl:attribute name="code">
							<xsl:value-of select="Message/Gender"/>
						</xsl:attribute>
					</value>
					<semanticsText>Person.Gender</semanticsText>
				</Person.Gender>
				<xsl:if test="string-length(Message/LocalPatientIdentifier) &gt; 0">
					<Person.LocalIdentifier>
						<value root="2.16.840.1.113883.2.1.3.2.4.18.24">
							<xsl:attribute name="extension">
								<xsl:value-of select="Message/LocalPatientIdentifier"/>
							</xsl:attribute>
						</value>
						<semanticsText>Person.LocalIdentifier</semanticsText>
					</Person.LocalIdentifier>
				</xsl:if>
				<Person.Name>
					<value>
						<given><xsl:value-of select="Message/GivenName"/></given>
						<family><xsl:value-of select="Message/Surname"/></family>
					</value>
					<semanticsText>Person.Name</semanticsText>
				</Person.Name>
				<xsl:if test="string-length(Message/Postcode) &gt; 0">
					<Person.Postcode>
						<value>
							<postalCode><xsl:value-of select="Message/Postcode"/></postalCode>
						</value>
						<semanticsText>Person.Postcode</semanticsText>
					</Person.Postcode>
				</xsl:if>
			</queryEvent>
		</getNHSNumberRequest-v1-0>
	
	</xsl:template>
</xsl:stylesheet>