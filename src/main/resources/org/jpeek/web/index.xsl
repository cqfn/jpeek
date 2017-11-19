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
  <xsl:template match="/index">
    <html lang="en">
      <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta name="description" content="jpeek"/>
        <meta name="keywords" content="code quality metrics"/>
        <meta name="author" content="jpeek.org"/>
        <link rel="shortcut icon" href="http://www.jpeek.org/logo.png"/>
        <link rel="stylesheet" href="http://cdn.rawgit.com/yegor256/tacit/gh-pages/tacit-css-1.1.1.min.css"/>
        <link rel="stylesheet" href="/jpeek.css"/>
        <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/sortable/0.8.0/js/sortable.min.js">&#xA0;</script>
        <title>
          <xsl:text>jpeek</xsl:text>
        </title>
      </head>
      <body>
        <p>
          <a href="http://i.jpeek.org">
            <img alt="logo" src="http://www.jpeek.org/logo.svg" style="height:60px"/>
          </a>
        </p>
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
          <xsl:text>See metrics </xsl:text>
          <a href="/mistakes">
            <xsl:text>summary</xsl:text>
          </a>
          <xsl:text> mistakes.</xsl:text>
        </p>
        <xsl:apply-templates select="recent"/>
        <footer style="color:gray;font-size:75%;">
          <p>
            <xsl:text>This is </xsl:text>
            <a href="http://www.jpeek.org">
              <xsl:text>jpeek </xsl:text>
              <xsl:value-of select="@version"/>
            </a>
            <xsl:text> here, it took </xsl:text>
            <xsl:value-of select="millis"/>
            <xsl:text>ms to build this page.</xsl:text>
          </p>
        </footer>
      </body>
    </html>
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
      <xsl:text> artifacts we've seen recently:</xsl:text>
    </p>
    <table data-sortable="true">
      <thead>
        <tr>
          <th>
            <xsl:text>Rank</xsl:text>
          </th>
          <th>
            <xsl:text>Artifact</xsl:text>
          </th>
          <th>
            <xsl:text>Classes</xsl:text>
          </th>
          <th>
            <xsl:text>Score</xsl:text>
          </th>
          <th>
            <xsl:text>Mistake</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="repo"/>
      </tbody>
    </table>
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
      <xsl:text>.</xsl:text>
    </p>
  </xsl:template>
</xsl:stylesheet>
