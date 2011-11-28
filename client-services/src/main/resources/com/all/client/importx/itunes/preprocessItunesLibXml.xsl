<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" encoding="iso-8859-1" />

<xsl:template match="/">
<library>
<xsl:call-template name="track"/>
<xsl:call-template name="playlist"/>
</library>
</xsl:template>


<xsl:template name="track">
<xsl:text>
</xsl:text><xsl:for-each select="/plist/dict/dict[preceding-sibling::key[text()='Tracks']]/dict">
<xsl:element name="track">

<xsl:for-each select="key[text()='Track ID'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::integer/text()"/></xsl:variable>
<xsl:attribute name="trackId"><xsl:value-of select="$text"/></xsl:attribute>
</xsl:for-each>

<xsl:for-each select="key[text()='Podcast'][1]">
<xsl:variable name="theCount"><xsl:value-of select="count(following-sibling::true)"/></xsl:variable>
<xsl:attribute name="isPodcast">
<xsl:if test="$theCount!='0'">
<xsl:text>true</xsl:text>
</xsl:if>
</xsl:attribute>
</xsl:for-each>

<xsl:text>
</xsl:text>

<xsl:for-each select="key[text()='Name'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::string/text()"/></xsl:variable>
<xsl:element name="name"><xsl:value-of select="$text"/></xsl:element><xsl:text>
</xsl:text>
</xsl:for-each>

<xsl:for-each select="key[text()='Album'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::string/text()"/></xsl:variable>
<xsl:element name="album"><xsl:value-of select="$text"/></xsl:element><xsl:text>
</xsl:text>
</xsl:for-each>


<xsl:for-each select="key[text()='Location'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::string/text()"/></xsl:variable>
<xsl:element name="location"><xsl:value-of select="$text"/></xsl:element><xsl:text>
</xsl:text>
</xsl:for-each>

</xsl:element><xsl:text>
</xsl:text>
</xsl:for-each><xsl:text>
</xsl:text>
</xsl:template>


<xsl:template name="playlist">
<xsl:text>
</xsl:text>
<xsl:for-each select="/plist/dict/array[preceding-sibling::key[text()='Playlists']]/dict">
<xsl:element name="playlist">

<xsl:for-each select="key[text()='Playlist ID'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::integer/text()"/></xsl:variable>
<xsl:attribute name="playlistId"><xsl:value-of select="$text"/></xsl:attribute>
</xsl:for-each>

<xsl:for-each select="key[text()='Folder'][1]">
<xsl:variable name="theCount"><xsl:value-of select="count(following-sibling::true)"/></xsl:variable>
<xsl:attribute name="isFolder">
<xsl:if test="$theCount!='0'">
<xsl:text>true</xsl:text>
</xsl:if>
</xsl:attribute>
</xsl:for-each>

<xsl:for-each select="key[text()='Parent Persistent ID'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::string/text()"/></xsl:variable>
<xsl:attribute name="parentPersistentId"><xsl:value-of select="$text"/></xsl:attribute>
</xsl:for-each>

<xsl:for-each select="key[text()='Playlist Persistent ID'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::string/text()"/></xsl:variable>
<xsl:attribute name="playlistPersistentId"><xsl:value-of select="$text"/></xsl:attribute>
</xsl:for-each>

<xsl:for-each select="key[text()='Master'][1]">
<xsl:variable name="theCount"><xsl:value-of select="count(following-sibling::true)"/></xsl:variable>
<xsl:attribute name="isMaster">
<xsl:if test="$theCount!='0'">
<xsl:text>true</xsl:text>
</xsl:if>
</xsl:attribute>
</xsl:for-each>

<xsl:for-each select="key[text()='Distinguished Kind'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::integer/text()"/></xsl:variable>
<xsl:attribute name="distinguishedKind"><xsl:value-of select="$text"/></xsl:attribute>
</xsl:for-each>

<xsl:for-each select="key[starts-with(text(),'Smart')][1]">
<xsl:attribute name="hasSmart"><xsl:text>true</xsl:text></xsl:attribute>
</xsl:for-each>

<xsl:text>
</xsl:text>

<xsl:for-each select="key[text()='Name'][1]">
<xsl:variable name="text"><xsl:value-of select="following-sibling::string/text()"/></xsl:variable>
<xsl:element name="name"><xsl:value-of select="$text"/></xsl:element><xsl:text>
</xsl:text>
</xsl:for-each>

<xsl:for-each select="array/dict/key[text()='Track ID']">
<xsl:variable name="text"><xsl:value-of select="following-sibling::integer/text()"/></xsl:variable>
<xsl:element name="track"><xsl:value-of select="$text"/></xsl:element><xsl:text>
</xsl:text>
</xsl:for-each>
</xsl:element><xsl:text>
</xsl:text>
</xsl:for-each>
</xsl:template>

</xsl:stylesheet>