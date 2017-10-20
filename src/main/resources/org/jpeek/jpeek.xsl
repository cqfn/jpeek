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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="1.0">
  <xsl:template match="/">
    <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
    <html lang="en">
      <head>
        <meta charset="UTF-8"/>
        <meta name="description" content="jpeek metric"/>
        <meta name="keywords" content="code quality metrics"/>
        <meta name="author" content="jpeek.org"/>
        <link rel="stylesheet" href="http://cdn.rawgit.com/yegor256/tacit/gh-pages/tacit-css-1.1.1.min.css"/>
        <title>
          <xsl:text>jpeek</xsl:text>
        </title>
        <style type="text/css">
        </style>
      </head>
      <body>
        <section>
          <xsl:apply-templates select="app"/>
        </section>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="app">
    <p>
      <a href="http://www.jpeek.org">
        <img src="http://www.jpeek.org/logo.svg" style="height:60px"/>
      </a>
    </p>
    <h1>
      <xsl:value-of select="@title"/>
    </h1>
    <table>
      <colgroup>
        <col/>
        <col/>
      </colgroup>
      <thead>
        <tr>
          <th>
            <xsl:text>ID</xsl:text>
          </th>
          <th style="text-align:right">
            <xsl:text>Value</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="package"/>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="package">
    <tr>
      <td>
        <code>
          <strong>
            <xsl:value-of select="@id"/>
          </strong>
        </code>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="@value"/>
      </td>
    </tr>
    <xsl:apply-templates select="class"/>
  </xsl:template>
  <xsl:template match="class">
    <tr>
      <td>
        <code>
          <xsl:value-of select="@id"/>
        </code>
      </td>
      <td>
        <xsl:if test="@color">
          <xsl:attribute name="style">
            <xsl:text>text-align:right;</xsl:text>
            <xsl:text>color:</xsl:text>
            <xsl:choose>
              <xsl:when test="@color='red'">
                <xsl:text>red</xsl:text>
              </xsl:when>
              <xsl:when test="@color='green'">
                <xsl:text>green</xsl:text>
              </xsl:when>
              <xsl:when test="@color='yellow'">
                <xsl:text>orange</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>inherit</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:text>;</xsl:text>
          </xsl:attribute>
        </xsl:if>
        <xsl:value-of select="@value"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
