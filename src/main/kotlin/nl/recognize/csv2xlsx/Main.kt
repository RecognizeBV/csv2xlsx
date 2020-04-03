package nl.recognize.csv2xlsx

import com.opencsv.CSVReader
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.header
import io.ktor.request.receiveStream
import io.ktor.response.header
import io.ktor.response.respondOutputStream
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.InputStreamReader
import java.util.logging.Logger

val logger = Logger.getLogger("Application")
val excelContentType = ContentType.parse("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

fun main() {
    embeddedServer(Netty, 8090) {
        install(CallLogging)
        routing {
            post("/csv") {
                logger.info("Got new request")
                handleRequest(call)
            }
            get("/health") {
                call.respondText("OK")
            }
        }
    }.start(wait = true)
}

suspend fun handleRequest(call: ApplicationCall) {
    call.response.header("Content-Disposition", "attachment; filename=\"result.xlsx\"")
    if (call.request.header("Content-Type") != ContentType.Text.CSV.toString()) {
        call.respondText("Invalid Content-Type. Expecting text/csv", ContentType.Text.Plain, HttpStatusCode.BadRequest)
    }

    val csvReader = CSVReader(InputStreamReader(call.receiveStream()))

    val columnDefinitions = try {
        parseColumnDefinition(csvReader.readNext())
    } catch (e: InvalidColumnDefinition) {
        logger.warning(e.message)
        call.respondText(e.message, ContentType.Text.Plain, HttpStatusCode.BadRequest)

        return
    }

    call.respondOutputStream(excelContentType) {
        val excelExporter = ExcelExporter("Export", columnDefinitions)

        csvReader.forEach {
            excelExporter.appendRow(it)
        }

        excelExporter.writeTo(this)
    }
}

