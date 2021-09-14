/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shinsengumifinance;


import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 *
 * @author Vinicius
 */
public class Finance {
    
    Document doc;
    String name;
    Element in;
    Element out;
    Element balance;
    String bdpath;
    
    public Finance(String bdpathname){
        bdpath = bdpathname;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            doc = docBuilder.parse(new File(bdpath));
        } catch (SAXException | IOException ex) {
            Logger.getLogger(Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Element root = doc.getDocumentElement();
        name = root.getElementsByTagName("name").item(0).getTextContent();
        balance = (Element) root.getElementsByTagName("balance").item(0); 
        in = (Element) root.getElementsByTagName("in").item(0);
        out = (Element) root.getElementsByTagName("out").item(0);
    }
    
    public boolean deposit(String when, String why, String value){
        String fvalue = value;
        fvalue = fvalue.replaceAll("\\.", "");
        fvalue = fvalue.replaceAll(",", ".");
        Element deposit = doc.createElement("deposit");
        deposit.setAttribute("when", when);
        deposit.setAttribute("why", why);
        deposit.setTextContent(fvalue);
        in.appendChild(deposit);
        float newbalance = Float.parseFloat(balance.getTextContent()) + Float.parseFloat(fvalue);
        balance.setTextContent(Float.toString(newbalance));
        save();
        return true;
    }
    
    public boolean withdraw(String when, String why, String value){
        String fvalue = value;
        fvalue = fvalue.replaceAll("\\.", "");
        fvalue = fvalue.replaceAll(",", ".");
        Element withdraw = doc.createElement("withdraw");
        withdraw.setAttribute("when", when);
        withdraw.setAttribute("why", why);
        withdraw.setTextContent(fvalue);
        out.appendChild(withdraw);
        float newbalance = Float.parseFloat(balance.getTextContent()) - Float.parseFloat(fvalue);
        balance.setTextContent(Float.toString(newbalance));
        save();
        return true;
    }
    
    public String balance(){
        return balance.getTextContent();
    }
    
    public String[][] deposits(){
        String[][] deposits = new String[in.getElementsByTagName("deposit").getLength()][3];
        for(int i = 0; i < in.getElementsByTagName("deposit").getLength(); i++){
            deposits[i][0] = in.getElementsByTagName("deposit").item(i).getAttributes().item(0).getTextContent();
            deposits[i][1] = in.getElementsByTagName("deposit").item(i).getAttributes().item(1).getTextContent();
            deposits[i][2] = in.getElementsByTagName("deposit").item(i).getTextContent();
        }
        return deposits;
    }
    
    public String[][] withdraws(){
        String[][] withdraws = new String[out.getElementsByTagName("withdraw").getLength()][3];
        for(int i = 0; i < out.getElementsByTagName("withdraw").getLength(); i++){
            withdraws[i][0] = out.getElementsByTagName("withdraw").item(i).getAttributes().item(0).getTextContent();
            withdraws[i][1] = out.getElementsByTagName("withdraw").item(i).getAttributes().item(1).getTextContent();
            withdraws[i][2] = out.getElementsByTagName("withdraw").item(i).getTextContent();
        }
        return withdraws;
    }
    
    
    public void save(){        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
        DOMSource source = new DOMSource(doc);
        StreamResult streamResult =  new StreamResult(new File(bdpath));
        try {
            transformer.transform(source, streamResult);
        } catch (TransformerException ex) {
            Logger.getLogger(Finance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
