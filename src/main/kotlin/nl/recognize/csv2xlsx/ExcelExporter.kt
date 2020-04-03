package nl.recognize.csv2xlsx

import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.OutputStream
import java.time.ZonedDateTime

class ExcelExporter(sheetName: String, val columnDefinitions: List<CsvColumnDefinition>) {
    private val workbook = XSSFWorkbook()
    private val sheet = workbook.createSheet(sheetName)
    private val dateCellStyle = createDateCellStyle("dd-MM-yyyy")
    private val dateTimeCellStyle = createDateCellStyle("dd-MM-yyyy hh:mm")

    private var currentRow = 1

    init {
        writeHeader()
    }

    fun appendRow(rawColumns: Array<String>) {
        check(rawColumns.size == columnDefinitions.size) {
            "Invalid number of columns. Expected ${columnDefinitions.size} got ${rawColumns.size}"
        }

        val row = sheet.createRow(currentRow)

        columnDefinitions.forEachIndexed { index, definition ->
            val cell = row.createCellForType(index, definition.type)

            when (definition.type) {
                CsvColumnType.Date -> cell.setCellValue(ZonedDateTime.parse(rawColumns[index]).toLocalDate())
                CsvColumnType.DateTime -> cell.setCellValue(ZonedDateTime.parse(rawColumns[index]).toLocalDateTime())
                CsvColumnType.Number -> cell.setCellValue(rawColumns[index].toDouble())
                else -> cell.setCellValue(rawColumns[index])
            }
        }

        currentRow += 1
    }

    fun writeTo(outputStream: OutputStream) = workbook.write(outputStream)

    private fun writeHeader() {
        val row = sheet.createRow(0)

        columnDefinitions.forEachIndexed { index, definition ->
            row.createCell(index).setCellValue(definition.label)
        }
    }

    private fun XSSFRow.createCellForType(index: Int, type: CsvColumnType): XSSFCell =
        this.createCell(index).apply {
            if (type == CsvColumnType.Date) {
                cellStyle = dateCellStyle
            } else if (type == CsvColumnType.DateTime) {
                cellStyle = dateTimeCellStyle
            }
        }

    private fun createDateCellStyle(format: String) = workbook.createCellStyle().apply {
        dataFormat = workbook.creationHelper.createDataFormat().getFormat(format)
    }
}
