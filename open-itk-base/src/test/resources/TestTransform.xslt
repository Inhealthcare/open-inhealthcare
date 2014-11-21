<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" exclude-result-prefixes="xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
	 	<xsl:param name="P1" />
	 	<xsl:param name="P2" />
	 	<xsl:param name="P3" />
		<TEST>
			<A><xsl:value-of select="Input/A"/></A>
			<B><xsl:value-of select="Input/B"/></B>
			<C><xsl:value-of select="Input/C"/></C>
			<P1><xsl:value-of select="$P1"/></P1>
			<P2><xsl:value-of select="$P2"/></P2>
			<P3><xsl:value-of select="$P3"/></P3>
		</TEST>
	
	</xsl:template>
</xsl:stylesheet>