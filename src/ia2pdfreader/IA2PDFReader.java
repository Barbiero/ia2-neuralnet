/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia2pdfreader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    
    /**
     * Caminho da pasta em que os PDFs se encontram
     */
    static final public String PDF_FOLDER = "pdfs/";
    
    
    /**
     * Termos a serem verificados nos arquivos pdf
     */
    static String[] palavrasDeIA = {
                "inteligence",
                "artificial",
                "simulation",
                "learning",
                "machine",
                "genetic",
                "algorithm",
                "fuzzy",
                "selection",
                "optimal generation",
                "dynamic programming",
                "business",
                "artificial evaluation",
                "artificial simulation"
                
    };
    
    /**
     * Arquivos que NÃO SÃO de IA a serem usados para treinamento
     */
    static String[] pdfsNaoDeIA = {
        
        "1.pdf", "2.pdf", "7.pdf", "8.pdf", "12.pdf", "24.pdf", "71.pdf", "68.pdf", "14.pdf",
        "180.pdf", "308.pdf"
    };
    
    /**
     * Arquivos que SÃO de IA a serem usados para treinamento
     */
    static String[] pdfsDeIA = {
        "102.pdf", "95.pdf", "9.pdf", "13.pdf", "6.pdf", "339.pdf", "325.pdf",
        "21.pdf"
    };
    
    static DataSetRow getDataSetRowFromFile(File pdfFile)
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
        
        try(PDDocument doc = PDDocument.load(pdfFile) ) {
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
            dsr.setLabel(pdfFile.getName());
            return dsr;
        }
        catch (IOException ex) {
            Logger.getLogger(IA2PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    static DataSetRow getDataSetRowFromFile(String pdfFile)
    {
        File file = new File(PDF_FOLDER.concat(pdfFile));
        if(file.isFile()) {
            return getDataSetRowFromFile(file);
        }
        return null;
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
    
    static DataSet createTrainingSet(String[] pdfsDeIA, String[] pdfsNaoDeIA) {
        return createTrainingSet(Arrays.asList(pdfsDeIA), Arrays.asList(pdfsNaoDeIA));
    }
    
    static DataSet getDataFromAllFiles() throws IOException 
    {
        DataSet ds = new DataSet(palavrasDeIA.length);
        
        File pdfFolder = new File(PDF_FOLDER);
        File[] listOfFiles = pdfFolder.listFiles((dir, name) -> 
                name.substring(name.lastIndexOf('.')).equals(".pdf"));
        for(File file : listOfFiles) {
            if(file.isFile()){
                DataSetRow dsr = getDataSetRowFromFile(file);
                if(dsr != null) {
                    dsr.setDesiredOutput(new double[]{0.0});
                    ds.addRow(dsr);
                }
            }
        }
        
        //normalizar os dados para facil processamento
        new MaxMinNormalizer().normalize(ds);
        
        return ds;
    }
}
