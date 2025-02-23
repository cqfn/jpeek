<?xml version="1.0"?>
<!--
 * Copyright (c) 2017-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:template match="/page">
    <html lang="en">
      <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <meta name="description" content="jpeek"/>
        <meta name="keywords" content="code quality metrics"/>
        <meta name="author" content="jpeek.org"/>
        <link rel="shortcut icon" href="//www.jpeek.org/logo.png"/>
        <link rel="stylesheet" href="//cdn.jsdelivr.net/gh/yegor256/tacit@gh-pages/tacit-css.min.css"/>
        <link rel="stylesheet" href="/jpeek.css"/>
        <script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/sortable/0.8.0/js/sortable.min.js">
          <!-- nothing -->
        </script>
        <xsl:apply-templates select="." mode="head"/>
      </head>
      <body>
        <p>
          <a href="https://i.jpeek.org">
            <img alt="logo" src="//www.jpeek.org/logo.svg" style="height:60px"/>
          </a>
        </p>
        <xsl:apply-templates select="." mode="body"/>
        <p style="color:gray;font-size:75%;">
          <xsl:text>This is </xsl:text>
          <a href="https://www.jpeek.org">
            <xsl:text>jpeek </xsl:text>
            <xsl:value-of select="@version"/>
          </a>
          <xsl:text> here, it took </xsl:text>
          <xsl:value-of select="millis"/>
          <xsl:text>ms to build this page;</xsl:text>
          <xsl:text> server time is </xsl:text>
          <xsl:value-of select="@date"/>
          <xsl:text>.</xsl:text>
        </p>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
