# Polymorphic tails

Since Epigraph type system supports polymorphism, there must be a way to change object projection depending on
what type it actually is, similar to type-based pattern matching.

Polymorphic tails is a mechanism to to this. They are lists of type handling specifications attached to var types and
allow to elaborate how corresponding value must be processed if it happens to be of some specific type. 

For example `(a,b)~C(d)` request projection tells to include fields `a` and `b`, but if data is of type `C` (or a 
subtype of `C`), then field `d` should also be included.

Multiple tail projections can be specified. For instance request projection
```
:(id,record(a))~(B:record(b),C:record(c))
```
tells that
- tags `id` and `record` should be returned
- `record` model should include field `a`
- if actual data instance is a subtype of `B`, then `record` should also include field `b`
- else if actual data instance is a subtype of `C`, then `record` should also include `c`

Notice that only one of `B` or `C` can win. Even if actual instance happen to be of some type `D` which extends
both `B` and `C`, then only `B` branch will be taken into account since it comes first. 
This makes sense if polymorphic tails are treated as type-based pattern matching.

Tails may also be recursive, for example
```
(a)~B(b)~C(c)  ==  (a)~(B(b)~(C(c)))
```
this is a series of specifications which says:
- include field `a`
  - if type happens to be `B`, then also include `b`
    - if type also happens to be `C`, then include `c`
    
Most specific fields should be processed first and be properly merged with less specific fields. For example
```
(someField;auth=TokenX)~Foo(someField;auth=TokenY)
```
overrides authentication token passed for `someField` if type happens to be an instance of `Foo`.

Another example:
```
( a(b)~X(x) ) ~B( a(c)~Y(y) )
```
if data is an instance of `B`, then this translates into `a(c, b) ~( Y(y), X(x) )`. Notice what happened to `X` and `Y`.

## Data processing pattern

Epigraph code dealing with data and projections usually follows the same pattern:
```
processVarData(type: DataType, data: Data, projection: VarProjection) {
  foreach (tag, modelProjection) in projection: {
    processModelData(tag.type, data.get(tag), modelProjection)
  }
}

processModelData(type: DatumType, data: Datum, projection: ModelProjection) {
  switch (type.kind) { // (type casts omitted)
    case Record: processRecordModelData(type, data, projection)
    case Map: processMapModelData(type, data, projection)
    case List: processListModelData(type, data, projection)
    case Enum: processEnumModelData(type, data, projection)
    case Primitive: processPrimitiveModelData(type, data, projection)
  }
}

processRecordModelData(type: RecordType, data: RecordDatum, projection: RecordModelProjection) {
  foreach (field, fieldProjection) in projection: {
    processVarData(field.type, data.get(field), fieldProjection)
  }
}


processMapModelData(type: MapType, data: MapDatum, projection: MapModelProjection) { ... }
processListModelData(type: ListType, data: ListDatum, projection: ListModelProjection) { ... }
processEnumModelData(type: EnumType, data: EnumDatum, projection: EnumModelProjection) { ... }
processPrimitiveModelData(type: PrimitiveType, data: PrimitiveDatum, projection: PrimitiveModelProjection) { ... }
```

Polymorphic tails add a few extra steps to this scheme.

### Tails linearization and effective type
Given a (recursive) list of tails and a type the task is to find matching tail(s) and remove the rest. For example:
```
~(A1~(B11,B12~(C121,C122)),A2~(B21,B22),A3)


     A1        A2       A3
    / \       / \
  B11 B12   B21 B22
      / \
   C121 C122
```
If data type extends `C121`, then linearized tail is `~A1~B12~C121`, the rest is irrelevant.

Effective type is the most specific type of the linearized tail, `C121` in this case.

Linearization algorithm is essentially depth-first traversal without backtracking:
1. init: set `tailsList = []`
1. given a list of tails `T1...Tn` and type `X` find first tail `Ti` such that type `X` is `Ti` or is a subtype of `Ti`
    - if such tail can't be found: stop
    - else `tailsList.add(Ti)`. If `Ti` defines (recursive) tails `K1..Km` then repeat step 2 for them.

In the end `tailsList` will contain linearized tails and it's last element will be the effective type.

### Data processing
All the data processing functions now receive lists of projections instead of singular projection. Elements are
sorted from most specific to least specific and should be processed in that order. Projection items (fields, tags, keys)
may be present in more than one projection and should be carefully merged, with those coming first taking priority.

### Var data processing
`processVarData` now has to start by linearizing `VarProjection` tails with regard to passed `type`. If result is empty,
then tails are essentially ignored. Otherwise tag are processed from the most specific to least specific tails. For
every tag it's model projections are collected from all the tails and are passed as a list to `processModelData`, 
together with the effective type
```
processVarData(type: DataType, data:Data, projections:List[VarProjection]) {
  allTails = projections.map(_.tails).flatten // collect all tails into a single list
  linearizedTails = linearizeTails(data.type, allTails) // sorted from most to least specific, eg [C121,B12,A1]
  
  effectiveType = linearizeTails.isEmpty ? type : linearizeTails.head.type // `data` is now treated as if being of this type
  effectiveProjections = linearizeTails ++ projections // top-level projections are least specific and go to the end
  
  processedTags: List[Tag] = []
  
  foreach projection in effectiveProjections: {
    foreach tag in projection unless processedTags contains tag : {
      processedTags.add(tag)
      
      // collect all model projections for `tag`
      modelProjections = []
      foreach (projection2 in effectiveProjections)
        modelProjections.append(projection2.get(tag)) if projection2.contains(tag)
      
      processModelData(effectiveType, data.get(tag), modelProjections)
    }
  }
}
```

### Model data processing
Functions processing model data will now be receiving lists of model projections instead of single projection. They
should be processed as if merged together, with elements coming first taking priority (since they're coming from more
specific tails).

For example, `processRecordModelData` may receive the following arguments:
- `type` is `T`
- `data` is `{a: {x: X, y: Y}, b:B, c:C}`
- `projections` is `[(a(x), b;param1=v1), (a(y), b;param1=v2, c), (c;param2=v3)]`

This tells that `data` must be treated as having type `T`, and output should be `{a: {x: X, y: Y}, b, c}`.
Note that

- `a` projection is `(x, y)`, due to `processVarData` receiving two projections for `a` field value
- `b` is built with `param1 = v1` which takes priority over `v2`
- `c` is built with `param2 = v3`

Algorithm is similar to var processing, but without linearization step.
```
processRecordModelData(type: RecordType, data: RecordDatum, projections: List[RecordModelProjection]) {
  processedFields: List[Field] = []
  
  foreach projection in projections {
    foreach field in projection unless processedFields contains field : {
      processedFields.add(field)
      
      // collect var projections for `field`
      fieldProjections = []
      foreach (projection2 in projections)
        fieldProjections.append(projection2.get(field)) if projection2.contains(field)
      
      processVarData(field.type, data.get(field), fieldProjections)
    }
  }
}
```

<!-- todo move to wiki -->
<!-- vim: set spell foldlevel=3: -->
