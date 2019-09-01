pager-taglib
============

[![Build Status](https://travis-ci.com/izuno4t/pager-taglib.svg?branch=develop)](https://travis-ci.com/noworks/pager-taglib)

The Pager Tag Library is the easy and flexible way to implement paging of large data sets in JavaServer Pages (JSP).

元々 [jsptags.com](https://www.jsptags.com/ "jsptags.com") で公開されていたページングライブラリですが、jsptags.com が閉鎖されているので、こちらで公開します。
本家で公開されているものから既に一部修正を行っています。

公開前の修正
---------------

コミットログに残っていない修正は以下の通りです。

```java
197a198
>             String encoding = pageContext.getRequest().getCharacterEncoding();
199,200c200,201
<                 name = java.net.URLEncoder.encode(name);
<                 value = java.net.URLEncoder.encode(value);
---
>                 name = java.net.URLEncoder.encode(name, encoding);
>                 value = java.net.URLEncoder.encode(value, encoding);
212c213
<                     name = java.net.URLEncoder.encode(name);
---
>                     name = java.net.URLEncoder.encode(name, encoding);
```

上記の修正のみを行った jar ファイルは[こちら](https://s3-ap-northeast-1.amazonaws.com/public.noworks.net/java/pager-taglib.tar.gz "pager-taglib")からで公開しています。

sonarcloud
---------------

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=izuno4t_pager-taglib)](https://sonarcloud.io/dashboard?id=izuno4t_pager-taglib)

```bash
mvn sonar:sonar \
  -Dsonar.projectKey=noworks_pager-taglib \
  -Dsonar.organization=noworks-github \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=05c30261224991c41816002525cb4a71267868b6
```
