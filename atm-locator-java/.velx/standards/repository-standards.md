# Repository rules and standards

When implementing a new or modifying a repository/entity follow these rules:

**Repository Naming:** `{Entity}Repository` extending `MongoRepository<{Entity}, String>`
**Entity Naming:** Singular noun matching domain concept (e.g., `Atm`, `Address`, `Coordinates`)
**Annotation:** Use `@Repository` on interface; `@Document(collection = "name")` on root entities only
**Embedded Documents:** No `@Document` annotation; just Lombok annotations (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
**Field Mapping:** Use `@Field("mongoFieldName")` when Java property names differ from MongoDB field names
**ID Field:** Use `@Id` with `String` type for MongoDB ObjectId

Always use these best practices:

**Indexing:** Create indexes explicitly on startup (not auto-created since Spring Data MongoDB 3.0); use `@Indexed`, `@CompoundIndex` annotations
**Embedded vs Referenced:** Embed for performance (data accessed together); reference for flexibility (large/shared data); use hybrid approach when needed
**Auditing:** Enable with `@EnableMongoAuditing`; use `@CreatedDate`, `@LastModifiedDate` with `Instant`; add `@Version` for optimistic locking
**Validation:** Combine Bean Validation (`@NotNull`, `@Size`) with MongoDB JSON Schema validation for data integrity

Entity Structure Template:
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "entities")
public class {Entity} {

    @Id
    private String id;

    private String name;

    @Field("mongoFieldName")
    private Boolean javaFieldName;

    private EmbeddedType embedded;  // No @Document on embedded types

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Version
    private Long version;
}
```

Repository Structure Template:
```java
@Repository
public interface {Entity}Repository extends MongoRepository<{Entity}, String> {

    // Derived query methods (optional)
    List<{Entity}> findByFieldName(Type value);

    Optional<{Entity}> findByUniqueField(String value);

    // Use Pageable for pagination
    Page<{Entity}> findByStatus(String status, Pageable pageable);

    // Custom queries with @Query annotation
    @Query("{ 'field': ?0, 'active': true }")
    List<{Entity}> findActiveByField(String field);
}
```
