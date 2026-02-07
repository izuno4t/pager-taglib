# TagExportParser Specification

This document describes the specification derived from
`src/main/java/com/jsptags/navigation/pager/parser/TagExportParser.jj`.
It prioritizes fidelity to the implementation and does not include design intent.

## Scope

This parser targets the `export` string and provides three entry points.

1. `parsePagerTagExport`
2. `parseIndexTagExport`
3. `parsePageTagExport`

## Common Rules

- Whitespace characters (space, tab, newline, CR, FF) are ignored.
- Separators are `,` and `;`.
- Assignment uses `=`.
- Input must end with EOF.

## Identifier (IDENTIFIER)

- Starts with `LETTER`.
- Continues with zero or more `LETTER` or `DIGIT`.

### LETTER

Allowed ranges:

- `$`
- `A-Z`
- `_`
- `a-z`
- `\u00c0-\u00d6`
- `\u00d8-\u00f6`
- `\u00f8-\u00ff`
- `\u0100-\u1fff`
- `\u3040-\u318f`
- `\u3300-\u337f`
- `\u3400-\u3d2d`
- `\u4e00-\u9fff`
- `\uf900-\ufaff`

### DIGIT

Allowed ranges:

- `0-9`
- `\u0660-\u0669`
- `\u06f0-\u06f9`
- `\u0966-\u096f`
- `\u09e6-\u09ef`
- `\u0a66-\u0a6f`
- `\u0ae6-\u0aef`
- `\u0b66-\u0b6f`
- `\u0be7-\u0bef`
- `\u0c66-\u0c6f`
- `\u0ce6-\u0cef`
- `\u0d66-\u0d6f`
- `\u0e50-\u0e59`
- `\u0ed0-\u0ed9`
- `\u1040-\u1049`

## Pager Grammar

### Pager Keywords

- `number`
- `offset`
- `page`
- `pageNumber`
- `pageOffset`

### Pager Forms

- Keyword alone
- Assignment form: `<IDENTIFIER> = <keyword>`

### Pager Examples

- `pageOffset,pageNumber`
- `currentPageNumber=pageNumber;`

## Index Grammar

### Index Keywords

- `itemCount`
- `items`
- `pageCount`
- `pages`

### Index Forms

- Keyword alone
- Assignment form: `<IDENTIFIER> = <keyword>`

### Index Examples

- `itemCount,pageCount`
- `cnt=itemCount,pages=pageCount;`

## Page Grammar

### Page Keywords

- `first`
- `firstItem`
- `last`
- `lastItem`
- `number`
- `page`
- `pageNumber`
- `pageUrl`
- `url`

### Page Forms

- Keyword alone
- Assignment form: `<IDENTIFIER> = <keyword>`

### Page Examples

- `pageUrl,pageNumber,firstItem,lastItem`
- `link=pageUrl,num=pageNumber,first=firstItem,last=lastItem;`
