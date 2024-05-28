package com.silvia.easybizzmanager3.ui.factura


import android.content.Context
import android.util.Log
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.layout.element.Paragraph
import com.silvia.easybizzmanager3.models.Factura
import java.io.File
import java.io.FileOutputStream
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.properties.VerticalAlignment


class GeneradorPDFFactura(private val context: Context) {
    fun generarYGuardarFacturaPdf(factura: Factura): String {
        val outputStream = ByteArrayOutputStream()
        val pdfWriter = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Agregar título
        val titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        val title = Paragraph("Factura  #${factura.numeroFactura}")
            .setFont(titleFont)
            .setFontSize(24f)
            .setMarginBottom(20f)
        document.add(title)
        // Información de la empresa y cliente
        val infoEmpresaCliente = Paragraph()
        infoEmpresaCliente.add("Información de la empresa:\n")
        infoEmpresaCliente.add("Nombre: ${factura.detallesPerfil.nombreEmpresa}\n")
        if (!factura.detallesPerfil.numeroEmpresa.isNullOrEmpty()){
            infoEmpresaCliente.add("Número de empresa: ${factura.detallesPerfil.numeroEmpresa}\n")
        }
        if (!factura.detallesPerfil.direccion.isNullOrEmpty()){
            infoEmpresaCliente.add("Dirección: ${factura.detallesPerfil.direccion}\n")
        }
        if (factura.detallesPerfil.numeroContacto!=null){
            infoEmpresaCliente.add("Teléfono: ${factura.detallesPerfil.numeroContacto!!.prefix + factura.detallesPerfil.numeroContacto!!.number}\n")
        }
        if (!factura.detallesPerfil.correoContacto.isNullOrEmpty()){
            infoEmpresaCliente.add("Correo electrónico: ${factura.detallesPerfil.correoContacto}\n\n")
        }


        infoEmpresaCliente.add("Información del cliente:\n")
        infoEmpresaCliente.add("Nombre: ${factura.comprador.nombre} ${factura.comprador.apellidos}\n")
        if (!factura.comprador.correo.isNullOrEmpty()){
            infoEmpresaCliente.add("Correo electrónico: ${factura.comprador.correo}\n")
        }
        if (!factura.comprador.direccion.isNullOrEmpty()){
            infoEmpresaCliente.add("Dirección: ${factura.comprador.direccion}\n")
        }
        if (factura.comprador.number!=null){
            infoEmpresaCliente.add("Teléfono: ${factura.comprador.number!!.prefix + factura.comprador.number!!.number}\n")
        }
        // Agregar párrafos al documento
        document.add(infoEmpresaCliente)

        // Agregar una tabla de productos/servicios
        // Calcular el ancho de cada columna
        val totalWidth = PageSize.A4.width - document.leftMargin - document.rightMargin
        val columnWidths = floatArrayOf(totalWidth * 0.5f, totalWidth * 0.125f, totalWidth * 0.125f, totalWidth * 0.125f)

        val table = Table(columnWidths)
        table.addCell("Descripción")
        table.addCell("Cantidad")
        table.addCell("Precio Unitario")
        table.addCell("Total")

        // Agregar productos a la tabla
        factura.serviciosProductos?.forEach { item ->
            table.addCell(item.nombre)
            table.addCell(item.cantidad.toString())
            table.addCell("$${item.precioUnitario}")
            table.addCell("$${item.precioUnitario * item.cantidad}")
        }

        // Agregar la tabla al documento
        document.add(table)

        // Agregar el total final
        val totalParagraph = Paragraph().setMarginTop(20f)


        if (factura.impuestos!=0){
            totalParagraph.add("Impuesto: ${factura.impuestos}%\n").setFontSize(12f)
        }
        if (factura.descuento!=0){
            totalParagraph.add("Descuento: ${factura.descuento}%\n").setFontSize(12f)
        }
        totalParagraph.add("Total: ${factura.importeTotal}€\n")
            .setFontSize(18f)

        document.add(totalParagraph)

        // Cerrar el documento
        document.close()

        // Guardar el archivo PDF en almacenamiento interno
        val pdfFile = File(context.filesDir, "factura_${factura.numeroFactura}.pdf")
        val fos = FileOutputStream(pdfFile)
        fos.write(outputStream.toByteArray())
        fos.close()
        if (pdfFile.exists()){
            Log.d("PDF Path", pdfFile.absolutePath)
        }

        // Retornar la ruta del archivo PDF
        return pdfFile.absolutePath

    }
}