<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:import href="/org/jpeek/web/layout.xsl"/>
  <xsl:template match="/page" mode="head">
    <title>
      <xsl:value-of select="group"/>
      <xsl:text>:</xsl:text>
      <xsl:value-of select="artifact"/>
    </title>
  </xsl:template>
  <xsl:template match="/page" mode="body">
    <p>
      <xsl:text>We failed to to prepare the report for </xsl:text>
      <code>
        <xsl:value-of select="group"/>
        <xsl:text>:</xsl:text>
        <xsl:value-of select="artifact"/>
      </code>
      <xsl:text>, we are very sorry about this.</xsl:text>
    </p>
    <pre>
      <xsl:value-of select="stacktrace"/>
    </pre>
  </xsl:template>
</xsl:stylesheet>
