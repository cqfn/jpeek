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
        <style>
          body {
            padding: 1em;
          }
          sup {
            top: -0.5em;
            font-size: 75%;
            line-height: 0;
            position: relative;
            vertical-align: baseline;
          }
          .under {
            font-size: 75%;
            display: block;
            text-align: right;
            color: gray;
          }
        </style>
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
    <p>
      <xsl:text>Overall score</xsl:text>
      <a href="#1">
        <sup>
          <xsl:text>1</xsl:text>
        </sup>
      </a>
      <xsl:text> is </xsl:text>
      <strong>
        <xsl:value-of select="format-number(sum(metric/score) div count(metric),'0.00')"/>
      </strong>
      <xsl:text> out of 10.</xsl:text>
      <xsl:text> Here is the </xsl:text>
      <a href="matrix.html">
        <xsl:text>matrix</xsl:text>
      </a>
      <xsl:text>.</xsl:text>
    </p>
    <p>
      <img src="badge.svg" alt="SVG badge"/>
    </p>
    <table>
      <thead>
        <tr>
          <th>
            <xsl:text>Metric</xsl:text>
          </th>
          <th>
            <xsl:text>Classes</xsl:text>
            <a href="#2">
              <sup>
                <xsl:text>2</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Average</xsl:text>
            <a href="#3">
              <sup>
                <xsl:text>3</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Min</xsl:text>
          </th>
          <th>
            <xsl:text>Max</xsl:text>
          </th>
          <th>
            <xsl:text>Green</xsl:text>
            <a href="#4">
              <sup>
                <xsl:text>4</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Yellow</xsl:text>
            <a href="#4">
              <sup>
                <xsl:text>4</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Red</xsl:text>
            <a href="#4">
              <sup>
                <xsl:text>4</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Score</xsl:text>
            <a href="#5">
              <sup>
                <xsl:text>5</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Options</xsl:text>
          </th>
        </tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="metric">
          <xsl:sort select="@name" order="ascending" data-type="text"/>
        </xsl:apply-templates>
      </tbody>
    </table>
    <p>
      <sup id="1">
        <xsl:text>1</xsl:text>
      </sup>
      <xsl:text>Overall score is an arithmetic average of all metric
        scores from the "Score" column in the table. </xsl:text>
      <sup id="2">
        <xsl:text>2</xsl:text>
      </sup>
      <xsl:text>"Classes" shows the total amount of classes measured by this metric,
        in most cases the number will be the same for all metrics. </xsl:text>
      <sup id="3">
        <xsl:text>3</xsl:text>
      </sup>
      <xsl:text>Mathematical average of all measurements, which
        doesn't give too much information about the quality of code,
        but is visible here for statistical purposes;
        we recommend to pay attention to the "Score" column. </xsl:text>
      <sup id="4">
        <xsl:text>4</xsl:text>
      </sup>
      <xsl:text>"Green," "Yellow," and "Red" columns show how many
        classes received that colors on their measurements;
        obviously, red classes are those which have lower quality;
        to see them just click the name of the metric in the first
        column. </xsl:text>
      <sup id="5">
        <xsl:text>5</xsl:text>
      </sup>
      <xsl:text>"Score" is a weighted average of the numbers
        from the "Green," "Yellow," and "Red" columns;
        the weight of green classes is 1.0, yellow ones get 0.25,
        and red ones get 0.05; if the color of the number is
        green, the quality is high enough, if it's orange the quality
        is average, if it's red, the quality is too low, for this
        particular metric. </xsl:text>
    </p>
    <p style="color:gray;font-size:75%;">
      <xsl:text>This report was generated by </xsl:text>
      <a href="http://www.jpeek.org">
        <xsl:text>jpeek </xsl:text>
        <xsl:value-of select="@version"/>
      </a>
      <xsl:text> on </xsl:text>
      <xsl:value-of select="@date"/>
      <xsl:text>.</xsl:text>
    </p>
  </xsl:template>
  <xsl:template match="metric">
    <tr>
      <td>
        <a href="{html}">
          <xsl:value-of select="@name"/>
        </a>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="classes"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(average,'0.00')"/>
      </td>
      <td style="text-align:right">
        <xsl:choose>
          <xsl:when test="min">
            <xsl:value-of select="format-number(min,'0.00')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>-</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td style="text-align:right">
        <xsl:choose>
          <xsl:when test="max">
            <xsl:value-of select="format-number(max,'0.00')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>-</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="green"/>
        <span class="under">
          <xsl:value-of select="format-number((green div classes) * 100, '#')"/>
          <xsl:text>%</xsl:text>
        </span>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="yellow"/>
        <span class="under">
          <xsl:value-of select="format-number((yellow div classes) * 100, '#')"/>
          <xsl:text>%</xsl:text>
        </span>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="red"/>
        <span class="under">
          <xsl:value-of select="format-number((red div classes) * 100, '#')"/>
          <xsl:text>%</xsl:text>
        </span>
      </td>
      <td>
        <xsl:attribute name="style">
          <xsl:text>text-align:right;</xsl:text>
          <xsl:text>font-weight:bold;</xsl:text>
          <xsl:text>color:</xsl:text>
          <xsl:choose>
            <xsl:when test="score &gt; 7">
              <xsl:text>green</xsl:text>
            </xsl:when>
            <xsl:when test="score &gt; 4">
              <xsl:text>orange</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>red</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        <xsl:value-of select="format-number(score,'0.00')"/>
      </td>
      <td>
        <a href="{xml}">
          <xsl:text>XML</xsl:text>
        </a>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
