<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:import href="/org/jpeek/templates.xsl"/>
  <xsl:template match="/">
    <xsl:apply-templates select="skeleton"/>
  </xsl:template>
  <xsl:template match="skeleton">
    <xsl:apply-templates select="//class"/>
  </xsl:template>
  <xsl:template match="class">
    <p>
      <code>
        <xsl:value-of select="@id"/>
      </code>
    </p>
  </xsl:template>
</xsl:stylesheet>
