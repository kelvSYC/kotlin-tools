# luxon-extensions

Kotlin/JS extensions bridging [Luxon](https://moment.github.io/luxon/) date/time types to
[kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) equivalents.

**Platform:** JS only.

**Dependencies consumers need:** `luxon` (npm) and `org.jetbrains.kotlinx:kotlinx-datetime` are
both `api` dependencies — they appear in public API signatures and must be on the consumer's
classpath.

---

## Type mapping

| Luxon type | Kotlin/kotlinx-datetime target | Notes |
|---|---|---|
| `LuxonDateTime` | `Instant` | Lossless via epoch milliseconds |
| `LuxonDuration` | `kotlin.time.Duration` | Sub-day only; lossy for calendar units |
| `LuxonDuration` | `DateTimePeriod` | Mostly lossless; Luxon weeks → days×7 |
| `LuxonInterval` | `ClosedRange<Instant>` | Interval must be bounded |

---

## Conversions

Each conversion pair provides both a `Converter<A, B>` (reusable, invertible) and convenience
extension functions that delegate to it.

### `LuxonDateTime` ↔ `Instant`

```kotlin
// Extension functions
fun LuxonDateTime.toKotlinInstant(): Instant
fun Instant.toLuxonDateTime(zone: String = "UTC"): LuxonDateTime

// Converter factory (zone is part of the A→B direction)
fun instantToLuxonDateTime(zone: String = "UTC"): Converter<Instant, LuxonDateTime>
```

### `LuxonDuration` ↔ `kotlin.time.Duration`

```kotlin
fun LuxonDuration.toKotlinDuration(): kotlin.time.Duration
fun kotlin.time.Duration.toLuxonDuration(): LuxonDuration
val luxonDurationToKotlinDuration: Converter<LuxonDuration, kotlin.time.Duration>
```

**Lossiness:** `toKotlinDuration()` calls `LuxonDuration.toMillis()` internally. Luxon's
millisecond total for calendar units (years, months) uses its own approximation (~30.4375
days/month). Round-tripping a calendar-unit duration through `toKotlinDuration()` and back does
not preserve years or months.

### `LuxonDuration` ↔ `DateTimePeriod`

```kotlin
fun LuxonDuration.toKotlinDateTimePeriod(): DateTimePeriod
fun DateTimePeriod.toLuxonDuration(): LuxonDuration
val luxonDurationToDateTimePeriod: Converter<LuxonDuration, DateTimePeriod>
```

**Lossiness:** Luxon weeks are folded into days (×7) during conversion to `DateTimePeriod`.
`DateTimePeriod` has no weeks field, so the information is preserved but the structure changes.
Sub-millisecond nanoseconds in `DateTimePeriod` are truncated to milliseconds on the Luxon side.

### `LuxonInterval` ↔ `ClosedRange<Instant>`

```kotlin
fun LuxonInterval.toInstantRange(): ClosedRange<Instant>
fun ClosedRange<Instant>.toLuxonInterval(): LuxonInterval
val luxonIntervalToInstantRange: Converter<LuxonInterval, ClosedRange<Instant>>
```

---

## Operator overloads

```kotlin
operator fun LuxonDateTime.plus(duration: kotlin.time.Duration): LuxonDateTime
operator fun LuxonDateTime.minus(duration: kotlin.time.Duration): LuxonDateTime
operator fun LuxonDateTime.rangeTo(other: LuxonDateTime): LuxonInterval
```

These enable natural Kotlin syntax:

```kotlin
val later = someDateTime + 5.seconds
val interval = start..end   // LuxonInterval
```

---

## External types

`LuxonDateTime`, `LuxonDuration`, and `LuxonInterval` are Kotlin `external class` declarations
backed by the corresponding Luxon JS classes. Constructing them directly is not possible from
Kotlin — use the factory functions on `LuxonModule.DateTime`, `LuxonModule.Duration`, and
`LuxonModule.Interval` (internal), or the conversion functions above.
