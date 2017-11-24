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
    <script src="https://code.jquery.com/jquery-3.2.1.min.js">
      <!-- nothing -->
    </script>
    <title>
      <xsl:text>Ping...</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="/page" mode="body">
    <p>
      <xsl:text>There are </xsl:text>
      <xsl:value-of select="count(recent/repo)"/>
      <xsl:text> artifact:</xsl:text>
    </p>
    <xsl:apply-templates select="recent/repo"/>
    <script>
        $(function () {
          $('.ping').each(function() {
            var $span = $(this);
            $.get(
              $span.attr('data-uri'),
              function(data, status) {
                $span.text(status);
              }
            );
          })
        })
      </script>
  </xsl:template>
  <xsl:template match="recent/repo">
    <p>
      <a href="{group}/{artifact}/">
        <xsl:value-of select="group"/>
        <xsl:text>:</xsl:text>
        <xsl:value-of select="artifact"/>
      </a>
      <xsl:text>: </xsl:text>
      <span class="ping" data-uri="http://i.jpeek.org/{group}/{artifact}/index.html">
        <xsl:text>...</xsl:text>
      </span>
    </p>
  </xsl:template>
</xsl:stylesheet>
