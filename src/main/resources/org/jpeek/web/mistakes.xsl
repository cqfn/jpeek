<?xml version="1.0"?>
<!--
The MIT License (MIT)

Copyright (c) 2017-2018 Yegor Bugayenko

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
      <xsl:text>Mistakes</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="/page" mode="body">
    <xsl:apply-templates select="worst"/>
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
            <xsl:text>Seen in</xsl:text>
            <a href="#1">
              <sup>
                <xsl:text>1</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Neg</xsl:text>
            <a href="#2">
              <sup>
                <xsl:text>2</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Total</xsl:text>
            <a href="#3">
              <sup>
                <xsl:text>3</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Pos</xsl:text>
            <a href="#2">
              <sup>
                <xsl:text>2</xsl:text>
              </sup>
            </a>
          </th>
          <th style="text-align:right;">
            <a href="https://en.wikipedia.org/wiki/Mean">
              <xsl:text>&#x3BC;</xsl:text>
            </a>
            <a href="#4">
              <sup>
                <xsl:text>4</xsl:text>
              </sup>
            </a>
          </th>
          <th style="text-align:right;">
            <a href="https://en.wikipedia.org/wiki/Standard_deviation">
              <xsl:text>&#x3C3;</xsl:text>
            </a>
            <a href="#4">
              <sup>
                <xsl:text>4</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Artifact</xsl:text>
            <a href="#5">
              <sup>
                <xsl:text>5</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Champions</xsl:text>
            <a href="#6">
              <sup>
                <xsl:text>6</xsl:text>
              </sup>
            </a>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="metric">
          <xsl:sort select="@id"/>
        </xsl:apply-templates>
      </tbody>
    </table>
    <p>
      <sup id="1">
        <xsl:text>1</xsl:text>
      </sup>
      <xsl:text>This is a total number of artifacts this metric was seen in. </xsl:text>
      <sup id="2">
        <xsl:text>2</xsl:text>
      </sup>
      <xsl:text>"Neg" and "Pos" demonstrate average mistakes this metric
        makes to either negative or positive direction. </xsl:text>
      <sup id="3">
        <xsl:text>3</xsl:text>
      </sup>
      <xsl:text>"Total" is the overall mistake value calculated as
        a square between "Neg" and "Pos" weighed by the total number of
        project they were seen in.</xsl:text>
      <sup id="4">
        <xsl:text>4</xsl:text>
      </sup>
      <xsl:text>&#x3BC; and &#x3C3; are taken from the projects where
        the amount of defects is less than 10% and the amount of
        classes is more than 200.</xsl:text>
      <sup id="5">
        <xsl:text>5</xsl:text>
      </sup>
      <xsl:text>"Artifact" is the name of the artifact where
        the best &#x3BC; and &#x3C3; were found.</xsl:text>
      <sup id="6">
        <xsl:text>6</xsl:text>
      </sup>
      <xsl:text>"Champions" is the amount of projects we've tried
        in order to select the best &#x3BC; and &#x3C3;.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="metric">
    <tr>
      <td>
        <xsl:value-of select="@id"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="pos + neg"/>
      </td>
      <td style="text-align:right">
        <xsl:if test="navg &gt; 0">
          <xsl:text>-</xsl:text>
          <xsl:value-of select="format-number(navg * 100, '#')"/>
          <xsl:text>%</xsl:text>
          <span class="under">
            <xsl:value-of select="neg"/>
          </span>
        </xsl:if>
      </td>
      <td>
        <xsl:attribute name="style">
          <xsl:text>text-align:right;</xsl:text>
          <xsl:text>color:</xsl:text>
          <xsl:choose>
            <xsl:when test="avg &lt; 0.10">
              <xsl:text>green</xsl:text>
            </xsl:when>
            <xsl:when test="avg &lt; 0.20">
              <xsl:text>orange</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>red</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:value-of select="format-number(avg * 100, '#')"/>
        <xsl:text>%</xsl:text>
      </td>
      <td style="text-align:right">
        <xsl:if test="pavg &gt; 0">
          <xsl:text>+</xsl:text>
          <xsl:value-of select="format-number(pavg * 100, '#')"/>
          <xsl:text>%</xsl:text>
          <span class="under">
            <xsl:value-of select="pos"/>
          </span>
        </xsl:if>
      </td>
      <td style="text-align:right">
        <a href="{artifact}">
          <xsl:value-of select="format-number(mean, '0.00')"/>
        </a>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(sigma, '0.00')"/>
      </td>
      <td>
        <code>
          <xsl:value-of select="artifact"/>
        </code>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="champions"/>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
