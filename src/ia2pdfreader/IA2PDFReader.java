/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia2pdfreader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 *
 * @author barbiero
 */
public class IA2PDFReader {

    static public List<List<String> > keywords = 
    new ArrayList<>(
        Arrays.asList(
            Arrays.asList(
                    "Financial",
                "Cognitve simulation",
                "Philosophical foundations"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            
            Arrays.asList(
                "Cartography",
                "Computer vision",
                "Decision support",
                "Education",
                "Games and infotainment",
                "Industrial automation",
                "Law",
                "Mathematics",
                "Medicine and science",
                "Military",
                "Natural language interfaces",
                "Office automation",
                "Space",
                "Transportation"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            Arrays.asList(
                "Automatic analysis of algorithms",
                "Program modification",
                "Program synthesis",
                "Program transformation",
                "Program verification"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            Arrays.asList(
                "Answer extraction",
                "reason extraction",
                "Constraint-based processing",
                "Deduction",
                "Inference engines",
                "Logic processing",
                "Logic programming",
                "Mathematical induction",
                "Metatheory",
                "Nonmonotonic reasoning",
                "belief revision",
                "Resolution",
                "Rule-based processing",
                "Uncertainty",
                "fuzzy",
                "probabilistic reasoning"

            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
        
    
            Arrays.asList(
                "Agent communication languages",
                "Distributed representations",
                "Frames and scripts",
                "Knowledge base management",
                "Knowledge base verification",
                "Modal logic",
                "Predicate logic",
                "Relation systems",
                "Representation languages",
                "Representations (procedural and rulebased)",
                "Semantic networks",
                "Storage mechanisms",
                "Temporal logic"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            Arrays.asList("Expert and knowledge-intensive system tools and techniques")
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            Arrays.asList(
                "Analogies",
                "Concept learning",
                "Connectionism and neural nets",
                "Heuristics design",
                "Induction",
                "Knowledge acquisition",
                "Machine learning",
                "Language acquisition",
                "Parameter learning"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),

            Arrays.asList(
                "Discourse",
                "Language generation",
                "Language models",
                "Language parsing and understanding",
                "Language summarization",
                "Machine translation",
                "Speech recognition and synthesis",
                "Text analysis",
                "Web text analysis"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            Arrays.asList(
                "Backtracking",
                "Constraint satisfaction",
                "Control theory",
                "Dynamic programming",
                "Graph and tree search strategies",
                "Heuristic methods",
                "Plan execution, formation, and generation",
                "Scheduling"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),

            Arrays.asList(
                "Autonomous vehicles",
                "Biorobotics",
                "Commercial robots and applications",
                "Kinematics and dynamics",
                "Manipulators",
                "Nanorobots",
                "Neuromorphic computing",
                "Operator interfaces",
                "Propelling mechanisms",
                "Sensors",
                "Workcell organization and planning",
                "Vision"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),

            Arrays.asList(
                "3D/stereo scene analysis",
                "Architecture and control structures",
                "Intensity, color, photometry, and thresholding",
                "Modeling and recovery of physical attributes",
                "Motion",
                "Perceptual reasoning",
                "Representations, data structures, and transforms",
                "Shape",
                "Texture",
                "Video analysis"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            Arrays.asList(
                "Coherence and coordination",
                "Intelligent agents",
                "Languages and structures",
                "Multiagent systems"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            Arrays.asList(
                "Intelligent Web service languages",
                "Internet reasoning services",
                "Ontology design",
                "Ontology languages"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList()),
        
            Arrays.asList(
                "Knowledge acquisition",
                "Knowledge engineering methodologies",
                "Knowledge life cycles",
                "Knowledge maintenance",
                "Knowledge modeling",
                "Knowledge personalization and customization",
                "Knowledge publishing",
                "Knowledge retrieval",
                "Knowledge reuse",
                "Knowledge valuation"
            )
            .stream()
            .map(s -> s.toLowerCase().replaceAll("\\s+",""))
            .collect(Collectors.toList())
        )
    );
    
    public static void main(String[] args) {

        
        String[] filesToRead = {
            "2.pdf", "6.pdf", "9.pdf", "25.pdf", "102.pdf", "227.pdf", "13.pdf", "14.pdf", "12.pdf"
        };
        
        try {
            for(String file : filesToRead) {
                PDDocument doc = PDDocument.load(new File("/home/barbiero/workspace/jcr/" + file));

                String text = new PDFTextStripper().getText(doc).toLowerCase().replaceAll("\\s+", "");
                System.out.println("********* file " + file + "************");
                for(int category = 0; category < keywords.size(); category++)
                {
                    System.out.println("CATEGORY " + category);
                    keywords.get(category).forEach( (String s) -> {
                        Pattern p = Pattern.compile(s);
                        Matcher m = p.matcher(text);
                        int count = 0;
                        while(m.find()) count++;

                        System.out.println(s+": " + count);
                    });
                    System.out.println("");
                }
            }
            
            
        } catch (IOException ex) {
            Logger.getLogger(IA2PDFReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
        /*//Abrindo um PDF
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
        double meanFrequency = (double)totalFrequency / (double)totalWords;
        
        //calculo da variancia
        double variance = wordFrequency.values().parallelStream()
                .map((value) -> ((value - meanFrequency) * (value - meanFrequency))/totalFrequency)
                .reduce(0.0, Double::sum, Double::sum);
        
        System.out.println("Total de palavras: " + totalWords);
        System.out.println("Total de frequencia: " + totalFrequency);
        System.out.println("Media de frequencia de palavras: " + String.format("%.2f", meanFrequency));
        System.out.println("Variancia: " + String.format("%.2f", variance));
        System.out.println("Desvio Padrao: " + String.format("%.2f", Math.sqrt(variance)));*/
}
