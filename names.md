# Current

## Types

```
Type
├──UnionType
└──DatumType
```
`DataType` = `Type` + `defaultTag`

## CType

```
CType ( acts as Union too )
└──CDatumType
```
`CDataType` = `CTypeRef` + `defaultTagNameDecl`

## Data

`Data` ( tag -> `Val`)  
`Val` ( `Datum` / `ErrorValue` ) = `Value` in generated  
`Datum`

field type: `DataType`  
field value: `Data`

---
# Proposed

## Types

```
Type
├──VarType
└──DatumType
```
`EntryType` = `Type` + `retroTag`

## Data

`VarData` ( tag -> `Value` )  
`Value` ( `Datum` / `ErrorValue` )  
`Datum`

field type: `EntryType`  
field value: `VarData`
