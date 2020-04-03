package nl.recognize.csv2xlsx

data class CsvColumnDefinition(
    val label: String,
    val type: CsvColumnType
)

enum class CsvColumnType {
    String,
    Number,
    Date,
    DateTime
}

class InvalidColumnDefinition(override val message: String) : Exception(message)

fun parseColumnDefinition(headerRow: Array<String>): List<CsvColumnDefinition> {
    return headerRow.map { column ->
        if (!column.contains(":")) {
            throw InvalidColumnDefinition("Invalid column format. Expecting label:type")
        }
        val (label, type) = column.split(":")

        val columnType = CsvColumnType.values().find { columnType -> columnType.toString().toLowerCase() == type.toLowerCase() }
            ?: throw InvalidColumnDefinition("Unknown ColumnType: $column")

        CsvColumnDefinition(label, columnType)
    }
}
