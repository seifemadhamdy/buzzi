package seifemadhamdy.buzzi.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.toFriendlyDate(): String = runCatching {
  when (val date = Instant.parse(this).atZone(ZoneId.systemDefault()).toLocalDate()) {
    LocalDate.now() -> "Today"
    LocalDate.now().minusDays(1) -> "Yesterday"
    else -> date.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
  }
}
  .getOrDefault(this)
