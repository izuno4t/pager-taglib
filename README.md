pager-taglib
============

The Pager Tag Library is the easy and flexible way to implement paging of large data sets in JavaServer Pages (JSP).


元々 [jsptags.com](jsptags.com "jsptags.com") で公開されていたページングライブラリですが、jsptags.com が閉鎖されているので、こちらで公開します。
本家で公開されているものから既に一部修正を行っています。





# 公開前の修正

コミットログに残っていない修正は以下の通りです。

```java
197a198
> 			String encoding = pageContext.getRequest().getCharacterEncoding();
199,200c200,201
< 				name = java.net.URLEncoder.encode(name);
< 				value = java.net.URLEncoder.encode(value);
---
> 				name = java.net.URLEncoder.encode(name, encoding);
> 				value = java.net.URLEncoder.encode(value, encoding);
212c213
< 					name = java.net.URLEncoder.encode(name);
---
> 					name = java.net.URLEncoder.encode(name, encoding);
```

上記の修正のみを行った jar ファイルは[こちら](http://noworks.net/pager-taglib.tar.gz "pager-taglib")からで公開しています。