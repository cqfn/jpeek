<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2017-2026 Yegor Bugayenko
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
      <xsl:choose>
        <xsl:when test="msec &gt; 1000">
          <xsl:text>We have been working on </xsl:text>
          <code>
            <xsl:value-of select="group"/>
            <xsl:text>:</xsl:text>
            <xsl:value-of select="artifact"/>
          </code>
          <xsl:text> for </xsl:text>
          <strong>
            <xsl:value-of select="spent"/>
          </strong>
          <xsl:text> already</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>We just started to prepare the report for </xsl:text>
          <code>
            <xsl:value-of select="group"/>
            <xsl:text>:</xsl:text>
            <xsl:value-of select="artifact"/>
          </code>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:text>, please reload this page in a few minutes...</xsl:text>
    </p>
    <p>
      <xsl:text>Future: </xsl:text>
      <code>
        <xsl:value-of select="future"/>
      </code>
    </p>
  </xsl:template>
</xsl:stylesheet>
