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
          <xsl:text>mistakes</xsl:text>
        </title>
      </head>
      <body>
        <p>
          <a href="http://i.jpeek.org">
            <img alt="logo" src="http://www.jpeek.org/logo.svg" style="height:60px"/>
          </a>
        </p>
        <xsl:apply-templates select="worst"/>
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
  <xsl:template match="worst[not(metric)]">
    <p>
      <xsl:text>There is nothing here yet.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="worst[metric]">
    <p>
      <xsl:text>This is the list of the worst </xsl:text>
      <xsl:value-of select="count(metric)"/>
      <xsl:text> metrics we've seen recently:</xsl:text>
    </p>
    <table data-sortable="true">
      <thead>
        <tr>
          <th>
            <xsl:text>Metric</xsl:text>
          </th>
          <th>
            <xsl:text>Pos</xsl:text>
          </th>
          <th>
            <xsl:text>P-avg</xsl:text>
          </th>
          <th>
            <xsl:text>Neg</xsl:text>
          </th>
          <th>
            <xsl:text>N-avg</xsl:text>
          </th>
          <th>
            <xsl:text>Avg</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="metric"/>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="metric">
    <tr>
      <td>
        <xsl:value-of select="@id"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="pos"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(pavg * 100, '#')"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="neg"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(navg * 100, '#')"/>
        <xsl:text>%</xsl:text>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(avg * 100, '#')"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
