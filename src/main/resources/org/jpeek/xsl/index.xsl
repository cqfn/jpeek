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
      <xsl:with-param name="title">
        <xsl:text>jpeek</xsl:text>
      </xsl:with-param>
      <xsl:with-param name="body" select="index"/>
    </xsl:call-template>
  </xsl:template>
  <xsl:template match="index">
    <p>
      <xsl:text>Overall score</xsl:text>
      <a href="#1">
        <sup>
          <xsl:text>1</xsl:text>
        </sup>
      </a>
      <xsl:text> is </xsl:text>
      <strong>
        <xsl:value-of select="format-number(@score,'0.00')"/>
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
    <table data-sortable="true">
      <thead>
        <tr>
          <th>
            <xsl:text>Metric</xsl:text>
          </th>
          <th>
            <xsl:text>E/C</xsl:text>
            <a href="#2">
              <sup>
                <xsl:text>2</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <a href="https://en.wikipedia.org/wiki/Mean">
              <xsl:text>&#x3BC;</xsl:text>
            </a>
            <a href="#3">
              <sup>
                <xsl:text>3</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <a href="https://en.wikipedia.org/wiki/Standard_deviation">
              <xsl:text>&#x3C3;</xsl:text>
            </a>
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
            <xsl:text>Defects</xsl:text>
            <a href="#6">
              <sup>
                <xsl:text>6</xsl:text>
              </sup>
            </a>
          </th>
          <th>
            <xsl:text>Graph</xsl:text>
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
      <xsl:text>The average mistake of individual scores: </xsl:text>
      <strong>
        <xsl:attribute name="style">
          <xsl:text>color:</xsl:text>
          <xsl:call-template name="color">
            <xsl:with-param name="name">
              <xsl:choose>
                <xsl:when test="@diff &lt; 0.10">
                  <xsl:text>green</xsl:text>
                </xsl:when>
                <xsl:when test="@diff &lt; 0.20">
                  <xsl:text>yellow</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>red</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:value-of select="format-number(100 * @diff,'#')"/>
        <xsl:text>%</xsl:text>
      </strong>
      <xsl:text>, average defects rate: </xsl:text>
      <strong>
        <xsl:attribute name="style">
          <xsl:text>color:</xsl:text>
          <xsl:call-template name="color">
            <xsl:with-param name="name">
              <xsl:choose>
                <xsl:when test="@defects &lt; 0.10">
                  <xsl:text>green</xsl:text>
                </xsl:when>
                <xsl:when test="@defects &lt; 0.20">
                  <xsl:text>yellow</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>red</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:value-of select="format-number(100 * @defects,'#')"/>
        <xsl:text>%</xsl:text>
      </strong>
      <xsl:text>.</xsl:text>
    </p>
    <p>
      <sup id="1">
        <xsl:text>1</xsl:text>
      </sup>
      <xsl:text>Overall score is an arithmetic average of all metric
        scores from the "Score" column in the table. </xsl:text>
      <sup id="2">
        <xsl:text>2</xsl:text>
      </sup>
      <xsl:text>"E/C" shows the total amount of classes measured by this metric,
        in most cases the number will be the same for all metrics, and the
        elements our statistics pays attention to (we ignore max and min). </xsl:text>
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
        particular metric; in small font below the score value
        you can see how big is the difference between this metric
        score and the average score for the entire code base.</xsl:text>
      <sup id="6">
        <xsl:text>6</xsl:text>
      </sup>
      <xsl:text>"Defects" is the percentage of classes that fall out
        of one-sigma standard deviation interval.</xsl:text>
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
        <xsl:value-of select="elements"/>
        <xsl:text>/</xsl:text>
        <xsl:value-of select="classes"/>
        <xsl:if test="classes &gt; 0">
          <span class="under">
            <xsl:value-of select="format-number(elements div classes, '0')"/>
            <xsl:text>%</xsl:text>
          </span>
        </xsl:if>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(mean,'0.00')"/>
      </td>
      <td style="text-align:right">
        <xsl:value-of select="format-number(sigma,'0.00')"/>
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
      <td style="text-align:right" class="sorttable_numeric" sorttable_customkey="{green div classes}">
        <xsl:value-of select="green"/>
        <xsl:if test="classes &gt; 0">
          <span class="under">
            <xsl:value-of select="format-number((green div classes) * 100, '#')"/>
            <xsl:text>%</xsl:text>
          </span>
        </xsl:if>
      </td>
      <td style="text-align:right" class="sorttable_numeric" sorttable_customkey="{yellow div classes}">
        <xsl:value-of select="yellow"/>
        <xsl:if test="classes &gt; 0">
          <span class="under">
            <xsl:value-of select="format-number((yellow div classes) * 100, '#')"/>
            <xsl:text>%</xsl:text>
          </span>
        </xsl:if>
      </td>
      <td style="text-align:right" class="sorttable_numeric" sorttable_customkey="{red div classes}">
        <xsl:value-of select="red"/>
        <xsl:if test="classes &gt; 0">
          <span class="under">
            <xsl:value-of select="format-number((red div classes) * 100, '#')"/>
            <xsl:text>%</xsl:text>
          </span>
        </xsl:if>
      </td>
      <td class="sorttable_numeric" sorttable_customkey="{score}">
        <xsl:attribute name="style">
          <xsl:text>text-align:right;</xsl:text>
          <xsl:text>font-weight:bold;</xsl:text>
          <xsl:text>color:</xsl:text>
          <xsl:call-template name="color">
            <xsl:with-param name="name">
              <xsl:choose>
                <xsl:when test="score &gt; 7">
                  <xsl:text>green</xsl:text>
                </xsl:when>
                <xsl:when test="score &gt; 4">
                  <xsl:text>yellow</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>red</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>
          <xsl:text>;</xsl:text>
        </xsl:attribute>
        <xsl:value-of select="format-number(score,'0.00')"/>
        <span class="under">
          <xsl:attribute name="style">
            <xsl:text>font-weight:normal;</xsl:text>
            <xsl:text>color:</xsl:text>
            <xsl:call-template name="color">
              <xsl:with-param name="name">
                <xsl:choose>
                  <xsl:when test="abs(@diff) &gt; 0.25">
                    <xsl:text>red</xsl:text>
                  </xsl:when>
                  <xsl:when test="abs(@diff) &gt; 0.15">
                    <xsl:text>yellow</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>gray</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:attribute>
          <xsl:value-of select="format-number(@diff * 100, '#')"/>
          <xsl:text>%</xsl:text>
        </span>
      </td>
      <td>
        <xsl:attribute name="style">
          <xsl:text>text-align:right;</xsl:text>
          <xsl:text>color:</xsl:text>
          <xsl:call-template name="color">
            <xsl:with-param name="name">
              <xsl:choose>
                <xsl:when test="defects &gt; 0.25">
                  <xsl:text>red</xsl:text>
                </xsl:when>
                <xsl:when test="defects &gt; 0.15">
                  <xsl:text>yellow</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>green</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:attribute>
        <xsl:value-of select="format-number(defects * 100, '#')"/>
        <xsl:text>%</xsl:text>
      </td>
      <td>
        <xsl:call-template name="bars">
          <xsl:with-param name="bars" select="bars"/>
          <xsl:with-param name="w" select="128"/>
          <xsl:with-param name="h" select="32"/>
        </xsl:call-template>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
