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
      <xsl:text>All </xsl:text>
      <xsl:value-of select="count(recent/repo)"/>
    </title>
    <script src="//code.jquery.com/jquery-3.2.1.min.js">
      <!-- nothing -->
    </script>
    <script type="text/javascript">
      <xsl:text>
        function ping_them_all() {
          $('.ping').each(function() {
            var $span = $(this);
            $.get(
              $span.attr('data-uri'),
              function(data, status) {
                $span.text(status);
              }
            );
          });
        }
      </xsl:text>
    </script>
  </xsl:template>
  <xsl:template match="/page" mode="body">
    <xsl:apply-templates select="recent"/>
  </xsl:template>
  <xsl:template match="recent">
    <p>
      <xsl:text>There are </xsl:text>
      <xsl:value-of select="count(repo)"/>
      <xsl:text> artifacts in our database (</xsl:text>
      <a href="#" onclick="ping_them_all(); return false;">
        <xsl:text>ping them all</xsl:text>
      </a>
      <xsl:text>):</xsl:text>
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
            <xsl:text>Rank</xsl:text>
          </th>
          <th>
            <xsl:text>Score</xsl:text>
          </th>
          <th>
            <xsl:text>Defects</xsl:text>
          </th>
          <th>
            <xsl:text>Version</xsl:text>
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
      <td>
        <xsl:attribute name="style">
          <xsl:text>text-align:right;</xsl:text>
          <xsl:if test="classes &gt; 200 and defects &lt; 0.15">
            <xsl:text>background-color:#ACE1AF;</xsl:text>
          </xsl:if>
        </xsl:attribute>
        <a href="/{group}/{artifact}">
          <xsl:value-of select="classes"/>
        </a>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(rank,'0.00')"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(score,'0.00')"/>
      </td>
      <td>
        <xsl:attribute name="style">
          <xsl:text>text-align:right;</xsl:text>
          <xsl:text>color:</xsl:text>
          <xsl:choose>
            <xsl:when test="defects &lt; 0.15">
              <xsl:text>green</xsl:text>
            </xsl:when>
            <xsl:when test="defects &lt; 0.25">
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
      <td title="{@added}">
        <span class="ping" data-uri="https://i.jpeek.org/{group}/{artifact}/index.html">
          <xsl:value-of select="version"/>
        </span>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
