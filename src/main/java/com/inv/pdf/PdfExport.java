package com.inv.pdf;

import com.inv.data.access.Settings;
import com.inv.data.access.orderutil.OrderUtil;
import com.inv.ui.ctrl.dialogs.Message;
import com.inv.ui.util.DoubleFormat;
import com.inv.xflux.entity.Company;
import com.inv.xflux.entity.Currency;
import com.inv.xflux.entity.Item;
import com.inv.xflux.entity.Order;
import com.inv.xflux.model.CompanyModel;
import com.inv.xflux.model.CurrencyModel;
import com.itextpdf.text.Font;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.scene.Node;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("Duplicates")
public class PdfExport {

    /**
     * Create Pdf from order
     * @param order Order
     */
    public static void makePDF(Order order, Node node) {
        Runnable task=()->{
            List<Item> items = order.getItems();
            //Get Currencies
            CurrencyModel currencyModel=new CurrencyModel();
            Currency currency=currencyModel.getDefault();
            Company company=new CompanyModel().getDefault();
            double discountTotal=0;
            Date curDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyy");
            String date = format.format(curDate);
            String invoiceFileName=Settings.getInvoicesFolderPath()+
                    order.getClient().getName()+"_"+OrderUtil.getOrderDate(order.getDate()) +".pdf";

            //Check if file dont exist to create it
            if (Files.exists(Paths.get(invoiceFileName))){
                try {
                    Files.delete(Paths.get(invoiceFileName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!Files.exists(Paths.get(invoiceFileName))){
                try {
                    OutputStream file = new FileOutputStream(new File(invoiceFileName));
                    Document document = new Document();
                    PdfWriter.getInstance(document, file);
                    /*
                     * Invoice Company and Client Data
                     */
                    FontSelector fs = new FontSelector();
                    fs.addFont(getFontH12Black());
                    Phrase billFrom= fs.process("Bill From:");

                    Paragraph companyName=new Paragraph(company.getName(),getFontH10Gray());
                    companyName.setIndentationLeft(20);

                    Paragraph compAddress=new Paragraph("Address: "+company.getAddress(),getFontH10Gray());
                    compAddress.setIndentationLeft(20);
                    Paragraph compContactPhone=new Paragraph("Phone: "+company.getPhone(),getFontH10Gray());
                    compContactPhone.setIndentationLeft(20);
                    Paragraph compContactFax=new Paragraph("Fax: "+company.getFax(),getFontH10Gray());
                    compContactFax.setIndentationLeft(20);
                    Paragraph comContactEmail=new Paragraph("Email: "+company.getEmail(),getFontH10Gray());
                    comContactEmail.setIndentationLeft(20);
                    //Bill To
                    Phrase bill = fs.process("Bill To:"); // customer information
                    Paragraph name = new Paragraph("Name: "+order.getClient().getName(), getFontH10Gray());
                    name.setIndentationLeft(20);
                    Paragraph contact = new Paragraph("Tel: "+order.getClient().getPhone(), getFontH10Gray());
                    contact.setIndentationLeft(20);
                    Paragraph address = new Paragraph("Email: "+order.getClient().getEmail(), getFontH10Gray());
                    address.setIndentationLeft(20);

                    /*
                     * Invoice Properties
                     */
                    PdfPTable irdTable = new PdfPTable(2);
                    irdTable.addCell(getIRDCell("Invoice #"));
                    irdTable.addCell(getIRDCell("Invoice Date"));
                    irdTable.addCell(getIRDCell(""+order.getId())); // pass invoice number
                    irdTable.addCell(getIRDCell(date)); // pass invoice date

                    PdfPTable irhTable = new PdfPTable(3);
                    irhTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    irhTable.setWidthPercentage(100);

                    irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
                    irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
                    irhTable.addCell(getIRHCell("Invoice", PdfPCell.ALIGN_RIGHT));
                    irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
                    irhTable.addCell(getIRHCell("", PdfPCell.ALIGN_RIGHT));
                    PdfPCell invoiceTable = new PdfPCell(irdTable);
                    invoiceTable.setBorder(0);
                    irhTable.addCell(invoiceTable);

                    /*
                     * Invoice Products
                     */
                    PdfPTable billTable = new PdfPTable(6);
                    billTable.setWidthPercentage(100);
                    billTable.setWidths(new float[]{3, 4, 2, 2, 2, 2});
                    billTable.setSpacingBefore(30.0f);
                    billTable.addCell(getBillHeaderCell("Service/Product"));
                    billTable.addCell(getBillHeaderCell("Description"));
                    billTable.addCell(getBillHeaderCell("Unit Price"));
                    billTable.addCell(getBillHeaderCell("Discount"));
                    billTable.addCell(getBillHeaderCell("Qty"));
                    billTable.addCell(getBillHeaderCell("Amount"));

                    //Insert all Products Data
                    for (Item item : items) {
                        billTable.addCell(getBillRowCell(item.getProduct().getName()));
                        billTable.addCell(getBillRowCell(item.getProduct().getDescription()));
                        billTable.addCell(getBillRowCell(currency.getSymbol() + DoubleFormat.sFormat(item.getProduct().getSellPrice())));
                        billTable.addCell(getBillRowCell(DoubleFormat.sFormat(item.getDiscount().get())));
                        billTable.addCell(getBillRowCell(item.getQuantity().get() +
                                "/" + item.getProduct().getUnit().getSymbol()));
                        billTable.addCell(getBillRowCell(currency.getSymbol() + DoubleFormat.sFormat(item.getTotal().get())));
                        discountTotal = discountTotal + item.getDiscount().get();
                    }


                    //Fix Table Size
                    /*for (int j = 0; j < 15 - items.size(); j++) {
                        billTable.addCell(getBillRowCell(" "));
                        billTable.addCell(getBillRowCell(""));
                        billTable.addCell(getBillRowCell(""));
                        billTable.addCell(getBillRowCell(""));
                        billTable.addCell(getBillRowCell(""));
                        billTable.addCell(getBillRowCell(""));
                    }*/

                    PdfPTable validity = new PdfPTable(1);
                    validity.setWidthPercentage(100);
                    validity.addCell(getValidityCell(" "));
                    validity.addCell(getValidityCell("Warranty"));
                    validity.addCell(getValidityCell(" * Products purchased comes with no warranty with the Store.\n"));
                    validity.addCell(getValidityCell(" * Warranty should be claimed only from the respective manufactures"));
                    PdfPCell summaryL = new PdfPCell(validity);
                    summaryL.setColspan(3);
                    summaryL.setPadding(1.0f);
                    billTable.addCell(summaryL);

                    PdfPTable accounts = new PdfPTable(2);
                    accounts.setWidthPercentage(100);
                    accounts.addCell(getAccountsCell("Subtotal"));
                    accounts.addCell(getAccountsCellR(currency.getSymbol()+DoubleFormat.sFormat(order.getTotal())));
                    accounts.addCell(getAccountsCell("Discount"));
                    accounts.addCell(getAccountsCellR(currency.getSymbol() + DoubleFormat.sFormat(discountTotal)));
                    accounts.addCell(getAccountsCell("Tax (" + DoubleFormat.sFormat(order.getTax().getTaxVal()) + "%)"));
                    accounts.addCell(getAccountsCellR(currency.getSymbol()+DoubleFormat.sFormat(order.getTotalWithTax() - order.getTotal())));
                    accounts.addCell(getAccountsCell("Total"));
                    accounts.addCell(getAccountsCellR(currency.getSymbol()+DoubleFormat.sFormat(order.getTotalWithTax())));
                    PdfPCell summaryR = new PdfPCell(accounts);
                    summaryR.setColspan(3);
                    billTable.addCell(summaryR);

                    PdfPTable describer = new PdfPTable(1);
                    describer.setWidthPercentage(100);
                    describer.addCell(getFooterNoteCell(" "));
                    describer.addCell(getFooterNoteCell("Goods once sold will not be taken back or exchanged || Subject to product justification || Product damage no one responsible || "
                            + " Service only at concerned authorized service centers"));

                    document.open();//PDF document opened........

                    //document.add(image);
                    document.add(billFrom);
                    document.add(companyName);
                    document.add(compAddress);
                    document.add(compContactPhone);
                    document.add(compContactFax);
                    document.add(comContactEmail);

                    document.add(irhTable);

                    document.add(bill);
                    document.add(name);
                    document.add(contact);
                    document.add(address);
                    document.add(billTable);
                    document.add(describer);

                    document.close();
                    file.close();
                    launchPdfApp(invoiceFileName,node);

                } catch (Exception e) {
                    Platform.runLater(()->{
                        Message.showMessage(node.getScene().getWindow(),"Error!",e.getMessage(),Message.MESSAGE_TYPE_ERROR);
                    });
                }
            }
        };
        Thread createPdf=new Thread(task);
        createPdf.start();
    }

    /**
     * Create IRH Cell
     * @param text Content
     * @param alignment Alignment
     * @return Cell
     */
    private static PdfPCell getIRHCell(String text, int alignment) {
        FontSelector fs = new FontSelector();
        fs.addFont(getFontH16Black());
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    /**
     * Create IRD Cell
     * @param text Content
     * @return Cell
     */
    private static PdfPCell getIRDCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text,getFontH10Gray()));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    /**
     * Create Bill Header Cell
     * @param text Content
     * @return Header Cell
     */
    private static PdfPCell getBillHeaderCell(String text) {
        FontSelector fs = new FontSelector();
        fs.addFont(getFontH10Black());
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        return cell;
    }

    /**
     * Create Bill Row Cell
     * @param text Content
     * @return Cell
     */
    private static PdfPCell getBillRowCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text,getFontH10Gray()));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    /**
     * Create Bill Footer Cell
     * @param text Content
     * @return Cell
     */
    private static PdfPCell getBillFooterCell(String text) {
        PdfPCell cell = new PdfPCell (new Paragraph (text,getFontH10Black()));
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setPadding (5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    /**
     * Create Validation Cell
     * @param text Content
     * @return Cell
     */
    private static PdfPCell getValidityCell(String text) {
        FontSelector fs = new FontSelector();
        fs.addFont(getFontH10Black());
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorder(0);
        return cell;
    }

    /**
     * Create Accounts Cell
     * @param text Content
     * @return Cell
     */
    private static PdfPCell getAccountsCell(String text) {
        FontSelector fs = new FontSelector();
        fs.addFont(getFontH10Black());
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setPadding (5.0f);
        return cell;
    }

    /**
     * Create Accounts Cell R
     * @param text Content
     * @return Cell R
     */
    private static PdfPCell getAccountsCellR(String text) {
        FontSelector fs = new FontSelector();
        fs.addFont(getFontH10Gray());
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
        cell.setPadding (5.0f);
        cell.setPaddingRight(20.0f);
        return cell;
    }

    /**
     * Create Footer Note Cell
     * @param text Content
     * @return Cell
     */
    private static PdfPCell getFooterNoteCell(String text) {
        FontSelector fs = new FontSelector();
        fs.addFont(getFontH10Black());
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell (phrase);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        cell.setBorder(0);
        return cell;
    }

    /**
     * Create Font Helvetica 10px Gray
     * @return Font
     */
    private static Font getFontH10Gray(){
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        return font;
    }

    /**
     * Create Font Helvetica 10px black
     * @return Font
     */
    private static Font getFontH10Black(){
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.BLACK);
        return font;
    }

    /**
     * Create Font Helvetica 16px Gray
     * @return Font
     */
    private static Font getFontH16Gray(){
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        font.setColor(BaseColor.GRAY);
        return font;
    }

    /**
     * Create Font Helvetica 16px Black
     * @return Font
     */
    private static Font getFontH16Black(){
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        font.setColor(BaseColor.BLACK);
        return font;
    }

    /**
     * Create Font Helvetica 12px Black
     * @return Font
     */
    private static Font getFontH12Black(){
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12);
        font.setColor(BaseColor.BLACK);
        return font;
    }

    /**
     * Create Font Helvetica 12px Gray
     * @return Font
     */
    private static Font getFontH12Gray(){
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12);
        font.setColor(BaseColor.GRAY);
        return font;
    }

    /**
     * Launch Adobe reader or any pdf app and load the file
     * @param location files path
     */
    private static void launchPdfApp(String location, Node node){
        Runnable task=()->{
            if (Desktop.isDesktopSupported()) {
                if (Files.exists(Paths.get(location))) {
                    try {
                        File myFile = new File(location);
                        Desktop.getDesktop().open(myFile);
                    } catch (IOException ex) {
                        Platform.runLater(()->{
                            Message.showMessage(node.getScene().getWindow(),"Error!", ex.getMessage(),Message.MESSAGE_TYPE_ERROR);
                        });
                    }
                }else {
                    Platform.runLater(()->{
                        Message.showMessage(node.getScene().getWindow(),"File not Found!","The Requested file don't Exist!",Message.MESSAGE_TYPE_ERROR);
                    });
                }
            }else {
                Platform.runLater(()->{
                    Message.showMessage(node.getScene().getWindow(),"Error!","Action not Supported!",Message.MESSAGE_TYPE_ERROR);
                });
            }
        };
        Thread openFile=new Thread(task);
        openFile.start();
    }


    public static PdfPCell getCompanyData(Company company) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.addElement(new Paragraph(company.getName(), getFontH12Black()));
        cell.addElement(new Paragraph(company.getEmail(), getFontH12Black()));
        cell.addElement(new Paragraph(company.getPhone(), getFontH12Black()));
        cell.addElement(new Paragraph(company.getAddress(), getFontH12Black()));
        //cell.addElement(new Paragraph(String.format("%s-%s %s", "countryID", "postcode", "city"), getFontH12Black()));
        return cell;
    }
}
