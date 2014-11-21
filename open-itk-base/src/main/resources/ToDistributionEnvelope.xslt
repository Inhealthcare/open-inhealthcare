<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" exclude-result-prefixes="xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<itk:DistributionEnvelope xmlns:itk="urn:nhs-itk:ns:201005" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		<itk:header>
			<xsl:attribute name="service">
				<xsl:value-of select="ITKMessage/Service"/>
			</xsl:attribute>
			<xsl:attribute name="trackingid">
				<xsl:value-of select="ITKMessage/TrackingId"/>
			</xsl:attribute>
			<itk:addresslist>
				<itk:address>
					<xsl:if test="ITKMessage/RecipientType!='2.16.840.1.113883.2.1.3.2.4.18.22'">
						<xsl:attribute name="type">
							<xsl:value-of select="ITKMessage/RecipientType"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="uri">
						<xsl:value-of select="ITKMessage/Recipient"/>
					</xsl:attribute>
				</itk:address>
			</itk:addresslist>

			<itk:auditIdentity>
			     <xsl:for-each select="ITKMessage/Authors/Author">
			        <itk:id>
						<xsl:attribute name="type">
							<xsl:value-of select="./Type"/>
						</xsl:attribute>
						<xsl:attribute name="uri">
							<xsl:value-of select="./URI"/>
						</xsl:attribute>
					</itk:id>
			     </xsl:for-each>

			</itk:auditIdentity>

			<itk:manifest count="1">
				<itk:manifestitem id="uuid_{ITKMessage/Manifest/@id}">
					<xsl:if test="ITKMessage/Manifest/@profileid">
						<xsl:attribute name="profileid">
							<xsl:value-of select="ITKMessage/Manifest/@profileid"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="ITKMessage/Manifest/@base64">
						<xsl:attribute name="base64">
							<xsl:value-of select="ITKMessage/Manifest/@base64"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="mimetype">
						<xsl:value-of select="ITKMessage/Manifest/@type"/>
					</xsl:attribute>
				</itk:manifestitem>
			</itk:manifest>
			<xsl:if test="ITKMessage/Sender">
				<itk:senderAddress>
						<xsl:if test="ITKMessage/SenderType!='2.16.840.1.113883.2.1.3.2.4.18.22'">
							<xsl:attribute name="type">
								<xsl:value-of select="ITKMessage/SenderType"/>
							</xsl:attribute>
						</xsl:if>
						<xsl:attribute name="uri">
							<xsl:value-of select="ITKMessage/Sender"/>
						</xsl:attribute>
				</itk:senderAddress>
			</xsl:if>
			<xsl:if test="ITKMessage/HandlingSpecs">
			     <itk:handlingSpecification>
				     <xsl:for-each select="ITKMessage/HandlingSpecs/Spec">
				        <itk:spec key="{@key}" value="{@value}" />
				     </xsl:for-each>
			     </itk:handlingSpecification>
			</xsl:if>
		</itk:header>
		<itk:payloads count="1">
			<itk:payload id="uuid_{ITKMessage/Payload/@id}">
				<xsl:copy-of select="ITKMessage/Payload/text() | ITKMessage/Payload/*"/>
			</itk:payload>
		</itk:payloads>
		</itk:DistributionEnvelope>
	</xsl:template>
</xsl:stylesheet>
