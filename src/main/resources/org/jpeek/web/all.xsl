<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017 Yegor Bugayenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:output method="html" doctype-system="about:legacy-compat" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:import href="/org/jpeek/web/layout.xsl"/>
  <xsl:template match="/page" mode="head">
    <title>
      <xsl:text>All </xsl:text>
      <xsl:value-of select="count(recent/repo)"/>
    </title>
  </xsl:template>
  <xsl:template match="/page" mode="body">
    <xsl:apply-templates select="recent"/>
  </xsl:template>
  <xsl:template match="recent">
    <p>
      <xsl:text>All </xsl:text>
      <xsl:value-of select="count(repo)"/>
      <xsl:text> artifacts in our database:</xsl:text>
    </p>
    <table data-sortable="true">
      <thead>
        <tr>
          <th>
            <xsl:text>Artifact</xsl:text>
          </th>
          <th>
            <xsl:text>Classes</xsl:text>
          </th>
          <th>
            <xsl:text>Defects</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="repo"/>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="recent/repo">
    <tr>
      <td>
        <code>
          <xsl:value-of select="group"/>
          <xsl:text>:</xsl:text>
          <xsl:value-of select="artifact"/>
        </code>
      </td>
      <td style="text-align:right">
        <a href="/{group}/{artifact}">
          <xsl:value-of select="classes"/>
        </a>
      </td>
      <td>
        <xsl:attribute name="style">
          <xsl:text>text-align:right;</xsl:text>
          <xsl:text>color:</xsl:text>
          <xsl:choose>
            <xsl:when test="defects &lt; 0.10">
              <xsl:text>green</xsl:text>
            </xsl:when>
            <xsl:when test="defects &lt; 0.20">
              <xsl:text>orange</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>red</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:value-of select="format-number(100 * defects,'#')"/>
        <xsl:text>%</xsl:text>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
