# xml-extensions

Kotlin extensions for XML parsing and navigation using StAX.
This module provides a type-safe XML tree API and XPath-based query engine that serves as a
Kotlin-idiomatic replacement for Groovy's `XmlSlurper`, offering typed accessors and path-based
navigation over parsed XML structures.

No external dependencies beyond the JDK's `javax.xml.stream` (StAX) and `javax.xml.namespace`
packages are required.

---

## XML Tree API

### `XmlNode` sealed hierarchy

A sealed class hierarchy representing XML document nodes:

- `XmlElement` — an element with a qualified name, attributes, and ordered children
- `XmlText` — a text node (CDATA sections are merged into text)
- `XmlComment` — a comment node
- `XmlProcessingInstruction` — a processing instruction node
- `XmlAttribute` — a projection-only node (see [Attribute projection](#attribute-projection) below)

Every node exposes a `stringValue` property matching XPath's string-value semantics:

| Node type | `stringValue` returns |
|-----------|----------------------|
| `XmlElement` | Deep concatenation of all descendant text |
| `XmlText` | The text content |
| `XmlComment` | The comment body |
| `XmlProcessingInstruction` | The PI data |
| `XmlAttribute` | The attribute value |

### Element navigation

`XmlElement` provides direct navigation methods that do not require XPath:

```kotlin
val pom = """
    <project>
        <groupId>com.example</groupId>
        <artifactId>my-app</artifactId>
        <dependencies>
            <dependency><groupId>org.lib</groupId></dependency>
            <dependency><groupId>org.test</groupId></dependency>
        </dependencies>
    </project>
""".trimIndent().parseXml()

pom.element("groupId")?.stringValue           // "com.example"
pom.elements("dependencies")                  // List with one element
pom.element("dependencies")!!.elements("dependency")  // List with two elements
```

- `element(localName)` / `element(qname)` — first matching child element, or `null`
- `elements()` — all direct child elements
- `elements(localName)` / `elements(qname)` — child elements filtered by name

### Attribute access

Attributes are stored as `Map<QName, String>` on `XmlElement` and accessed via:

```kotlin
val item = """<item id="42" type="widget"/>""".parseXml()
item.attr("id")        // "42" (namespace-unaware, matches by local name)
item.attr(QName("http://example.com", "id"))  // namespace-aware lookup
item.attributes        // Map<QName, String> for direct iteration
```

### Namespace support

Element and attribute names use `javax.xml.namespace.QName` directly. The parser preserves
namespace URIs and prefixes as declared in the source document:

```kotlin
val xml = """<ns:root xmlns:ns="http://example.com"><ns:child/></ns:root>""".parseXml()
xml.name.namespaceURI   // "http://example.com"
xml.name.localPart      // "root"
xml.name.prefix         // "ns"
```

### Parsing

Extension functions for parsing XML into an `XmlElement` tree:

- `String.parseXml()` — parse an XML string
- `Reader.parseXml()` — parse from a `java.io.Reader`
- `InputStream.parseXml()` — parse from a `java.io.InputStream`

The parser is **non-validating**, does not process DTDs, and disables external entity resolution.
This matches `XmlSlurper`'s default behavior and prevents XML External Entity (XXE) attacks.

Whitespace-only text nodes between elements are discarded; significant text content (including
whitespace in mixed content) is preserved.

---

## XPath Queries (Partial Implementation)

`XPath` provides a **deliberately partial** implementation of XPath 1.0 for querying the `XmlNode`
tree. The supported subset covers the navigation patterns that `XmlSlurper` users actually rely on
in practice — child traversal, descendant search, attribute access, and simple filtering — without
the complexity of a full XPath expression evaluator.

### Supported syntax

| Syntax | Description | Example |
|--------|-------------|---------|
| `name` | Child element by local name | `groupId` |
| `prefix:name` | Child element by prefixed name | `ns:child` |
| `/` | Path step separator | `dependencies/dependency` |
| `//` | Descendant search | `//groupId` |
| `*` | Any child element (wildcard) | `dependencies/*` |
| `@attr` | Attribute access (terminal step) | `item/@id` |
| `text()` | Text node selection | `name/text()` |
| `node()` | Any child node | `root/node()` |
| `[n]` | Positional predicate (1-based) | `dependency[1]` |
| `[@attr='value']` | Attribute equality predicate | `item[@type='a']` |
| `[child]` | Child existence predicate | `item[sub]` |

### Deliberate omissions

The following XPath features are **intentionally unsupported**:

| Feature | Reason for omission |
|---------|---------------------|
| Axes (`parent::`, `ancestor::`, `following-sibling::`, etc.) | Upward/sibling navigation requires parent pointers, adding tree complexity. Navigate from root instead. |
| Functions (`count()`, `sum()`, `contains()`, `normalize-space()`, etc.) | Expression evaluation is a language-within-a-language. Kotlin collection operations are more readable and composable. |
| Arithmetic and comparison operators (`>`, `<`, `!=`, `+`) | Same rationale as functions — use Kotlin's type system for filtering logic. |
| Union operator (`\|`) | Call `query()` twice and concatenate. Simpler to understand. |
| Parent navigation (`..`) | Requires parent pointers. Restructure the query from root instead. |
| Array slices (`[position() > 3]`) | Positional filtering beyond indexing belongs in Kotlin code. |
| Namespace axis | Rarely used even in full XPath engines. Access `QName` properties directly. |

These are not missing due to incomplete implementation — they are excluded by design. The XPath
subset is a **query locator**, not an expression language. Complex filtering, transformation, and
aggregation are better expressed in Kotlin:

```kotlin
// Instead of XPath: //dependency[contains(@scope, 'test')]
root.query("//dependency")
    .filterIsInstance<XmlElement>()
    .filter { it.attr("scope")?.contains("test") == true }
```

### Usage

```kotlin
val pom = File("pom.xml").inputStream().parseXml()

// All matching nodes
pom.query("dependencies/dependency")           // List<XmlNode>
pom.query("//groupId")                         // descendants named "groupId"
pom.query("dependency[@scope='compile']")      // filtered by attribute

// Single value extraction
pom.queryOne("version")?.stringValue           // "1.0.0"
pom.queryString("artifactId")                  // "my-app" (shorthand)

// Attribute values via query
pom.queryStrings("dependencies/dependency/@scope")  // ["compile", "test"]
```

Convenience extensions on `XmlElement`:

- `query(path)` — returns `List<XmlNode>` of all matches
- `queryOne(path)` — returns single match or `null` (zero/multiple → `null`)
- `queryStrings(path)` — returns `List<String>` of string-values
- `queryString(path)` — returns single string-value or `null`

The `XPath` class can also be pre-parsed for repeated evaluation:

```kotlin
val path = XPath.parse("//dependency/@scope")
path.query(pom)  // reusable across multiple documents
```

### Attribute projection

XPath treats attributes as nodes on a separate axis. Since our tree model stores attributes as
`Map<QName, String>` on elements (not as child nodes), the query engine **projects** attribute
values as `XmlAttribute` instances in the result set when a path terminates with `@attr`:

```kotlin
val results = root.query("item/@id")
// results: [XmlAttribute(name=QName("id"), value="42"), ...]

// Use stringValue for uniform text extraction:
results.map { it.stringValue }  // ["42", ...]

// Or use the queryStrings shorthand:
root.queryStrings("item/@id")   // ["42", ...]
```

`XmlAttribute` never appears in `XmlElement.children` — it exists solely to keep the query return
type uniform (`List<XmlNode>`) without requiring a separate API for attribute queries.

---

## Migration from XmlSlurper

Common `XmlSlurper` patterns translate as follows:

| Groovy (`XmlSlurper`) | Kotlin (`xml-extensions`) |
|-----------------------|--------------------------|
| `new XmlSlurper().parseText(str)` | `str.parseXml()` |
| `root.child` | `root.element("child")` |
| `root.child.text()` | `root.element("child")?.stringValue` |
| `root.@attr` | `root.attr("attr")` |
| `root.children()` | `root.elements()` |
| `root.'**'.findAll { it.name() == 'x' }` | `root.query("//x")` |
| `root.deps.dep.findAll { it.@scope == 'test' }` | `root.query("deps/dep[@scope='test']")` |
| `root.deps.dep[0]` | `root.query("deps/dep[1]")` (1-based) |
