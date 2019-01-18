<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2019 Yegor Bugayenko

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
  <xsl:import href="/org/jpeek/templates.xsl"/>
  <xsl:template match="/">
    <xsl:call-template name="html">
      <xsl:with-param name="title" select="metric/title"/>
      <xsl:with-param name="body" select="metric"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="metric">
    <p>
      <a href="index.html">
        <xsl:text>Back to index</xsl:text>
      </a>
    </p>
    <h1>
      <xsl:value-of select="title"/>
    </h1>
    <p>
      <xsl:text>Min: </xsl:text>
      <xsl:value-of select="format-number(min,'0.0000')"/>
      <xsl:text>, max: </xsl:text>
      <xsl:value-of select="format-number(max,'0.0000')"/>
      <xsl:text>, yellow zone: </xsl:text>
      <code>
        <xsl:value-of select="colors"/>
      </code>
      <xsl:text>.</xsl:text>
    </p>
    <xsl:apply-templates select="app/@value"/>
    <xsl:apply-templates select="statistics"/>
    <p>
      <xsl:text>Packages: </xsl:text>
      <xsl:value-of select="count(//package)"/>
      <xsl:text>, classes: </xsl:text>
      <xsl:value-of select="count(//class)"/>
      <xsl:text>.</xsl:text>
    </p>
    <p>
      <xsl:text>Green: </xsl:text>
      <xsl:value-of select="count(//class[@color='green'])"/>
      <xsl:text>, yellow: </xsl:text>
      <xsl:value-of select="count(//class[@color='yellow'])"/>
      <xsl:text>, red: </xsl:text>
      <xsl:value-of select="count(//class[@color='red'])"/>
      <xsl:text>.</xsl:text>
    </p>
    <p>
      <xsl:call-template name="bars">
        <xsl:with-param name="bars" select="bars"/>
        <xsl:with-param name="w" select="min((max((240, count(bars/bar) * 5)), 1024))"/>
        <xsl:with-param name="h" select="64"/>
      </xsl:call-template>
    </p>
    <xsl:apply-templates select="app"/>
    <p>
      <xsl:text>Download </xsl:text>
      <a href="{title}.xml">
        <xsl:text>XML</xsl:text>
      </a>
      <xsl:text>.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="statistics">
    <p>
      <xsl:text>Elements: </xsl:text>
      <xsl:value-of select="elements"/>
      <xsl:text>, </xsl:text>
      <a href="https://en.wikipedia.org/wiki/Mean">
        <xsl:text>&#x3BC;</xsl:text>
      </a>
      <xsl:text>: </xsl:text>
      <xsl:value-of select="format-number(mean, '0.0000')"/>
      <xsl:text>, </xsl:text>
      <a href="https://en.wikipedia.org/wiki/Standard_deviation">
        <xsl:text>&#x3C3;</xsl:text>
      </a>
      <xsl:text>: </xsl:text>
      <xsl:value-of select="format-number(sigma, '0.0000')"/>
      <xsl:text>, </xsl:text>
      <a href="https://en.wikipedia.org/wiki/Variance">
        <xsl:text>Var</xsl:text>
      </a>
      <xsl:text>: </xsl:text>
      <xsl:value-of select="format-number(variance, '0.0000')"/>
      <xsl:text>, defects: </xsl:text>
      <xsl:value-of select="format-number(defects * 100, '#')"/>
      <xsl:text>%</xsl:text>
      <xsl:if test="reverse='true'">
        <xsl:text>, reversed metric</xsl:text>
      </xsl:if>
      <xsl:text>.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="app/@value">
    <p>
      <xsl:text>App measurement: </xsl:text>
      <code>
        <xsl:value-of select="."/>
      </code>
      <xsl:text>.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="app[not(package/class)]">
    <p>
      <xsl:text>No measurements for packages.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="app[package/class]">
    <table data-sortable="true">
      <colgroup>
        <col/>
        <col/>
      </colgroup>
      <thead>
        <tr>
          <th>
            <xsl:text>Class</xsl:text>
          </th>
          <th style="text-align:right">
            <xsl:value-of select="/metric/title"/>
          </th>
          <xsl:for-each select="package[1]/class[1]/vars/var">
            <th style="text-align:right">
              <xsl:value-of select="@id"/>
            </th>
          </xsl:for-each>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="package"/>
      </tbody>
    </table>
  </xsl:template>
  <xsl:template match="package">
    <xsl:if test="@value">
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
    </xsl:if>
    <xsl:apply-templates select="class"/>
  </xsl:template>
  <xsl:template match="class">
    <tr>
      <td>
        <code title="{../@id}.{@id}">
          <xsl:value-of select="replace(../@id, '([a-z])[a-z0-9\$]+\.', '$1.')"/>
          <xsl:text>.</xsl:text>
          <xsl:value-of select="replace(@id, '([A-Z])[A-Za-z0-9]+\$', '$1..\$')"/>
        </code>
      </td>
      <td>
        <xsl:if test="@color">
          <xsl:attribute name="style">
            <xsl:text>text-align:right;</xsl:text>
            <xsl:text>color:</xsl:text>
            <xsl:call-template name="color">
              <xsl:with-param name="name">
                <xsl:choose>
                  <xsl:when test="@color='red'">
                    <xsl:text>red</xsl:text>
                  </xsl:when>
                  <xsl:when test="@color='green'">
                    <xsl:text>green</xsl:text>
                  </xsl:when>
                  <xsl:when test="@color='yellow'">
                    <xsl:text>yellow</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>default</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
            <xsl:text>;</xsl:text>
          </xsl:attribute>
        </xsl:if>
        <xsl:value-of select="@value"/>
      </td>
      <xsl:for-each select="vars/var">
        <td style="text-align:right">
          <xsl:value-of select="."/>
        </td>
      </xsl:for-each>
    </tr>
  </xsl:template>
</xsl:stylesheet>
