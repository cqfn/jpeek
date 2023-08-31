<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2023 Yegor Bugayenko

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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/2000/svg" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes"/>
  <xsl:template match="/badge">
    <svg width="76" height="20">
      <xsl:if test="@style = 'round'">
        <linearGradient id="b" x2="0" y2="100%">
          <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
          <stop offset="1" stop-opacity=".1"/>
        </linearGradient>
      </xsl:if>
      <mask id="a">
        <rect width="76" height="20" fill="#fff">
          <xsl:if test="@style = 'round'">
            <xsl:attribute name="rx">
              <xsl:text>3</xsl:text>
            </xsl:attribute>
          </xsl:if>
        </rect>
      </mask>
      <g mask="url(#a)">
        <path fill="#555" d="M0 0h27v20H0z"/>
        <path d="M27 0h77v20H27z">
          <xsl:attribute name="fill">
            <xsl:choose>
              <xsl:when test="@unknown">
                <xsl:text>#ccc</xsl:text>
              </xsl:when>
              <xsl:when test=". &gt; 8">
                <xsl:text>#44cc11</xsl:text>
              </xsl:when>
              <xsl:when test=". &lt; 5">
                <xsl:text>#d9644d</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>#dfb317</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </path>
      </g>
      <g fill="#fff" text-anchor="middle" font-family="DejaVu Sans,Verdana,Geneva,sans-serif" font-size="11">
        <xsl:if test="@style = 'round'">
          <g transform="translate(6, 2.5)" stroke="#1a1a1a">
            <path d="M3.18327974,5.11254019 L6.94533762,12.829582" id="Line" stroke-linecap="square"/>
            <circle id="Oval" fill="#1a1a1a" fill-rule="nonzero" cx="7.11414791" cy="12.9501608" r="2.00160772"/>
            <path d="M4.9437299,4.0755627 L10.562701,1.01286174" id="Line" stroke-linecap="square"/>
            <path d="M5.13665595,5.23311897 L12.829582,6.46302251" id="Line" stroke-linecap="square"/>
            <circle id="Oval" fill="#1a1a1a" fill-rule="nonzero" cx="3.2073955" cy="5.04019293" r="3.2073955"/>
            <circle id="Oval" fill="#1a1a1a" fill-rule="nonzero" cx="10.2491961" cy="1.13344051" r="1.13344051"/>
            <circle id="Oval" fill="#1a1a1a" fill-rule="nonzero" cx="12.7813505" cy="6.46302251" r="2.21864952"/>
          </g>
        </xsl:if>
        <g transform="translate(6, 2.5)" stroke="#FFFFFF">
          <path d="M3.18327974,5.11254019 L6.94533762,12.829582" id="Line" stroke-linecap="square"/>
          <circle id="Oval" fill="#FFFFFF" fill-rule="nonzero" cx="7.11414791" cy="12.9501608" r="2.00160772"/>
          <path d="M4.9437299,4.0755627 L10.562701,1.01286174" id="Line" stroke-linecap="square"/>
          <path d="M5.13665595,5.23311897 L12.829582,6.46302251" id="Line" stroke-linecap="square"/>
          <circle id="Oval" fill="#FFFFFF" fill-rule="nonzero" cx="3.2073955" cy="5.04019293" r="3.2073955"/>
          <circle id="Oval" fill="#FFFFFF" fill-rule="nonzero" cx="10.2491961" cy="1.13344051" r="1.13344051"/>
          <circle id="Oval" fill="#FFFFFF" fill-rule="nonzero" cx="12.7813505" cy="6.46302251" r="2.21864952"/>
        </g>
        <xsl:if test="@style = 'round'">
          <text x="72.5" y="15" fill="#010101" fill-opacity=".3" text-anchor="end">
            <xsl:value-of select="format-number(.,'0.0000')"/>
          </text>
        </xsl:if>
        <text x="72.5" y="14" text-anchor="end">
          <xsl:value-of select="format-number(.,'0.0000')"/>
        </text>
      </g>
    </svg>
  </xsl:template>
</xsl:stylesheet>
