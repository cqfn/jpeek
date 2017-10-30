<img src="http://www.jpeek.org/logo.svg" height="92px"/>

[![Managed by Zerocracy](http://www.0crat.com/badge/C7JGJ00DP.svg)](http://www.0crat.com/p/C7JGJ00DP)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/jpeek)](http://www.rultor.com/p/yegor256/jpeek)

[![Build Status](https://travis-ci.org/yegor256/jpeek.svg?branch=master)](https://travis-ci.org/yegor256/jpeek)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.jpeek/jpeek/badge.svg?color=blue&prefix=v)](http://www.javadoc.io/doc/org.jpeek/jpeek)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/jpeek)](http://www.0pdd.com/p?name=yegor256/jpeek)
[![Maven Central](https://img.shields.io/maven-central/v/org.jpeek/jpeek.svg)](https://maven-badges.herokuapp.com/maven-central/org.jpeek/jpeek)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/jpeek/blob/master/LICENSE.txt)

[![jpeek report](http://i.jpeek.org/org.jpeek/jpeek/badge.svg)](http://i.jpeek.org/org.jpeek/jpeek/)
[![Test Coverage](https://img.shields.io/codecov/c/github/yegor256/jpeek.svg)](https://codecov.io/github/yegor256/jpeek?branch=master)
[![SonarQube](https://img.shields.io/badge/sonar-ok-green.svg)](https://sonarcloud.io/dashboard?id=org.jpeek%3Ajpeek)

jPeek is a static collector of Java code metrics.

**Motivation**:
[Class cohesion](http://www.jot.fm/issues/issue_2008_07/article1.pdf), for example,
is considered as one of most important object-oriented software attributes.
There are
[over 30](http://www.math.md/files/csjm/v25-n1/v25-n1-(pp44-74).pdf)
different cohesion metrics invented so far, but almost none of them
have calculators available. The situation with other metrics is very similar.
We want to create such a tool that will make it
possible to analyze code quality more or less formally (with hundreds of metrics). Then, we will
apply this analysis to different Java libraries with an intent to prove
that the ideas from [Elegant Objects](http://www.yegor256.com/elegant-objects.html)
book series make sense.

## How to use?

Load [this JAR file](http://repo1.maven.org/maven2/org/jpeek/jpeek/0.5/jpeek-0.5-jar-with-dependencies.jar) and then:

```bash
$ java -jar jpeek-0.5-jar-with-dependencies.jar . ./jpeek
```

jPeek will analyze Java files in the current directory.
XML reports will be generated in the `./jpeek` directory. Enjoy.

## Cohesion Metrics

These papers provide a pretty good summary of cohesion metrics:

[`izadkhah17`]
Habib Izadkhah et al.,<br/>
_Class Cohesion Metrics for Software Engineering: A Critical Review_,<br/>
Computer Science Journal of Moldova, vol.25, no.1(73), 2017,
[PDF](http://www.math.md/files/csjm/v25-n1/v25-n1-(pp44-74).pdf).

[`badri08`]
Linda Badri et al.,<br/>
_Revisiting Class Cohesion: An empirical investigation on several systems_,<br/>
Journal of Object Technology, vol.7, no.6, 2008,
[PDF](http://www.jot.fm/issues/issue_2008_07/article1.pdf).

Here is a list of metrics we already implement:

[`bansiya99`]
Cohesion Among Methods of Classes (**CAMC**).<br/>
Jagdish Bansiya et al.,<br/>
_A class cohesion metric for object-oriented designs_,<br/>
Journal of Object-Oriented Programming, vol. 11, no. 8, 1999,
[PDF](https://pdfs.semanticscholar.org/2709/1005bacefaee0242cf2643ba5efa20fa7c47.pdf).

[`chidamber94`]
Lack of Cohesion in Methods (**LCOM**).<br/>
Shyam Chidamber et al.,<br/>
_A metrics suite for object oriented design_,<br/>
IEEE Transactions on Software Engineering, vol.20, no.6, 1994,
[PDF](http://www.pitt.edu/~ckemerer/CK%20research%20papers/MetricForOOD_ChidamberKemerer94.pdf).

[`aman04`]
Optimistic Class Cohesion (**OCC**).<br/>
Hirohisa Aman et al.,<br/>
_A proposal of class cohesion metrics using sizes of cohesive parts_,<br/>
Proc. of Fifth Joint Conference on Knowledge-based Software Engineering, 2002,
[PDF](https://www.researchgate.net/profile/Hirohisa_Aman/publication/268046583_A_Proposal_of_Class_Cohesion_Metrics_Using_Sizes_of_Cohesive_Parts/links/5729ca4b08ae057b0a060fa6/A-Proposal-of-Class-Cohesion-Metrics-Using-Sizes-of-Cohesive-Parts.pdf).

## How to contribute?

Read [`CONTRIBUTING.md`](https://github.com/yegor256/jpeek/blob/master/CONTRIBUTING.md)

## Contributors

  - [@yegor256](https://github.com/yegor256) as Yegor Bugayenko ([Blog](http://www.yegor256.com))
  - [@alayor](https://github.com/alayor) as Alonso A. Ortega ([Blog](http://www.alayor.com))
  - [@memoyil](https://github.com/memoyil) as Mehmet Yildirim

Don't hesitate to add your name to this list in your next pull request.

## License (MIT)

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
