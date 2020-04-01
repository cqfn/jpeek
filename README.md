<img src="https://www.jpeek.org/logo.svg" height="92px"/>

[![Donate via Zerocracy](https://www.0crat.com/contrib-badge/C7JGJ00DP.svg)](https://www.0crat.com/contrib/C7JGJ00DP)

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C7JGJ00DP.svg)](https://www.0crat.com/p/C7JGJ00DP)
[![DevOps By Rultor.com](https://www.rultor.com/b/yegor256/jpeek)](https://www.rultor.com/p/yegor256/jpeek)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Build Status](https://travis-ci.org/yegor256/jpeek.svg?branch=master)](https://travis-ci.org/yegor256/jpeek)
[![Javadoc](http://www.javadoc.io/badge/org.jpeek/jpeek.svg)](http://www.javadoc.io/doc/org.jpeek/jpeek)
[![PDD status](https://www.0pdd.com/svg?name=yegor256/jpeek)](https://www.0pdd.com/p?name=yegor256/jpeek)
[![Maven Central](https://img.shields.io/maven-central/v/org.jpeek/jpeek.svg)](https://maven-badges.herokuapp.com/maven-central/org.jpeek/jpeek)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/jpeek/blob/master/LICENSE.txt)

[![jpeek report](https://i.jpeek.org/org.jpeek/jpeek/badge.svg)](https://i.jpeek.org/org.jpeek/jpeek/)
[![Test Coverage](https://img.shields.io/codecov/c/github/yegor256/jpeek.svg)](https://codecov.io/github/yegor256/jpeek?branch=master)
[![SonarQube](https://img.shields.io/badge/sonar-ok-green.svg)](https://sonarcloud.io/dashboard?id=org.jpeek%3Ajpeek)
[![Hits-of-Code](https://hitsofcode.com/github/yegor256/jpeek)](https://hitsofcode.com/view/github/yegor256/jpeek)

Project architect: [@paulodamaso](https://github.com/paulodamaso)

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
that the ideas from [Elegant Objects](https://www.yegor256.com/elegant-objects.html)
book series make sense.

## How to use?

Load the latest `jar-with-dependencies.jar` file from
[here](https://repo1.maven.org/maven2/org/jpeek/jpeek/)
and then:

```bash
$ java -jar jpeek-jar-with-dependencies.jar --sources . --target ./jpeek
```

jPeek will analyze Java files in the current directory.
XML reports will be generated in the `./jpeek` directory. Enjoy.

You can also deploy it as a web service to your own platform. Just compile it
with `mvn clean package --settings settings.xml` and then run, as `Procfile` suggests.
You will need to have `settings.xml` with the following data:

```xml
<settings>
  <profiles>
    <profile>
      <id>jpeek-heroku</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
        <sentry.dsn>https://...</sentry.dsn>
        <dynamo.key>AKIAI..........LNN6A</dynamo.key>
        <dynamo.secret>6560KMv5+8Ti....................Qdwob63Z</dynamo.secret>
      </properties>
    </profile>
  </profiles>
</settings>
```

You will also need these tables in DynamoDB (all indexes must deliver `ALL` attributes):

```
jpeek-mistakes:
  metric (HASH/String)
  version (RANGE/String)
  indexes:
    mistakes (GSI):
      version (HASH/String),
      avg (RANGE/Number)
jpeek-results:
  artifact (HASH/String)
  indexes:
    ranks (GSI):
      version (HASH/String)
      rank (RANGE/Number)
    scores (GSI):
      version (HASH/String)
      score (RANGE/Number)
    recent (GSI):
      good (HASH/String)
      added (RANGE/Number)
```

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

### Here is a list of metrics we have already implemented:

[`bansiya99`]
Cohesion Among Methods of Classes (**CAMC**).<br/>
Jagdish Bansiya et al.,<br/>
_A class cohesion metric for object-oriented designs_,<br/>
Journal of Object-Oriented Programming, vol. 11, no. 8, 1999,
[PDF](papers/bansiya99_CAMC.pdf).

[`chidamber94`]
Lack of Cohesion in Methods (**LCOM**).<br/>
Shyam Chidamber et al.,<br/>
_A metrics suite for object oriented design_,<br/>
IEEE Transactions on Software Engineering, vol.20, no.6, 1994,
[PDF](papers/chidamber94_LCOM.pdf).

[`aman04`]
Optimistic Class Cohesion (**OCC**) and Pessimistic Class Cohesion (**PCC**).<br/>
Hirohisa Aman et al.,<br/>
_A proposal of class cohesion metrics using sizes of cohesive parts_,<br/>
Proc. of Fifth Joint Conference on Knowledge-based Software Engineering, 2002,
[PDF](papers/aman04_OCC_PCC.pdf).

[`dallal07`]
Method-Method through Attributes Cohesion (**MMAC**).<br/>
Jehad Al Dallal,<br/>
_A Design-Based Cohesion Metric for Object-Oriented Classes_,<br/>
World Academy of Science, Engineering and Technology International Journal of Computer and Information Engineering Vol:1, No:10, 2007,
[PDF](papers/dallal07_MMAC.pdf).

[`counsell06`]
Normalized Hamming Distance (**NHD**).<br/>
Steve Counsell et al.,<br/>
_The interpretation and utility of three cohesion metrics for object-oriented design_,<br/>
ACM TOSEM, April 2006,
[PDF](papers/counsell06_NHD.pdf).

[`sellers96`]
Lack of Cohesion in Methods 2-3 (**LCOM 2, 3 and 5**).<br/>
B. Henderson-Sellers et al.,<br/>
_Coupling and cohesion (towards a valid metrics suite for object-oriented analysis and design)_,<br/>
Object Oriented Systems 3, 1996,
[PDF](papers/sellers96_LCOM2_LCOM3_LCOM5.pdf).

[`wasiq01`]
Class Connection Metric (**CCM**).<br/>
M. Wasiq<br/>
_Measuring Class Cohesion in Object-Oriented Systems_,<br/>
Master Thesis at the King Fahd University of Petroleum & Minerals, 2001,
[PDF](papers/wasiq01_CCM.pdf).

[`fernandez06`]
A Sensitive Metric of Class Cohesion (**SCOM**).<br/>
Luis Fernández et al.,<br/>
_[A] new metric [...] yielding meaningful values [...] more sensitive than those previously reported_,<br/>
International Journal "Information Theories & Applications", Volume 13, 2006,
[PDF](papers/fernandez06_SCOM.pdf).

[`bieman95`]
Tight Class Cohesion (**TCC**) and Loose Class Cohesion (**LCC**).<br/>
James M. Bieman et al.,<br/>
_Cohesion and Reuse in an Object-Oriented System_,<br/>
Department of Computer Science, Colorado State University, 1995,
[PDF](papers/bieman95_TCC.pdf).

[`dallal11`]
Transitive Lack of Cohesion in Methods (**TLCOM**).<br/>
Jehad Al Dallal,<br/>
_Transitive-based object-oriented lack-of-cohesion metric_,<br/>
Department of Information Science, Kuwait University, 2011,
[PDF](papers/dallal11_TLCOM.pdf).

[`hitz95`]
Lack of Cohesion in Methods 4 (**LCOM4**).<br/>
Martin Hitz et al.,<br/>
_Measuring Coupling and Cohesion In Object-Oriented Systems_,<br/>
Institute of Applied Computer Science and Systems Analysis, University of Vienna, 1995,
[PDF](papers/hitz95_LCOM4.pdf).

[`marcus05`]
Conceptual Cohesion of Classes (**C3**).<br/>
A. Marcus and D. Poshyvanyk,<br/>
_The conceptual cohesion of classes_,<br/>
21st IEEE International Conference on Software Maintenance (ICSM'05), Budapest, Hungary, 2005, pp. 133-142,
[PDF](papers/marcus05_C3.pdf)

[`liu09`]
Maximal Weighted Entropy (**MWE**).<br/>
Y. Liu, D. Poshyvanyk, R. Ferenc, T. Gyim´othy, and N. Chrisochoides,<br/>
_Modeling class cohesion as mixtures of latent topics_,<br/>
Software Maintenance, 2009. ICSM 2009. IEEE International Conference on. IEEE, 2009, pp. 233–242,
[PDF](papers/liu09_MWE.pdf)

[`etzkorn00`]
LOgical Relatedness of Methods (**LORM**).<br/>
L. Etzkorn and H. Delugach,<br/>
_Towards a semantic metrics suite for object-oriented design_,<br/>
Technology of Object-Oriented Languages and Systems, 2000. TOOLS 34. Proceedings. 34th International Conference on. IEEE, 2000, pp. 71–80,
[PDF](papers/etzkorn00_LORM.pdf)

## How it works?

First, `Skeleton` parses Java bytecode using Javaassit and ASM, in order to produce
`skeleton.xml`. This XML document contains information about each class, which
is necessary for the metrics calculations. For example, this simple Java
class:

```java
class Book {
  private int id;
  int getId() {
    return this.id;
  }
}
```

Will look like this in the `skeleton.xml`:

```xml
<class id='Book'>
  <attributes>
   <attribute public='false' static='false' type='I'>id</attribute>
  </attributes>
  <methods>
    <method abstract='false' ctor='true' desc='()I' name='getId' public='true' static='false'>
      <return>I</return>
      <args/>
    </method>
  </methods>
</class>
```

Then, we have a collection of XSL stylesheets, one per each metric. For example,
`LCOM.xsl` transforms `skeleton.xml` into `LCOM.xml`, which may look like this:

```xml
<metric>
  <title>MMAC</title>
  <app>
    <class id='InstantiatorProvider' value='1'/>
    <class id='InstantationException' value='0'/>
    <class id='AnswersValidator' value='0.0583'/>
    <class id='ClassNode' value='0.25'/>
    [... skipped ...]
  </app>
</metric>
```

Thus, all calculations happen inside the XSLT files. We decided to implement
it this way after a less successful attempt to do it all in Java. It seems
that XSL is much more suitable for manipulations with data than Java.

## Known Limitations

* The java compiler is known to inline constant variables as per [JLS 13.1](https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html#jls-13.1). This affects the results calculated by metrics that take into account access to class attributes if these are `final` constants. For instance, all LCOM* and *COM metrics are affected.

## How to contribute?

Just fork, make changes, run `mvn clean install -Pqulice` and submit
a pull request; read [this](http://www.yegor256.com/2014/04/15/github-guidelines.html),
if lost.

## Contributors

  - [@yegor256](https://github.com/yegor256) as Yegor Bugayenko ([Blog](https://www.yegor256.com))
  - [@alayor](https://github.com/alayor) as Alonso A. Ortega ([Blog](http://www.alayor.com))
  - [@memoyil](https://github.com/memoyil) as Mehmet Yildirim
  - [@sergey-karazhenets](https://github.com/sergey-karazhenets) as Sergey Karazhenets
  - [@llorllale](https://github.com/llorllale) as George Aristy
  - [@mesut](https://github.com/mesut) as Mesut Özen
  - [@serranya](https://github.com/serranya) as Peter Lamby
  - [@humb1t](https://github.com/humb1t) as Nikita Puzankov
  - [@stepanov-dmitry](https://github.com/stepanov-dmitry) as Dmitry Stepanov
  - [@GnusinPavel](https://github.com/GnusinPavel) as Gnusin Pavel
  - [@mohamednizar](https://github.com/mohamednizar) as Mohamed Nizar

Don't hesitate to add your name to this list in your next pull request.
