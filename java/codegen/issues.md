- [ ] Rename generated container accessor methods:
  - [ ] records:
    - `getFoo()` to access default tag datum (if declared)
    - `getFoo_()` to access default tag value (if declared)
    - `getFoo$()` to access data (for var typed fields)
    - `getFoo$tag()` to access tag datum (for var typed fields)
  - [ ] lists:
    - `elements()` to access default tag datums (if declared)
    - `elements_()` to access default tag values (if declared)
    - `elements$()` to access element data (for var typed lists)
    - `elements$tag()` to access element tag datums (for var typed lists)
  - [ ] maps:
    - `entries()` to access key to default tag datum map (if declared)
    - `entries_()` to access key to default tag value map (if declared)
    - `entries$()` to access key to data map (for var typed lists)
    - `entries$tag()` to access key to tag datum map (for var typed lists)

- [ ] List and Map Builders could implement `List<DefaultTagDatum>`
      and `Map<KeyImmDatum, DefaultTagDatum>` interfaces, respectively
      (if default is implied/declared).

- [ ] Enum type bindings
