namespace x

record R1
record R2 extends R1

record R3 supplements R1

record R4

supplement R3 with R4
