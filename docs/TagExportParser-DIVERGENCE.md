# TagExportParser 乖離レポート

このレポートは
`src/main/java/com/jsptags/navigation/pager/parser/TagExportParser.jj`
（`docs/TagExportParser-SYNTAX.md` の内容）と
`docs/pager-taglib.html` の記述差異をまとめたものです。

## 1. 区切り文字の扱い

### `docs/pager-taglib.html` の記述

export 式の区切りとして以下を許可しています。

- カンマ `,`
- セミコロン `;`
- 空白（スペース）

有効例として、以下が挙げられています。

- `pageOffset;pageNumber`
- `pageOffset pageNumber`

### `TagExportParser.jj` の実装

- リストの区切りは **カンマのみ**。
- セミコロンは **末尾に 1 回だけ置ける任意終端**。
- 空白は **無視**され、区切りにはならない。

### 影響

次の式は **HTML では有効**ですが、**パーサでは無効**です。

- `pageOffset;pageNumber`
- `pageOffset;pageNumber;`
- `pageOffset pageNumber`

## 2. `docs/pager-taglib.html` 内の自己矛盾

`pageOffset pageNumber` が

- 有効例
- 無効例

の両方に記載されています。
パーサ実装では **一貫して無効**です。

## 3. セミコロンの使用位置

### 記述

セミコロンをリスト中の区切りとして扱う書き方が有効とされています。

### 実装

セミコロンは **末尾の任意終端**としてのみ受理され、
リスト中の区切りとしては扱われません。

## 4. 一致している点

次の点は両者で一致しています。

- 短縮名（`offset`, `page`, `number`）の利用
- `IDENTIFIER=keyword` 形式のカスタム変数名
- `offset=foo` のような書式が無効であること

## まとめ

主な乖離は **リストの区切り規則**です。
HTML ではセミコロンや空白で区切れると記載されていますが、
パーサはカンマ区切りのみを受理し、セミコロンは末尾終端としてのみ
扱います。その結果、HTML 内の有効/無効例が自己矛盾する状態にも
なっています。
