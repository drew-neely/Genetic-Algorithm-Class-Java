import java.util.ArrayList;

/**
 * @author Drew Neely
 */
 
interface GeneticAlgorithmInterface {
    public void generatePool();
    public int[] select();
    public void mutate(int chromIndex);
    public void crossover(int chromIndex1, int chromIndex2);
    public double fitness(String chrom);
    public String run();
}

public abstract class GeneticAlgorithm implements GeneticAlgorithmInterface {

    int iterations = 200;
    private ArrayList<String> pool = new ArrayList<>();
    private int poolLength = 100;
    private int elementSize = -1;
    private double mutationRate = .002;
    private double crossoverRate = .6;

    public GeneticAlgorithm() {}

    public GeneticAlgorithm(int poolLength, int elementSize) {
        this.poolLength = poolLength;
        this.elementSize = elementSize;
    }

    public GeneticAlgorithm(int poolLength, int elementSize, double mutationRate, double crossoverRate) {
        this(poolLength, elementSize);
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
    }

    public GeneticAlgorithm(int poolLength, int elementSize, int iterations) {
        this(poolLength, elementSize);
        this.iterations = iterations;
    }

    public GeneticAlgorithm(int poolLength, int elementSize, double mutationRate, double crossoverRate, int iterations) {
        this(poolLength, elementSize, mutationRate, crossoverRate);
        this.iterations = iterations;
    }

    @Override
    public void generatePool() {
        while (pool.size() < poolLength) {
            String chrom = "";
            while (chrom.length() < elementSize) {
                chrom += (int) (Math.random() * 2);
            }
            pool.add(chrom);
        }
    }

    @Override
    public int[] select() {
        double[] fitnesses = new double[pool.size()];
        for (int i = 0; i < pool.size(); i++) {
            fitnesses[i] = fitness(pool.get(i));
        }
        double sum = 0;
        for (double fitness : fitnesses) {
            sum += fitness;
        }
        double val = Math.random() * sum;
        int chrom1Index = -1;
        int chrom2Index = -1;
        for (int i = 0; chrom1Index < 0; i++) {
            val -= fitnesses[i];
            if (val <= 0) {
                chrom1Index = i;
            }
        }
        val = Math.random() * (sum - fitnesses[chrom1Index]);
        for (int i = 0; chrom2Index < 0; i++) {
            if (i != chrom1Index) {
                val -= fitnesses[i];
                if (val <= 0) {
                    chrom2Index = i;
                }
            }
        }
        return new int[]{chrom1Index, chrom2Index};
    }

    @Override
    public void mutate(int chromIndex) {
        boolean mutated = false;
        char[] chrom = pool.get(chromIndex).toCharArray();
        for (int i = 0; i < chrom.length; i++) {
            if (Math.random() < mutationRate) {
                chrom[i] = (chrom[i] == '1') ? '0' : '1';
                mutated = true;
            }
        }
        if (mutated) {
            String res = new String(chrom);
            pool.set(chromIndex, res);
        }
    }

    @Override
    public void crossover(int chromIndex1, int chromIndex2) {
        if (Math.random() < crossoverRate) {
            String chrom1 = pool.get(chromIndex1);
            String chrom2 = pool.get(chromIndex2);
            //split before chrom[bit]
            int bit = (int) Math.floor(Math.random() * (elementSize - 1) + 1);
            String chr1 = chrom1.substring(0, bit) + chrom2.substring(bit);
            String chr2 = chrom2.substring(0, bit) + chrom1.substring(bit);
            pool.set(chromIndex1, chr1);
            pool.set(chromIndex2, chr2);
        }
    }

    @Override
    public String run() {
        if (elementSize < 0) {
            Exception e = new Exception("elementSize < 0");
            e.printStackTrace();
            System.exit(-1);
        }

        pool.clear();
        generatePool();

        String bestSolution = "";
        double bestFitness = -1;

        for (int i = 0; i < iterations; i++) {
            ArrayList<String> newPool = new ArrayList<>();
            while (pool.size() > 0) {
                int[] pair = select();
                crossover(pair[0], pair[1]);
                mutate(pair[0]);
                mutate(pair[1]);
                newPool.add(pool.get(pair[0]));
                newPool.add(pool.get(pair[1]));
                pool.remove(pair[0]);
                pool.remove(pair[1] - ((pair[0] < pair[1]) ? 1 : 0));
            }
            pool = newPool;
            for (String chrom : pool) {
                double fitness = fitness(chrom);
                if (fitness > bestFitness) {
                    bestSolution = chrom;
                    bestFitness = fitness;
                }
            }
        }

        return bestSolution;
    }
}
