<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <!-- Parameters -->
  <xsl:param name="package"/>
  <xsl:param name="class"/>
  <xsl:param name="value"/>
  <!-- Identity template: copies everything as is by default -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <!-- Template to add <ncc> tag inside <class> if package and class match parameters -->
  <xsl:template match="package[@id=$package]//class[@id=$class]">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
      <ncc>
        <xsl:value-of select="$value"/>
      </ncc>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>
