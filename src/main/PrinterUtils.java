package main;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class PrinterUtils 
{
	private PrintService[] printers;
	
	private static PrinterUtils instance;
	
	private PrinterUtils()
	{
		DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
	    PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
	    patts.add(Sides.DUPLEX);
	    printers = PrintServiceLookup.lookupPrintServices(flavor, patts);
	 
	    Utils.log("Found " + printers.length + " available printers", Main.printing);
	}
	
	
	private PrintService getPrinter(String name)
	{
		 for (PrintService printService : printers) 
		 {
			 if (printService.getName().equals("Your printer name"))
			 {
				 return printService;
			 }
		 }
		 return null;
	}
	
	public static PrinterUtils getInstance()
	{
		if (instance==null)
		{
			instance= new PrinterUtils();
		}
		return instance;
	}
	
	public void print(File f) throws IOException, PrinterException
	{
		String printername=Utils.getIstance().get("defaultPrinter");
		PrintService p = getPrinter(printername);
		if (p==null)
		{
			Utils.log("Selected printer ("+printername+") not found", Main.error);
			return;
		}
		
		PDDocument document = PDDocument.load(f);
		PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        job.setPrintService(p);
        job.print();

	}
}
