/*
 * The MIT License
 *
 * Copyright 2017 barbiero.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ia2pdfreader;

import java.io.IOException;
import java.util.ArrayList;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.Perceptron;

/**
 * Classe para usar a rede neural com o Neuroph
 * @author barbiero
 */
public class UseNeuralNtw {
    
    /**
     * coloca como 'true' se deseja recriar conjuntos de dados
     */
    static public final boolean CREATE_NEW_SETS = false;
    
    /**
     * coloca como 'true' se deseja re-treinar a rede neural
     */
    static public final boolean CREATE_NEW_NNET = true;
    
    static public void main(String... args) throws IOException {
        
        DataSet trainingSet, toReadSet;
        NeuralNetwork net;
        
        if(CREATE_NEW_SETS) {
            trainingSet = IA2PDFReader.createTrainingSet(IA2PDFReader.pdfsDeIA, IA2PDFReader.pdfsNaoDeIA);
            trainingSet.save("trainingSet.ds");

            toReadSet = IA2PDFReader.getDataFromAllFiles();
            toReadSet.save("toReadSet.ds");
        }
        
        if(CREATE_NEW_NNET) {
            trainingSet = DataSet.load("trainingSet.ds");
            
            net = new Perceptron(IA2PDFReader.palavrasDeIA.length, 1);
            System.out.println("Begin learning...");
            net.learn(trainingSet);
            System.out.println("Learning complete.");
            net.save("trainedNetwork.nnet");
        } else {
            System.out.println("Reading NNet from file trainedNetwork.nnet");
            net = NeuralNetwork.createFromFile("trainedNetwork.nnet");
            System.out.println("Done.");
        }
        
        toReadSet = DataSet.load("toReadSet.ds");
        
        
        System.out.println("\nLet's read em all!");
        ArrayList<String> textosQueSaoDeIA = new ArrayList<>();
        
        for(DataSetRow dsr : toReadSet) {
            net.setInput(dsr.getInput());
            net.calculate();
            double[] output = net.getOutput();
            if(output[0] == 1.0) {
                textosQueSaoDeIA.add(dsr.getLabel());
            }
        }
        
        //ordenação para a saida sair organizada
        textosQueSaoDeIA.sort((s1, s2) -> {
            Integer pdf_num = Integer.parseInt(s1.replaceAll("\\D", ""));
            Integer pdf2_num = Integer.parseInt(s2.replaceAll("\\D", ""));
            
            return pdf_num.compareTo(pdf2_num);
        });
        
        System.out.println("AI texts: ");
        textosQueSaoDeIA.forEach((s) -> System.out.print(s+" "));
        System.out.println();
    }
    
}
