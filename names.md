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

```
CDataValue
├──CData
└──CDatum
```

`CRecordDatum` fields: `String` -> `CDataValue`

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
├──DataType
└──DatumType
```
~~`EntryType` = `Type` + `retroTag`~~

## Data

`Data` ( tag -> `Holder` )  
`Holder` ( `Datum` / `ErrorValue` )  
`Datum`

field type: `Type`?  
field value: `Data`
