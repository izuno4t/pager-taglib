# TagExportParser 生成情報

## 対象ファイル

- `src/main/java/com/jsptags/navigation/pager/parser/TagExportParser.jj`

## 生成される Java ソース

- `src/main/java/com/jsptags/navigation/pager/parser/TagExportParser.java`
- `src/main/java/com/jsptags/navigation/pager/parser/TagExportParserConstants.java`
- `src/main/java/com/jsptags/navigation/pager/parser/TagExportParserTokenManager.java`
- `src/main/java/com/jsptags/navigation/pager/parser/ParseException.java`
- `src/main/java/com/jsptags/navigation/pager/parser/Token.java`
- `src/main/java/com/jsptags/navigation/pager/parser/TokenMgrError.java`
- `src/main/java/com/jsptags/navigation/pager/parser/JavaCharStream.java`

## 生成方法（JavaCC）

1. JavaCC をインストールする。
2. プロジェクトルートで以下を実行する。

```sh
javacc -OUTPUT_DIRECTORY=src/main/java/com/jsptags/navigation/pager/parser \
  src/main/java/com/jsptags/navigation/pager/parser/TagExportParser.jj
```
