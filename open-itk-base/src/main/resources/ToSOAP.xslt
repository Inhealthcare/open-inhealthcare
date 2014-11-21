<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" exclude-result-prefixes="xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">

<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" 
	       xmlns:wsa="http://www.w3.org/2005/08/addressing" 
	       xmlns:itk="urn:nhs-itk:ns:201005">
	<soap:Header>
		<wsa:MessageID><xsl:value-of select="SOAPMessage/MessageId"/></wsa:MessageID>
		<wsa:Action><xsl:value-of select="SOAPMessage/Action"/></wsa:Action>
		<wsa:To><xsl:value-of select="SOAPMessage/To"/></wsa:To>

		<xsl:if test="SOAPMessage/MessageType = 'SYNCREQ' ">
			<wsa:From>
				<wsa:Address><xsl:value-of select="SOAPMessage/From"/></wsa:Address>
			</wsa:From>
			<xsl:if test="SOAPMessage/Expires != '' or SOAPMessage/Username != ''">
				<wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
					<wsu:Timestamp xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="D6CD5232-14CF-11DF-9423-1F9A910D4703">
						<wsu:Created><xsl:value-of select="SOAPMessage/Created"/></wsu:Created>
						<wsu:Expires><xsl:value-of select="SOAPMessage/Expires"/></wsu:Expires>
					</wsu:Timestamp>
					<wsse:UsernameToken>
						<wsse:Username><xsl:value-of select="SOAPMessage/Username"/></wsse:Username>
					</wsse:UsernameToken>
				</wsse:Security>
			</xsl:if>
		</xsl:if>

		<xsl:if test="SOAPMessage/MessageType = 'SYNCRESP' ">
			<!-- SOAP Sync Response does not need the WS Security header or the from header -->
		</xsl:if>

		<xsl:if test="SOAPMessage/MessageType = 'ASYNCREQ' ">
			<!-- SOAP Async Request should specify the replyTo and FaultTo parameters -->
			<!-- This impl allow them to be absent because CDA is ASYNC really but SOAP SYNC -->
			<wsa:From>
				<wsa:Address><xsl:value-of select="SOAPMessage/From"/></wsa:Address>
			</wsa:From>
			<xsl:if test="SOAPMessage/ReplyTo != '' ">
				<wsa:ReplyTo>
					<wsa:Address><xsl:value-of select="SOAPMessage/ReplyTo"/></wsa:Address>
				</wsa:ReplyTo>
			</xsl:if>
			<xsl:if test="SOAPMessage/FaultTo != '' ">
				<wsa:FaultTo>
					<wsa:Address><xsl:value-of select="SOAPMessage/FaultTo"/></wsa:Address>
				</wsa:FaultTo>
			</xsl:if>
			<xsl:if test="SOAPMessage/Expires != '' or SOAPMessage/Username != ''">
				<wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
					<wsu:Timestamp xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="D6CD5232-14CF-11DF-9423-1F9A910D4703">
						<wsu:Created><xsl:value-of select="SOAPMessage/Created"/></wsu:Created>
						<wsu:Expires><xsl:value-of select="SOAPMessage/Expires"/></wsu:Expires>
					</wsu:Timestamp>
					<wsse:UsernameToken>
						<wsse:Username><xsl:value-of select="SOAPMessage/Username"/></wsse:Username>
					</wsse:UsernameToken>
				</wsse:Security>
			</xsl:if>
		</xsl:if>

		<xsl:if test="SOAPMessage/MessageType = 'ASYNCRESP' ">
			<!-- Does an ASYNC Response actually exist? Or would it be a new ASYNCREQ ? -->
			<wsa:From>
				<wsa:Address><xsl:value-of select="SOAPMessage/From"/></wsa:Address>
			</wsa:From>
			<xsl:if test="SOAPMessage/Expires != '' or SOAPMessage/Username != ''">
				<wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd">
					<wsu:Timestamp xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" wsu:Id="D6CD5232-14CF-11DF-9423-1F9A910D4703">
						<wsu:Created><xsl:value-of select="SOAPMessage/Created"/></wsu:Created>
						<wsu:Expires><xsl:value-of select="SOAPMessage/Expires"/></wsu:Expires>
					</wsu:Timestamp>
					<wsse:UsernameToken>
						<wsse:Username><xsl:value-of select="SOAPMessage/Username"/></wsse:Username>
					</wsse:UsernameToken>
				</wsse:Security>
			</xsl:if>
		</xsl:if>
	
	</soap:Header>
	<soap:Body>
		<xsl:copy-of select="SOAPMessage/Payload/*"/>
	</soap:Body>
</soap:Envelope>

	</xsl:template>
</xsl:stylesheet>
	
