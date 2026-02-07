# TagExportParser SYNTAX

This document defines the syntax of the `export` attribute used by the Pager
Tag Library.
It is derived from the JavaCC grammar in
`src/main/java/com/jsptags/navigation/pager/parser/TagExportParser.jj`.

## Scope

The syntax applies to three entry points:

1. `parsePagerTagExport`
2. `parseIndexTagExport`
3. `parsePageTagExport`

## Lexical Rules

### Whitespace

The following characters are ignored everywhere:

- space
- tab
- line feed
- carriage return
- form feed

### Separators

- `=` (assignment)
- `,` (separator)
- `;` (optional terminator)

### IDENTIFIER

- An identifier starts with `LETTER`.
- It may be followed by zero or more `LETTER` or `DIGIT` characters.

#### LETTER

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

#### DIGIT

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

## Grammar (EBNF)

```text
export-list    ::= export-item ("," export-item)* [";"]
export-item    ::= keyword | IDENTIFIER "=" keyword
```

### Pager Grammar

Keywords:

- `number`
- `offset`
- `page`
- `pageNumber`
- `pageOffset`

Examples:

- `pageOffset,pageNumber`
- `currentPageNumber=pageNumber;`

### Index Grammar

Keywords:

- `itemCount`
- `items`
- `pageCount`
- `pages`

Examples:

- `itemCount,pageCount`
- `cnt=itemCount,pages=pageCount;`

### Page Grammar

Keywords:

- `first`
- `firstItem`
- `last`
- `lastItem`
- `number`
- `page`
- `pageNumber`
- `pageUrl`
- `url`

Examples:

- `pageUrl,pageNumber,firstItem,lastItem`
- `link=pageUrl,num=pageNumber,first=firstItem,last=lastItem;`

## Valid and Invalid Examples (from v2.0 reference)

Valid:

- `pageOffset,pageNumber`
- `pageOffset;pageNumber`
- `pageOffset,pageNumber;`
- `pageOffset;pageNumber;`
- `pageOffset;`
- `offset, pageNumber`
- `versatz=offset, zahl=number;`

Invalid:

- `pageOffset,`
- `pageOffset, pageNumber,`
- `nonexistentVariable`
- `offset=foo` (should be `foo=offset`)

Note:

- The v2.0 reference lists a space-separated form (for example
  `pageOffset pageNumber`) as valid. The current grammar does not accept this
  because whitespace is ignored rather than treated as a separator.
