<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml"/>

<xsl:template match="dict/text()"><xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="plist/text()"><xsl:text>
</xsl:text>
</xsl:template>


<xsl:template match="plist">
    <xsl:copy>
        <xsl:apply-templates select="dict|text()"/>
    </xsl:copy>
</xsl:template>

<xsl:template match="dict">
    <xsl:copy>
        <xsl:apply-templates select="key|dict|text()"/>
    </xsl:copy>
    <xsl:text>+++++</xsl:text>
</xsl:template>


<xsl:template match="key">
<xsl:element name="item">
		<xsl:copy>
			<xsl:apply-templates select="@*|text()"/>
		</xsl:copy>
		<xsl:for-each select="parent::*/child::*[.=current()]/following::*[1]">
		<xsl:copy>
			<xsl:apply-templates select="child::node()"/>
		</xsl:copy>
		</xsl:for-each>
	</xsl:element>
	</xsl:template>

<!-- 
<xsl:template match="*">
<xsl:variable name="nextPosition" select="position()+1"/>
<xsl:variable name="nextNode"><xsl:copy-of select="../*[position()=$nextPosition]"/></xsl:variable>
<xsl:choose>
<xsl:when test="name()='key'">
<xsl:element name="item">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
		<xsl:copy-of select="$nextNode"/>
	</xsl:element>
</xsl:when>
<xsl:when test="position()">
</xsl:when>
<xsl:otherwise>
    <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
</xsl:otherwise>
</xsl:choose>
</xsl:template>
-->

</xsl:stylesheet>