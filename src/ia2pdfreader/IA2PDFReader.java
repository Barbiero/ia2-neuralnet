/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia2pdfreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.data.norm.MaxMinNormalizer;

/**
 *
 * @author barbiero
 */
public class IA2PDFReader {
    
    static String[] palavrasDeIA = {
                "inteligence",
                "artificial",
                "simulation",
                "learning",
                "machine",
                "genetic",
                "algorithm",
                "perceptron",
                "fuzzy",
                "selection",
                "optimalgeneration",
                "dynamicprogramming"
    };
    
    static String[] pdfsNaoDeIA = {
        
        "1.pdf", "7.pdf", "8.pdf", "12.pdf", "24.pdf", "71.pdf", "68.pdf", "14.pdf",
        "180.pdf", "308.pdf"
    };
    static String[] pdfsDeIA = {
        "2.pdf", "102.pdf", "95.pdf", "9.pdf", "13.pdf", "6.pdf", "339.pdf", "325.pdf",
        "21.pdf"
    };
    
    static final public String PDF_FOLDER = "/home/barbiero/workspace/jcr/";
    
    static public class DocInfo {
        String file;
        String text;
        int totalWords;
        SortedMap<String, Double> wordFrequency = new TreeMap<>();
    }
    
    
    
    static DataSetRow getDataSetRowFromFile(String pdfFile)
    {
        PDFTextStripper stripper = null;
        try {
            stripper = new PDFTextStripper();
        } catch (IOException ex) {
            Logger.getLogger(IA2PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(stripper == null){
            return null;
        }
        
        try(PDDocument doc = PDDocument.load(new File(PDF_FOLDER.concat(pdfFile))) ) {
            String text = stripper.getText(doc).toLowerCase();

            int totalWords = text.split("(?U)[^\\p{Alpha}0-9']+").length;
            text = text.replaceAll("\\s+", "");

            double[] inputs = new double[palavrasDeIA.length];
            for(int i = 0; i < palavrasDeIA.length; i++) {
                Pattern p = Pattern.compile(palavrasDeIA[i]);
                Matcher m = p.matcher(text);
                int count = 0;
                while(m.find()) count++;

                inputs[i] = (double)count / (double)totalWords;
            }
            DataSetRow dsr = new DataSetRow(inputs);
            dsr.setLabel(pdfFile);
            return dsr;
        } catch(FileNotFoundException ex) {
            return null;
        }
        catch (IOException ex) {
            Logger.getLogger(IA2PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    static DataSet createTrainingSet(String[] pdfsDeIA, String[] pdfsNaoDeIA) {
        return createTrainingSet(Arrays.asList(pdfsDeIA), Arrays.asList(pdfsNaoDeIA));
    }
    
    
    static DataSet createTrainingSet(List<String> pdfsDeIA, List<String> pdfsNaoDeIA)
    {
        DataSet ds = new DataSet(palavrasDeIA.length, 1); //(entradas, saida)
        //ds.setColumnNames(palavrasDeIA);
        //Ler arquivos de IA
        pdfsDeIA.forEach(file -> {
            DataSetRow dsr = getDataSetRowFromFile(file);
            if(dsr != null) { 
                dsr.setDesiredOutput(new double[]{1.0});
                ds.addRow(dsr); }
        });
        //agora os de nao ia
        pdfsNaoDeIA.forEach(file -> {
            DataSetRow dsr = getDataSetRowFromFile(file);
            if(dsr != null) {    
                dsr.setDesiredOutput(new double[]{0.0});
                ds.addRow(dsr);
            }
        });
        
        //normalizar os dados para facil processamento
        new MaxMinNormalizer().normalize(ds);
        
        return ds;
    }
    
    static DataSet getDataFromAllFiles() throws IOException 
    {
        DataSet ds = new DataSet(palavrasDeIA.length);
        
        IntStream.rangeClosed(1, 341).forEach(i -> {
            String file = i + ".pdf";
            DataSetRow dsr = getDataSetRowFromFile(file);
            if(dsr != null) {
                dsr.setDesiredOutput(new double[]{0.0});
                ds.addRow(dsr);
            }
        });
        //normalizar os dados para facil processamento
        new MaxMinNormalizer().normalize(ds);
        
        return ds;
    }
    
    static List<DocInfo> readInputFromAllFiles() throws IOException
    {
        //espera-se que existam ~275 arquivos, assim o map tem que trabalhar menos
        //durante a criação
        List<DocInfo> fileInputs = new ArrayList<>(275);
        
        //singleton!
        PDFTextStripper stripper = new PDFTextStripper();
        
        IntStream.rangeClosed(1, 341).forEach(i -> {
            try{
                PDDocument doc = null;
                try {
                    doc = PDDocument.load(new File("/home/barbiero/workspace/jcr/" + i + ".pdf"));
                } catch(FileNotFoundException e) {
                    //arquivo nao existe, ignorar
                }
                
                if(doc == null) {return;}
            
                DocInfo dInfo = new DocInfo();
                dInfo.file = i + ".pdf";
                
                {
                    //escopo avulso mantem o tempo de vida de 'tmpText' o mais curto possivel
                    String tmpText = stripper.getText(doc).toLowerCase();

                    dInfo.totalWords = tmpText.split("(?U)[^\\p{Alpha}0-9']+").length;
                    dInfo.text = tmpText.replaceAll("\\s+", "");
                }
                
                for(String s : palavrasDeIA) {
                    Pattern p = Pattern.compile(s);
                    Matcher m = p.matcher(dInfo.text);
                    int count = 0;
                    while(m.find()) count++;
                    
                    dInfo.wordFrequency.put(s, (double)count / (double)dInfo.totalWords);
                }
                
                fileInputs.add(dInfo);
                doc.close();
                
            } catch(IOException ex) {
                Logger.getLogger(IA2PDFReader.class.getName()).log(Level.SEVERE, null, ex);
                
            }
        });
        return fileInputs;
    }
    
}
