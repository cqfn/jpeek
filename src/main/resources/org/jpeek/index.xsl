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
  <xsl:template match="/">
    <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
    <html lang="en">
      <head>
        <meta charset="UTF-8"/>
        <meta name="description" content="jpeek metrics"/>
        <meta name="keywords" content="code quality metrics"/>
        <meta name="author" content="jpeek.org"/>
        <link rel="shortcut icon" href="http://www.jpeek.org/logo.png"/>
        <link rel="stylesheet" href="http://cdn.rawgit.com/yegor256/tacit/gh-pages/tacit-css-1.1.1.min.css"/>
        <title>
          <xsl:text>jpeek</xsl:text>
        </title>
      </head>
      <body>
        <xsl:apply-templates select="metrics"/>
      </body>
    </html>
  </xsl:template>
  <xsl:template match="metrics">
    <p>
      <a href="http://www.jpeek.org">
        <img alt="logo" src="http://www.jpeek.org/logo.svg" style="height:60px"/>
      </a>
    </p>
    <h1>
      <xsl:text>jpeek</xsl:text>
    </h1>
    <ul>
      <xsl:apply-templates select="metric"/>
    </ul>
  </xsl:template>
  <xsl:template match="metric">
    <li>
      <a href="{@html}">
        <xsl:value-of select="."/>
      </a>
      <xsl:text> (</xsl:text>
      <a href="{@xml}">
        <xsl:text>XML</xsl:text>
      </a>
      <xsl:text>)</xsl:text>
    </li>
    <xsl:apply-templates select="class"/>
  </xsl:template>
</xsl:stylesheet>
