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
    
    static public void main(String... args) throws IOException {
        
        //DataSet trainingSet = IA2PDFReader.createTrainingSet(IA2PDFReader.pdfsDeIA, IA2PDFReader.pdfsNaoDeIA);
        //trainingSet.saveAsTxt("/home/barbiero/Documents/trainingSet.txt", " ");
        //trainingSet.save("trainingSet.ds");
        
        //DataSet toReadSet = IA2PDFReader.getDataFromAllFiles();
        //toReadSet.saveAsTxt("/home/barbiero/Documents/allFilesSet.txt", " ");
        //toReadSet.save("toReadSet.ds");
        
        DataSet trainingSet = DataSet.load("trainingSet.ds");
        DataSet toReadSet = DataSet.load("toReadSet.ds");
        
        NeuralNetwork net = new Perceptron(12, 1);
        System.out.println("Begin learning...");
        net.learn(trainingSet);
        System.out.println("Learning complete.");
        
        System.out.println("\nLet's test em all!");
        ArrayList<String> textosQueSaoDeIA = new ArrayList<>();
        
        for(DataSetRow dsr : toReadSet) {
            net.setInput(dsr.getInput());
            net.calculate();
            double[] output = net.getOutput();
            if(output[0] == 1.0) {
                textosQueSaoDeIA.add(dsr.getLabel());
            }
        }
        
        System.out.println("Textos que sao de IA: ");
        textosQueSaoDeIA.stream().map(s-> s+" ").forEach(System.out::print);
        System.out.println();
    }
    
}
