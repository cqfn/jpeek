# jPeek

![jPeek logo](https://www.jpeek.org/logo.svg)

[![EO principles respected here][badge-eo]][link-eo]
[![DevOps By Rultor.com][badge-rultor]][link-rultor]
[![We recommend IntelliJ IDEA][badge-idea]][link-idea]

[![mvn][badge-mvn]][link-mvn]
[![Javadoc][badge-javadoc]][link-javadoc]
[![PDD status][badge-pdd]][link-pdd]
[![Maven Central][badge-maven]][link-maven]
[![License][badge-license]][link-license]
[![codecov][badge-codecov]][link-codecov]
[![jpeek report][badge-report]][link-report]
[![SonarQube][badge-sonar]][link-sonar]
[![Hits-of-Code][badge-hits]][link-hits]

jPeek is a static collector of Java code metrics.

## Motivation

[Class cohesion](http://www.jot.fm/issues/issue_2008_07/article1.pdf), for
example, is considered as one of most important object-oriented software
attributes. There are
[over 30](http://www.math.md/files/csjm/v25-n1/v25-n1-(pp44-74).pdf) different
cohesion metrics invented so far, but almost none of them have calculators
available. The situation with other metrics is very similar. We want to create
such a tool that will make it possible to analyze code quality more or less
formally (with hundreds of metrics). Then, we will apply this analysis to
various Java libraries with an intent to prove that the ideas from
[Elegant Objects](https://www.yegor256.com/elegant-objects.html) book series
make sense.

## How to use?

Load the latest `jar-with-dependencies.jar` file from
[repo1.maven.org](https://repo1.maven.org/maven2/org/jpeek/jpeek/) and then:

```bash
java -jar jpeek-jar-with-dependencies.jar --sources . --target ./jpeek
```

jPeek will analyze Java files in the current directory. XML reports will be
generated in the `./jpeek` directory. Enjoy.

### Available CLI options

* `-s, --sources <path>`: **Required.** Path to directory with the class files.
* `-t, --target <path>`: **Required.** Path to directory where the reports will
  be generated.
* `--include-ctors`: Include constructors into all formulas.
* `--include-static-methods`: Include static methods into all formulas.
* `--include-private-methods`: Include private methods into all formulas.
* `--metrics <metrics>`: Comma-separated list of metrics to include (default:
  `"LCOM5,NHD,MMAC,SCOM,CAMC"`).
* `--overwrite`: Overwrite the target directory, if it exists, or exit with
  error.
* `--quiet`: Turn off logging.
* `--help`: Display help message.

You can also deploy it as a web service to your own platform. Compile it with
`mvn clean package --settings settings.xml` and then run it, as `Procfile`
suggests. You will need to have `settings.xml` with the following data:

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

You will also need these tables in DynamoDB (all indexes must deliver `ALL`
attributes):

```text
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

* `izadkhah17`: Habib Izadkhah et al., _Class Cohesion Metrics for Software
  Engineering: A Critical Review_, Computer Science Journal of Moldova,
  vol.25, no.1(73), 2017.
  [PDF](http://www.math.md/files/csjm/v25-n1/v25-n1-(pp44-74).pdf).
* `badri08`: Linda Badri et al., _Revisiting Class Cohesion: An empirical
  investigation on several systems_, Journal of Object Technology, vol.7, no.6,
  2008. [PDF](http://www.jot.fm/issues/issue_2008_07/article1.pdf).

### Metrics already implemented

* `chidamber94`: Lack of Cohesion in Methods (**LCOM**). Shyam Chidamber et al.,
  _A metrics suite for object oriented design_, IEEE Transactions on Software
  Engineering, vol.20, no.6, 1994. [PDF](papers/chidamber94_LCOM.pdf).
* `bieman95`: Tight Class Cohesion (**TCC**) and Loose Class Cohesion (**LCC**).
  James M. Bieman et al., _Cohesion and Reuse in an Object-Oriented System_,
  Department of Computer Science, Colorado State University, 1995.
  [PDF](papers/bieman95_TCC.pdf).
* `hitz95`: Lack of Cohesion in Methods 4 (**LCOM4**). Martin Hitz et al.,
  _Measuring Coupling and Cohesion In Object-Oriented Systems_, Institute of
  Applied Computer Science and Systems Analysis, University of Vienna, 1995.
  [PDF](papers/hitz95_LCOM4.pdf).
* `sellers96`: Lack of Cohesion in Methods 2-3 (**LCOM 2, 3 and 5**).
  B. Henderson-Sellers et al., _Coupling and cohesion (towards a valid metrics
  suite for object-oriented analysis and design)_, Object Oriented Systems 3,
  1996. [PDF](papers/sellers96_LCOM2_LCOM3_LCOM5.pdf).
* `bansiya99`: Cohesion Among Methods of Classes (**CAMC**). Jagdish Bansiya
  et al., _A class cohesion metric for object-oriented designs_, Journal of
  Object-Oriented Programming, vol. 11, no. 8, 1999.
  [PDF](papers/bansiya99_CAMC.pdf).
* `etzkorn00`: LOgical Relatedness of Methods (**LORM**). L. Etzkorn and
  H. Delugach, _Towards a semantic metrics suite for object-oriented design_,
  Technology of Object-Oriented Languages and Systems, 2000. TOOLS 34.
  Proceedings. 34th International Conference on. IEEE, 2000, pp. 71–80.
  [PDF](papers/etzkorn00_LORM.pdf).
* `wasiq01`: Class Connection Metric (**CCM**). M. Wasiq, _Measuring Class
  Cohesion in Object-Oriented Systems_, Master Thesis at the King Fahd
  University of Petroleum & Minerals, 2001. [PDF](papers/wasiq01_CCM.pdf).
* `aman04`: Optimistic Class Cohesion (**OCC**) and Pessimistic Class Cohesion
  (**PCC**). Hirohisa Aman et al., _A proposal of class cohesion metrics using
  sizes of cohesive parts_, Proc. of Fifth Joint Conference on Knowledge-based
  Software Engineering, 2002. [PDF](papers/aman04_OCC_PCC.pdf).
* `marcus05`: Conceptual Cohesion of Classes (**C3**). A. Marcus and
  D. Poshyvanyk, _The conceptual cohesion of classes_, 21st IEEE International
  Conference on Software Maintenance (ICSM'05), Budapest, Hungary, 2005,
  pp. 133-142. [PDF](papers/marcus05_C3.pdf).
* `counsell06`: Normalized Hamming Distance (**NHD**). Steve Counsell et al.,
  _The interpretation and utility of three cohesion metrics for object-oriented
  design_, ACM TOSEM, April 2006. [PDF](papers/counsell06_NHD.pdf).
* `fernandez06`: A Sensitive Metric of Class Cohesion (**SCOM**). Luis
  Fernández et al., _[A] new metric [...] yielding meaningful values [...] more
  sensitive than those previously reported_, International Journal "Information
  Theories & Applications", Volume 13, 2006.
  [PDF](papers/fernandez06_SCOM.pdf).
* `dallal07`: Method-Method through Attributes Cohesion (**MMAC**).
  Jehad Al Dallal, _A Design-Based Cohesion Metric for Object-Oriented
  Classes_, World Academy of Science, Engineering and Technology International
  Journal of Computer and Information Engineering Vol:1, No:10, 2007.
  [PDF](papers/dallal07_MMAC.pdf).
* `liu09`: Maximal Weighted Entropy (**MWE**). Y. Liu, D. Poshyvanyk,
  R. Ferenc, T. Gyim´othy, and N. Chrisochoides, _Modeling class cohesion as
  mixtures of latent topics_, Software Maintenance, 2009. ICSM 2009. IEEE
  International Conference on. IEEE, 2009, pp. 233–242.
  [PDF](papers/liu09_MWE.pdf).
* `dallal11`: Transitive Lack of Cohesion in Methods (**TLCOM**).
  Jehad Al Dallal, _Transitive-based object-oriented lack-of-cohesion metric_,
  Department of Information Science, Kuwait University, 2011.
  [PDF](papers/dallal11_TLCOM.pdf).
* `yegor256`: Distance of Coupling (**DOC**).
  Yegor Bugayenko, _Distance of Coupling_,
  [Blog](https://www.yegor256.com/2020/10/27/distance-of-coupling.html).

## How it works?

First, `Skeleton` parses Java bytecode using Javaassit and ASM, in order to
produce `skeleton.xml`. This XML document contains information about each
class, which is necessary for the metrics calculations. For example, this
simple Java class:

```java
class Book {
  private int id;
  int getId() {
    return this.id;
  }
}
```

will look like this in the `skeleton.xml`:

```xml
<class id='Book'>
  <attributes>
   <attribute public='false' static='false' type='I'>id</attribute>
  </attributes>
  <methods>
    <method abstract='false' ctor='true' desc='()I' name='getId' public='true'
      static='false'>
      <return>I</return>
      <args/>
    </method>
  </methods>
</class>
```

Then, we have a collection of XSL stylesheets, one per each metric. For
example, `LCOM.xsl` transforms `skeleton.xml` into `LCOM.xml`, which may look
like this:

```xml
<metric>
  <title>MMAC</title>
  <app>
    <class id='InstantiatorProvider' value='1'/>
    <class id='InstantiationException' value='0'/>
    <class id='AnswersValidator' value='0.0583'/>
    <class id='ClassNode' value='0.25'/>
    [... skipped ...]
  </app>
</metric>
```

Thus, all calculations happen inside the XSLT files. We decided to implement
it this way after a less successful attempt to do it all in Java. It seems
that XSL is much more suitable for manipulations with data than Java.

### jPeek maven plugin

We are developing a jPeek plugin for Maven. See the
[jPeek Maven plugin](https://github.com/yegor256/jpeek-maven-plugin) project.

## Known Limitations

* The java compiler is known to inline constant variables as per
  [JLS 13.1][link-jls-13-1].
  This affects the results calculated by metrics that take into account access
  to class attributes if these are `final` constants. For instance, all LCOM
  and COM metrics are affected.

## How to contribute?

Just fork, make changes, run `mvn clean install -Pqulice` and submit a pull
request. Read
[this guide](http://www.yegor256.com/2014/04/15/github-guidelines.html) if
lost.

## Contributors

* [@yegor256](https://github.com/yegor256) as Yegor Bugayenko
  ([Blog](https://www.yegor256.com))
* [@alayor](https://github.com/alayor) as Alonso A. Ortega
  ([Blog](http://www.alayor.com))
* [@memoyil](https://github.com/memoyil) as Mehmet Yildirim
* [@sergey-karazhenets](https://github.com/sergey-karazhenets) as Sergey
  Karazhenets
* [@llorllale](https://github.com/llorllale) as George Aristy
* [@mesut](https://github.com/mesut) as Mesut Özen
* [@serranya](https://github.com/serranya) as Peter Lamby
* [@humb1t](https://github.com/humb1t) as Nikita Puzankov
* [@stepanov-dmitry](https://github.com/stepanov-dmitry) as Dmitry Stepanov
* [@GnusinPavel](https://github.com/GnusinPavel) as Gnusin Pavel
* [@mohamednizar](https://github.com/mohamednizar) as Mohamed Nizar

Don't hesitate to add your name to this list in your next pull request.

[badge-eo]: https://www.elegantobjects.org/badge.svg
[badge-rultor]: https://www.rultor.com/b/cqfn/jpeek
[badge-idea]: https://www.elegantobjects.org/intellij-idea.svg
[badge-mvn]: https://github.com/cqfn/jpeek/actions/workflows/mvn.yml/badge.svg
[badge-javadoc]: https://www.javadoc.io/badge/org.jpeek/jpeek.svg
[badge-pdd]: https://www.0pdd.com/svg?name=cqfn/jpeek
[badge-maven]: https://img.shields.io/maven-central/v/org.jpeek/jpeek.svg
[badge-license]: https://img.shields.io/badge/license-MIT-green.svg
[badge-codecov]: https://codecov.io/gh/cqfn/jpeek/branch/master/graph/badge.svg
[badge-report]: https://i.jpeek.org/org.jpeek/jpeek/badge.svg
[badge-sonar]: https://img.shields.io/badge/sonar-ok-green.svg
[badge-hits]: https://hitsofcode.com/github/cqfn/jpeek
[link-eo]: https://www.elegantobjects.org
[link-rultor]: https://www.rultor.com/p/cqfn/jpeek
[link-idea]: https://www.jetbrains.com/idea/
[link-mvn]: https://github.com/cqfn/jpeek/actions/workflows/mvn.yml
[link-javadoc]: https://www.javadoc.io/doc/org.jpeek/jpeek
[link-pdd]: https://www.0pdd.com/p?name=cqfn/jpeek
[link-maven]: https://maven-badges.herokuapp.com/maven-central/org.jpeek/jpeek
[link-license]: https://github.com/cqfn/jpeek/blob/master/LICENSE.txt
[link-codecov]: https://codecov.io/gh/cqfn/jpeek
[link-report]: https://i.jpeek.org/org.jpeek/jpeek/
[link-sonar]: https://sonarcloud.io/dashboard?id=org.jpeek%3Ajpeek
[link-hits]: https://hitsofcode.com/view/github/cqfn/jpeek
[link-jls-13-1]: https://docs.oracle.com/javase/specs/jls/se8/html/jls-13.html
