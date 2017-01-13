/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia2pdfreader;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author barbiero
 */
public class IA2PDFReader {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        //Abrindo um PDF
        String filename = "pdfs/1.pdf";
        System.out.println("Lendo arquivo " + filename);
        
        PDDocument doc = PDDocument.load(new File(filename));
        String text = new PDFTextStripper().getText(doc);
        List<String> words = Arrays.asList(text.split("(?U)[^\\p{Alpha}0-9']+"));
        
        Map<String, Integer> wordFrequency = words.parallelStream().
                collect(Collectors.toConcurrentMap(w -> w, w -> 1, Integer::sum));
        
        int totalFrequency = wordFrequency.values().parallelStream().reduce(0, Integer::sum, Integer::sum);
        
        int totalWords = wordFrequency.keySet().size();
        //media de frequencia das palavras
        double meanFrequency = (double)totalWords / (double)totalFrequency;
        
        //calculo da variancia
        double variance = wordFrequency.values().parallelStream()
                .map((value) -> ((value - meanFrequency) * (value - meanFrequency))/totalFrequency)
                .reduce(0.0, Double::sum, Double::sum);
        
        /*System.out.println("Frequencia de palavras: ");
        wordFrequency.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .filter(es -> {return es.getValue() > 10;}).forEach((Map.Entry es) -> {
            System.out.println(es.getKey() + " -> " + es.getValue());
        });*/
        System.out.println("Total de palavras: " + totalWords);
        System.out.println("Total de frequencia: " + totalFrequency);
        System.out.println("Media de frequencia de palavras: " + String.format("%.2f", meanFrequency));
        System.out.println("Variancia: " + String.format("%.2f", variance));
        System.out.println("Desvio Padrao: " + String.format("%.2f", Math.sqrt(variance)));
    }
    
}
