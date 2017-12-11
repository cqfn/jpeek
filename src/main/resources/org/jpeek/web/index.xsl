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
      <xsl:text>Jpeek</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="/page" mode="body">
    <p>
      <xsl:text>You can test any artifact just by pointing your browser to </xsl:text>
      <code>
        <xsl:text>http://i.jpeek.org/{group}/{artifact}/</xsl:text>
      </code>
      <xsl:text>, where </xsl:text>
      <code>
        <xsl:text>{group}</xsl:text>
      </code>
      <xsl:text> is the group part of your Maven coordinates and </xsl:text>
      <code>
        <xsl:text>{artifact}</xsl:text>
      </code>
      <xsl:text> is the artifact name.</xsl:text>
      <xsl:text> For example, for </xsl:text>
      <code>
        <xsl:text>org.takes:takes</xsl:text>
      </code>
      <xsl:text> it is </xsl:text>
      <code>
        <xsl:text>http://i.jpeek.org/org.takes/takes/</xsl:text>
      </code>
      <xsl:text>. Got the idea?</xsl:text>
      <xsl:text> Please, remember that every time we deploy a new</xsl:text>
      <xsl:text> version of jpeek, the list gets cleaned up.</xsl:text>
      <xsl:text> To refresh your report, just re-open the URL.</xsl:text>
    </p>
    <xsl:apply-templates select="best"/>
    <p>
      <xsl:text>See metric mistakes </xsl:text>
      <a href="/mistakes">
        <xsl:text>summary</xsl:text>
      </a>
      <xsl:text> and the </xsl:text>
      <a href="/queue">
        <xsl:text>queue</xsl:text>
      </a>
      <xsl:text>.</xsl:text>
    </p>
    <xsl:apply-templates select="recent"/>
  </xsl:template>
  <xsl:template match="best[not(repo)]">
    <p>
      <xsl:text>There is nothing here yet.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="best[repo]">
    <p>
      <xsl:text>This is the list of the best </xsl:text>
      <xsl:value-of select="count(repo)"/>
      <xsl:text> artifacts we've seen recently</xsl:text>
      <xsl:text> (only if they have over 100 classes, they get into this list):</xsl:text>
    </p>
    <table data-sortable="true">
      <thead>
        <tr>
          <th>
            <xsl:text>&#xA0;</xsl:text>
          </th>
          <th>
            <xsl:text>Artifact</xsl:text>
          </th>
          <th>
            <xsl:text>Classes</xsl:text>
          </th>
          <th>
            <xsl:text>Elements</xsl:text>
          </th>
          <th>
            <xsl:text>Rank</xsl:text>
          </th>
          <th>
            <xsl:text>Score</xsl:text>
          </th>
          <th>
            <xsl:text>Mistake</xsl:text>
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
    <p>
      <xsl:text>Average mistake: </xsl:text>
      <xsl:value-of select="format-number(100 * sum(repo/diff) div count(repo/diff),'#')"/>
      <xsl:text>%.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="best/repo">
    <tr>
      <td>
        <xsl:text>#</xsl:text>
        <xsl:value-of select="position()"/>
      </td>
      <td>
        <code>
          <xsl:value-of select="group"/>
          <xsl:text>:</xsl:text>
          <xsl:value-of select="artifact"/>
        </code>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="classes"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="elements"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(rank,'0.00')"/>
      </td>
      <td style="text-align:right">
        <a href="{group}/{artifact}/">
          <xsl:value-of select="format-number(score,'0.00')"/>
        </a>
      </td>
      <td>
        <xsl:attribute name="style">
          <xsl:text>text-align:right;</xsl:text>
          <xsl:text>color:</xsl:text>
          <xsl:choose>
            <xsl:when test="diff &lt; 0.10">
              <xsl:text>green</xsl:text>
            </xsl:when>
            <xsl:when test="diff &lt; 0.20">
              <xsl:text>orange</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>red</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:value-of select="format-number(100 * diff,'#')"/>
        <xsl:text>%</xsl:text>
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
  <xsl:template match="recent[repo]">
    <p>
      <xsl:text>Recently analyzed: </xsl:text>
      <xsl:for-each select="repo">
        <xsl:if test="position() &gt; 1">
          <xsl:text>, </xsl:text>
        </xsl:if>
        <a href="{group}/{artifact}/">
          <xsl:value-of select="group"/>
          <xsl:text>:</xsl:text>
          <xsl:value-of select="artifact"/>
        </a>
      </xsl:for-each>
      <xsl:text>, </xsl:text>
      <a href="/all">
        <xsl:text>...</xsl:text>
      </a>
    </p>
  </xsl:template>
</xsl:stylesheet>
